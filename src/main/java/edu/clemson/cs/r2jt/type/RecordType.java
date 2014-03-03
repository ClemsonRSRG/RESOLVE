/**
 * RecordType.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
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
