# Decisions

Short records of choices that shape the project. When you change one, add a new entry
instead of editing history, so you can see why things are the way they are.

---

## D1. Java 21 for the product, Python only for optional research

**Decision.** Everything that ships is Java 21, built with Gradle. A `research/` folder may
hold Python notebooks for experiments and model training.

**Why Java works here.** The approach (capture real letters, choose and vary them, render ink,
warp onto a photo) is geometry, image processing and careful engineering, not heavy deep
learning. Java is strong at exactly that:
- Java2D gives anti-aliased curves, affine transforms and image compositing out of the box.
- OpenCV has maintained Java bindings for the photo work (page detection, homography, thinning).
- Static types and records keep a multi-week codebase understandable as it grows.

**The trade-off.** Training neural networks is far easier in Python. If we later want a
learned handwriting model, train it in Python, export to ONNX, and run it in Java with
ONNX Runtime. The `Synthesizer` and `VariationModel` interfaces exist so this is a swap,
not a rewrite.

---

## D2. Handwriting is stored as strokes (vectors), not letter images

**Decision.** A captured letter is a list of strokes with per-point pressure and width,
not a cropped bitmap.

**Why.** Variation is the whole game. With strokes we can bend, slant, resize and re-ink a
letter and it still looks like a pen made it; the renderer can draw any pen on any page at
any resolution; and joining letters means connecting an exit point to an entry point.
Bitmaps would look pasted and would show the original pen forever.

**Cost.** Capture must trace photos into strokes (thinning plus path tracing), which is
harder than cropping. Stroke order from photos is a guess; a stylus or tablet capture could
be added later as a second `SampleExtractor`.

---

## D3. A "hand" is one writer with one pen

**Decision.** Samples and style are stored per (writer, pen), e.g. `raphael/bic-blue` and
`raphael/pencil-hb`.

**Why.** People write differently with different pens: pencil is lighter and more slanted,
a felt tip makes letters bigger. Keeping them separate lets the tool reproduce each. Shared
habits can later be pooled across a writer's hands if a pen has few samples.

---

## D4. One seeded random source

**Decision.** All randomness comes from `core.random.Rng`, created from a seed and forked
per subsystem.

**Why.** "It looked wrong on that one run" is impossible to debug otherwise. With a seed,
the same text and seed always produce the same page; tests can assert exact output; and
you can compare two versions of the code on identical randomness.

---

## D5. Layout is a box tree from the start

**Decision.** `synth.layout.Box` is a tree (rows, columns, glyphs, gaps), not a flat string.

**Amended by D6:** the box tree describes the shape of one unit (a word, an equation); placing units on the page happens one at a time.

**Why.** Math is on the roadmap. Fractions, exponents, square roots and aligned equations
are boxes inside boxes (the model TeX uses). Text is just the simple case of the same tree,
so math becomes new box kinds and new layout rules instead of a second engine.

---

## D6. The writer works word by word, like a person

**Decision.** Instead of laying out the whole text as a block and then drawing it, the
writer puts down one word at a time. Before each word it looks at its current situation
(where the pen is, how much room is left on the line, how far the writing has drifted from
the ruled line, how tired the hand is) and decides what to do. After each word it updates
that situation. Text can be fed in a word, a sentence or a page at a time.

**Why.** Real handwriting is shaped by reacting as you go. You notice the line drifting and
pull back to it, squeeze the last word in before the margin or give up and start a new
line, write smaller and faster as you tire, slope downhill toward the end of a line. A
block layout fits text perfectly, which is exactly what makes it look fake. Doing it
incrementally also means the strokes are recorded in writing order, so later the renderer
can replay them (ink running low, smudges) and we can animate the page being written.

**Cost.** Some decisions a block layout makes easily (balanced lines, never running out of
room) become harder. That is acceptable: people do not balance their lines either.

**Proposed by Raphael**, replacing the original block layout.
