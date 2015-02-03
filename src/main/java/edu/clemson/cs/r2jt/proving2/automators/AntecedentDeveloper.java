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
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
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
package edu.clemson.cs.r2jt.proving2.automators;

import edu.clemson.cs.r2jt.proving2.model.Theorem;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.transformations.ExpandAntecedentByImplication;
import edu.clemson.cs.r2jt.proving2.transformations.ExpandAntecedentBySubstitution;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import edu.clemson.cs.r2jt.proving2.utilities.AddsSomethingNewPredicate;
import edu.clemson.cs.r2jt.utilities.FlagManager;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class AntecedentDeveloper implements Automator {

    private final Iterable<Transformation> myTheoremTransformations;
    private final AddsSomethingNewPredicate myDevelopmentPredicate;
    private int myRemainingRounds;

    public AntecedentDeveloper(PerVCProverModel model,
            Set<String> variableSymbols,
            Iterable<Transformation> transformations, int totalRounds) {
        myDevelopmentPredicate =
                new AddsSomethingNewPredicate(model, variableSymbols);
        myRemainingRounds = totalRounds;

        List<Transformation> finalTransformations =
                new LinkedList<Transformation>();
        for (Transformation transformation : transformations) {
            if (transformation instanceof ExpandAntecedentByImplication
                    || transformation instanceof ExpandAntecedentBySubstitution) {

                if (!transformation.introducesQuantifiedVariables()) {
                    finalTransformations.add(transformation);
                }
            }
        }

        myTheoremTransformations = finalTransformations;
    }

    @Override
    public void step(Deque<Automator> stack, PerVCProverModel model) {
        if (myRemainingRounds > 0) {
            if (!FlagManager.getInstance().isFlagSet("nodebug")) {
                System.out.println("AntecedentDeveloper got the heartbeat.");
            }
            stack.push(new AntecedentDeveloperRound(model));
        }
        else {
            stack.pop();
        }

        myRemainingRounds--;
    }

    /**
     * <p>Retrieves all applications of all theorems to the current antecedent
     * and applies all of them.</p>
     */
    private class AntecedentDeveloperRound implements Automator {

        public static final int MAX_DEVELOPMENTS = 200;
        private int myDevelopmentCount;
        private Iterator<Application> myApplications;
        private ProbationaryApplication myProbationaryApplication;

        public AntecedentDeveloperRound(PerVCProverModel model) {
            List<Application> applications = new LinkedList<Application>();
            //We need to iterate over all possible applications, but each
            //application will add a new antecedent to the model, which we 
            //don't want to consider in this pass.  Since no application is 
            //going to change or remove a current antecedent, and we know 
            //that we're going to apply them all, we slurp them all up now 
            //to be applied later one at a time.
            Iterator<Application> tApplications;

            // YS: For some reason Hampton decided that it would be a problem
            // a VC does not contain any givens (antecedents), so he throws a
            // runtime exception if the local theorem list is empty. But for
            // cases like "S = S" or "true", we shouldn't need any givens to
            // prove it. (Probably just need Boolean_Theory)
            for (Theorem t : model.getLocalTheoremList()) {
                for (Transformation transformation : t.getTransformations()) {
                    if (transformation instanceof ExpandAntecedentByImplication
                            || transformation instanceof ExpandAntecedentBySubstitution) {

                        if (!transformation.introducesQuantifiedVariables()) {
                            tApplications =
                                    transformation.getApplications(model);
                            while (tApplications.hasNext()) {
                                applications.add(tApplications.next());
                            }
                        }
                    }
                }
            }

            for (Transformation t : myTheoremTransformations) {
                tApplications = t.getApplications(model);
                while (tApplications.hasNext()) {
                    applications.add(tApplications.next());
                }
            }

            myApplications = applications.iterator();
        }

        @Override
        public void step(Deque<Automator> stack, PerVCProverModel model) {
            if (myDevelopmentCount < MAX_DEVELOPMENTS
                    && myApplications.hasNext()) {

                if (myProbationaryApplication != null
                        && myProbationaryApplication.changeStuck()) {
                    myDevelopmentCount++;
                }
                if (!FlagManager.getInstance().isFlagSet("nodebug")) {
                    System.out
                            .println("AntecedentDeveloperRound - adding development - "
                                    + myDevelopmentCount);
                }

                myProbationaryApplication =
                        new ProbationaryApplication(myApplications.next(),
                                myDevelopmentPredicate);
                stack.push(myProbationaryApplication);
            }
            else {
                stack.pop();
            }
        }
    }
}
