/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.proofsteps;

import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;

/**
 *
 * @author hamptos
 */
public class LabelStep implements ProofStep {

    private final String myLabel;

    public LabelStep(String label) {
        myLabel = label;
    }

    @Override
    public void undo(PerVCProverModel m) {

    }

    @Override
    public String toString() {
        return myLabel;
    }
}
