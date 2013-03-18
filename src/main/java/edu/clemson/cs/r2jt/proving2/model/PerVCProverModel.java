/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.model;

import edu.clemson.cs.r2jt.proving.ChainingIterator;
import edu.clemson.cs.r2jt.proving.DummyIterator;
import edu.clemson.cs.r2jt.proving.LazyMappingIterator;
import edu.clemson.cs.r2jt.proving.absyn.BindingException;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.immutableadts.EmptyImmutableList;
import edu.clemson.cs.r2jt.proving.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.proving2.AutomatedProver;
import edu.clemson.cs.r2jt.proving2.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.Theorem;
import edu.clemson.cs.r2jt.proving2.VC;
import edu.clemson.cs.r2jt.proving2.justifications.Given;
import edu.clemson.cs.r2jt.proving2.justifications.Justification;
import edu.clemson.cs.r2jt.proving2.proofsteps.ProofStep;
import edu.clemson.cs.r2jt.proving2.utilities.InductiveSiteIteratorIterator;
import edu.clemson.cs.r2jt.proving2.utilities.UnsafeIteratorLinkedList;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author hamptos
 */
public final class PerVCProverModel {

    public static enum ChangeEventMode {
        ALWAYS {

            @Override
            public boolean report(boolean important) {
                return true;
            }
        },
        INTERMITTENT {

            private int eventCount;

            @Override
            public boolean report(boolean important) {
                eventCount++;

                return important || (eventCount % 20 == 0);
            }
        };

        public abstract boolean report(boolean important);
    };

    private final TypeGraph myTypeGraph;

    /**
     * <p>A friendly name of what we're trying to prove.  Should go well with
     * "Proving XXX" and "Proof for XXX".</p>
     */
    private final String myTheoremName;

    /**
     * <p>A hashmap of local theorems for quick searching.  Its keyset is always
     * the same as the set of <code>PExp</code>s embedded in the 
     * <code>LocalTheorem</code>s of <code>myLocalTheoremsList</code>.  Note
     * that this means no <code>PExp</code> in this set will have a top-level
     * "and".</p>
     * 
     * <p>Each entry in the map maps to an integer count of the number of local
     * theorems that embed that PExp, making this a representation of a 
     * multiset.  As an invariant, no entry will map to 0 or less.</p>
     */
    private final Map<PExp, Integer> myLocalTheoremsSet =
            new HashMap<PExp, Integer>();
    private final Set<PExp> myLocalTheoremSetForReturning;

    /**
     * <p>A list of local theorems in the order they were introduced, for 
     * friendly displaying and tagged with useful information.  The set of
     * <code>PExp</code>s embedded in this list's elements will always be the
     * same as those <code>PExp</code>s in <code>myLocalTheoremSet</code>.</p>
     * 
     * <p>Each <code>LocalTheorem</code> in the list is guaranteed to be a 
     * unique object.</p>
     */
    private final List<LocalTheorem> myLocalTheoremsList =
            new UnsafeIteratorLinkedList<LocalTheorem>();

    /**
     * <p>A list of expressions remaining to be established as true.  Each of 
     * these is guaranteed not to have a top-level "and" expression (otherwise 
     * the conjuncts would have been broken up into separate entries in this 
     * list.) Once we empty this list, the proof is complete.</p>
     */
    private final List<PExp> myConsequents =
            new UnsafeIteratorLinkedList<PExp>();

    private int myLocalTheoremsHash;
    private int myConsequentsHash;

    /**
     * <p>A list of the current proof under consideration.  Starting with a 
     * fresh <code>PerVCProverModel</code> initialized with the consequents, 
     * antecedents, and global theorems originally provided to this class, then
     * applying these steps in order, would bring the fresh model into the exact
     * same state that this one is currently in.</p>
     */
    private final List<ProofStep> myProofSoFar = new LinkedList<ProofStep>();

    /**
     * <p>A link to the global theorem library.</p>
     */
    private final ImmutableList<Theorem> myTheoremLibrary;

    /**
     * <p>A list of listeners to be contacted when the model changes.  Note that
     * the behavior of change listening is modified by 
     * <code>myChangeEventMode</code>.</p>
     */
    private List<ChangeListener> myChangeListeners =
            new LinkedList<ChangeListener>();

