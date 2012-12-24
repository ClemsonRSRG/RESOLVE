/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.transformations;

import edu.clemson.cs.r2jt.proving.Antecedent;
import edu.clemson.cs.r2jt.proving.ChainingIterable;
import edu.clemson.cs.r2jt.proving.ConditionalAntecedentExtender;
import edu.clemson.cs.r2jt.proving.DummyIterator;
import edu.clemson.cs.r2jt.proving.IncrementalBindingIterator;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.proving2.BindingsIterator;
import edu.clemson.cs.r2jt.proving2.TotalBindingIterator;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * <p>A transformation that applies "A and B and C implies D" by first seeing
 * if all antecedents (A, B, and C) can be directly matched against givens or
 * global theorems, and if so, adding a new given matching the form of D, with 
 * variables appropriately replaced based on the matching of the antecedents.
 * </p>
 * 
 * <p><strong>Random Quirk:</strong> Because the intention of this class is to
 * extend available assumptions based on local contextual data, at least one
 * variable binding when matching the theorem antecedent against known facts
 * must come from the prover state's antecedent.  That is, the prover state will
 * not be extended with applications of this conditional theorem entirely to 
 * global facts--those "extensions" should themselves be listed as global 
 * theorems.</p>
 * 
 * <p><strong>Example:</strong>  Given the theorem 
 * <code>|S| &gt; 0 implies S /= Empty_String</code>, consider the following
 * VC:</p>
 * 
 * <pre>
 * |T| > 0
 * --->
 * |S o T| > |S|
 * </pre>
 * 
 * One (the only) application of this transformation would be:
 * 
 * <pre>
 * |T| > 0 and
 * T /= Empty_String
 * --->
 * |S o T| > |S|
 * </pre>
 */
public class ExpandAntecedentByImplication implements Transformation {

    private final ImmutableList<PExp> myAntecedents;
    private final ImmutableList<PExp> myGlobalTheorems;

    private final int myAntecedentsSize;

    private final PExp myConsequent;

    public ExpandAntecedentByImplication(ImmutableList<PExp> antecedents,
            PExp consequent, ImmutableList<PExp> globalTheorems) {
        myAntecedents = antecedents;
        myAntecedentsSize = antecedents.size();
        myConsequent = consequent;
        myGlobalTheorems = globalTheorems;
    }

    /**
     * <p>An <code>ExtendedAntecedentsIterator</code> iterates over variations
     * of the application of the conditional theorem embedded in this
     * <code>ConditionalAntecedentExtender</code> to a given VC antecedent,
     * given a set of global facts.</p>
     * 
     * <p>See the note on the random quirk in the parent class comments.</p>
     */
    private class ExtendedAntecedentsIterator
            implements
                Iterator<Map<PExp, PExp>> {

        private final ImmutableList<PExp> myPatterns;

        private int myLocalConditionIndex;

        private Iterator<Map<PExp, PExp>> myLocalConditionApplications;
        private Map<PExp, PExp> myNextBinding;

        public ExtendedAntecedentsIterator(ImmutableList<PExp> patterns) {

            myPatterns = patterns;
            myLocalConditionIndex = 0;

            myLocalConditionApplications =
                    DummyIterator.getInstance(myLocalConditionApplications);

            setUpNext();
        }

        private void setUpNext() {
            while (!myLocalConditionApplications.hasNext()
                    && myLocalConditionIndex < myAntecedentsSize) {

                myLocalConditionApplications =
                        new QuirkyBindingIterator(myPatterns
                                .get(myLocalConditionIndex), myPatterns
                                .removed(myLocalConditionIndex), myPatterns);
                myLocalConditionIndex++;
            }

            if (myLocalConditionApplications.hasNext()) {
                myNextBinding = myLocalConditionApplications.next();
            }
            else {
                myNextBinding = null;
            }
        }

        @Override
        public boolean hasNext() {
            return myNextBinding != null;
        }

        @Override
        public Map<PExp, PExp> next() {
            Map<PExp, PExp> retval = myNextBinding;

            setUpNext();

            return retval;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * <p>A <code>QuirkyBindingIterator</code> enforces the random quirk listed
     * in <code>ConditionalAntecedentExtender</code>'s class comments.  It
     * accepts a pattern expression to match against conjuncts in the VC 
     * antecedent and then a pattern antecedent to match against conjuncts in
     * the VC antecedent or global theorems, then iterates over all possible
     * bindings.</p>
     */
    private class QuirkyBindingIterator implements Iterator<Map<PExp, PExp>> {

        private final ImmutableList<PExp> myLocalTheorems;
        private final ChainingIterable<PExp> myFacts;

        private final IncrementalBindingIterator myFirstBinding;
        private Iterator<Map<PExp, PExp>> myOtherBindings;

        private final PExp[] myOtherPatterns;

        private Map<PExp, PExp> myNextReturn;

        public QuirkyBindingIterator(PExp firstPattern,
                PExp[] otherPatterns,
                ImmutableList<PExp> localTheorems) {

            myOtherPatterns = otherPatterns;
            myLocalTheorems = localTheorems;

            myFacts = new ChainingIterable<PExp>();
            myFacts.add(localTheorems);
            myFacts.add(myGlobalTheorems);

            myFirstBinding =
                    new IncrementalBindingIterator(firstPattern, localTheorems);

            myOtherBindings = DummyIterator.getInstance(myOtherBindings);

            setUpNext();
        }

        private void setUpNext() {

            Map<PExp, PExp> firstBindings;
            while (!myOtherBindings.hasNext() && myFirstBinding.hasNext()) {
                firstBindings = myFirstBinding.next();
                myOtherBindings =
                        new BindingsIterator(myFacts, myOtherPatterns, 
                                0, firstBindings);
            }

            if (myOtherBindings.hasNext()) {
                myNextReturn = myOtherBindings.next();
            }
            else {
                myNextReturn = null;
            }
        }

        @Override
        public boolean hasNext() {
            return myNextReturn != null;
        }

        @Override
        public Map<PExp, PExp> next() {
            Map<PExp, PExp> retval = myNextReturn;

            setUpNext();

            return retval;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
