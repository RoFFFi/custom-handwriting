# Data formats

## Folder layout (local only, never committed)

```
data/
  writers/
    raphael/
      pages/                  photos of handwritten pages, JPEG, full resolution
        journal-3/            one folder per document
          IMG_2109.jpg
        journal-5/
          IMG_2408.jpg
          earlier-versions/   older photos of a page that was later added to
            IMG_2407.jpg
      transcripts/            same folders as pages/, same file name with .txt
        journal-3/
          IMG_2109.txt
        journal-5/
          IMG_2408.txt
```

Photos in `earlier-versions/` are kept for reference but not transcribed, so no letter is
counted twice. A transcript's `photo` header must name its own photo (`IMG_2109.txt`
describes `IMG_2109.jpg`); the loader checks this.

`data/` is git-ignored. Convert iPhone HEIC photos to JPEG first
(on macOS: `sips -s format jpeg IMG_2109.HEIC --out IMG_2109.jpg`).

## Transcript (`.txt`)

A transcript says exactly what is written on one photo, **as written**, line by line.
It is how the tool knows which marks are which letters, so accuracy matters more than speed.

```
# photo: IMG_2109.jpg
# writer: raphael
# pen: black-ballpoint
# document: Music Hum Journal 3, page 1
Music Hum Journal 3
Listening environment: I am alone in my room,
listening on my laptop speakers. It is a quiet
environment but I am a little bit tired.
[box]FIRST LISTEN[/box]

The words that Strozzi repeats are primarily ~~tentatively~~
```

### Rules

- **Header lines** start with `# key: value` and come first in the file. `photo` and
  `writer` are required; `pen`, `document` and `notes` are optional. Any other key is an
  error, so typos are caught. After the first line of writing, a line starting with `#` is
  ordinary handwriting (for a `#` on the very first line of writing, use `\#`).
- **One transcript line per handwritten line**, with the line breaks exactly where the
  writer made them. A word split across lines stays split.
- **Write what is on the paper, not what was meant.** Misspellings, missing letters and
  odd capitals stay as they are, because they are part of the handwriting.
- **A blank line** marks a gap of one or more empty ruled lines (a paragraph break). Lines
  with no writing are otherwise not included.
- **Markup** for things that are not plain text:

| Markup | Meaning |
|--------|---------|
| `~~word~~` | crossed out |
| `_word_` | underlined |
| `[box]word[/box]` | boxed or circled |
| `^word^` | inserted above the line (a correction or addition) |
| `[?]` | a word that cannot be read |
| `[word?]` | a best guess at a hard-to-read word |
| `[drawing]` | a sketch, graph or diagram, on its own line |
| `[drawing] Flute, Oboe` | the same, with the words handwritten inside the drawing |
| `[table]` ... `[/table]` | a table: each line in between is one row, top to bottom, cells left to right separated by `\|` (an empty cell is allowed) |
| `[margin] text` | a note written in the margin, on its own line, placed where it sits vertically |

- **Marks open and close on the same line.** A phrase crossed out across two lines is
  marked on each line. Marks can combine: `_~~word~~_`.
- **Backslash** writes a markup character that is really in the handwriting: `\[`, `\]`,
  `\_`, `\~`, `\^`, `\|`, `\#`, `\\`. For example `\[at least\]` for brackets the
  writer drew.
- **Scattered notes** (short notes placed around a page rather than on the lines) are
  written top to bottom, then left to right, one note per line. Use `[margin]` for notes
  that clearly sit in the margin. Exact positions come from the photo later.
- **Ignore** writing that shows through from the back of the page, and printed text.
