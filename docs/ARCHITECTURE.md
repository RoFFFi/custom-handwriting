# Architecture

## The goal in one picture

```
 YOUR HANDWRITING                         THE PAGE                 THE TEXT
 (photos of pages you                     (photo of the paper      "Dear Sam, ..."
  already wrote + transcripts)             you want written on)        |
        |                                        |                     |
     capture                                   page                  synth
        |                                   (analyze)                  |
        v                                        |                     |
   Hand = writer + pen  ------------------------------------------>  layout + variation
   (glyph library + style)                       |                     |
                                                 |                     v
                                                 |               InkDocument (strokes in mm)
                                                 |                     |
                                                 |                  render
                                                 |                     v
                                                 |               InkLayer (ink pixels, flat)
                                                 v                     |
                                              page (composite) <-------+
                                                 |
                                                 v
                                     photo of the page, now handwritten
```

## Modules

| Module    | Job | Depends on |
|-----------|-----|------------|
| `core`    | Shared vocabulary: strokes, glyphs, hands, pens, `Rng`, `InkDocument`. No third party libraries. | nothing |
| `capture` | Learn a hand from uploaded pages: transcripts, coverage and confidence, photo to words to strokes. | core |
| `synth`   | The writer: puts text down word by word, reacting to what is already on the page (drift, margins, fatigue). | core |
| `render`  | Decide what the ink looks like: pen physics, line width, texture. Output is a flat transparent layer. | core |
| `page`    | Two jobs. Before writing: read the paper photo and describe the sheet (size, lines, margins, existing ink). After rendering: blend the ink into the photo. | core, render |
| `app`     | Command line that wires the modules together. | all |

Rule: a module never reaches "up" this table. If `synth` needs something from `page`, the
shared piece belongs in `core`.

## Key data types

- **`Stroke`**: one pen-down movement as a list of `StrokePoint(x, y, pressure, width)`.
- **`GlyphKey`**: what a sample represents. `"a"` is a letter, `"th"` is a cluster the writer merges.
- **`GlyphSample`**: one real captured instance of a key (strokes + metrics for joining).
- **`GlyphLibrary`**: every sample for one hand, grouped by key.
- **`PenProfile`**: the physical pen (type, colour, width, opacity).
- **`StyleParams`**: the writer's habits with that pen (slant, size, spacing, wobble).
- **`Hand`**: writer + pen + style + library. The unit you capture, store and write with.
- **`Sheet`**: the flat writing surface in millimetres (lines, margins, areas already inked). The hand-off from `page` to `synth`.
- **`WriterState`**: what the writer knows mid-page: pen position, drift, slope, fatigue.
- **`InkDocument`**: every stroke placed on a page in millimetres, in writing order. The hand-off from `synth` to `render`.
- **`InkLayer`**: rendered ink pixels for a flat page. The hand-off from `render` to `page`.

## Coordinate spaces

| Space  | Units | Origin | Used by |
|--------|-------|--------|---------|
| Glyph  | 1.0 = x-height | left end of baseline | captured samples |
| Page   | millimetres | top-left of the flat sheet | `InkDocument`, layout, template cells |
| Pixel  | pixels | top-left of an image | `render`, `page` only |

+y points down everywhere (Java2D convention). Ascenders therefore have negative y in glyph space.

## How a page gets written (target behaviour)

```
page photo --page.analyze--> Sheet --+
                                     v
text --> for each word:  plan -> look -> adjust -> commit   (synth, WriterState updated each time)
                                     |
                                     v
                       InkDocument (strokes in writing order)
                                     |
                                  render
                                     v
                         InkLayer --page.composite--> finished photo
```

1. **Read the page photo**: find the sheet, the mapping from flat mm to photo pixels, the
   ruled lines or grid, the margins, any existing writing, and the lighting. Produce a `Sheet`.
2. **Write word by word.** For each word:
   - *Plan*: choose clusters or single letters (`thing` → `[th, ing]` or `[t, h, i, ng]`),
     pick one of the real samples for each, vary it, and measure the word.
   - *Look*: check the `WriterState`. Does it fit before the margin? How far has the line drifted?
   - *Adjust*: squeeze it in, move to the next line, pull the baseline back toward the rule,
     or let it trail off, as a person would.
   - *Commit*: append its strokes to the ink record and update the state (pen position,
     drift, size creep, fatigue).
3. **Render** the strokes with the pen model at high resolution, in writing order.
4. **Composite**: warp the ink into the photo, darken by the local lighting, match blur and noise.

All randomness flows from one seeded `Rng`, so a seed reproduces a page exactly.

## Where machine learning fits

Version 1 is a sampling system: it reuses your real letters and varies them. That already
gives convincing results and is fully explainable. The interfaces leave room to swap in
learned components later without touching the rest:

- `VariationModel` → learned from the spread of your real samples.
- `Tokenizer` → learned cluster preferences from real text you wrote.
- A full stroke-generation model (for example a recurrent or diffusion model trained on your
  strokes) can sit behind `Synthesizer`. Training would happen in Python under `research/`,
  exported to ONNX, and run from Java with ONNX Runtime. See DECISIONS.md, D1.
