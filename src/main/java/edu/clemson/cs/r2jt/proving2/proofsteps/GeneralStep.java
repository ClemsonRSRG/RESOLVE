/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.proofsteps;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.Conjunct;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class GeneralStep extends AbstractProofStep {

    private final List<Conjunct> myRemovedConjuncts;
    private final List<Integer> myRemovedConjunctsIndecis;

    private Map<Conjunct, PExp> myOriginalValues;

    private Set<Conjunct> myIntroducedConjuncts;

    public GeneralStep(List<Conjunct> removedConjuncts,
            List<Integer> removedConjunctsIndecis,
            Map<Conjunct, PExp> originalValues,
            Set<Conjunct> introducedConjuncts, Transformation t, Application a,
            Collection<Site> boundSites) {
        super(t, a, boundSites);

        myRemovedConjuncts = removedConjuncts;
        myRemovedConjunctsIndecis = removedConjunctsIndecis;

        myOriginalValues = originalValues;

        myIntroducedConjuncts = introducedConjuncts;
    }

    public Set<Conjunct> getIntroducedConjuncts() {
        return myIntroducedConjuncts;
    }

    @Override
    public void undo(PerVCProverModel m) {
        //Reintroduce removed conjuncts
        Iterator<Conjunct> removedConjunctIter = myRemovedConjuncts.iterator();
        Iterator<Integer> removedConjunctIndexIter =
                myRemovedConjunctsIndecis.iterator();

        while (removedConjunctIter.hasNext()) {
            m.insertConjunct(removedConjunctIter.next(),
                    removedConjunctIndexIter.next());
        }

        //Restore original values
        for (Map.Entry<Conjunct, PExp> originalValue : myOriginalValues
                .entrySet()) {
            m.alterConjunct(originalValue.getKey(), originalValue.getValue());
        }

        //Remove introduced conjuncts
        for (Conjunct introducedConjunct : myIntroducedConjuncts) {
            m.removeConjunct(introducedConjunct);
        }
    }

    @Override
    public String toString() {
        return "" + getTransformation();
    }
}
