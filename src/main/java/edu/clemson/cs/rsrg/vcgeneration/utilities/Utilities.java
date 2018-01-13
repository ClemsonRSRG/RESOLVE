/*
 * Utilities.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.vcgeneration.utilities;

import edu.clemson.cs.r2jt.rewriteprover.immutableadts.ImmutableList;
import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.AbstractTypeRepresentationDec;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.EqualsExp.Operator;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.*;
import edu.clemson.cs.rsrg.absyn.rawtypes.NameTy;
import edu.clemson.cs.rsrg.absyn.rawtypes.Ty;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.LocationDetailModel;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.entry.*;
import edu.clemson.cs.rsrg.typeandpopulate.entry.ProgramParameterEntry.ParameterMode;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.SymbolNotOfKindTypeException;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTGeneric;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.rsrg.typeandpopulate.query.NameQuery;
import edu.clemson.cs.rsrg.typeandpopulate.query.OperationQuery;
import edu.clemson.cs.rsrg.typeandpopulate.query.UnqualifiedNameQuery;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.cs.rsrg.treewalk.TreeWalkerVisitor;
import edu.clemson.cs.rsrg.vcgeneration.VCGenerator;
import edu.clemson.cs.rsrg.vcgeneration.sequents.Sequent;
import edu.clemson.cs.rsrg.vcgeneration.utilities.formaltoactual.FormalActualLists;
import edu.clemson.cs.rsrg.vcgeneration.utilities.formaltoactual.InstantiatedEnhSpecRealizItem;
import edu.clemson.cs.rsrg.vcgeneration.utilities.formaltoactual.InstantiatedFacilityDecl;
import java.util.*;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;

/**
 * <p>This class contains a bunch of utilities methods used by the {@link VCGenerator}
 * and all of its associated {@link TreeWalkerVisitor TreeWalkerVisitors}.</p>
 *
 * @author Yu-Shan Sun
 * @version 2.0
 */
public class Utilities {

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Converts the different types of {@link Exp} to the
     * ones used by the VC Generator.</p>
     *
     * @param oldExp The expression to be converted.
     * @param scope The module scope to start our search.
     *
     * @return A modified {@link Exp}.
     */
    public static Exp convertExp(Exp oldExp, ModuleScope scope) {
        Exp retExp;

        // Case #1: ProgramIntegerExp
        if (oldExp instanceof ProgramIntegerExp) {
            IntegerExp exp =
                    new IntegerExp(oldExp.getLocation(), null,
                            ((ProgramIntegerExp) oldExp).getValue());

            // At this point all programming integer expressions
            // should be greater than or equals to 0. Negative
            // numbers should have called the corresponding operation
            // to convert it to a negative number. Therefore, we
            // need to locate the type "N" (Natural Number)
            MathSymbolEntry mse =
                    searchMathSymbol(exp.getLocation(), "N", scope);
            try {
                exp.setMathType(mse.getTypeValue());
            }
            catch (SymbolNotOfKindTypeException e) {
                notAType(mse, exp.getLocation());
            }

            retExp = exp;
        }
        // Case #2: ProgramCharacterExp
        else if (oldExp instanceof ProgramCharExp) {
            CharExp exp =
                    new CharExp(oldExp.getLocation(),
                            ((ProgramCharExp) oldExp).getValue());
            exp.setMathType(oldExp.getMathType());
            retExp = exp;
        }
        // Case #3: ProgramStringExp
        else if (oldExp instanceof ProgramStringExp) {
            StringExp exp =
                    new StringExp(oldExp.getLocation(),
                            ((ProgramStringExp) oldExp).getValue());
            exp.setMathType(oldExp.getMathType());
            retExp = exp;
        }
        // Case #4: VariableDotExp
        else if (oldExp instanceof ProgramVariableDotExp) {
            List<ProgramVariableExp> segments =
                    ((ProgramVariableDotExp) oldExp).getSegments();
            List<Exp> newSegments = new ArrayList<>();

            // Need to replace each of the segments in a dot expression
            MTType lastMathType = null;
            MTType lastMathTypeValue = null;
            for (ProgramVariableExp v : segments) {
                // Can only be a ProgramVariableNameExp. Anything else
                // is a case we have not handled.
                if (v instanceof ProgramVariableNameExp) {
                    VarExp varExp =
                            new VarExp(v.getLocation(), v.getQualifier(),
                                    ((ProgramVariableNameExp) v).getName());
                    varExp.setMathType(v.getMathType());
                    varExp.setMathTypeValue(v.getMathTypeValue());
                    lastMathType = v.getMathType();
                    lastMathTypeValue = v.getMathTypeValue();
                    newSegments.add(varExp);
                }
                else {
                    expNotHandled(v, v.getLocation());
                }
            }

            // Set the segments and the type information.
            DotExp exp = new DotExp(oldExp.getLocation(), newSegments);
            exp.setMathType(lastMathType);
            exp.setMathTypeValue(lastMathTypeValue);
            retExp = exp;
        }
        // Case #5: VariableNameExp
        else if (oldExp instanceof ProgramVariableNameExp) {
            VarExp exp =
                    new VarExp(oldExp.getLocation(), ((ProgramVariableNameExp) oldExp).getQualifier(),
                            ((ProgramVariableNameExp) oldExp).getName());
            exp.setMathType(oldExp.getMathType());
            exp.setMathTypeValue(oldExp.getMathTypeValue());
            retExp = exp;
        }
        // Else simply return oldExp
        else {
            retExp = oldExp;
        }

        return retExp;
    }

    /**
     * <p>This method returns a {@link FunctionExp} with the specified
     * name and arguments.</p>
     *
     * @param loc Location that wants to create
     *            this function expression.
     * @param qualifier Qualifier for the function expression.
     * @param name Name of the function expression.
     * @param argExpList List of arguments to the function expression.
     * @param functionNameType Mathematical type for the function name.
     * @param funcType Mathematical type for the function expression.
     *
     * @return The new {@link FunctionExp}.
     */
    public static FunctionExp createFunctionExp(Location loc,
            PosSymbol qualifier, PosSymbol name, List<Exp> argExpList,
            MTType functionNameType, MTType funcType) {
        // Create a VarExp for the function name
        VarExp functionName =
                Utilities.createVarExp(loc, qualifier, name, functionNameType,
                        null);

        // Create the function expression
        FunctionExp exp = new FunctionExp(loc, functionName, null, argExpList);
        exp.setMathType(funcType);

        return exp;
    }

