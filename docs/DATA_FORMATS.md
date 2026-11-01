# Data formats

## Folder layout (local only, never committed)

```
data/
  writers/
    raphael/
      pages/          photos of handwritten pages, JPEG, full resolution
        IMG_2109.jpg
      transcripts/    one transcript per photo, same file name with .txt
        IMG_2109.txt
```

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

- **Header lines** start with `# key: value`. `photo` and `writer` are required; `pen`,
  `document` and `notes` are optional.
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
| `[drawing]` | a sketch, graph or diagram on its own lines |
| `[table]` | a table; transcribe cells left to right, top to bottom, one row per line, cells separated by ` | ` |
| `[margin] text` | a note written in the margin, on its own line, placed where it sits vertically |

- **Ignore** writing that shows through from the back of the page, and printed text.
