# custom-handwriting

Learn a person's handwriting, per pen, and write any text onto a photo of real paper so the
result looks written by hand: messy, varied, on a realistic page. Math comes later.

> Status: **Week 0, foundations.** The structure, model and plan are in place; the features
> are built step by step following [docs/ROADMAP.md](docs/ROADMAP.md).

## How it will work

1. **Learn.** Upload photos of pages you already wrote, with the text they say. The tool
   learns your letters, common clusters (`th`, `ing`, ...) and whole words, shows how confident
   it is about each, and asks you to hand-write a short passage covering only what is missing.
   Each writer and pen pair is a separate *hand*.
2. **Choose a page.** Photograph the paper you want written on (lined, grid, blank).
3. **Write.** Type the text. The tool picks your real letters and clusters at random,
   varies each one within your natural range, lays them out on the page's lines, renders
   them with the right pen, and blends the ink into the photo.

Read [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for the design and
[docs/DECISIONS.md](docs/DECISIONS.md) for why it is built this way.

## Requirements

- JDK 21 (for example `brew install openjdk@21` on macOS)
- Nothing else: the Gradle wrapper downloads Gradle and the libraries.

## Commands

```bash
./gradlew build                 # compile everything and run all tests
./gradlew test                  # tests only
./gradlew :core:test            # tests for one module
./gradlew :app:run --args="help"
```

## Project layout

```
core/       shared model: strokes, glyphs, hands, pens, seeded randomness
capture/    learning a hand from photos of template sheets
synth/      text -> letters and clusters -> layout -> varied strokes
render/     strokes -> ink pixels (pen physics and texture)
page/       understanding the paper photo and blending ink into it
app/        command line entry point
docs/       architecture, decisions, roadmap
samples/    small test photos and hands (keep personal handwriting out of public repos)
research/   optional Python experiments, never required to build
scripts/    helper scripts (GitHub setup)
```

## Working rhythm

- One GitHub issue per task, one branch per issue (`week3/skeleton-tracing`), one pull request.
- CI must be green before merging to `main`.
- When a choice shapes the project, add an entry to `docs/DECISIONS.md`.
- Never use `Math.random()`: take an `Rng` parameter (see D4).

## Privacy and consent

Handwriting is personal data, and a generator of someone's handwriting can be misused.
This repository holds code only: real pages, transcripts and captured hands live in the
git-ignored `data/` and `profiles/` folders and are never committed. Only
generate handwriting for people who have agreed to it. The tool is meant for writers
learning their own handwriting, not for imitating someone else's.