    /**
     * <p>This method returns a {@link DotExp} with the {@link VarDec}
     * and its initialization ensures clause.</p>
     *
     * @param varDec The declared variable.
     * @param booleanType Mathematical boolean type.
     *
     * @return The new {@link DotExp}.
     */
    public static DotExp createInitExp(VarDec varDec, MTType booleanType) {
        // Convert the declared variable into a VarExp
        VarExp varExp =
                Utilities.createVarExp(varDec.getLocation(), null, varDec.getName(),
                        varDec.getTy().getMathTypeValue(), null);

        // Create a VarExp using the type
        VarExp typeNameExp = null;
        if (varDec.getTy() instanceof NameTy) {
            NameTy ty = (NameTy) varDec.getTy();
            typeNameExp =
                    Utilities.createVarExp(ty.getLocation(), ty.getQualifier(), ty.getName(),
                            ty.getMathType(), ty.getMathTypeValue());
        }
        else {
            Utilities.tyNotHandled(varDec.getTy(), varDec.getTy().getLocation());
        }

        // Create the "Is_Initial" FunctionExp
        List<Exp> isInitialArgs = new ArrayList<>();
        isInitialArgs.add(varExp);
        FunctionExp isInitialExp =
                createFunctionExp(varDec.getLocation(), null,
                        new PosSymbol(varDec.getLocation(), "Is_Initial"),
                        isInitialArgs, varDec.getTy().getMathType(), booleanType);

        // Create the DotExp
        List<Exp> segments = new ArrayList<>();
        segments.add(typeNameExp);
        segments.add(isInitialExp);

        return new DotExp(varDec.getLocation(), segments);
    }

    /**
     * <p>This method creates a list of {@link VarExp VarExps}
     * representing each of the {@code Operation's} {@link ParameterVarDec ParameterVarDecs}.</p>
     *
     * @param parameterVarDecs List of operation parameters.
     *
     * @return A list containing the {@link VarExp VarExps} representing
     * each operation parameter.
     */
    public static List<VarExp> createOperationParamExpList(List<ParameterVarDec> parameterVarDecs) {
        List<VarExp> retExpList = new ArrayList<>(parameterVarDecs.size());

        // Create a VarExp representing each of the operation parameters
        for (ParameterVarDec dec : parameterVarDecs) {
            retExpList.add(Utilities.createVarExp(dec.getLocation(),
                    null, dec.getName(), dec.getMathType(), null));
        }

        return retExpList;
    }

    /**
     * <p>This method returns a newly created {@link VarExp}
     * with {@code P_Val} as the name and {@code N} as its math type.</p>
     *
     * @param loc New {@link VarExp VarExp's} {@link Location}.
     * @param scope The module scope to start our search.
     *
     * @return {@code P_Val} variable expression.
     */
    public static VarExp createPValExp(Location loc, ModuleScope scope) {
        VarExp retExp = null;

        // Locate "N" (Natural Number)
        MathSymbolEntry mse = searchMathSymbol(loc, "N", scope);
        try {
            // Create a variable with the name P_val
            retExp =
                    createVarExp(loc.clone(), null, new PosSymbol(loc.clone(),
                            "P_Val"), mse.getTypeValue(), null);
        }
        catch (SymbolNotOfKindTypeException e) {
            notAType(mse, loc);
        }

        return retExp;
    }

    /**
     * <p>This method uses all the {@code requires} and {@code constraint}
     * clauses from the various different sources (see below for complete list)
     * and builds the appropriate {@code assume} clause that goes at the
     * beginning an {@link AssertiveCodeBlock}.</p>
     *
     * <p>List of different places where clauses can originate from:</p>
     * <ul>
     *     <li>{@code Concept}'s {@code requires} clause.</li>
     *     <li>{@code Concept}'s module {@code constraint} clause.</li>
     *     <li>{@code Shared Variables}' {@code constraint} clause.</li>
     *     <li>{@code Concept Realization}'s {@code requires} clause.</li>
     *     <li>{@code Shared Variables}' {@code convention} clause.</li>
     *     <li>{@code Shared Variables}' {@code correspondence} clause.</li>
     *     <li>{@code constraint} clauses for all the parameters with the
     *     appropriate substitutions made.</li>
     *     <li>Any {@code which_entails} expressions that originated from any of the
     *     clauses above.</li>
     * </ul>
     *
     * @param loc The location in the AST that we are
     *            currently visiting.
     * @param moduleLevelRequiresClauses A list containing all the module level {@code requires} clauses.
     * @param moduleLevelConstraintClauses A map containing all the module level {@code constraint} clauses.
     * @param moduleLevelLocationDetails A map containing {@link LocationDetailModel} for
     *                                   the module level clauses.
     *
     * @return The top-level assumed expression.
     */
    public static Exp createTopLevelAssumeExpFromContext(Location loc,
            List<AssertionClause> moduleLevelRequiresClauses,
            Map<Dec, List<AssertionClause>> moduleLevelConstraintClauses,
            Map<AssertionClause, LocationDetailModel> moduleLevelLocationDetails) {
        Exp retExp = null;

        // Add the module level requires clause. Note that we don't
        // need to add their location details to the map because
        // it is there already.
        for (AssertionClause clause : moduleLevelRequiresClauses) {
            retExp =
                    formConjunct(loc, retExp, clause,
                            moduleLevelLocationDetails.get(clause));
        }

        // Add the module level constraint clauses.
        // Note that we don't need to add their location details to the map
        // because it is there already.
        for (Dec dec : moduleLevelConstraintClauses.keySet()) {
            for (AssertionClause clause : moduleLevelConstraintClauses.get(dec)) {
                retExp =
                        formConjunct(loc, retExp, clause,
                                moduleLevelLocationDetails.get(clause));
            }
        }

        // TODO: Add any shared variable's constraint, convention, correspondence here.

        return retExp;
    }

