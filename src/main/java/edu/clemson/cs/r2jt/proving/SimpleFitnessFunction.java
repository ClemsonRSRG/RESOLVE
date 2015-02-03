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

import edu.clemson.cs.r2jt.absyn.EqualsExp;
import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.FunctionExp;
import edu.clemson.cs.r2jt.absyn.InfixExp;
import edu.clemson.cs.r2jt.absyn.OutfixExp;
import edu.clemson.cs.r2jt.absyn.PrefixExp;

public class SimpleFitnessFunction implements FitnessFunction<EqualsExp> {

    public double determineFitness(EqualsExp rule, VerificationCondition vc) {

        Conjuncts consequents = vc.getConsequents();

        List<String> vcFunctions = getFunctionsIn(consequents);
        List<String> ruleFunctions = getFunctionsIn(rule);

        int nonOverlaps =
                Math.max(inAButNotB(vcFunctions, ruleFunctions), inAButNotB(
                        ruleFunctions, vcFunctions));

        double simplificationFactor =
                functionCount(rule.getLeft())
                        / (double) (functionCount(rule.getRight()) + 1);

        return Math.min(1 * Math.pow(0.9, nonOverlaps) * simplificationFactor
                * 0.9, 1.0);
    }

    private int inAButNotB(List<String> a, List<String> b) {
        int notThere = 0;

        for (String s : a) {
            if (!b.contains(s)) {
                notThere++;
            }
        }

        return notThere;
    }

    private List<String> getFunctionsIn(Conjuncts c) {
        List<String> accumulator = new LinkedList<String>();
        for (Exp e : c) {
            getFunctionsIn(e, accumulator);
        }

        return accumulator;
    }

    private List<String> getFunctionsIn(Exp e) {
        List<String> accumulator = new LinkedList<String>();

        getFunctionsIn(e, accumulator);

        return accumulator;
    }

    private void getFunctionsIn(Exp e, List<String> accumulator) {
        if (e instanceof FunctionExp) {
            addUnique(accumulator, ((FunctionExp) e).getName().getName());
        }
        else if (e instanceof InfixExp) {
            addUnique(accumulator, ((InfixExp) e).getOpName().getName());
        }
        /*else if (e instanceof OutfixExp) {
        	addUnique(accumulator, ((OutfixExp) e).getOperator().getName());
        }*///XXX : WHY DOES OUTFIX HAVE A NUMERICAL OPERATOR AND EVERYTHING
        //      ELSE USES STRING?????????
        else if (e instanceof PrefixExp) {
            addUnique(accumulator, ((PrefixExp) e).getSymbol().getName());
        }

        List<Exp> subexpressions = e.getSubExpressions();
        for (Exp subexpression : subexpressions) {
            getFunctionsIn(subexpression, accumulator);
        }
    }

    private int functionCount(Conjuncts c) {
        int accumulator = 0;
        for (Exp e : c) {
            accumulator += functionCount(e);
        }

        return accumulator;
    }

    private int functionCount(Exp e) {
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

    private void addUnique(List<String> l, String e) {
        if (!l.contains(e)) {
            l.add(e);
        }
    }
}
