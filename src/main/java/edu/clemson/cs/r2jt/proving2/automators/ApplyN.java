/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.automators;

import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.Deque;

/**
 *
 * @author hamptos
 */
public class ApplyN implements Automator {

    private Transformation myTransformation;
    private int myRemainingApplications;

    public ApplyN(Transformation t, int count) {
        myTransformation = t;
        myRemainingApplications = count;
    }

    @Override
    public void step(Deque<Automator> stack, PerVCProverModel model) {
        if (myRemainingApplications == 0) {
            stack.pop();
        }
        else {
            myTransformation.getApplications(model).next().apply(model);
        }

        myRemainingApplications--;
    }

}
