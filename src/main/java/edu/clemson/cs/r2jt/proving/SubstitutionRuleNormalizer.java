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

import java.util.ArrayList;
import java.util.List;

import edu.clemson.cs.r2jt.proving.absyn.PExp;

/**
 * <p>A <code>RuleNormalizer</code> that filters out all rules but simple
 * substitutions, i.e. those rules of the form <code>x = y</code> and expresses
 * each as two different <code>VCTransformer</code>s, one substituting 
 * <code>y</code> for <code>x</code> and one substituting <code>x</code> for
 * <code>y</code>.  This substitution occurs either in the consequent of the VC
 * or as an extension to the antecedent.</p>
 * 
 * <p>Because other sorts of rules are generally mistakes as of this writing,
 * this class can be put into <em>noisy mode</em>, in which it will print
 * a warning when it filters out a rule.</p>
 */
public class SubstitutionRuleNormalizer extends AbstractEqualityRuleNormalizer {

    public SubstitutionRuleNormalizer(boolean noisy) {
        super(noisy);
    }

    public SubstitutionRuleNormalizer() {
        this(true);
    }

    @Override
    protected List<VCTransformer> doNormalize(PExp left, PExp right) {
        List<VCTransformer> retval = new ArrayList<VCTransformer>(2);

        //Substitute left expression for right
        retval.add(new MatchReplaceStep(new NewBindReplace(left, right)));

        //Substitute right expression for left
        retval.add(new MatchReplaceStep(new NewBindReplace(right, left)));

        return retval;
    }

}
