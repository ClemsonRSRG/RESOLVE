/**
 * ScopeBuilder.java
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
package edu.clemson.cs.rsrg.typeandpopulate.symboltables;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.cs.rsrg.absyn.declarations.mathdecl.MathAssertionDec;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.FacilityTypeRepresentationDec;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.TypeRepresentationDec;
import edu.clemson.cs.rsrg.absyn.items.mathitems.SpecInitFinalItem;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.typeandpopulate.entry.*;
import edu.clemson.cs.rsrg.typeandpopulate.entry.ProgramParameterEntry.ParameterMode;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTFacilityRepresentation;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTFamily;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTInstantiated;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>A <code>ScopeBuilder</code> is a working, mutable realization of
 * {@link Scope}.</p>
 *
 * <p>Note that <code>ScopeBuilder</code> has no public constructor.
 * <code>ScopeBuilders</code>s are acquired through calls to some of the methods
 * of {@link MathSymbolTableBuilder}.</p>
 *
 * @version 2.0
 */
public class ScopeBuilder extends SyntacticScope {

    // ===========================================================
    // Member Fields
    // ===========================================================

    private final List<ScopeBuilder> myChildren = new LinkedList<>();

    private final TypeGraph myTypeGraph;

    // ==========================================================
    // Constructors
    // ==========================================================

