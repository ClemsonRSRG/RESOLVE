/*
 * ConjunctionOfNormalizedAtomicExpressions.java
 * ---------------------------------
 * Copyright (c) 2018
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.prover.utilities.expressions;

import edu.clemson.cs.rsrg.prover.absyn.PExp;
import edu.clemson.cs.rsrg.prover.absyn.expressions.PSymbol;
import edu.clemson.cs.rsrg.prover.absyn.iterators.PExpSubexpressionIterator;
import edu.clemson.cs.rsrg.prover.exception.NotPSymbolException;
import edu.clemson.cs.rsrg.prover.utilities.ImmutableVC;
import edu.clemson.cs.rsrg.prover.utilities.Registry;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTFunction;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import java.util.*;

/**
 * <p>This class represents a conjunction of normalized atomic expression.</p>
 *
 * @author Mike Khabbani
 * @version 2.0
 */
public class ConjunctionOfNormalizedAtomicExpressions {

    // ===========================================================
    // Member Fields
    // ===========================================================

    private final Map<NormalizedAtomicExpression, NormalizedAtomicExpression> myExpressionSet;

    private boolean myEvaluatesToFalseFlag;

    /** <p>Registry for symbols that we have encountered so far.</p> */
    private final Registry myRegistry;

    private long myTimeToEnd = -1;

    private final Map<Integer, Map<Integer, Set<NormalizedAtomicExpression>>> myUseMap;

