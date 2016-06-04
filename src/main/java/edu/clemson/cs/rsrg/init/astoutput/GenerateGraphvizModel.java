/**
 * GenerateGraphvizModel.java
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
package edu.clemson.cs.rsrg.init.astoutput;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.VirtualListNode;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.cs.rsrg.absyn.rawtypes.NameTy;
import edu.clemson.cs.rsrg.treewalk.TreeWalkerStackVisitor;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This class generates a Graphviz model file object using the provided
 * RESOLVE abstract syntax tree. This visitor logic is implemented as a
 * a {@link TreeWalkerStackVisitor}.</p>
 *
 * @author Chuck Cook
 * @author Yu-Shan Sun
 *
 * @version 2.0
 */
public class GenerateGraphvizModel extends TreeWalkerStackVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Current node number.</p> */
    private int myNodeNum;

    /** <p>List of parent nodes.</p> */
    private final List<ResolveConceptualElement> myParentList;

    /** <p>String buffer that contains all the visited nodes.</p> */
    private final StringBuffer myNodeList;

    /** <p>String buffer that contains all the connecting arrows.</p> */
    private final StringBuffer myArrowList;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates an object that overrides methods to generate
     * a Graphviz model file.</p>
     */
    public GenerateGraphvizModel() {
        myParentList = new ArrayList<>();
        myNodeList = new StringBuffer();
        myNodeNum = 0;
        myArrowList = new StringBuffer();
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // All objects
    // -----------------------------------------------------------

    /**
     * <p>For all objects, generate a node and add arrows to from its
     * parent node.</p>
     *
     * @param data Current {@link ResolveConceptualElement} we are visiting.
     */
    @Override
    public void preAnyStack(ResolveConceptualElement data) {
        if (!(data instanceof VirtualListNode)) {
            ResolveConceptualElement parent = getParent();
            String className = data.getClass().getSimpleName();

            if (parent != null) {
                myParentList.add(myNodeNum, parent);
                int p = myParentList.indexOf(parent) - 1;
                myArrowList.append("n" + p + " -> n" + myNodeNum + " //"
                        + className + "\n");
            }
            else {
                myParentList.add(0, null);
            }

            myNodeList.append("n" + (myNodeNum++) + " [label=\"" + className);
            if (data instanceof VarExp) {
                myNodeList.append("\\n(" + ((VarExp) data).getName().toString()
                        + ")");
            }
            else if (data instanceof NameTy) {
                myNodeList.append("\\n(" + ((NameTy) data).getName().toString()
                        + ")");
            }
            else if (data instanceof ResolveConceptualElement) {
                myNodeList.append("\\n(" + data.toString() + ")");
            }
            myNodeList.append("\"]; //" + className + "\n");
        }
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Returns the list of all nodes.</p>
     *
     * @return A {@link StringBuffer} containing all the nodes.
     */
    public final StringBuffer getNodeList() {
        return myNodeList;
    }

    /**
     * <p>Returns the list of all the connecting arrows.</p>
     *
     * @return A {@link StringBuffer} containing all the connecting arrows.
     */
    public final StringBuffer getArrowList() {
        return myArrowList;
    }

}