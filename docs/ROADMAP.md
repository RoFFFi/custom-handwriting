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

## Week 2: Pages and transcripts

- [ ] Define the transcript format (docs/DATA_FORMATS.md) as a `PageTranscript` model in capture, with a loader and tests
- [ ] Transcribe test writer 1 (Raphael): Journal 1, then Journal 3, then Journal 5, into `data/writers/raphael/transcripts/`
- [ ] Coverage report v1: per writer, count every letter, capital, digit, punctuation mark and common cluster across all transcripts; list what is well covered, thin and missing
- [ ] Gap-filling passage v1: generate a short text that uses the thin and missing letters and clusters, for the writer to copy by hand and upload
- [ ] Add test writer 2 (a friend, or pages from the IAM Handwriting Database) so nothing gets tuned to one person
- [ ] Measure habits from whole pages: line spacing, word gaps, slant, line drift and slope, margin behaviour

**Done when:** one command on a writer's transcripts prints their coverage and a passage that fills the gaps.

## Week 3: Photo to strokes

- [ ] Add OpenCV; find the page in the photo and flatten it to page space
- [ ] Separate ink from paper despite shadows; remove ruled lines and show-through from the back of the page
- [ ] Find text lines, split them into words at the gaps, pair each word with its transcript word and report mismatches
- [ ] Split each word into letters and clusters, guided by the transcript (keep merged shapes like a writer's "the" whole)
- [ ] Thin the ink to a skeleton and trace it into ordered strokes; measure ink width along each
- [ ] Confidence v2: score each letter and cluster by sample count and trace quality, not just counts
- [ ] Build a review sheet: each trace drawn over its photo, so tracing errors (picked-up rule lines, split loops) are easy to spot and fix. Messy letters are real variation and always stay.

**Done when:** the debug renderer draws letters and words cut from your own journal photos.

## Week 4: First real writing (on white)

- [ ] Implement `ClusterTokenizer` (random choice between clusters and single letters, weighted toward clusters)
- [ ] Implement `VariationModel` v1: per-glyph scale, slant and rotation jitter from `StyleParams`
- [ ] Implement `IncrementalWriter`: plan, look, adjust, commit for each word, updating `WriterState`
- [ ] Writer reactions v1: new line when a word will not fit, squeeze near the margin, gradual baseline drift and correction
- [ ] App `write` command that outputs a white lined page PNG, plus a debug option that saves one frame per word
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
- [ ] Build the `Sheet` from the analysis: ruled lines, margins, areas already inked

**Done when:** text appears on the sample notebook photo, following the grid and the camera angle.

## Week 7: Realism pass

- [ ] Joining: connect exit to entry between letters where the writer joins
- [ ] VariationModel v2: smooth elastic deformation and slight tremor
- [ ] Writer reactions v2: fatigue over the page, lines trailing off downhill, size creep, crossed-out words
- [ ] Compositor v2: match photo blur, noise, colour cast and JPEG softness
- [ ] Page curl near spiral bindings: mesh warp instead of a single homography
- [ ] Blind test: mix real and generated lines, see if someone can pick them out

**Done when:** a friend cannot reliably tell a generated page from a real one.

## Week 8: Multiple pens and polish

- [ ] Capture a second pen; confirm the two hands look different
- [ ] Pool shared habits across a writer's hands when a pen has few samples
- [ ] Graceful handling of missing characters (fallback glyphs, warnings)
- [ ] Automatic transcript suggestion for uploaded pages (handwriting recognition), which the writer confirms or corrects
- [ ] Onboarding flow: upload pages, confirm transcripts, see confidence, write the gap-filling passage
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
