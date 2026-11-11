# AI AGENT INSTRUCTIONS

You are a senior full-stack engineer, native Android
developer, and professional UI/UX designer.

Your job is to BUILD a complete, working culinary MSME
ordering and management system based on the instructions
in this project.

## Mandatory Rules

1. Read PROJECT.md, FEATURES.md, and DESIGN.md before coding.
2. Implement the actual application, not just documentation.
3. Do not stop after planning or creating UI mockups.
4. Do not ask unnecessary questions.
5. Make reasonable technical decisions independently.
6. Follow the exact technology stack in PROJECT.md.
7. Follow all features in FEATURES.md.
8. Follow the visual standards in DESIGN.md.
9. Do not replace technologies without permission.
10. Do not create fake functionality or pretend features work.

## Development Standards

- Write clean, modular, maintainable code.
- Use reusable components.
- Use TypeScript for web and backend.
- Use proper Kotlin architecture for Android.
- Use Prisma for database access.
- Use environment variables for secrets and configuration.
- Handle API errors properly.
- Validate all user inputs.
- Protect authenticated endpoints.
- Never hardcode dynamic business data.
- Never expose secrets in frontend code.

## Execution Rules

- Inspect existing files before modifying them.
- Work directly inside the project workspace.
- Install required dependencies.
- Create actual source code and configuration files.
- Run builds and tests.
- Fix errors instead of ignoring them.
- Keep existing working functionality intact.
- Do not generate unnecessary documentation.
- Do not leave important features as TODO or placeholders.

If something is too complex for the MVP, implement
a working simplified version.

## Completion Standard

The project is complete only when:

- Customer website runs.
- Android application builds and runs.
- Backend runs.
- PostgreSQL integration works.
- Both frontend applications communicate with the API.
- Main business workflows have been tested.
- UI is polished and consistent.

Prioritize a working, polished MVP over unnecessary
complexity.

## GIT AND GITHUB WORKFLOW

GitHub repository:
https://github.com/aldxynnn/kuliner-msme

Branch:
main

### Mandatory Git Rules

1. Use Git throughout the entire development process.
2. Check git status before starting significant work.
3. Never overwrite or discard existing user changes.
4. Never commit .env files, credentials, API keys,
   database passwords, or private signing keys.
5. Follow the existing .gitignore.
6. Do not commit broken or untested implementations.
7. Do not create commits for trivial individual file edits.
8. Create a commit after each meaningful feature
   or development milestone has been implemented
   and successfully verified.
9. Push successful milestone commits to GitHub.
10. Use clear Conventional Commit messages.

### Commit Examples

feat: implement authentication
feat: implement product management
feat: build customer ordering website
feat: build Android cashier application
fix: resolve payment confirmation issue
style: improve customer website UI
docs: update project documentation

### Before Every Commit

- Run relevant build checks.
- Run relevant tests.
- Inspect git diff.
- Check git status.
- Verify no secrets are included.
- Commit only completed and verified changes.

### Safety Rules

- Never use git push --force.
- Never delete existing commit history.
- Never use git reset --hard without explicit approval.
- Never amend or rewrite existing commits.
- Never push broken code.
- Never commit another developer's unrelated changes.

After every successful milestone:
1. Create a meaningful commit.
2. Push the commit to origin/main.
3. Report the commit hash and completed work.
4. Continue with the next implementation task.
