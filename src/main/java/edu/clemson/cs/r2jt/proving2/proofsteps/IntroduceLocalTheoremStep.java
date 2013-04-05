/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.proofsteps;

import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.Conjunct;
import edu.clemson.cs.r2jt.proving2.model.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Theorem;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class IntroduceLocalTheoremStep extends AbstractProofStep {

    private final LocalTheorem myIntroducedTheorem;
    private final Set<Theorem> myPrerequisiteTheorems;

    public IntroduceLocalTheoremStep(LocalTheorem introducedTheorem,
            Set<Theorem> prerequisiteTheorems, Transformation t, Application a) {
        super(t, a);

        myIntroducedTheorem = introducedTheorem;
        myPrerequisiteTheorems =
                Collections.unmodifiableSet(prerequisiteTheorems);
    }

    public Set<Theorem> getPrerequisiteTheorems() {
        return new HashSet<Theorem>(myPrerequisiteTheorems);
    }

    public LocalTheorem getIntroducedTheorem() {
        return myIntroducedTheorem;
    }

    @Override
    public String toString() {
        return "" + getTransformation();
    }

    @Override
    public void undo(PerVCProverModel m) {
        m.removeLocalTheorem(myIntroducedTheorem);
    }

}
