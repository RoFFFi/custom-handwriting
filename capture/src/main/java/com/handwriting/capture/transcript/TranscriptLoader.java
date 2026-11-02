package com.handwriting.capture.transcript;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Loads transcripts from disk. A writer's transcripts live in
 * {@code data/writers/<writer>/transcripts/}, in subfolders that mirror the photo folders
 * (one per document), with the same file name as the photo and a {@code .txt} extension.
 */
public final class TranscriptLoader {

    private TranscriptLoader() {}

    /**
     * Loads one transcript and checks that its {@code photo} header names the matching photo
     * ({@code IMG_2109.txt} must describe {@code IMG_2109.jpg}), which catches copy and paste
     * mistakes between files.
     */
    public static PageTranscript load(Path file) throws IOException {
        String source = file.toString();
        PageTranscript transcript = TranscriptParser.parse(source, Files.readString(file, StandardCharsets.UTF_8));
        String expected = stem(file.getFileName().toString());
        String photo = stem(Path.of(transcript.header().photo()).getFileName().toString());
        if (!photo.equals(expected)) {
            throw new TranscriptFormatException(source, 0,
                    "header says photo '" + transcript.header().photo() + "' but the file is for '" + expected + "'");
        }
        return transcript;
    }

    /** Loads every {@code .txt} transcript under a folder, including subfolders, sorted by path. */
    public static List<PageTranscript> loadAll(Path folder) throws IOException {
        List<Path> files;
        try (Stream<Path> walk = Files.walk(folder)) {
            files = walk.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".txt"))
                    .sorted()
                    .toList();
        }
        List<PageTranscript> transcripts = new ArrayList<>();
        for (Path file : files) {
            transcripts.add(load(file));
        }
        return transcripts;
    }

    private static String stem(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? fileName : fileName.substring(0, dot);
    }
}
