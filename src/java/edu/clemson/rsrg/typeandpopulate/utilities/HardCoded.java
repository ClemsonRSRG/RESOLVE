/*
 * HardCoded.java
 * ---------------------------------
 * Copyright (c) 2022
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.typeandpopulate.utilities;

import edu.clemson.rsrg.absyn.declarations.Dec;
import edu.clemson.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.rsrg.absyn.declarations.moduledecl.PrecisModuleDec;
import edu.clemson.rsrg.absyn.declarations.paramdecl.ModuleParameterDec;
import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.DotExp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.TupleExp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.rsrg.absyn.items.programitems.UsesItem;
import edu.clemson.rsrg.init.file.ModuleType;
import edu.clemson.rsrg.init.file.ResolveFile;
import edu.clemson.rsrg.init.file.ResolveFileBasicInfo;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.parsing.data.PosSymbol;
import edu.clemson.rsrg.typeandpopulate.entry.SymbolTableEntry.Quantification;
import edu.clemson.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.rsrg.typeandpopulate.mathtypes.*;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.rsrg.typeandpopulate.symboltables.ScopeBuilder;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.List;
import org.antlr.v4.runtime.UnbufferedCharStream;

/**
 * <p>
 * The <code>HardCoded</code> class defines all mathematical symbols and relationships that are built into the compiler
 * and acts as the base for where types, definitions, theorems, etc.
 * </p>
 *
 * @version 2.0
 */
