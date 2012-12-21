/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.proving.absyn.PExp;

/**
 *
 * @author hamptos
 */
public class Theorem {
    
    private final PExp myAssertion;
    private final Justification myJustification;
    
    public Theorem(PExp assertion, Justification justification) {
        myAssertion = assertion;
        myJustification = justification;
    }
}
