package com.handwriting.capture.transcript;

import java.util.List;

/**
 * One line of a transcript. Most lines are {@link TextLine}s; the other kinds mark gaps,
 * drawings, table rows and margin notes, which later steps treat differently (a margin note
 * is not part of the main text flow, a table row is split into cells).
 */
public sealed interface TranscriptLine permits TextLine, BlankLine, DrawingLine, TableRow, MarginNote {

    /** Line number in the transcript file, starting at 1. Used in error messages and reports. */
    int sourceLine();

    /** Every handwritten span on this line, in reading order. Empty for a blank line. */
    List<Span> spans();
}
