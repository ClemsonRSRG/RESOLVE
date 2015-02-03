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

import java.util.Deque;

/**
 * <p>A <code>RuleProvider</code> simply assists the prover iterating over the
 * available rules to chose the next one to try to apply.  An example of a
 * very simple <code>RuleProvider</code> would be an iterative implementation
 * that simply iterates over all available rules in the order they were 
 * initially provided.</p>
 * 
 * <p>The general order in which methods should be called is:</p>
 * 
 * <p><ol>
 * <li>Call <code>addRule()</code> to add each rule.</li>
 * <li>Call <code>lock()</code> to indicate to the implementation that you are
 * done adding rules.</li>
 * <li>Call <code>consider()</code> each time you're ready to start applying
 * rules to a particular situation.</li>
 * <li>Call <code>getNextBestRule()</code> to iterate over the suggested order
 * for the rules until it throws a <code>DoneSuggestingException</code>.</li>
 * </ol></p>
 * 
 * @author H. Smith
 */
public abstract class RuleProvider {

    /**
     * <p>Indicates to the provider that a new situation is being considered for
     * which we would like suggestions of rules to apply.  Returns an Iterator
     * over the ordering of rules it would like to apply.</p>
     * 
     * @param vC The VC under consideration.
     * @param curLength The current depth into the proof.
     * @param metrics A set of useful metrics about the state of the proof.
     * @param pastStates A list of past transformations to the VC on our way
     * 		to this point.  <code>pastStates.size() == curLength</code>
     * 
     * @return An Iterator of <code>MatchReplace</code>s representing the rules
     * 			that should be applied in the order they should be applied.
     * 
     * @throws NullPointerException If any object parameter is 
     * 		<code>null</code>.
     */
    public abstract KnownSizeIterator<MatchReplace> consider(
            VerificationCondition vC, int curLength, Metrics metrics,
            Deque<VerificationCondition> pastStates);

    /**
     * <p>This should eventually supersede the above.</p>
     * 
     * @param vc
     * @param curLength
     * @param metrics
     * @param pastStates
     * @return
     */
    public KnownSizeIterator<MatchReplace> consider(VC vc, int curLength,
            Metrics metrics, Deque<VC> pastStates) {

        //This insanity brought to you by edu.clemson.cs.r2jt.collections.List
        return consider(new VerificationCondition(
                new edu.clemson.cs.r2jt.collections.List(vc.getAntecedent()
                        .getMutableCopy()),
                new edu.clemson.cs.r2jt.collections.List(vc.getConsequent()
                        .getMutableCopy()), (String) null), curLength, metrics,
                null);
    }

    /**
     * <p>Returns the approximate size of the set of <code>MatchReplace</code>s
     * that will be returned from any given call to <code>consider()</code>.  If
     * there is no way to approximate, returns -1.</p>
     * 
     * @return The approximate size of a rule set returned from any given call
     *         to <code>consider</code> on this provider or -1 if there is no
     *         way to approximate.
     */
    public abstract int getApproximateRuleSetSize();
}
