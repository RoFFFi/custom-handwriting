rootProject.name = "custom-handwriting"

// Each module has one job. Dependencies only point "down" this list:
//   app -> page -> render -> core
//   app -> synth -> core,  app -> capture -> core
include(
    "core",     // domain model: strokes, glyphs, hands, pens, seeded randomness. No dependencies.
    "capture",  // turning photos of the user's handwriting into stroke samples
    "synth",    // text -> layout -> varied strokes placed on a page (the "writer")
    "render",   // strokes + pen model -> ink raster layer
    "page",     // understanding the target paper photo and compositing ink onto it
    "app",      // command line entry point that wires everything together
)
