/**
 * ConjunctionOfNormalizedAtomicExpressions.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.congruenceclassprover;

import edu.clemson.cs.r2jt.proving.absyn.*;
import edu.clemson.cs.r2jt.proving.immutableadts.ArrayBackedImmutableList;
import edu.clemson.cs.r2jt.proving.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.proving2.model.Theorem;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

import java.util.*;

/**
 * Created by mike on 4/3/2014.
 */
public class ConjunctionOfNormalizedAtomicExpressions {

    private final Registry m_registry;
    private final List<NormalizedAtomicExpressionMapImpl> m_exprList;
    protected long m_timeToEnd = -1;
    private final List<NormalizedAtomicExpressionMapImpl> m_removedExprList;
    protected boolean m_evaluates_to_false = false;
    private int f_num = 0;

    /**
     * @param registry the Registry symbols contained in the conjunction will
     * reference. This class will add entries to the registry if needed.
     */
    public ConjunctionOfNormalizedAtomicExpressions(Registry registry) {
        m_registry = registry;
        m_exprList = new LinkedList<NormalizedAtomicExpressionMapImpl>();
        m_removedExprList = new LinkedList<NormalizedAtomicExpressionMapImpl>();
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
        NormalizedAtomicExpressionMapImpl ubExpr =
                translQuery.incrementLastKnown();

        box.upperBound = m_exprList.size() - 1;
        box.lowerBound = lowerBound;
        box.currentIndex = lowerBound;
        box.directMatch = false;
    }

    protected NormalizedAtomicExpressionMapImpl getExprAtPosition(int position) {
        return m_exprList.get(position);
    }

    protected String addExpression(PExp expression, long timeToEnd) {
        m_timeToEnd = timeToEnd;
        return addExpression(expression);
    }

    // Top level
    protected String addExpression(PExp expression) {
        if (m_timeToEnd > 0 && System.currentTimeMillis() > m_timeToEnd) {
            return "";
        }
        String name = expression.getTopLevelOperation();

        if (expression.isEquality()) {
            int lhs = addFormula(expression.getSubExpressions().get(0));
            int rhs = addFormula(expression.getSubExpressions().get(1));
            return mergeOperators(lhs, rhs);
        }
        else if (name.equals("and")) {
            addExpression(expression.getSubExpressions().get(0));
            addExpression(expression.getSubExpressions().get(1));
        }
        else {
            MTType type = expression.getType();
            PSymbol asPsymbol = (PSymbol) expression;
            int intRepOfOp = addPsymbol(asPsymbol);
            int root = addFormula(expression);
            if (type.isBoolean()) {
                return mergeOperators(m_registry.getIndexForSymbol("true"),
                        root);
            }
        }
        return "";
    }

