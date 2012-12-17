package edu.clemson.cs.r2jt.proving;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.TimeoutException;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.init.CompileEnvironment;

/**
 * <p>A <code>SingleStrategyProver</code> provides a facility through which to
 * prove VCs using a single unified set of parameters.</p>
 * 
 * @author H. Smith
 *
 */
class SingleStrategyProver implements VCProver {

    /**
     * <p>Whether or not the prover should backtrack if it finds that it has
     * applied a series of rules that took it in a circle.  True means it 
     * should.</p>
     */
    private final boolean OPTION_BACKTRACK_ON_CYCLE;

    /**
     * <p>The minimum proof length on which the prover should expend energy
     * checking to see if the proof demonstrates the VC.  Useful, for example,
     * if we've already checked all the proofs of length 3 and now want to
     * check the proofs of length 4 through 6.  We don't need to waste energy
     * checking the proofs of length &lt= 3 as we generate them on our way to
     * the longer proofs.</p>
     * 
     * <p><strong>INVARIANT:</strong> 
     * <code>0 &lt= MIN_PROOF_LENGTH &lt= MAX_PROOF_LENGTH</code></p>.
     */
    private final int MIN_PROOF_LENGTH;

    /**
     * <p>A value indicating the size of the proofspace of this strategy as
     * compared to any other.  Or -1 if no such estimation can be made.</p>
     */
    private BigInteger myProofCountOrder;

    /**
     * <p>A class in charge of choosing the next rule to apply.</p>
     */
    private final RuleProvider myRuleProvider;

    private final List<Implication> IMPLICATIONS;

    private final CompileEnvironment myInstanceEnvironment;

    public SingleStrategyProver(RuleProvider ruleChooser,
            boolean backtrackOnCycle, int minProofLength,
            List<Implication> implications, CompileEnvironment e) {

        OPTION_BACKTRACK_ON_CYCLE = backtrackOnCycle;
        MIN_PROOF_LENGTH = minProofLength;
        myRuleProvider = ruleChooser;
        myProofCountOrder =
                BigInteger.valueOf(ruleChooser.getApproximateRuleSetSize());

        IMPLICATIONS = implications;

        myInstanceEnvironment = e;
    }

    public void prove(final VerificationCondition vC,
            final ProverListener progressListener,
            ActionCanceller actionCanceller, long timeoutAt)
            throws VCInconsistentException,
                VCProvedException,
                UnableToProveException {

        Metrics metrics = new Metrics();
        metrics.progressListener = progressListener;

        try {
            propagateTheorems(vC, metrics, timeoutAt);
        }
        catch (TimeoutException e) {
            //We ran out of time.  We'll just continue on as though everything
            //is ok and continueProofFrom() will deal appropriately with the 
            //timeout.
        }

        if (myInstanceEnvironment.flags.isFlagSet(Prover.FLAG_VERBOSE)) {
            System.out.println(vC);
        }

        if (actionCanceller == null) {
            actionCanceller = new ActionCanceller();
        }

        metrics.actionCanceller = actionCanceller;

        continueProofFrom(vC, 0, metrics,
                new ArrayDeque<VerificationCondition>(10), timeoutAt);

        throw new UnableToProveException(metrics);
    }

    private void propagateTheorems(final VerificationCondition vC, Metrics m,
            long timeoutAt) throws UnableToProveException, TimeoutException {

        for (int j = 0; j < 5; j++) {
            for (Implication i : IMPLICATIONS) {
                if (System.currentTimeMillis() >= timeoutAt) {
                    throw new UnableToProveException(m);
                }

                vC
                        .setAntecedents(new Conjuncts(Utilities
                                .applyImplicationToAssumptions(vC
                                        .getAntecedents(), i.getAntecedent(), i
                                        .getConsequent(), timeoutAt)));
            }

            vC.propagateExpansionsInPlace();

            vC.simplify();
        }
    }

    public BigInteger getProofCountOrder() {
        return myProofCountOrder;
    }

