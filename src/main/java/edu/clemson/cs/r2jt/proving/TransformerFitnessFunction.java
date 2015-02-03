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

import java.util.LinkedList;
import java.util.List;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.FunctionExp;
import edu.clemson.cs.r2jt.absyn.InfixExp;
import edu.clemson.cs.r2jt.absyn.OutfixExp;
import edu.clemson.cs.r2jt.absyn.PrefixExp;

/**
 * <p>A <code>TransformerFitnessFunction</p> assigns a relevance ranking to
 * <code>VCTransformers</code> in light of a particular <code>VC</code>.</p>
 */
public abstract class TransformerFitnessFunction {

    /**
     * <p>Utility function to count the total number of function applications in
     * a list of <code>Exp</code>s.</p>
     * 
     * @param es A <code>List</code> of <code>Exp</code> in which to count the 
     *           functions.
     * @return The number of function applications.
     */
    public static int functionCount(Iterable<Exp> es) {
        int count = 0;

        for (Exp e : es) {
            count += functionCount(e);
        }

        return count;
    }

    /**
     * <p>Utility function to count the number of function applications in a
     * given <code>Exp</code>.</p>
     * 
     * @param e An <code>Exp</code> in which to count the functions.
     * @return The number of function applications.
     */
    public static int functionCount(Exp e) {
        int retval = 0;

        List<Exp> subexpressions = e.getSubExpressions();
        for (Exp subexpression : subexpressions) {
            retval += functionCount(subexpression);
        }

        if (e instanceof FunctionExp || e instanceof InfixExp
                || e instanceof OutfixExp || e instanceof PrefixExp) {

            retval += 1;
        }

        return retval;
    }

    /**
     * <p>Returns a real value between -1 and 1, inclusive, indicating the
     * relative likelihood a <code>t</code> would be useful to apply to 
     * <code>vc</code> on the way to a proof.  In general, the magnitude of the
     * returned values should not be considered meaningful--only the ordering 
     * they impose on the various transformers.  However, fitness values less
     * than 0 indicate that, in the opinion of the fitness function, the given
     * transformation should not even be attempted.</p>
     * 
     * @param t The transformer whose fitness should be determined.
     * @param vc The VC in the context of which <code>t</code>'s relevance
     *           should be determined.
     *           
     * @return A double value between -1 and 1, inclusive, indicating this
     *         fitness function's opinion on the relative likelihood that the
     *         given transformation would be a useful one to apply.  Negative
     *         values indicate that this fitness function's opinion is that it
     *         is not even worth attempting the given transformer.
     */
    public abstract double calculateFitness(VCTransformer t, VC vc);

    public final Iterable<VCTransformer> filter(
            Iterable<VCTransformer> transformers, VC vc, double threshhold) {

        List<VCTransformer> passedTransformers =
                new LinkedList<VCTransformer>();

        for (VCTransformer t : transformers) {
            if (calculateFitness(t, vc) >= threshhold) {
                passedTransformers.add(t);
            }
        }

        return passedTransformers;
    }
}
