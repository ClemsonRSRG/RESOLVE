/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.proofsteps;

import edu.clemson.cs.r2jt.proving2.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.Theorem;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import edu.clemson.cs.r2jt.typeandpopulate.NoSolutionException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class IntroduceLocalTheoremStep implements ProofStep {

    private final Site myIntroducedSite;
    private final LocalTheorem myLocalTheorem;
    private final Transformation myTransformation;
    private final Set<Theorem> myPrerequisiteTheorems;
    private final Collection<Site> myPrerequisiteSites;

    public IntroduceLocalTheoremStep(Site introducedSite,
            Transformation transformation, Collection<Site> prerequisiteSites) {
        myIntroducedSite = introducedSite;

        try {
            myLocalTheorem = (LocalTheorem) myIntroducedSite.getRootTheorem();
        }
        catch (NoSolutionException nse) {
            throw new IllegalArgumentException("Site " + introducedSite
                    + " not a local theorem.");
        }
        catch (ClassCastException cce) {
            throw new IllegalArgumentException("Site " + introducedSite
                    + " not a local theorem.");
        }

        myTransformation = transformation;
        myPrerequisiteSites = prerequisiteSites;

        myPrerequisiteTheorems = new HashSet<Theorem>();
        for (Site s : prerequisiteSites) {
            try {
                myPrerequisiteTheorems.add(s.getRootTheorem());
            }
            catch (NoSolutionException nse) {
                throw new IllegalArgumentException("Site " + s + " does not "
                        + "identify a theorem.");
            }
        }
    }

    @Override
    public Transformation getTransformation() {
        return myTransformation;
    }

    @Override
    public Set<Site> getPrerequisiteSites() {
        return new HashSet<Site>(myPrerequisiteSites);
    }

    @Override
    public Set<Site> getAffectedSites() {
        return Collections.singleton(myIntroducedSite.root);
    }

    public Set<Theorem> getPrerequisiteTheorems() {
        return myPrerequisiteTheorems;
    }

    public LocalTheorem getIntroducedTheorem() {
        return myLocalTheorem;
    }

    @Override
    public String toString() {
        return "" + myTransformation;
    }

    @Override
    public void undo(PerVCProverModel m) {
        m.removeLocalTheorem(myLocalTheorem);
    }

}
