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

import java.util.Set;

/**
 * <p>A straightforward implementation of 
 * <code>TransformerFitnessFunction</code> that gives greater relevance to those
 * transformers that operate on similar variable types and function names as
 * those that appear in the VC to be operated on.  Also gives greater relevance
 * to those transformers that simplify (i.e., reduce the number of variables or
 * functions) rather than complicate.</p>
 * 
 * <p>This fitness function advises against (i.e., returns a negative value from
 * <code>calculateFitness()</code>) the application of transformations that
 * introduce quantified variables.</p>
 */
public class SimpleTransformerFitnessFunction
        extends
            TransformerFitnessFunction {

    @Override
    public String toString() {
        return "Relevance Fitness";
    }

    @Override
    public double calculateFitness(VCTransformer t, VC vc) {
        double retval;

        try {
            if (t.introducesQuantifiedVariables()) {
                retval = -1;
            }
            else {
                Antecedent pattern = t.getPattern();
                Consequent template = t.getReplacementTemplate();

                Set<String> vcFunctions = vc.getConsequent().getSymbolNames();

                Set<String> ruleFunctions = pattern.getSymbolNames();
                ruleFunctions.addAll(template.getSymbolNames());

                int nonOverlaps = inAButNotB(ruleFunctions, vcFunctions);

                double findFunctionCount =
                        pattern.getFunctionApplications().size();
                double replaceFunctionCount =
                        template.getFunctionApplications().size();
                double simplificationRatio =
                        (replaceFunctionCount + 1.0)
                                / (findFunctionCount + 1.0);

                double simplificationFactor =
                        Math.pow(0.9, simplificationRatio);

                retval =
                        Math.min(Math.pow(0.8, nonOverlaps)
                                * simplificationFactor, 1.0);
            }
        }
        catch (UnsupportedOperationException e) {
            throw new RuntimeException(this.getClass() + " doesn't know how "
                    + "to rank a " + t.getClass());
        }

        return retval;
    }

    private static int inAButNotB(Set<String> a, Set<String> b) {
        int notThere = 0;

        for (String s : a) {
            if (!b.contains(s)) {
                notThere++;
            }
        }

        return notThere;
    }
}
