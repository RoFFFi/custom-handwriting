package com.handwriting.capture.transcript;

/** A transcript that does not follow docs/DATA_FORMATS.md. The message names the file and line. */
public class TranscriptFormatException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String source;
    private final int line;

    /**
     * @param source file name or other label, for the message
     * @param line   line number starting at 1, or 0 when the problem is the file as a whole
     */
    public TranscriptFormatException(String source, int line, String problem) {
        super(source + (line > 0 ? " line " + line : "") + ": " + problem);
        this.source = source;
        this.line = line;
    }

    public String source() {
        return source;
    }

    public int line() {
        return line;
    }
}
