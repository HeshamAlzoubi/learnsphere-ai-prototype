import type { Answer, Assessment, Lesson, SubmissionResponse, DemoProgress, LearnerAttempt } from './types';
export class ApiError extends Error { constructor(message: string, readonly status?: number) { super(message); this.name='ApiError'; } }
async function request<T>(path: string, init?: RequestInit): Promise<T> {
  let response: Response;
  try { response=await fetch(`/api/v1${path}`, { ...init, headers: { 'Accept':'application/json', ...(init?.body ? {'Content-Type':'application/json'} : {}), ...init?.headers } }); }
  catch (cause) {
    if (init?.signal?.aborted) throw cause;
    throw new ApiError('Cannot reach the learning service. Make sure the backend is running.');
  }
  if (!response.ok) throw new ApiError(response.status===404 ? 'This learning resource was not found.' : response.status===400 ? 'Your submission was not accepted. Please review your answers.' : response.status===401 ? 'Your session is not authenticated. Please sign in again.' : response.status===403 ? 'You do not have access to this learner history.' : 'The learning service returned an error.', response.status);
  try { return await response.json() as T; }
  catch { throw new ApiError('The learning service returned an unexpected response.'); }
}
export const api = {
  demoProgress: () => request<DemoProgress>('/demo-progress'),
  /** Caller supplies a trusted identity-provider access token; never store it in browser storage. */
  ownAttempts: (accessToken: string, signal?: AbortSignal) => {
    if (!accessToken || !accessToken.trim()) throw new ApiError('Sign in is required to view learner history.', 401);
    return request<LearnerAttempt[]>('/learner/me/attempts', { signal, headers: { Authorization: `Bearer ${accessToken}` } });
  },
  lessons: () => request<Lesson[]>('/lessons'),
  lesson: (id: string) => request<Lesson>(`/lessons/${encodeURIComponent(id)}`),
  assessment: (id: string) => request<Assessment>(`/assessments/${encodeURIComponent(id)}`),
  lessonAssessment: (id: string) => request<Assessment>(`/lessons/${encodeURIComponent(id)}/assessment`),
  submit: (id: string, answers: Answer[]) => request<SubmissionResponse>(`/assessments/${encodeURIComponent(id)}/submissions`, {method:'POST',body:JSON.stringify({answers})})
};
