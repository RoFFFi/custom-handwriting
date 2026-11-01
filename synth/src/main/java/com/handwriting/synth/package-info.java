/**
 * The "writer": puts text on a sheet one word at a time, reacting to what it has already
 * written, the way a person does.
 *
 * <p>For each word: {@link com.handwriting.synth.Tokenizer} (pick letters and clusters)
 * -> {@link com.handwriting.synth.layout.Box} (shape of the word or, later, a math expression)
 * -> {@link com.handwriting.synth.VariationModel} (make each instance unique)
 * -> {@link com.handwriting.synth.Writer} decides where it goes given
 * {@link com.handwriting.synth.WriterState}, then appends it to the ink record.
 */
package com.handwriting.synth;
