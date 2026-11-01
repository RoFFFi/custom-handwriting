/**
 * The "writer": turns text into strokes placed on a page.
 *
 * <p>Pipeline: text -> {@link com.handwriting.synth.Tokenizer} (pick letters and clusters)
 * -> {@link com.handwriting.synth.layout.Box} tree (where things go)
 * -> {@link com.handwriting.synth.VariationModel} (make each instance unique)
 * -> {@link com.handwriting.core.ink.InkDocument}.
 */
package com.handwriting.synth;
