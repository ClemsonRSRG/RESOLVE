/**
 * TypeHolder.java
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

import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.location.TypeLocator;
import edu.clemson.cs.r2jt.location.SymbolSearchException;
import edu.clemson.cs.r2jt.type.Type;

// Changed Char to Character - literal string change
public class TypeHolder {

    // ==========================================================
    // Variables
    // ==========================================================
    CompileEnvironment myInstanceEnvironment;

    private ModuleScope scope;

    private Type typeB;

    private Type typeN;

    private Type typeZ;

    private Type typeR;

    private Type typeStr;

    private Type typeBoolean;

    private Type typeInteger;

    private Type typeReal;

    private Type typeChar;

    private Type typeChar_Str;

    private ErrorHandler err;

    // ===========================================================
    // Constructors
    // ===========================================================

    public TypeHolder(ModuleScope scope, CompileEnvironment instanceEnvironment) {
        myInstanceEnvironment = instanceEnvironment;
        this.scope = scope;
        this.err = instanceEnvironment.getErrorHandler();
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Contains Methods
    // -----------------------------------------------------------

    public boolean containsTypeB() {
        return (typeB != null);
    }

    public boolean containsTypeN() {
        return (typeN != null);
    }

    public boolean containsTypeZ() {
        return (typeZ != null);
    }

    public boolean containsTypeR() {
        return (typeR != null);
    }

    public boolean containsTypeStr() {
        return (typeStr != null);
    }

    public boolean containsTypeBoolean() {
        return (typeBoolean != null);
    }

    public boolean containsTypeInteger() {
        return (typeInteger != null);
    }

    public boolean containsTypeReal() {
        return (typeReal != null);
    }

    public boolean containsTypeChar() {
        return (typeChar != null);
    }

    public boolean containsTypeChar_Str() {
        return (typeChar_Str != null);
    }

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    public Type getTypeB() {
        return typeB;
    }

    public Type getTypeN() {
        return typeN;
    }

    public Type getTypeZ() {
        return typeZ;
    }

    public Type getTypeR() {
        return typeR;
    }

    public Type getTypeStr() {
        return typeStr;
    }

    public Type getTypeBoolean() {
        return typeBoolean;
    }

    public Type getTypeInteger() {
        return typeInteger;
    }

    public Type getTypeReal() {
        return typeReal;
    }

    public Type getTypeChar() {
        return typeChar;
    }

    public Type getTypeChar_Str() {
        return typeChar_Str;
    }

    // -----------------------------------------------------------
    // Location Methods
    // -----------------------------------------------------------

    public void searchForBuiltInTypes() {
        TypeID tid = null;
        tid = new TypeID(sym("Boolean_Theory"), sym("B"), 0);
        typeB = searchForType(tid);
        tid = new TypeID(sym("Natural_Theory"), sym("N"), 0);
        typeN = searchForType(tid);
        tid = new TypeID(sym("Integer_Theory"), sym("Z"), 0);
        typeZ = searchForType(tid);
        tid = new TypeID(sym("Real_Number_Theory"), sym("R"), 0);
        typeR = searchForType(tid);
        tid = new TypeID(sym("String_Theory"), sym("Str"), 1);
        typeStr = searchForType(tid);
        tid = new TypeID(sym("Std_Boolean_Fac"), sym("Boolean"), 0);
        typeBoolean = searchForType(tid);
        tid = new TypeID(sym("Std_Integer_Fac"), sym("Integer"), 0);
        typeInteger = searchForType(tid);
        tid = new TypeID(sym("Std_Real_Number_Fac"), sym("Real"), 0);
        typeReal = searchForType(tid);
        tid = new TypeID(sym("Std_Character_Fac"), sym("Character"), 0); //Changed this from Char to Character
        typeChar = searchForType(tid);
        tid = new TypeID(sym("Std_Char_Str_Fac"), sym("Char_Str"), 0);
        typeChar_Str = searchForType(tid);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Visible Built-in Types: ( ");
        if (typeB != null) {
            sb.append("B ");
        }
        if (typeN != null) {
            sb.append("N ");
        }
        if (typeZ != null) {
            sb.append("Z ");
        }
        if (typeR != null) {
            sb.append("R ");
        }
        if (typeStr != null) {
            sb.append("Str ");
        }
        if (typeBoolean != null) {
            sb.append("Boolean ");
        }
        if (typeInteger != null) {
            sb.append("Integer ");
        }
        if (typeReal != null) {
            sb.append("Real ");
        }
        if (typeChar != null) {
            sb.append("Character ");
        } //Changed this from Char to Character
        if (typeChar_Str != null) {
            sb.append("Char_Str ");
        }
        sb.append(")");
        return sb.toString();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    private Symbol sym(String str) {
        return Symbol.symbol(str);
    }

    public Type searchForType(TypeID tid) {
        Symbol moduleName = scope.getModuleID().getName();
        TypeLocator tlocator = new TypeLocator(scope, myInstanceEnvironment);
        if (moduleName == tid.getQualifier()) {
            if (scope.containsType(tid.getName())) {
                return scope.getType(tid.getName()).getType();
            }
        }
        else {
            try {
                return tlocator.locateMathType(tid).getType();
            }
            catch (SymbolSearchException ex) {
                return null;
            }
        }
        return null;
    }
}
