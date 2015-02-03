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
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.data.Location;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class VirtualListNode extends ResolveConceptualElement {

    ResolveConceptualElement myParent;
    String myName;
    List<ResolveConceptualElement> myList;
    Class<?> myListType;

    public VirtualListNode(ResolveConceptualElement parent, String listName,
            List<ResolveConceptualElement> list, Class<?> listType) {
        this.myParent = parent;
        this.myName = parent.getClass().getSimpleName() + toCamelCase(listName);
        this.myList = list;
        this.myListType = listType;
    }

    public ResolveConceptualElement getParent() {
        return myParent;
    }

    public String getNodeName() {
        return myName;
    }

    public Class<?> getListType() {
        return myListType;
    }

    @Override
    public List<ResolveConceptualElement> getChildren() {
        List<ResolveConceptualElement> children =
                new LinkedList<ResolveConceptualElement>();
        Iterator<ResolveConceptualElement> iter = myList.iterator();
        while (iter.hasNext()) {
            children.add(ResolveConceptualElement.class.cast(iter.next()));
        }
        return children;
    }

    @Override
    public void accept(ResolveConceptualVisitor v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String asString(int indent, int increment) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private String toCamelCase(String s) {
        StringBuilder buffer = new StringBuilder();
        StringTokenizer tokens = new StringTokenizer(s, "_");
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            buffer.append(Character.toUpperCase(token.charAt(0)));
            buffer.append(token.substring(1));
        }
        return buffer.toString();
    }

    @Override
    public Location getLocation() {
        throw new UnsupportedOperationException(this.getClass()
                + " has no location by definition.");
    }
}