    /*

    public PSymbol(MTType type, MTType typeValue, String leftPrint,
            String rightPrint, ImmutableList<PExp> arguments,
            Quantification quantification, DisplayType display)
     */
    // Converts lambda into for all: ex:
    // lambda(x:Z).(x + k) becomes:
    // +(x,k) = _v_1
    // _lambda_1(x) = v_1
    // and returns int rep for _lambda_1
    // if an identical formula already exists, this should return the int rep for it and should not
    // create a new formula.
    protected int removeLambda(PLambda lamb) {

        // Make new function symbol
        String fname = "lambda" + f_num++;
        // Make new parameters
        ArrayList<PSymbol> paramsAL = new ArrayList<PSymbol>();
        Iterator<PLambda.Parameter> pit = lamb.parameters.iterator();
        PExp[] emptyArr = new PExp[0];
        ArrayBackedImmutableList<PExp> emptyList =
                new ArrayBackedImmutableList<PExp>(emptyArr);

        int pnum = 0;
        HashMap<PExp, PExp> quantToLit = new HashMap<PExp, PExp>();
        while (pit.hasNext()) {
            PLambda.Parameter p = pit.next();
            String pname = p.type.toString().toLowerCase() + pnum++;
            // temporary hack until I can get lambda param types substituted
            if (pname.startsWith("'d'")) {
                pname = "z" + (pnum - 1);
            }
            paramsAL
                    .add(new PSymbol(p.type, p.type, pname, pname,
                            new ArrayBackedImmutableList<PExp>(emptyList),
                            PSymbol.Quantification.FOR_ALL,
                            PSymbol.DisplayType.PREFIX));
            // map original name to new name to substitute in body
            quantToLit.put(new PSymbol(p.type, null, p.name), new PSymbol(
                    p.type, null, pname));
        }
        ImmutableList<PExp> argList =
                new ArrayBackedImmutableList<PExp>(paramsAL
                        .toArray(new PExp[paramsAL.size()]));
        PSymbol func =
                new PSymbol(lamb.getType(), lamb.getTypeValue(), fname, fname,
                        argList, PSymbol.Quantification.FOR_ALL,
                        PSymbol.DisplayType.PREFIX);
        // Enter new expression, replacing parameter name with the fresh one
        PExp body = lamb.getSubExpressions().get(0).substitute(quantToLit); // this is the body, its a single element list
        int bodyRep = addFormula(body);
        int funcRep = addFormula(func);
        mergeOperators(bodyRep, funcRep);
        mergeMatchingLambdas();
        return m_registry.getIndexForSymbol(fname);

    }

    // could be optimized with bin search if expressions were in alpha order.
    protected String mergeMatchingLambdas() {
        int i = 0;
        String rString = "";
        loopStart: while (i < m_exprList.size()) {
            int p0_i = m_exprList.get(i).readPosition(0);
            String p0_i_name = m_registry.getSymbolForIndex(p0_i);
            if (!p0_i_name.startsWith("lambda")) {
                ++i;
                continue;
            }
            int pLast = m_exprList.get(i).readRoot();
            int j = i + 1;
            while (j < m_exprList.size()) {
                int p0_j = m_exprList.get(j).readPosition(0);
                String p0_j_name = m_registry.getSymbolForIndex(p0_j);
                // compare rest of expressions, if the same, merge func names
                if (p0_j_name.startsWith("lambda")
                        && m_exprList.get(j).readRoot() == pLast
                        && !p0_i_name.equals(p0_j_name)) {
                    // suppose lambda1(k) = c0, lambda2(j) = c0
                    // merge makes this: lambda1(k) = c0, lambda1(j) = c0;
                    rString += mergeOperators(p0_i, p0_j);
                    //System.err.println("mergeMatchingLambdas: merging " + p0_i_name + " " + p0_j_name);
                    i = 0;
                    continue loopStart;
                }
                ++j;
            }
            ++i;
        }
        return rString;
    }

    protected int addPAlternative(PExp formula) {
        return -1;
    }

    // adds a particular symbol to the registry
    protected int addPsymbol(PSymbol ps) {
        String name = ps.getTopLevelOperation();
        MTType type = ps.getTypeValue();
        if (type == null || ps.isFunction()) {
            type = ps.getType();
        }
        if (ps.name.equals("Az")) {
            int j = 0;
        }
        Registry.Usage usage = Registry.Usage.SINGULAR_VARIABLE;
        if (ps.isLiteral()) {
            usage = Registry.Usage.LITERAL;
        }

        else if (name.startsWith("lambda")
                || ps.isFunction()
                || ps.getType().getClass().getSimpleName().contains(
                        "MTFunction")) {
            if (ps.quantification.equals(PSymbol.Quantification.FOR_ALL)) {
                usage = Registry.Usage.HASARGS_FORALL;
            }
            else {
                usage = Registry.Usage.HASARGS_SINGULAR;
            }
        }
        else if (ps.quantification.equals(PSymbol.Quantification.FOR_ALL)) {
            usage = Registry.Usage.FORALL;
        }
        return m_registry.addSymbol(name, type, usage);
    }

