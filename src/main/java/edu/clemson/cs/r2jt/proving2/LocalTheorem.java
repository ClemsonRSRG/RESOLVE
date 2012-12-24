/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.proving2.justifications.Justification;
import edu.clemson.cs.r2jt.proving.absyn.PExp;

/**
 *
 * @author hamptos
 */
public class LocalTheorem extends Theorem {

    /**
     * <p>Mathematically speaking, once we successfully move all the consequents
     * "above the line", i.e., establish them as things we know, we're done,
     * and we don't care which things above the line were part of our original
     * givens and which were the things we were originally trying to establish.
     * Practically, however, in order to reconstruct the proof we need to know
     * which antecedents started life as consequents.  This flag is set to 
     * indicate that an established truth is one of the things we were 
     * <em>trying</em> to establish, rather than some intermediary or original
     * given.</p>
     */
    private boolean myThingWeWereTryingToProveFlag;

    public LocalTheorem(PExp assertion, Justification justification,
            boolean tryingToProveThis) {

        super(assertion, justification);

        myThingWeWereTryingToProveFlag = tryingToProveThis;
    }
}
