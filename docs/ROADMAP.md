# Roadmap

Each week is one GitHub milestone and each checkbox is one issue
(`scripts/bootstrap-github.sh` creates them from this file, so keep the format:
`## Week N: Title`, then `- [ ] task`). A week is done when its **Done when** line is true.
Weeks are a guide, not a deadline: move on when the milestone works, not when the calendar says.

---

## Week 0: Foundations

- [x] Gradle multi-module project, Java 21, CI on GitHub
- [x] Core domain model: strokes, glyphs, hands, pens, seeded randomness
- [x] Architecture, decisions and roadmap docs

**Done when:** `./gradlew build` is green locally and on GitHub.

## Week 1: See strokes on screen

- [ ] Implement `DebugRenderer` in render: draw an InkDocument as plain polylines to a PNG
- [ ] Implement a JSON `HandRepository` (add Jackson to core or a new `store` module)
- [ ] Hand-write a tiny test hand in JSON (5 letters typed as coordinates) under `samples/hands/`
- [ ] Add an `app` command `debug-render` that loads the test hand and writes a word to PNG

**Done when:** you can run one command and open a PNG showing a word built from hand-made strokes.

## Week 2: Capture sheets

- [ ] Decide the character set: a-z, A-Z, 0-9, punctuation, plus a first list of clusters (th, he, in, er, an, ing, ll, ss, tt, oo)
- [ ] Generate printable template sheets (PDF or 300 dpi PNG) with corner markers, cells, baseline and x-height guides
- [ ] Each key appears 3 to 5 times across sheets; record the layout as `TemplateSheet` JSON next to the image
- [ ] Fill in the sheets by hand with your first pen and photograph them

**Done when:** you have printed, filled and photographed a full set for one pen.

## Week 3: Photo to strokes

- [ ] Add OpenCV; detect the corner markers and flatten the photo to the sheet's mm space
- [ ] Cut each cell, separate ink from paper (adaptive threshold), remove guide lines
- [ ] Thin the ink to a skeleton and trace it into ordered strokes; measure ink width along each
- [ ] Normalize to glyph space using the printed baseline and x-height; compute metrics (advance, entry, exit)
- [ ] Build a review sheet: all samples of each key side by side, so bad traces are easy to spot and drop

**Done when:** the debug renderer draws your own letters from your own photos.

## Week 4: First real writing (on white)

- [ ] Implement `ClusterTokenizer` (random choice between clusters and single letters, weighted toward clusters)
- [ ] Implement `VariationModel` v1: per-glyph scale, slant and rotation jitter from `StyleParams`
- [ ] Implement layout: words into rows, line breaking inside a `WritingArea`, word spacing jitter
- [ ] Implement `DefaultSynthesizer` and an app `write` command that outputs a white page PNG
- [ ] Estimate `StyleParams` (x-height, slant, spacing) from the captured samples instead of guessing

**Done when:** a paragraph you type comes out in your handwriting on a white page, different on every seed.

## Week 5: Ink that looks like ink

- [ ] `PenRenderer`: smooth strokes (Catmull-Rom or similar), variable width from pressure and the captured width profile
- [ ] Speed model: thinner and lighter on fast straight parts, heavier at turns and stroke starts
- [ ] Per-pen behaviour: ballpoint skips and small blobs, gel solid, pencil grain and low opacity, felt-tip soft edges
- [ ] Supersample and downscale for clean edges; ink colour variation along strokes

**Done when:** zoomed in, a rendered word is hard to tell apart from a scan of real ink.

## Week 6: Writing on a real page photo

- [ ] `PageAnalyzer`: find the sheet outline and compute the homography (flat mm to photo pixels)
- [ ] Detect ruling: blank, lined, grid or dot, plus pitch and offset (use `samples/pages/` as test photos)
- [ ] Estimate illumination of the bare paper
- [ ] `Compositor` v1: warp the ink layer into the photo and multiply-blend with lighting
- [ ] Writing area from ruling: baselines land on lines or grid rows, sensible margins

**Done when:** text appears on the sample notebook photo, following the grid and the camera angle.

## Week 7: Realism pass

- [ ] Joining: connect exit to entry between letters where the writer joins
- [ ] VariationModel v2: smooth elastic deformation and slight tremor
- [ ] Line and page drift: baseline wander, slope, gradual size and slant change, tighter letters near the right margin
- [ ] Compositor v2: match photo blur, noise, colour cast and JPEG softness
- [ ] Page curl near spiral bindings: mesh warp instead of a single homography
- [ ] Blind test: mix real and generated lines, see if someone can pick them out

**Done when:** a friend cannot reliably tell a generated page from a real one.

## Week 8: Multiple pens and polish

- [ ] Capture a second pen; confirm the two hands look different
- [ ] Pool shared habits across a writer's hands when a pen has few samples
- [ ] Graceful handling of missing characters (fallback glyphs, warnings)
- [ ] Optional: small JavaFX or web UI for upload page, choose hand, type text, preview

**Done when:** you can pick between two pens and the output matches each.

## Week 9 and later: Math

- [ ] Parse a small LaTeX subset: `^`, `_`, `\frac`, `\sqrt`, Greek letters, operators
- [ ] New box kinds: superscript, subscript, fraction (with a hand-drawn rule), radical
- [ ] Capture sheet for math symbols (=, +, -, ×, ÷, ∫, Σ, π, θ, parentheses at several sizes)
- [ ] Math layout rules: smaller scripts, centred fractions, aligned multi-line working
- [ ] Mixed text and math on one page

**Done when:** a short worked solution looks hand-written on grid paper.

## Ideas parking lot

- Stylus or tablet capture (real stroke order and pressure)
- Learned variation or full stroke generation trained in Python, run via ONNX
- Cross-outs and small corrections for extra realism
- Ink smudges and pressure indentations
