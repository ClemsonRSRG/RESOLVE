/**
 * ConjunctionOfNormalizedAtomicExpressions.java
 * --------------------------------- Copyright (c) 2014 RESOLVE Software
 * Research Group School of Computing Clemson University All rights reserved.
 * --------------------------------- This file is subject to the terms and
 * conditions defined in file 'LICENSE.txt', which is part of this source code
 * package.
 */
package edu.clemson.cs.r2jt.congruenceclassprover;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PExpSubexpressionIterator;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;

import java.util.*;

/**
 * Created by mike on 4/3/2014.
 */
public class ConjunctionOfNormalizedAtomicExpressions {

    private Registry m_registry;
    private List<NormalizedAtomicExpressionMapImpl> m_exprList;

    /**
     * @param registry the Registry symbols contained in the conjunction will
     * reference. This class will add entries to the registry if needed.
     */
    public ConjunctionOfNormalizedAtomicExpressions(Registry registry) {
        m_registry = registry;
        m_exprList = new LinkedList<NormalizedAtomicExpressionMapImpl>();
    }

    protected int size() {
        return m_exprList.size();
    }

    protected void findNAE(SearchBox box) {
        NormalizedAtomicExpressionMapImpl translQuery = box.m_translated;
        int lowerBound = Collections.binarySearch(m_exprList, translQuery);
        // if it exists in the list...
        if (lowerBound >= 0 && lowerBound < m_exprList.size()) {
            box.lowerBound = lowerBound;
            box.upperBound = lowerBound;
            box.currentIndex = lowerBound;
            box.directMatch = true;
            return;
        }
        lowerBound = -lowerBound - 1;
        NormalizedAtomicExpressionMapImpl ubExpr = translQuery.incrementLastKnown();
        int upperBound = Collections.binarySearch(m_exprList, ubExpr);
        if (!(upperBound >= 0 && upperBound < m_exprList.size())) {
            if(upperBound >= m_exprList.size()) upperBound = m_exprList.size()-1;
            else if (upperBound < 0) {
                upperBound = - upperBound -1;
                if(upperBound >= m_exprList.size()) upperBound = m_exprList.size()-1;
            }       
        }
        box.upperBound = upperBound;
        box.lowerBound = lowerBound;
        box.currentIndex = lowerBound;
        box.directMatch = false;
        return;
    }

    private String fromOperatorIndicesToAtomString(int[] atom) {
        String r = m_registry.getSymbolForIndex(atom[0]);
        if (atom.length > 2) {
            r += "(";
            for (int i = 1; i < atom.length - 1; ++i) {
                r += m_registry.getSymbolForIndex(atom[i]) + ",";
            }
            r = r.substring(0, r.length() - 1);
            r += ")";
        }
        r += "=" + m_registry.getSymbolForIndex(atom[atom.length - 1]);
        return r;
    }

    protected NormalizedAtomicExpressionMapImpl getExprAtPosition(int position) {
        return m_exprList.get(position);
    }

    protected void addExpression(PExp expression) {
        String name = expression.getTopLevelOperation();
        MTType type = expression.getType();
        PSymbol asPsymbol = (PSymbol) expression;
        int intRepOfOp = addPsymbol(asPsymbol);
        if (expression.isEquality()) {
            int lhs = addFormula(expression.getSubExpressions().get(0));
            int rhs = addFormula(expression.getSubExpressions().get(1));
            mergeOperators(lhs, rhs);
        } else if (name.equals("and")) {
            addExpression(expression.getSubExpressions().get(0));
            addExpression(expression.getSubExpressions().get(1));
        } else {
            int root = addFormula(expression);
            if (type.isBoolean()) {
                mergeOperators(m_registry.getIndexForSymbol("true"), root);
            }
        }
    }

    protected int addPsymbol(PSymbol ps) {
        String name = ps.getTopLevelOperation();
        MTType type = ps.getType();
        Registry.Usage usage = Registry.Usage.SINGULAR_VARIABLE;
        if (ps.isLiteral()) {
            usage = Registry.Usage.LITERAL;
        } else if (ps.isFunction()) {
            usage = Registry.Usage.LITERAL; // making function names global in context of theorems and vc's
        } else if (ps.quantification.equals(PSymbol.Quantification.FOR_ALL)) {
            usage = Registry.Usage.FORALL;
        }
        return m_registry.addSymbol(name, type, usage);
    }

