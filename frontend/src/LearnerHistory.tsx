import { useState } from 'react';
import { api, ApiError } from './api';
import type { LearnerAttempt } from './types';

/**
 * Prototype integration boundary: token supplied for the current session by a trusted
 * identity provider. No sign-in form or token persistence is implemented here.
 */
export function LearnerHistory({ accessToken }: { accessToken: string | null }) {
  const [attempts, setAttempts] = useState<LearnerAttempt[] | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  async function loadHistory() {
    if (!accessToken?.trim()) {
      setAttempts(null);
      setError('Sign in is required to view your assessment history.');
      return;
    }
    setLoading(true);
    setError('');
    setAttempts(null);
    try {
      setAttempts(await api.ownAttempts(accessToken));
    } catch (cause) {
      setError(cause instanceof ApiError ? cause.message : 'Unable to load your assessment history.');
    } finally {
      setLoading(false);
    }
  }
  return <section aria-labelledby="learner-history-heading" className="learner-history">
    <h2 id="learner-history-heading">My assessment history</h2>
    <p>Private assessment history is available only after authentication. Shared demo progress is separate.</p>
    {!accessToken?.trim() ? <p role="status">Sign in through a trusted identity provider to access your own results. Sign-in integration is not yet available in this prototype.</p> :
      <button type="button" className="secondary learner-history-load" disabled={loading} onClick={loadHistory}>
        {loading ? 'Loading…' : attempts === null ? 'Load my history' : 'Refresh my history'}
      </button>}
    {loading && <p role="status">Loading your assessment history…</p>}
    {error && <p role="alert" className="error">{error}</p>}
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
