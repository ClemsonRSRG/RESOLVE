/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
