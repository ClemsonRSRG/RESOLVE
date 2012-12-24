/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.proving2.justifications.Given;
import edu.clemson.cs.r2jt.proving.ProofStep;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PExpVisitor;
import edu.clemson.cs.r2jt.proving.immutableadts.ImmutableList;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class PerVCProverModel {

    /**
     * <p>A set of local theorems for quick searching.  It's contents are always
     * the same as the <code>PExp</code>s embedded in the 
     * <code>LocalTheorem</code>s of <code>myLocalTheoremsList</code>.  Note
     * that this means no <code>PExp</code> in this set will have a top-level
     * "and".</p>
     */
    private final Set<PExp> myLocalTheoremsSet = new HashSet<PExp>();

    /**
     * <p>A list of local theorems in the order they were introduced, for 
     * friendly displaying and tagged with useful information.  The set of
     * <code>PExp</code>s embedded in this list's elements will always be the
     * same as those <code>PExp</code>s in <code>myLocalTheoremSet</code>.</p>
     */
    private final List<LocalTheorem> myLocalTheoremsList =
            new LinkedList<LocalTheorem>();

    /**
     * <p>A list of expressions remaining to be established as true.  Each of 
     * these is guaranteed not to have a top-level "and" expression (otherwise 
     * the conjuncts would have been broken up into separate entries in this 
     * list.) Once we empty this list, the proof is complete.</p>
     */
    private final List<PExp> myConsequents = new LinkedList<PExp>();

    /**
     * <p>A list of the current proof under consideration.  Starting with a 
     * fresh <code>PerVCProverModel</code> initialized with the consequents, 
     * antecedents, and global theorems originally provided to this class, then
     * applying these steps in order, would bring the fresh model into the exact
     * same state that this one is currently in.</p>
     */
    private final List<ProofStep> myProofSoFar = new LinkedList<ProofStep>();

    public PerVCProverModel(List<PExp> antecedents, List<PExp> consequents) {

        myLocalTheoremsSet.addAll(antecedents);
        for (PExp assumption : antecedents) {
            myLocalTheoremsList.add(new LocalTheorem(assumption, new Given(),
                    false));
        }

        myConsequents.addAll(consequents);
    }

    public PerVCProverModel(VC vc) {
        this(listFromIterable(vc.getAntecedent()), listFromIterable(vc
                .getConsequent()));
    }

    private static List<PExp> listFromIterable(Iterable<PExp> i) {
        List<PExp> result = new LinkedList<PExp>();
        for (PExp e : i) {
            result.add(e);
        }

        return result;
    }

    public void processStringRepresentation(PExpVisitor visitor, Appendable a) {

        try {
            boolean first = true;
            for (LocalTheorem t : myLocalTheoremsList) {

                if (first) {
                    first = false;
                }
                else {
                    a.append(" and\n");
                }

                t.getAssertion().processStringRepresentation(visitor, a);
            }

            a.append("\n  -->\n");

            first = true;
            for (PExp c : myConsequents) {

                if (first) {
                    first = false;
                }
                else {
                    a.append(" and\n");
                }

                c.processStringRepresentation(visitor, a);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
