/*
 * TreeWalkerStackVisitor.java
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
package edu.clemson.cs.rsrg.treewalk;

import edu.clemson.cs.rsrg.absyn.*;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class TreeWalkerStackVisitor extends TreeWalkerVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    private LinkedList<ResolveConceptualElement> myVisitStack =
        new LinkedList<>();

    private ResolveConceptualElement myParent;

    // ===========================================================
    // Public Methods
    // ===========================================================

    public void preAnyStack(ResolveConceptualElement data) {}

    public void postAnyStack(ResolveConceptualElement data) {}

    public final void preAny(ResolveConceptualElement data) {
        preAnyStack(data);
        myParent = data;
        pushParent();
    }

    public final void postAny(ResolveConceptualElement data) {
        popParent();
        postAnyStack(data);
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    protected final ResolveConceptualElement getParent() {
        return myVisitStack.peek();
    }

    protected final ResolveConceptualElement getAncestor(int index) {
        return myVisitStack.get(index);
    }

    protected final int getAncestorSize() {
        return myVisitStack.size();
    }

    protected final Iterator<ResolveConceptualElement> getAncestorInterator() {
        return myVisitStack.iterator();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    private final void pushParent() {
        myVisitStack.push(myParent);
    }

    private final void popParent() {
        myVisitStack.pop();
    }

}