public class HardCoded {

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method establishes all built-in relationships of the symbol table.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param b
     *            The current scope repository builder.
     */
    public static void addBuiltInRelationships(TypeGraph g, MathSymbolTableBuilder b) {
        try {
            // YS: Everything we build here could be written in a file called Cls_Theory,
            // but since right now the whole math theory files and type system is still
            // being hashed out, we hard code these. At some point someone should revisit
            // this and see if it can be moved to a physical file.
            Location classTheoryLoc = new Location(
                    new ResolveFile(new ResolveFileBasicInfo("Cls_Theory", ""), ModuleType.THEORY,
                            new UnbufferedCharStream(new StringReader("")), null, new ArrayList<String>(), ""),
                    0, 0);
            ModuleDec module = new PrecisModuleDec(classTheoryLoc.clone(),
                    new PosSymbol(classTheoryLoc.clone(), "Cls_Theory"), new ArrayList<ModuleParameterDec>(),
                    new ArrayList<UsesItem>(), new ArrayList<Dec>(),
                    new LinkedHashMap<ResolveFileBasicInfo, Boolean>());
            ScopeBuilder s = b.startModuleScope(module);

            // Since adding a binding require something of ResolveConceptualElement,
            // we associate everything with a VarExp.
            VarExp v = new VarExp(classTheoryLoc.clone(), null, new PosSymbol(classTheoryLoc.clone(), "Cls_Theory"));

            // Built-in functions
            s.addBinding("Instance_Of", v, new MTFunction(g, g.BOOLEAN, g.CLS, g.ENTITY));
            s.addBinding("Powerclass", v, g.POWERCLASS);
            s.addBinding("union", v, g.UNION);
            s.addBinding("intersection", v, g.INTERSECT);
            s.addBinding("->", v, g.CLS_FUNCTION);
            s.addBinding("*", v, g.CLS_CROSS);

            // This is just a hard-coded version of this theoretical type theorem
            // that can't actually appear in a theory because it won't type-check
            // (it requires itself to typecheck):

            // Type Theorem Function_Subtypes:
            // For all D1, R1 : Cls,
            // For all D2 : Powerclass(D1),
            // For all R2 : Powerclass(R1),
            // For all f : D2 -> R2,
            // f : D1 -> R1;
            VarExp typeTheorem1 = new VarExp(classTheoryLoc.clone(), null,
                    new PosSymbol(classTheoryLoc.clone(), "Function_Subtypes"));
            ScopeBuilder typeTheorem1Scope = b.startScope(typeTheorem1);

            // Various binding
            typeTheorem1Scope.addBinding("D1", Quantification.UNIVERSAL, v, g.CLS);
            typeTheorem1Scope.addBinding("R1", Quantification.UNIVERSAL, v, g.CLS);
            typeTheorem1Scope.addBinding("D2", Quantification.UNIVERSAL, v,
                    new MTPowerclassApplication(g, new MTNamed(g, "D1")));
            typeTheorem1Scope.addBinding("R2", Quantification.UNIVERSAL, v,
                    new MTPowerclassApplication(g, new MTNamed(g, "R1")));
            typeTheorem1Scope.addBinding("f", Quantification.UNIVERSAL, v,
                    new MTFunction(g, new MTNamed(g, "R2"), new MTNamed(g, "D2")));

            // VarExp refering to function f
            VarExp f = new VarExp(classTheoryLoc.clone(), null, new PosSymbol(classTheoryLoc.clone(), "f"),
                    Quantification.UNIVERSAL);
            f.setMathType(new MTFunction(g, new MTNamed(g, "R2"), new MTNamed(g, "D2")));

            // Add relationship and close typeTheorem21 scope
            g.addRelationship(f, new MTFunction(g, new MTNamed(g, "R1"), new MTNamed(g, "D1")), null,
                    typeTheorem1Scope);
            b.endScope();

            // Type Theorem Card_Prod_Thingy:
            // For all T1, T2 : Cls,
            // For all R1 : Powerclass(T1),
            // For all R2 : Powerclass(T2),
            // For all r1 : R1,
            // For all r2 : R2,
            // (r1, r2) : (T1 * T2);
            VarExp typeTheorem2 = new VarExp(classTheoryLoc.clone(), null,
                    new PosSymbol(classTheoryLoc.clone(), "Card_Prod_Thingy"));
            ScopeBuilder typeTheorem2Scope = b.startScope(typeTheorem2);

            // Various binding
            typeTheorem2Scope.addBinding("T1", Quantification.UNIVERSAL, v, g.CLS);
            typeTheorem2Scope.addBinding("T2", Quantification.UNIVERSAL, v, g.CLS);
            typeTheorem2Scope.addBinding("R1", Quantification.UNIVERSAL, v,
                    new MTPowerclassApplication(g, new MTNamed(g, "T1")));
            typeTheorem2Scope.addBinding("R2", Quantification.UNIVERSAL, v,
                    new MTPowerclassApplication(g, new MTNamed(g, "T2")));
            typeTheorem2Scope.addBinding("r1", Quantification.UNIVERSAL, v, new MTNamed(g, "R1"));
            typeTheorem2Scope.addBinding("r2", Quantification.UNIVERSAL, v, new MTNamed(g, "R2"));

            // Binding type
            List<MTCartesian.Element> bindingTypeElements = new LinkedList<>();
            bindingTypeElements.add(new MTCartesian.Element(new MTNamed(g, "T1")));
            bindingTypeElements.add(new MTCartesian.Element(new MTNamed(g, "T2")));
            MTCartesian bindingType = new MTCartesian(g, bindingTypeElements);

            // Fields inside the tuple
            List<Exp> tupleExps = new LinkedList<>();
            VarExp r1 = new VarExp(classTheoryLoc.clone(), null, new PosSymbol(classTheoryLoc.clone(), "r1"),
                    Quantification.UNIVERSAL);
            r1.setMathType(new MTNamed(g, "R1"));
            tupleExps.add(r1);

            VarExp r2 = new VarExp(classTheoryLoc.clone(), null, new PosSymbol(classTheoryLoc.clone(), "r2"),
                    Quantification.UNIVERSAL);
            r2.setMathType(new MTNamed(g, "R2"));
            tupleExps.add(r2);

            // Create the tuple and create it's type
            TupleExp tupleExp = new TupleExp(classTheoryLoc.clone(), tupleExps);
            List<MTCartesian.Element> fieldTypes = new LinkedList<>();
            fieldTypes.add(new MTCartesian.Element(new MTNamed(g, "R1")));
            fieldTypes.add(new MTCartesian.Element(new MTNamed(g, "R2")));
            MTCartesian tupleType = new MTCartesian(g, fieldTypes);
            tupleExp.setMathType(tupleType);

            // Add relationship and close typeTheorem2 scope
            g.addRelationship(tupleExp, bindingType, null, typeTheorem2Scope);
            b.endScope();

            // Close module scope
            b.endScope();
        } catch (DuplicateSymbolException dse) {
            // Not possible--we're the first ones to add anything
            throw new RuntimeException(dse);
        }
    }

