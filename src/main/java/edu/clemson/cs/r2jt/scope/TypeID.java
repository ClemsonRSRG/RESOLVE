/**
 * TypeID.java
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
package edu.clemson.cs.r2jt.scope;

import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;

public class TypeID {

    // ===========================================================
    // Variables
    // ===========================================================

    private Symbol qualifier = null;

    private Symbol name = null;

    private int params = 0;

    // ===========================================================
    // Constructors
    // ===========================================================

    public TypeID(PosSymbol qualifier, PosSymbol name, int params) {
        if (qualifier != null) {
            this.qualifier = qualifier.getSymbol();
        }
        this.name = name.getSymbol();
        this.params = params;
    }

    public TypeID(Symbol qualifier, Symbol name, int params) {
        this.qualifier = qualifier;
        this.name = name;
        this.params = params;
    }

    public TypeID(Symbol sym) {
        name = sym;
    }

    // ===========================================================
    // Accessors
    // ===========================================================

    public Symbol getQualifier() {
        return qualifier;
    }

    public Symbol getName() {
        return name;
    }

    public int getParamCount() {
        return params;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (qualifier != null) {
            sb.append(qualifier.toString() + ".");
        }
        sb.append(name.toString());
        if (params > 0) {
            sb.append("(" + params + ")");
        }
        return sb.toString();
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof TypeID) {
            TypeID tid = (TypeID) obj;
            return (this.qualifier == tid.qualifier && this.name == tid.name && this.params == tid.params);
        }
        else {
            return false;
        }
    }
}
