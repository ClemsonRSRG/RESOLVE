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

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PExpSubexpressionIterator;
import edu.clemson.cs.r2jt.proving.absyn.PLambda;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol;
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
    public List<TheoremCongruenceClosureImpl> m_lambdasAsTheorems;
    public HashMap<String, String> m_lambdas;

    /**
     * @param registry the Registry symbols contained in the conjunction will
     * reference. This class will add entries to the registry if needed.
     */
    public ConjunctionOfNormalizedAtomicExpressions(Registry registry) {
        m_registry = registry;
        m_exprList = new LinkedList<NormalizedAtomicExpressionMapImpl>();
        m_removedExprList = new LinkedList<NormalizedAtomicExpressionMapImpl>();
        m_lambdasAsTheorems = new ArrayList<TheoremCongruenceClosureImpl>();
        m_lambdas = new HashMap<String, String>();
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

    protected void addExpression(PExp expression, long timeToEnd) {
        m_timeToEnd = timeToEnd;
        addExpression(expression);
    }

    // Top level
    protected void addExpression(PExp expression) {
        if (m_timeToEnd > 0 && System.currentTimeMillis() > m_timeToEnd) {
            return;
        }
        String name = expression.getTopLevelOperation();

        if (expression.isEquality()) {
            int lhs = addFormula(expression.getSubExpressions().get(0));
            int rhs = addFormula(expression.getSubExpressions().get(1));
            mergeOperators(lhs, rhs);
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
                mergeOperators(m_registry.getIndexForSymbol("true"), root);
            }
        }
    }

    // add a lambda formula
    /*

    public PSymbol(MTType type, MTType typeValue, String leftPrint,
            String rightPrint, ImmutableList<PExp> arguments,
            Quantification quantification, DisplayType display)
     */
    protected int addLambda(PLambda lamb){
    //todo: check lambda list for duplicate before adding.  VC's will want to create multiple copies of the same lambda
        if (m_lambdas.containsKey(lamb.toString())) return m_registry.m_symbolToIndex.get((m_lambdas.get(lamb.toString())));
        // Make new function symbol
        String fname = "_lambda" + f_num++;
        // Make new parameters
        ArrayList<PSymbol> paramsAL = new ArrayList<PSymbol>();
        Iterator<PLambda.Parameter> pit = lamb.parameters.iterator();
        PExp[] emptyArr = new PExp[0];
        ArrayBackedImmutableList<PExp> emptyList = new ArrayBackedImmutableList<PExp>(emptyArr);
        //int pnum = 0;
        //HashMap<PExp,PExp> subsP = new HashMap<PExp, PExp>();
        //HashMap<String,String> subsS = new HashMap<String, String>();
        while(pit.hasNext()){
            PLambda.Parameter p = pit.next();
            //String pname = p.type.toString().toLowerCase() + pnum++;
            paramsAL.add(new PSymbol(p.type, p.type, p.name, p.name, new ArrayBackedImmutableList<PExp>(emptyList), PSymbol.Quantification.FOR_ALL, PSymbol.DisplayType.PREFIX));
            
        }
        ImmutableList<PExp> argList = new ArrayBackedImmutableList<PExp>(paramsAL.toArray(new PExp[paramsAL.size()]));
        PSymbol func = new PSymbol(lamb.getType(),lamb.getTypeValue(),fname,fname,argList, PSymbol.Quantification.NONE, PSymbol.DisplayType.PREFIX);
        // Enter new expression, replacing parameter name with the fresh one
        PExp body = lamb.getSubExpressions().get(0);
        PExp[] argsForEq = new PExp[2];
        argsForEq[0] = func;
        argsForEq[1] = body;
        // Equate formula with expression
        PSymbol asPsymbol = new PSymbol(m_registry.m_typeGraph.BOOLEAN, m_registry.m_typeGraph.BOOLEAN, "=", "=" , new ArrayBackedImmutableList<PExp>(argsForEq), PSymbol.Quantification.NONE, PSymbol.DisplayType.INFIX);
        m_lambdasAsTheorems.add(new TheoremCongruenceClosureImpl(m_registry.m_typeGraph,asPsymbol));
        m_lambdas.put(lamb.toString(),fname);
        // Return int rep of fname.
        return addPsymbol(func);
    }
    // adds a particular symbol to the registry
    protected int addPsymbol(PSymbol ps) {
        String name = ps.getTopLevelOperation();
        MTType type = ps.getType();

        Registry.Usage usage = Registry.Usage.SINGULAR_VARIABLE;
        if (ps.isLiteral()) {
            usage = Registry.Usage.LITERAL;
        }

        else if (ps.isFunction() || ps.getType().getClass().getSimpleName().contains("MTFunction")){
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
        if (formula instanceof PLambda){
            return addLambda((PLambda)formula);
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
        int rhs = m_registry.makeSymbol(typeOfFormula);
        atomicFormula.writeToRoot(rhs);
        m_exprList.add(indexToInsert, atomicFormula);
        return rhs;
    }

    protected void mergeOperators(int a, int b) {
        if (m_timeToEnd > 0 && System.currentTimeMillis() > m_timeToEnd) {
            return;
        }
        a = m_registry.findAndCompress(a);
        b = m_registry.findAndCompress(b);
        Stack<Integer> holdingTank = mergeOnlyArgumentOperators(a, b);
        while (holdingTank != null && !holdingTank.empty()) {
            if (m_timeToEnd > 0 && System.currentTimeMillis() > m_timeToEnd) {
                return;
            }
            int opA = m_registry.findAndCompress(holdingTank.pop());
            int opB = m_registry.findAndCompress(holdingTank.pop());
            if (opA != opB) {
                holdingTank.addAll(mergeOnlyArgumentOperators(opA, opB));
            }
        }
        //mergeArgsOfEqualityPredicateIfRootIsTrue();
    }

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
    protected Stack<Integer> mergeOnlyArgumentOperators(int a, int b) {
        if (m_timeToEnd > 0 && System.currentTimeMillis() > m_timeToEnd) {
            return null;
        }
        if (a == b) {
            return null;
        }
        /*if (a > b) {
         int temp = a;
         a = b;
         b = temp;
         }*/// this is the original way, merge toward lower valued indices.
        String aString = m_registry.getSymbolForIndex(a);
        String bString = m_registry.getSymbolForIndex(b);
        if (aString.compareTo(bString) > 0) {
            int temp = a;
            String tempS = aString;
            a = b;
            aString = bString;
            b = temp;
            bString = tempS;
        }
        // Favor retaining literals
        // THIS IS CURRENTLY ABSOLUTELY NECCESSARY.  Finding could be redone to avoid this.
        // Otherwise, proofs can fail to prove if literals are redefined.

        if(aString.equals("false") && bString.equals("true")){
            m_evaluates_to_false = true;
            return null;
        }
        // TODO: find out why when aString == bString here, a !=b.  Child probably merged.
        Registry.Usage uB = m_registry.getUsage(bString);
        Registry.Usage uA = m_registry.getUsage((aString));
        // Can't rely on literal property being set.  false is not set to be a literal.
        if(uA.equals(Registry.Usage.LITERAL) && uB.equals(Registry.Usage.LITERAL)){
            System.err.println("Literal redefinition: " + aString + a + " -> " + bString + b);
            System.err.println(m_registry.m_symbolToIndex);
            System.err.println(m_registry.m_indexToSymbol);
        }
        if (uB.equals(Registry.Usage.LITERAL)) {
            int temp = a;
            a = b;
            b = temp;
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
        if(m_evaluates_to_false)
               r += "Conjunction evaluates to false" + "\n";

        for (NormalizedAtomicExpressionMapImpl cur : m_exprList) {
            r += cur.toHumanReadableString(m_registry) + "\n";
        }
        for(TheoremCongruenceClosureImpl th : m_lambdasAsTheorems){
            r += th.m_theoremString + "\n";
        }
        return r;
    }

}
