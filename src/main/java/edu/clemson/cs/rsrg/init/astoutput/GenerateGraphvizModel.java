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
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.cs.rsrg.treewalk.TreeWalkerStackVisitor;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import java.util.HashMap;
import java.util.Map;

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

    /** <p>Map from each element in the tree to its associated node number.</p> */
    private final Map<ResolveConceptualElement, Integer> myElementToNodeNumMap;

    /** <p>Current node number.</p> */
    private int myNodeNum;

    /** <p>String template for the base Graphviz model.</p> */
    private final ST myModel;

    /** <p>Check to see if we need to special handle this node.</p> */
    private boolean mySpecialHandlingNode;

    /** <p>String template groups for generating the Graphviz model.</p> */
    private final STGroup mySTGroup;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates an object that overrides methods to generate
     * a Graphviz model file.</p>
     *
     * @param stGroup The string template file.
     * @param model The model we are going be generating.
     */
    public GenerateGraphvizModel(STGroup stGroup, ST model) {
        myElementToNodeNumMap = new HashMap<>();
        myModel = model;
        myNodeNum = 0;
        mySpecialHandlingNode = false;
        mySTGroup = stGroup;
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // All objects
    // -----------------------------------------------------------

    /**
     * <p>For all nodes that have a parent, add an edge from its
     * parent node to itself.</p>
     *
     * @param data Current {@link ResolveConceptualElement} we are visiting.
     */
    @Override
    public void preAnyStack(ResolveConceptualElement data) {
        // Get a new node number
        myNodeNum++;

        // If we have a parent, we need to add an edge
        ResolveConceptualElement parent = getParent();
        if (parent != null) {
            int parentNum = myElementToNodeNumMap.get(parent);

            // Add a new edge using our string template
            ST edge =
                    mySTGroup.getInstanceOf("outputGraphvizEdges").add(
                            "parentNodeNum", parentNum).add("nodeNum",
                            myNodeNum);
            myModel.add("edges", edge);
        }

        // Assume this is not a special node we need to handle
        mySpecialHandlingNode = false;

        // Put the current node number into the map
        myElementToNodeNumMap.put(data, myNodeNum);
    }

    /**
     * <p>Done generating items for this node.</p>
     *
     * @param data Current {@link ResolveConceptualElement} we are visiting.
     */
    @Override
    public void postAnyStack(ResolveConceptualElement data) {
        if (!mySpecialHandlingNode) {
            // Add a new node using our string template
            ST node =
                    mySTGroup.getInstanceOf("outputGraphvizNodes").add(
                            "nodeNum", myElementToNodeNumMap.get(data)).add(
                            "nodeName", data.getClass().getSimpleName()).add(
                            "hasNodeData", false);
            myModel.add("nodes", node);
        }
    }

    // -----------------------------------------------------------
    // Module Declarations
    // -----------------------------------------------------------

    /**
     * <p>For all {@link ModuleDec} nodes, add the name of the module to the node.</p>
     *
     * @param e Current {@link ModuleDec} we are visiting.
     */
    @Override
    public void postModuleDec(ModuleDec e) {
        // Add the module node
        ST node =
                mySTGroup.getInstanceOf("outputGraphvizNodes").add("nodeNum",
                        myElementToNodeNumMap.get(e)).add("nodeName",
                        e.getClass().getSimpleName()).add("hasNodeData", true)
                        .add("nodeData", e.getName().getName());
        myModel.add("nodes", node);

        // This is a node we need to special handle
        mySpecialHandlingNode = true;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Returns the completed model with all the nodes and edges.</p>
     *
     * @return String template rendering of the model.
     */
    public final String getCompleteModel() {
        return myModel.render();
    }

}