package com.handwriting.synth;

import com.handwriting.core.glyph.GlyphKey;
import com.handwriting.core.glyph.GlyphLibrary;
import com.handwriting.core.random.Rng;
import java.util.List;

/**
 * Splits a word into glyph keys the library can draw. "thing" might become
 * [th, ing] one time and [t, h, i, ng] the next: alternating randomly between single letters
 * and clusters is a core realism feature.
 *
 * <p>Week 4: implement {@code ClusterTokenizer}. Suggested approach: walk the word left to
 * right; at each position collect every library key that matches the upcoming characters
 * (up to {@link GlyphLibrary#longestSpan()}); choose one with a weighted random pick that
 * favours longer clusters. Fall back to a placeholder for characters the user never wrote.
 */
public interface Tokenizer {

    List<GlyphKey> tokenize(String word, GlyphLibrary library, Rng rng);
}
