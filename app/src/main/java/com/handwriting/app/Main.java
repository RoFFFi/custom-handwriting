package com.handwriting.app;

import com.handwriting.capture.coverage.CoverageCounter;
import com.handwriting.capture.coverage.CoverageReport;
import com.handwriting.capture.transcript.PageTranscript;
import com.handwriting.capture.transcript.TranscriptLoader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command line entry point. Run with {@code ./gradlew :app:run --args="help"}.
 *
 * <p>Planned commands (see docs/ROADMAP.md):
 * <pre>
 *   coverage  data/writers/raphael/transcripts      count letters and clusters   (week 2)
 *   template --out sheet.pdf                       print a capture sheet        (week 2)
 *   capture   --hand raphael/bic-blue photo.jpg     learn from a filled sheet    (week 3)
 *   write     --hand raphael/bic-blue --page page.jpg --text "..." --seed 42 --out result.png
 *                                                   the end goal                 (weeks 4 to 7)
 * </pre>
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) throws IOException {
        String command = args.length == 0 ? "help" : args[0];
        switch (command) {
            case "help", "--help", "-h" -> printHelp();
            case "coverage" -> {
                if (args.length != 2) {
                    System.err.println("Usage: coverage <transcripts folder>, for example data/writers/raphael/transcripts");
                    System.exit(2);
                }
                coverage(Path.of(args[1]));
            }
            default -> {
                System.err.println("Unknown or not yet implemented command: " + command);
                printHelp();
                System.exit(2);
            }
        }
    }

    /** Prints which letters, digits, punctuation marks and clusters a writer's pages cover. */
    private static void coverage(Path folder) throws IOException {
        List<PageTranscript> transcripts = TranscriptLoader.loadAll(folder);
        String writers = transcripts.stream()
                .map(transcript -> transcript.header().writer())
                .distinct()
                .collect(Collectors.joining(", "));
        System.out.print(CoverageReport.format(writers.isEmpty() ? folder.toString() : writers,
                CoverageCounter.count(transcripts)));
    }

    private static void printHelp() {
        System.out.println("""
                custom-handwriting

                Commands:
                  help        show this message
                  coverage    count the letters and clusters in a folder of transcripts
                  template    (week 2) generate a capture sheet
                  capture     (week 3) learn glyphs from a photographed sheet
                  write       (weeks 4 to 7) write text onto a page photo
                """);
    }
}
