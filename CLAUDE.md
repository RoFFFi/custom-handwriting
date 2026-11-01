# custom-handwriting: notes for Claude

## What this is
A tool that learns anyone's handwriting from pages they already wrote, and writes new text
onto a photo of real paper so it looks hand-written: messy, varied, realistic. Per pen.
Math later. Java 21, Gradle multi-module. Public repo: github.com/RoFFFi/custom-handwriting.

Before any work, read:
- `docs/ARCHITECTURE.md`: modules, data types, coordinate spaces, pipeline
- `docs/DECISIONS.md`: why things are the way they are (D1 to D7). Do not contradict a
  decision silently; propose a new entry instead.
- `docs/ROADMAP.md`: weekly milestones; each `- [ ]` line is a GitHub issue
- `docs/DATA_FORMATS.md`: where real pages and transcripts live and their format

## How the owner (Raphael) works
- He is building this step by step to learn. Explain the plan in plain words before
  writing code, keep each change small, and say what each new class is for.
- One issue per branch (`week2/transcript-format`), one pull request, `Closes #N` in the
  body. CI must be green before merging.
- Every new behaviour gets a test. `./gradlew build` must pass.
- When a choice shapes the project, add a DECISIONS.md entry.
- When roadmap tasks change, edit docs/ROADMAP.md and run
  `scripts/bootstrap-github.sh RoFFFi/custom-handwriting` to create the new issues.

## Code rules
- `core` has no third party dependencies. Module dependencies only point down:
  app -> page -> render -> core, app -> synth -> core, app -> capture -> core.
- All randomness goes through `core.random.Rng` (never `Math.random()` or `new Random()`).
- Units: glyph space (1.0 = x-height), page space (mm), pixels only in render and page.
  +y points down everywhere.
- Prefer records and small interfaces. Add dependency versions to `gradle/libs.versions.toml`.

## Data and privacy
- Real handwriting photos, transcripts and captured hands live in `data/` and `profiles/`,
  which are git-ignored. Never commit them, never paste their contents into issues or PRs.
- Test fixtures in `samples/` must be tiny and contain no personal writing unless the owner
  says so.

## Writing style for docs, comments and messages
- No em dashes. No contractions (write "do not", "it is", "I am"). Do not use the word "genuinely".
