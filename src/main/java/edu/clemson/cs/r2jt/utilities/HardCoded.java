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
package edu.clemson.cs.r2jt.utilities;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.FacilityModuleDec;
import edu.clemson.cs.r2jt.absyn.ModuleDec;
import edu.clemson.cs.r2jt.absyn.VarExp;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.typeandpopulate.DuplicateSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.MTFunction;
import edu.clemson.cs.r2jt.typeandpopulate.MTNamed;
import edu.clemson.cs.r2jt.typeandpopulate.MTPowertypeApplication;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTableBuilder;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeBuilder;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry.Quantification;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

public class HardCoded {

    public static void addBuiltInRelationships(TypeGraph g,
            MathSymbolTableBuilder b) {
        try {
            //This is just a hard-coded version of this theoretical type theorem
            //that can't actually appear in a theory because it won't type-check
            //(it requires itself to typecheck):

            //Type Theorem Function_Subtypes:
            //   For all D1, R1 : MType,
            //   For all D2 : Powerset(D1),
            //   For all R2 : Powerset(R1),
            //   For all f : D2 -> R2,
            //       f : D1 -> R1;

            PosSymbol ntv = new PosSymbol(null, Symbol.symbol("native"));
            ModuleDec module =
                    new FacilityModuleDec(ntv, null, null, null, null, null);

            VarExp v = new VarExp();
            v.setName(ntv);
            ScopeBuilder s = b.startModuleScope(module);

            s.addBinding("D1", Quantification.UNIVERSAL, v, g.MTYPE);
            s.addBinding("R1", Quantification.UNIVERSAL, v, g.MTYPE);

            s.addBinding("D2", Quantification.UNIVERSAL, v,
                    new MTPowertypeApplication(g, new MTNamed(g, "D1")));
            s.addBinding("R2", Quantification.UNIVERSAL, v,
                    new MTPowertypeApplication(g, new MTNamed(g, "R1")));

            s.addBinding("f", Quantification.UNIVERSAL, v, new MTFunction(g,
                    new MTNamed(g, "R2"), new MTNamed(g, "D2")));

            PosSymbol fSym = new PosSymbol(null, Symbol.symbol("f"));
            VarExp f = new VarExp();
            f.setName(fSym);
            f.setMathType(new MTFunction(g, new MTNamed(g, "R2"), new MTNamed(
                    g, "D2")));
            f.setQuantification(VarExp.FORALL);

            g.addRelationship(f, new MTFunction(g, new MTNamed(g, "R1"),
                    new MTNamed(g, "D1")), null, s);

            b.endScope();
        }
        catch (DuplicateSymbolException dse) {
            //Not possible--we're the first ones to add anything
            throw new RuntimeException(dse);
        }
    }

    /**
     * <p>This method establishes all built-in symbols of the symbol table.</p>
     */
    public static void addBuiltInSymbols(TypeGraph g, ScopeBuilder b) {
        VarExp v = new VarExp();
        v.setName(new PosSymbol(null, Symbol.symbol("native")));

        try {
            b.addBinding("Entity", v, g.MTYPE, g.ENTITY);
            b.addBinding("MType", v, g.MTYPE, g.MTYPE);

            b.addBinding("Instance_Of", v, new MTFunction(g, g.BOOLEAN,
                    g.MTYPE, g.ENTITY));

            b.addBinding("SSet", v, g.MTYPE, g.SET);
            b.addBinding("B", v, g.MTYPE, g.BOOLEAN);

            b.addBinding("Empty_Set", v, g.MTYPE, g.EMPTY_SET);
            b.addBinding("Powerset", v, g.POWERTYPE);
            b.addBinding("true", v, g.BOOLEAN);
            b.addBinding("false", v, g.BOOLEAN);
            b.addBinding("union", v, g.UNION);
            b.addBinding("intersect", v, g.INTERSECT);
            b.addBinding("->", v, g.FUNCTION);
            b.addBinding("and", v, g.AND);
            b.addBinding("not", v, g.NOT);
            b.addBinding("*", v, g.CROSS);

            b.addBinding("=", v, new MTFunction(g, g.BOOLEAN, g.ENTITY,
                    g.ENTITY));
            b.addBinding("/=", v, new MTFunction(g, g.BOOLEAN, g.ENTITY,
                    g.ENTITY));
            b.addBinding("or", v, new MTFunction(g, g.BOOLEAN, g.BOOLEAN,
                    g.BOOLEAN));

            b.addBinding("Z", v, g.MTYPE, g.Z);
            b.addBinding("-", v, new MTFunction(g, g.Z, g.Z));
        }
        catch (DuplicateSymbolException dse) {
            //Not possible--we're the first ones to add anything
            throw new RuntimeException(dse);
        }
    }

    public static MTType getMetaFieldType(TypeGraph g, Exp e, String metaSegment) {

        MTType result = null;

        if (e.getMathTypeValue() != null && metaSegment.equals("Is_Initial")) {

            result = new MTFunction(g, g.BOOLEAN, g.ENTITY);
        }

        return result;
    }
}