    /**
     * <p>This method uses all the {@code requires} and {@code constraint}
     * clauses from the various different sources (see below for complete list)
     * and builds the appropriate {@code assume} clause that goes at the
     * beginning an {@link AssertiveCodeBlock}.</p>
     *
     * <p>List of different places where clauses can originate from:</p>
     * <ul>
     *     <li>{@code Concept}'s {@code requires} clause.</li>
     *     <li>{@code Concept}'s module {@code constraint} clause.</li>
     *     <li>{@code Shared Variables}' {@code constraint} clause.</li>
     *     <li>{@code Concept Realization}'s {@code requires} clause.</li>
     *     <li>{@code Shared Variables}' {@code convention} clause.</li>
     *     <li>{@code Shared Variables}' {@code correspondence} clause.</li>
     *     <li>{@code constraint} clauses for all the parameters with the
     *     appropriate substitutions made.</li>
     *     <li>The {@code operation}'s {@code requires} clause with the following
     *     change if it is an implementation for a {@code concept}'s operation:</li>
     *     <li>
     *         <ul>
     *             <li>Substitute the parameter name with {@code Conc.<name>} if this
     *             is the type we are implementing in a {@code concept realization}.</li>
     *         </ul>
     *     </li>
     *     <li>Any {@code which_entails} expressions that originated from any of the
     *     clauses above.</li>
     * </ul>
     *
     * <p>See the {@code Procedure} declaration rule for more detail.</p>
     *
     * @param loc The location in the AST that we are
     *            currently visiting.
     * @param scope The module scope to start our search.
     * @param currentBlock The current {@link AssertiveCodeBlock} we are currently generating.
     * @param moduleLevelRequiresClauses A list containing all the module level {@code requires} clauses.
     * @param moduleLevelConstraintClauses A map containing all the module level {@code constraint} clauses.
     * @param moduleLevelLocationDetails A map containing {@link LocationDetailModel} for
     *                                   the module level clauses.
     * @param correspondingOperationEntry The corresponding {@link OperationEntry}.
     * @param typeFamilyDecs List of abstract types we are implementing or extending.
     * @param localRepresentationTypeDecs List of local representation types.
     * @param processedInstFacDecs List of {@link InstantiatedFacilityDecl} we have processed
     *                             so far.
     * @param isLocalOperation {@code true} if it is a local operation, {@code false} otherwise.
     *
     * @return The top-level assumed expression.
     */
    public static Exp createTopLevelAssumeExpForProcedureDec(
            Location loc,
            ModuleScope scope,
            AssertiveCodeBlock currentBlock,
            List<AssertionClause> moduleLevelRequiresClauses,
            Map<Dec, List<AssertionClause>> moduleLevelConstraintClauses,
            Map<AssertionClause, LocationDetailModel> moduleLevelLocationDetails,
            OperationEntry correspondingOperationEntry,
            List<TypeFamilyDec> typeFamilyDecs,
            List<AbstractTypeRepresentationDec> localRepresentationTypeDecs,
            List<InstantiatedFacilityDecl> processedInstFacDecs,
            boolean addConstraints, boolean isLocalOperation) {
        // Add all the expressions we can assume from the current context
        Exp retExp =
                createTopLevelAssumeExpFromContext(loc,
                        moduleLevelRequiresClauses,
                        moduleLevelConstraintClauses,
                        moduleLevelLocationDetails);

        // Add the operation's requires clause (and any which_entails clause)
        AssertionClause requiresClause =
                correspondingOperationEntry.getRequiresClause();
        Exp requiresExp = requiresClause.getAssertionExp().clone();
        if (!VarExp.isLiteralTrue(requiresExp)) {
            // Form a conjunct with the requires clause and add
            // the location detail associated with it.
            retExp =
                    formConjunct(loc, retExp, requiresClause,
                            new LocationDetailModel(requiresClause
                                    .getLocation().clone(), requiresClause
                                    .getLocation().clone(),
                                    "Requires Clause of "
                                            + correspondingOperationEntry
                                                    .getName()));
        }

        // Store the operation parameter's type constraints.
        // YS: We are not adding these automatically. Most of the time, these
        //     constraints wouldn't really help us prove any of the VCs. If you
        //     are ever interested in adding these to the givens list, use the
        //     "addConstraints" flag. Note that these constraints still need to be
        //     processed by the parsimonious step, so there is no guarantee that they
        //     will show up in all of the VCs.
        if (addConstraints) {
            retExp =
                    addParamTypeConstraints(loc, retExp, scope, currentBlock,
                            correspondingOperationEntry.getParameters(),
                            typeFamilyDecs, localRepresentationTypeDecs,
                            processedInstFacDecs);
        }

        return retExp;
    }

    /**
     * <p>This method returns a newly created {@link VarExp}
     * with the {@link PosSymbol} and math type provided.</p>
     *
     * @param loc New {@link VarExp VarExp's} {@link Location}.
     * @param qualifier New {@link VarExp VarExp's} qualifier.
     * @param name New {@link VarExp VarExp's} name.
     * @param type New {@link VarExp VarExp's} math type.
     * @param typeValue New {@link VarExp VarExp's} math type value.
     *
     * @return The new {@link VarExp}.
     */
    public static VarExp createVarExp(Location loc, PosSymbol qualifier,
            PosSymbol name, MTType type, MTType typeValue) {
        // Create the VarExp
        VarExp exp = new VarExp(loc, qualifier, name);
        exp.setMathType(type);
        exp.setMathTypeValue(typeValue);

        return exp;
    }

    /**
     * <p>An helper method that creates a new "question mark" variable.</p>
     *
     * @param currentBlock The current {@link AssertiveCodeBlock} we are currently generating.
     * @param varExp The original expression.
     *
     * @return A {@link VCVarExp}.
     */
    public static VCVarExp createVCVarExp(AssertiveCodeBlock currentBlock,
            Exp varExp) {
        VCVarExp exp = new VCVarExp(varExp.getLocation(), varExp);
        if (currentBlock.containsFreeVar(exp)) {
            exp = createVCVarExp(currentBlock, exp);
        }

        return exp;
    }

