/*
 * Utilities.java
 * ---------------------------------
 * Copyright (c) 2019
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import edu.clemson.cs.r2jt.absyn.BetweenExp;
import edu.clemson.cs.r2jt.absyn.EqualsExp;
import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.FunctionExp;
import edu.clemson.cs.r2jt.absyn.InfixExp;
import edu.clemson.cs.r2jt.absyn.MathVarDec;
import edu.clemson.cs.r2jt.absyn.QuantExp;
import edu.clemson.cs.r2jt.absyn.VarExp;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;

/**
 * <p>A variety of useful general-purpose methods.</p>
 *
 * @author H. Smith
 */
//This class can go to straight to hell.
public class Utilities {

    private static boolean genericBindWarningPrinted = false;
    private static boolean myBindDebugFlag = false;

    public static void setBindDebugFlag(boolean f) {
        myBindDebugFlag = f;
    }

    public static String conjunctListToString(java.util.List<PExp> l) {
        StringBuilder b = new StringBuilder();

        boolean first = true;
        for (PExp p : l) {
            if (first) {
                first = false;
            }
            else {
                b.append(" and ");
            }

            b.append(p);
        }

        return "" + b;
    }

    public static void buildExpMapFromPosSymbolMap(
            Map<PosSymbol, Exp> original, Map<Exp, Exp> newMap) {
        Set<Map.Entry<PosSymbol, Exp>> entrySet = original.entrySet();
        for (Map.Entry<PosSymbol, Exp> binding : entrySet) {
            newMap.put(new VarExp(null, null, binding.getKey()), binding
                    .getValue());
        }
    }

    public static Map<Exp, Exp> shallowCopyMap(Map<Exp, Exp> original) {
        Map<Exp, Exp> newMap = new HashMap<Exp, Exp>();

        Set<Map.Entry<Exp, Exp>> entrySet = original.entrySet();
        for (Map.Entry<Exp, Exp> binding : entrySet) {
            newMap.put(binding.getKey(), binding.getValue());
        }

        return newMap;
    }

    public static List<Exp> splitIntoConjuncts(List<Exp> c) {
        List<Exp> retval = new List<Exp>();
        for (Exp e : c) {
            splitIntoConjuncts(e, retval);
        }

        return retval;
    }

    public static List<Exp> splitIntoConjuncts(Exp e) {
        List<Exp> accumulator = new List<Exp>();
        splitIntoConjuncts(e, accumulator);
        return accumulator;
    }

    public static void splitIntoConjuncts(Exp e, List<Exp> accumulator) {
        if (e instanceof InfixExp && Utilities.isAndExp((InfixExp) e)) {
            InfixExp eAsInfix = (InfixExp) e;
            splitIntoConjuncts(eAsInfix.getLeft(), accumulator);
            splitIntoConjuncts(eAsInfix.getRight(), accumulator);
        }
        else if (e instanceof BetweenExp) {
            BetweenExp eAsBetween = (BetweenExp) e;
            List<Exp> subexpressions = eAsBetween.getLessExps();

            for (Exp sub : subexpressions) {
                splitIntoConjuncts(sub, accumulator);
            }
        }
        else {
            accumulator.add(e);
        }
    }

    public static List<Exp> splitIntoDisjuncts(Exp e) {
        List<Exp> accumulator = new List<Exp>();
        splitIntoDisjuncts(e, accumulator);
        return accumulator;
    }

    private static void splitIntoDisjuncts(Exp e, List<Exp> disjuncts) {
        if (e instanceof InfixExp && Utilities.isOrExp((InfixExp) e)) {
            InfixExp eAsInfix = (InfixExp) e;
            splitIntoDisjuncts(eAsInfix.getLeft(), disjuncts);
            splitIntoDisjuncts(eAsInfix.getRight(), disjuncts);
        }
        else {
            disjuncts.add(e);
        }
    }

    /**
     * <p>Returns <code>true</code> <strong>iff</strong> the given expression is
     * an equality with both sides precisely equal.</p>
     *
     * @param expression The expression to test.
     * @return <code>true</code> <strong>iff</strong> <code>e</code> is an
     *         equality with both sides precisely equal.
     */
    public static boolean isSymmetricEquality(Exp expression) {
        boolean retval = false;

        if (expression instanceof EqualsExp) {
            EqualsExp expressionAsEquals = (EqualsExp) expression;

            if (expressionAsEquals.getOperator() == EqualsExp.EQUAL) {
                retval =
                        expressionAsEquals.getLeft().equivalent(
                                expressionAsEquals.getRight());
            }
        }

        return retval;
    }

    /**
     * <p>Returns <code>true</code> <strong>iff</strong> the provided
     * <code>Exp</code> repesents the Resolve boolean value "true".</p>
     *
     * @param e The <code>Exp</code> to check.
     * @return <code>true</code> <strong>iff</strong> <code>e</code> represents
     *         the boolean value "true".
     */
    public static boolean isLiteralTrue(Exp e) {
        return (e instanceof VarExp && ((VarExp) e).getName().getName()
                .equalsIgnoreCase("true"));
    }

