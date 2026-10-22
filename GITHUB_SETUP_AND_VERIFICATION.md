# LearnSphere AI — GitHub Actions verification guide (Increment 12)

## Purpose
Run the existing backend and frontend verification workflows on GitHub-hosted runners, without administrator privileges on a college laptop. **No workflows have been run as part of preparing this package.**

## 1. Create a private GitHub repository
1. Sign in to https://github.com/ using your own account.
2. Click **New repository** and name it `learnsphere-ai-prototype` (or a name of your choice).
3. Select **Private**. Do not initialize with README, .gitignore, or license if you will upload this project's existing files.
4. Never share your password, access tokens, recovery codes, or two-factor codes.

## 2. Upload the project
1. Extract this ZIP on your computer.
2. Open the new GitHub repository and select **Add file → Upload files**.
3. Upload the **contents** of `LearnSphere_AI_Prototype_Increment_12` to the repository root (so `.github/workflows`, `backend`, `frontend`, and `tools` are at root). GitHub's browser upload interface may not preserve hidden folders or directory structures reliably. If the `.github` folder is absent, use GitHub Desktop or Git CLI to commit the complete folder instead.
4. Check that `.github/workflows/backend-tests.yml` and `.github/workflows/frontend-tests.yml` exist in the repository.
5. Do not upload `.env` files, passwords, tokens, production data, or student records. The supplied `.env.example` is a placeholder template only.

## 3. Run workflows
1. Open the repository **Actions** tab.
2. If prompted, enable Actions for this repository.
3. Select **Backend Maven Tests**, click **Run workflow**, and run on your default branch.
4. Select **Frontend Typecheck Build and Tests**, click **Run workflow**.
5. Wait for both to finish; inspect each job's detailed logs.
6. Share only the workflow status and relevant error messages with the project assistant. Redact secrets if present.

## 4. Interpret results
- Green means only that the steps included in that specific workflow passed on GitHub's runner.
- Red means a build/test step failed; inspect the first substantive error and correct the source before rerunning.
- These workflows do not establish production security, real-user readiness, or complete MySQL behavior. Backend tests use H2 in MySQL compatibility mode, not a real MySQL service.

## 5. Before using real learners
Implement and independently verify authentication, authorization, learner isolation, privacy/retention, consent requirements, and deployment controls. Use synthetic data until then.