    /* experimentally handling =
     i.e.: (|?S| = 0) = (?S = Empty_String))
     is broken down by addExpression so (|?S| = 0) is an argument
     should return int for true if known to be equal, otherwise return root representative. 
     */
    protected int addFormula(PExp formula) {
        if (formula.isEquality()) {
            int lhs = addFormula(formula.getSubExpressions().get(0));
            PExp r = formula.getSubExpressions().get(1);
            int rhs = addFormula(r);
            lhs = m_registry.findAndCompress(lhs);
            rhs = m_registry.findAndCompress(rhs);
            // This prevents matching of (i=i)=true, which is not built in
            /*if (lhs == rhs) {
                return m_registry.getIndexForSymbol("true");
            }
            else {*/
            // insert =(lhs,rhs) = someNewRoot
            int questEq = m_registry.getIndexForSymbol("=");
            NormalizedAtomicExpressionMapImpl pred =
                    new NormalizedAtomicExpressionMapImpl();
            pred.writeOnto(questEq, 0);
            pred.writeOnto(lhs, 1);
            pred.writeOnto(rhs, 2);
            return addAtomicFormula(pred);
            // }
        }
        PSymbol asPsymbol;
        if (formula instanceof PLambda) {
            return removeLambda((PLambda) formula);
        }
        else if (formula instanceof PAlternatives) {
            return addPAlternative(formula);
        }
        else
            asPsymbol = (PSymbol) formula;
        int intRepOfOp = addPsymbol(asPsymbol);
        // base case
        if (formula.isVariable()) {
            return intRepOfOp;
        }

        NormalizedAtomicExpressionMapImpl newExpr =
                new NormalizedAtomicExpressionMapImpl();
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
        // no such formula exists
        int indexToInsert = -(posIfFound + 1);
        MTType typeOfFormula =
                m_registry.getTypeByIndex(atomicFormula.readPosition(0));
        String symName =
                m_registry.getSymbolForIndex(atomicFormula.readPosition(0));
        assert typeOfFormula != null : symName + " has null type";
        // if any of the symbols in atomicFormula are variables (FORALL) make created symbol a variable
        boolean isVar = false;
        for (Integer is : atomicFormula.getKeys()) {
            String s = m_registry.getSymbolForIndex(is);
            if (s.startsWith("¢v")) {
                isVar = true;
                break;
            }
            Registry.Usage us = m_registry.getUsage(s);
            if (us == Registry.Usage.FORALL
                    || us == Registry.Usage.HASARGS_FORALL) {
                isVar = true;
                break;
            }
        }
        int rhs = m_registry.makeSymbol(typeOfFormula, isVar);
        atomicFormula.writeToRoot(rhs);
        m_exprList.add(indexToInsert, atomicFormula);
        return rhs;
    }

