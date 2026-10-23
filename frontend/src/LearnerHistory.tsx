import { useEffect, useRef, useState } from 'react';
import { api, ApiError } from './api';
import type { LearnerAttempt } from './types';

/**
 * Access token comes from a trusted session provider, never localStorage,
 * URL parameters, or an editable token field. No identity provider is wired yet.
 */
export function LearnerHistory({ accessToken }: { accessToken: string | null }) {
  const [history, setHistory] = useState<{ token: string; attempts: LearnerAttempt[] } | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const generation = useRef(0);
  const activeRequest = useRef<AbortController | null>(null);
  const token = accessToken?.trim() || null;

  useEffect(() => {
    // Discard any in-flight response when identity changes or component unmounts.
    generation.current += 1;
    activeRequest.current?.abort();
    setHistory(null);
    setError('');
    setLoading(false);
    return () => { generation.current += 1; activeRequest.current?.abort(); };
  }, [token]);

  async function loadHistory() {
    const currentToken = token;
    if (!currentToken) {
      setHistory(null);
      setError('Sign in is required to view your assessment history.');
      return;
    }
    activeRequest.current?.abort();
    const controller = new AbortController();
    activeRequest.current = controller;
    const requestGeneration = ++generation.current;
    setLoading(true);
    setError('');
    setHistory(null);
    try {
      const attempts = await api.ownAttempts(currentToken, controller.signal);
      if (generation.current === requestGeneration) {
        setHistory({ token: currentToken, attempts });
      }
    } catch (cause) {
      if (generation.current === requestGeneration) {
        setError(cause instanceof ApiError ? cause.message : 'Unable to load your assessment history.');
      }
    } finally {
      if (generation.current === requestGeneration) setLoading(false);
    }
  }

  // Never display results from a previous identity, even for one render.
  const attempts = history?.token === token ? history.attempts : null;
  return <section aria-labelledby="learner-history-heading" className="learner-history">
    <h2 id="learner-history-heading">My assessment history</h2>
    <p>Private assessment history is available only after authentication. Shared demo progress is separate.</p>
    {!token ? <p role="status">Sign in through a trusted identity provider to access your own results. Sign-in integration is not yet available in this prototype.</p> :
      <button type="button" className="secondary learner-history-load" disabled={loading} onClick={loadHistory}>
        {loading ? 'Loading…' : attempts === null ? 'Load my history' : 'Refresh my history'}
      </button>}
    {loading && token && <p role="status">Loading your assessment history…</p>}
    {error && token && <p role="alert" className="error">{error}</p>}
    {attempts?.length === 0 && <p role="status">No assessment attempts are available for this learner.</p>}
    {attempts && attempts.length > 0 && <div className="cards" aria-label="Your assessment attempts">
      {attempts.map((attempt, index) => <article className="lessoncard" key={attempt.assessmentId + '-' + attempt.createdAt + '-' + index}>
        <h3>Assessment {attempt.assessmentId}</h3>
        <p>Score: {attempt.correct} of {attempt.total} ({Math.round(attempt.percentage)}%)</p>
        <p>Recommendation: {attempt.recommendation.replaceAll('_', ' ')}</p>
        <p>Completed: <time dateTime={attempt.createdAt}>{new Date(attempt.createdAt).toLocaleString()}</time></p>
      </article>)}
    </div>}
  </section>;
}
