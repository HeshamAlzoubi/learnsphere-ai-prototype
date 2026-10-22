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
});
