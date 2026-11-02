package com.handwriting.capture.transcript;

/**
 * Something the writer did to a piece of text besides plainly writing it. Each mark matches
 * one piece of markup in docs/DATA_FORMATS.md.
 */
public enum Mark {
    /** {@code ~~word~~}: struck through. The letters are real but distorted by the line. */
    CROSSED_OUT("~~"),
    /** {@code _word_}: underlined. */
    UNDERLINED("_"),
    /** {@code [box]word[/box]}: boxed or circled. */
    BOXED("[box]"),
    /** {@code ^word^}: squeezed in above the line, usually smaller. */
    INSERTED("^"),
    /** {@code [word?]}: the transcriber's best guess, so the letters may be wrong. */
    GUESS("[word?]"),
    /** {@code [?]}: a word that cannot be read. The span has no text. */
    UNREADABLE("[?]");

    private final String markup;

    Mark(String markup) {
        this.markup = markup;
    }

    /** How this mark is written in a transcript, for error messages. */
    public String markup() {
        return markup;
    }
}
