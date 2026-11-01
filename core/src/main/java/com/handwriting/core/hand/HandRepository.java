package com.handwriting.core.hand;

import java.io.IOException;
import java.util.List;

/**
 * Loads and saves hands. Week 1: implement a JSON directory store, one folder per hand:
 * <pre>
 * profiles/
 *   raphael/
 *     bic-blue/
 *       hand.json          (pen + style)
 *       glyphs/a.json      (all samples of "a")
 *       glyphs/th.json     (all samples of the cluster "th")
 * </pre>
 */
public interface HandRepository {

    List<String> listHandIds() throws IOException;

    Hand load(String handId) throws IOException;

    void save(Hand hand) throws IOException;
}