    /**
     * @param formula a formula that should not contain = or and. Predicate
     * symbols are treated as any other function symbol here.
     * @return current index in list of expressions.
     */
    protected int addFormula(PExp formula) {
        PSymbol asPsymbol = (PSymbol) formula;
        int intRepOfOp = addPsymbol(asPsymbol);
        // base case
        if (formula.isVariable()) {
            return intRepOfOp;
        }

        NormalizedAtomicExpressionMapImpl newExpr
                = new NormalizedAtomicExpressionMapImpl();
        newExpr.writeOnto(intRepOfOp, 0);
        int pos = 0;
        PExpSubexpressionIterator it = formula.getSubExpressionIterator();
        while (it.hasNext()) {
            PExp p = it.next();
            pos++;
            int root = addFormula(p);
            assert newExpr != null;
            newExpr.writeOnto(root, pos);
        }
        return addAtomicFormula(newExpr);
    }

    /**
     * @param atomicFormula one sided expression. (= new root) is appended and
     * expression is inserted if no match of the side is found. Otherwise
     * current root is returned.
     * @return current integer value of root symbol that represents the input.
     */
    private int addAtomicFormula(NormalizedAtomicExpressionMapImpl atomicFormula) {
        int posIfFound = Collections.binarySearch(m_exprList, atomicFormula);
        if (posIfFound >= 0) {
            return m_exprList.get(posIfFound).readRoot();
        }
        // no such formula exists. Note that
        int indexToInsert = -(posIfFound + 1);
        MTType typeOfFormula
                = m_registry.getTypeByIndex(atomicFormula.readPosition(0));
        int rhs = m_registry.makeSymbol(typeOfFormula);
        atomicFormula.writeToRoot(rhs);
        m_exprList.add(indexToInsert, atomicFormula);
        return rhs;
    }

    protected void mergeOperators(int a, int b) {
        Stack<Integer> holdingTank = mergeOnlyArgumentOperators(a, b);
        while (holdingTank != null && !holdingTank.empty()) {
            int opA = m_registry.findAndCompress(holdingTank.pop());
            int opB = m_registry.findAndCompress(holdingTank.pop());
            if (opA != opB) {
                holdingTank.addAll(mergeOnlyArgumentOperators(opA, opB));
            }
        }

    }

    // Return list of modified predicates by their position. Only these can cause new merges.
    protected Stack<Integer> mergeOnlyArgumentOperators(int a, int b) {
        if (a == b) {
            return null;
        }
        if (a > b) {
            int temp = a;
            a = b;
            b = temp;
        }
        Iterator<NormalizedAtomicExpressionMapImpl> it = m_exprList.iterator();
        Stack<NormalizedAtomicExpressionMapImpl> modifiedEntries
                = new Stack<NormalizedAtomicExpressionMapImpl>();
        Stack<Integer> coincidentalMergeHoldingTank = new Stack<Integer>();
        while (it.hasNext()) {
            NormalizedAtomicExpressionMapImpl curr = it.next();
            if (curr.replaceOperator(b, a)) {
                modifiedEntries.push(curr);
                it.remove();
            }
        }
        while (!modifiedEntries.empty()) {
            int indexToInsert
                    = Collections
                    .binarySearch(m_exprList, modifiedEntries.peek());
            // If the modified one is already there, don't put it back
            if (indexToInsert < 0) {
                indexToInsert = -(indexToInsert + 1);
                m_exprList.add(indexToInsert, modifiedEntries.pop());
            } else {
                // the expr is in the list, but are the roots different?
                int rootA = modifiedEntries.pop().readRoot();
                int rootB = m_exprList.get(indexToInsert).readRoot();
                if (rootA != rootB) {
                    coincidentalMergeHoldingTank.push(rootA);
                    coincidentalMergeHoldingTank.push(rootB);
                }
            }
        }
        m_registry.substitute(a, b);
        return coincidentalMergeHoldingTank;
    }

    @Override
    public String toString() {
        String r = "";
        for (NormalizedAtomicExpressionMapImpl cur : m_exprList) {
            r += cur.toHumanReadableString(m_registry) + "\n";
        }
        return r;
    }
}
