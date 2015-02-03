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
package edu.clemson.cs.r2jt.proving;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.clemson.cs.r2jt.proving.absyn.BindingException;
import edu.clemson.cs.r2jt.proving.absyn.PExp;

/**
 * <p>An <code>IncrementalBindingIterator</code> returns successive possible 
 * bindings of a universally quantified <code>Exp</code> to a set of available 
 * facts, possibly given a set of assumed bindings.  All possible bindings will 
 * be returned, but the order they will be returned in is undefined.</p>
 * 
 * <p>As an example, if the <code>Exp</code> is 
 * <code>For all i, j : Z, i &lt; j</code> and the set of facts is:</p>
 * 
 * <ul>
 *   <li><code>a &lt; b</code></li>
 *   <li><code>c &lt; b</code></li>
 *   <li><code>d &lt; c</code></li>
 * </ul>
 * 
 * And there is only one assumed binding, namely:
 * 
 * <ul>
 *   <li><code>j --&gt; b</code></li>
 * </ul>
 * 
 * <p>Then <code>IncrementalBindingIterator</code> might first return the 
 * binding:</p>
 * 
 * <ul>
 *   <li><code>j --&gt; b</code></li>
 *   <li><code>i --&gt; a</code></li>
 * </ul>
 * 
 * <p>And then:</p>
 * 
 * <ul>
 *   <li><code>j --&gt; b</code></li>
 *   <li><code>i --&gt; c</code></li>
 * </ul>
 * 
 * <p>After which it will return nothing else.  Note that by time the 
 * <code>Exp</code> makes its way to this class, quantifiers should already have
 * been eliminated and propagated down to the variables themselves (that is, the
 * variable nodes should reflect their quantified state, not a top-level 
 * quantifying expression.)</p>
 */
public class IncrementalBindingIterator implements Iterator<Map<PExp, PExp>> {

    private static final Map<PExp, PExp> EMPTY_MAP = new HashMap<PExp, PExp>();

    private final PExp myPattern;
    private final Iterator<PExp> myFacts;

    private final Map<PExp, PExp> myAssumedBindings;

    private Map<PExp, PExp> myCurrentIncrementalBindings;

    public IncrementalBindingIterator(PExp pattern, Iterator<PExp> facts,
            Map<PExp, PExp> assumedBindings) {

        myPattern = pattern.substitute(assumedBindings);
        myFacts = facts;
        myAssumedBindings = assumedBindings;

        setUpNext();
    }

    public IncrementalBindingIterator(PExp pattern, Iterable<PExp> facts,
            Map<PExp, PExp> assumedBindings) {
        this(pattern, facts.iterator(), assumedBindings);
    }

    public IncrementalBindingIterator(PExp pattern, Iterator<PExp> facts) {
        this(pattern, facts, EMPTY_MAP);
    }

    public IncrementalBindingIterator(PExp pattern, Iterable<PExp> facts) {
        this(pattern, facts.iterator(), EMPTY_MAP);
    }

    private void setUpNext() {

        myCurrentIncrementalBindings = null;
        while (myCurrentIncrementalBindings == null && myFacts.hasNext()) {
            PExp fact = myFacts.next().substitute(myAssumedBindings);

            try {
                myCurrentIncrementalBindings = myPattern.bindTo(fact);
            }
            catch (BindingException e) {

            }
        }
    }

    @Override
    public boolean hasNext() {
        return myCurrentIncrementalBindings != null;
    }

    @Override
    public Map<PExp, PExp> next() {
        Map<PExp, PExp> retval = new HashMap<PExp, PExp>();

        //TODO : Might be good, eventually, to have a "chaining map" that makes
        //       this a constant-time operation
        retval.putAll(myCurrentIncrementalBindings);
        retval.putAll(myAssumedBindings);

        setUpNext();

        return retval;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
