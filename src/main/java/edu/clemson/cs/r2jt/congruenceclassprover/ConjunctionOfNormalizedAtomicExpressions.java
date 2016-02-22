/**
 * ConjunctionOfNormalizedAtomicExpressions.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.congruenceclassprover;

import edu.clemson.cs.r2jt.rewriteprover.absyn.*;
import edu.clemson.cs.r2jt.typeandpopulate.MTFunction;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;

import java.util.*;

/**
 * Created by mike on 4/3/2014.
 */
public class ConjunctionOfNormalizedAtomicExpressions {

    private final Registry m_registry;
    protected final Set<NormalizedAtomicExpression> m_expSet;
    protected long m_timeToEnd = -1;
    protected boolean m_evaluates_to_false = false;
    private int f_num = 0;
    private String m_current_justification = "";
    protected final Map<Integer, Set<NormalizedAtomicExpression>> m_useMap;
    protected final VerificationConditionCongruenceClosureImpl m_VC;

    /**
     * @param registry the Registry symbols contained in the conjunction will
     *                 reference. This class will add entries to the registry if needed.
     */
    public ConjunctionOfNormalizedAtomicExpressions(Registry registry,
            VerificationConditionCongruenceClosureImpl vc) {
        m_registry = registry;
        m_expSet = new HashSet<NormalizedAtomicExpression>(2048, .5f);
        m_useMap =
                new HashMap<Integer, Set<NormalizedAtomicExpression>>(2048, .5f);
        m_VC = vc; // null if this is a theorem
    }

    protected int size() {
        return m_expSet.size();
    }

    protected void clear() {
        m_expSet.clear();
    }

    protected String addExpressionAndTrackChanges(PExp expression,
            long timeToEnd, String justification) {
        m_timeToEnd = timeToEnd;
        m_timeToEnd = Long.MAX_VALUE;
        m_current_justification = justification;
        String rString = "";
        rString += addExpression(expression);
        m_current_justification = "";
        return rString;
    }

    protected PExp find(PExp exp){
        if(exp.getSubExpressions().size()==0){
            String s = m_registry.getRootSymbolForSymbol(exp.getTopLevelOperation());
            if(s.equals("")) {
                return exp;
            }
            else{
                 return  new PSymbol(exp.getType(),exp.getTypeValue(),s);
            }
        }
        PExpSubexpressionIterator it = exp.getSubExpressionIterator();
        ArrayList<PExp> args = new ArrayList<PExp>();
        boolean irreducable = false;
        while(it.hasNext()){
            PExp cur = it.next();
            PExp fcur = find(cur);
            if(fcur.getSubExpressions().size()>0 ||
                    !m_registry.m_symbolToIndex.containsKey(fcur.getTopLevelOperation()))
                irreducable = true;
            args.add(fcur);
        }
        String op = m_registry.getRootSymbolForSymbol(exp.getTopLevelOperation());
        if(!irreducable && !op.equals("")){
            int[] ia;
            ia = new int[args.size()+1];
            ia[0] = m_registry.getIndexForSymbol(op);
            for(int i = 1; i < ia.length; ++i){
                ia[i] = m_registry.getIndexForSymbol(args.get(i-1).getTopLevelOperation());
            }
            NormalizedAtomicExpression na = new NormalizedAtomicExpression(m_registry,ia);
            if(m_registry.m_exprRootMap.containsKey(na)){
                int r = m_registry.m_exprRootMap.get(na);
                String rs = m_registry.getSymbolForIndex(r);
                return new PSymbol(m_registry.getTypeByIndex(r),null,rs);
            }
            else return new PSymbol(exp.getType(), exp.getTypeValue(), exp.getTopLevelOperation(), args);
        }else {
            return new PSymbol(exp.getType(), exp.getTypeValue(), exp.getTopLevelOperation(), args);
        }
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
            String r = "";
            r += addExpression(expression.getSubExpressions().get(0));
            r += addExpression(expression.getSubExpressions().get(1));
            return r;
        }

