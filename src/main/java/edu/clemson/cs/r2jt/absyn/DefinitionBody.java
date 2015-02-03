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

public class DefinitionBody extends ResolveConceptualElement {

    /** The base member. */
    private Exp base;

    /** The hypothesis member. */
    private Exp hypothesis;

    /** The definition member. */
    private Exp definition;

    private boolean isInductive;

    public DefinitionBody(Exp base, Exp hypothesis, Exp definition) {
        this.base = base;
        this.hypothesis = hypothesis;
        this.definition = definition;
        this.isInductive = (base != null);
    }

    public boolean isInductive() {
        return this.isInductive;
    }

    public Location getLocation() {
        Location result;

        if (base == null) {
            result = definition.getLocation();
        }
        else {
            result = base.getLocation();
        }

        return result;
    }

    @Override
    public void accept(ResolveConceptualVisitor v) {
    // TODO Auto-generated method stub
    }

    @Override
    public String asString(int indent, int increment) {
        StringBuffer sb = new StringBuffer();
        if (base != null) {
            sb.append(base.asString(indent + increment, increment));
        }

        if (hypothesis != null) {
            sb.append(hypothesis.asString(indent + increment, increment));
        }

        if (definition != null) {
            sb.append(definition.asString(indent + increment, increment));
        }
        return sb.toString();
    }

    public Exp getBase() {
        return base;
    }

    public Exp getHypothesis() {
        return hypothesis;
    }

    public Exp getDefinition() {
        return definition;
    }

    public void setBase(Exp newBase) {
        base = newBase;
    }

    public void setHypothesis(Exp newHypothesis) {
        hypothesis = newHypothesis;
    }

    public void setDefinition(Exp newDefinition) {
        definition = newDefinition;
    }
}
