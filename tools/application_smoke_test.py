#!/usr/bin/env python3
"""HTTP integration smoke test; disposable CI data only. No browser automation."""
import json
import sys
import time
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen

BACKEND = 'http://127.0.0.1:8080'
FRONTEND = 'http://127.0.0.1:5173'

def fetch(base, path, payload=None):
    body = json.dumps(payload).encode() if payload is not None else None
    headers = {'Accept': 'application/json'}
    if body is not None:
        headers['Content-Type'] = 'application/json'
    req = Request(base + path, data=body, headers=headers, method='POST' if body else 'GET')
    with urlopen(req, timeout=10) as resp:
        data = resp.read()
        return resp.status, resp.headers.get('Content-Type', ''), data

def wait_for(base, path, label):
    last_error = 'No response received'

    for attempt in range(90):
        try:
            status, _, _ = fetch(base, path)

            if status == 200:
                print('READY:', label, flush=True)
                return

            last_error = f'HTTP {status}'

        except HTTPError as exc:
            last_error = f'HTTP {exc.code}: {exc.reason}'

        except (URLError, TimeoutError, ConnectionError) as exc:
            last_error = str(exc)

        if attempt % 10 == 0:
            print(
                f'WAITING: {label} at {base + path}; '
                f'last error: {last_error}',
                flush=True
            )

        time.sleep(2)

    raise AssertionError(
        f'{label} did not become ready within 180 seconds. '
        f'Last error: {last_error}'
    )
def api(path, payload=None):
    status, content_type, body = fetch(FRONTEND, '/api/v1' + path, payload)
    assert status == 200, f'{path}: HTTP {status}'
    assert 'application/json' in content_type, f'{path}: wrong content type {content_type}'
    return json.loads(body)

def main():
    wait_for(BACKEND, '/api/v1/lessons', 'Spring Boot backend')
    wait_for(FRONTEND, '/', 'Vite frontend')
    status, content_type, html = fetch(FRONTEND, '/')
    assert status == 200 and b'<div id="root"></div>' in html, 'Frontend HTML entrypoint missing'
    print('PASS: frontend HTML served')
    lessons = api('/lessons')
    assert len(lessons) >= 3 and any(x['id'] == 'lesson-001' for x in lessons)
    print('PASS: lesson catalog through Vite API proxy')
    lesson = api('/lessons/lesson-001')
    assert lesson['id'] == 'lesson-001'
    assessment = api('/lessons/lesson-001/assessment')
    assert assessment['id'] == 'assessment-001' and len(assessment['questions']) == 3
    assert all('correctAnswer' not in q for q in assessment['questions'])
    print('PASS: lesson and assessment fetched through proxy')
    before = api('/demo-progress')
    answers = [{'questionId': 'q1', 'optionId': 'b'},
               {'questionId': 'q2', 'optionId': 'c'},
               {'questionId': 'q3', 'optionId': 'a'}]
    result = api('/assessments/assessment-001/submissions', {'answers': answers})
    assert result['correct'] == 3 and result['total'] == 3
    assert result['percentage'] == 100 and result['recommendation'] == 'ADVANCE'
    print('PASS: assessment submission and rule-based recommendation')
    after = api('/demo-progress')
    assert after['totalAttempts'] == before['totalAttempts'] + 1
    entry = next(x for x in after['lessons'] if x['lessonId'] == 'lesson-001')
    assert entry['latestPercentage'] == 100 and entry['latestRecommendation'] == 'ADVANCE'
    assert entry['suggestedAction'] == 'EXPLORE_NEXT_LESSON'
    print('PASS: persisted submission visible through fresh progress request')
    # Invalid submission must not create another persisted attempt.
    try:
        api('/assessments/assessment-001/submissions', {'answers': []})
    except HTTPError as exc:
        assert exc.code == 400, f'Expected 400, got {exc.code}'
    else:
        raise AssertionError('Invalid submission unexpectedly succeeded')
    final = api('/demo-progress')
    assert final['totalAttempts'] == after['totalAttempts']
    print('PASS: invalid submission rejected without persistence')
    print('APPLICATION SMOKE TEST PASSED (HTTP via Vite proxy, Spring Boot, MySQL)')

if __name__ == '__main__':
    try:
        main()
    except Exception as exc:
        print(f'APPLICATION SMOKE TEST FAILED: {exc}', file=sys.stderr)
        raise
