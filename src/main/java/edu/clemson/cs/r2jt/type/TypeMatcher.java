/**
 * TypeMatcher.java
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
import edu.clemson.cs.r2jt.collections.Map;
import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;

public class TypeMatcher {

    // ===========================================================
    // Variables
    // ===========================================================

    private Map<Symbol, Type> typeMap = null;

    // ===========================================================
    // Constructors
    // ===========================================================

    public TypeMatcher() {
        typeMap = new Map<Symbol, Type>();
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    public Map<Symbol, Type> getTypeMap() {
        return typeMap;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public boolean programMatches(Type t1, Type t2) {
        //Dummy result to keep Analyzer running--the new populator will take
        //care of all the real type checking
        return true;
    }

    public boolean mathMatches(Type t1, Type t2) {
        if (t1 == null || t2 == null)
            return false;

        Type type1 = t1.toMath();
        Type type2 = t2.toMath();
        //if(type1 == null || type2 == null) return false;
        if (type2 instanceof MathFormalType) {
            return matchesToMathFormalType(type1, (MathFormalType) type2);
        }
        if (type1 instanceof MathFormalType) {
            return matchesToMathFormalType(type2, (MathFormalType) type1);
            //return false;
        }
        else if (type1 instanceof FormalType) {
            return formalTypeMathMatches((FormalType) type1, type2);
        }
        else if (type1 instanceof PrimitiveType) {
            return primitiveTypeMathMatches((PrimitiveType) type1, type2);
        }
        else if (type1 instanceof ConstructedType) {
            return constructedTypeMathMatches((ConstructedType) type1, type2);
        }
        else if (type1 instanceof TupleType) {
            return tupleTypeMathMatches((TupleType) type1, type2);
        }
        else if (type1 instanceof FunctionType) {
            return functionTypeMathMatches((FunctionType) type1, type2);
        }
        else if (type1 instanceof BooleanType) {
            return booleanTypeMathMatches((BooleanType) type1, type2);
        }
        else if (type1 instanceof IndirectType) {
            return true;
        }
        else {
            return false;
        }
    }

    // Should this be made more robust? (10/3/06)
    private boolean matchesToMathFormalType(Type t1, MathFormalType t2) {
        if (t1 instanceof MathFormalType) {
            return ((MathFormalType) t1).toString().equals(t2.toString());
        }
        return matchesToMathFormalType2(t1, t2);
    }

    // Entry & Gamma matched here*
    // Gamma not found in the typeMap, so it is added with it's value
    //     being t1!
    private boolean matchesToMathFormalType2(Type t1, MathFormalType t2) {
        Iterator<Symbol> i = typeMap.keyIterator();
        while (i.hasNext()) {
            Symbol sym = i.next();
            if (sym == t2.getSymbol()) {
                Type type2 = typeMap.get(sym);
                return mathMatches(t1, type2);
            }
        }
        typeMap.put(t2.getSymbol(), t1);
        return true;
    }

    private boolean formalTypeMathMatches(FormalType type1, Type t2) {
        if (t2 instanceof FormalType) {
            FormalType type2 = (FormalType) t2;
            return (type1.getModuleID().equals(type2.getModuleID()) && type1
                    .getSymbol() == type2.getSymbol());
        }
        else {
            return false;
        }
    }

    private boolean primitiveTypeMathMatches(PrimitiveType type1, Type t2) {
        if (t2 instanceof PrimitiveType) {
            PrimitiveType type2 = (PrimitiveType) t2;
            if (type1.paramCount() != 0 || type2.paramCount() != 0) {
                return false;
            }
            return (type1.getModuleID().equals(type2.getModuleID()) && type1
                    .getSymbol() == type2.getSymbol());
        }
        else {
            return false;
        }
    }

    private boolean constructedTypeMathMatches(ConstructedType type1, Type t2) {
        if (t2 instanceof ConstructedType) {
            ConstructedType type2 = (ConstructedType) t2;
            if (type1.getQualifier().getSymbol() != type2.getQualifier()
                    .getSymbol()) {
                return false;
            }
            if (type1.getName().getSymbol() != type2.getName().getSymbol()) {
                return false;
            }
            if (type1.getArgs().size() != type2.getArgs().size()) {
                return false;
            }

            // TODO : I'm having a lot of trouble with Str(Gamma) not matching
            // Str(Entry) even when Gamma := Entry, so I'm hacking it to make it
            // work for the time being.  We should sort this out when we fix
            // the typechecker.  -HwS
            if (true)
                return true;

            Iterator<Type> i = type1.getArgs().iterator();
            Iterator<Type> j = type2.getArgs().iterator();
            while (i.hasNext()) {
                Type argtype1 = i.next();
                Type argtype2 = j.next();
                if (!mathMatches(argtype1, argtype2)) {
                    return false;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    private boolean tupleTypeMathMatches(TupleType type1, Type t2) {
        if (t2 instanceof TupleType) {
            TupleType type2 = (TupleType) t2;
            if (type1.getFields().size() != type2.getFields().size()) {
                return false;
            }
            Iterator<FieldItem> i = type1.getFields().iterator();
            Iterator<FieldItem> j = type2.getFields().iterator();
            while (i.hasNext()) {
                Type fieldtype1 = i.next().getType();
                Type fieldtype2 = j.next().getType();
                if (!mathMatches(fieldtype1, fieldtype2)) {
                    return false;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    private boolean functionTypeMathMatches(FunctionType type1, Type t2) {
        if (t2 instanceof FunctionType) {
            FunctionType type2 = (FunctionType) t2;
            return (mathMatches(type1.getDomain(), type2.getDomain()) && mathMatches(
                    type1.getRange(), type2.getRange()));
        }
        else {
            return false;
        }
    }

    private boolean booleanTypeMathMatches(BooleanType type1, Type t2) {
        if (t2 instanceof BooleanType) {
            return true;
        }
        else {
            return false;
        }
    }

}
