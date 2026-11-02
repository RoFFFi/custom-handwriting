package com.handwriting.capture.transcript;

import java.util.Objects;
import java.util.Optional;

/**
 * The {@code # key: value} lines at the top of a transcript: which photo it describes, who
 * wrote it, and optionally the pen, the document and free notes.
 */
public record TranscriptHeader(
        String photo, String writer, Optional<String> pen, Optional<String> document, Optional<String> notes) {

    public TranscriptHeader {
        Objects.requireNonNull(photo, "photo");
        Objects.requireNonNull(writer, "writer");
        Objects.requireNonNull(pen, "pen");
        Objects.requireNonNull(document, "document");
        Objects.requireNonNull(notes, "notes");
    }
}
