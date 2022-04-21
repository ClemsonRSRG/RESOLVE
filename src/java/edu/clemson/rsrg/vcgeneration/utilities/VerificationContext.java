/*
 * VerificationContext.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.vcgeneration.utilities;

import edu.clemson.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.rsrg.absyn.declarations.Dec;
import edu.clemson.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.rsrg.absyn.declarations.moduledecl.*;
import edu.clemson.rsrg.absyn.declarations.paramdecl.ConstantParamDec;
import edu.clemson.rsrg.absyn.declarations.paramdecl.ModuleParameterDec;
import edu.clemson.rsrg.absyn.declarations.sharedstatedecl.SharedStateDec;
import edu.clemson.rsrg.absyn.declarations.sharedstatedecl.SharedStateRealizationDec;
import edu.clemson.rsrg.absyn.declarations.typedecl.AbstractTypeRepresentationDec;
import edu.clemson.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.rsrg.absyn.declarations.variabledecl.MathVarDec;
import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.MathExp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.rsrg.absyn.rawtypes.NameTy;
import edu.clemson.rsrg.init.CompileEnvironment;
import edu.clemson.rsrg.parsing.data.BasicCapabilities;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.parsing.data.LocationDetailModel;
import edu.clemson.rsrg.parsing.data.PosSymbol;
import edu.clemson.rsrg.typeandpopulate.entry.ProgramTypeEntry;
import edu.clemson.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import edu.clemson.rsrg.vcgeneration.utilities.formaltoactual.InstantiatedFacilityDecl;
import java.util.*;
import static edu.clemson.rsrg.vcgeneration.VCGenerator.FLAG_ADD_CONSTRAINT;

/**
 * <p>
 * This class contains all the module-level items relevant to the file we are generating VCs for.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class VerificationContext implements BasicCapabilities, Cloneable {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The symbol table we are currently building.
     * </p>
     */
    private final MathSymbolTableBuilder myBuilder;

    /**
     * <p>
     * The current job's compilation environment that stores all necessary objects and flags.
     * </p>
     */
    private final CompileEnvironment myCompileEnvironment;

    /**
     * <p>
     * The module scope for the file we are generating {@code VCs} for.
     * </p>
     */
    private final ModuleScope myCurrentModuleScope;

    /**
     * <p>
     * The name of the current module we are generating VCs for.
     * </p>
     */
    private final PosSymbol myName;

    // -----------------------------------------------------------
    // Module Level Requires and Constraint Clauses
    // -----------------------------------------------------------

    /**
     * <p>
     * A list that stores all the module level {@code constraint} clauses for the various different declarations.
     * </p>
     */
    private final Map<Dec, List<AssertionClause>> myModuleLevelConstraints;

    /**
     * <p>
     * A map that stores all the details associated with a particular module level {@link AssertionClause}.
     * </p>
     */
    private final Map<AssertionClause, LocationDetailModel> myModuleLevelLocationDetails;

    /**
     * <p>
     * A list that stores all the module level {@code requires} clauses.
     * </p>
     */
    private final List<AssertionClause> myModuleLevelRequires;

    // -----------------------------------------------------------
    // Processed Facility Declarations
    // -----------------------------------------------------------

    /**
     * <p>
     * The list of processed {@link InstantiatedFacilityDecl}.
     * </p>
     */
    private final List<InstantiatedFacilityDecl> myProcessedInstFacilityDecls;

    // -----------------------------------------------------------
    // Shared State Declarations and Representations
    // -----------------------------------------------------------

    /**
     * <p>
     * This contains all the shared state declared by the {@code Concept}.
     * </p>
     */
    private final List<SharedStateDec> myConceptSharedStates;

    /**
     * <p>
     * If our current module scope allows us to introduce new shared state realizations, this will contain all the
     * {@link SharedStateRealizationDec}. Otherwise, this list will be empty.
     * </p>
     */
    private final List<SharedStateRealizationDec> myLocalSharedStateRealizationDecs;

    // -----------------------------------------------------------
    // Type Declarations and Representations
    // -----------------------------------------------------------

    /**
     * <p>
     * This contains all the types declared by the {@code Concept} associated with the current module. Note that if we
     * are in a {@code Facility}, this list will be empty.
     * </p>
     */
    private final List<TypeFamilyDec> myConceptDeclaredTypes;

    /**
     * <p>
     * If our current module scope allows us to introduce new type implementations, this will contain all the
     * {@link AbstractTypeRepresentationDec}. Otherwise, this list will be empty.
     * </p>
     */
    private final List<AbstractTypeRepresentationDec> myLocalTypeRepresentationDecs;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a verification context that stores all the relevant information related to the module we are
     * generating {@code VCs} for.
     * </p>
     *
     * @param name
     *            Name of the module we are generating VCs for.
     * @param moduleScope
     *            The module scope associated with {@code name}.
     * @param builder
     *            A scope builder for a symbol table.
     * @param compileEnvironment
     *            The current job's compilation environment that stores all necessary objects and flags.
     */
    public VerificationContext(PosSymbol name, ModuleScope moduleScope, MathSymbolTableBuilder builder,
            CompileEnvironment compileEnvironment) {
        myBuilder = builder;
        myCompileEnvironment = compileEnvironment;
        myConceptDeclaredTypes = new LinkedList<>();
        myConceptSharedStates = new LinkedList<>();
        myCurrentModuleScope = moduleScope;
        myLocalSharedStateRealizationDecs = new LinkedList<>();
        myLocalTypeRepresentationDecs = new LinkedList<>();
        myModuleLevelConstraints = new LinkedHashMap<>();
        myModuleLevelLocationDetails = new LinkedHashMap<>();
        myModuleLevelRequires = new LinkedList<>();
        myName = name;
        myProcessedInstFacilityDecls = new LinkedList<>();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method creates a special indented text version of the instantiated object.
     * </p>
     *
     * @param indentSize
     *            The base indentation to the first line of the text.
     * @param innerIndentInc
     *            The additional indentation increment for the subsequent lines.
     *
     * @return A formatted text string of the class.
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        return "";
    }

    /**
     * <p>
     * This method constructs an expression containing shared state conventions.
     * </p>
     *
     * @param loc
     *            The location in the AST that we are currently visiting.
     *
     * @return A (possible conjunct) of shared state realization's conventions or {@code true}.
     */
    public final Exp createSharedStateRealizConventionExp(Location loc) {
        // Process any local shared state realizations
        Exp retExp = null;
        for (SharedStateRealizationDec sharedStateRealizationDec : myLocalSharedStateRealizationDecs) {
            AssertionClause stateConventionClause = sharedStateRealizationDec.getConvention();
            retExp = Utilities.formConjunct(loc, retExp, stateConventionClause,
                    new LocationDetailModel(stateConventionClause.getAssertionExp().getLocation().clone(),
                            stateConventionClause.getAssertionExp().getLocation().clone(),
                            "Shared Variable Convention"));
        }

        if (retExp == null) {
            retExp = VarExp.getTrueVarExp(loc, myBuilder.getTypeGraph());
        }

        return retExp;
    }

    /**
     * <p>
     * This method constructs an expression containing shared state correspondence.
     * </p>
     *
     * @param loc
     *            The location in the AST that we are currently visiting.
     *
     * @return A (possible conjunct) of shared state realization's correspondence or {@code true}.
     */
    public final Exp createSharedStateRealizCorrespondenceExp(Location loc) {
        // Process any local shared state realizations
        Exp retExp = null;
        for (SharedStateRealizationDec sharedStateRealizationDec : myLocalSharedStateRealizationDecs) {
            AssertionClause stateCorrespondenceClause = sharedStateRealizationDec.getCorrespondence();
            retExp = Utilities.formConjunct(loc, retExp, stateCorrespondenceClause,
                    new LocationDetailModel(stateCorrespondenceClause.getAssertionExp().getLocation().clone(),
                            stateCorrespondenceClause.getAssertionExp().getLocation().clone(),
                            "Shared Variable Correspondence"));
        }

        if (retExp == null) {
            retExp = VarExp.getTrueVarExp(loc, myBuilder.getTypeGraph());
        }

        return retExp;
    }

    /**
     * <p>
     * This method uses all the {@code requires} and {@code constraint} clauses from the various different sources (see
     * below for complete list) and builds the appropriate {@code assume} clause that goes at the beginning an
     * {@link AssertiveCodeBlock}.
     * </p>
     *
     * <p>
     * List of different places where clauses can originate from:
     * </p>
     * <ul>
     * <li>{@code Concept}'s {@code requires} clause.</li>
     * <li>{@code Concept}'s module {@code constraint} clause.</li>
     * <li>{@code Shared Variables}' {@code constraint} clause.</li>
     * <li>{@code Concept Realization}'s {@code requires} clause.</li>
     * <li>{@code Shared Variables}' {@code convention} clause.</li>
     * <li>{@code Shared Variables}' {@code correspondence} clause.</li>
     * <li>{@code constraint} clauses for all the parameters with the appropriate substitutions made.</li>
     * <li>Any {@code which_entails} expressions that originated from any of the clauses above.</li>
     * </ul>
     *
     * @param loc
     *            The location in the AST that we are currently visiting.
     * @param addSharedConventionFlag
     *            A flag that indicates whether or not we need to add the {@code Shared Variable}'s {@code convention}.
     * @param addSharedCorrespondenceFlag
     *            A flag that indicates whether or not we need to add the {@code Shared Variable}'s
     *            {@code correspondence}.
     *
     * @return The top-level assumed expression.
     */
    public final Exp createTopLevelAssumeExpFromContext(Location loc, boolean addSharedConventionFlag,
            boolean addSharedCorrespondenceFlag) {
        Exp retExp = null;

        // Add all the module level requires clause.
        for (AssertionClause clause : myModuleLevelRequires) {
            retExp = Utilities.formConjunct(loc, retExp, clause, myModuleLevelLocationDetails.get(clause));
        }

        // Add all the module level constraint clauses.
        for (Dec dec : myModuleLevelConstraints.keySet()) {
            for (AssertionClause clause : myModuleLevelConstraints.get(dec)) {
                retExp = Utilities.formConjunct(loc, retExp, clause, myModuleLevelLocationDetails.get(clause));
            }
        }

        // Add all share variable's constraints.
        // YS: We are not adding these automatically. Most of the time, these
        // constraints wouldn't really help us prove any of the VCs. If you
        // are ever interested in adding these to the givens list, use the
        // "addConstraints" flag. Note that these constraints still need to be
        // processed by the parsimonious step, so there is no guarantee that they
        // will show up in all of the VCs.
        if (myCompileEnvironment.flags.isFlagSet(FLAG_ADD_CONSTRAINT)) {
            // Add any facility instantiated shared state constraints
            for (InstantiatedFacilityDecl facilityDecl : myProcessedInstFacilityDecls) {
                for (SharedStateDec stateDec : facilityDecl.getConceptSharedStates()) {
                    AssertionClause stateConstraintClause = stateDec.getConstraint();

                    // All shared variables should be add facilityDecl's name.
                    Map<Exp, Exp> substitutions = new LinkedHashMap<>();
                    for (MathVarDec mathVarDec : stateDec.getAbstractStateVars()) {
                        // Convert mathVarDec to VarExp. Also create a new qualified version of it.
                        VarExp mathVarDecAsVarExp = Utilities.createVarExp(mathVarDec.getLocation().clone(), null,
                                mathVarDec.getName().clone(), mathVarDec.getMathType(), null);
                        VarExp qualifiedVarExp = (VarExp) mathVarDecAsVarExp.clone();
                        qualifiedVarExp.setQualifier(facilityDecl.getInstantiatedFacilityName().clone());

                        // Put them into our substitutions map.
                        substitutions.put(mathVarDecAsVarExp, qualifiedVarExp);
                    }

                    // Generate the proper facility qualified constraint
                    // and which_entails clauses (if any).
                    Exp modifiedConstraint = stateConstraintClause.getAssertionExp().substitute(substitutions);
                    Exp modifiedWhichEntails = stateConstraintClause.getWhichEntailsExp();
                    if (modifiedWhichEntails != null) {
                        modifiedWhichEntails = modifiedWhichEntails.substitute(substitutions);
                    }

                    // Create the modified state constraint clause and add it to the retExp.
                    AssertionClause modifiedStateConstraintClause = new AssertionClause(
                            stateConstraintClause.getLocation().clone(), stateConstraintClause.getClauseType(),
                            modifiedConstraint, modifiedWhichEntails);
                    retExp = Utilities.formConjunct(loc, retExp, modifiedStateConstraintClause,
                            myModuleLevelLocationDetails.get(stateConstraintClause));
                }
            }

            // Add concept shared state constraints
            for (SharedStateDec stateDec : myConceptSharedStates) {
                AssertionClause stateConstraintClause = stateDec.getConstraint();
                retExp = Utilities.formConjunct(loc, retExp, stateConstraintClause,
                        myModuleLevelLocationDetails.get(stateConstraintClause));
            }
        }

        // Add the share variable realization's convention (if requested).
        if (addSharedConventionFlag) {
            Exp conventionExp = createSharedStateRealizConventionExp(loc);
            if (!VarExp.isLiteralTrue(conventionExp)) {
                if (retExp == null) {
                    retExp = conventionExp;
                } else {
                    retExp = MathExp.formConjunct(loc, retExp, conventionExp);
                }
            }
        }

        // Add the shared variable realization's correspondence (if requested).
        if (addSharedCorrespondenceFlag) {
            Exp correspondenceExp = createSharedStateRealizCorrespondenceExp(loc);
            if (!VarExp.isLiteralTrue(correspondenceExp)) {
                if (retExp == null) {
                    retExp = correspondenceExp;
                } else {
                    retExp = MathExp.formConjunct(loc, retExp, correspondenceExp);
                }
            }
        }

        return retExp;
    }

    /**
     * <p>
     * This method returns a list containing the various {@link TypeFamilyDec TypeFamilyDecs} in the current context.
     * </p>
     *
     * @return A list containing {@link TypeFamilyDec TypeFamilyDecs}.
     */
    public final List<TypeFamilyDec> getConceptDeclaredTypes() {
        return myConceptDeclaredTypes;
    }

    /**
     * <p>
     * This method returns a list containing the various {@link SharedStateDec SharedStateDecs} in the current context.
     * </p>
     *
     * @return A list containing {@link SharedStateDec SharedStateDecs}.
     */
    public final List<SharedStateDec> getConceptSharedVars() {
        return myConceptSharedStates;
    }

    /**
     * <p>
     * This method returns a list containing the various {@link SharedStateRealizationDec SharedStateRealizationDecs} in
     * the current context.
     * </p>
     *
     * @return A list containing {@link SharedStateRealizationDec SharedStateRealizationDecs}.
     */
    public final List<SharedStateRealizationDec> getLocalSharedStateRealizationDecs() {
        return myLocalSharedStateRealizationDecs;
    }

    /**
     * <p>
     * This method returns a list containing the various {@link AbstractTypeRepresentationDec
     * AbstractTypeRepresentationDecs} in the current context.
     * </p>
     *
     * @return A list containing {@link AbstractTypeRepresentationDec AbstractTypeRepresentationDecs}.
     */
    public final List<AbstractTypeRepresentationDec> getLocalTypeRepresentationDecs() {
        return myLocalTypeRepresentationDecs;
    }

    /**
     * <p>
     * This method returns the name of the module that created this context.
     * </p>
     *
     * @return The name in {@link PosSymbol} format.
     */
    public final PosSymbol getModuleName() {
        return myName;
    }

    /**
     * <p>
     * This method returns the instantiated facility declaration corresponding to a {@link FacilityDec}.
     * </p>
     *
     * @param dec
     *            A facility declaration.
     *
     * @return The {@link InstantiatedFacilityDecl} corresponding to {@code dec}.
     */
    public final InstantiatedFacilityDecl getProcessedInstFacilityDecl(FacilityDec dec) {
        InstantiatedFacilityDecl decl = null;
        Iterator<InstantiatedFacilityDecl> it = myProcessedInstFacilityDecls.iterator();
        while (it.hasNext() && decl == null) {
            InstantiatedFacilityDecl nextDecl = it.next();
            if (nextDecl.getInstantiatedFacilityName().getName().equals(dec.getName().getName())) {
                decl = nextDecl;
            }
        }

        return decl;
    }

    /**
     * <p>
     * This method returns a list of all instantiated {@code Facilities}.
     * </p>
     *
     * @return A list of {@link InstantiatedFacilityDecl} containing all the information.
     */
    public final List<InstantiatedFacilityDecl> getProcessedInstFacilityDecls() {
        return myProcessedInstFacilityDecls;
    }

    /**
     * <p>
     * This method stores a {@code concept}'s module level {@code requires} and {@code constraint} clauses for future
     * use.
     * </p>
     *
     * @param loc
     *            The location of where we found the {@code concept}.
     * @param id
     *            A {@link ModuleIdentifier} referring to a {@code concept}.
     * @param isFacilityImport
     *            A flag that indicates whether or not we are storing information that originated from a
     *            {@link FacilityDec}.
     */
    public final void storeConceptAssertionClauses(Location loc, ModuleIdentifier id, boolean isFacilityImport) {
        try {
            ConceptModuleDec conceptModuleDec = (ConceptModuleDec) myBuilder.getModuleScope(id).getDefiningElement();

            // We only need to store these if the concept didn't originate from
            // a facility declaration.
            if (!isFacilityImport) {
                // Store the concept's requires clause
                storeRequiresClause(conceptModuleDec.getName().getName(), conceptModuleDec.getRequires());

                // Store the concept's type constraints from the module parameters
                // YS: We are not adding these automatically. Most of the time, these
                // constraints wouldn't really help us prove any of the VCs. If you
                // are ever interested in adding these to the givens list, use the
                // "addConstraints" flag. Note that these constraints still need to be
                // processed by the parsimonious step, so there is no guarantee that they
                // will show up in all of the VCs.
                if (myCompileEnvironment.flags.isFlagSet(FLAG_ADD_CONSTRAINT)) {
                    storeModuleParameterTypeConstraints(conceptModuleDec.getLocation(),
                            conceptModuleDec.getParameterDecs());
                }
            }

            // Store the concept's module constraints and
            // its associated location detail for future use.
            if (!conceptModuleDec.getConstraints().isEmpty()) {
                myModuleLevelConstraints.put(conceptModuleDec, conceptModuleDec.getConstraints());

                for (AssertionClause constraint : conceptModuleDec.getConstraints()) {
                    Location constraintLoc = constraint.getAssertionExp().getLocation();
                    myModuleLevelLocationDetails.put(constraint, new LocationDetailModel(constraintLoc.clone(),
                            constraintLoc.clone(), "Constraint Clause of " + conceptModuleDec.getName()));
                }
            }
        } catch (NoSuchSymbolException e) {
            Utilities.noSuchModule(loc);
        }
    }

    /**
     * <p>
     * This method stores a {@code concept realization}'s module level {@code requires} and {@code constraint} clauses
     * for future use.
     * </p>
     *
     * @param loc
     *            The location of where we found the {@code concept realization}.
     * @param id
     *            A {@link ModuleIdentifier} referring to a {@code concept realization}.
     * @param isFacilityImport
     *            A flag that indicates whether or not we are storing information that originated from a
     *            {@link FacilityDec}.
     */
    public final void storeConceptRealizAssertionClauses(Location loc, ModuleIdentifier id, boolean isFacilityImport) {
        try {
            ConceptRealizModuleDec realizModuleDec = (ConceptRealizModuleDec) myBuilder.getModuleScope(id)
                    .getDefiningElement();

            // We only need to store these if they are part of a FacilityDec
            if (!isFacilityImport) {
                // Store the concept realization's requires clause
                storeRequiresClause(realizModuleDec.getName().getName(), realizModuleDec.getRequires());

                // Store the concept realization's type constraints from the module parameters
                // YS: We are not adding these automatically. Most of the time, these
                // constraints wouldn't really help us prove any of the VCs. If you
                // are ever interested in adding these to the givens list, use the
                // "addConstraints" flag. Note that these constraints still need to be
                // processed by the parsimonious step, so there is no guarantee that they
                // will show up in all of the VCs.
                if (myCompileEnvironment.flags.isFlagSet(FLAG_ADD_CONSTRAINT)) {
                    storeModuleParameterTypeConstraints(realizModuleDec.getLocation(),
                            realizModuleDec.getParameterDecs());
                }
            }
        } catch (NoSuchSymbolException e) {
            Utilities.noSuchModule(loc);
        }
    }

    /**
     * <p>
     * This method stores a {@code concept's} {@code Shared Variables} declarations for future use.
     * </p>
     *
     * @param dec
     *            A {@link SharedStateDec} declared in a {@code Concept}.
     */
    public final void storeConceptSharedStateDec(SharedStateDec dec) {
        myConceptSharedStates.add((SharedStateDec) dec.clone());
    }

    /**
     * <p>
     * This method stores the imported {@code concept's} {@code Shared Variables} declarations for future use.
     * </p>
     *
     * @param loc
     *            The location of the imported {@code module}.
     * @param id
     *            A {@link ModuleIdentifier} referring to an importing {@code concept}.
     */
    public final void storeConceptSharedStateDecs(Location loc, ModuleIdentifier id) {
        try {
            ConceptModuleDec conceptModuleDec = (ConceptModuleDec) myBuilder.getModuleScope(id).getDefiningElement();
            List<Dec> decs = conceptModuleDec.getDecList();

            for (Dec dec : decs) {
                if (dec instanceof SharedStateDec) {
                    myConceptSharedStates.add((SharedStateDec) dec.clone());
                }
            }
        } catch (NoSuchSymbolException e) {
            Utilities.noSuchModule(loc);
        }
    }

    /**
     * <p>
     * This method stores a {@code concept's} {@code Type Family} declarations for future use.
     * </p>
     *
     * @param dec
     *            A type family declared in a {@code Concept}.
     */
    public final void storeConceptTypeFamilyDec(TypeFamilyDec dec) {
        myConceptDeclaredTypes.add((TypeFamilyDec) dec.clone());
    }

    /**
     * <p>
     * This method stores the imported {@code concept's} {@code Type Family} declarations for future use.
     * </p>
     *
     * @param loc
     *            The location of the imported {@code module}.
     * @param id
     *            A {@link ModuleIdentifier} referring to an importing {@code concept}.
     */
    public final void storeConceptTypeFamilyDecs(Location loc, ModuleIdentifier id) {
        try {
            ConceptModuleDec conceptModuleDec = (ConceptModuleDec) myBuilder.getModuleScope(id).getDefiningElement();
            List<Dec> decs = conceptModuleDec.getDecList();

            for (Dec dec : decs) {
                if (dec instanceof TypeFamilyDec) {
                    myConceptDeclaredTypes.add((TypeFamilyDec) dec.clone());
                }
            }
        } catch (NoSuchSymbolException e) {
            Utilities.noSuchModule(loc);
        }
    }

    /**
     * <p>
     * This method stores a {@code enhancement}'s module level {@code requires} and {@code constraint} clauses for
     * future use.
     * </p>
     *
     * @param loc
     *            The location of where we found the {@code enhancement}.
     * @param id
     *            A {@link ModuleIdentifier} referring to a {@code enhancement}.
     * @param isFacilityImport
     *            A flag that indicates whether or not we are storing information that originated from a
     *            {@link FacilityDec}.
     */
    public final void storeEnhancementAssertionClauses(Location loc, ModuleIdentifier id, boolean isFacilityImport) {
        try {
            EnhancementModuleDec enhancementModuleDec = (EnhancementModuleDec) myBuilder.getModuleScope(id)
                    .getDefiningElement();

            // We only need to store these if they are part of a FacilityDec
            if (!isFacilityImport) {
                // Store the enhancement's requires clause
                storeRequiresClause(enhancementModuleDec.getName().getName(), enhancementModuleDec.getRequires());

                // Store the enhancement's type constraints from the module parameters
                // YS: We are not adding these automatically. Most of the time, these
                // constraints wouldn't really help us prove any of the VCs. If you
                // are ever interested in adding these to the givens list, use the
                // "addConstraints" flag. Note that these constraints still need to be
                // processed by the parsimonious step, so there is no guarantee that they
                // will show up in all of the VCs.
                if (myCompileEnvironment.flags.isFlagSet(FLAG_ADD_CONSTRAINT)) {
                    storeModuleParameterTypeConstraints(enhancementModuleDec.getLocation(),
                            enhancementModuleDec.getParameterDecs());
                }
            }
        } catch (NoSuchSymbolException e) {
            Utilities.noSuchModule(loc);
        }
    }

    /**
     * <p>
     * This method stores a {@code enhancement realization}'s module level {@code requires} and {@code constraint}
     * clauses for future use.
     * </p>
     *
     * @param loc
     *            The location of where we found the {@code enhancement realization}.
     * @param id
     *            A {@link ModuleIdentifier} referring to a {@code enhancement realization}.
     * @param isFacilityImport
     *            A flag that indicates whether or not we are storing information that originated from a
     *            {@link FacilityDec}.
     */
    public final void storeEnhancementRealizAssertionClauses(Location loc, ModuleIdentifier id,
            boolean isFacilityImport) {
        try {
            EnhancementRealizModuleDec realizModuleDec = (EnhancementRealizModuleDec) myBuilder.getModuleScope(id)
                    .getDefiningElement();

            // We only need to store these if they are part of a FacilityDec
            if (!isFacilityImport) {
                // Store the enhancement realization's requires clause
                storeRequiresClause(realizModuleDec.getName().getName(), realizModuleDec.getRequires());

                // Store the enhancement realization's type constraints from the module parameters
                // YS: We are not adding these automatically. Most of the time, these
                // constraints wouldn't really help us prove any of the VCs. If you
                // are ever interested in adding these to the givens list, use the
                // "addConstraints" flag. Note that these constraints still need to be
                // processed by the parsimonious step, so there is no guarantee that they
                // will show up in all of the VCs.
                if (myCompileEnvironment.flags.isFlagSet(FLAG_ADD_CONSTRAINT)) {
                    storeModuleParameterTypeConstraints(realizModuleDec.getLocation(),
                            realizModuleDec.getParameterDecs());
                }
            }
        } catch (NoSuchSymbolException e) {
            Utilities.noSuchModule(loc);
        }
    }

    /**
     * <p>
     * This method stores a {@code facility}'s module level {@code requires} and {@code constraint} clauses for future
     * use.
     * </p>
     *
     * @param loc
     *            The location of where we found the {@code facility}.
     * @param id
     *            A {@link ModuleIdentifier} referring to a {@code facility}.
     */
    public final void storeFacilityModuleAssertionClauses(Location loc, ModuleIdentifier id) {
        try {
            FacilityModuleDec facilityModuleDec = (FacilityModuleDec) myBuilder.getModuleScope(id).getDefiningElement();

            // Store the facility's requires clause
            storeRequiresClause(facilityModuleDec.getName().getName(), facilityModuleDec.getRequires());

            // Store the facility's type constraints from the module parameters
            // YS: We are not adding these automatically. Most of the time, these
            // constraints wouldn't really help us prove any of the VCs. If you
            // are ever interested in adding these to the givens list, use the
            // "addConstraints" flag. Note that these constraints still need to be
            // processed by the parsimonious step, so there is no guarantee that they
            // will show up in all of the VCs.
            if (myCompileEnvironment.flags.isFlagSet(FLAG_ADD_CONSTRAINT)) {
                storeModuleParameterTypeConstraints(facilityModuleDec.getLocation(),
                        facilityModuleDec.getParameterDecs());
            }
        } catch (NoSuchSymbolException e) {
            Utilities.noSuchModule(loc);
        }
    }

    /**
     * <p>
     * This method stores an object that records all relevant information of an instantiated {@code Facility} for future
     * use.
     * </p>
     *
     * @param decl
     *            A {@link InstantiatedFacilityDecl} containing all the information.
     */
    public final void storeInstantiatedFacilityDecl(InstantiatedFacilityDecl decl) {
        myProcessedInstFacilityDecls.add(decl);
    }

    /**
     * <p>
     * This method stores a shared realization declaration for future use.
     * </p>
     *
     * @param dec
     *            A shared state realization.
     */
    public final void storeLocalSharedRealizationDec(SharedStateRealizationDec dec) {
        myLocalSharedStateRealizationDecs.add((SharedStateRealizationDec) dec.clone());
    }

    /**
     * <p>
     * This method stores a type representation declaration for future use.
     * </p>
     *
     * @param dec
     *            A type representation.
     */
    public final void storeLocalTypeRepresentationDec(AbstractTypeRepresentationDec dec) {
        myLocalTypeRepresentationDecs.add((AbstractTypeRepresentationDec) dec.clone());
    }

    /**
     * <p>
     * This method returns the object in string format.
     * </p>
     *
     * @return Object as a string.
     */
    @Override
    public final String toString() {
        return asString(0, 4);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * An helper method for storing all the {@code constraint} clauses for a list of {@link ModuleParameterDec
     * ModuleParameterDecs}.
     * </p>
     *
     * @param loc
     *            The location of the {@code module} that contains the module parameters.
     * @param moduleParameterDecs
     *            A list of {@link ModuleParameterDec}.
     */
    private void storeModuleParameterTypeConstraints(Location loc, List<ModuleParameterDec> moduleParameterDecs) {
        for (ModuleParameterDec m : moduleParameterDecs) {
            Dec wrappedDec = m.getWrappedDec();
            if (wrappedDec instanceof ConstantParamDec) {
                ConstantParamDec dec = (ConstantParamDec) wrappedDec;
                ProgramTypeEntry typeEntry;

                if (dec.getVarDec().getTy() instanceof NameTy) {
                    NameTy pNameTy = (NameTy) dec.getVarDec().getTy();

                    // Query for the type entry in the symbol table
                    SymbolTableEntry ste = Utilities.searchProgramType(pNameTy.getLocation(), pNameTy.getQualifier(),
                            pNameTy.getName(), myCurrentModuleScope);
                    typeEntry = ste.toProgramTypeEntry(pNameTy.getLocation());

                    // Make sure we don't have a generic type
                    if (typeEntry.getDefiningElement() instanceof TypeFamilyDec) {
                        // Obtain the original dec from the AST
                        TypeFamilyDec type = (TypeFamilyDec) typeEntry.getDefiningElement();

                        if (!VarExp.isLiteralTrue(type.getConstraint().getAssertionExp())) {
                            AssertionClause constraintClause = type.getConstraint();
                            AssertionClause modifiedConstraint = Utilities.getTypeConstraintClause(constraintClause,
                                    dec.getLocation(), null, dec.getName(), type.getExemplar(),
                                    typeEntry.getModelType(), null);

                            // Store the constraint and its associated location detail for future use
                            Location constraintLoc = modifiedConstraint.getLocation();
                            myModuleLevelLocationDetails.put(modifiedConstraint, new LocationDetailModel(constraintLoc,
                                    constraintLoc, "Constraint Clause of " + dec.getName()));
                            myModuleLevelConstraints.put(dec, Collections.singletonList(modifiedConstraint));
                        }
                    }
                } else {
                    Utilities.tyNotHandled(dec.getVarDec().getTy(), loc);
                }
            }
        }
    }

    /**
     * <p>
     * An helper method for storing a {@code requires} clause and its associated location detail for future use.
     * </p>
     *
     * @param decName
     *            Name of the declaration that contains the {@code requiresClause}.
     * @param requiresClause
     *            An {@link AssertionClause} containing a {@code requires} clause.
     */
    private void storeRequiresClause(String decName, AssertionClause requiresClause) {
        if (!VarExp.isLiteralTrue(requiresClause.getAssertionExp())) {
            myModuleLevelRequires.add(requiresClause);

            // Add the location details for the requires clause
            Location assertionLoc = requiresClause.getAssertionExp().getLocation();
            myModuleLevelLocationDetails.put(requiresClause, new LocationDetailModel(assertionLoc.clone(),
                    assertionLoc.clone(), "Requires Clause of " + decName));
        }
    }

}
