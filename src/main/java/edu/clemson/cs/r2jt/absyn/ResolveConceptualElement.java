/*
 * This software is released under the new BSD 2006 license.
 * 
 * Note the new BSD license is equivalent to the MIT License, except for the
 * no-endorsement final clause.
 * 
 * Copyright (c) 2007, Clemson University
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the Clemson University nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This sofware has been developed by past and present members of the
 * Reusable Sofware Research Group (RSRG) in the School of Computing at
 * Clemson University. Contributors to the initial version are:
 * 
 * Steven Atkinson
 * Greg Kulczycki
 * Kunal Chopra
 * John Hunt
 * Heather Keown
 * Ben Markle
 * Kim Roche
 * Murali Sitaraman
 */
/*
 * ResolveConceptualElement.java
 * 
 * The Resolve Software Composition Workbench Project
 * 
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.absyn;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.AsStringCapability;

public abstract class ResolveConceptualElement implements AsStringCapability {

    public abstract void accept(ResolveConceptualVisitor v);

    public abstract String asString(int indent, int increment);

    /**
     * Builds a sequence of numSpaces spaces and returns that
     * sequence.
     */
    protected void printSpace(int numSpaces, StringBuffer buffer) {
        for (int i = 0; i < numSpaces; ++i) {
            buffer.append(" ");
        }
    }

    public List<ResolveConceptualElement> getChildren() {
        List<ResolveConceptualElement> children =
                new List<ResolveConceptualElement>();
        // get a list of all the declared and inherited members of this object
        ArrayList<Field> fields = new ArrayList<Field>();
        Class<?> curClass = this.getClass();
        while (curClass != ResolveConceptualElement.class) {
            Field[] curFields = curClass.getDeclaredFields();
            for (int i = 0; i < curFields.length; ++i) {
                fields.add(curFields[i]);
            }
            curClass = curClass.getSuperclass();
        }

        // loop through all the class members
        Iterator<Field> iterFields = fields.iterator();
        while (iterFields.hasNext()) {
            Field curField = iterFields.next();
            curField.setAccessible(true);
            Class<?> fieldType = curField.getType();

            try {
                // is this member a ResolveConceptualElement?
                // if so, add it as a child
                if (ResolveConceptualElement.class.isAssignableFrom(fieldType)) {
                    //System.out.println("Walking: " + curField.getName());
                    children.add(ResolveConceptualElement.class.cast(curField
                            .get(this)));
                }
                // is this member a list of ResolveConceptualElements?
                // if so, add the elements to the list of children
                else if (List.class.isAssignableFrom(fieldType)) {
                    List<?> fieldList = List.class.cast(curField.get(this));
                    if (fieldList != null
                            && fieldList.size() > 0
                            && ResolveConceptualElement.class
                                    .isAssignableFrom(fieldList.get(0)
                                            .getClass())) {
                        children.add(new VirtualListNode(this, curField
                                .getName(),
                                (List<ResolveConceptualElement>) fieldList));
                    }

                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return children;
    }
}
