import { test, expect } from '@playwright/test';

test('learner can complete a lesson and see persisted demo progress', async ({ page }) => {
  await page.goto('/');
  await expect(page.getByRole('heading', { name: 'Available lessons' })).toBeVisible();

  const lessonCards = page.locator('.lessoncard');
  await expect(lessonCards).toHaveCount(3);
  const firstTitle = await lessonCards.first().getByRole('heading').innerText();
  await lessonCards.first().getByRole('button', { name: 'Explore lesson' }).click();
  await expect(page.getByRole('heading', { name: firstTitle })).toBeVisible();

  await page.getByRole('button', { name: 'Start knowledge check' }).click();
  await expect(page.getByRole('heading', { name: 'Check your understanding' })).toBeVisible();
  const questions = page.locator('fieldset.question');
  await expect(questions).toHaveCount(3);

  const submit = page.getByRole('button', { name: 'Submit answers' });
  await expect(submit).toBeDisabled();
  for (let i = 0; i < 3; i++) {
    await questions.nth(i).locator('input[type="radio"]').first().check();
  }
  await expect(submit).toBeEnabled();
  await submit.click();

  await expect(page.getByText('ASSESSMENT COMPLETE')).toBeVisible();
  await expect(page.getByRole('heading', { name: 'Your learning snapshot' })).toBeVisible();
  await expect(page.getByText(/3 of 3 correct|2 of 3 correct|1 of 3 correct|0 of 3 correct/)).toBeVisible();

  await page.getByRole('button', { name: 'Back to catalog' }).click();
  await page.getByRole('button', { name: 'View shared demo progress' }).click();
  await expect(page.getByRole('heading', { name: 'Shared synthetic assessment history' })).toBeVisible();
  const progressCard = page.locator('.lessoncard').filter({ has: page.getByRole('heading', { name: firstTitle }) });
  await expect(progressCard).toBeVisible();
  await expect(progressCard.getByText(/Attempts: [1-9][0-9]*/)).toBeVisible();
  await expect(progressCard.getByText(/Latest score: [0-9]+%/)).toBeVisible();
  await expect(progressCard.getByText(/Recent score trend: (Not enough attempts|Improving|Declining|Unchanged)/)).toBeVisible();
});

test('learner can return from a lesson without submitting', async ({ page }) => {
  await page.goto('/');
  await expect(page.getByRole('heading', { name: 'Available lessons' })).toBeVisible();
  await page.getByRole('button', { name: 'Explore lesson' }).first().click();
  await page.getByRole('button', { name: '← All lessons' }).click();
  await expect(page.getByRole('heading', { name: 'Available lessons' })).toBeVisible();
});