    /**
     * <p>An helper method to print a list of expressions.</p>
     *
     * @param exps A list of {@link Exp Exps}.
     *
     * @return A formatted string.
     */
    public static String expListAsString(List<Exp> exps) {
        StringBuilder sb = new StringBuilder();

        Iterator<Exp> expIterator = exps.iterator();
        while (expIterator.hasNext()) {
            Exp nextExp = expIterator.next();
            sb.append(nextExp.asString(0, 0));

            if (expIterator.hasNext()) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    /**
     * <p>An helper method that throws the appropriate message that
     * the expression type that we found isn't handled.</p>
     *
     * @param exp An expression.
     * @param loc Location where this expression was found.
     */
    public static void expNotHandled(Exp exp, Location loc) {
        String message =
                "[VCGenerator] Exp type not handled: "
                        + exp.getClass().getCanonicalName();
        throw new SourceErrorException(message, loc);
    }

    /**
     * <p>An helper method that uses the assertion expression and any
     * {@code which_entails} expressions to {@code exp} to form a new
     * conjuncted expression.</p>
     *
     * @param loc The location in the AST that we are
     *            currently visiting.
     * @param exp The original expression.
     * @param clause An {@link AssertionClause}.
     * @param clauseDetailModel The {@link LocationDetailModel} associated
     *                          with {@code clause}.
     *
     * @return A new {@link Exp}.
     */
    public static Exp formConjunct(Location loc, Exp exp,
            AssertionClause clause, LocationDetailModel clauseDetailModel) {
        Exp retExp;

        // Add the assertion expression
        Exp assertionExp = clause.getAssertionExp().clone();
        assertionExp.setLocationDetailModel(clauseDetailModel);
        if (exp == null) {
            retExp = assertionExp;
        }
        else {
            retExp = InfixExp.formConjunct(loc, exp, assertionExp);
        }

        // Add any which_entails
        if (clause.getWhichEntailsExp() != null) {
            Exp whichEntailsExp = clause.getWhichEntailsExp().clone();
            Location entailsLoc = clause.getWhichEntailsExp().getLocation();
            whichEntailsExp.setLocationDetailModel(new LocationDetailModel(
                    entailsLoc.clone(), entailsLoc.clone(),
                    "Which_Entails Expression Located at "
                            + clause.getLocation()));
            retExp = InfixExp.formConjunct(loc, retExp, whichEntailsExp);
        }

        return retExp;
    }

    /**
     * <p>An helper method that returns {@link ProgramFunctionExp ProgramFunctionExp's}
     * corresponding {@link OperationEntry}.</p>
     *
     * @param functionExp A program function expression.
     * @param scope The module scope to start our search.
     *
     * @return The corresponding {@link OperationEntry}.
     */
    public static OperationEntry getOperationEntry(ProgramFunctionExp functionExp, ModuleScope scope) {
        // Obtain the corresponding program types from the arguments
        List<PTType> argTypes = new LinkedList<>();
        for (ProgramExp arg : functionExp.getArguments()) {
            argTypes.add(arg.getProgramType());
        }

        return Utilities.searchOperation(functionExp.getLocation(),
                functionExp.getQualifier(), functionExp.getName(), argTypes, scope);
    }

    /**
     * <p>Given the original {@code constraint} clause, use the provided information
     * on the actual parameter variable to substitute the {@code exemplar} in the
     * {@code constraint} clause and create a new {@link AssertionClause}.</p>
     *
     * @param originalConstraintClause The {@link AssertionClause} containing the original
     *                                 {@code constraint} clause.
     * @param loc The location in the AST that we are
     *            currently visiting.
     * @param qualifier The parameter variable's qualifier.
     * @param name The parameter variable's name.
     * @param exemplarName The {@code exemplar} name for the corresponding type.
     * @param type The mathematical type associated with this type.
     * @param typeValue The mathematical type value associated with this type.
     *
     * @return A modified {@link AssertionClause} containing the new {@code constraint}
     * clause.
     */
    public static AssertionClause getTypeConstraintClause(AssertionClause originalConstraintClause, Location loc,
            PosSymbol qualifier, PosSymbol name, PosSymbol exemplarName, MTType type, MTType typeValue) {
        // Create a variable expression from the declared variable
        VarExp varDecExp = Utilities.createVarExp(loc, qualifier, name, type, typeValue);

        // Create a variable expression from the type exemplar
        VarExp exemplar = Utilities.createVarExp(loc, null, exemplarName, type, typeValue);

        // Create a replacement map
        Map<Exp, Exp> substitutions = new HashMap<>();
        substitutions.put(exemplar, varDecExp);

        // Create new assertion clause by replacing the exemplar with the actual
        Location newLoc = loc.clone();
        Exp constraintWithReplacements =
                originalConstraintClause.getAssertionExp().substitute(substitutions);
        Exp whichEntailsWithReplacements = null;
        if (originalConstraintClause.getWhichEntailsExp() != null) {
            whichEntailsWithReplacements =
                    originalConstraintClause.getWhichEntailsExp().substitute(substitutions);
        }

        return new AssertionClause(newLoc, AssertionClause.ClauseType.CONSTRAINT,
                constraintWithReplacements, whichEntailsWithReplacements);
    }

    /**
     * <p>Given the original {@code ensures} clause, use the provided
     * information on the actual parameter variable to substitute the {@code exemplar} in
     * the {@code initialization ensures} clause and create a new {@link AssertionClause}.</p>
     *
     * @param originalEnsuresClause The {@link AssertionClause} containing the
     *                              original {@code ensures} clause.
     * @param loc The location in the AST that we are
     *            currently visiting.
     * @param qualifier The parameter variable's qualifier.
     * @param name The parameter variable's name.
     * @param exemplarName The {@code exemplar} name for the corresponding type.
     * @param type The mathematical type associated with this type.
     * @param typeValue The mathematical type value associated with this type.
     *
     * @return A modified {@link AssertionClause} containing the new
     * {@code ensures} clause.
     */
    public static AssertionClause getTypeEnsuresClause(AssertionClause originalEnsuresClause,
            Location loc, PosSymbol qualifier, PosSymbol name,
            PosSymbol exemplarName, MTType type, MTType typeValue) {
        // Create a variable expression from the declared variable
        VarExp varDecExp = Utilities.createVarExp(loc, qualifier, name, type, typeValue);

        // Create a variable expression from the type exemplar
        VarExp exemplar = Utilities.createVarExp(loc, null, exemplarName, type, typeValue);

        // Create a replacement map
        Map<Exp, Exp> substitutions = new HashMap<>();
        substitutions.put(exemplar, varDecExp);

        // Create new assertion clause by replacing the exemplar with the actual
        Location newLoc = loc.clone();
        Exp constraintWithReplacements =
                originalEnsuresClause.getAssertionExp().substitute(substitutions);
        Exp whichEntailsWithReplacements = null;
        if (originalEnsuresClause.getWhichEntailsExp() != null) {
            whichEntailsWithReplacements =
                    originalEnsuresClause.getWhichEntailsExp().substitute(substitutions);
        }

        return new AssertionClause(newLoc, AssertionClause.ClauseType.ENSURES,
                constraintWithReplacements, whichEntailsWithReplacements);
    }

    /**
     * <p>Given a {@link ProgramVariableExp}, return the associated
     * {@link PosSymbol} name.</p>
     *
     * @param exp A program variable expression.
     *
     * @return A {@link PosSymbol} name.
     */
    public static PosSymbol getVarName(ProgramVariableExp exp) {
        // Return value
        PosSymbol name = null;

        // Program variable expression
        if (exp instanceof ProgramVariableNameExp) {
            name = ((ProgramVariableNameExp) exp).getName();
        }
        // Program dotted expression.
        else if (exp instanceof ProgramVariableDotExp) {
            List<ProgramVariableExp> segments =
                    ((ProgramVariableDotExp) exp).getSegments();
            name = getVarName(segments.get(segments.size() - 1));
        }
        // Can't handle any other kind of expressions
        else {
            expNotHandled(exp, exp.getLocation());
        }

        return name;
    }

    /**
     * <p>Given the name of an operation check to see if it is a
     * local operation</p>
     *
     * @param name The name of the operation.
     * @param scope The module scope we are searching.
     *
     * @return {@code true} if it is a local operation, {@code false} otherwise.
     */
    public static boolean isLocationOperation(String name, ModuleScope scope) {
        boolean isIn;

        // Query for the corresponding operation
        List<SymbolTableEntry> entries =
                scope.query(new NameQuery(null, name,
                        ImportStrategy.IMPORT_NONE,
                        FacilityStrategy.FACILITY_IGNORE, true));

        // Not found
        if (entries.size() == 0) {
            isIn = false;
        }
        // Found one
        else if (entries.size() == 1) {
            // If the operation is declared here, then it will be an OperationEntry.
            // Thus it is a local operation.
            isIn = entries.get(0) instanceof OperationEntry;
        }
        // Found more than one
        else {
            //This should be caught earlier, when the duplicate symbol is
            //created
            throw new RuntimeException();
        }

        return isIn;
    }

    /**
     * <p>An helper method that negates the incoming expression.</p>
     *
     * @param exp The expression to be negated.
     * @param booleanType Mathematical boolean type.
     *
     * @return A modified {@link Exp}.
     */
    public static Exp negateExp(Exp exp, MTType booleanType) {
        Exp retExp;

        // Case 1: Some kind of equality expression
        if (exp instanceof EqualsExp) {
            EqualsExp expAsEqualsExp = (EqualsExp) exp;

            // Obtain the new operator
            Operator newOperator;
            if (expAsEqualsExp.getOperator() == Operator.EQUAL) {
                newOperator = Operator.NOT_EQUAL;
            }
            else {
                newOperator = Operator.EQUAL;
            }

            // Copy the qualifier if any
            PosSymbol newQualifier = null;
            if (expAsEqualsExp.getQualifier() != null) {
                newQualifier = expAsEqualsExp.getQualifier().clone();
            }

            // Create the negation of the equality expression.
            retExp =
                    new EqualsExp(expAsEqualsExp.getLocation().clone(),
                            expAsEqualsExp.getLeft().clone(), newQualifier,
                            newOperator, expAsEqualsExp.getRight().clone());
        }
        // Case 2: Some kind of prefix expression
        else if (exp instanceof PrefixExp) {
            PrefixExp expAsPrefixExp = (PrefixExp) exp;

            // We can only deal with not expressions.
            // Any other kind of expression, we simply negate it!
            if (expAsPrefixExp.getOperatorAsString().equals("not")) {
                retExp = expAsPrefixExp.getArgument().clone();
            }
            else {
                // Copy the qualifier if any
                PosSymbol newQualifier = null;
                if (expAsPrefixExp.getQualifier() != null) {
                    newQualifier = expAsPrefixExp.getQualifier().clone();
                }

                retExp =
                        new PrefixExp(expAsPrefixExp.getLocation().clone(),
                                newQualifier, new PosSymbol(expAsPrefixExp
                                        .getLocation().clone(), "not"),
                                expAsPrefixExp.clone());
            }
        }
        // Case 3: All other kinds of expressions.
        else {
            retExp =
                    new PrefixExp(exp.getLocation().clone(), null,
                            new PosSymbol(exp.getLocation().clone(), "not"),
                            exp.clone());
        }

        // Set the type of this new expression to be boolean
        retExp.setMathType(booleanType);

        return retExp;
    }

    /**
     * <p>An helper method that throws the appropriate message that
     * the symbol table entry that we found isn't a type.</p>
     *
     * @param entry A symbol table entry.
     * @param loc Location where this entry was found.
     */
    public static void notAType(SymbolTableEntry entry, Location loc) {
        throw new SourceErrorException("[VCGenerator] "
                + entry.getSourceModuleIdentifier()
                        .fullyQualifiedRepresentation(entry.getName())
                + " is not known to be a type.", loc);
    }

    /**
     * <p>An helper method that throws the appropriate no module found
     * message.</p>
     *
     * @param loc Location where this module name was found.
     */
    public static void noSuchModule(Location loc) {
        throw new SourceErrorException(
                "[VCGenerator] Module does not exist or is not in scope.", loc);
    }

    /**
     * <p>An helper method that throws the appropriate no symbol found
     * message.</p>
     *
     * @param qualifier The symbol's qualifier.
     * @param symbolName The symbol's name.
     * @param loc Location where this symbol was found.
     */
    public static void noSuchSymbol(PosSymbol qualifier, String symbolName,
            Location loc) {
        String message;

        if (qualifier == null) {
            message = "[VCGenerator] No such symbol: " + symbolName;
        }
        else {
            message =
                    "[VCGenerator] No such symbol in module: "
                            + qualifier.getName() + "::" + symbolName;
        }

        throw new SourceErrorException(message, loc);
    }

    /**
     * <p>This method is used to check for there are paths from
     * {@code originalSequent} to each sequent in {@code resultSequents}
     * in the reduction tree.</p>
     *
     * @param g The reduction tree.
     * @param originalSequent The original {@link Sequent}.
     * @param resultSequents The {@link Sequent} that resulted from the
     *                       sequent reduction applications.
     *
     * @return {@code true} if all the {@link Sequent} in {@code resultSequents}
     * have a path from {@code originalSequent} in the reduction tree,
     * {@code false} otherwise.
     */
    public static boolean pathsExist(Graph<Sequent, DefaultEdge> g,
            Sequent originalSequent, List<Sequent> resultSequents) {
        boolean retVal = true;

        // Check to see if the originalSequent is in the tree.
        // YS: Should be in here, but just in case it isn't...
        if (!g.containsVertex(originalSequent)) {
            retVal = false;
        }

        // Check to see if there is a path from originalSequent to each
        // sequent in resultSequents.
        // YS: We choose to use Dijkstra's algorithm, but we could chose
        // other ones if needed.
        ShortestPathAlgorithm<Sequent, DefaultEdge> pathAlgorithm = new DijkstraShortestPath<>(g);
        Iterator<Sequent> iterator = resultSequents.iterator();
        while(iterator.hasNext() && retVal) {
            Sequent next = iterator.next();

            // Check to see if there is a path from
            if (pathAlgorithm.getPath(originalSequent, next) == null) {
                retVal = false;
            }
        }

        return retVal;
    }

    /**
     * <p>This method is used to replace any parameter declarations whose program types
     * come from instantiated facility declarations. This will replace any the instantiated
     * type's formal with its actual instantiation expression in {@code clauseExp}.</p>
     *
     * @param clauseExp Some clause expression we are trying to replace.
     * @param paramList List of parameter declarations from the operation we are
     *                  trying to call.
     * @param currentModuleName Name of the current module.
     * @param typeFamilyDecs List of abstract types we are implementing or extending.
     * @param localRepresentationTypeDecs List of local representation types.
     * @param processedInstFacDecs List of {@link InstantiatedFacilityDecl} we have processed
     *                             so far.
     *
     * @return The modified {@link Exp}.
     */
    public static Exp replaceFacilityFormalWithActual(Exp clauseExp,
            List<ParameterVarDec> paramList, PosSymbol currentModuleName,
            List<TypeFamilyDec> typeFamilyDecs,
            List<AbstractTypeRepresentationDec> localRepresentationTypeDecs,
            List<InstantiatedFacilityDecl> processedInstFacDecs) {
        // Make a copy of the clauseExp for modification
        Exp modifiedClauseExp = clauseExp.clone();

        // YS: Check each operation parameter's raw type. If it matches
        // one that originated from an instantiated facility type,
        // then replace any formal parameters with its corresponding
        // instantiation argument.
        for (ParameterVarDec dec : paramList) {
            // YS: For it to be a instantiated type by a facility,
            // it must be a NameTy. It can't be a RecordTy or ArbitraryTy.
            // We also ignore any generic types.
            if ((dec.getTy() instanceof NameTy)
                    && !(dec.getTy().getProgramType() instanceof PTGeneric)) {
                NameTy decTyAsNameTy = (NameTy) dec.getTy();

                // Make sure it is an instantiated facility type.
                // YS: The way we check this is by process of elimination.
                // It can't be a type we are implementing (concept realizations)
                // or a extending some functionality (enhancements).
                boolean isInstantiatedType = true;
                if (decTyAsNameTy.getQualifier() == null) {
                    // Check all concept types
                    Iterator<TypeFamilyDec> it = typeFamilyDecs.iterator();
                    while (it.hasNext() && isInstantiatedType) {
                        // If the name matches, then it must be a concept abstract type
                        if (decTyAsNameTy.getName().getName().equals(
                                it.next().getName().getName())) {
                            isInstantiatedType = false;
                        }
                    }

                    // Check all representation types.
                    Iterator<AbstractTypeRepresentationDec> it2 =
                            localRepresentationTypeDecs.iterator();
                    while (it2.hasNext() && isInstantiatedType) {
                        // If the name matches, then it must be the representation type
                        if (decTyAsNameTy.getName().getName().equals(
                                it2.next().getName().getName())) {
                            isInstantiatedType = false;
                        }
                    }
                }
                else {
                    // This is a no brainer. If the qualifier matches the current
                    // module name, then it isn't a instantiated type.
                    if (decTyAsNameTy.getQualifier().getName().equals(
                            currentModuleName.getName())) {
                        isInstantiatedType = false;
                    }
                }

                // YS: Only proceed if it is a instantiated type.
                // Loop through each instantiated facility declaration
                // and obtain the facility that instantiated this type.
                if (isInstantiatedType) {
                    InstantiatedFacilityDecl instantiatedFacilityDecl = null;
                    Iterator<InstantiatedFacilityDecl> it =
                            processedInstFacDecs.iterator();
                    if (decTyAsNameTy.getQualifier() == null) {
                        while (it.hasNext() && instantiatedFacilityDecl == null) {
                            InstantiatedFacilityDecl nextDec = it.next();

                            // Search the types that the instantiated facility declaration
                            // implements. If it one of them matches, then it must be the type
                            // we are looking for. There can't be another type with the same name,
                            // because the Populator would have complained about it being ambiguous.
                            Iterator<TypeFamilyDec> it2 =
                                    nextDec.getConceptDeclaredTypes()
                                            .iterator();
                            while (it2.hasNext()
                                    && instantiatedFacilityDecl == null) {
                                if (decTyAsNameTy.getName().getName().equals(
                                        it2.next().getName().getName())) {
                                    instantiatedFacilityDecl = nextDec;
                                }
                            }
                        }
                    }
                    else {
                        while (it.hasNext() && instantiatedFacilityDecl == null) {
                            // Match the facility declaration name with the parameter type qualifier.
                            InstantiatedFacilityDecl nextDec = it.next();
                            if (decTyAsNameTy.getQualifier().getName().equals(
                                    nextDec.getInstantiatedFacilityName()
                                            .getName())) {
                                instantiatedFacilityDecl = nextDec;
                            }
                        }
                    }

                    // Throw an error if we reached this point and didn't find
                    // the instantiating facility declaration.
                    if (instantiatedFacilityDecl == null) {
                        throw new MiscErrorException(
                                "[VCGenerator] Couldn't replace formal parameters with the instantiated arguments in "
                                        + clauseExp.toString(),
                                new RuntimeException());
                    }
                    else {
                        // Replace concept's formal parameters with actual
                        // instantiation arguments
                        FormalActualLists conceptFormalActuals =
                                instantiatedFacilityDecl
                                        .getConceptParamArgLists();
                        modifiedClauseExp =
                                replaceFormalWithActual(modifiedClauseExp,
                                        conceptFormalActuals
                                                .getFormalParamList(),
                                        conceptFormalActuals.getActualArgList());

                        // Replace concept realization formal parameters
                        // with actual instantiation arguments
                        FormalActualLists conceptRealizFormalActuals =
                                instantiatedFacilityDecl
                                        .getConceptRealizParamArgLists();
                        modifiedClauseExp =
                                replaceFormalWithActual(modifiedClauseExp,
                                        conceptRealizFormalActuals
                                                .getFormalParamList(),
                                        conceptRealizFormalActuals
                                                .getActualArgList());

                        // Replace enhancement/enhancement realization's
                        // formal parameters with actual instantiation arguments.
                        List<InstantiatedEnhSpecRealizItem> items =
                                instantiatedFacilityDecl
                                        .getInstantiatedEnhSpecRealizItems();
                        for (InstantiatedEnhSpecRealizItem item : items) {
                            // Enhancement
                            FormalActualLists enhancementFormalActuals =
                                    item.getEnhancementParamArgLists();
                            modifiedClauseExp =
                                    replaceFormalWithActual(modifiedClauseExp,
                                            enhancementFormalActuals
                                                    .getFormalParamList(),
                                            enhancementFormalActuals
                                                    .getActualArgList());

                            // Enhancement Realization
                            FormalActualLists enhancementRealizFormalActuals =
                                    item.getEnhancementRealizParamArgLists();
                            modifiedClauseExp =
                                    replaceFormalWithActual(modifiedClauseExp,
                                            enhancementRealizFormalActuals
                                                    .getFormalParamList(),
                                            enhancementRealizFormalActuals
                                                    .getActualArgList());
                        }
                    }
                }
            }
        }

        return modifiedClauseExp;
    }

    /**
     * <p>This method is used to replace the module parameters with the actual instantiated arguments.
     * Note that both of these have been converted to mathematical expressions.</p>
     *
     * @param exp The expression to be replaced.
     * @param formalParams List of module formal parameters.
     * @param actualArgs List of module instantiated arguments.
     *
     * @return The modified expression.
     *
     * @throws MiscErrorException This exception is thrown when we pass two lists that aren't equal
     * in size.
     */
    public static Exp replaceFormalWithActual(Exp exp, List<VarExp> formalParams, List<Exp> actualArgs) {
        // YS: We need two replacement maps in case we happen to have the
        // same names in formal parameters expressions and in the argument list.
        Map<Exp, Exp> paramToTemp = new HashMap<>();
        Map<Exp, Exp> tempToActual = new HashMap<>();

        Exp retExp = exp.clone();
        if (formalParams.size() == actualArgs.size()) {
            // Loop through both lists
            for (int i = 0; i < formalParams.size(); i++) {
                VarExp formalParam = formalParams.get(i);
                Exp actualArg = actualArgs.get(i);

                // A temporary VarExp that avoids any formal with the same name as the actual.
                VarExp tempExp = Utilities.createVarExp(formalParam.getLocation(), null,
                        new PosSymbol(formalParam.getLocation(), "_" + formalParam.getName().getName()),
                        actualArg.getMathType(), actualArg.getMathTypeValue());

                // Add a substitution entry from formal parameter to tempExp.
                paramToTemp.put(formalParam, tempExp);

                // Add a substitution entry from tempExp to actual parameter.
                tempToActual.put(tempExp, actualArg);
            }

            // Replace from formal to temp and then from temp to actual
            retExp = retExp.substitute(paramToTemp);
            retExp = retExp.substitute(tempToActual);
        }
        else {
            // Something went wrong while obtaining the parameter and argument lists.
            throw new MiscErrorException(
                    "[VCGenerator] Formal parameter size is different than actual argument size.",
                    new RuntimeException());
        }

        return retExp;
    }

    /**
     * <p>Given a math symbol name, locate and return
     * the {@link MathSymbolEntry} stored in the
     * symbol table.</p>
     *
     * @param loc The location in the AST that we are
     *            currently visiting.
     * @param name The string name of the math symbol.
     * @param scope The module scope to start our search.
     *
     * @return A {@link MathSymbolEntry} from the
     *         symbol table.
     */
    public static MathSymbolEntry searchMathSymbol(Location loc, String name,
            ModuleScope scope) {
        // Query for the corresponding math symbol
        MathSymbolEntry ms = null;
        try {
            ms =
                    scope.queryForOne(
                            new UnqualifiedNameQuery(name,
                                    ImportStrategy.IMPORT_RECURSIVE,
                                    FacilityStrategy.FACILITY_IGNORE, true,
                                    true)).toMathSymbolEntry(loc);
        }
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(null, name, loc);
        }
        catch (DuplicateSymbolException dse) {
            //This should be caught earlier, when the duplicate symbol is
            //created
            throw new RuntimeException(dse);
        }

        return ms;
    }

    /**
     * <p>Given the qualifier, name and the list of argument
     * types, locate and return the {@link OperationEntry}
     * stored in the symbol table.</p>
     *
     * @param loc The location in the AST that we are
     *            currently visiting.
     * @param qualifier The qualifier of the operation.
     * @param name The name of the operation.
     * @param argTypes The list of argument types.
     * @param scope The module scope to start our search.
     *
     * @return An {@link OperationEntry} from the
     *         symbol table.
     */
    public static OperationEntry searchOperation(Location loc,
            PosSymbol qualifier, PosSymbol name, List<PTType> argTypes,
            ModuleScope scope) {
        // Query for the corresponding operation
        OperationEntry op = null;
        try {
            op =
                    scope.queryForOne(new OperationQuery(qualifier, name,
                            argTypes));
        }
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(null, name.getName(), loc);
        }
        catch (DuplicateSymbolException dse) {
            //This should be caught earlier, when the duplicate operation is
            //created
            throw new RuntimeException(dse);
        }

        return op;
    }

