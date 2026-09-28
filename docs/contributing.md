# Contributing guide

## Required PR checks

River Watch validates pull requests with automated checks for:

- branch naming
- PR title format
- linked story reference for `feat/` and `fix/` branches
- required PR template sections
- commit messages following Conventional Commits
- committed secrets with Gitleaks

## Make the checks required on `main`

GitHub Actions can run the checks automatically, but a repository maintainer still needs to make them required in GitHub:

1. Open **Settings** → **Rules** → **Rulesets**.
2. Select **New branch ruleset**.
3. Target the `main` branch.
4. Enable **Require a pull request before merging**.
5. Enable **Require status checks to pass**.
6. Add these required checks:
   - `Conventions`
   - `Secret scan`
   - the US-006 build/test job, when that workflow exists

## Notes

- Rulesets do not block merges on private repositories that use the GitHub Free plan. In that case the checks still run, but they are informational only.
- If the repository is public, enable **Secret scanning** and **Push protection** under **Settings** → **Code security** for stronger secret protection.
