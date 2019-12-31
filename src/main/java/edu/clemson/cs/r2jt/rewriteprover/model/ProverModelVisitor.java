/*
 * ProverModelVisitor.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover.model;

import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PExpVisitor;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author hamptos
 */
public class ProverModelVisitor extends PExpVisitor {

    private Deque<Integer> myIndices = new LinkedList<Integer>();
    private LinkedList<Site> myIDs = new LinkedList<Site>();
    private Site myRoot;

    private final PerVCProverModel myModel;
    private Conjunct myConjunct;

    public ProverModelVisitor(PerVCProverModel model) {
        myModel = model;
        myConjunct = null;
    }

    public void setConjunct(Conjunct c) {
        myConjunct = c;
    }

    protected Site getID() {
        return myIDs.peek();
    }

    protected Site getID(int levels) {
        return myIDs.get(levels);
    }

    protected int getDepth() {
        return myIDs.size();
    }

    @Override
    public final void beginPExp(PExp p) {

        myIndices.push(0); // We start at the zeroth child

        Site s;
        if (myRoot == null) {
            myRoot = new Site(myModel, myConjunct, p);
            s = myRoot;
        }
        else {
            s = buildID(p, myIndices);
        }
        myIDs.push(s);

        doBeginPExp(p);
    }

    private Site buildID(PExp p, Deque<Integer> indices) {
        List<Integer> idIndices = new ArrayList<Integer>(indices.size() - 1);

        // The ID should reflect all but the last index
        Iterator<Integer> indicesIter = indices.descendingIterator();
        for (int i = 0; i < (indices.size() - 1); i++) {
            idIndices.add(indicesIter.next());
        }

        return new Site(myModel, myConjunct, idIndices, p);
    }

    public void doBeginPExp(PExp p) {

    }

    @Override
    public final void endPExp(PExp p) {
        doEndPExp(p);

        if (!p.equals(myRoot.exp)) {
            // We're not visiting any more children at this level (because the
            // level just ended!)
            myIndices.pop();

            // Increment to the next potential child index
            int i = myIndices.pop();
            myIndices.push(i + 1);
        }

        // We just left a PExp, so get rid of its ID
        myIDs.pop();

        if (p.equals(myRoot.exp)) {
            myRoot = null;
            myIndices.pop();
        }
    }

    public void doEndPExp(PExp p) {

    }
}
