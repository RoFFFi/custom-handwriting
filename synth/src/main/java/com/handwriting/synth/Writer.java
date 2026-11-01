package com.handwriting.synth;

import com.handwriting.core.hand.Hand;
import com.handwriting.core.random.Rng;
import com.handwriting.core.sheet.Sheet;

/**
 * Writes the way a person does: one word at a time, looking at what is already on the page
 * before deciding where the next word goes. See docs/DECISIONS.md, D6.
 *
 * <p>Week 4: implement {@code IncrementalWriter}. Each word goes through:
 * <ol>
 *   <li><b>Plan</b>: tokenize into letters and clusters, pick samples, vary them, measure the width.</li>
 *   <li><b>Look</b>: given {@link WriterState}, does it fit before the margin? Is the line drifting?</li>
 *   <li><b>Adjust</b>: squeeze the word, start a new line, pull the baseline back toward the rule,
 *       or let it trail off a little, as real writers do.</li>
 *   <li><b>Commit</b>: append its strokes to the session's ink record, update the state.</li>
 * </ol>
 */
public interface Writer {

    WritingSession begin(Hand hand, Sheet sheet, Rng rng);
}
