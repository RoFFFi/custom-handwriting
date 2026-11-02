package com.handwriting.capture.transcript;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TranscriptLoaderTest {

    private static Path resources() throws URISyntaxException {
        return Path.of(TranscriptLoaderTest.class.getResource("/transcripts").toURI());
    }

    @Test
    void loadsEveryTranscriptInSubfoldersInPathOrder() throws Exception {
        List<PageTranscript> all = TranscriptLoader.loadAll(resources());

        assertThat(all).extracting(t -> t.header().photo()).containsExactly("page-01.jpg", "page-02.jpg");
        PageTranscript first = all.get(0);
        assertThat(first.header().document()).contains("Invented journal, page 1");
        assertThat(first.lines()).hasSize(6);
        assertThat(first.lines().get(4)).isInstanceOf(BlankLine.class);
        assertThat(first.spans()).filteredOn(Span::isClean).extracting(Span::text)
                .doesNotContain("shop", "wondred");
        assertThat(all.get(1).lines()).hasExactlyElementsOfTypes(
                DrawingLine.class, TableRow.class, TableRow.class, MarginNote.class);
    }

    @Test
    void rejectsATranscriptWhosePhotoHeaderNamesAnotherPhoto(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("IMG_2.txt");
        Files.writeString(file, "# photo: IMG_1.jpg\n# writer: w\nhi\n");

        assertThatThrownBy(() -> TranscriptLoader.load(file))
                .isInstanceOf(TranscriptFormatException.class)
                .hasMessageContaining("header says photo 'IMG_1.jpg' but the file is for 'IMG_2'");
    }

    @Test
    void errorsNameTheFile(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("IMG_3.txt");
        Files.writeString(file, "# photo: IMG_3.jpg\n# writer: w\n~~oops\n");

        assertThatThrownBy(() -> TranscriptLoader.load(file))
                .hasMessageContaining("IMG_3.txt line 3: unclosed ~~");
    }
}
