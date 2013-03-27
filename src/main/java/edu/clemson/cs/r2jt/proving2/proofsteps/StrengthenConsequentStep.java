/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.proofsteps;

import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class StrengthenConsequentStep implements ProofStep {

    private static final SiteIndexComparator BY_INDEX =
            new SiteIndexComparator();

    private final Collection<Site> myEliminatedSites;
    private final int myIntroducedCount;
    private final Transformation myTransformation;
    private final Set<Site> myNewSites;

    public StrengthenConsequentStep(Collection<Site> eliminatedSites,
            Set<Site> newSites, int introducedCount,
            Transformation transformation) {
        myEliminatedSites = eliminatedSites;
        myIntroducedCount = introducedCount;
        myTransformation = transformation;
        myNewSites = newSites;
    }

    @Override
    public Transformation getTransformation() {
        return myTransformation;
    }

    @Override
    public Set<Site> getPrerequisiteSites() {
        return new HashSet<Site>(myEliminatedSites);
    }

    @Override
    public Set<Site> getAffectedSites() {
        return myNewSites;
    }

    @Override
    public void undo(PerVCProverModel m) {
        for (int i = 0; i < myIntroducedCount; i++) {
            m.removeConsequent(m.getConsequentList().size() - 1);
        }

        List<Site> sites = new ArrayList<Site>(myEliminatedSites);
        Collections.sort(sites, BY_INDEX);

        //Note that the same site could appear twice
        int lastIndex = -1;
        for (Site s : sites) {

            if (s.index != lastIndex) {
                m.addConsequent(s.exp, s.index);
            }

            lastIndex = s.index;
        }
    }

    @Override
    public String toString() {
        return "" + myTransformation;
    }

    /**
     * <p>Sorts Sites in order by index.</p>
     */
    private static class SiteIndexComparator implements Comparator<Site> {

        @Override
        public int compare(Site o1, Site o2) {
            return o1.index - o2.index;
        }
    }
}
