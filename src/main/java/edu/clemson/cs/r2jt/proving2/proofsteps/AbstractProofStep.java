/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.proofsteps;

import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.Conjunct;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public abstract class AbstractProofStep implements ProofStep {

    private final Transformation myTransformation;
    private final Application myApplication;
    private final Collection<Site> myBoundSites;
    
    private Set<Conjunct> myBoundConjuncts;

    public AbstractProofStep(Transformation t, Application a, 
            Collection<Site> boundSites) {
        myTransformation = t;
        myApplication = a;
        myBoundSites = boundSites;
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
    public final Set<Conjunct> getPrerequisiteConjuncts() {
        return myApplication.getPrerequisiteConjuncts();
    }
    
    @Override
    public final Set<Conjunct> getBoundConjuncts() {
        if (myBoundConjuncts == null) {
            myBoundConjuncts = new HashSet<Conjunct>();
            
            if (myBoundSites != null) {
                for (Site s : myBoundSites) {
                    myBoundConjuncts.add(s.conjunct);
                }
            }
            
            myBoundConjuncts = Collections.unmodifiableSet(myBoundConjuncts);
        }
        
        return myBoundConjuncts;
    }

    @Override
    public final Set<Conjunct> getAffectedConjuncts() {
        return myApplication.getAffectedConjuncts();
    }

    @Override
    public final Set<Site> getAffectedSites() {
        return myApplication.getAffectedSites();
    }
}
