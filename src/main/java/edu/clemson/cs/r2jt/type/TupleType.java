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
package edu.clemson.cs.r2jt.type;

import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.scope.Binding;
import edu.clemson.cs.r2jt.scope.ScopeID;

public class TupleType extends Type {

    // ===========================================================
    // Variables
    // ===========================================================

    private List<FieldItem> fields = new List<FieldItem>();

    // ===========================================================
    // Constructors
    // ===========================================================

    public TupleType(List<FieldItem> fields) {
        this.fields.addAll(fields);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public List<FieldItem> getFields() {
        return fields;
    }

    public TupleType instantiate(ScopeID sid, Binding binding) {
        List<FieldItem> fields2 = new List<FieldItem>();
        Iterator<FieldItem> i = fields.iterator();
        while (i.hasNext()) {
            fields2.add(i.next());
        }
        return new TupleType(fields2);
    }

    public TypeName getProgramName() {
        return null;
    }

    public String getRelativeName(Location loc) {
        return null;
    }

    public TupleType toMath() {
        return this;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        Iterator<FieldItem> i = fields.iterator();
        while (i.hasNext()) {
            FieldItem item = i.next();
            sb.append(item.toString());
            if (i.hasNext()) {
                sb.append(" x ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public String asString() {
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        Iterator<FieldItem> i = fields.iterator();
        while (i.hasNext()) {
            FieldItem item = i.next();
            sb.append(item.asString());
            if (i.hasNext()) {
                sb.append(" x ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
