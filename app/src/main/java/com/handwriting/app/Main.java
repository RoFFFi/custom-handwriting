package com.handwriting.app;

/**
 * Command line entry point. Run with {@code ./gradlew :app:run --args="help"}.
 *
 * <p>Planned commands (see docs/ROADMAP.md):
 * <pre>
 *   template  --out sheet.pdf                       print a capture sheet        (week 2)
 *   capture   --hand raphael/bic-blue photo.jpg     learn from a filled sheet    (week 3)
 *   write     --hand raphael/bic-blue --page page.jpg --text "..." --seed 42 --out result.png
 *                                                   the end goal                 (weeks 4 to 7)
 * </pre>
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        String command = args.length == 0 ? "help" : args[0];
        switch (command) {
            case "help", "--help", "-h" -> printHelp();
            default -> {
                System.err.println("Unknown or not yet implemented command: " + command);
                printHelp();
                System.exit(2);
            }
        }
    }

    private static void printHelp() {
        System.out.println("""
                custom-handwriting

                Commands:
                  help        show this message
                  template    (week 2) generate a capture sheet
                  capture     (week 3) learn glyphs from a photographed sheet
                  write       (weeks 4 to 7) write text onto a page photo
                """);
    }
}
