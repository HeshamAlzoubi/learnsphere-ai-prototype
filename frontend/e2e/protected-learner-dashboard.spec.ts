import { test, expect } from '@playwright/test';

test.describe('Milestone 14 Gate D: protected learner dashboard', () => {
  test('navigates to private history and back without fetching protected attempts', async ({ page }) => {
    let privateRequests = 0;
    await page.route('**/api/v1/learner/me/attempts', async route => {
      privateRequests += 1;
      await route.fulfill({ status: 401, contentType: 'application/json', body: '{"message":"Unauthorized"}' });
    });
    await page.goto('/');
    const historyNav = page.getByRole('button', { name: 'My history' });
    await expect(historyNav).toBeVisible();
    await historyNav.click();
    await expect(page.getByRole('heading', { name: 'My assessment history' }).first()).toBeVisible();
    await expect(historyNav).toHaveAttribute('aria-current', 'page');
    await expect(page.getByText('Sign-in integration is not yet available in this prototype.', { exact: false }).first()).toBeVisible();
    await expect(page.getByRole('button', { name: 'Load my history' })).toHaveCount(0);
    await expect(page.getByRole('region', { name: 'Your assessment attempts' })).toHaveCount(0);
    expect(privateRequests).toBe(0);
    await page.getByRole('button', { name: /All lessons/ }).click();
    await expect(page.getByRole('heading', { name: 'Available lessons' })).toBeVisible();
    await expect(historyNav).not.toHaveAttribute('aria-current', 'page');
  });

  test('private history navigation works by keyboard', async ({ page }) => {
    await page.goto('/');
    const historyNav = page.getByRole('button', { name: 'My history' });
    await historyNav.focus();
    await expect(historyNav).toBeFocused();
    await page.keyboard.press('Enter');
    await expect(page.getByRole('heading', { name: 'My assessment history' }).first()).toBeVisible();
    await expect(historyNav).toHaveAttribute('aria-current', 'page');
  });

  test('shared demo progress stays separate from private history', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('button', { name: 'View shared demo progress' }).click();
    await expect(page.getByRole('heading', { name: 'Shared demo progress' })).toBeVisible();
    await expect(page.getByText('These are not linked to any learner.', { exact: false })).toBeVisible();
    await page.getByRole('button', { name: 'My history' }).click();
    await expect(page.getByText('Private assessment history is available only after authentication.', { exact: false })).toBeVisible();
    await expect(page.getByText('Shared synthetic assessment history')).toHaveCount(0);
  });

  test('unauthenticated HTTP request to learner attempts is rejected', async ({ request }) => {
    const response = await request.get('/api/v1/learner/me/attempts');
    expect(response.status()).toBe(401);
  });

  test('signed-out dashboard remains usable at narrow viewport', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 812 });
    await page.goto('/');
    await page.getByRole('button', { name: 'My history' }).click();
    await expect(page.getByRole('heading', { name: 'My assessment history' }).first()).toBeVisible();
    await expect(page.getByRole('button', { name: /All lessons/ })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Load my history' })).toHaveCount(0);
  });
});
