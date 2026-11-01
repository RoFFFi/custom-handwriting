package com.handwriting.synth;

/**
 * What a person "knows" while writing: where the pen is, how the current line is going,
 * how tired the hand is. The writer reads this before every word and updates it after.
 * This is what lets the output react to itself instead of being fitted as a block.
 * Expect this to grow as realism improves.
 *
 * @param lineIndex       which ruled line is being written on
 * @param penXmm          where the next word starts
 * @param baselineDriftMm how far the actual writing has wandered from the ruled line
 *                        (positive = below it). Builds up word by word; the writer corrects
 *                        it gradually, the way people notice and pull back to the line.
 * @param lineSlopeDeg    current tendency of the line to rise or fall
 * @param sizeDrift       current size multiplier (letters creep larger or smaller over time)
 * @param fatigue         0 = fresh, 1 = tired: more mess, faster and looser strokes
 * @param wordsWritten    words so far in this session
 */
public record WriterState(
        int lineIndex,
        double penXmm,
        double baselineDriftMm,
        double lineSlopeDeg,
        double sizeDrift,
        double fatigue,
        int wordsWritten) {
}
