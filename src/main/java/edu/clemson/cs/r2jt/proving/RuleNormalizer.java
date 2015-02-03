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

import edu.clemson.cs.r2jt.proving.absyn.PExp;

/**
 * <p>A <code>RuleNormalizer</code> acts as a bridge between theorems and other
 * mathematical rules for transformation expressed as <code>Exp</code>s and
 * the <code>VCTransformer</code>s used by the proof subsystem to apply such
 * transformations.  It transforms <code>Exp</code>s into iterable sets of 
 * <code>VCTransformer</code>s.</p>
 * 
 * <p>In addition, it acts as a filter, eliminating those <code>Exp</code>
 * unsuitable to its purpose by returning empty sets for them.</p>
 */
public interface RuleNormalizer {

    /**
     * <p>Takes an <code>Exp</code> representing a mathematical rule and returns
     * an <code>Iterable</codE> over <code>VCTransformer</code>s that represent
     * the applications of that rule.  May return an empty set, but will not
     * return <code>null</code>.</p>
     * 
     * @param e The mathematical rule.
     * @return A non-null iterable set of <code>VCTransformer</code>s.
     */
    public Iterable<VCTransformer> normalize(PExp e);
}
