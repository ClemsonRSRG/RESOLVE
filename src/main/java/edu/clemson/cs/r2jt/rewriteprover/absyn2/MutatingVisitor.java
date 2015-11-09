/**
 * MutatingVisitor.java
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
package edu.clemson.cs.r2jt.rewriteprover.absyn2;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * TODO : MutatingVisitor tends to crash if you use it for multiple sequential
 * traversals.  Always creating a new instance of the visitor is a workaround
 * for now.
 */
public class MutatingVisitor extends BoundVariableVisitor {

    private LinkedList<Integer> myIndices = new LinkedList<Integer>();
    private PExpr myRoot;
    private LinkedList<Map<Integer, PExpr>> myChangesAtLevel =
            new LinkedList<Map<Integer, PExpr>>();

    private PExpr myClosingType;

    protected PExpr myFinalExpression;

    public PExpr getFinalPExp() {
        return myFinalExpression;
    }

    @Override
    public final void beginPExp(PExpr e) {

        if (myRoot == null) {
            myRoot = e;
            myFinalExpression = myRoot;
        }

        myIndices.push(0); //We start at the zeroth child
        myChangesAtLevel.push(new HashMap<Integer, PExpr>());

        mutateBeginPExp(e);
    }

    protected boolean atRoot() {
        return (myIndices.size() == 1);
    }

    public void mutateBeginPExp(PExpr e) {}

    public void mutateEndPExp(PExpr e) {}

    public void replaceWith(PExpr replacement) {
        if (myIndices.size() == 1) {
            //We're the root
            myFinalExpression = replacement;
        }
        else {
            myChangesAtLevel.get(1).put(myIndices.get(1), replacement);
        }
    }

    protected final PExpr getTransformedVersion() {
        return myClosingType;
    }

    @Override
    public final void endChildren(PExpr e) {
        myClosingType = e;

        Map<Integer, PExpr> changes = myChangesAtLevel.peek();
        if (!changes.isEmpty()) {
            myClosingType = e.withSubExpressionsReplaced(changes);
            replaceWith(myClosingType);
        }

        mutateEndChildren(e);
    }

    public void mutateEndChildren(PExpr t) {}

    @Override
    public final void endPExp(PExpr e) {
        mutateEndPExp(e);

        //We're not visiting any more children at this level (because the
        //level just ended!)
        myIndices.pop();
        myChangesAtLevel.pop();

        //If I'm the root, there's no chance I have any siblings
        if (e != myRoot) {
            //Increment to the next potential child index
            int i = myIndices.pop();

            myIndices.push(i + 1);
        }
    }
}
