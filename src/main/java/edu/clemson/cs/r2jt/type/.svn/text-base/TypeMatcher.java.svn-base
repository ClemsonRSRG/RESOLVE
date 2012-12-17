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
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer. 
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution. 
 *   * Neither the name of the Clemson University nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission. 
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
 * Clemson University.  Contributors to the initial version are:
 * 
 *     Steven Atkinson
 *     Greg Kulczycki
 *     Kunal Chopra
 *     John Hunt
 *     Heather Keown
 *     Ben Markle
 *     Kim Roche
 *     Murali Sitaraman
 */
/*
 * TypeMatcher.java
 *
 * The Resolve Software Composition Workbench Project
 *
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
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
        if (t1 instanceof VoidType && t2 instanceof VoidType) {
            return true;
        }
        TypeName tn1 = t1.getProgramName();
        TypeName tn2 = t2.getProgramName();
        if (tn1 == null || tn2 == null) { return false; }
        return (tn1.equals(tn2));
    }

    public boolean mathMatches(Type t1, Type t2) {
        if (t1 == null || t2 == null) return false;
    	
    	Type type1 = t1.toMath();
        Type type2 = t2.toMath();
        //if(type1 == null || type2 == null) return false;
        if (type2 instanceof MathFormalType) {
            return matchesToMathFormalType(type1, (MathFormalType)type2);
        }
        if (type1 instanceof MathFormalType) {
        	return matchesToMathFormalType(type2, (MathFormalType)type1);
            //return false;
        } else if (type1 instanceof FormalType) {
            return formalTypeMathMatches((FormalType)type1, type2);
        } else if (type1 instanceof PrimitiveType) {
            return primitiveTypeMathMatches((PrimitiveType)type1, type2);
        } else if (type1 instanceof ConstructedType) {
            return constructedTypeMathMatches((ConstructedType)type1, type2);
        } else if (type1 instanceof TupleType) {
            return tupleTypeMathMatches((TupleType)type1, type2);
        } else if (type1 instanceof FunctionType) {
            return functionTypeMathMatches((FunctionType)type1, type2);
        } else if (type1 instanceof BooleanType) {
            return booleanTypeMathMatches((BooleanType)type1, type2);
        } else {
            return false;
        }
    }
    
    // Should this be made more robust? (10/3/06)
    private boolean matchesToMathFormalType(Type t1, MathFormalType t2) {
    	if(t1 instanceof MathFormalType) {
     		return ((MathFormalType)t1).toString().equals(t2.toString());
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
            FormalType type2 = (FormalType)t2;
            return (type1.getModuleID().equals(type2.getModuleID()) &&
                    type1.getSymbol() == type2.getSymbol());
        } else {
            return false;
        }
    }

    private boolean primitiveTypeMathMatches(PrimitiveType type1, Type t2) {
        if (t2 instanceof PrimitiveType) {
            PrimitiveType type2 = (PrimitiveType)t2;
            if (  type1.paramCount() != 0 ||
                  type2.paramCount() != 0) {
                return false;
            }
            return (type1.getModuleID().equals(type2.getModuleID()) &&
                    type1.getSymbol() == type2.getSymbol());
        } else {
            return false;
        }
    }

    private boolean constructedTypeMathMatches(ConstructedType type1,
                                               Type t2) {
        if (t2 instanceof ConstructedType) {
            ConstructedType type2 = (ConstructedType)t2;
            if (type1.getQualifier().getSymbol()
                    != type2.getQualifier().getSymbol()) {
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
            if (true) return true;
            
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
        } else {
            return false;
        }
    }

    private boolean tupleTypeMathMatches(TupleType type1, Type t2) {
        if (t2 instanceof TupleType) {
            TupleType type2 = (TupleType)t2;
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
        } else {
            return false;
        }
    }

    private boolean functionTypeMathMatches(FunctionType type1, Type t2) {
        if (t2 instanceof FunctionType) {
            FunctionType type2 = (FunctionType)t2;
            return (  mathMatches(type1.getDomain(), type2.getDomain()) &&
                      mathMatches(type1.getRange(), type2.getRange()));
        } else {
            return false;
        }
    }
    
    private boolean booleanTypeMathMatches(BooleanType type1, Type t2) {
        if (t2 instanceof BooleanType) {
        	return true;
        } else {
            return false;
        }
    }
                    
}


