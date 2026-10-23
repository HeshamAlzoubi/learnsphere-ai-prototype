import { afterEach, describe, expect, it, vi } from 'vitest';
import { api, ApiError } from './api';

afterEach(() => vi.unstubAllGlobals());
describe('API client response handling', () => {
 it('loads catalog JSON', async () => {
  const catalog = [{ id: 'lesson-001', title: 'Algorithms' }];
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: true, json: async () => catalog }));
  await expect(api.lessons()).resolves.toEqual(catalog);
  expect(fetch).toHaveBeenCalledWith('/api/v1/lessons', expect.objectContaining({ headers: expect.objectContaining({ Accept: 'application/json' }) }));
 });
 it('encodes lesson identifiers', async () => {
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: true, json: async () => ({}) }));
  await api.lesson('a/b');
  expect(fetch).toHaveBeenCalledWith('/api/v1/lessons/a%2Fb', expect.anything());
 });
 it('provides safe not-found message', async () => {
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: false, status: 404 }));
  await expect(api.lesson('missing')).rejects.toMatchObject({ status: 404, message: 'This learning resource was not found.' });
 });
 it('handles unavailable backend', async () => {
  vi.stubGlobal('fetch', vi.fn().mockRejectedValue(new Error('network unavailable')));
  await expect(api.lessons()).rejects.toBeInstanceOf(ApiError);
 });
 it('sends answers in POST body', async () => {
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: true, json: async () => ({ recommendation: 'ADVANCE' }) }));
  await api.submit('assessment-001', [{ questionId: 'q1', optionId: 'b' }]);
  expect(fetch).toHaveBeenCalledWith('/api/v1/assessments/assessment-001/submissions', expect.objectContaining({ method: 'POST', body: JSON.stringify({ answers: [{ questionId: 'q1', optionId: 'b' }] }) }));
 });
 it('requests only authenticated own-attempt history using a bearer token', async () => {
  const attempts = [{ assessmentId: 'assessment-001', correct: 2, total: 3, percentage: 66.67, recommendation: 'PRACTICE', createdAt: '2026-01-01T00:00:00Z' }];
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: true, json: async () => attempts }));
  await expect(api.ownAttempts('synthetic-access-token')).resolves.toEqual(attempts);
  expect(fetch).toHaveBeenCalledWith('/api/v1/learner/me/attempts', expect.objectContaining({
   headers: expect.objectContaining({ Authorization: 'Bearer synthetic-access-token' })
  }));
 });
 it('does not request learner data without an access token', async () => {
  const fetchMock = vi.fn();
  vi.stubGlobal('fetch', fetchMock);
  await expect(api.ownAttempts('')).rejects.toMatchObject({ status: 401 });
  expect(fetchMock).not.toHaveBeenCalled();
 });
 it('shows safe access-denied messaging for learner history', async () => {
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: false, status: 403 }));
  await expect(api.ownAttempts('synthetic-access-token')).rejects.toMatchObject({ status: 403, message: 'You do not have access to this learner history.' });
 });
});
