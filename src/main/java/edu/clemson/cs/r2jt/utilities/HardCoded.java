package edu.clemson.cs.r2jt.utilities;

import edu.clemson.cs.r2jt.absyn.FacilityModuleDec;
import edu.clemson.cs.r2jt.absyn.ModuleDec;
import edu.clemson.cs.r2jt.absyn.VarExp;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.typeandpopulate.DuplicateSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.MTFunction;
import edu.clemson.cs.r2jt.typeandpopulate.MTNamed;
import edu.clemson.cs.r2jt.typeandpopulate.MTPowertypeApplication;
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
            b.addBinding("Is_Initial", v,
                    new MTFunction(g, g.BOOLEAN, g.ENTITY));
            b.addBinding("SSet", v, g.MTYPE, g.SET);
            b.addBinding("B", v, g.MTYPE, g.BOOLEAN);

            b.addBinding("Empty_Set", v, g.MTYPE, g.EMPTY_SET);
            b.addBinding("Powerset", v, g.POWERTYPE);
            b.addBinding("true", v, g.BOOLEAN);
            b.addBinding("false", v, g.BOOLEAN);
            b.addBinding("union", v, g.UNION);
            b.addBinding("intersect", v, g.INTERSECT);
            b.addBinding("->", v, g.FUNCTION);
            b.addBinding("implies", v, g.BOOLEAN);
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
}