    /**
     * <p>Attempts to prove a single VC using no more than <code>maxDepth</code>
     * steps, and assuming a proof-in-progress that has taken the proof through
     * each of the states in <code>pastStates</code>, where the top of that 
     * stack is the current state and the bottom is the original state.  If the 
     * VC can be proved, throws a <code>VCProvedException</code>.  If the VC 
     * cannot be proved in the provided depth, simply returns.  If the VC is 
     * skipped by a call to <code>skipVC()</code>, returns 
     * <code>UnableToProveException</code>.  If the VC is discovered to be 
     * provably incorrect, throws a <code>VCInconsistentException</code>.
     * 
     * @param vC The verification condition to be proved.  May not be 
     *           <code>null</code>.
     * @param theorems A list of theorems that may be applied as part of the
     *                 proof.  May not be <code>null</code>.
     * @param maxDepth The maximum number of steps the prover should attempt 
     *                 before giving up on a proof.
     * @param metrics A reference to the metrics the prover should keep on the
     *                proof in progress.  May not be <code>null</code>.
     * @param pastStates A list of proof states that have already been visited
     *                   along the path that is the current proof-in-progress.
     *                   May not be <code>null</code>.
     * @param topLevel If <code>true</code>, indicates that this is the first
     *                 proof step on the way to proving the VC.  Otherwise, this
     *                 is a recursive call attempting to recursively solve some
     *                 subset of the proof.
     *            
     * @throws UnableToProveException If the VC is skipped by a call to
     *                                <code>skipVC()</code>.
     * @throws VCInconsistentException If the VC can be proved inconsistent.
     * @throws VCProvedException If the VC is proved.
     * @throws NullPointerException If <code>vC</code>, <code>theorems</code>,
     *                              <code>metrics</code>, or 
     *                              <code>pastStates</code> is 
     *                              <code>null</code>.
     */
    private void continueProofFrom(final VerificationCondition vC,
            final int curLength, final Metrics metrics,
            final Deque<VerificationCondition> pastStates, long timeoutAt)
            throws UnableToProveException,
                VCProvedException,
                VCInconsistentException {

        if (System.currentTimeMillis() >= timeoutAt) {
            throw new UnableToProveException(metrics);
        }

        metrics.incrementProofsConsidered();

        if (curLength >= MIN_PROOF_LENGTH) {
            vC.simplify();

            if (vC.getNumConsequents() == 0) {
                throw new VCProvedException(metrics);
            }
        }

        if (!metrics.actionCanceller.running) {
            //Someone from the outside world told us to stop proving.  We finish
            //without being able to prove.
            throw new UnableToProveException(metrics);
        }

        if (OPTION_BACKTRACK_ON_CYCLE && isRepeatState(vC, pastStates)) {
            //We've decided not to explore the tree from here down, so update
            //the count on the number of times we've usefully backtracked
            metrics.numTimesBacktracked =
                    metrics.numTimesBacktracked.add(BigInteger.ONE);
        }
        else {
            pastStates.push(vC);
            attemptStep(vC, curLength, metrics, pastStates, timeoutAt);
            pastStates.pop();
        }
    }

    private void attemptStep(VerificationCondition vC, int curLength,
            Metrics metrics, Deque<VerificationCondition> pastStates,
            long timeoutAt)
            throws UnableToProveException,
                VCProvedException,
                VCInconsistentException {

        KnownSizeIterator<MatchReplace> rules =
                myRuleProvider.consider(vC, curLength, metrics, pastStates);

        if (curLength == 0) {
            metrics.ruleCount = rules.size();

            if (metrics.ruleCount < 0) {
                metrics.progressListener = null;
            }
        }

        MatchReplace curRule;
        while (rules.hasNext()) {
            curRule = rules.next();
            applyReplaceStep(curRule, vC, curLength, metrics, pastStates,
                    timeoutAt);
            incrementProgress(curLength, metrics);
        }
    }

    /**
     * <p>Returns <code>true</code> <strong>iff</strong> <code>vC</code> is
     * equal to some past state listed in <code>pastStates</code>.</p>
     * 
     * @param vC The VC to test if it is a repeat state.  May not be 
     *           <code>null</code>.
     * @param pastStates The list of previous states.  May not be 
     *        <code>null</code>.
     * @return <code>true</code> <strong>iff</strong> <code>vC</code> is a
     *         repeat state.
     */
    private static boolean isRepeatState(final VerificationCondition vC,
            final Deque<VerificationCondition> pastStates) {

        if (true) {
            throw new UnsupportedOperationException("This code seems to be "
                    + "broken (or more likely there are serious issues with "
                    + "Exp.equivalent() throughout its concrete subclasses.)  "
                    + "Until it is fixed, do not use the backtrack-on-cycle "
                    + "option of the old prover.");
        }

        boolean retval = false;

        Iterator<VerificationCondition> pastStatesIter = pastStates.iterator();

        while (!retval && pastStatesIter.hasNext()) {
            retval = pastStatesIter.next().equals(vC);
        }

        return retval;
    }

