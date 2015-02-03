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

import java.util.Iterator;

/**
 * <p>A <code>VCTransformer</code> maps a VC into one or more new VCs according
 * to some predefined rule.</p>
 */
public interface VCTransformer {

    /**
     * <p>Returns an <code>Iterator</code> over alternative applications of this
     * transformer to the given <code>VC</code>.</p>
     *
     * @param original The <code>VC</code> to transform.
     *
     * @return A non-<code>null</code> <code>Iterator</code> over alternative
     *         applications of this transformer to <code>original</code>.  Note
     *         that if there are no such applications, this method will return
     *         an <code>Iterator</code> over the empty set.
     */
    public Iterator<VC> transform(VC original);

    /**
     * <p>Most <code>VCTransformer</code>s represent the application of a
     * theorem that is looking for some pattern and transforming it according
     * to some template.</p>
     *
     * <p>If this is the case for this transformer, this method returns the
     * pattern it is looking for.  Otherwise, this method indicates that the
     * idea of a pattern is not applicable by throwing an
     * <code>UnsupportedOperationException</code>.</p>
     *
     * @return The pattern this transformer is matching against.
     *
     * @throws UnsupportedOperationException If the concept of a pattern is
     *      not applicable.
     */
    public Antecedent getPattern();

    /**
     * <p>Most <code>VCTransformer</code>s represent the application of a
     * theorem that is looking for some pattern and transforming it according
     * to some template.</p>
     *
     * <p>If this is the case for this transformer, this method returns the
     * template used for transforming.  Otherwise, this method indicates that 
     * the idea of a template is not applicable by throwing an
     * <code>UnsupportedOperationException</code>.</p>
     *
     * @return The replacement template this transformer is applying.
     *
     * @throws UnsupportedOperationException If the concept of a template is
     *      not applicable.
     */
    public Consequent getReplacementTemplate();

    /**
     * <p>Returns <code>false</code> <strong>iff</strong> no application of
     * this transformation to any VC could result in a new, unbound quantified
     * variable.</p>
     * 
     * @return <code>false</code> <strong>iff</strong> no application of
     * 		this transformation to any VC could result in a new, unbound 
     * 		quantified variable.
     */
    public boolean introducesQuantifiedVariables();
}
