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
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
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

import java.util.ArrayList;
import java.util.HashMap;

import edu.clemson.cs.r2jt.absyn.*;

public class VisitorGenModuleDecDot extends TreeWalkerStackVisitor {

    private int nodeNum = 0;
    private ArrayList<ResolveConceptualElement> parentList =
            new ArrayList<ResolveConceptualElement>();
    private StringBuffer nodeList = new StringBuffer();
    private StringBuffer arrowList = new StringBuffer();

    public VisitorGenModuleDecDot() {}

    @Override
    public void preAnyStack(ResolveConceptualElement data) {
        ResolveConceptualElement parent = getParent();
        String className = data.getClass().getSimpleName();
        if (parent != null) {
            parentList.add(nodeNum, parent);
            int p = parentList.indexOf(parent) - 1;
            arrowList.append("n" + p + " -> n" + nodeNum + " //" + className
                    + "\n");
        }
        else {
            parentList.add(0, null);
        }
        nodeList.append("n" + (nodeNum++) + " [label=\"" + className);
        if (data instanceof VarExp) {
            nodeList
                    .append("\\n(" + ((VarExp) data).getName().toString() + ")");
        }
        else if (data instanceof NameTy) {
            nodeList
                    .append("\\n(" + ((NameTy) data).getName().toString() + ")");
        }
        else if (data instanceof Object) {
            try {
                if ((data.getClass()).getMethod("toString").getDeclaringClass() == data
                        .getClass()) {
                    nodeList.append("\\n("
                            + ((Object) data).toString().toString() + ")");
                }
            }
            catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        nodeList.append("\"]; //" + className + "\n");
    }

    @Override
    public void postAnyStack(ResolveConceptualElement data) {
    //--indent;
    }

    public StringBuffer getNodeList() {
        return nodeList;
    }

    public StringBuffer getArrowList() {
        return arrowList;
    }
}
