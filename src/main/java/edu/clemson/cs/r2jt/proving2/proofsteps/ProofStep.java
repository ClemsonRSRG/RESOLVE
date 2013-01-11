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
public interface ProofStep {

    public void undo(PerVCProverModel m);
}
