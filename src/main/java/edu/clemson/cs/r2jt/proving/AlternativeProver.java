/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.clemson.cs.r2jt.proving;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.proving.absyn.PExp;

public class AlternativeProver implements VCProver {

    private final TransformationChooser myChooser;
    private final CompileEnvironment myInstanceEnvironment;

    public AlternativeProver(CompileEnvironment e, TransformationChooser chooser) {
        myChooser = chooser;
        myInstanceEnvironment = e;
    }

    @Override
    public BigInteger getProofCountOrder() {
        return BigInteger.valueOf(-1);
    }

    @Override
    public void prove(VerificationCondition originalVC,
            ProverListener progressListener, ActionCanceller actionCanceller,
            long timeoutAt)
            throws VCInconsistentException,
                VCProvedException,
                UnableToProveException {

        VC vc = convertToImmutableVC(originalVC);

        Metrics metrics = new Metrics();
        metrics.progressListener = progressListener;

        if (actionCanceller == null) {
            actionCanceller = new ActionCanceller();
        }

        metrics.actionCanceller = actionCanceller;

        myChooser.preoptimizeForVC(vc);

        try {
            continueProofFrom(vc, 0, metrics, new ProofData());
        }
        catch (VCProvedException e) {
            e.setOriginal(vc);
            throw e;
        }

        throw new UnableToProveException(metrics);
    }

    private void continueProofFrom(final VC vc, final int curLength,
            final Metrics metrics, final ProofData proofData)
            throws UnableToProveException,
                VCProvedException,
                VCInconsistentException {

        if (curLength > 100) {
            //Very deep recursion.  Probably an infinite loop.
            System.err.println("Warning: Very deep recursion.");
        }

        metrics.incrementProofsConsidered();

        if (vc.getConsequent().size() == 0) {
            throw new VCProvedException(metrics);
        }

        if (!metrics.actionCanceller.running) {
            //Someone from the outside world told us to stop proving.  We finish
            //without being able to prove.
            throw new UnableToProveException(metrics);
        }

        attemptStep(vc, curLength, metrics, proofData);
    }

    public void attemptStep(VC vc, int curLength, Metrics metrics,
            ProofData proofData)
            throws UnableToProveException,
                VCProvedException,
                VCInconsistentException {

        Iterator<ProofPathSuggestion> suggestions =
                myChooser.suggestTransformations(vc, curLength, metrics,
                        proofData);

        Iterator<VC> substitutions;
        ProofPathSuggestion suggestion;
        while (suggestions.hasNext()) {
            suggestion = suggestions.next();

            substitutions = suggestion.step.transform(vc);

            while (substitutions.hasNext()) {
                VC newVC = substitutions.next();

                if (myInstanceEnvironment.flags.isFlagSet(Prover.FLAG_VERBOSE)
                        && suggestion.debugNote != null) {

                    VC vcToPrint;

                    if (suggestion.debugPrevious) {
                        vcToPrint = vc;
                    }
                    else {
                        vcToPrint = newVC;
                    }

                    System.out.println(suggestion.debugNote + "\n\n"
                            + vcToPrint);
                }

                try {
                    continueProofFrom(newVC, curLength + 1, metrics,
                            suggestion.data.addStep(vc));
                }
                catch (VCProvedException e) {
                    e.addStep(new AlternativeProofStep(suggestion, newVC));
                    throw e;
                }
            }
        }
    }

    public static VC convertToImmutableVC(VerificationCondition vc) {

        List<PExp> newAntecedents = new LinkedList<PExp>();

        Conjuncts oldAntecedents = vc.getAntecedents();
        for (Exp a : oldAntecedents) {
            newAntecedents.add(PExp.buildPExp(a));
        }

        List<PExp> newConsequents = new LinkedList<PExp>();

        Conjuncts oldConsequents = vc.getConsequents();
        for (Exp c : oldConsequents) {
            newConsequents.add(PExp.buildPExp(c));
        }

        VC retval =
                new VC(vc.getName(), new Antecedent(newAntecedents),
                        new Consequent(newConsequents));

        return retval;
    }
}
