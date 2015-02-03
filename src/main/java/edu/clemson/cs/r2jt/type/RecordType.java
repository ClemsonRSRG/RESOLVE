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
package edu.clemson.cs.r2jt.type;

import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.scope.Binding;
import edu.clemson.cs.r2jt.scope.ScopeID;

public class RecordType extends Type {

    // ===========================================================
    // Variables
    // ===========================================================

    private ModuleID id;

    private PosSymbol name;

    private List<FieldItem> fields = new List<FieldItem>();

    // ===========================================================
    // Constructors
    // ===========================================================

    public RecordType(ModuleID id, PosSymbol name, List<FieldItem> fields) {
        this.id = id;
        this.name = name;
        this.fields.addAll(fields);
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    public ModuleID getModuleID() {
        return id;
    }

    public PosSymbol getName() {
        return name;
    }

    public void setName(PosSymbol name) {
        this.name = name;
    }

    public void setFields(List<FieldItem> fields) {
        this.fields = fields;
    }

    public List<FieldItem> getFields() {
        return fields;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public RecordType instantiate(ScopeID sid, Binding binding) {
        edu.clemson.cs.r2jt.collections.List<FieldItem> fields2 =
                new edu.clemson.cs.r2jt.collections.List<FieldItem>();
        Iterator<FieldItem> i = fields.iterator();
        while (i.hasNext()) {
            edu.clemson.cs.r2jt.type.FieldItem item = i.next();
            fields2.add(item);
        }
        return new RecordType(id, name, fields2);
    }

    public TypeName getProgramName() {
        return new TypeName(id, null, name);
    }

    public String getRelativeName(Location loc) {
        StringBuffer sb = new StringBuffer();
        if (name.toString().startsWith("%")) {
            sb.append("Record declared at ");
            sb.append(name.getPos().toString());
            if (!(loc.getFilename().equals(id.getFilename()))) {
                sb.append(" in " + id.toString());
            }
        }
        else {
            if (!(loc.getFilename().equals(id.getFilename()))) {
                sb.append(id.toString() + ".");
            }
            sb.append(name.toString());
        }
        return sb.toString();
    }

    public TupleType toMath() {
        List<FieldItem> fields2 = new List<FieldItem>();
        Iterator<FieldItem> i = fields.iterator();
        while (i.hasNext()) {
            FieldItem item = i.next();
            fields2.add(item);
        }
        return new TupleType(fields2);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[RECORD]");
        sb.append(getProgramName().toString());
        sb.append("(");
        Iterator<FieldItem> i = fields.iterator();
        while (i.hasNext()) {
            FieldItem item = i.next();
            sb.append(item.toString());
            if (i.hasNext()) {
                sb.append("; ");
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
                sb.append("; ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