    protected String mergeOperators(int a, int b) {
        String rString = "";
        if (m_timeToEnd > 0 && System.currentTimeMillis() > m_timeToEnd) {
            return rString;
        }
        a = m_registry.findAndCompress(a);
        b = m_registry.findAndCompress(b);
        if (a == b)
            return "";
        Stack<Integer> holdingTank = new Stack<Integer>();
        holdingTank.push(a);
        holdingTank.push(b);

        while (holdingTank != null && !holdingTank.empty()) {
            if (m_timeToEnd > 0 && System.currentTimeMillis() > m_timeToEnd) {
                return rString;
            }
            int opA = m_registry.findAndCompress(holdingTank.pop());
            int opB = m_registry.findAndCompress(holdingTank.pop());
            // Rules that determine which symbol becomes root go here
            if (opA == opB)
                continue;
            String aString = m_registry.getSymbolForIndex(opA);
            String bString = m_registry.getSymbolForIndex(opB);

            if (aString.compareTo(bString) > 0) {
                int temp = opA;
                opA = opB;
                opB = temp;
            }

            // Favor retaining literals
            // THIS IS CURRENTLY ABSOLUTELY NECESSARY.  Finding could be redone to avoid this.
            // Otherwise, proofs can fail to prove if literals are redefined.
            aString = m_registry.getSymbolForIndex(opA);
            bString = m_registry.getSymbolForIndex(opB);
            Registry.Usage uB = m_registry.getUsage(bString);
            Registry.Usage uA = m_registry.getUsage((aString));
            // puts created in b
            if (uA.equals(Registry.Usage.CREATED)
                    && !uB.equals(Registry.Usage.CREATED)) {
                int temp = opA;
                opA = opB;
                opB = temp;
            }
            // Can't rely on literal property being set.  false is not set to be a literal.
            else if (uA.equals(Registry.Usage.LITERAL)
                    && uB.equals(Registry.Usage.LITERAL)) {
                System.err.println("Literal redefinition: " + aString + "."
                        + opA + " -> " + bString + "." + opB);
                //System.err.println(m_registry.m_symbolToIndex);
                //System.err.println(m_registry.m_indexToSymbol);
            }
            else if (uB.equals(Registry.Usage.LITERAL)) {
                int temp = opA;
                opA = opB;
                opB = temp;
            }
            aString = m_registry.getSymbolForIndex(opA);
            bString = m_registry.getSymbolForIndex(opB);
            uA = m_registry.getUsage((aString));
            uB = m_registry.getUsage(bString);
            // prevent constant replacement by variable
            if (uA != uB) {
                boolean aIsVar =
                        (uA == Registry.Usage.FORALL
                                || uA == Registry.Usage.HASARGS_FORALL || aString
                                .startsWith("¢v"));
                boolean bIsVar =
                        (uB == Registry.Usage.FORALL
                                || uB == Registry.Usage.HASARGS_FORALL || bString
                                .startsWith("¢v"));
                if ((aIsVar || bIsVar) && !(aIsVar == bIsVar) && (aIsVar)) {
                    int temp = opA;
                    opA = opB;
                    opB = temp;
                }
            }
            aString = m_registry.getSymbolForIndex(opA);
            bString = m_registry.getSymbolForIndex(opB);
            // prefer true or false
            if (bString.equals("true") || bString.equals("false")) {
                int temp = opA;
                opA = opB;
                opB = temp;
            }

            if (aString.equals("false") && bString.equals("true")
                    || (aString.equals("true") && bString.equals("false"))) {
                m_evaluates_to_false = true;
            }

            rString +=
                    m_registry.getSymbolForIndex(opA) + "/"
                            + m_registry.getSymbolForIndex(opB) + ",";
            Stack<Integer> mResult = mergeOnlyArgumentOperators(opA, opB);

            if (mResult != null)
                holdingTank.addAll(mResult);

        }
        //mergeArgsOfEqualityPredicateIfRootIsTrue();
        rString += mergeMatchingLambdas();
        return rString;
    }

    // This has been replaced by a theorem in my Boolean_Theory - mike
    // look for =(x,y)=true in list.  If found call merge(x,y).
    //  = will always be at top of list.
    // These expression will not be removed by this function,
    // but can be removed by merge().
    protected void mergeArgsOfEqualityPredicateIfRootIsTrue() {
        if (m_timeToEnd > 0 && System.currentTimeMillis() > m_timeToEnd) {
            return;
        }
        // loop until end, function op is not =, or =(x,y)=true
        // when found do merge, start again.
        int eqQ = m_registry.getIndexForSymbol("=");
        for (int i = 0; i < m_exprList.size(); ++i) {
            NormalizedAtomicExpressionMapImpl cur = m_exprList.get(i);
            int f = cur.readPosition(0);
            if (f != eqQ) {
                return;
            }
            int t = m_registry.getIndexForSymbol("true");
            int root = cur.readRoot();
            int op1 = cur.readPosition(1);
            int op2 = cur.readPosition(2);
            if (root == t && op1 != op2) {
                mergeOperators(cur.readPosition(1), cur.readPosition(2));
                // mergeOperators will do any other merges that arise.
                i = 0;
            }
        }
    }

