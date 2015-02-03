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
package edu.clemson.cs.r2jt.proving2.transformations;

import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.applications.Application;
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
