package com.handwriting.synth;

import com.handwriting.core.ink.InkDocument;

/**
 * One sheet being written on. Text can be fed in any size of chunk (a word, a sentence, a
 * paragraph); internally it is always written word by word, so feeding a sentence at a time
 * gives the same result as feeding the whole text at once with the same seed.
 */
public interface WritingSession {

    /** Write more text, continuing from where the pen is. Returns false if the sheet is full. */
    boolean write(String text);

    /** The writer's current situation, useful for debugging and for tests. */
    WriterState state();

    /** Everything written so far, strokes in the order they were written. */
    InkDocument ink();
}