    // Return list of modified predicates by their position. Only these can cause new merges.
    // b is replaced by a
    protected Stack<Integer> mergeOnlyArgumentOperators(int a, int b) {
        if (m_timeToEnd > 0 && System.currentTimeMillis() > m_timeToEnd) {
            return null;
        }

        Iterator<NormalizedAtomicExpressionMapImpl> it = m_exprList.iterator();
        Stack<NormalizedAtomicExpressionMapImpl> modifiedEntries =
                new Stack<NormalizedAtomicExpressionMapImpl>();
        Stack<Integer> coincidentalMergeHoldingTank = new Stack<Integer>();
        while (it.hasNext()) {
            NormalizedAtomicExpressionMapImpl curr = it.next();
            if (curr.replaceOperator(b, a)) {
                modifiedEntries.push(curr);
                it.remove();
            }
        }
        while (!modifiedEntries.empty()) {
            int indexToInsert =
                    Collections
                            .binarySearch(m_exprList, modifiedEntries.peek());
            // If the modified one is already there, don't put it back
            if (indexToInsert < 0) {
                indexToInsert = -(indexToInsert + 1);
                m_exprList.add(indexToInsert, modifiedEntries.pop());
            }
            else {
                // the expr is in the list, but are the roots different?
                int rootA = modifiedEntries.pop().readRoot();
                int rootB = m_exprList.get(indexToInsert).readRoot();
                if (rootA != rootB) {
                    coincidentalMergeHoldingTank.push(rootA);
                    coincidentalMergeHoldingTank.push(rootB);
                }
            }
        }
        //System.err.println(m_registry.getSymbolForIndex(a) + "/" + m_registry.getSymbolForIndex(b));
        m_registry.substitute(a, b);
        return coincidentalMergeHoldingTank;
    }

    protected Map<String, Integer> getSymbolProximity(Set<String> symbols) {
        boolean done = false;
        Map<Integer, Integer> relatedKeys = new HashMap<Integer, Integer>();
        for (String s : symbols) {
            relatedKeys.put(m_registry.getIndexForSymbol(s), 0);
        }
        int closeness = 0;
        Set<Integer> relatedSet = new HashSet<Integer>();

        while (!done) {
            closeness++;
            int startSize = relatedKeys.size();
            HashMap<Integer, Integer> relatedKeys2 =
                    new HashMap<Integer, Integer>();
            for (int i = 0; i < m_exprList.size(); ++i) {
                if (relatedSet.contains(i))
                    continue;
                Set<Integer> intersection =
                        new HashSet<Integer>(m_exprList.get(i).getKeys());
                intersection.retainAll(relatedKeys.keySet());
                if (!intersection.isEmpty()) {
                    relatedSet.add(i);
                    for (Integer k : m_exprList.get(i).getKeys()) {
                        if (!relatedKeys.containsKey(k)) {
                            relatedKeys2.put(k, closeness);
                        }
                    }
                }
            }
            relatedKeys.putAll(relatedKeys2);
            if (startSize == relatedKeys.size()) {
                done = true;
            }
        }

        HashSet<Integer> toRemove = new HashSet<Integer>();
        for (int i = 0; i < m_exprList.size(); ++i) {
            toRemove.add(i);
        }
        toRemove.removeAll(relatedSet);

        for (Integer i : toRemove) {
            m_removedExprList.add(m_exprList.get(i));
        }

        for (NormalizedAtomicExpressionMapImpl r : m_removedExprList) {
            m_exprList.remove(r);
        }

        Map<String, Integer> rMap =
                new HashMap<String, Integer>(relatedKeys.size());
        for (Integer i : relatedKeys.keySet()) {
            rMap.put(m_registry.getSymbolForIndex(i), relatedKeys.get(i));
        }
        return rMap;
    }

    @Override
    public String toString() {
        String r = "";
        if (m_evaluates_to_false)
            r += "Conjunction evaluates to false" + "\n";

        for (NormalizedAtomicExpressionMapImpl cur : m_exprList) {
            r += cur.toHumanReadableString(m_registry) + "\n";
        }
        return r;
    }

}
