/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.proofsteps;

import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.Conjunct;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public abstract class AbstractProofStep implements ProofStep {

    private final Transformation myTransformation;
    private final Application myApplication;

    public AbstractProofStep(Transformation t, Application a) {
        myTransformation = t;
        myApplication = a;
    }

    @Override
    public final Transformation getTransformation() {
        return myTransformation;
    }

    @Override
    public final Application getApplication() {
        return myApplication;
    }

    @Override
    public final Set<Conjunct> getPrerequisiteSites() {
        return myApplication.getPrerequisiteConjuncts();
    }

    @Override
    public final Set<Conjunct> getAffectedSites() {
        return myApplication.getAffectedConjuncts();
    }
}
