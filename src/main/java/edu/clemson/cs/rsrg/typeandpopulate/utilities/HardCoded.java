/**
 * HardCoded.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
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
import edu.clemson.cs.rsrg.init.file.ModuleType;
import edu.clemson.cs.rsrg.init.file.ResolveFile;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.StatusHandler;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry.Quantification;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTFunction;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTNamed;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTPowerclassApplication;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ScopeBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import org.antlr.v4.runtime.ANTLRInputStream;

/**
 * <p>The <code>HardCoded</code> class defines all mathematical symbols
 * and relationships that cannot be put into a {@code Precis} module.</p>
 *
 * @version 2.0
 */
public class HardCoded {

    /**
     * <p>Since all of our {@link StatusHandler} requires a {@link Location} to work
     * properly, we create an object pointing to an input stream that is empty.
     * Subsequently, we use this object to create all of our built in type information.</p>
     */
    private static final Location NATIVE_FILE_FAKE_LOCATION;

    static {
        try {
            NATIVE_FILE_FAKE_LOCATION =
                    new Location(new ResolveFile("native", ModuleType.THEORY,
                            new ANTLRInputStream(new StringReader("")),
                            new ArrayList<String>(), ""), 0, 0, "");
        }
        catch (IOException e) {
            throw new MiscErrorException("Error instantiating native file", e);
        }
    }

    /**
     * <p>This method establishes all built-in relationships of the symbol table.</p>
     *
     * @param g The current type graph.
     * @param b The current scope repository builder.
     */
    public static void addBuiltInRelationships(TypeGraph g,
            MathSymbolTableBuilder b) {
        try {
            //This is just a hard-coded version of this theoretical type theorem
            //that can't actually appear in a theory because it won't type-check
            //(it requires itself to typecheck):

            //Type Theorem Function_Subtypes:
            //   For all D1, R1 : Cls,
            //   For all D2 : Powerclass(D1),
            //   For all R2 : Powerclass(R1),
            //   For all f : D2 -> R2,
            //       f : D1 -> R1;

            AssertionClause requires =
                    new AssertionClause(NATIVE_FILE_FAKE_LOCATION,
                            ClauseType.REQUIRES, VarExp.getTrueVarExp(
                                    NATIVE_FILE_FAKE_LOCATION, g));
            ModuleDec module =
                    new FacilityModuleDec(NATIVE_FILE_FAKE_LOCATION,
                            new PosSymbol(NATIVE_FILE_FAKE_LOCATION, "native"),
                            new ArrayList<ModuleParameterDec>(),
                            new ArrayList<UsesItem>(), requires,
                            new ArrayList<Dec>(),
                            new HashMap<PosSymbol, Boolean>());

            VarExp v =
                    new VarExp(NATIVE_FILE_FAKE_LOCATION, null, new PosSymbol(
                            NATIVE_FILE_FAKE_LOCATION, "native"));
            ScopeBuilder s = b.startModuleScope(module);

            s.addBinding("D1", Quantification.UNIVERSAL, v, g.CLS);
            s.addBinding("R1", Quantification.UNIVERSAL, v, g.CLS);

            s.addBinding("D2", Quantification.UNIVERSAL, v,
                    new MTPowerclassApplication(g, new MTNamed(g, "D1")));
            s.addBinding("R2", Quantification.UNIVERSAL, v,
                    new MTPowerclassApplication(g, new MTNamed(g, "R1")));

            s.addBinding("f", Quantification.UNIVERSAL, v, new MTFunction(g,
                    new MTNamed(g, "R2"), new MTNamed(g, "D2")));

            VarExp f =
                    new VarExp(null, null, new PosSymbol(null, "f"),
                            Quantification.UNIVERSAL);
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
     * @param g The current type graph.
     * @param b The current scope repository builder.
     */
    public static void addBuiltInSymbols(TypeGraph g, ScopeBuilder b) {
        VarExp v =
                new VarExp(NATIVE_FILE_FAKE_LOCATION, null, new PosSymbol(
                        NATIVE_FILE_FAKE_LOCATION, "native"));

        try {
            // built-in symbols
            b.addBinding("Entity", v, g.CLS, g.ENTITY);
            b.addBinding("Element", v, g.CLS, g.ELEMENT);
            b.addBinding("Cls", v, g.CLS, g.CLS);
            b.addBinding("SSet", v, g.CLS, g.SSET);
            b.addBinding("B", v, g.CLS, g.BOOLEAN);
            b.addBinding("Empty_Class", v, g.CLS, g.EMPTY_CLASS);
            b.addBinding("Empty_Set", v, g.SSET, g.EMPTY_SET);

            // built-in symbols that are defined as a function
            b.addBinding("Instance_Of", v, new MTFunction(g, g.BOOLEAN, g.CLS,
                    g.ENTITY));
            b.addBinding("Powerset", v, g.POWERSET);
            b.addBinding("Powerclass", v, g.POWERCLASS);
            b.addBinding("cls_union", v, g.UNION);
            b.addBinding("cls_intersection", v, g.INTERSECT);
            b.addBinding("->", v, g.FUNCTION);
            b.addBinding("*", v, g.CROSS);

            // TODO: Candidates for removal and add to Boolean_Theory? -YS
            b.addBinding("true", v, g.BOOLEAN);
            b.addBinding("false", v, g.BOOLEAN);
            b.addBinding("not", v, new MTFunction(g, g.BOOLEAN, g.BOOLEAN));
            b.addBinding("and", v, new MTFunction(g, g.BOOLEAN, g.BOOLEAN,
                    g.BOOLEAN));
            b.addBinding("or", v, new MTFunction(g, g.BOOLEAN, g.BOOLEAN,
                    g.BOOLEAN));
            b.addBinding("=", v, new MTFunction(g, g.BOOLEAN, g.ENTITY,
                    g.ENTITY));
            b.addBinding("/=", v, new MTFunction(g, g.BOOLEAN, g.ENTITY,
                    g.ENTITY));
        }
        catch (DuplicateSymbolException dse) {
            //Not possible--we're the first ones to add anything
            throw new RuntimeException(dse);
        }
    }

    /**
     * <p>This method returns the mathematical function used to represent an
     * expression's meta-segment.</p>
     *
     * @param g The current type graph.
     * @param e An {@link Exp}.
     * @param metaSegment A string representing a meta-segment for
     *                    <code>e</code>.
     *
     * @return A {@link MTType} if we can establish its type,
     * <code>null</code> otherwise.
     */
    public static MTType getMetaFieldType(TypeGraph g, Exp e, String metaSegment) {
        MTType result = null;

        if (e.getMathTypeValue() != null && metaSegment.equals("Is_Initial")) {
            result = new MTFunction(g, g.BOOLEAN, g.ENTITY);
        }

        return result;
    }

}
