# Architecture

## The goal in one picture

```
 YOUR HANDWRITING                         THE PAGE                 THE TEXT
 (photos of filled                        (photo of the paper      "Dear Sam, ..."
  capture sheets, per pen)                 you want written on)        |
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
| `capture` | Learn a hand: template sheets, photo to glyph samples. | core |
| `synth`   | Decide what to write and where: tokenize into letters and clusters, lay out with a box tree, vary every instance. | core |
| `render`  | Decide what the ink looks like: pen physics, line width, texture. Output is a flat transparent layer. | core |
| `page`    | Understand the paper photo (outline, angle, grid or lines, lighting) and blend the ink into it. | core, render |
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
- **`InkDocument`**: every stroke placed on a page in millimetres. The hand-off from `synth` to `render`.
- **`InkLayer`**: rendered ink pixels for a flat page. The hand-off from `render` to `page`.

## Coordinate spaces

| Space  | Units | Origin | Used by |
|--------|-------|--------|---------|
| Glyph  | 1.0 = x-height | left end of baseline | captured samples |
| Page   | millimetres | top-left of the flat sheet | `InkDocument`, layout, template cells |
| Pixel  | pixels | top-left of an image | `render`, `page` only |

+y points down everywhere (Java2D convention). Ascenders therefore have negative y in glyph space.

## How a page gets written (target behaviour)

1. **Analyze the page photo**: find the sheet, compute the mapping from flat mm to photo
   pixels, detect grid or lines and their pitch, estimate lighting.
2. **Build a writing area** from the ruling (baselines on lines or grid rows, margins).
3. **Tokenize** each word: randomly choose between clusters and single letters
   (`thing` → `[th, ing]` or `[t, h, i, ng]`).
4. **Lay out** a box tree: rows of glyphs and gaps, broken into lines to fit the area.
5. **Pick and vary** a sample for every glyph: random choice among real samples, then
   jitter in size, slant, rotation and shape, all within the writer's measured range.
6. **Drift**: baseline wander along each line, slight size and slant changes over the page.
7. **Render** strokes with the pen model at high resolution.
8. **Composite**: warp the ink into the photo, darken by the local lighting, match blur and noise.

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
