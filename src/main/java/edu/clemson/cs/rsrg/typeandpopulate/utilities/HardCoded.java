package edu.clemson.cs.rsrg.typeandpopulate.utilities;

import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause.ClauseType;
import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.FacilityModuleDec;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ModuleParameterDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.cs.rsrg.absyn.items.programitems.UsesItem;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry.Quantification;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTFunction;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTNamed;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTPowertypeApplication;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.util.ArrayList;

public class HardCoded {

    /**
     * <p>This method establishes all built-in relationships of the symbol table.</p>
     *
     * @param g
     * @param b
     */
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

            AssertionClause requires = new AssertionClause(null, ClauseType.REQUIRES, VarExp.getTrueVarExp(null, g));
            ModuleDec module =
                    new FacilityModuleDec(null, new PosSymbol(null, "native"), new ArrayList<ModuleParameterDec>(), new ArrayList<UsesItem>(), requires, new ArrayList<Dec>());

            VarExp v = new VarExp(null, null, new PosSymbol(null, "native"));
            ScopeBuilder s = b.startModuleScope(module);

            s.addBinding("D1", Quantification.UNIVERSAL, v, g.CLS);
            s.addBinding("R1", Quantification.UNIVERSAL, v, g.CLS);

            s.addBinding("D2", Quantification.UNIVERSAL, v,
                    new MTPowertypeApplication(g, new MTNamed(g, "D1")));
            s.addBinding("R2", Quantification.UNIVERSAL, v,
                    new MTPowertypeApplication(g, new MTNamed(g, "R1")));

            s.addBinding("f", Quantification.UNIVERSAL, v, new MTFunction(g,
                    new MTNamed(g, "R2"), new MTNamed(g, "D2")));

            VarExp f = new VarExp(null, null, new PosSymbol(null, "f"), Quantification.UNIVERSAL);
            f.setMathType(new MTFunction(g, new MTNamed(g, "R2"), new MTNamed(
                    g, "D2")));

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
     *
     * @param g
     * @param b
     */
    public static void addBuiltInSymbols(TypeGraph g, ScopeBuilder b) {
        VarExp v = new VarExp(null, null, new PosSymbol(null, "native"));

        try {
            b.addBinding("Entity", v, g.CLS, g.ENTITY);
            b.addBinding("MType", v, g.CLS, g.CLS);
            b.addBinding("Cls", v, g.CLS, g.CLS);

            b.addBinding("Instance_Of", v, new MTFunction(g, g.BOOLEAN, g.CLS,
                    g.ENTITY));

            b.addBinding("SSet", v, g.CLS, g.CLS);
            b.addBinding("B", v, g.CLS, g.BOOLEAN);

            b.addBinding("Empty_Set", v, g.CLS, g.EMPTY_SET);
            b.addBinding("Powerset", v, g.POWERTYPE);
            b.addBinding("Powerclass", v, g.POWERCLASS);
            b.addBinding("true", v, g.BOOLEAN);
            b.addBinding("false", v, g.BOOLEAN);
            b.addBinding("cls_union", v, g.UNION);
            b.addBinding("cls_intersection", v, g.INTERSECT);
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
        }
        catch (DuplicateSymbolException dse) {
            //Not possible--we're the first ones to add anything
            throw new RuntimeException(dse);
        }
    }

    /**
     *
     * @param g
     * @param e
     * @param metaSegment
     *
     * @return
     */
    public static MTType getMetaFieldType(TypeGraph g, Exp e, String metaSegment) {
        MTType result = null;

        if (e.getMathTypeValue() != null && metaSegment.equals("Is_Initial")) {
            result = new MTFunction(g, g.BOOLEAN, g.ENTITY);
        }

        return result;
    }
	
}
