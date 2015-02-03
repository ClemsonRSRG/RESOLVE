/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