    /**
     * <p>
     * This method establishes all built-in symbols of the symbol table.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param b
     *            The current scope repository builder.
     */
    public static void addBuiltInSymbols(TypeGraph g, ScopeBuilder b) {
        try {
            // YS: Everything we build here lives in a global namespace. This means
            // that we don't need to import anything to access any of these symbols.
            // Since adding a binding require something of ResolveConceptualElement,
            // we associate everything with a VarExp.
            Location globalSpaceLoc = new Location(
                    new ResolveFile(new ResolveFileBasicInfo("Global", ""), ModuleType.THEORY,
                            new UnbufferedCharStream(new StringReader("")), null, new ArrayList<String>(), ""),
                    0, 0);
            VarExp v = new VarExp(globalSpaceLoc, null, new PosSymbol(globalSpaceLoc, "Global"));

            // built-in symbols
            b.addBinding("Entity", v, g.CLS, g.ENTITY);
            b.addBinding("Element", v, g.CLS, g.ELEMENT);
            b.addBinding("Cls", v, g.CLS, g.CLS);
            b.addBinding("SSet", v, g.CLS, g.SSET);
            b.addBinding("B", v, g.SSET, g.BOOLEAN);
            b.addBinding("Empty_Class", v, g.CLS, g.EMPTY_CLASS);
            b.addBinding("Empty_Set", v, g.SSET, g.EMPTY_SET);
            b.addBinding("true", v, g.BOOLEAN);
            b.addBinding("false", v, g.BOOLEAN);

            // built-in symbols that are defined as a function
            // These must be built in for our compiler to function correctly.
            b.addBinding("Powerset", v, g.POWERSET);
            b.addBinding("->", v, g.SSET_FUNCTION);
            b.addBinding("*", v, g.SSET_CROSS);
            b.addBinding("not", v, new MTFunction(g, g.BOOLEAN, g.BOOLEAN));
            b.addBinding("and", v, new MTFunction(g, g.BOOLEAN, g.BOOLEAN, g.BOOLEAN));
            b.addBinding("or", v, new MTFunction(g, g.BOOLEAN, g.BOOLEAN, g.BOOLEAN));
            b.addBinding("=", v, new MTFunction(g, g.BOOLEAN, g.ENTITY, g.ENTITY));
            b.addBinding("/=", v, new MTFunction(g, g.BOOLEAN, g.ENTITY, g.ENTITY));
        } catch (DuplicateSymbolException dse) {
            // Not possible--we're the first ones to add anything
            throw new RuntimeException(dse);
        }
    }

    /**
     * <p>
     * This method returns the mathematical function used to represent an expression's meta-segment.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param lastExp
     *            The last typed {@link Exp} in a {@link DotExp}.
     * @param currentExpName
     *            Name of {@code currentExp}.
     *
     * @return A {@link MTType} if it is one of the special meta segments, <code>null</code> otherwise.
     */
    public static MTType getMetaFieldType(TypeGraph g, Exp lastExp, String currentExpName) {
        MTType result = null;

        if (lastExp.getMathTypeValue() != null) {
            switch (currentExpName) {
            case "Is_Initial":
                result = new MTFunction(g, g.BOOLEAN, lastExp.getMathTypeValue());
                break;
            case "Val_in":
                result = new MTFunction(g, lastExp.getMathTypeValue(), g.RECEPTACLES);
                break;
            }
        }

        return result;
    }

}
