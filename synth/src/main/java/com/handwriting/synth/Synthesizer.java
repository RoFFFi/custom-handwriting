package com.handwriting.synth;

import com.handwriting.core.hand.Hand;
import com.handwriting.core.ink.InkDocument;
import com.handwriting.core.random.Rng;

/** Top-level entry point of the module. Week 4: implement {@code DefaultSynthesizer}. */
public interface Synthesizer {

    InkDocument write(Hand hand, String text, WritingArea area, double pageWidthMm, double pageHeightMm, Rng rng);
}