    /** <p>A reference to the immutable {@code VC} we are currently processing.</p> */
    private final ImmutableVC myVC;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * @param registry the Registry symbols contained in the conjunction will
     *                 reference. This class will add entries to the registry if needed.
     */
    public ConjunctionOfNormalizedAtomicExpressions(ImmutableVC vc, Registry registry) {
        myExpressionSet = new HashMap<>(2048, .5f);
        myEvaluatesToFalseFlag = false;
        myRegistry = registry;
        myUseMap = new HashMap<>(2048, .5f);
        myVC = vc; // null if this is a theorem.
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // Top level
    public final String addExpression(PExp expression) {
        if (myEvaluatesToFalseFlag
                || (myTimeToEnd > 0 && System.currentTimeMillis() > myTimeToEnd)) {
            return "";
        }

        String name = expression.getTopLevelOperation();
        switch (name) {
        case "=B":
            int lhs = addFormula(expression.getSubExpressions().get(0));
            int rhs = addFormula(expression.getSubExpressions().get(1));

            return mergeOperators(lhs, rhs);
        case "andB":
            String r = "";
            r += addExpression(expression.getSubExpressions().get(0));
            r += addExpression(expression.getSubExpressions().get(1));

            return r;
        default:
            MTType type = expression.getMathType();
            int root = addFormula(expression);
            if (myEvaluatesToFalseFlag) {
                return "";
            }

            if (type.isBoolean()) {
                return mergeOperators(myRegistry.getIndexForSymbol("true"),
                        root);
            }

            break;
        }

        return "";
    }

    public final String addExpressionAndTrackChanges(PExp expression,
            long timeToEnd) {
        myTimeToEnd = timeToEnd;
        myTimeToEnd = Long.MAX_VALUE;
        String rString = "";
        rString += addExpression(expression);

        return rString;
    }

    /* experimentally handling =
     i.e.: (|?S| = 0) = (?S = Empty_String))
     is broken down by addExpression so (|?S| = 0) is an argument
     should return int for true if known to be equal, otherwise return root representative.
     */
    public final int addFormula(PExp formula) {
        if (formula.getTopLevelOperation().equals("=B")) {
            int lhs = addFormula(formula.getSubExpressions().get(0));
            PExp r = formula.getSubExpressions().get(1);
            int rhs = addFormula(r);
            lhs = myRegistry.findAndCompress(lhs);
            rhs = myRegistry.findAndCompress(rhs);

            // This prevents matching of (i=i)=true, which is not built in
            /*if (lhs == rhs) {
                return m_registry.getIndexForSymbol("true");
            }
            else {*/
            // insert =(lhs,rhs) = someNewRoot

            int questEq = myRegistry.getIndexForSymbol("=B");
            NormalizedAtomicExpression pred =
                    new NormalizedAtomicExpression(myRegistry, new int[] {
                            questEq, lhs, rhs });
            return addAtomicFormula(pred);
            // }
        }

        PSymbol asPsymbol;
        if (!(formula instanceof PSymbol)) {
            throw new NotPSymbolException(formula);
        }
        else {
            asPsymbol = (PSymbol) formula;
        }

        int intRepOfOp = addPSymbol(asPsymbol);
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
                new NormalizedAtomicExpression(myRegistry, ne);
        if (myEvaluatesToFalseFlag) {
            return -1;
        }

        newExpr = newExpr.rootOps();

        return addAtomicFormula(newExpr);
    }

    public final void clear() {
        myExpressionSet.clear();
    }

    public final PExp find(PExp exp) {
        if (exp.getSubExpressions().size() == 0) {
            String s =
                    myRegistry.getRootSymbolForSymbol(exp
                            .getTopLevelOperation());
            if (s.equals("")) {
                return exp;
            }
            else {
                return new PSymbol(exp.getMathType(), exp.getMathTypeValue(), s);
            }
        }

        PExpSubexpressionIterator it = exp.getSubExpressionIterator();
        List<PExp> args = new ArrayList<>();
        boolean irreducible = false;
        while (it.hasNext()) {
            PExp cur = it.next();
            PExp fcur = find(cur);
            if (fcur.getSubExpressions().size() > 0
                    || !myRegistry.mySymbolToIndex.containsKey(fcur
                            .getTopLevelOperation())) {
                irreducible = true;
            }

            args.add(fcur);
        }

        String op =
                myRegistry.getRootSymbolForSymbol(exp.getTopLevelOperation());
        if (!irreducible && !op.equals("")) {
            int[] ia;
            ia = new int[args.size() + 1];
            ia[0] = myRegistry.getIndexForSymbol(op);
            for (int i = 1; i < ia.length; ++i) {
                ia[i] =
                        myRegistry.getIndexForSymbol(args.get(i - 1)
                                .getTopLevelOperation());
            }

            NormalizedAtomicExpression na =
                    new NormalizedAtomicExpression(myRegistry, ia);
            if (myExpressionSet.containsKey(na) && myExpressionSet.get(na).readRoot() >= 0) {
                int r = myExpressionSet.get(na).readRoot();
                String rs = myRegistry.getSymbolForIndex(r);

                return new PSymbol(myRegistry.getTypeByIndex(r), null, rs);
            }
            else {
                return new PSymbol(exp.getMathType(), exp.getMathTypeValue(), exp
                        .getTopLevelOperation(), args);
            }
        }
        else {
            return new PSymbol(exp.getMathType(), exp.getMathTypeValue(), exp
                    .getTopLevelOperation(), args);
        }
    }

    public final Set<Map<String, String>> getMatchesForOverrideSet(NormalizedAtomicExpression expr,
            Set<Map<String, String>> foreignSymbolOverrideSet) {
        Set<Map<String, String>> rSet = new HashSet<>();
        for (Map<String, String> fs_m : foreignSymbolOverrideSet) {
            Set<Map<String, String>> results =
                    getBindingsForSearchExpr(expr, fs_m);
            if (results != null && results.size() != 0) {
                rSet.addAll(results);
            }
        }

        return rSet;
    }

    public final Set<NormalizedAtomicExpression> getUses(int symk) {
        HashSet<NormalizedAtomicExpression> rSet =
                new HashSet<>();
        Map<Integer, Set<NormalizedAtomicExpression>> usesByPos =
                myUseMap.get(symk);
        for (Map.Entry<Integer, Set<NormalizedAtomicExpression>> me : usesByPos
                .entrySet()) {
            rSet.addAll(me.getValue());
        }

        return rSet;
    }

    public final int size() {
        return myExpressionSet.size();
    }

    /**
     * <p>This method returns the object in string format.</p>
     *
     * @return Object as a string.
     */
    @Override
    public final String toString() {
        StringBuilder r = new StringBuilder();
        if (myEvaluatesToFalseFlag) {
            r.append("Conjunction evaluates to false" + "\n");
        }

        for (MTType key : myRegistry.myTypeToSetOfOperators.keySet()) {
            r.append(key.toString()).append(":\n");
            r.append(myRegistry.myTypeToSetOfOperators.get(key)).append("\n\n");
        }

        for (NormalizedAtomicExpression cur : myExpressionSet.keySet()) {
            r.append(cur.toString()).append("\n");
        }

        return r.toString();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * @param atomicFormula one sided expression. (= new root) is appended and
     *                      expression is inserted if no match of the side is found. Otherwise
     *                      current root is returned.
     * @return current integer value of root symbol that represents the input.
     */
    private int addAtomicFormula(NormalizedAtomicExpression atomicFormula) {
        // Return root if atomic formula is present
        if (myExpressionSet.containsKey(atomicFormula)) {
            return myExpressionSet.get(atomicFormula).readRoot();
        }

        // no such formula exists
        MTType typeOfFormula =
                myRegistry.getTypeByIndex(atomicFormula.readPosition(0));
        // this is the full type and is necessarily a function type

        MTType rangeType = ((MTFunction) typeOfFormula).getRange();
        String symName =
                myRegistry.getSymbolForIndex(atomicFormula.readPosition(0));
        assert rangeType != null : symName + " has null type";

        // if any of the symbols in atomicFormula are variables (FORALL) make created symbol a variable
        boolean isVar = false;
        if (atomicFormula.hasVarOps()) {
            isVar = true;
        }

        int rhs = myRegistry.makeSymbol(rangeType, isVar);
        atomicFormula.writeToRoot(rhs);
        Stack<Integer> hTank = new Stack<>();
        applyBuiltInLogic(atomicFormula, hTank);
        addExprToSet(atomicFormula);
        while (!hTank.isEmpty()) {
            mergeOperators(hTank.pop(), hTank.pop());
        }

        return myRegistry.findAndCompress(rhs);
    }

    private void addExprToSet(NormalizedAtomicExpression nae) {
        for (int i : nae.getOpIds()) {
            addMapUse(i, nae);
        }

        int root = nae.readRoot();
        assert root >= 0 : "adding unrooted expression to conj";
        addMapUse(root, nae);
        myExpressionSet.put(nae, nae);
    }

    private void addMapUse(int symk, NormalizedAtomicExpression nae) {
        if (!myUseMap.containsKey(symk)) {
            myUseMap.put(symk,
                    new HashMap<Integer, Set<NormalizedAtomicExpression>>());
        }

        Map<Integer, Set<NormalizedAtomicExpression>> posMapRef =
                myUseMap.get(symk);
        int[] pos = nae.getPositionsFor(symk);

        for (int p : pos) {
            if (!posMapRef.containsKey(p)) {
                posMapRef.put(p, new HashSet<NormalizedAtomicExpression>());
            }

            posMapRef.get(p).add(nae);
        }
    }

    private void applyBuiltInLogic(NormalizedAtomicExpression nm,
            Stack<Integer> tank) {
        // turn off if this is not part of a VC
        if (myVC == null) {
            return;
        }

        int arity = nm.getArity();
        if (arity != 2) {
            return;
        }

        String op = nm.readSymbol(0);
        int arg1 = nm.readPosition(1);
        int rhs = nm.readRoot();
        if (rhs < 0) {
            return;
        }

        int tr = myRegistry.getIndexForSymbol("true");
        int fl = myRegistry.getIndexForSymbol("false");
        // =,true,false,not.  recorded first in reg. logic relation args (and, or, =) are ordered.

        int arg2 = nm.readPosition(2);
        // arity 2 guard: return if all constant
        if ((arg1 == tr || arg1 == fl) && (arg2 == tr || arg2 == fl)
                && (rhs == tr || rhs == fl)) {
            return;
        }

        // guard: return if all var and op is not equals or or
        if (!(op.equals("=B") || op.equals("orB")) && arg1 != tr && arg1 != fl
                && (arg2 != tr && arg2 != fl) && (rhs != tr && rhs != fl)) {
            return;
        }

        // rules for and
        if (op.equals("andB")) {
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

        if (op.equals("orB")) {
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
            if (myVC.VCGoalStrings.contains(myRegistry.getSymbolForIndex(rhs))) {
                if (rhs == arg1) {
                    myVC.addGoal(myRegistry.getSymbolForIndex(arg2));
                }
                else if (rhs == arg2) {
                    myVC.addGoal(myRegistry.getSymbolForIndex(arg1));
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
        if (op.equals("=B")) {
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
        }
    }

    // adds a particular symbol to the registry
    private int addPSymbol(PSymbol ps) {
        String name = ps.getTopLevelOperation();
        if (myRegistry.mySymbolToIndex.containsKey(name)) {
            return myRegistry.mySymbolToIndex.get(name);
        }

        MTType type = ps.getMathType();
        Registry.Usage usage = Registry.Usage.SINGULAR_VARIABLE;
        if (ps.isLiteral()) {
            usage = Registry.Usage.LITERAL;
        }
        else if (ps.isFunction()
                || ps.getMathType().getClass().getSimpleName().equals("MTFunction")) {
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
            List<MTType> paramList = new ArrayList<>();
            for (PExp pParam : ps.getSubExpressions()) {
                paramList.add(pParam.getMathType());
            }

            type = new MTFunction(myRegistry.getTypeGraph(), type, paramList);
        }

        return myRegistry.addSymbol(name, type, usage);
    }

    // need to choose literals over vars for theorem matching purposes
    // i.e. the theorem expression should keep the literals
    private int chooseSymbolToKeep(int a, int b) {
        String s = myRegistry.myIndexToSymbol.get(a);
        if (s.contains("¢")) {
            if (!myRegistry.myIndexToSymbol.get(b).contains("¢")) {
                return b; // a is created, b is not
            }
            else {
                return a < b ? a : b; // a is created, b is created
            }
        }

        return a < b ? a : b;
    }

    private Set<Map<String, String>> computeBindings(Set<NormalizedAtomicExpression> filteredSet,
            Map<String, String> baseMap, String[] unmappedWildcards, Registry searchReg) {
        Set<Map<String, String>> rSet = new HashSet<>(filteredSet.size(), .5f);
        next: for (NormalizedAtomicExpression e : filteredSet) {
            Map<String, String> bmap = new HashMap<>(baseMap);
            for (int i = 0; i < unmappedWildcards.length; ++i) {
                String wc = unmappedWildcards[i];
                String ac =
                        (i < unmappedWildcards.length - 1 ? e.readSymbol(i)
                                : myRegistry.getSymbolForIndex(e.readRoot()));
                if (wc.equals("")) {
                    continue;
                }

                if (!bmap.get(wc).equals("") && !bmap.get(wc).equals(ac)) {
                    continue next; // this clause ensures usage of same symbol where required.
                }

                MTType wildType =
                        searchReg.getTypeByIndex(searchReg
                                .getIndexForSymbol(wc));
                MTType localType =
                        myRegistry.getTypeByIndex(myRegistry
                                .getIndexForSymbol(ac));
                if (!myRegistry.isSubtype(localType, wildType)) {
                    continue next;
                }

                bmap.put(wc, ac);
            }

            rSet.add(bmap);
        }

        return rSet;
    }

    private Set<Map<String, String>> getBindingsForSearchExpr(
            NormalizedAtomicExpression expr,
            Map<String, String> foreignSymbolOverride) {
        int[] searchKeys =
                expr.rootedLiterals(foreignSymbolOverride, myRegistry);
        if (searchKeys == null) {
            return null;
        }

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
                            foreignSymbolOverride, unMappedWildCards, expr
                                    .getRegistry());
                }
            }
            else {
                // only one blank. 2 searches
                rSet =
                        computeBindings(getExprsMatchingAtPosition(searchKeys),
                                foreignSymbolOverride, unMappedWildCards, expr
                                        .getRegistry());
                int t = searchKeys[1];
                String s = unMappedWildCards[1];
                searchKeys[1] = searchKeys[2];
                unMappedWildCards[1] = unMappedWildCards[2];
                searchKeys[2] = t;
                unMappedWildCards[2] = s;
                rSet.addAll(computeBindings(
                        getExprsMatchingAtPosition(searchKeys),
                        foreignSymbolOverride, unMappedWildCards, expr
                                .getRegistry()));

                return rSet;
            }
        }

        return computeBindings(getExprsMatchingAtPosition(searchKeys),
                foreignSymbolOverride, unMappedWildCards, expr.getRegistry());
    }

    private Set<NormalizedAtomicExpression> getExprsMatchingAtPosition(int[] searchKey) {
        Set<NormalizedAtomicExpression> rSet = new HashSet<>();
        for (int p = 0; p < searchKey.length; ++p) {
            int k = searchKey[p];
            if (k < 0)
                continue;
            if (!myUseMap.containsKey(k)) {
                return rSet; // k is not used in the conjunction, but is still in Registry. Can happen with =, not, etc.
            }

            int x = (p < searchKey.length - 1) ? p : -1;
            if (!myUseMap.get(k).containsKey(x)) { // k not used in position p anywhere
                return rSet;
            }
        }

        for (int p = 0; p < searchKey.length; ++p) {
            int k = searchKey[p];
            if (k < 0) {
                continue;
            }

            if (p == 0) {
                rSet.addAll(myUseMap.get(k).get(p));
            }
            else if (p == searchKey.length - 1) { //last element which is root class
                rSet.retainAll(getUses(k, -1));
            }
            else {
                rSet.retainAll(getUses(k, p));
            }
        }

        return rSet;
    }

    private Set<NormalizedAtomicExpression> getUses(int symk, int pos) {
        return new HashSet<>(myUseMap.get(symk).get(pos));
    }

    // Return list of modified predicates by their position. Only these can cause new merges.
    // b is replaced by a
    private Stack<Integer> mergeOnlyArgumentOperators(int a, int b) {
        if (myEvaluatesToFalseFlag
                || (myTimeToEnd > 0 && System.currentTimeMillis() > myTimeToEnd)) {
            return null;
        }

        if (myUseMap.get(b) == null) {
            return null;
        }

        Stack<Integer> coincidentalMergeHoldingTank = new Stack<>();
        // todo: make sure myUseMap reflects root usage of b
        Set<NormalizedAtomicExpression> bUses = getUses(b);
        myUseMap.remove(b);
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
                assert ne.hashCode() != nm.hashCode();
                removeExprFromSet(nm);

                // Check for existence of the rewritten atom in conj. Add new cong if roots are different.
                if (myExpressionSet.containsKey(ne)) {
                    ne = myExpressionSet.get(ne);
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

    private String mergeOperators(int a, int b) {
        int t = myRegistry.getIndexForSymbol("true");
        int f = myRegistry.getIndexForSymbol("false");

        StringBuilder rString = new StringBuilder();
        if (myEvaluatesToFalseFlag
                || (myTimeToEnd > 0 && System.currentTimeMillis() > myTimeToEnd)) {
            return rString.toString();
        }

        a = myRegistry.findAndCompress(a);
        b = myRegistry.findAndCompress(b);
        if (a == b) {
            return "";
        }

        Stack<Integer> holdingTank = new Stack<>();
        holdingTank.push(a);
        holdingTank.push(b);
        while (!holdingTank.empty()) {
            if (myEvaluatesToFalseFlag
                    || (myTimeToEnd > 0 && System.currentTimeMillis() > myTimeToEnd)) {
                return rString.toString();
            }

            int opB = myRegistry.findAndCompress(holdingTank.pop());
            int opA = myRegistry.findAndCompress(holdingTank.pop());
            if (opA == opB) {
                continue;
            }

            // Want to replace quantified vars with constant if it is equal to the constant
            int keeper = chooseSymbolToKeep(opA, opB);
            if (keeper == opB) {
                int temp = opA;
                opA = opB;
                opB = temp;
            }

            if ((opA == t && opB == f)) {
                myEvaluatesToFalseFlag = true;

                return "contradiction detected " + rString;
            }

            rString.append(myRegistry.getSymbolForIndex(opA)).append("/").append(myRegistry.getSymbolForIndex(opB)).append(",");
            Stack<Integer> mResult = mergeOnlyArgumentOperators(opA, opB);
            String bstring = myRegistry.getSymbolForIndex(opB);
            if (myVC != null && myVC.VCGoalStrings.contains(bstring)) {
                myVC.VCGoalStrings.remove(bstring);
                myVC.VCGoalStrings.add(myRegistry.getSymbolForIndex(opA));
            }

            myRegistry.substitute(opA, opB);

            if (mResult != null) {
                holdingTank.addAll(mResult);
            }
        }

        return rString.toString();
    }

    private void removeExprFromSet(NormalizedAtomicExpression nae) {
        for (int i : nae.getOpIds()) {
            removeMapUse(i, nae);
        }

        removeMapUse(nae.readRoot(), nae);
        myExpressionSet.remove(nae);
    }

    private void removeMapUse(int symK, NormalizedAtomicExpression nae) {
        if (myUseMap.containsKey(symK)) {
            myUseMap.get(symK).remove(nae);
        }
    }

}