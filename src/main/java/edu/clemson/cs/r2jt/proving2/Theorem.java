/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import edu.clemson.cs.r2jt.proving2.justifications.Justification;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import java.util.List;

/**
 *
 * @author hamptos
 */
public class Theorem {

    /**
     * <p>Guaranteed not to have a top-level and (otherwise this would be two
     * theorems.)</p>
     */
    private final PExp myAssertion;
    private final Justification myJustification;

    public Theorem(PExp assertion, Justification justification) {
        myAssertion = assertion;
        myJustification = justification;
    }

    public PExp getAssertion() {
        return myAssertion;
    }

    public List<Transformation> getTransformations() {
        throw new UnsupportedOperationException();
    }
}
