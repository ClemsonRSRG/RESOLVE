/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.proofsteps;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.Conjunct;
import edu.clemson.cs.r2jt.proving2.model.Consequent;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class ExistentialInstantiationStep extends AbstractProofStep {

    private final Conjunct myExistentialConsequent;
    private final List<PExp> myOriginalValues;
    private final int myExistentialConsequentIndex;

    public ExistentialInstantiationStep(Transformation t, Application a,
            Conjunct existentialConjunct, int existentialConjunctIndex,
            List<PExp> originalValues, Collection<Site> boundSites) {
        super(t, a, boundSites);

        myExistentialConsequent = existentialConjunct;
        myExistentialConsequentIndex = existentialConjunctIndex;
        myOriginalValues = originalValues;
    }

    @Override
    public void undo(PerVCProverModel m) {
        m.insertConjunct(myExistentialConsequent, myExistentialConsequentIndex);

        Iterator<PExp> originals = myOriginalValues.iterator();
        for (Consequent c : m.getConsequentList()) {
            m.alterSite(c.toSite(m), originals.next());
        }
    }
}
