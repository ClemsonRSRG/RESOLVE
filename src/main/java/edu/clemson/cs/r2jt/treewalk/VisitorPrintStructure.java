/**
 * VisitorPrintStructure.java
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
package edu.clemson.cs.r2jt.treewalk;

import edu.clemson.cs.r2jt.absyn.*;

public class VisitorPrintStructure extends TreeWalkerStackVisitor {

    private int indent = 0;
    private final boolean showIdentifiers = false;

    @Override
    public void preAnyStack(ResolveConceptualElement data) {
        System.out.print(ConvertNodeToString(data, false));

        if (this.getParent() != null) {
            System.out.append(" (Parent: "
                    + ConvertNodeToString(this.getParent(), true) + ")");
        }
        System.out.println();
        ++indent;
    }

    private String ConvertNodeToString(ResolveConceptualElement data,
            boolean disableIndent) {
        String nodeString = "";

        for (int i = 0; !disableIndent && i < indent; ++i) {
            nodeString += "  ";
        }

        if (data instanceof VirtualListNode) {
            nodeString += ((VirtualListNode) data).getNodeName() + " [List]";
        }
        else {
            nodeString += data.getClass().getSimpleName();
        }

        if (showIdentifiers) {
            if (data instanceof VarExp) {
                nodeString += " (" + ((VarExp) data).getName().toString() + ")";
            }
            else if (data instanceof InfixExp) {
                nodeString +=
                        " (" + ((InfixExp) data).getOpName().toString() + ")";
            }
            else if (data instanceof OutfixExp) {
                nodeString +=
                        " (" + ((OutfixExp) data).getOperatorAsString() + ")";
            }
        }

        return nodeString;
    }

    @Override
    public void postAnyStack(ResolveConceptualElement data) {
        --indent;
    }

    @Override
    public void midFacilityOperationDecStatements(FacilityOperationDec node,
            Statement previous, Statement next) {
        if (previous == null) {
            System.out.println("Next statement: " + next.toString(0));
        }
        else if (next == null) {
            System.out.println("Previous statement: " + previous.toString(0));
        }
        else {
            System.out.println("Previous statement: " + previous.toString(0)
                    + "\nNext statement: " + next.toString(0));
        }
    }
}
