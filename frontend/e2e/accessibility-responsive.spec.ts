import { test, expect } from '@playwright/test';
import AxeBuilder from '@axe-core/playwright';

test('catalog and assessment have no critical or serious automated accessibility violations', async ({ page }) => {
  await page.goto('/');
  await expect(page.getByRole('heading', { name: 'Available lessons' })).toBeVisible();
  let results = await new AxeBuilder({ page }).withTags(['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa']).analyze();
  expect(results.violations.filter(v => v.impact === 'critical' || v.impact === 'serious')).toEqual([]);

  await page.getByRole('button', { name: 'Explore lesson' }).first().click();
  await page.getByRole('button', { name: 'Start knowledge check' }).click();
  await expect(page.locator('fieldset.question')).toHaveCount(3);
  results = await new AxeBuilder({ page }).withTags(['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa']).analyze();
  expect(results.violations.filter(v => v.impact === 'critical' || v.impact === 'serious')).toEqual([]);
});

test('keyboard users can reach main content and choose an answer', async ({ page }) => {
  await page.goto('/');
  await expect(page.getByRole('heading', { name: 'Available lessons' })).toBeVisible();
  await page.keyboard.press('Tab');
  await expect(page.getByRole('link', { name: 'Skip to main content' })).toBeFocused();
  await page.keyboard.press('Enter');
  await expect(page.locator('#main-content')).toBeFocused();

  await page.getByRole('button', { name: 'Explore lesson' }).first().focus();
  await page.keyboard.press('Enter');
  await expect(page.getByRole('button', { name: 'Start knowledge check' })).toBeVisible();
  await page.getByRole('button', { name: 'Start knowledge check' }).focus();
  await page.keyboard.press('Enter');
  const first = page.locator('fieldset.question').first().locator('input[type="radio"]').first();
  await first.focus();
  await page.keyboard.press('Space');
  await expect(first).toBeChecked();
});

for (const viewport of [{ width: 375, height: 812 }, { width: 768, height: 1024 }]) {
  test(`catalog and assessment fit ${viewport.width}px viewport without horizontal overflow`, async ({ page }) => {
    await page.setViewportSize(viewport);
    await page.goto('/');
    await expect(page.getByRole('heading', { name: 'Available lessons' })).toBeVisible();
    const noOverflow = () => document.documentElement.scrollWidth <= window.innerWidth;
    expect(await page.evaluate(noOverflow)).toBe(true);
    await page.getByRole('button', { name: 'Explore lesson' }).first().click();
    await page.getByRole('button', { name: 'Start knowledge check' }).click();
    await expect(page.locator('fieldset.question')).toHaveCount(3);
    expect(await page.evaluate(noOverflow)).toBe(true);
  });
}
