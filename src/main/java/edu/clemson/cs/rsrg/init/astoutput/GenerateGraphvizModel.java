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
import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.mathdecl.MathAssertionDec;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.cs.rsrg.absyn.items.mathitems.DefinitionBodyItem;
import edu.clemson.cs.rsrg.absyn.items.programitems.UsesItem;
import edu.clemson.cs.rsrg.absyn.rawtypes.NameTy;
import edu.clemson.cs.rsrg.absyn.rawtypes.Ty;
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

        // Put the current node number into the map
        myElementToNodeNumMap.put(data, myNodeNum);
    }

    /**
     * <p>For all {@link VirtualListNode} nodes, create a new node.</p>
     *
     * @param data Current {@link ResolveConceptualElement} we are visiting.
     */
    @Override
    public void postAnyStack(ResolveConceptualElement data) {
        // Add a new node using our string template
        if (data instanceof VirtualListNode) {
            ST node =
                    createNode(myElementToNodeNumMap.get(data), data.getClass()
                            .getSimpleName(), false);
            myModel.add("nodes", node);
        }
    }

    // -----------------------------------------------------------
    // Declarations
    // -----------------------------------------------------------

    /**
     * <p>For all {@link Dec} nodes, create a new node and
     * add the declaration's name field.</p>
     *
     * @param e Current {@link Dec} we are visiting.
     */
    @Override
    public void postDec(Dec e) {
        // Create the new node
        ST node =
                createNode(myElementToNodeNumMap.get(e), e.getClass()
                        .getSimpleName(), true);
        String data;

        if (e instanceof MathAssertionDec) {
            data =
                    ((MathAssertionDec) e).getAssertionType().name() + " "
                            + e.getName().getName();
        }
        else {
            data = e.getName().getName();
        }

        node.add("nodeData", data);
        myModel.add("nodes", node);
    }

    // -----------------------------------------------------------
    // Definition Body Items (Definition Body)
    // -----------------------------------------------------------

    /**
     * <p>For all {@link DefinitionBodyItem} nodes, create a new node and
     * add the "=" as the operator.</p>
     *
     * @param e Current {@link DefinitionBodyItem} we are visiting.
     */
    @Override
    public void postDefinitionBodyItem(DefinitionBodyItem e) {
        // Create the new node
        ST node =
                createNode(myElementToNodeNumMap.get(e), e.getClass()
                        .getSimpleName(), true);

        node.add("nodeData", "=");
        myModel.add("nodes", node);
    }

    // -----------------------------------------------------------
    // Uses Items (Imports)
    // -----------------------------------------------------------

    /**
     * <p>For all {@link UsesItem} nodes, create a new node and
     * add the name of the imported module.</p>
     *
     * @param e Current {@link UsesItem} we are visiting.
     */
    @Override
    public void postUsesItem(UsesItem e) {
        // Create the new node
        ST node =
                createNode(myElementToNodeNumMap.get(e), e.getClass()
                        .getSimpleName(), true);

        // Add the imported module name
        node.add("nodeData", e.getName().getName());

        myModel.add("nodes", node);
    }

    // -----------------------------------------------------------
    // Raw Types
    // -----------------------------------------------------------

    /**
     * <p>For all {@link Ty} nodes, create a new node. If this node
     * is a {@link NameTy}, we add in the name.</p>
     *
     * @param e Current {@link Ty} we are visiting.
     */
    @Override
    public void postTy(Ty e) {
        // Create the new node
        ST node;
        String data;

        if (e instanceof NameTy) {
            NameTy ty = (NameTy) e;
            data = ty.getName().getName();

            if (ty.getQualifier() != null) {
                data = ty.getQualifier() + "::" + data;
            }

            node =
                    createNode(myElementToNodeNumMap.get(e), e.getClass()
                            .getSimpleName(), true);
            node.add("nodeData", data);
        }
        else {
            node =
                    createNode(myElementToNodeNumMap.get(e), e.getClass()
                            .getSimpleName(), false);
        }

        myModel.add("nodes", node);
    }

    // -----------------------------------------------------------
    // Math Expressions
    // -----------------------------------------------------------

    /**
     * <p>For all {@link FunctionExp} nodes, create a new node and
     * add the function's name (with qualifier if it is not {@code null}
     * and/or with the carat expression if it is not {@code null}).</p>
     *
     * @param e Current {@link FunctionExp} we are visiting.
     */
    @Override
    public void postFunctionExp(FunctionExp e) {
        // Create the new node
        ST node =
                createNode(myElementToNodeNumMap.get(e), e.getClass()
                        .getSimpleName(), true);

        String data = e.getOperatorAsString();
        if (e.getQualifier() != null) {
            data = e.getQualifier() + "::" + data;
        }

        if (e.getCaratExp() != null) {
            data = data + "^" + e.getCaratExp();
        }

        node.add("nodeData", data);
        myModel.add("nodes", node);
    }

    /**
     * <p>For all {@link InfixExp} nodes, create a new node and
     * add the operator's name (with qualifier if it is not {@code null}).</p>
     *
     * @param e Current {@link InfixExp} we are visiting.
     */
    @Override
    public void postInfixExp(InfixExp e) {
        // Create the new node
        ST node =
                createNode(myElementToNodeNumMap.get(e), e.getClass()
                        .getSimpleName(), true);

        // Need to replace special characters
        StringBuilder sb = new StringBuilder();
        char[] stringArray = e.getOperatorAsString().toCharArray();
        for (char c : stringArray) {
            switch (c) {
            case '<':
                sb.append("&lt;");
                break;
            case '>':
                sb.append("&gt;");
                break;
            default:
                sb.append(c);
                break;
            }
        }

        String data = sb.toString();
        if (e.getQualifier() != null) {
            data = e.getQualifier() + "::" + data;
        }

        node.add("nodeData", data);
        myModel.add("nodes", node);
    }

    /**
     * <p>For all {@link QuantExp} nodes, create a new node and
     * add the appropriate quantification.</p>
     *
     * @param e Current {@link QuantExp} we are visiting.
     */
    @Override
    public void postQuantExp(QuantExp e) {
        // Create the new node
        ST node =
                createNode(myElementToNodeNumMap.get(e), e.getClass()
                        .getSimpleName(), true);

        node.add("nodeData", e.getQuantification().name());
        myModel.add("nodes", node);
    }

    /**
     * <p>For all {@link TypeAssertionExp} nodes, create a new node and
     * add its operator as a string.</p>
     *
     * @param e Current {@link TypeAssertionExp} we are visiting.
     */
    @Override
    public void postTypeAssertionExp(TypeAssertionExp e) {
        // Create the new node
        ST node =
                createNode(myElementToNodeNumMap.get(e), e.getClass()
                        .getSimpleName(), true);

        node.add("nodeData", ":");
        myModel.add("nodes", node);
    }

    /**
     * <p>For all {@link VarExp} nodes, create a new node and
     * add the name (with qualifier if it is not {@code null}).</p>
     *
     * @param e Current {@link VarExp} we are visiting.
     */
    @Override
    public void postVarExp(VarExp e) {
        // Create the new node
        ST node =
                createNode(myElementToNodeNumMap.get(e), e.getClass()
                        .getSimpleName(), true);
        String data = e.getName().getName();

        if (e.getQualifier() != null) {
            data = e.getQualifier() + "::" + data;
        }

        node.add("nodeData", data);
        myModel.add("nodes", node);
    }

    /**
     * <p>For all {@link MathExp} nodes that we didn't add special
     * override logic, we create a new node with its simple class name.</p>
     *
     * @param e Current {@link MathExp} we are visiting.
     */
    @Override
    public void postMathExp(MathExp e) {
        if (!(e instanceof VarExp) && !(e instanceof InfixExp)
                && !(e instanceof TypeAssertionExp) && !(e instanceof QuantExp)
                && !(e instanceof FunctionExp)) {
            myModel.add("nodes", createNode(myElementToNodeNumMap.get(e), e
                    .getClass().getSimpleName(), false));
        }
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

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>An helper method to create a new node using string template.</p>
     *
     * @param nodeNum The associated node number.
     * @param nodeName The node's simple class name.
     * @param hasData {@code true} if the node has data, {@code false} otherwise.
     *
     * @return The newly created ST for "node".
     */
    private ST createNode(int nodeNum, String nodeName, boolean hasData) {
        return mySTGroup.getInstanceOf("outputGraphvizNodes").add("nodeNum",
                nodeNum).add("nodeName", nodeName).add("hasNodeData", hasData);
    }

}