    ScopeBuilder(MathSymbolTableBuilder b, TypeGraph g, ResolveConceptualElement definingElement,
                 Scope parent, ModuleIdentifier enclosingModule) {
        super(b, definingElement, parent, enclosingModule, new BaseSymbolTable());
        myTypeGraph = g;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================


    /**
     * <p>Modifies the current working scope to add a new binding for a
     * symbol with an unqualified name, <code>name</code>, defined by the AST
     * node <code>definingElement</code> and of type <code>type</code>.</p>
     *
     * @param name The unqualified name of the symbol.
     * @param definingElement The AST Node that introduced the symbol.
     * @param type The declared type of the symbol.
     * @param typeValue The type assigned to the symbol (can be null).
     *
     * @throws DuplicateSymbolException If such a symbol is already defined
     * directly in the scope represented by this <code>ScopeBuilder</code>.
     * Note that this exception is not thrown if the symbol is defined in
     * a parent scope or an imported module.
     */
    public final MathSymbolEntry addBinding(String name, SymbolTableEntry.Quantification q,
            ResolveConceptualElement definingElement, MTType type, MTType typeValue,
            Map<String, MTType> schematicTypes, Map<String, MTType> genericsInDefiningContext)
            throws DuplicateSymbolException {
        sanityCheckBindArguments(name, definingElement, type);

        MathSymbolEntry entry =
                new MathSymbolEntry(myTypeGraph, name, q, definingElement,
                        type, typeValue, schematicTypes,
                        genericsInDefiningContext, myRootModule);

        myBindings.put(name, entry);

        return entry;
    }

    public final MathSymbolEntry addBinding(String name, SymbolTableEntry.Quantification q,
            ResolveConceptualElement definingElement, MTType type)
            throws DuplicateSymbolException {
        return addBinding(name, q, definingElement, type, null, null, null);
    }

    public final MathSymbolEntry addBinding(String name, ResolveConceptualElement definingElement,
            MTType type, MTType typeValue)
            throws DuplicateSymbolException {
        return addBinding(name, SymbolTableEntry.Quantification.NONE, definingElement, type, typeValue, null, null);
    }

    public final MathSymbolEntry addBinding(String name, ResolveConceptualElement definingElement, MTType type)
            throws DuplicateSymbolException {
        return addBinding(name, SymbolTableEntry.Quantification.NONE, definingElement, type);
    }

    public final FacilityEntry addFacility(FacilityDec facility)
            throws DuplicateSymbolException {
        SymbolTableEntry curLocalEntry =
                myBindings.get(facility.getName().getName());
        if (curLocalEntry != null) {
            throw new DuplicateSymbolException("Found two matching entries!", curLocalEntry);
        }

        FacilityEntry entry =
                new FacilityEntry(facility, myRootModule, getSourceRepository());

        myBindings.put(facility.getName().getName(), entry);

        return entry;
    }

    public final FacilityTypeRepresentationEntry addFacilityRepresentationEntry(String name,
            FacilityTypeRepresentationDec definingElement, PTInstantiated representationType,
            AssertionClause convention)
            throws DuplicateSymbolException {
        sanityCheckBindArguments(name, definingElement, "");

        FacilityTypeRepresentationEntry result = new FacilityTypeRepresentationEntry(name, definingElement,
                myRootModule, new PTFacilityRepresentation(myTypeGraph, representationType, name), convention);

        myBindings.put(name, result);

        return result;
    }

    public final ProgramParameterEntry addFormalParameter(String name, ResolveConceptualElement definingElement,
            ParameterMode mode, PTType type)
            throws DuplicateSymbolException {
        sanityCheckBindArguments(name, definingElement, type);

        ProgramParameterEntry entry =
                new ProgramParameterEntry(myTypeGraph, name, definingElement,
                        myRootModule, type, mode);

        myBindings.put(name, entry);

        return entry;
    }

    public final OperationEntry addOperation(String name, ResolveConceptualElement definingElement,
            List<ProgramParameterEntry> params, PTType returnType)
            throws DuplicateSymbolException {
        sanityCheckBindArguments(name, definingElement, returnType);

        OperationEntry entry =
                new OperationEntry(name, definingElement, myRootModule,
                        returnType, params);

        myBindings.put(name, entry);

        return entry;
    }

    public final OperationProfileEntry addOperationProfile(String name, ResolveConceptualElement definingElement,
            OperationEntry correspondingOperation)
            throws DuplicateSymbolException {
        sanityCheckBindArguments(name, definingElement, "");

        OperationProfileEntry entry =
                new OperationProfileEntry(name, definingElement, myRootModule,
                        correspondingOperation);

        myBindings.put(name, entry);

        return entry;
    }

    public final ProcedureEntry addProcedure(String name, ResolveConceptualElement definingElement,
            OperationEntry correspondingOperation)
            throws DuplicateSymbolException {
        sanityCheckBindArguments(name, definingElement, "");

        ProcedureEntry entry =
                new ProcedureEntry(name, definingElement, myRootModule,
                        correspondingOperation);

        myBindings.put(name, entry);

        return entry;
    }

    public final ProgramTypeEntry addProgramTypeDefinition(String name, TypeFamilyDec definingElement,
            MTType model, MathSymbolEntry exemplarEntry)
            throws DuplicateSymbolException {
        sanityCheckBindArguments(name, definingElement, model);

        PosSymbol exemplarSymbol = definingElement.getExemplar();
        if (exemplarSymbol == null) {
            throw new IllegalArgumentException("Null exemplar.");
        }

        SpecInitFinalItem init = definingElement.getInitialization();
        SpecInitFinalItem finalization = definingElement.getFinalization();

        AssertionClause initEnsures = init.getEnsures();
        AssertionClause finalizationEnsures = finalization.getEnsures();

        ProgramTypeEntry entry = new TypeFamilyEntry(myTypeGraph, name, definingElement,
                myRootModule, model, new PTFamily(model, name, exemplarSymbol.getName(),
                definingElement.getConstraint(), initEnsures, finalizationEnsures),
                exemplarEntry, definingElement.getConstraint());

        myBindings.put(name, entry);

        return entry;
    }

    public final ProgramVariableEntry addProgramVariable(String name,
            ResolveConceptualElement definingElement, PTType type)
            throws DuplicateSymbolException {
        sanityCheckBindArguments(name, definingElement, type);

        ProgramVariableEntry entry =
                new ProgramVariableEntry(name, definingElement, myRootModule,
                        type);

        myBindings.put(name, entry);

        return entry;
    }

    public final TypeRepresentationEntry addRepresentationTypeEntry(String name,
            TypeRepresentationDec definingElement, TypeFamilyEntry definition, PTType representationType,
            AssertionClause convention, AssertionClause correspondence)
            throws DuplicateSymbolException {
        sanityCheckBindArguments(name, definingElement, "");

        TypeRepresentationEntry result = new TypeRepresentationEntry(name, definingElement, myRootModule,
                definition, representationType, convention, correspondence);

        myBindings.put(name, result);

        return result;
    }

    public final TheoremEntry addTheorem(String name, MathAssertionDec definingElement)
            throws DuplicateSymbolException {
        sanityCheckBindArguments(name, definingElement, "");

        TheoremEntry entry =
                new TheoremEntry(myTypeGraph, name, definingElement,
                        myRootModule);

        myBindings.put(name, entry);

        return entry;
    }

    // ===========================================================
    // Package Private Methods
    // ===========================================================

    void addChild(ScopeBuilder b) {
        myChildren.add(b);
    }

    List<ScopeBuilder> children() {
        return new LinkedList<>(myChildren);
    }

    FinalizedScope seal(MathSymbolTable finalTable) {
        return new FinalizedScope(finalTable, myDefiningElement, myParent, myRootModule, myBindings);
    }

    void setParent(Scope parent) {
        myParent = parent;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    private void sanityCheckBindArguments(String name, ResolveConceptualElement definingElement, Object type)
            throws DuplicateSymbolException {
        SymbolTableEntry curLocalEntry = myBindings.get(name);
        if (curLocalEntry != null) {
            throw new DuplicateSymbolException("Found two matching entries!", curLocalEntry);
        }

        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("Symbol table entry name must "
                    + "be non-null and contain at least one character.");
        }

        if (type == null) {
            throw new IllegalArgumentException("Symbol table entry type must "
                    + "be non-null.");
        }
    }

}