    /**
     * <p>In order to not slow down the proving process by synchronizing this
     * whole class, we let an automated prover have special status to the model.
     * Whenever the model would like to alert its change-listeners, it first
     * checks to see if it has an associate automated prover.  If it does not,
     * or if the automated prover is not running, it alerts its listeners
     * normally.  If there is an associated automated prover and it's running,
     * the model simply alerts the automated prover that the model would like
     * to alert its listeners, at which case the ball is in the automated 
     * prover's court to clean up, pause any further modifications to the model,
     * then alert the model that it's ready.  At that point, the model sends out
     * its change alerts, then alerts the automated prover that it is done, 
     * whereupon the automated prover continues its work.</p>
     */
    private AutomatedProver myAutomatedProver;

    private ChangeEventMode myChangeEventMode = ChangeEventMode.INTERMITTENT;

    public PerVCProverModel(TypeGraph g, String proofFor,
            List<PExp> antecedents, List<PExp> consequents,
            ImmutableList<Theorem> theoremLibrary) {

        myTheoremName = proofFor;

        for (PExp assumption : antecedents) {
            addLocalTheorem(assumption, new Given(), false);
        }

        for (PExp consequent : consequents) {
            myConsequentsHash += consequents.hashCode();
            myConsequents.add(consequent);
        }

        myLocalTheoremSetForReturning = myLocalTheoremsSet.keySet();

        myTheoremLibrary = theoremLibrary;
        myTypeGraph = g;
    }

    public PerVCProverModel(TypeGraph g, String proofFor, VC vc,
            ImmutableList<Theorem> theoremLibrary) {
        this(g, proofFor, listFromIterable(vc.getAntecedent()),
                listFromIterable(vc.getConsequent()), theoremLibrary);
    }

    public String getTheoremName() {
        return myTheoremName;
    }

    public void setChangeEventMode(ChangeEventMode m) {
        myChangeEventMode = m;
    }

    public void addChangeListener(ChangeListener l) {
        myChangeListeners.add(l);
    }

    /**
     * <p>Sets the automated prover that is working on this model.  The prover
     * will be alerted before change events go out so that it can stop modifying
     * the model.</p>
     * 
     * @param l 
     */
    public void setAutomatedProver(AutomatedProver p) {
        myAutomatedProver = p;
    }