    /**
     * <p>Returns <code>true</code> <strong>iff</strong> the provided
     * <code>InfixExp</code> represents Resolve boolean function "and".</p>
     *
     * @param e The <code>InfixExp</code> to check.
     * @return <code>true</code> <strong>iff</strong> <code>e</code> represents
     *         boolean "and".
     */
    public static boolean isAndExp(InfixExp e) {
        return ((InfixExp) e).getOpName().getName().equals("and");
    }

    /**
     * <p>Returns <code>true</code> <strong>iff</strong> the provided
     * <code>InfixExp</code> represents Resolve boolean function "and".</p>
     *
     * @param e The <code>InfixExp</code> to check.
     * @return <code>true</code> <strong>iff</strong> <code>e</code> represents
     *         boolean "and".
     */
    public static boolean isOrExp(InfixExp e) {
        return ((InfixExp) e).getOpName().getName().equals("or");
    }

    private static Map<PosSymbol, Exp> unifyMaps(Map<PosSymbol, Exp> m1,
            Map<PosSymbol, Exp> m2) {
        Set<Map.Entry<PosSymbol, Exp>> entrySet = m2.entrySet();
        Iterator<Map.Entry<PosSymbol, Exp>> entryIter = entrySet.iterator();

        Exp otherExp;
        Map<PosSymbol, Exp> retval = m1;
        Map.Entry<PosSymbol, Exp> entry;
        while (retval != null && entryIter.hasNext()) {
            entry = entryIter.next();

            otherExp = mapContainsPosSymbol(retval, entry.getKey());

            if (otherExp != null) {
                if (!otherExp.equivalent(entry.getValue())) {
                    retval = null;
                }
            }
            else {
                retval.put(entry.getKey(), entry.getValue());
            }
        }

        return retval;
    }

    /**
     * <p>(TODO) See newBind</p>
     * @param m1
     * @param m2
     * @return
     */
    private static Map<Exp, Exp> newUnifyMaps(Map<Exp, Exp> m1, Map<Exp, Exp> m2) {
        Set<Map.Entry<Exp, Exp>> entrySet = m2.entrySet();
        Iterator<Map.Entry<Exp, Exp>> entryIter = entrySet.iterator();

        Exp otherExp;
        Map<Exp, Exp> retval = m1;
        Map.Entry<Exp, Exp> entry;
        while (retval != null && entryIter.hasNext()) {
            entry = entryIter.next();

            otherExp = mapContainsExp(retval, entry.getKey());

            if (otherExp != null) {
                if (!otherExp.equivalent(entry.getValue())) {
                    retval = null;
                }
            }
            else {
                retval.put(entry.getKey(), entry.getValue());
            }
        }

        return retval;
    }

    /**
     * <p>(TODO) See newBind.</p>
     * @param m
     * @param k
     * @return
     */
    private static Exp mapContainsExp(Map<Exp, Exp> m, Exp k) {
        Exp retval = null;

        Iterator<Exp> i = m.keySet().iterator();
        Exp curKey;
        while (i.hasNext() && retval == null) {
            curKey = i.next();
            if (curKey.equivalent(k)) {
                retval = m.get(curKey);
            }
        }

        return retval;
    }

    private static Exp mapContainsPosSymbol(Map<PosSymbol, Exp> m, PosSymbol k) {
        Exp retval = null;

        Iterator<PosSymbol> i = m.keySet().iterator();
        PosSymbol curKey;
        while (i.hasNext() && retval == null) {
            curKey = i.next();
            if (curKey.getName().equals(k.getName())) {
                retval = m.get(curKey);
            }
        }

        return retval;
    }

    /**
     * <p>Takes an expression and distributes its quantifiers down to the
     * variables, removing the quantifiers in the process.  (That is, in the
     * expression <code>For all x, x = y</code>, the variable <code>x</code>
     * in the equals expression would be marked internaly as a "for all"
     * variable and the expression <code>x = y</code> would be returned.)
     * <code>For all</code> quantifiers are not permitted to have a "where"
     * clause (that is, they must be total).  If a <code>for all</code> with a
     * "where" clause is detected, an <code>IllegalArgumentException</code>
     * will be thrown.</p>
     *
     * @param e The expression for whom quantifiers should be distributed
     *          downward.  This will largely be modified in place.
     *
     * @return The root of the new expression.
     *
     * @throw IllegalArgumentException If <code>e</code> contains a
     *    <code>for all</code> expression with a "where" clause.
     */
    public static Exp applyQuantification(Exp e) {
        Map<String, Integer> quantifiedVariables =
                new HashMap<String, Integer>();

        return applyQuantification(e, quantifiedVariables);
    }

