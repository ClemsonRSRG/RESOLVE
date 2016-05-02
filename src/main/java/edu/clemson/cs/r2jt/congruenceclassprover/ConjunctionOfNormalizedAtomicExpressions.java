/**
 * ConjunctionOfNormalizedAtomicExpressions.java
 * ---------------------------------
 * Copyright (c) 2016
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
    protected final Map<NormalizedAtomicExpression, NormalizedAtomicExpression> m_expSet;
    protected long m_timeToEnd = -1;
    protected boolean m_evaluates_to_false = false;
    private int f_num = 0;
    private String m_current_justification = "";
    protected final Map<Integer, Map<Integer, Set<NormalizedAtomicExpression>>> m_useMap;
    protected final VerificationConditionCongruenceClosureImpl m_VC;

    /**
     * @param registry the Registry symbols contained in the conjunction will
     *                 reference. This class will add entries to the registry if needed.
     */
    public ConjunctionOfNormalizedAtomicExpressions(Registry registry,
            VerificationConditionCongruenceClosureImpl vc) {
        m_registry = registry;
        m_expSet =
                new HashMap<NormalizedAtomicExpression, NormalizedAtomicExpression>(
                        2048, .5f);
        m_useMap =
                new HashMap<Integer, Map<Integer, Set<NormalizedAtomicExpression>>>(
                        2048, .5f);
        m_VC = vc; // null if this is a theorem
    }

    protected int size() {
        return m_expSet.size();
    }

    protected void clear() {
        m_expSet.clear();
    }

    protected Registry getRegistry() {
        return m_registry;
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

    protected PExp find(PExp exp) {
        if (exp.getSubExpressions().size() == 0) {
            String s =
                    m_registry.getRootSymbolForSymbol(exp
                            .getTopLevelOperation());
            if (s.equals("")) {
                return exp;
            }
            else {
                return new PSymbol(exp.getType(), exp.getTypeValue(), s);
            }
        }
        PExpSubexpressionIterator it = exp.getSubExpressionIterator();
        ArrayList<PExp> args = new ArrayList<PExp>();
        boolean irreducable = false;
        while (it.hasNext()) {
            PExp cur = it.next();
            PExp fcur = find(cur);
            if (fcur.getSubExpressions().size() > 0
                    || !m_registry.m_symbolToIndex.containsKey(fcur
                            .getTopLevelOperation()))
                irreducable = true;
            args.add(fcur);
        }
        String op =
                m_registry.getRootSymbolForSymbol(exp.getTopLevelOperation());
        if (!irreducable && !op.equals("")) {
            int[] ia;
            ia = new int[args.size() + 1];
            ia[0] = m_registry.getIndexForSymbol(op);
            for (int i = 1; i < ia.length; ++i) {
                ia[i] =
                        m_registry.getIndexForSymbol(args.get(i - 1)
                                .getTopLevelOperation());
            }
            NormalizedAtomicExpression na =
                    new NormalizedAtomicExpression(this, ia);
            if (m_expSet.containsKey(na) && m_expSet.get(na).readRoot() >= 0) {
                int r = m_expSet.get(na).readRoot();
                String rs = m_registry.getSymbolForIndex(r);
                return new PSymbol(m_registry.getTypeByIndex(r), null, rs);
            }
            else
                return new PSymbol(exp.getType(), exp.getTypeValue(), exp
                        .getTopLevelOperation(), args);
        }
        else {
            return new PSymbol(exp.getType(), exp.getTypeValue(), exp
                    .getTopLevelOperation(), args);
        }
    }

    // Top level
    protected String addExpression(PExp expression) {
        if (m_evaluates_to_false
                || (m_timeToEnd > 0 && System.currentTimeMillis() > m_timeToEnd)) {
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
                    new NormalizedAtomicExpression(this, new int[] { questEq,
                            lhs, rhs });
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
                new NormalizedAtomicExpression(this, ne);
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
        if (m_expSet.containsKey(atomicFormula))
            return m_expSet.get(atomicFormula).readRoot();
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
        if (m_evaluates_to_false
                || (m_timeToEnd > 0 && System.currentTimeMillis() > m_timeToEnd)) {
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
            if (m_evaluates_to_false
                    || (m_timeToEnd > 0 && System.currentTimeMillis() > m_timeToEnd)) {
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
            if (m_VC != null && m_VC.m_goal.contains(bstring)) {
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
        if (!m_useMap.containsKey(symk))
            m_useMap.put(symk,
                    new HashMap<Integer, Set<NormalizedAtomicExpression>>());
        Map<Integer, Set<NormalizedAtomicExpression>> posMapRef =
                m_useMap.get(symk);
        int[] pos = nae.getPositionsFor(symk);
        for (int i = 0; i < pos.length; ++i) {
            int p = pos[i];
            if (!posMapRef.containsKey(p))
                posMapRef.put(p, new HashSet<NormalizedAtomicExpression>());
            posMapRef.get(p).add(nae);
        }
    }

    private void addExprToSet(NormalizedAtomicExpression nae) {
        for (int i : nae.getOpIds()) {
            addMapUse(i, nae);
        }
        int root = nae.readRoot();
        assert root >= 0 : "adding unrooted expression to conj";
        addMapUse(root, nae);
        m_expSet.put(nae, nae);
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
        m_expSet.remove(nae);
    }

    private void applyBuiltInLogic(NormalizedAtomicExpression nm,
            Stack<Integer> tank) {
        // turn off if this is not part of a VC
        if (m_VC == null)
            return;
        int arity = nm.getArity();
        if (arity != 2)
            return;
        String op = nm.readSymbol(0);
        int arg1 = nm.readPosition(1);
        int rhs = nm.readRoot();
        if (rhs < 0)
            return;
        int tr = m_registry.getIndexForSymbol("true");
        int fl = m_registry.getIndexForSymbol("false");
        // =,true,false,not.  recorded first in reg. logic relation args (and, or, =) are ordered.

        int arg2 = nm.readPosition(2);
        // arity 2 guard: return if all constant
        if ((arg1 == tr || arg1 == fl) && (arg2 == tr || arg2 == fl)
                && (rhs == tr || rhs == fl))
            return;
        // guard: return if all var and op is not equals or or
        if (!(op.equals("=") || op.equals("or")) && arg1 != tr && arg1 != fl
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
            // x or some goal g = some goal g
            if (m_VC != null
                    && m_VC.m_goal.contains(m_registry.getSymbolForIndex(rhs))) {
                if (rhs == arg1) {
                    m_VC.addGoal(m_registry.getSymbolForIndex(arg2));
                }
                else if (rhs == arg2) {
                    m_VC.addGoal(m_registry.getSymbolForIndex(arg1));
                }
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

    protected Set<NormalizedAtomicExpression> getUses(int symk) {
        HashSet<NormalizedAtomicExpression> rSet =
                new HashSet<NormalizedAtomicExpression>();
        Map<Integer, Set<NormalizedAtomicExpression>> usesByPos =
                m_useMap.get(symk);
        for (Map.Entry<Integer, Set<NormalizedAtomicExpression>> me : usesByPos
                .entrySet()) {
            rSet.addAll(me.getValue());
        }
        return rSet;
    }

    protected Set<NormalizedAtomicExpression> getUses(int symk, int pos) {
        HashSet<NormalizedAtomicExpression> rSet =
                new HashSet<NormalizedAtomicExpression>();
        rSet.addAll(m_useMap.get(symk).get(pos));
        return rSet;
    }

    // Return list of modified predicates by their position. Only these can cause new merges.
    // b is replaced by a
    protected Stack<Integer> mergeOnlyArgumentOperators(int a, int b) {
        if (m_evaluates_to_false
                || (m_timeToEnd > 0 && System.currentTimeMillis() > m_timeToEnd)) {
            return null;
        }
        if (m_useMap.get(b) == null) {
            return null;
        }
        Stack<Integer> coincidentalMergeHoldingTank = new Stack<Integer>();
        // todo: make sure m_useMap reflects root usage of b
        Set<NormalizedAtomicExpression> bUses = getUses(b);
        m_useMap.remove(b);
        nextUse: for (NormalizedAtomicExpression nm : bUses) {
            int oldRoot = nm.readRoot();
            assert oldRoot > 0;
            NormalizedAtomicExpression ne = nm.replaceOperator(b, a); // also changes root if b
            if (nm == ne) {
                // no change in atom, so only root is b
                assert oldRoot == b;
                addMapUse(a, nm);
                applyBuiltInLogic(nm, coincidentalMergeHoldingTank);

            }
            else {
                assert ne != nm;
                assert ne.hashCode() != nm.hashCode();
                removeExprFromSet(nm);
                // Check for existence of the rewritten atom in conj. Add new cong if roots are different.
                if (m_expSet.containsKey(ne)) {
                    ne = m_expSet.get(ne);
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
            Set<NormalizedAtomicExpression> tResults = getUses(rKey);
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

    protected Set<java.util.Map<String, String>> getMatchesForOverrideSet(
            NormalizedAtomicExpression expr,
            Set<Map<String, String>> foreignSymbolOverrideSet) {
        Set<java.util.Map<String, String>> rSet =
                new HashSet<Map<String, String>>();
        for (Map<String, String> fs_m : foreignSymbolOverrideSet) {
            Set<java.util.Map<String, String>> results =
                    getBindingsForSearchExpr(expr, fs_m);
            if (results != null && results.size() != 0)
                rSet.addAll(results);
        }
        return rSet;
    }

    protected Set<Map<String, String>> getBindingsForSearchExpr(
            NormalizedAtomicExpression expr,
            Map<String, String> foreignSymbolOverride) {
        int[] searchKeys =
                expr.rootedLiterals(foreignSymbolOverride, m_registry);
        if (searchKeys == null)
            return null;
        String[] unMappedWildCards =
                expr.unMappedWildcards(foreignSymbolOverride);
        Set<Map<String, String>> rSet;
        boolean isCommutOp =
                expr.getRegistry().isCommutative(expr.readPosition(0));
        // only supporting arity 2 commutative search
        // do additional search with swapped args if only one arg is blank
        // if neither is blank, do search with ordered args instead
        if (isCommutOp && searchKeys.length == 4
                && (searchKeys[1] != searchKeys[2])) {
            if (!(searchKeys[1] == -1 || searchKeys[2] == -1)) {
                // neither blank. order keys.
                if (searchKeys[1] > searchKeys[2]) {
                    int t = searchKeys[1];
                    searchKeys[1] = searchKeys[2];
                    searchKeys[2] = t;
                    return computeBindings(
                            getExprsMatchingAtPosition(searchKeys),
                            foreignSymbolOverride, searchKeys,
                            unMappedWildCards, expr.getRegistry());
                }
                else {
                    // only one blank. 2 searches
                    rSet =
                            computeBindings(
                                    getExprsMatchingAtPosition(searchKeys),
                                    foreignSymbolOverride, searchKeys,
                                    unMappedWildCards, expr.getRegistry());
                    int t = searchKeys[1];
                    String s = unMappedWildCards[1];
                    searchKeys[1] = searchKeys[2];
                    unMappedWildCards[1] = unMappedWildCards[2];
                    searchKeys[2] = t;
                    unMappedWildCards[2] = s;
                    rSet.addAll(computeBindings(
                            getExprsMatchingAtPosition(searchKeys),
                            foreignSymbolOverride, searchKeys,
                            unMappedWildCards, expr.getRegistry()));
                    return rSet;
                }
            }
        }
        return computeBindings(getExprsMatchingAtPosition(searchKeys),
                foreignSymbolOverride, searchKeys, unMappedWildCards, expr
                        .getRegistry());
    }

    private Set<Map<String, String>> computeBindings(
            Set<NormalizedAtomicExpression> filteredSet,
            Map<String, String> baseMap, int[] searchKey,
            String[] unmappedWildcards, Registry searchReg) {

        Set<Map<String, String>> rSet =
                new HashSet<Map<String, String>>(filteredSet.size(), .5f);
        next: for (NormalizedAtomicExpression e : filteredSet) {
            Map<String, String> bmap = new HashMap<String, String>(baseMap);
            for (int i = 0; i < unmappedWildcards.length; ++i) {
                String wc = unmappedWildcards[i];
                String ac =
                        (i < unmappedWildcards.length - 1 ? e.readSymbol(i)
                                : m_registry.getSymbolForIndex(e.readRoot()));
                if (wc.equals(""))
                    continue;
                if (!bmap.get(wc).equals("") && !bmap.get(wc).equals(ac))
                    continue next; // this clause ensures usage of same symbol where required.
                MTType wildType =
                        searchReg.getTypeByIndex(searchReg
                                .getIndexForSymbol(wc));
                MTType localType =
                        m_registry.getTypeByIndex(m_registry
                                .getIndexForSymbol(ac));
                if (!m_registry.isSubtype(localType, wildType))
                    continue next;
                bmap.put(wc, ac);
            }
            rSet.add(bmap);
        }
        return rSet;
    }

    private Set<NormalizedAtomicExpression> getExprsMatchingAtPosition(
            int[] searchKey) {
        HashSet<NormalizedAtomicExpression> rSet =
                new HashSet<NormalizedAtomicExpression>();
        for (int p = 0; p < searchKey.length; ++p) {
            int k = searchKey[p];
            if (k < 0)
                continue;
            if (!m_useMap.containsKey(k))
                return rSet; // k is not used in the conjunction, but is still in Registry. Can happen with =, not, etc.
            int x = (p < searchKey.length - 1) ? p : -1;
            if (!m_useMap.get(k).containsKey(x)) // k not used in position p anywhere
                return rSet;
        }

        for (int p = 0; p < searchKey.length; ++p) {
            int k = searchKey[p];
            if (k < 0)
                continue;
            if (p == 0)
                rSet.addAll(m_useMap.get(k).get(p));
            else if (p == searchKey.length - 1)//last element which is root class
                rSet.retainAll(getUses(k, -1));
            else
                rSet.retainAll(getUses(k, p));

        }
        return rSet;

    }

    @Override
    public String toString() {
        String r = "";
        if (m_evaluates_to_false)
            r += "Conjunction evaluates to false" + "\n";
        /*for (MTType key : m_registry.m_typeToSetOfOperators.keySet()) {
            r += key.toString() + ":\n";
            r += m_registry.m_typeToSetOfOperators.get(key) + "\n\n";
        }
         */
        for (NormalizedAtomicExpression cur : m_expSet.keySet()) {
            r += cur.toString() + "\n";
        }
        return r;
    }

}