    /**
     * <p>Given the name of the type locate and return
     * the {@link SymbolTableEntry} stored in the
     * symbol table.</p>
     *
     * @param loc The location in the AST that we are
     *            currently visiting.
     * @param qualifier The qualifier of the type.
     * @param name The name of the type.
     * @param scope The module scope to start our search.
     *
     * @return A {@link SymbolTableEntry} from the
     *         symbol table.
     */
    public static SymbolTableEntry searchProgramType(Location loc,
            PosSymbol qualifier, PosSymbol name, ModuleScope scope) {
        SymbolTableEntry retEntry = null;

        List<SymbolTableEntry> entries =
                scope.query(new NameQuery(qualifier, name,
                        ImportStrategy.IMPORT_NAMED,
                        FacilityStrategy.FACILITY_INSTANTIATE, true));

        if (entries.size() == 0) {
            noSuchSymbol(qualifier, name.getName(), loc);
        }
        else if (entries.size() == 1) {
            retEntry = entries.get(0).toProgramTypeEntry(loc);
        }
        else {
            // When we have more than one, it means that we have a
            // type representation. In that case, we just need the
            // type representation.
            for (int i = 0; i < entries.size() && retEntry == null; i++) {
                SymbolTableEntry ste = entries.get(i);
                if (ste instanceof TypeRepresentationEntry) {
                    retEntry = ste.toTypeRepresentationEntry(loc);
                }
            }

            // Throw duplicate symbol error if we don't have a type
            // representation
            if (retEntry == null) {
                //This should be caught earlier, when the duplicate type is
                //created
                throw new RuntimeException();
            }
        }

        return retEntry;
    }