    /**
     * <p>Takes an expression and distributes its quantifiers down to the
     * variables, removing the quantifiers in the process.  (That is, in the
     * expression <code>For all x, x = y</code>, the variable <code>x</code>
     * in the equals expression would be marked internaly as a "for all"
     * variable and the expression <code>x = y</code> would be returned.)</p>
     *
     * <p>Quantified expressiong with "where" clauses will be normalized to
     * remove the where clause.  In the case of "for all" statements, the
     * where clause will be added as the antecedent to an implication, so that
     * <code>For all x, where Q(x), P(x)</code> will become <code>For all x,
     * Q(x) implies P(x)</code> and <code>There exists x, where Q(x), such that
     * P(x)</code> will become <code>There exists x such that Q(x) and P(x)
     * </code>.</p>
     *
     * @param e The expression for whom quantifiers should be distributed
     *          downward.  This will largely be modified in place.
     * @param quantifiedVariables A map of variable names that should be 
     *          considered quantified mapped to the quantifiers that apply to
     *          them. 
     *
     * @return The root of the new expression. 
     */
    private static Exp applyQuantification(Exp e,
            Map<String, Integer> quantifiedVariables) {

        Exp retval;

        if (e instanceof QuantExp) {
            QuantExp eAsQuantifier = (QuantExp) e;

            //Normalize our eAsQuantifier so that it doesn't have a "where"
            //clause by appropriately transferring the "where" clause into
            //the body using logical connectives
            if (eAsQuantifier.getWhere() != null) {
                switch (eAsQuantifier.getOperator()) {
                case QuantExp.FORALL:
                    eAsQuantifier =
                            new QuantExp(eAsQuantifier.getLocation(),
                                    eAsQuantifier.getOperator(), eAsQuantifier
                                            .getVars(), null, Exp
                                            .buildImplication(eAsQuantifier
                                                    .getWhere(), eAsQuantifier
                                                    .getBody()));
                    break;
                case QuantExp.EXISTS:
                    eAsQuantifier =
                            new QuantExp(eAsQuantifier.getLocation(),
                                    eAsQuantifier.getOperator(), eAsQuantifier
                                            .getVars(), null, Exp
                                            .buildConjunction(eAsQuantifier
                                                    .getWhere(), eAsQuantifier
                                                    .getBody()));
                    break;
                default:
                    throw new RuntimeException("Don't know how to normalize "
                            + "this kind of quantified expression.");
                }
            }

            List<MathVarDec> variableNames = eAsQuantifier.getVars();

            for (MathVarDec v : variableNames) {
                quantifiedVariables.put(v.getName().getName(), eAsQuantifier
                        .getOperator());
            }

            retval =
                    applyQuantification(eAsQuantifier.getBody(),
                            quantifiedVariables);

            for (MathVarDec v : variableNames) {
                quantifiedVariables.remove((v.getName().getName()));
            }
        }
        else {
            if (e instanceof VarExp) {
                VarExp eAsVar = (VarExp) e;
                String varName = eAsVar.getName().getName();

                if (quantifiedVariables.containsKey(varName)) {
                    eAsVar.setQuantification(quantifiedVariables.get(varName));
                }
            }
            else {
                if (e instanceof FunctionExp) {
                    FunctionExp eAsFunctionExp = (FunctionExp) e;
                    String functionName = eAsFunctionExp.getName().getName();
                    if (quantifiedVariables.containsKey(functionName)) {
                        eAsFunctionExp.setQuantification(quantifiedVariables
                                .get(functionName));
                    }
                }

                List<Exp> subExpressions = e.getSubExpressions();
                int numSubExpressions = subExpressions.size();
                Exp curSubExpression;
                for (int curIndex = 0; curIndex < numSubExpressions; curIndex++) {

                    curSubExpression = subExpressions.get(curIndex);
                    e.setSubExpression(curIndex, applyQuantification(
                            curSubExpression, quantifiedVariables));
                }
            }

            retval = e;
        }

        return retval;
    }

    public static java.util.List<String> getQuantifiedVariables(Exp e) {
        java.util.List<String> variables = new LinkedList<String>();
        getQuantifiedVariables(e, variables);
        return variables;
    }

    public static java.util.List<String> getQuantifiedVariables(Iterable<Exp> es) {

        java.util.List<String> variables = new LinkedList<String>();
        getQuantifiedVariables(es, variables);
        return variables;
    }

    public static java.util.List<String> getQuantifiedVariables(
            Iterable<Exp> es, java.util.List<String> variables) {

        for (Exp e : es) {
            getQuantifiedVariables(e, variables);
        }

        return variables;
    }

    public static void getQuantifiedVariables(Exp e,
            java.util.List<String> accumulator) {

        if (e instanceof VarExp) {
            VarExp eAsVarExp = (VarExp) e;
            if (eAsVarExp.getQuantification() != VarExp.NONE) {
                addUnique(accumulator, eAsVarExp.toString());
            }
        }
        else {
            List<Exp> subexpressions = e.getSubExpressions();
            for (Exp subexpression : subexpressions) {
                getQuantifiedVariables(subexpression, accumulator);
            }
        }
    }

    public static void addUnique(java.util.List<String> l, String e) {
        if (!l.contains(e)) {
            l.add(e);
        }
    }

    public static <T> boolean containsAny(Set<T> container, Set<T> possibilities) {
        boolean result = false;

        Iterator<T> possibilitiesIter = possibilities.iterator();
        while (!result && possibilitiesIter.hasNext()) {
            result = container.contains(possibilitiesIter.next());
        }

        return result;
    }
}