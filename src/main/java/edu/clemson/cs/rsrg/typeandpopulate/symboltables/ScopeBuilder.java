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

    /** <p>This contains all the children scopes</p> */
    private final List<ScopeBuilder> myChildren = new LinkedList<>();

    /** <p>The current type graph object in use.</p> */
    private final TypeGraph myTypeGraph;

    // ==========================================================
    // Constructors
    // ==========================================================

    /**
     * <p>This constructs a scope where new entries can be added.</p>
     *
     * @param b The current scope repository builder.
     * @param g The current type graph.
     * @param definingElement The element that created this scope.
     * @param parent The parent scope.
     * @param enclosingModule The module identifier for the module
     *                        that this scope belongs to.
     */
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
     * @param q The symbol quantification.
     * @param definingElement The AST Node that introduced the symbol.
     * @param type The declared type of the symbol.
     * @param typeValue The type assigned to the symbol (can be null).
     * @param schematicTypes The schematic mathematical types.
     * @param genericsInDefiningContext The generic math types.
     *
     * @return A new {@link MathSymbolEntry}.
     *
     * @throws DuplicateSymbolException If such a symbol is already defined
     * directly in the scope represented by this <code>ScopeBuilder</code>.
     * Note that this exception is not thrown if the symbol is defined in
     * a parent scope or an imported module.
     * @throws IllegalArgumentException Arguments do not meet the entry creation
     * criteria. Most likely, we have passed <code>null</code> objects.
     */
    public final MathSymbolEntry addBinding(String name, SymbolTableEntry.Quantification q,
            ResolveConceptualElement definingElement, MTType type, MTType typeValue,
            Map<String, MTType> schematicTypes, Map<String, MTType> genericsInDefiningContext)
            throws DuplicateSymbolException, IllegalArgumentException {
        sanityCheckBindArguments(name, definingElement, type);

        MathSymbolEntry entry =
                new MathSymbolEntry(myTypeGraph, name, q, definingElement,
                        type, typeValue, schematicTypes,
                        genericsInDefiningContext, myRootModule);

        myBindings.put(name, entry);

        return entry;
    }

    /**
     * <p>Modifies the current working scope to add a new binding for a
     * symbol with an unqualified name, <code>name</code>, defined by the AST
     * node <code>definingElement</code> and of type <code>type</code>.</p>
     *
     * @param name The unqualified name of the symbol.
     * @param q The symbol quantification.
     * @param definingElement The AST Node that introduced the symbol.
     * @param type The declared type of the symbol.
     *
     * @return A new {@link MathSymbolEntry}.
     *
     * @throws DuplicateSymbolException If such a symbol is already defined
     * directly in the scope represented by this <code>ScopeBuilder</code>.
     * Note that this exception is not thrown if the symbol is defined in
     * a parent scope or an imported module.
     * @throws IllegalArgumentException Arguments do not meet the entry creation
     * criteria. Most likely, we have passed <code>null</code> objects.
     */
    public final MathSymbolEntry addBinding(String name, SymbolTableEntry.Quantification q,
            ResolveConceptualElement definingElement, MTType type)
            throws DuplicateSymbolException, IllegalArgumentException {
        return addBinding(name, q, definingElement, type, null, null, null);
    }

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
     * @return A new {@link MathSymbolEntry}.
     *
     * @throws DuplicateSymbolException If such a symbol is already defined
     * directly in the scope represented by this <code>ScopeBuilder</code>.
     * Note that this exception is not thrown if the symbol is defined in
     * a parent scope or an imported module.
     * @throws IllegalArgumentException Arguments do not meet the entry creation
     * criteria. Most likely, we have passed <code>null</code> objects.
     */
    public final MathSymbolEntry addBinding(String name, ResolveConceptualElement definingElement,
            MTType type, MTType typeValue)
            throws DuplicateSymbolException, IllegalArgumentException {
        return addBinding(name, SymbolTableEntry.Quantification.NONE, definingElement, type, typeValue, null, null);
    }

    /**
     * <p>Modifies the current working scope to add a new binding for a
     * symbol with an unqualified name, <code>name</code>, defined by the AST
     * node <code>definingElement</code> and of type <code>type</code>.</p>
     *
     * @param name The unqualified name of the symbol.
     * @param definingElement The AST Node that introduced the symbol.
     * @param type The declared type of the symbol.
     *
     * @return A new {@link MathSymbolEntry}.
     *
     * @throws DuplicateSymbolException If such a symbol is already defined
     * directly in the scope represented by this <code>ScopeBuilder</code>.
     * Note that this exception is not thrown if the symbol is defined in
     * a parent scope or an imported module.
     * @throws IllegalArgumentException Arguments do not meet the entry creation
     * criteria. Most likely, we have passed <code>null</code> objects.
     */
    public final MathSymbolEntry addBinding(String name, ResolveConceptualElement definingElement, MTType type)
            throws DuplicateSymbolException, IllegalArgumentException {
        return addBinding(name, SymbolTableEntry.Quantification.NONE, definingElement, type);
    }

    /**
     * <p>Modifies the current working scope to add a new facility entry
     * defined by the AST node <code>facility</code>.</p>
     *
     * @param facility The AST Node that introduced the symbol.
     * @param isSharingConceptInstantiation This flag indicates whether or not this {@code facility}
     *                                      is an instantiation of a {@code sharing concept}.
     *
     * @return A new {@link FacilityEntry}.
     *
     * @throws DuplicateSymbolException If such a symbol is already defined
     * directly in the scope represented by this <code>ScopeBuilder</code>.
     * Note that this exception is not thrown if the symbol is defined in
     * a parent scope or an imported module.
     * @throws IllegalArgumentException Arguments do not meet the entry creation
     * criteria. Most likely, we have passed <code>null</code> objects.
     */
    public final FacilityEntry addFacility(FacilityDec facility, boolean isSharingConceptInstantiation)
            throws DuplicateSymbolException, IllegalArgumentException {
        SymbolTableEntry curLocalEntry =
                myBindings.get(facility.getName().getName());
        if (curLocalEntry != null) {
            throw new DuplicateSymbolException("Found two matching entries!", curLocalEntry);
        }

        FacilityEntry entry = new FacilityEntry(facility, isSharingConceptInstantiation,
                myRootModule, getSourceRepository());

        myBindings.put(facility.getName().getName(), entry);

        return entry;
    }

    /**
     * <p>Modifies the current working scope to add a new facility type representation
     * for a type with an unqualified name, <code>name</code>, defined by the AST
     * node <code>definingElement</code>, instantiated with type <code>representationType</code>
     * and has a <code>convention</code> clause.</p>
     *
     * @param name The unqualified name of the symbol.
     * @param definingElement The AST Node that introduced the symbol.
     * @param representationType The instantiated program type used to implement this type.
     * @param convention The new type's convention clause.
     *
     * @return A new {@link FacilityTypeRepresentationEntry}.
     *
     * @throws DuplicateSymbolException If such a symbol is already defined
     * directly in the scope represented by this <code>ScopeBuilder</code>.
     * Note that this exception is not thrown if the symbol is defined in
     * a parent scope or an imported module.
     * @throws IllegalArgumentException Arguments do not meet the entry creation
     * criteria. Most likely, we have passed <code>null</code> objects.
     */
    public final FacilityTypeRepresentationEntry addFacilityRepresentationEntry(String name,
            FacilityTypeRepresentationDec definingElement, PTInstantiated representationType,
            AssertionClause convention)
            throws DuplicateSymbolException, IllegalArgumentException {
        sanityCheckBindArguments(name, definingElement, "");

        FacilityTypeRepresentationEntry result = new FacilityTypeRepresentationEntry(name, definingElement,
                myRootModule, new PTFacilityRepresentation(myTypeGraph, representationType, name), convention);

        myBindings.put(name, result);

        return result;
    }

    /**
     * <p>Modifies the current working scope to add a new parameter variable
     * with an unqualified name, <code>name</code>, defined by the AST
     * node <code>definingElement</code>, with parameter <code>mode</code>
     * and has the specified program <code>type</code>.</p>
     *
     * @param name The unqualified name of the symbol.
     * @param definingElement The AST Node that introduced the symbol.
     * @param mode The parameter mode for this variable.
     * @param type The program type for this variable.
     *
     * @return A new {@link ProgramParameterEntry}.
     *
     * @throws DuplicateSymbolException If such a symbol is already defined
     * directly in the scope represented by this <code>ScopeBuilder</code>.
     * Note that this exception is not thrown if the symbol is defined in
     * a parent scope or an imported module.
     * @throws IllegalArgumentException Arguments do not meet the entry creation
     * criteria. Most likely, we have passed <code>null</code> objects.
     */
    public final ProgramParameterEntry addFormalParameter(String name, ResolveConceptualElement definingElement,
            ParameterMode mode, PTType type)
            throws DuplicateSymbolException, IllegalArgumentException {
        sanityCheckBindArguments(name, definingElement, type);

        ProgramParameterEntry entry =
                new ProgramParameterEntry(myTypeGraph, name, definingElement,
                        myRootModule, type, mode);

        myBindings.put(name, entry);

        return entry;
    }

    /**
     * <p>Modifies the current working scope to add a new operation
     * with an unqualified name, <code>name</code>, defined by the AST
     * node <code>definingElement</code>, with parameters <code>params</code>
     * and could have a <code>returnType</code>.</p>
     *
     * @param name The unqualified name of the symbol.
     * @param definingElement The AST Node that introduced the symbol.
     * @param params List of parameters variables for this operation.
     * @param returnType A return program type (could be null).
     *
     * @return A new {@link OperationEntry}.
     *
     * @throws DuplicateSymbolException If such a symbol is already defined
     * directly in the scope represented by this <code>ScopeBuilder</code>.
     * Note that this exception is not thrown if the symbol is defined in
     * a parent scope or an imported module.
     * @throws IllegalArgumentException Arguments do not meet the entry creation
     * criteria. Most likely, we have passed <code>null</code> objects.
     */
    public final OperationEntry addOperation(String name, ResolveConceptualElement definingElement,
            List<ProgramParameterEntry> params, PTType returnType)
            throws DuplicateSymbolException, IllegalArgumentException {
        sanityCheckBindArguments(name, definingElement, returnType);

        OperationEntry entry =
                new OperationEntry(name, definingElement, myRootModule,
                        returnType, params);

        myBindings.put(name, entry);

        return entry;
    }

    /**
     * <p>Modifies the current working scope to add a new operation profile
     * with an unqualified name, <code>name</code>, defined by the AST
     * node <code>definingElement</code> that is associated with
     * <code>correspondingOperation</code>.</p>
     *
     * @param name The unqualified name of the symbol.
     * @param definingElement The AST Node that introduced the symbol.
     * @param correspondingOperation The corresponding operation for this profile entry.
     *
     * @return A new {@link OperationProfileEntry}.
     *
     * @throws DuplicateSymbolException If such a symbol is already defined
     * directly in the scope represented by this <code>ScopeBuilder</code>.
     * Note that this exception is not thrown if the symbol is defined in
     * a parent scope or an imported module.
     * @throws IllegalArgumentException Arguments do not meet the entry creation
     * criteria. Most likely, we have passed <code>null</code> objects.
     */
    public final OperationProfileEntry addOperationProfile(String name, ResolveConceptualElement definingElement,
            OperationEntry correspondingOperation)
            throws DuplicateSymbolException, IllegalArgumentException {
        sanityCheckBindArguments(name, definingElement, "");

        OperationProfileEntry entry =
                new OperationProfileEntry(name, definingElement, myRootModule,
                        correspondingOperation);

        myBindings.put(name, entry);

        return entry;
    }

    /**
     * <p>Modifies the current working scope to add a new procedure entry
     * with an unqualified name, <code>name</code>, defined by the AST
     * node <code>definingElement</code> that is associated with
     * <code>correspondingOperation</code>.</p>
     *
     * @param name The unqualified name of the symbol.
     * @param definingElement The AST Node that introduced the symbol.
     * @param correspondingOperation The corresponding operation for this procedure entry.
     *
     * @return A new {@link ProcedureEntry}.
     *
     * @throws DuplicateSymbolException If such a symbol is already defined
     * directly in the scope represented by this <code>ScopeBuilder</code>.
     * Note that this exception is not thrown if the symbol is defined in
     * a parent scope or an imported module.
     * @throws IllegalArgumentException Arguments do not meet the entry creation
     * criteria. Most likely, we have passed <code>null</code> objects.
     */
    public final ProcedureEntry addProcedure(String name, ResolveConceptualElement definingElement,
            OperationEntry correspondingOperation)
            throws DuplicateSymbolException, IllegalArgumentException {
        sanityCheckBindArguments(name, definingElement, "");

        ProcedureEntry entry =
                new ProcedureEntry(name, definingElement, myRootModule,
                        correspondingOperation);

        myBindings.put(name, entry);

        return entry;
    }

    /**
     * <p>Modifies the current working scope to add a new type family entry
     * with an unqualified name, <code>name</code>, defined by the AST
     * node <code>definingElement</code> that is represented with
     * <code>model</code> and has the exemplar <code>exemplarEntry</code>.</p>
     *
     * @param name The unqualified name of the symbol.
     * @param definingElement The AST Node that introduced the symbol.
     * @param model The mathematical model for this entry.
     * @param exemplarEntry The exemplar variable.
     *
     * @return A new {@link ProgramTypeEntry}.
     *
     * @throws DuplicateSymbolException If such a symbol is already defined
     * directly in the scope represented by this <code>ScopeBuilder</code>.
     * Note that this exception is not thrown if the symbol is defined in
     * a parent scope or an imported module.
     * @throws IllegalArgumentException Arguments do not meet the entry creation
     * criteria. Most likely, we have passed <code>null</code> objects.
     */
    public final ProgramTypeEntry addProgramTypeDefinition(String name, TypeFamilyDec definingElement,
            MTType model, MathSymbolEntry exemplarEntry)
            throws DuplicateSymbolException, IllegalArgumentException {
        sanityCheckBindArguments(name, definingElement, model);

        PosSymbol exemplarSymbol = definingElement.getExemplar();
        if (exemplarSymbol == null) {
            throw new IllegalArgumentException("Null exemplar.");
        }

        ProgramTypeEntry entry = new TypeFamilyEntry(myTypeGraph, name, definingElement,
                myRootModule, model, new PTFamily(model, name, exemplarSymbol.getName()),
                exemplarEntry, definingElement.getConstraint(), definingElement.getDefinitionVarList());

        myBindings.put(name, entry);

        return entry;
    }

    /**
     * <p>Modifies the current working scope to add a new program variable entry
     * with an unqualified name, <code>name</code>, defined by the AST
     * node <code>definingElement</code> that has <code>type</code> as programming
     * type.</p>
     *
     * @param name The unqualified name of the symbol.
     * @param definingElement The AST Node that introduced the symbol.
     * @param type The program type for this entry.
     *
     * @return A new {@link ProgramVariableEntry}.
     *
     * @throws DuplicateSymbolException If such a symbol is already defined
     * directly in the scope represented by this <code>ScopeBuilder</code>.
     * Note that this exception is not thrown if the symbol is defined in
     * a parent scope or an imported module.
     * @throws IllegalArgumentException Arguments do not meet the entry creation
     * criteria. Most likely, we have passed <code>null</code> objects.
     */
    public final ProgramVariableEntry addProgramVariable(String name,
            ResolveConceptualElement definingElement, PTType type)
            throws DuplicateSymbolException, IllegalArgumentException {
        sanityCheckBindArguments(name, definingElement, type);

        ProgramVariableEntry entry =
                new ProgramVariableEntry(name, definingElement, myRootModule,
                        type);

        myBindings.put(name, entry);

        return entry;
    }

    /**
     * <p>Modifies the current working scope to add a new type representation
     * for a type with an unqualified name, <code>name</code>, defined by the AST
     * node <code>definingElement</code>, that is associated with <code>definition</code>
     * and instantiated with type <code>representationType</code> with
     * <code>convention</code> and <code>correspondence</code> clauses.</p>
     *
     * @param name The unqualified name of the symbol.
     * @param definingElement The AST Node that introduced the symbol.
     * @param definition The associated type family entry.
     * @param representationType The instantiated program type used to implement this type.
     * @param convention The new type's convention clause.
     * @param correspondence The new type's correspondence clause.
     *
     * @return A new {@link TypeRepresentationEntry}.
     *
     * @throws DuplicateSymbolException If such a symbol is already defined
     * directly in the scope represented by this <code>ScopeBuilder</code>.
     * Note that this exception is not thrown if the symbol is defined in
     * a parent scope or an imported module.
     * @throws IllegalArgumentException Arguments do not meet the entry creation
     * criteria. Most likely, we have passed <code>null</code> objects.
     */
    public final TypeRepresentationEntry addRepresentationTypeEntry(String name,
            TypeRepresentationDec definingElement, TypeFamilyEntry definition, PTType representationType,
            AssertionClause convention, AssertionClause correspondence)
            throws DuplicateSymbolException, IllegalArgumentException {
        sanityCheckBindArguments(name, definingElement, "");

        TypeRepresentationEntry result = new TypeRepresentationEntry(name, definingElement, myRootModule,
                definition, representationType, convention, correspondence);

        myBindings.put(name, result);

        return result;
    }

    /**
     * <p>Modifies the current working scope to add a new theorem
     * with an unqualified name, <code>name</code> and defined by the AST
     * node <code>definingElement</code>.</p>
     *
     * @param name The unqualified name of the symbol.
     * @param definingElement The AST Node that introduced the symbol.
     *
     * @return A new {@link TheoremEntry}.
     *
     * @throws DuplicateSymbolException If such a symbol is already defined
     * directly in the scope represented by this <code>ScopeBuilder</code>.
     * Note that this exception is not thrown if the symbol is defined in
     * a parent scope or an imported module.
     * @throws IllegalArgumentException Arguments do not meet the entry creation
     * criteria. Most likely, we have passed <code>null</code> objects.
     */
    public final TheoremEntry addTheorem(String name, MathAssertionDec definingElement)
            throws DuplicateSymbolException, IllegalArgumentException {
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

    /**
     * <p>This method adds a child scope builder.</p>
     *
     * @param b Child {@link ScopeBuilder}.
     */
    final void addChild(ScopeBuilder b) {
        myChildren.add(b);
    }

    /**
     * <p>This method returns the list of children scope builders.</p>
     *
     * @return A list of {@link ScopeBuilder}.
     */
    final List<ScopeBuilder> children() {
        return new LinkedList<>(myChildren);
    }

    /**
     * <p>This method seals this scope from further modifications.</p>
     *
     * @param finalTable The finalized symbol table.
     *
     * @return A {@link FinalizedScope} object.
     */
    FinalizedScope seal(MathSymbolTable finalTable) {
        return new FinalizedScope(finalTable, myDefiningElement, myParent, myRootModule, myBindings);
    }

    /**
     * <p>This method sets the parent scope.</p>
     *
     * @param parent The parent {@link Scope}.
     */
    final void setParent(Scope parent) {
        myParent = parent;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This method performs basic sanity checks before we attempt
     * to add an entry into the symbol table.</p>
     *
     * @param name The unqualified name of the symbol.
     * @param definingElement The AST Node that introduced the symbol.
     * @param type The entry type.
     *
     * @throws DuplicateSymbolException If such a symbol is already defined
     * directly in the scope represented by this <code>ScopeBuilder</code>.
     * Note that this exception is not thrown if the symbol is defined in
     * a parent scope or an imported module.
     * @throws IllegalArgumentException Arguments do not meet the entry creation
     * criteria. Most likely, we have passed <code>null</code> objects.
     */
    private void sanityCheckBindArguments(String name, ResolveConceptualElement definingElement, Object type)
            throws DuplicateSymbolException, IllegalArgumentException {
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

        if (definingElement == null) {
            throw new IllegalArgumentException("Defining entry must be non-null!");
        }
    }

}