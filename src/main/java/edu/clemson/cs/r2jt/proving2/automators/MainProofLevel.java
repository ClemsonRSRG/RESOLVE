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

import edu.clemson.cs.r2jt.proving.ChainingIterator;
import edu.clemson.cs.r2jt.proving.DummyIterator;
import edu.clemson.cs.r2jt.proving.Simplifier;
import edu.clemson.cs.r2jt.proving2.AutomatedProver;
import edu.clemson.cs.r2jt.proving2.model.Theorem;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * <p><code>MainProofLevel</code> is an @{link Automator Automator} that 
 * attempts to apply series of 
 * {@link edu.clemson.cs.r2jt.proving2.transformations.Transformation
 *     Transformation}s, by spawning a 
 * {@link ApplicationApplier ApplicationApplier} on the automator stack for 
 * each application of each transformation.  When applying the application is
 * done, this automator spawns a {@link Simplify Simplify} automator,
 * followed by a {@link CheckDone CheckDone} automator.  Assuming the proof is
 * not completed, it then spawns a {@link Restore Restore} automator on the
 * stack that will restore the proof to how it looked before the application,
 * then spawns a new <code>MainProofLevel</code> automator on top.</p>
 * 
 * <p>Each <code>MainProofLevel</code> has a "tether length".  When it spawns
 * a new, sub-<code>MainProofLevel</code>, the new one will have a tether length
 * one less than its spawner.  A <code>MainProofLevel</code> with a tether
 * length of zero will not spawn further levels, but simply pop itself off the
 * stack.</p>
 */
public class MainProofLevel implements Automator {

    private final PerVCProverModel myModel;
    private final int myTetherLength;
    private final Iterable<Transformation> myTransformations;

    private Iterator<Transformation> myTransformationsIterator;
    private Transformation myCurrentTransformation;
    private Iterator<Application> myCurrentApplications;

    private int myStep;

    private Restore myRestore;

    private final Set<Integer> myPreviousProofStates;

    private boolean myDetectedCycleFlag;

    public MainProofLevel(PerVCProverModel model, int tetherLength,
            Iterable<Transformation> transformations) {
        this(model, tetherLength, transformations, new HashSet<Integer>());
    }

    public MainProofLevel(PerVCProverModel model, int tetherLength,
            Iterable<Transformation> transformations,
            Set<Integer> previousProofStates) {

        myModel = model;
        myTetherLength = tetherLength;
        myTransformations = transformations;
        myPreviousProofStates = previousProofStates;

        myCurrentApplications =
                DummyIterator.getInstance(myCurrentApplications);
    }

    private void prepTransformationIterator() {
        List<Transformation> localTransformations =
                new LinkedList<Transformation>();
        List<Transformation> localTheoremTransformations;
        for (Theorem t : myModel.getLocalTheoremList()) {
            localTheoremTransformations = t.getTransformations();

            for (Transformation transformation : localTheoremTransformations) {
                if (!transformation.couldAffectAntecedent()) {
                    localTransformations.add(transformation);
                }
            }
        }

        myTransformationsIterator =
                new ChainingIterator<Transformation>(localTransformations
                        .iterator(), myTransformations.iterator());
    }

    /**
     * <p>Performs bookkeeping before a restore happens.</p>
     */
    public void prepForRestore() {
        if (!myDetectedCycleFlag) {
            myPreviousProofStates.remove(myModel.implicationHashCode());
        }
        myDetectedCycleFlag = false;
    }

    @Override
    public void step(Deque<Automator> stack, PerVCProverModel model) {
        if (myTransformationsIterator == null) {
            prepTransformationIterator();
            myPreviousProofStates.add(model.implicationHashCode());
            myRestore = new Restore(model, this);
        }

        switch (myStep) {
        case 0:
            //Apply some application
            while (!myCurrentApplications.hasNext()
                    && myTransformationsIterator.hasNext()) {

                myCurrentTransformation = myTransformationsIterator.next();
                myCurrentApplications =
                        myCurrentTransformation.getApplications(model);
            }

            if (myCurrentApplications.hasNext()) {
                myCurrentApplications.next().apply(model);
            }
            else {
                stack.pop();
            }
            break;
        case 1:
            //Simplify
            stack.push(Simplify.INSTANCE);
            break;
        case 2:
            //Next level
            stack.push(myRestore);

            myDetectedCycleFlag =
                    AutomatedProver.H_DETECT_CYCLES
                            && myPreviousProofStates.contains(myModel
                                    .implicationHashCode());

            if (myTetherLength > 0 && !myDetectedCycleFlag) {
                stack.push(new MainProofLevel(myModel, myTetherLength - 1,
                        myTransformations, myPreviousProofStates));
            }
            break;
        default:
            throw new RuntimeException("Unexpected step: " + myStep);
        }

        myStep = (myStep + 1) % 3;
    }
}
