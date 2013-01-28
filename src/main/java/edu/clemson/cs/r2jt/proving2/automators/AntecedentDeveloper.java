/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.automators;

import edu.clemson.cs.r2jt.proving.ChainingIterator;
import edu.clemson.cs.r2jt.proving.DummyIterator;
import edu.clemson.cs.r2jt.proving2.Theorem;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.transformations.ExpandAntecedentByImplication;
import edu.clemson.cs.r2jt.proving2.transformations.ExpandAntecedentBySubstitution;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import edu.clemson.cs.r2jt.proving2.utilities.AddsSomethingNewPredicate;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author hamptos
 */
public class AntecedentDeveloper implements Automator {

    private final Iterable<Transformation> myTheoremTransformations;
    private final AddsSomethingNewPredicate myDevelopmentPredicate;
    private int myRemainingRounds;

    public AntecedentDeveloper(PerVCProverModel model,
            Iterable<Theorem> theorems, int totalRounds) {
        myDevelopmentPredicate = new AddsSomethingNewPredicate(model);
        myRemainingRounds = totalRounds;

        List<Transformation> transformations = new LinkedList<Transformation>();
        List<Transformation> tTransformations;
        for (Theorem t : theorems) {
            tTransformations = t.getTransformations();
            for (Transformation transformation : tTransformations) {
                if (transformation instanceof ExpandAntecedentByImplication
                        || transformation instanceof ExpandAntecedentBySubstitution) {

                    if (!transformation.introducesQuantifiedVariables()) {
                        transformations.add(transformation);
                    }
                }
            }
        }
        myTheoremTransformations = transformations;
    }

    @Override
    public void step(Deque<Automator> stack, PerVCProverModel model) {
        if (myRemainingRounds > 0) {
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

        private Iterator<Application> myApplications;

        public AntecedentDeveloperRound(PerVCProverModel model) {
            List<Application> applications = new LinkedList<Application>();
            //We need to iterate over all possible applications, but each
            //application will add a new antecedent to the model, which we 
            //don't want to consider in this pass.  Since no application is 
            //going to change or remove a current antecedent, and we know 
            //that we're going to apply them all, we slurp them all up now 
            //to be applied later one at a time.
            Iterator<Application> tApplications;
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
            if (myApplications.hasNext()) {
                stack.push(new ProbationaryApplication(myApplications.next(),
                        myDevelopmentPredicate));
            }
            else {
                stack.pop();
            }
        }
    }
}
