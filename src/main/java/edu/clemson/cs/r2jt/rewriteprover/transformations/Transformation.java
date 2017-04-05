/*
 * Transformation.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover.transformations;

import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel;
import edu.clemson.cs.r2jt.rewriteprover.applications.Application;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>Whereas a {@link Theorem} represents a high-level mathematical statement
 * of truth, a <code>Transformation</code> represents a more fine-grained 
 * transformation that can be applied to a prover state.  So, for example, a 
 * theorem may state that <code>|&lt;Empty_String&gt;| = 0</code>, but a 
 * specific transformation derived from that theorem might be "Replace instances
 * of <code>0</code> with <code>|Empty_String|</code> inside consequents."</p>
 * 
 * <p>Transformations still represent a general sort of "thing that can be done"
 * rather than an actual step that has been taken in a proof.  For this reason,
 * transformation is generally the level at which heuristics operate, ordering
 * and pruning transformations based on the reality of the VC, before any action
 * is actually taken.</p>
 */
public interface Transformation {

    public enum Equivalence {
        WEAKER, EQUIVALENT, STRONGER
    }

    public Iterator<Application> getApplications(PerVCProverModel m);

    public boolean couldAffectAntecedent();

    public boolean couldAffectConsequent();

    public int functionApplicationCountDelta();

    public boolean introducesQuantifiedVariables();

    public Set<String> getPatternSymbolNames();

    public Set<String> getReplacementSymbolNames();

    public Equivalence getEquivalence();

    /**
     * <p>A string key that identifies a transformation consistently between 
     * invocations of the compiler.  That is: all transformations of the same
     * concrete class that do the same thing should share a key, and that key
     * should not change from invocation to invocation of the compiler.</p>
     * 
     * @return 
     */
    public String getKey();
}
