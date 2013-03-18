/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.automators;

import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.Deque;
import java.util.Iterator;

/**
 *
 * @author hamptos
 */
public class ApplyAll implements Automator {

    private final Transformation myTransformation;
    private int myApplicationCount;

    public ApplyAll(Transformation t) {
        myTransformation = t;
    }

    public int getApplicationCount() {
        return myApplicationCount;
    }

    @Override
    public void step(Deque<Automator> stack, PerVCProverModel model) {
        Iterator<Application> applications =
                myTransformation.getApplications(model);

        if (applications.hasNext()) {
            applications.next().apply(model);
            myApplicationCount++;
        }
        else {
            stack.pop();
        }
    }
    
    @Override
    public String toString() {
        return "Apply all: " + myTransformation;
    }
}
