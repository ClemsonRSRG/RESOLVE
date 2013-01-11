/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.utilities;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.Theorem;
import edu.clemson.cs.r2jt.utilities.Mapping;

/**
 *
 * @author hamptos
 */
public class TheoremUnwrapper implements Mapping<Theorem, PExp> {

    public static final TheoremUnwrapper INSTANCE = new TheoremUnwrapper();

    private TheoremUnwrapper() {

    }

    @Override
    public PExp map(Theorem input) {
        return input.getAssertion();
    }
}
