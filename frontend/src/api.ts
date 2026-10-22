import type { Answer, Assessment, Lesson, SubmissionResponse, DemoProgress } from './types';
export class ApiError extends Error { constructor(message: string, readonly status?: number) { super(message); this.name='ApiError'; } }
async function request<T>(path: string, init?: RequestInit): Promise<T> {
  let response: Response;
  try { response=await fetch(`/api/v1${path}`, { ...init, headers: { 'Accept':'application/json', ...(init?.body ? {'Content-Type':'application/json'} : {}), ...init?.headers } }); }
  catch { throw new ApiError('Cannot reach the learning service. Make sure the backend is running.'); }
  if (!response.ok) throw new ApiError(response.status===404 ? 'This learning resource was not found.' : response.status===400 ? 'Your submission was not accepted. Please review your answers.' : 'The learning service returned an error.', response.status);
  try { return await response.json() as T; }
  catch { throw new ApiError('The learning service returned an unexpected response.'); }
}
export const api = {
  demoProgress: () => request<DemoProgress>('/demo-progress'),
  lessons: () => request<Lesson[]>('/lessons'),
  lesson: (id: string) => request<Lesson>(`/lessons/${encodeURIComponent(id)}`),
  assessment: (id: string) => request<Assessment>(`/assessments/${encodeURIComponent(id)}`),
  lessonAssessment: (id: string) => request<Assessment>(`/lessons/${encodeURIComponent(id)}/assessment`),
  submit: (id: string, answers: Answer[]) => request<SubmissionResponse>(`/assessments/${encodeURIComponent(id)}/submissions`, {method:'POST',body:JSON.stringify({answers})})
};