        else {
            MTType type = expression.getType();
            PSymbol asPsymbol = (PSymbol) expression;
            int root = addFormula(expression);
            if (m_evaluates_to_false)
                return "";
            if (type.isBoolean()) {
                return mergeOperators(m_registry.getIndexForSymbol("true"),
                        root);
            }
        }
        return "";
    }

    // adds a particular symbol to the registry
    protected int addPsymbol(PSymbol ps) {
        String name = ps.getTopLevelOperation();
        if (m_registry.m_symbolToIndex.containsKey(name))
            return m_registry.m_symbolToIndex.get(name);
        MTType type = ps.getType();
        Registry.Usage usage = Registry.Usage.SINGULAR_VARIABLE;
        if (ps.isLiteral()) {
            usage = Registry.Usage.LITERAL;
        }
        else if (ps.isFunction()
                || ps.getType().getClass().getSimpleName().equals("MTFunction")) {
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
        // The type stored with expressions is actually the range type
        // Ex: (S = T):B.
        // However, I need to store types for functions/relations.
        // Building these here.
        // It would be far better to handle this upstream.
        // Currently PExps from theorems have correct type set already
        if (ps.getSubExpressions().size() > 0) {
            List<MTType> paramList = new ArrayList<MTType>();
            for (PExp pParam : ps.getSubExpressions()) {
                paramList.add(pParam.getType());
            }
            type = new MTFunction(m_registry.m_typeGraph, type, paramList);
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
            NormalizedAtomicExpression pred =
                    new NormalizedAtomicExpression(m_registry, new int[] {
                            questEq, lhs, rhs });
            return addAtomicFormula(pred);
            // }
        }
        PSymbol asPsymbol;
        if (!(formula instanceof PSymbol)) {
            System.err.println("unhandled PExp: " + formula.toString());
            throw new RuntimeException();

        }
        else
            asPsymbol = (PSymbol) formula;
        int intRepOfOp = addPsymbol(asPsymbol);
        // base case
        if (formula.isVariable()) {
            return intRepOfOp;
        }

        int[] ne = new int[formula.getSubExpressions().size() + 1];
        ne[0] = intRepOfOp;
        int pos = 1;
        PExpSubexpressionIterator it = formula.getSubExpressionIterator();
        while (it.hasNext()) {
            PExp p = it.next();
            int root = addFormula(p);
            ne[pos++] = root;
        }
        NormalizedAtomicExpression newExpr =
                new NormalizedAtomicExpression(m_registry, ne);
        if (m_evaluates_to_false) {
            return -1;
        }
        newExpr = newExpr.rootOps();
        return addAtomicFormula(newExpr);
    }

    /**
     * @param atomicFormula one sided expression. (= new root) is appended and
     *                      expression is inserted if no match of the side is found. Otherwise
     *                      current root is returned.
     * @return current integer value of root symbol that represents the input.
     */
    private int addAtomicFormula(NormalizedAtomicExpression atomicFormula) {
        // Return root if atomic formula is present
        if (m_expSet.contains(atomicFormula))
            return m_registry.m_exprRootMap.get(atomicFormula);
        // no such formula exists
        MTType typeOfFormula =
                m_registry.getTypeByIndex(atomicFormula.readPosition(0));
        // this is the full type and is necessarily a function type

        MTType rangeType = ((MTFunction) typeOfFormula).getRange();
        String symName =
                m_registry.getSymbolForIndex(atomicFormula.readPosition(0));
        assert rangeType != null : symName + " has null type";
        // if any of the symbols in atomicFormula are variables (FORALL) make created symbol a variable
        boolean isVar = false;
        if (atomicFormula.hasVarOps()) {
            isVar = true;
        }
        int rhs = m_registry.makeSymbol(rangeType, isVar);
        atomicFormula.writeToRoot(rhs);
        Stack<Integer> hTank = new Stack<Integer>();
        applyBuiltInLogic(atomicFormula, hTank);
        addExprToSet(atomicFormula);
        while (!hTank.isEmpty()) {
            mergeOperators(hTank.pop(), hTank.pop());
        }
        return m_registry.findAndCompress(rhs);

    }

    protected String mergeOperators(int a, int b) {
        int t = m_registry.getIndexForSymbol("true");
        int f = m_registry.getIndexForSymbol("false");

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
            int opB = m_registry.findAndCompress(holdingTank.pop());
            int opA = m_registry.findAndCompress(holdingTank.pop());
            if (opA == opB)
                continue;
            // Want to replace quantified vars with constant if it is equal to the constant
            int keeper = chooseSymbolToKeep(opA, opB);
            if (keeper == opB) {
                int temp = opA;
                opA = opB;
                opB = temp;
            }
            if ((opA == t && opB == f)) {
                m_evaluates_to_false = true;
                return "contradiction detected " + rString;
            }
            rString +=
                    m_registry.getSymbolForIndex(opA) + "/"
                            + m_registry.getSymbolForIndex(opB) + ",";
            Stack<Integer> mResult = mergeOnlyArgumentOperators(opA, opB);
            String bstring = m_registry.getSymbolForIndex(opB);
            if(m_VC!= null && m_VC.m_goal.contains(bstring)){
                m_VC.m_goal.remove(bstring);
                m_VC.m_goal.add(m_registry.getSymbolForIndex(opA));
            }
            m_registry.substitute(opA, opB);
            if (mResult != null)
                holdingTank.addAll(mResult);

        }
        return rString;
    }

    // need to choose literals over vars for theorem matching purposes
    // i.e. the theorem expression should keep the literals
    protected int chooseSymbolToKeep(int a, int b) {
        String s = m_registry.m_indexToSymbol.get(a);
        if (s.contains("¢")) {
            if (!m_registry.m_indexToSymbol.get(b).contains("¢"))
                return b; // a is created, b is not
            else
                return a < b ? a : b; // a is created, b is created
        }
        return a < b ? a : b;
    }

    private void addMapUse(int symk, NormalizedAtomicExpression nae) {
        if (m_useMap.containsKey(symk)) {
            m_useMap.get(symk).add(nae);
        }
        else {
            Set<NormalizedAtomicExpression> iUses =
                    new HashSet<NormalizedAtomicExpression>();
            iUses.add(nae);
            m_useMap.put(symk, iUses);
        }
    }

    private void addExprToSet(NormalizedAtomicExpression nae) {
        for (int i : nae.getOpIds()) {
            addMapUse(i, nae);
        }
        int root = nae.readRoot();
        assert root >= 0 : "adding unrooted expression to conj";
        addMapUse(root, nae);
        m_expSet.add(nae);
    }

    private void removeMapUse(int symK, NormalizedAtomicExpression nae) {
        if (m_useMap.containsKey(symK)) {
            m_useMap.get(symK).remove(nae);
        }
    }

    private void removeExprFromSet(NormalizedAtomicExpression nae) {
        for (int i : nae.getOpIds()) {
            removeMapUse(i, nae);
        }
        removeMapUse(nae.readRoot(), nae);
        m_registry.m_exprRootMap.remove(nae);
        m_expSet.remove(nae);
    }

    private void applyBuiltInLogic(NormalizedAtomicExpression nm,
            Stack<Integer> tank) {
        int arity = nm.getArity();
        if (arity < 1 || 2 < arity)
            return;
        String op = nm.readSymbol(0);
        int arg1 = nm.readPosition(1);
        int rhs = nm.readRoot();
        if (rhs < 0)
            return;
        int tr = m_registry.getIndexForSymbol("true");
        int fl = m_registry.getIndexForSymbol("false");
        // =,true,false,not.  recorded first in reg. logic relation args (and, or, =) are ordered.

        // arity 1 guard: return if all constant or all vars
        if (arity == 1 && (arg1 == tr || arg1 == fl)
                && (rhs == tr || rhs == fl))
            return;
        if (arity == 1 && !(arg1 == tr || arg1 == fl)
                && !(rhs == tr || rhs == fl))
            return;
        if (op.equals("not")) {
            // constant rhs
            if (rhs == tr) {
                // not p = true, false/p
                tank.push(fl);
                tank.push(arg1);
                return;
            }
            if (rhs == fl) {
                // not p = false, true/p
                tank.push(tr);
                tank.push(arg1);
                return;
            }
            // constant arg
            if (arg1 == tr) {
                // not(tr) = p, false/p
                tank.push(fl);
                tank.push(rhs);
                return;
            }
            if (arg1 == fl) {
                // not(fl) = p, true/p
                tank.push(tr);
                tank.push(rhs);
                return;
            }
        }
        if (arity < 2)
            return;
        int arg2 = nm.readPosition(2);
        // arity 2 guard: return if all constant
        if ((arg1 == tr || arg1 == fl) && (arg2 == tr || arg2 == fl)
                && (rhs == tr || rhs == fl))
            return;
        // guard: return if all var and op is not equals
        if (!op.equals("=") && arg1 != tr && arg1 != fl
                && (arg2 != tr && arg2 != fl) && (rhs != tr && rhs != fl))
            return;
        // rules for and
        if (op.equals("and")) {
            // constant rhs
            if (rhs == tr) {
                // (p and q) = t |= t/p, t/q
                if (tr != arg1) {
                    tank.push(tr);
                    tank.push(arg1);
                }
                if (tr != arg2) {
                    tank.push(tr);
                    tank.push(arg2);
                }
                return;
            }
            // constant t arg
            if (arg1 == tr) {
                // (t and p) = q |= p/q
                if (rhs != arg2) {
                    tank.push(rhs);
                    tank.push(arg2);
                }
                return;
            }
            // constant f arg
            if (arg1 == fl) {
                // (f and p) = q |= f/q
                if (fl != rhs) {
                    tank.push(fl);
                    tank.push(rhs);
                }
                return;
            }
            // all vars
            if (arg1 == arg2) {
                // (p = p) = q |= t/q
                if (tr != rhs) {
                    tank.push(tr);
                    tank.push(rhs);
                }
                return;
            }
            return;
        }
        if (op.equals("or")) {
            // constant f rhs
            if (rhs == fl) {
                // p or q = f |= f/p/q
                if (fl != arg1) {
                    tank.push(fl);
                    tank.push(arg1);
                }
                if (fl != arg2) {
                    tank.push(fl);
                    tank.push(arg2);
                }
                return;
            }
            // rhs = some goal
            if(m_VC != null && m_VC.m_goal.contains(m_registry.getSymbolForIndex(rhs))){
                m_VC.addGoal(m_registry.getSymbolForIndex(arg1));
                m_VC.addGoal(m_registry.getSymbolForIndex(arg2));
                return;
            }
            // constant t arg
            if (arg1 == tr) {
                // (t or p) = q |= t/q
                if (tr != rhs) {
                    tank.push(tr);
                    tank.push(rhs);
                }
                return;
            }
            // constant f arg
            if (arg1 == fl) {
                // (f or p) = q |= p/q
                if (arg2 != rhs) {
                    tank.push(arg2);
                    tank.push(rhs);
                }
                return;
            }
            // all vars.
            // p or not p = q := t/q
            return;
        }
        if (op.equals("=")) {
            // constant t rhs
            if (rhs == tr) {
                // (p = q) = t |= p/q
                if (arg1 != arg2) {
                    tank.push(arg1);
                    tank.push(arg2);
                }
                return;
            }
            // constant f rhs
            if (rhs == fl) {
                // constant t arg
                if (arg1 == tr) {
                    if (fl != arg2) {
                        // (t = p) = f |= f/p
                        tank.push(fl);
                        tank.push(arg2);
                    }
                    return;
                }
                // constant f arg
                if (arg1 == fl) {
                    // (f = p) = f |= t/p
                    if (tr != arg2) {
                        tank.push(tr);
                        tank.push(arg2);
                    }
                }
                return;
            }
            // constant t arg
            if (arg1 == tr) {
                // (t = p) = q |= p/q
                if (arg2 != rhs) {
                    tank.push(arg2);
                    tank.push(rhs);
                }
                return;
            }
            // all vars
            if (arg1 == arg2) {
                // (p = p) = q
                tank.push(tr);
                tank.push(rhs);
            }
            return;
        }
        return;

    }

    // Return list of modified predicates by their position. Only these can cause new merges.
    // b is replaced by a
    protected Stack<Integer> mergeOnlyArgumentOperators(int a, int b) {
        if (m_timeToEnd > 0 && System.currentTimeMillis() > m_timeToEnd) {
            return null;
        }
        if (m_useMap.get(b) == null) {
            return null;
        }
        Stack<Integer> coincidentalMergeHoldingTank = new Stack<Integer>();
        // todo: make sure m_useMap reflects root usage of b
        Set<NormalizedAtomicExpression> bUses = m_useMap.get(b);
        m_useMap.remove(b);
        nextUse: for (NormalizedAtomicExpression nm : bUses) {
            int oldRoot = nm.readRoot();
            assert oldRoot > 0;
            NormalizedAtomicExpression ne = nm.replaceOperator(b, a); // also changes root if b
            if (nm == ne) {
                // no change in atom, so only root is b
                assert oldRoot == b;
                nm.writeToRoot(a);
                addMapUse(a, nm);
                applyBuiltInLogic(nm, coincidentalMergeHoldingTank);

            }
            else {
                assert ne != nm;
                assert ne.hashCode() != nm.hashCode();
                removeExprFromSet(nm);
                // Check for existence of the rewritten atom in conj. Add new cong if roots are different.
                if (m_expSet.contains(ne)) {
                    int neroot = ne.readRoot();
                    if (oldRoot != neroot
                            && !((oldRoot == a || oldRoot == b) && (neroot == a || neroot == b))) {
                        // dont put a and b on stack, already doing a/b
                        coincidentalMergeHoldingTank.push(oldRoot);
                        coincidentalMergeHoldingTank.push(neroot);
                    }
                }
                else {
                    ne.writeToRoot(oldRoot);
                    addExprToSet(ne);
                    applyBuiltInLogic(ne, coincidentalMergeHoldingTank);
                }
            }
        }
        return coincidentalMergeHoldingTank;
    }

    protected Set<NormalizedAtomicExpression> multiKeyUseMapSearch(
            Set<String> keys) {

        Set<NormalizedAtomicExpression> resultSet =
                new HashSet<NormalizedAtomicExpression>();
        boolean firstkey = true;
        for (String k : keys) {
            int rKey = m_registry.getIndexForSymbol(k);
            if (!m_useMap.containsKey(rKey)) {
                return null;
            }
            Set<NormalizedAtomicExpression> tResults =
                    new HashSet<NormalizedAtomicExpression>(m_useMap.get(rKey));
            if (tResults == null || tResults.isEmpty())
                return null;
            if (firstkey) {
                resultSet = tResults;
                firstkey = false;
            }
            else { // result is intersection
                resultSet.retainAll(tResults);
            }
        }
        return resultSet;
    }

    protected Set<java.util.Map<String, String>> getMatchesForOverideSet(
            NormalizedAtomicExpression expr, Registry exprReg,
            Set<Map<String, String>> foreignSymbolOverideSet) {
        Set<java.util.Map<String, String>> rSet =
                new HashSet<Map<String, String>>();
        for (Map<String, String> fs_m : foreignSymbolOverideSet) {
            Set<java.util.Map<String, String>> results =
                    getMatchesForEq(expr, exprReg, fs_m);
            if (results != null && results.size() != 0)
                rSet.addAll(results);
        }
        return rSet;
    }

    protected Set<Map<String, String>> getMatchesForEq(
            NormalizedAtomicExpression expr, Registry exprReg,
            Map<String, String> foreignSymbolOveride) {
        // Identify the literals.
        Set<String> literalsInexpr = new HashSet<String>();
        Map<String, Integer> exprMMap = expr.getOperatorsAsStrings(false);
        for (String s : exprMMap.keySet()) {
            String lit = "";
            if (!foreignSymbolOveride.containsKey(s)) {
                lit = s;
            }
            else if (!foreignSymbolOveride.get(s).equals("")) {
                // wildcard may have been bound in search for another expression
                lit = foreignSymbolOveride.get(s);
            }
            if (!lit.equals("")) {
                if (!m_registry.m_symbolToIndex.containsKey(lit))
                    return null;
                literalsInexpr.add(lit);
            }

        }
        // everything is quantified:
        if (literalsInexpr.isEmpty()) {
            return getBindings_NonCommutative(m_expSet, foreignSymbolOveride,
                    expr, exprReg);
        }
        Set<NormalizedAtomicExpression> vCNaemlsWithAllLiterals =
                multiKeyUseMapSearch(literalsInexpr);
        if (vCNaemlsWithAllLiterals == null)
            return null;

        Set<NormalizedAtomicExpression> filtered_vcNaemlsWithAllLiterals;

        String[] filter = new String[expr.getArity() + 2];
        for (int i = 0; i < filter.length; ++i) {

            String symAtPos;
            if (i == filter.length - 1) {
                symAtPos = exprReg.getSymbolForIndex(expr.readRoot());

            }
            else
                symAtPos = expr.readSymbol(i);
            if (foreignSymbolOveride.containsKey(symAtPos))
                symAtPos = foreignSymbolOveride.get(symAtPos);
            if (literalsInexpr.contains(symAtPos)) {
                filter[i] = symAtPos;
            }
            else
                filter[i] = "_";
        }

        boolean isCommutOp = exprReg.isCommutative(expr.readPosition(0));

        if (!isCommutOp) {
            filtered_vcNaemlsWithAllLiterals =
                    nonCommutativeFilter(vCNaemlsWithAllLiterals, filter);
            if (filtered_vcNaemlsWithAllLiterals.isEmpty())
                return null;
            return getBindings_NonCommutative(filtered_vcNaemlsWithAllLiterals,
                    foreignSymbolOveride, expr, exprReg);
        }
        else {
            filtered_vcNaemlsWithAllLiterals =
                    commutativeFilter(vCNaemlsWithAllLiterals, filter);
            if (filtered_vcNaemlsWithAllLiterals.isEmpty())
                return null;
            return getBindings_Commutative(filtered_vcNaemlsWithAllLiterals,
                    foreignSymbolOveride, expr, exprReg);
        }
    }

    // Should be able to assume literals in root/pos 0 match (else the filter didnt work)
    // And lits that are args are at least used as many times as in the theorem
    Set<Map<String, String>> getBindings_Commutative(
            Set<NormalizedAtomicExpression> vcEquations,
            Map<String, String> basemap, NormalizedAtomicExpression searchExpr,
            Registry searchReg) {
        // Argument sets
        Map<String, Integer> thArgs = searchExpr.getOperatorsAsStrings(true);
        Map<String, Integer> thOps = searchExpr.getOperatorsAsStrings(false);
        Deque<String> thOpsLitFirst = new LinkedList<String>();
        for (String thOp : thOps.keySet()) {
            if (!basemap.containsKey(thOp) || !basemap.get(thOp).equals("")) {
                thOpsLitFirst.addFirst(thOp);
            }
            else {
                thOpsLitFirst.addLast(thOp);
            }
        }
        Set<Map<String, String>> bindings = new HashSet<Map<String, String>>();
        bindToAVCEquation: for (NormalizedAtomicExpression vc_r : vcEquations) {
            Map<String, String> currentBind =
                    new HashMap<String, String>(basemap);
            Map<String, Integer> vcArgs = vc_r.getOperatorsAsStrings(true);
            Stack<String> comVCargsBound = new Stack<String>();
            ArrayList<String> comTargsBound = new ArrayList<String>();
            for (String thOp : thOpsLitFirst) {
                if (!basemap.containsKey(thOp) || !basemap.get(thOp).equals("")) {
                    // This is a literal
                    // Remove num uses as arg from arg count
                    if (basemap.containsKey(thOp) && thArgs.containsKey(thOp)) {
                        // Wildcard argument has already been bound
                        int numUses = thArgs.get(thOp);
                        String lit =
                                m_registry.getRootSymbolForSymbol(basemap
                                        .get(thOp));
                        // May be in commutative section
                        if (vcArgs.containsKey(lit)) {
                            vcArgs.put(lit, vcArgs.get(lit) - numUses);
                        }
                        else { // lit is arg of theorem, but not arg of vcEq
                            continue bindToAVCEquation; // shoulndt happen
                        }
                    }
                    else if (thArgs.containsKey(thOp)) {
                        // arg literal that exists in both places, not ever was wildcard
                        int numUses = thArgs.get(thOp);
                        thOp = m_registry.getRootSymbolForSymbol(thOp);
                        if (vcArgs.containsKey(thOp)) {
                            vcArgs.put(thOp, vcArgs.get(thOp) - numUses);
                        }
                        else {
                            continue bindToAVCEquation; // shouldnt happen
                        }
                    }
                    continue; // go to next op
                }
                // thOp must be a wildcard
                String wild = thOp;
                int wildOpCode = searchReg.getIndexForSymbol(wild);
                // Basemap is used over a set of equations; ie wild may not even be used in this one
                if (!searchExpr.getOpIds().contains(wildOpCode))
                    continue;
                String localToBindTo = "";

                boolean isRoot = searchExpr.readRoot() == wildOpCode;
                boolean isFSymb = searchExpr.readPosition(0) == wildOpCode;
                boolean isArg =
                        searchExpr.getOperatorsAsStrings(true)
                                .containsKey(wild);
                boolean isArgOnly = (isArg && !(isRoot || isFSymb));
                if (isRoot || isFSymb) {
                    if (!isArg && (isRoot != isFSymb)) { // Single use and is func symbol or root
                        if (isRoot) {
                            localToBindTo =
                                    m_registry.getSymbolForIndex(vc_r
                                            .readRoot());
                        }
                        else {
                            localToBindTo = vc_r.readSymbol(0);
                        }
                    }
                    else {// used as (root or func symb) and as arg(s) in search
                        // so reject if same not true of vcr
                        String loc = "";
                        if (isRoot && isFSymb) {
                            if (vc_r.readPosition(0) == vc_r.readRoot()) {
                                loc =
                                        m_registry.getSymbolForIndex(vc_r
                                                .readRoot());
                            }
                        }
                        else if (isFSymb) { // and an arg
                            loc =
                                    m_registry.getSymbolForIndex(vc_r
                                            .readPosition(0));
                        }
                        else if (isRoot) { // and an arg
                            loc = m_registry.getSymbolForIndex(vc_r.readRoot());
                        }
                        if (!loc.equals("")) {
                            int thC = thArgs.get(wild);
                            int vcC = vcArgs.get(loc);
                            if (thC <= vcC) {
                                vcArgs.put(loc, vcC - thC);
                                localToBindTo = loc;
                            }
                            else
                                continue bindToAVCEquation;
                        }
                        else
                            continue bindToAVCEquation;
                    }
                }
                // Only use is in arg list
                else {
                    isArgOnly = true;
                    int thC = thArgs.get(wild);
                    // Choose the first that has the min no. of uses.  (Going to potentially miss some matches)
                    for (String vcA : vcArgs.keySet()) {
                        int vcACnt = vcArgs.get(vcA);
                        if (thC <= vcACnt) {
                            localToBindTo = vcA;
                            vcArgs.put(localToBindTo, vcACnt - thC);
                            break;
                        }
                    }
                }
                if (!localToBindTo.equals("")) {
                    MTType wildType =
                            searchReg.getTypeByIndex(searchReg
                                    .getIndexForSymbol(wild));
                    MTType localType =
                            m_registry.getTypeByIndex(m_registry
                                    .getIndexForSymbol(localToBindTo));
                    if (!m_registry.isSubtype(localType, wildType))
                        continue bindToAVCEquation;
                    currentBind.put(wild, localToBindTo);
                    if (isArgOnly) {
                        comTargsBound.add(wild);
                        comVCargsBound.push(localToBindTo);
                    }
                }
                else
                    continue bindToAVCEquation;

            }
            bindings.add(currentBind);
            Map<String, String> rotBind =
                    new HashMap<String, String>(currentBind);
            // do simple rotation permutations
            for (String tArg : comTargsBound) {
                String locSym = comVCargsBound.pop();
                MTType wildType =
                        searchReg.getTypeByIndex(searchReg
                                .getIndexForSymbol(tArg));
                MTType locType =
                        m_registry.getTypeByIndex(m_registry
                                .getIndexForSymbol(locSym));
                if (m_registry.isSubtype(locType, wildType))
                    rotBind.put(tArg, locSym);
            }
            if (!rotBind.isEmpty())
                bindings.add(rotBind);
        }
        // Have to typecheck again.  Might have 1 nat, 1 Z arg of + for ex.
        // Just rotating.  Complete for 2 args bound, which is good enough for now.

        return bindings;
    }

    Set<Map<String, String>> getBindings_NonCommutative(
            Set<NormalizedAtomicExpression> vcEquations,
            Map<String, String> basemap, NormalizedAtomicExpression searchExpr,
            Registry searchReg) {
        Set<Map<String, String>> bindings = new HashSet<Map<String, String>>();
        bindToAVCEquation: for (NormalizedAtomicExpression vc_r : vcEquations) {
            Map<String, String> currentBind =
                    new HashMap<String, String>(basemap);
            for (String wild : basemap.keySet()) {
                String wildVal = basemap.get(wild);
                if (wildVal.equals("")) {
                    int wildInt = searchReg.getIndexForSymbol(wild);
                    if (!searchExpr.getOpIds().contains(wildInt))
                        continue; // wild not used in this eq.
                    int vc_eq_op =
                            vc_r.getOpIdUsedInAllPos(searchExpr, wildInt);
                    if (vc_eq_op < 0)
                        continue bindToAVCEquation; // non matching due to wildcard symbol used more places than found
                    String localToBindTo =
                            m_registry.getSymbolForIndex(vc_eq_op);
                    MTType wildType =
                            searchReg.getTypeByIndex(searchReg
                                    .getIndexForSymbol(wild));
                    MTType localType =
                            m_registry.getTypeByIndex(m_registry
                                    .getIndexForSymbol(localToBindTo));
                    if (!m_registry.isSubtype(localType, wildType))
                        continue bindToAVCEquation;
                    currentBind.put(wild, localToBindTo);
                }
            }
            bindings.add(currentBind);
        }
        return bindings;
    }

    protected Set<NormalizedAtomicExpression> commutativeFilter(
            Set<NormalizedAtomicExpression> raw, String[] filter_criteria) {
        Set<NormalizedAtomicExpression> filteredSet =
                new HashSet<NormalizedAtomicExpression>();
        Map<String, Integer> litMmapArgs = new HashMap<String, Integer>();
        for (int i = 1; i < filter_criteria.length - 1; ++i) {
            String s = filter_criteria[i];
            if (s.equals("_"))
                continue;
            if (litMmapArgs.containsKey(s)) {
                litMmapArgs.put(s, litMmapArgs.get(s) + 1);
            }
            else {
                litMmapArgs.put(s, 1);
            }
        }

        next_raw: for (NormalizedAtomicExpression r_n : raw) {
            if (r_n.getArity() != filter_criteria.length - 2) {
                continue next_raw; // choose next in raw
            }

            String s = filter_criteria[0];
            if (!s.equals("_") && !s.equals(r_n.readSymbol(0)))
                continue next_raw;
            s = filter_criteria[filter_criteria.length - 1];
            if (!s.equals("_")
                    && !s.equals(m_registry.getSymbolForIndex(r_n.readRoot())))
                continue next_raw;
            Map<String, Integer> cargs = r_n.getOperatorsAsStrings(true);
            for (String k : litMmapArgs.keySet()) {
                if (!cargs.containsKey(k))
                    continue next_raw;
                if (litMmapArgs.get(k) > cargs.get(k))
                    continue next_raw;
                ;
            }
            filteredSet.add(r_n);
        }
        return filteredSet;
    }

    protected Set<NormalizedAtomicExpression> nonCommutativeFilter(
            Set<NormalizedAtomicExpression> raw, String[] filter_criteria) {
        Set<NormalizedAtomicExpression> filteredSet =
                new HashSet<NormalizedAtomicExpression>();
        next_raw: for (NormalizedAtomicExpression r_n : raw) {
            if (r_n.getArity() != filter_criteria.length - 2) {
                continue next_raw; // choose next in raw
            }
            for (int i = 0; i < filter_criteria.length - 1; ++i) {
                String s = filter_criteria[i];
                if (s.equals("_"))
                    continue;
                if (!r_n.readSymbol(i).equals(s))
                    continue next_raw;
                ;
            }
            String s = filter_criteria[filter_criteria.length - 1];
            if (!s.equals("_")
                    && !s.equals(m_registry.getSymbolForIndex(r_n.readRoot())))
                continue next_raw;
            filteredSet.add(r_n);
        }
        return filteredSet;
    }

    @Override
    public String toString() {
        String r = "";
        if (m_evaluates_to_false)
            r += "Conjunction evaluates to false" + "\n";
        /*        for (MTType key : m_registry.m_typeToSetOfOperators.keySet()) {
         r += key.toString() + ":\n";
         r += m_registry.m_typeToSetOfOperators.get(key) + "\n\n";
         }
         */
        for (NormalizedAtomicExpression cur : m_expSet) {
            r += cur.toHumanReadableString() + "\n";
        }
        return r;
    }

}
