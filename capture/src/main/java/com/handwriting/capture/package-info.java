/**
 * Learning a writer's handwriting from pages they already wrote (see docs/DECISIONS.md, D7).
 *
 * <p>Planned pipeline:
 * <ol>
 *   <li><b>Pages and transcripts</b> (week 2): a photo of each page plus the text it says,
 *       in the format of docs/DATA_FORMATS.md. Later the transcript is suggested
 *       automatically and the writer only confirms it.</li>
 *   <li><b>Coverage and confidence</b> (week 2, then week 3): for every letter and cluster,
 *       how many good samples exist. Weak spots drive a short gap-filling passage the writer
 *       copies by hand.</li>
 *   <li><b>Photo to words</b> (week 3): flatten the page, remove ruled lines and show-through,
 *       separate ink from paper, find lines and words, pair each word with the transcript.</li>
 *   <li><b>Words to samples</b> (week 3): split words into letters and clusters guided by the
 *       transcript, trace the ink into strokes, store them as
 *       {@link com.handwriting.core.glyph.GlyphSample}s.</li>
 * </ol>
 */
package com.handwriting.capture;