    /**
     * <p>Attempts to prove a single VC using no more than <code>maxDepth</code>
     * steps and applying a replacement described in <code>matcher</code> as the
     * first step of the proof, and assuming a proof-in-progress that has taken 
     * the proof through each of the states in <code>pastStates</code>, where 
     * the top of that stack is the current state and the bottom is the original
     * state.  If the VC can be proved, throws a <code>VCProvedException</code>.
     * If the VC cannot be proved in the provided depth, simply returns.  If the 
     * VC is skipped by a call to <code>skipVC()</code>, returns 
     * <code>UnableToProveException</code>.  If the VC is discovered to be 
     * provably incorrect, throws a <code>VCInconsistentException</code>.
     * 
     * @param matcher A <code>MatchReplace</code> defining the specific kind of
     *                transformation that should be performed on the current
     *                proof state as th first step of the requested proof.  May
     *                not be <code>null</code>.
     * @param vC The verification condition to be proved.  May not be 
     *           <code>null</code>.
     * @param theorems A list of theorems that may be applied as part of the
     *                 proof.  May not be <code>null</code>.
     * @param maxDepth The maximum number of steps the prover should attempt 
     *                 before giving up on a proof.
     * @param metrics A reference to the metrics the prover should keep on the
     *                proof in progress.  May not be <code>null</code>.
     * @param pastStates A list of proof states that have already been visited
     *                   along the path that is the current proof-in-progress.
     *                   May not be <code>null</code>.
     * @param topLevel If <code>true</code>, indicates that this is the first
     *                 proof step on the way to proving the VC.  Otherwise, this
     *                 is a recursive call attempting to recursively solve some
     *                 subset of the proof.
     *            
     * @throws UnableToProveException If the VC is skipped by a call to
     *                                <code>skipVC()</code>.
     * @throws VCInconsistentException If the VC can be proved inconsistent.
     * @throws VCProvedException If the VC is proved.
     * @throws NullPointerException If <code>vC</code>, <code>theorems</code>,
     *                              <code>metrics</code>, 
     *                              <code>pastStates</code>, or
     *                              <code>matcher</code> is 
     *                              <code>null</code>.
     */
    private void applyReplaceStep(final MatchReplace matcher,
            final VerificationCondition vC, final int curLength,
            Metrics metrics, final Deque<VerificationCondition> pastStates,
            long timeoutAt)
            throws UnableToProveException,
                VCProvedException,
                VCInconsistentException {

        MatchApplicator replacement =
                new MatchApplicator(vC.getConsequents(), matcher);

        int curLengthPlusOne = curLength + 1;
        List<Exp> newConsequents = replacement.getNextApplication();
        while (newConsequents != null) {
            newConsequents = Utilities.splitIntoConjuncts(newConsequents);
            try {
                VerificationCondition newVC =
                        new VerificationCondition(vC.getAntecedents(),
                                newConsequents);

                continueProofFrom(newVC, curLengthPlusOne, metrics, pastStates,
                        timeoutAt);
            }
            catch (VCProvedException e) {
                e.addStep(new ProofStep(matcher.toString(), newConsequents));
                throw e;
            }
            newConsequents = replacement.getNextApplication();
        }
    }

    /**
     * <p>Updates <code>myProgressWindow</code> appropriately, based on whether
     * or not we have made progress at the top level and whether or the progress
     * window option is currently active.</p>
     * 
     * @param topLevel True <strong>iff</strong> the progress was made at the
     *                 top level.
     */
    private void incrementProgress(final int curLength, final Metrics metrics) {
        if (curLength == 0) {
            metrics.rulesTried++;

            if (metrics.progressListener != null) {
                double progress =
                        ((double) metrics.rulesTried) / metrics.ruleCount;
                metrics.progressListener.progressUpdate(progress);
            }
        }
    }
}
