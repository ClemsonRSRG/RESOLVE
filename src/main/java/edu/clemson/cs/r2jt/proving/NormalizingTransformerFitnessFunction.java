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

/**
 * <p>A <code>NormalizingTransformerFitnessFunction</p> ranks a set of 
 * transforms based on their affect on the number of function applications in a 
 * <code>VC</code>.  Transformers that reduce this number more are given higher 
 * precedence.  Transformers that do not reduce the number (i.e., keep it the 
 * same or increase it) are recommended against (i.e., they will be given a 
 * negative weight.)</p>
 */
public class NormalizingTransformerFitnessFunction
        extends
            TransformerFitnessFunction {

    @Override
    public String toString() {
        return "Reduction Fitness";
    }

    @Override
    public double calculateFitness(VCTransformer t, VC vc) {

        double retval;

        if (t instanceof MatchReplaceStep) {
            Antecedent pattern = t.getPattern();
            Consequent template = t.getReplacementTemplate();

            if (template.containsQuantifiedVariableNotIn(pattern)) {
                retval = -1;
            }
            else {

                double findFunctionCount =
                        pattern.getFunctionApplications().size();
                double replaceFunctionCount =
                        template.getFunctionApplications().size();
                double difference = findFunctionCount - replaceFunctionCount;

                double simplificationFactor = Math.pow(0.5, difference);

                //Note at this point that 0 < simplificationFactor <= .5  if
                //the rule results in the reduction of at least one function
                //application.  And 1 <= simplificationFactor otherwise.
                retval = .9 - simplificationFactor;
            }
        }
        else {
            retval = -1;
        }

        return retval;
    }
}