    public void triggerUIUpdates() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                System.out
                        .println("PerVCProverModel - Alerting Change Listeners");
                alertChangeListeners();
                System.out
                        .println("PerVCProverModel - Done Alerting Change Listeners");
            }
        });
    }

    public void removeChangeListener(ChangeListener l) {
        myChangeListeners.remove(l);
    }

    public PExp getTrue() {
        return PExp.trueExp(myTypeGraph);
    }

    /**
     * <p>Must be called on the event dispatching thread.</p>
     */
    private void alertChangeListeners() {
        ChangeEvent e = new ChangeEvent(PerVCProverModel.this);
        for (ChangeListener l : myChangeListeners) {
            l.stateChanged(e);
        }

        if (myAutomatedProver != null) {
            myAutomatedProver.uiUpdateFinished();
        }
    }

    public void touch() {
        modelChanged(true);
    }

    private void modelChanged(boolean important) {
        if (myChangeEventMode.report(important)) {

            if (myAutomatedProver == null || !myAutomatedProver.isRunning()) {
                Runnable alertListeners = new Runnable() {

                    @Override
                    public void run() {
                        alertChangeListeners();
                    }
                };

                if (SwingUtilities.isEventDispatchThread()) {
                    alertListeners.run();
                }
                else {
                    SwingUtilities.invokeLater(alertListeners);
                }
            }
            else {
                myAutomatedProver.prepForUIUpdate();
            }
        }
    }

    public List<ProofStep> getProofSteps() {
        return myProofSoFar;
    }

    public ProofStep getLastProofStep() {
        return myProofSoFar.get(myProofSoFar.size() - 1);
    }

    public void undoLastProofStep() {
        myProofSoFar.get(myProofSoFar.size() - 1).undo(this);
        myProofSoFar.remove(myProofSoFar.size() - 1);
    }

    public ImmutableList<Theorem> getTheoremLibrary() {
        return myTheoremLibrary;
    }

    public PExp getConsequent(int index) {
        return myConsequents.get(index);
    }

    public void setConsequent(int index, PExp newConsequent) {

        myConsequentsHash -= myConsequents.get(index).hashCode();
        myConsequentsHash += newConsequent.hashCode();

        myConsequents.set(index, newConsequent);
        modelChanged(false);
    }

    public boolean noConsequents() {
        return myConsequents.isEmpty();
    }

    public LocalTheorem getLocalTheorem(int index) {
        return myLocalTheoremsList.get(index);
    }

    public List<LocalTheorem> getLocalTheoremList() {
        return myLocalTheoremsList;
    }

    public List<PExp> getConsequentList() {
        return myConsequents;
    }

    public Set<PExp> getLocalTheoremSet() {
        return myLocalTheoremSetForReturning;
    }

    private static List<PExp> listFromIterable(Iterable<PExp> i) {
        List<PExp> result = new LinkedList<PExp>();
        for (PExp e : i) {
            result.add(e);
        }

        return result;
    }

    public boolean containsLocalTheorem(PExp t) {
        return myLocalTheoremsSet.keySet().contains(t);
    }

    public void addConsequent(PExp c) {
        myConsequents.add(c);
        myConsequentsHash += c.hashCode();

        //This is an important change if it took us away from a proved state
        modelChanged(myConsequents.size() == 1);
    }
    
    public void addConsequent(PExp c, int index) {
        myConsequents.add(index, c);
        myConsequentsHash += c.hashCode();

        //This is an important change if it took us away from a proved state
        modelChanged(myConsequents.size() == 1);
    }

    public PExp removeConsequent(int index) {
        PExp result = myConsequents.remove(index);
        myConsequentsHash -= result.hashCode();

        //This change is important if it eleminates the last conjunct--because
        //then we've proved it!
        modelChanged(myConsequents.isEmpty());

        return result;
    }

    /**
     * <p>Alters the value at the given site, assuming it indicates either an
     * antecedent or consequent (indicating a global theorem will trigger an
     * <code>IllegalArgumentException</code>.  Returns the original value of the
     * root site.</p>
     * 
     * @param s
     * @param newValue
     * @return 
     */
    public PExp alterSite(Site s, PExp newValue) {
        PExp result;

        if (s.getModel() != this) {
            throw new IllegalArgumentException(
                    "Site does not belong to this model.");
        }

        if (newValue == null) {
            throw new IllegalArgumentException(
                    "Can't change value to a null PExp.");
        }

        switch (s.section) {
        case ANTECEDENTS:
            LocalTheorem t = myLocalTheoremsList.get(s.index);
            result = t.getAssertion();
            removeLocalTheorem(t);

            addLocalTheorem(t.getAssertion().withSiteAltered(s.pathIterator(),
                    newValue), t.getJustification(), t.amTryingToProveThis(),
                    s.index);
            break;
        case CONSEQUENTS:
            result = myConsequents.get(s.index);
            myConsequentsHash -= result.hashCode();

            PExp altered = result.withSiteAltered(s.pathIterator(), newValue);

            myConsequentsHash += altered.hashCode();

            myConsequents.set(s.index, altered);
            break;
        case THEOREM_LIBRARY:
            throw new IllegalArgumentException("Can't modify a global theorem.");
        default:
            throw new RuntimeException("No way to get here.");
        }

        modelChanged(false);

        return result;
    }

    /**
     * <p>Adds a theorem to the list of local theorems (i.e., the antecedent of
     * the implication represented by the current proof state) with the given
     * justification.  Setting <code>tryingToProveThis</code> to 
     * <code>true</code> simply indicates to the prover that the newly 
     * introduced theorem was original a conjunct of the consequent, which helps
     * prune unproductive proof steps when outputting a final proof (e.g., 
     * theorems that were not trying to be proved, or used along the way toward
     * proving one that was are irrelevant and can be omitted.)</p>
     * 
     * <p>The returned {@link LocalTheorem LocalTheorem} </p>
     * @param assertion
     * @param j
     * @param tryingToProveThis
     * @return 
     */
    public LocalTheorem addLocalTheorem(PExp assertion, Justification j,
            boolean tryingToProveThis, int index) {

        myLocalTheoremsHash += assertion.hashCode();

        LocalTheorem theorem =
                new LocalTheorem(assertion, j, tryingToProveThis);

        myLocalTheoremsList.add(index, theorem);

        Integer count = myLocalTheoremsSet.get(assertion);

        if (count == null) {
            count = 0;
        }

        myLocalTheoremsSet.put(assertion, count + 1);

        modelChanged(false);

        return theorem;
    }

    public LocalTheorem addLocalTheorem(PExp assertion, Justification j,
            boolean tryingToProveThis) {
        return addLocalTheorem(assertion, j, tryingToProveThis,
                myLocalTheoremsList.size());
    }

    public LocalTheorem removeLocalTheorem(int index) {
        LocalTheorem t = getLocalTheorem(index);

        myLocalTheoremsHash -= t.hashCode();

        removeLocalTheorem(t);

        return t;
    }

    public void removeLocalTheorem(LocalTheorem t) {
        PExp tAssertion = t.getAssertion();

        boolean removed = myLocalTheoremsList.remove(t);
        if (removed) {
            myLocalTheoremsHash -= t.getAssertion().hashCode();
        }
        else {
            throw new RuntimeException("No such theorem.");
        }

        Integer count = myLocalTheoremsSet.get(tAssertion);

        if (count > 1) {
            myLocalTheoremsSet.put(tAssertion, count - 1);
        }
        else {
            myLocalTheoremsSet.remove(tAssertion);
        }

        modelChanged(false);
    }

    public LocalTheorem getLocalTheoremAncestor(Site s)
            throws NoSuchElementException {

        if (s.getModel() != this) {
            throw new IllegalArgumentException(
                    "Site does not belong to this model.");
        }

        if (!s.section.equals(Site.Section.ANTECEDENTS)) {
            throw new NoSuchElementException();
        }

        return myLocalTheoremsList.get(s.index);
    }

    public void addProofStep(ProofStep s) {
        myProofSoFar.add(s);

        modelChanged(false);
    }

    public void processStringRepresentation(ProverModelVisitor visitor,
            Appendable a) {

        try {
            boolean first = true;

            int i = 0;
            visitor.setSection(Site.Section.ANTECEDENTS);
            for (LocalTheorem t : myLocalTheoremsList) {
                visitor.setIndex(i);
                i++;

                if (first) {
                    first = false;
                }
                else {
                    a.append(" and\n");
                }

                t.getAssertion().processStringRepresentation(visitor, a);
            }

            a.append("\n  -->\n");

            i = 0;
            first = true;
            visitor.setSection(Site.Section.CONSEQUENTS);
            for (PExp c : myConsequents) {
                visitor.setIndex(i);
                i++;

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

    public Iterator<Site> topLevelAntecedentSiteIterator() {
        return new IndexIncrementingSiteIterator(
                new LazyMappingIterator<LocalTheorem, PExp>(myLocalTheoremsList
                        .iterator(), LocalTheorem.UNWRAPPER),
                Site.Section.ANTECEDENTS, 0);
    }

    public Iterator<Site> topLevelConsequentSiteIterator() {
        return new IndexIncrementingSiteIterator(myConsequents.iterator(),
                Site.Section.CONSEQUENTS, 0);
    }

    public Iterator<Site> topLevelAntecedentAndConsequentSiteIterator() {
        return new ChainingIterator<Site>(topLevelAntecedentSiteIterator(),
                topLevelConsequentSiteIterator());
    }

    public Iterator<Site> topLevelGlobalTheoremsIterator() {
        return new IndexIncrementingSiteIterator(
                new LazyMappingIterator<Theorem, PExp>(myTheoremLibrary
                        .iterator(), Theorem.UNWRAPPER),
                Site.Section.THEOREM_LIBRARY, 0);
    }

    public Iterator<Site> topLevelAntecedentAndGlobalTheoremSiteIterator() {
        return new ChainingIterator<Site>(topLevelAntecedentSiteIterator(),
                topLevelGlobalTheoremsIterator());
    }

    public Iterator<BindResult> bind(Set<Binder> binders) {
        return new BinderSatisfyingIterator(binders, new HashMap<PExp, PExp>(),
                new LinkedList<Site>());
    }

    public int implicationHashCode() {
        return myLocalTheoremsHash + (51 * myConsequentsHash);
    }

    private class BinderSatisfyingIterator implements Iterator<BindResult> {

        private final Binder myFirstBinder;
        private final Iterator<Site> myFirstBinderSites;

        private Site myCurFirstSite;
        private Map<PExp, PExp> myCurFirstSiteBindings;

        private final Set<Binder> myOtherBinders = new HashSet<Binder>();
        private Iterator<BindResult> myOtherBindings;

        private BindResult myNextReturn;

        private final Map<PExp, PExp> myAssumedBindings;
        private final List<Site> myBoundSiteSoFar;

        public BinderSatisfyingIterator(Set<Binder> binders,
                Map<PExp, PExp> assumedBindings, List<Site> boundSitesSoFar) {
            myAssumedBindings = assumedBindings;
            myBoundSiteSoFar = boundSitesSoFar;

            if (!binders.isEmpty()) {
                myFirstBinder = binders.iterator().next();
                myFirstBinderSites =
                        myFirstBinder.getInterestingSiteVisitor(
                                PerVCProverModel.this, Collections.EMPTY_LIST);
                myOtherBinders.addAll(binders);
                myOtherBinders.remove(myFirstBinder);
                myOtherBindings = DummyIterator.getInstance(myOtherBindings);

                setUpNext();
            }
            else {
                myFirstBinder = null;
                myFirstBinderSites = null;
                myNextReturn =
                        new BindResult(new HashMap<Binder, Site>(),
                                new HashMap<PExp, PExp>());
            }
        }

        @Override
        public boolean hasNext() {
            return (myNextReturn != null);
        }

        @Override
        public BindResult next() {
            if (myNextReturn == null) {
                throw new NoSuchElementException();
            }

            BindResult result = myNextReturn;
            setUpNext();

            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void setUpNext() {
            if (myFirstBinder == null) {
                myNextReturn = null;
            }
            else {
                while (myFirstBinderSites.hasNext()
                        && !myOtherBindings.hasNext()) {

                    myCurFirstSite = myFirstBinderSites.next();

                    try {
                        myCurFirstSiteBindings =
                                myFirstBinder.considerSite(myCurFirstSite,
                                        myAssumedBindings);

                        if (!myCurFirstSite.exp.getQuantifiedVariables()
                                .isEmpty()) {
                            throw new BindingException();
                        }

                        Map<PExp, PExp> inductiveBindings =
                                new HashMap<PExp, PExp>(myAssumedBindings);
                        inductiveBindings.putAll(myCurFirstSiteBindings);

                        List<Site> inductiveSites =
                                new LinkedList<Site>(myBoundSiteSoFar);
                        inductiveSites.add(myCurFirstSite);

                        myOtherBindings =
                                new BinderSatisfyingIterator(myOtherBinders,
                                        inductiveBindings, inductiveSites);
                    }
                    catch (BindingException be) {
                        //Can't bind the current site.  No worries--just keep
                        //searching.
                    }
                }

                //Either !myFirstBinderSites.hasNext(), or 
                //myOtherBindings.hasNext(), or both
                if (myOtherBindings.hasNext()) {
                    myNextReturn = myOtherBindings.next();
                    myNextReturn.bindSites.put(myFirstBinder, myCurFirstSite);
                    myNextReturn.freeVariableBindings
                            .putAll(myCurFirstSiteBindings);
                }
                else {
                    myNextReturn = null;
                }
            }
        }
    }

    public static class BindResult {

        public Map<Binder, Site> bindSites;
        public Map<PExp, PExp> freeVariableBindings;

        public BindResult(Map<Binder, Site> bindSites,
                Map<PExp, PExp> freeVariableBindings) {
            this.bindSites = bindSites;
            this.freeVariableBindings = freeVariableBindings;
        }
    }

    public static interface Binder {

        /**
         * <p>Returns an iterator over binding sites that should be considered,
         * in the order they should be considered, based on any other sites that
         * have already been bound.</p>
         * 
         * @param boundSitesSoFar Sites that have been bound by previously bound
         *              <code>Binder</code>s
         * .
         * @return An iterator over interesting sites.
         */
        public Iterator<Site> getInterestingSiteVisitor(PerVCProverModel m,
                List<Site> boundSitesSoFar);

        /**
         * <p>Attempts to bind to the given site, which was returned from an
         * iterator returned by 
         * {@link getInterestingSiteVisitor() getInterestingSiteVisitor()}.
         * Before applying any pattern, the binder must take into account the
         * <code>assumedBindings</code> which indicate bindings determined by
         * previously applied binders and may "fill in" certain free variables.
         * </p>
         * 
         * @param s A non-null site under consideration.
         * @param assumedBindings The mapping that's been proposed by 
         *              previously-bound <code>Binder</code>s.
         * 
         * @return A mapping of any newly-bound free variables.
         * 
         * @throws BindingException If the site is rejected.
         */
        public Map<PExp, PExp> considerSite(Site s,
                Map<PExp, PExp> assumedBindings) throws BindingException;
    }

    public static class TopLevelAntecedentBinder extends AbstractBinder {

        public TopLevelAntecedentBinder(PExp pattern) {
            super(pattern);
        }

        @Override
        public Iterator<Site> getInterestingSiteVisitor(PerVCProverModel m,
                List<Site> boundSitesSoFar) {
            return m.topLevelAntecedentSiteIterator();
        }
    }

    public static class TopLevelConsequentBinder extends AbstractBinder {

        public TopLevelConsequentBinder(PExp pattern) {
            super(pattern);
        }

        @Override
        public Iterator<Site> getInterestingSiteVisitor(PerVCProverModel m,
                List<Site> boundSitesSoFar) {
            return m.topLevelConsequentSiteIterator();
        }
    }

    public static class InductiveAntecedentBinder extends AbstractBinder {

        public InductiveAntecedentBinder(PExp pattern) {
            super(pattern);
        }

        @Override
        public Iterator<Site> getInterestingSiteVisitor(PerVCProverModel m,
                List<Site> boundSitesSoFar) {
            return new InductiveSiteIteratorIterator(m
                    .topLevelAntecedentSiteIterator());
        }
    }

    public static class InductiveConsequentBinder extends AbstractBinder {

        public InductiveConsequentBinder(PExp pattern) {
            super(pattern);
        }

        @Override
        public Iterator<Site> getInterestingSiteVisitor(PerVCProverModel m,
                List<Site> boundSitesSoFar) {
            return new InductiveSiteIteratorIterator(m
                    .topLevelConsequentSiteIterator());
        }
    }

    public static class TopLevelAntecedentAndConsequentBinder
            extends
                AbstractBinder {

        public TopLevelAntecedentAndConsequentBinder(PExp pattern) {
            super(pattern);
        }

        @Override
        public Iterator<Site> getInterestingSiteVisitor(PerVCProverModel m,
                List<Site> boundSitesSoFar) {
            return m.topLevelAntecedentAndConsequentSiteIterator();
        }
    }

    public static abstract class AbstractBinder implements Binder {

        private PExp myPattern;

        public AbstractBinder(PExp pattern) {
            myPattern = pattern;
        }

        @Override
        public Map<PExp, PExp> considerSite(Site s,
                Map<PExp, PExp> assumedBindings) throws BindingException {
            return myPattern.substitute(assumedBindings).bindTo(s.exp);
        }
    }

    private class IndexIncrementingSiteIterator implements Iterator<Site> {

        private final Site.Section mySection;
        private final ImmutableList<Integer> myPath =
                new EmptyImmutableList<Integer>();
        private int myCurIndex;

        private final Iterator<PExp> myExpressions;

        public IndexIncrementingSiteIterator(Iterator<PExp> expressions,
                Site.Section section, int startIndex) {

            myExpressions = expressions;
            mySection = section;
            myCurIndex = startIndex;
        }

        @Override
        public boolean hasNext() {
            return myExpressions.hasNext();
        }

        @Override
        public Site next() {
            Site result =
                    new Site(PerVCProverModel.this, mySection, myCurIndex,
                            myPath, myExpressions.next());

            myCurIndex++;

            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