    /**
     * <p>An helper method that throws the appropriate raw type
     * not handled message.</p>
     *
     * @param ty A raw type.
     * @param loc Location where this raw type was found.
     */
    public static void tyNotHandled(Ty ty, Location loc) {
        throw new SourceErrorException("[VCGenerator] Ty not handled: "
                + ty.toString(), loc);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>An helper method that adds the operation's type constraints.</p>
     *
     * @param loc The location in the AST that we are
     *            currently visiting.
     * @param exp The top level assume expression we have built so far.
     * @param scope The module scope to start our search.
     * @param currentBlock The current {@link AssertiveCodeBlock} we are currently generating.
     * @param entries List of operation's parameter entries.
     * @param typeFamilyDecs List of abstract types we are implementing or extending.
     * @param localRepresentationTypeDecs List of local representation types.
     * @param processedInstFacDecs List of {@link InstantiatedFacilityDecl} we have processed
     *                             so far.
     *
     * @return The original {@code exp} plus any operation parameter's type constraints.
     */
    private static Exp addParamTypeConstraints(Location loc, Exp exp,
            ModuleScope scope, AssertiveCodeBlock currentBlock,
            ImmutableList<ProgramParameterEntry> entries,
            List<TypeFamilyDec> typeFamilyDecs,
            List<AbstractTypeRepresentationDec> localRepresentationTypeDecs,
            List<InstantiatedFacilityDecl> processedInstFacDecs) {
        Exp retExp = exp;

        // Loop through each of the parameters in the operation entry.
        for (ProgramParameterEntry entry : entries) {
            ParameterVarDec parameterVarDec =
                    (ParameterVarDec) entry.getDefiningElement();
            PTType declaredType = entry.getDeclaredType();
            ParameterMode parameterMode = entry.getParameterMode();

            // Only deal with actual types and don't deal
            // with entry types passed in to the concept realization
            if (!(declaredType instanceof PTGeneric)) {
                // Query for the type entry in the symbol table
                NameTy nameTy = (NameTy) parameterVarDec.getTy();
                SymbolTableEntry ste =
                        Utilities.searchProgramType(loc, nameTy.getQualifier(),
                                nameTy.getName(), scope);

                ProgramTypeEntry typeEntry;
                if (ste instanceof ProgramTypeEntry) {
                    typeEntry = ste.toProgramTypeEntry(nameTy.getLocation());
                }
                else {
                    typeEntry =
                            ste.toTypeRepresentationEntry(nameTy.getLocation())
                                    .getDefiningTypeEntry();
                }

                // Obtain the original dec from the AST
                TypeFamilyDec typeFamilyDec =
                        (TypeFamilyDec) typeEntry.getDefiningElement();

                // Other than the replaces mode, constraints for the
                // other parameter modes needs to be added
                // to the requires clause as conjuncts.
                if (parameterMode != ParameterMode.REPLACES) {
                    if (!VarExp.isLiteralTrue(typeFamilyDec.getConstraint()
                            .getAssertionExp())) {
                        AssertionClause constraintClause =
                                typeFamilyDec.getConstraint();
                        AssertionClause modifiedConstraintClause =
                                getTypeConstraintClause(constraintClause, loc,
                                        null, parameterVarDec.getName(),
                                        typeFamilyDec.getExemplar(), typeEntry
                                                .getModelType(), null);

                        // Replace any facility formal with actual
                        Exp constraintExp =
                                modifiedConstraintClause.getAssertionExp();
                        constraintExp =
                                Utilities
                                        .replaceFacilityFormalWithActual(
                                                constraintExp,
                                                Collections
                                                        .singletonList(parameterVarDec),
                                                scope.getDefiningElement()
                                                        .getName(),
                                                typeFamilyDecs,
                                                localRepresentationTypeDecs,
                                                processedInstFacDecs);

                        Exp whichEntailsExp =
                                modifiedConstraintClause.getWhichEntailsExp();
                        if (whichEntailsExp != null) {
                            whichEntailsExp =
                                    Utilities
                                            .replaceFacilityFormalWithActual(
                                                    whichEntailsExp,
                                                    Collections
                                                            .singletonList(parameterVarDec),
                                                    scope.getDefiningElement()
                                                            .getName(),
                                                    typeFamilyDecs,
                                                    localRepresentationTypeDecs,
                                                    processedInstFacDecs);
                        }

                        modifiedConstraintClause =
                                new AssertionClause(modifiedConstraintClause
                                        .getLocation().clone(),
                                        modifiedConstraintClause
                                                .getClauseType(),
                                        constraintExp, whichEntailsExp);

                        // Form a conjunct with the modified constraint clause and add
                        // the location detail associated with it.
                        Location constraintLoc =
                                modifiedConstraintClause.getAssertionExp()
                                        .getLocation();
                        retExp =
                                formConjunct(loc, retExp,
                                        modifiedConstraintClause,
                                        new LocationDetailModel(constraintLoc
                                                .clone(),
                                                constraintLoc.clone(),
                                                "Constraint Clause of "
                                                        + parameterVarDec
                                                                .getName()));
                    }
                }

                // TODO: Handle type representations from concept realizations
                /*
                // If the type is a type representation, then our requires clause
                // should really say something about the conceptual type and not
                // the variable
                if (ste instanceof RepresentationTypeEntry && !isLocal) {
                    requires =
                            Utilities.replace(requires, parameterExp,
                                    Utilities
                                            .createConcVarExp(opLocation,
                                                    parameterExp,
                                                    parameterExp
                                                            .getMathType(),
                                                    BOOLEAN));
                    requires.setLocation((Location) opLocation.clone());
                }

                // If the type is a type representation, then we need to add
                // all the type constraints from all the variable declarations
                // in the type representation.
                if (ste instanceof RepresentationTypeEntry) {
                    Exp repConstraintExp = null;
                    Set<VarExp> keys =
                            myRepresentationConstraintMap.keySet();
                    for (VarExp varExp : keys) {
                        if (varExp.getQualifier() == null
                                && varExp.getName().getName().equals(
                                pNameTy.getName().getName())) {
                            if (repConstraintExp == null) {
                                repConstraintExp =
                                        myRepresentationConstraintMap
                                                .get(varExp);
                            }
                            else {
                                Utilities.ambiguousTy(pNameTy, pNameTy
                                        .getLocation());
                            }
                        }
                    }

                    // Only do the following if the expression is not simply true
                    if (!repConstraintExp.isLiteralTrue()) {
                        // Replace the exemplar with the actual parameter variable expression
                        repConstraintExp =
                                Utilities.replace(repConstraintExp,
                                        exemplar, parameterExp);

                        // Add this to our requires clause
                        requires =
                                myTypeGraph.formConjunct(requires,
                                        repConstraintExp);
                        requires.setLocation((Location) opLocation.clone());
                    }
                }*/
            }

            // Add the current variable to our list of free variables
            currentBlock.addFreeVar(Utilities.createVarExp(parameterVarDec
                    .getLocation(), null, parameterVarDec.getName(),
                    declaredType.toMath(), null));

        }

        return retExp;
    }

}