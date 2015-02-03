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

import java.util.LinkedList;
import java.util.List;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol;

/**
 * <p>The base class for <code>RuleNormalizer</code>s that operate only on those
 * rules of the form <code>x = y</code>.
 * 
 * <p>Because other sorts of rules are generally mistakes as of this writing,
 * this class can be put into <em>noisy mode</em>, in which it will print
 * a warning when it filters out a rule.</p>
 */
public abstract class AbstractEqualityRuleNormalizer implements RuleNormalizer {

    private static final List<VCTransformer> DUMMY_SET =
            new LinkedList<VCTransformer>();

    private final boolean myNoisyFlag;

    public AbstractEqualityRuleNormalizer(boolean noisy) {
        myNoisyFlag = noisy;
    }

    @Override
    public final Iterable<VCTransformer> normalize(PExp e) {
        List<VCTransformer> retval = DUMMY_SET;

        if (e instanceof PSymbol) {
            PSymbol sE = (PSymbol) e;

            if (sE.isEquality()) {
                retval = doNormalize(sE.arguments.get(0), sE.arguments.get(1));
            }
        }

        if (myNoisyFlag && retval == DUMMY_SET) {
            System.out.println("WARNING: " + this.getClass() + " is "
                    + "filtering out this non-equality rule: \n" + e.toString()
                    + "(" + e.getClass() + ")");
        }

        return retval;
    }

    protected abstract List<VCTransformer> doNormalize(PExp left, PExp right);

    public final Iterable<VCTransformer> normalizeAll(Iterable<PExp> es) {
        return normalizeAll(this, es);
    }

    public static final Iterable<VCTransformer> normalizeAll(RuleNormalizer n,
            Iterable<PExp> es) {

        ChainingIterable<VCTransformer> transformers =
                new ChainingIterable<VCTransformer>();

        for (PExp e : es) {
            transformers.add(n.normalize(e));
        }

        return transformers;
    }
}
