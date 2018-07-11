/*
 * Utilities.java
 * ---------------------------------
 * Copyright (c) 2018
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.vcgeneration.utilities;

import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.cs.rsrg.absyn.declarations.mathdecl.MathDefVariableDec;
import edu.clemson.cs.rsrg.absyn.declarations.sharedstatedecl.SharedStateDec;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.AbstractTypeRepresentationDec;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.TypeRepresentationDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.MathVarDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.EqualsExp.Operator;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.*;
import edu.clemson.cs.rsrg.absyn.items.programitems.EnhancementSpecRealizItem;
import edu.clemson.cs.rsrg.absyn.rawtypes.NameTy;
import edu.clemson.cs.rsrg.absyn.rawtypes.Ty;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.LocationDetailModel;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.entry.*;
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
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
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
     * <p>This method to check if an equivalent expression is in the
     * specified collection.</p>
     *
     * <p><em>Note:</em> We can't use {@link Collection#contains(Object)} because it will
     * use the strict {@link Exp#equals(Object)} method rather than {@link Exp#equivalent(Exp)}.</p>
     *
     * @param collection Collection of expressions.
     * @param exp Expression to check.
     *
     * @return {@code true} if an equivalent {@code exp} is in the collection,
     * {@code false} otherwise.
     */
    public static boolean containsEquivalentExp(Collection<Exp> collection,
            Exp exp) {
        boolean found = false;

        Iterator<Exp> expIterator = collection.iterator();
        while (expIterator.hasNext() && !found) {
            found = expIterator.next().equivalent(exp);
        }

        return found;
    }

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
     * <p>This method returns the conceptual version of the {@link VarDec}.</p>
     *
     * @param varDec The parameter variable.
     * @param varDecType Mathematical type for the parameter variable.
     * @param booleanType Mathematical boolean type.
     *
     * @return The new {@link DotExp}.
     */
    public static DotExp createConcVarExp(VarDec varDec, MTType varDecType, MTType booleanType) {
        // Convert the declared variable into a VarExp
        VarExp varExp =
                Utilities.createVarExp(varDec.getLocation(), null, varDec.getName(),
                        varDecType, null);

        // Create a VarExp with the name "Conc"
        VarExp concVarExp =
                Utilities.createVarExp(varDec.getLocation(), null,
                        new PosSymbol(varDec.getLocation(), "Conc"),
                        booleanType, null);

        // Create the DotExp
        List<Exp> segments = new ArrayList<>();
        segments.add(concVarExp);
        segments.add(varExp);

        DotExp retExp = new DotExp(varDec.getLocation(), segments);
        retExp.setMathType(varDecType);

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

        DotExp retExp = new DotExp(varDec.getLocation(), segments);
        retExp.setMathType(booleanType);

        return retExp;
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
            // No need to form a conjunct if it is simply "true"
            if (!VarExp.isLiteralTrue(assertionExp)) {
                retExp = InfixExp.formConjunct(loc, exp, assertionExp);
            }
            else {
                retExp = exp;
            }
        }

        // Add any which_entails
        if (clause.getWhichEntailsExp() != null) {
            Exp whichEntailsExp = clause.getWhichEntailsExp().clone();
            Location entailsLoc = clause.getWhichEntailsExp().getLocation();
            whichEntailsExp.setLocationDetailModel(new LocationDetailModel(
                    entailsLoc.clone(), entailsLoc.clone(),
                    "Which_Entails Expression Located at "
                            + clause.getLocation()));

            // No need to form a conjunct if it is simply "true"
            if (!VarExp.isLiteralTrue(whichEntailsExp)) {
                retExp = InfixExp.formConjunct(loc, retExp, whichEntailsExp);
            }
        }

        return retExp;
    }

    /**
     * <p>An helper method for locating the associated {@code Type Family} from
     * a {@link TypeRepresentationDec}.</p>
     *
     * @param dec An {@link TypeRepresentationDec}.
     * @param context The current {@link VerificationContext}.
     *
     * @return A {@link TypeFamilyDec}.
     */
    public static TypeFamilyDec getAssociatedTypeFamilyDec(
            TypeRepresentationDec dec, VerificationContext context) {
        // Obtain the type family we are implementing
        TypeFamilyDec typeFamilyDec = null;
        Iterator<TypeFamilyDec> conceptTypeIt =
                context.getConceptDeclaredTypes().iterator();
        while (conceptTypeIt.hasNext() && typeFamilyDec == null) {
            TypeFamilyDec nextDec = conceptTypeIt.next();
            if (nextDec.getName().equals(dec.getName())) {
                typeFamilyDec = nextDec;
            }
        }

        // Make sure we found one.
        if (typeFamilyDec == null) {
            // Shouldn't be possible but just in case it ever happens
            // by accident.
            Utilities.noSuchSymbol(null, dec.getName().getName(), dec
                    .getLocation());
        }

        return typeFamilyDec;
    }

    /**
     * <p>An helper method for locating a facility qualifier (if any) from
     * a raw program type.</p>
     *
     * @param ty A raw program type.
     * @param context The current {@link VerificationContext}.
     *
     * @return A facility qualifier if the program type came from a facility
     * instantiation, {@code null} otherwise.
     */
    public static PosSymbol getFacilityQualifier(NameTy ty,
            VerificationContext context) {
        PosSymbol facQualifier = ty.getQualifier();

        // Check to see if there is a facility that instantiated a type
        // that matches "ty".
        if (facQualifier == null) {
            Iterator<InstantiatedFacilityDecl> it =
                    context.getProcessedInstFacilityDecls().iterator();
            while (it.hasNext() && facQualifier == null) {
                InstantiatedFacilityDecl decl = it.next();

                // One we find one, we are done!
                for (TypeFamilyDec dec : decl.getConceptDeclaredTypes()) {
                    if (dec.getName().getName().equals(ty.getName().getName())) {
                        facQualifier = decl.getInstantiatedFacilityName();
                    }
                }
            }
        }

        return facQualifier;
    }

    /**
     * <p>An helper method for locating the instantiating facility (if any) from
     * an operation entry.</p>
     *
     * @param operationEntry An {@link OperationEntry}.
     * @param context The current {@link VerificationContext}.
     *
     * @return A {@link InstantiatedFacilityDecl} if {@code operationEntry} is from
     * a module that has been instantiated, {@code null} otherwise.
     */
    public static InstantiatedFacilityDecl getInstantiatingFacility(
            OperationEntry operationEntry, VerificationContext context) {
        InstantiatedFacilityDecl instantiatedFacilityDecl = null;
        ModuleIdentifier sourceModuleIdentifier =
                operationEntry.getSourceModuleIdentifier();

        // Loop and attempt to find the one that instantiated this
        // operation entry.
        Iterator<InstantiatedFacilityDecl> it =
                context.getProcessedInstFacilityDecls().iterator();
        while (it.hasNext() && instantiatedFacilityDecl == null) {
            InstantiatedFacilityDecl decl = it.next();
            FacilityDec facilityDec = decl.getInstantiatedFacilityDec();

            // YS: There are two potential scenarios: either it is
            // an operation declared in a concept or an operation from
            // an enhancement. Can't be private operations from
            // concept realizations or enhancement realizations.
            if (sourceModuleIdentifier.equals(new ModuleIdentifier(facilityDec
                    .getConceptName().getName()))) {
                instantiatedFacilityDecl = decl;
            }
            else {
                Iterator<EnhancementSpecRealizItem> specRealizItemIt =
                        facilityDec.getEnhancementRealizPairs().iterator();
                while (specRealizItemIt.hasNext()
                        && instantiatedFacilityDecl == null) {
                    EnhancementSpecRealizItem item = specRealizItemIt.next();
                    if (sourceModuleIdentifier.equals(new ModuleIdentifier(item
                            .getEnhancementName().getName()))) {
                        instantiatedFacilityDecl = decl;
                    }
                }
            }
        }

        return instantiatedFacilityDecl;
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
                functionExp.getQualifier(), functionExp.getName(), argTypes,
                ImportStrategy.IMPORT_NAMED, FacilityStrategy.FACILITY_INSTANTIATE, scope);
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
     * <p>Given the original {@code convention} clause, use the provided information
     * on the actual parameter variable to substitute the {@code exemplar} in the
     * {@code convention} clause and create a new {@link AssertionClause}.</p>
     *
     * @param originalConventionClause The {@link AssertionClause} containing the
     *                                 original {@code convention} clause.
     * @param loc The location in the AST that we are
     *            currently visiting.
     * @param name The parameter variable's name.
     * @param exemplarName The {@code exemplar} name for the corresponding type.
     * @param type The mathematical type associated with this type.
     * @param typeValue The mathematical type value associated with this type.
     *
     * @return A modified {@link AssertionClause} containing the new
     * {@code convention} clause.
     */
    public static AssertionClause getTypeConventionClause(AssertionClause originalConventionClause,
            Location loc, PosSymbol name, PosSymbol exemplarName, MTType type, MTType typeValue) {
        // Create a variable expression from the declared variable
        VarExp varDecExp = Utilities.createVarExp(loc, null, name, type, typeValue);

        // Create a variable expression from the type exemplar
        VarExp exemplar = Utilities.createVarExp(loc, null, exemplarName, type, typeValue);

        // Create a replacement map
        Map<Exp, Exp> substitutions = new HashMap<>();
        substitutions.put(exemplar, varDecExp);

        // Create new assertion clause by replacing the exemplar with the actual
        Location newLoc = loc.clone();
        Exp conventionWithReplacements =
                originalConventionClause.getAssertionExp().substitute(substitutions);
        Exp whichEntailsWithReplacements = null;
        if (originalConventionClause.getWhichEntailsExp() != null) {
            whichEntailsWithReplacements =
                    originalConventionClause.getWhichEntailsExp().substitute(substitutions);
        }

        return new AssertionClause(newLoc, AssertionClause.ClauseType.CONVENTION,
                conventionWithReplacements, whichEntailsWithReplacements);
    }

    /**
     * <p>Given the original {@code correspondence} clause, use the provided information
     * on the actual parameter variable to substitute the {@code exemplar} in the
     * {@code correspondence} clause and create a new {@link AssertionClause}.</p>
     *
     * @param originalCorrespondenceClause The {@link AssertionClause} containing the
     *                                     original {@code correspondence} clause.
     * @param loc The location in the AST that we are
     *            currently visiting.
     * @param name The parameter variable's name.
     * @param ty The parameter's raw type.
     * @param exemplarName The {@code exemplar} name for the corresponding type.
     * @param exemplarTy The {@code exemplar}'s raw type.
     * @param type The mathematical type associated with this type.
     * @param typeValue The mathematical type value associated with this type.
     * @param booleanType Mathematical boolean type.
     *
     * @return A modified {@link AssertionClause} containing the new
     * {@code correspondence} clause.
     */
    public static AssertionClause getTypeCorrespondenceClause(AssertionClause originalCorrespondenceClause,
            Location loc, PosSymbol name, Ty ty, PosSymbol exemplarName, Ty exemplarTy,
            MTType type, MTType typeValue, MTType booleanType) {
        // Create a variable expression from the declared variable
        VarExp varDecExp = Utilities.createVarExp(loc, null, name, type, typeValue);

        // Create a conceptual variable expression from the declared variable
        DotExp concVarDecExp = Utilities.createConcVarExp(new VarDec(name, ty), type, booleanType);

        // Create a variable expression from the type exemplar
        VarExp exemplar = Utilities.createVarExp(loc, null, exemplarName, type, typeValue);

        // Create a conceptual variable expression from the type exemplar
        DotExp concExemplarExp = Utilities.createConcVarExp(new VarDec(exemplarName, exemplarTy), type, booleanType);

        // Create a replacement map
        Map<Exp, Exp> substitutions = new HashMap<>();
        substitutions.put(exemplar, varDecExp);
        substitutions.put(concExemplarExp, concVarDecExp);

        // Create new assertion clause by replacing the exemplar with the actual
        Location newLoc = loc.clone();
        Exp correspondenceWithReplacements =
                originalCorrespondenceClause.getAssertionExp().substitute(substitutions);
        Exp whichEntailsWithReplacements = null;
        if (originalCorrespondenceClause.getWhichEntailsExp() != null) {
            whichEntailsWithReplacements =
                    originalCorrespondenceClause.getWhichEntailsExp().substitute(substitutions);
        }

        return new AssertionClause(newLoc, AssertionClause.ClauseType.CORRESPONDENCE,
                correspondenceWithReplacements, whichEntailsWithReplacements);
    }

    /**
     * <p>Given the original {@code finalization ensures} clause, use the provided
     * information on a program variable to substitute the {@code #exemplar} in
     * the {@code finalization ensures} clause and create a new {@link AssertionClause}.</p>
     *
     * @param originalFinalEnsuresClause The {@link AssertionClause} containing the
     *                                   original {@code finalization ensures} clause.
     * @param loc The location in the AST that we are
     *            currently visiting.
     * @param qualifier The program variable's qualifier.
     * @param name The program variable's name.
     * @param exemplarName The {@code exemplar} name for the corresponding type.
     * @param type The mathematical type associated with this type.
     * @param typeValue The mathematical type value associated with this type.
     *
     * @return A modified {@link AssertionClause} containing the new
     * {@code finalization ensures} clause.
     */
    public static AssertionClause getTypeFinalEnsuresClause(AssertionClause originalFinalEnsuresClause, Location loc,
        PosSymbol qualifier, PosSymbol name, PosSymbol exemplarName, MTType type, MTType typeValue) {
        // Create an incoming variable expression from the declared variable
        VarExp varDecExp = Utilities.createVarExp(loc, qualifier, name, type, typeValue);

        // Create a variable expression from the type exemplar
        // YS: Finalization ensures always talks about the incoming exemplar value and never
        //     about the outgoing exemplar value since it is finalized...
        VarExp exemplar = Utilities.createVarExp(loc, null, exemplarName, type, typeValue);
        OldExp oldExemplarExp = new OldExp(loc.clone(), exemplar);

        // Create a replacement map
        Map<Exp, Exp> substitutions = new HashMap<>();
        substitutions.put(oldExemplarExp, varDecExp);

        // Create new assertion clause by replacing the exemplar with the actual
        Location newLoc = loc.clone();
        Exp finalEnsuresWithReplacements =
                originalFinalEnsuresClause.getAssertionExp().substitute(substitutions);
        Exp whichEntailsWithReplacements = null;
        if (originalFinalEnsuresClause.getWhichEntailsExp() != null) {
            whichEntailsWithReplacements =
                    originalFinalEnsuresClause.getWhichEntailsExp().substitute(substitutions);
        }

        return new AssertionClause(newLoc, AssertionClause.ClauseType.ENSURES,
                finalEnsuresWithReplacements, whichEntailsWithReplacements);
    }

    /**
     * <p>Given the original {@code initialization ensures} clause, use the provided
     * information on a program variable to substitute the {@code exemplar} in
     * the {@code initialization ensures} clause and create a new {@link AssertionClause}.</p>
     *
     * @param originalInitEnsuresClause The {@link AssertionClause} containing the
     *                                  original {@code initialization ensures} clause.
     * @param loc The location in the AST that we are
     *            currently visiting.
     * @param qualifier The program variable's qualifier.
     * @param name The program variable's name.
     * @param exemplarName The {@code exemplar} name for the corresponding type.
     * @param type The mathematical type associated with this type.
     * @param typeValue The mathematical type value associated with this type.
     *
     * @return A modified {@link AssertionClause} containing the new
     * {@code initialization ensures} clause.
     */
    public static AssertionClause getTypeInitEnsuresClause(AssertionClause originalInitEnsuresClause, Location loc,
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
        Exp initEnsuresWithReplacements =
                originalInitEnsuresClause.getAssertionExp().substitute(substitutions);
        Exp whichEntailsWithReplacements = null;
        if (originalInitEnsuresClause.getWhichEntailsExp() != null) {
            whichEntailsWithReplacements =
                    originalInitEnsuresClause.getWhichEntailsExp().substitute(substitutions);
        }

        return new AssertionClause(newLoc, AssertionClause.ClauseType.ENSURES,
                initEnsuresWithReplacements, whichEntailsWithReplacements);
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
     * @param context The current {@link VerificationContext}.
     *
     * @return The modified {@link Exp}.
     */
    public static Exp replaceFacilityFormalWithActual(Exp clauseExp,
            List<ParameterVarDec> paramList, PosSymbol currentModuleName,
            VerificationContext context) {
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
                    Iterator<TypeFamilyDec> it =
                            context.getConceptDeclaredTypes().iterator();
                    while (it.hasNext() && isInstantiatedType) {
                        // If the name matches, then it must be a concept abstract type
                        if (decTyAsNameTy.getName().getName().equals(
                                it.next().getName().getName())) {
                            isInstantiatedType = false;
                        }
                    }

                    // Check all representation types.
                    Iterator<AbstractTypeRepresentationDec> it2 =
                            context.getLocalTypeRepresentationDecs().iterator();
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
                            context.getProcessedInstFacilityDecls().iterator();
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

                        // Replace all concept's shared variables by adding
                        // the facility qualifier.
                        PosSymbol facName =
                                instantiatedFacilityDecl.getInstantiatedFacilityName();
                        List<SharedStateDec> sharedStateDecs =
                                instantiatedFacilityDecl.getConceptSharedStates();
                        for (SharedStateDec sharedStateDec : sharedStateDecs) {
                            List<VarExp> sharedVarsAsVarExps = new ArrayList<>();
                            List<Exp> qualifiedSharedVars = new ArrayList<>();
                            for (MathVarDec mathVarDec : sharedStateDec.getAbstractStateVars()) {
                                // Convert to VarExp
                                sharedVarsAsVarExps.add(Utilities.createVarExp(mathVarDec.getLocation().clone(),
                                        null, mathVarDec.getName().clone(),
                                        mathVarDec.getMathType(), null));

                                // Convert to VarExp with the facility qualifier name.
                                qualifiedSharedVars.add(Utilities.createVarExp(mathVarDec.getLocation().clone(),
                                        facName.clone(), mathVarDec.getName().clone(),
                                        mathVarDec.getMathType(), null));
                            }

                            // Replace in clause
                            modifiedClauseExp =
                                    replaceFormalWithActual(modifiedClauseExp,
                                            sharedVarsAsVarExps, qualifiedSharedVars);
                        }

                        // Replace all concept's definition variables by adding
                        // the facility qualifier.
                        List<TypeFamilyDec> typeFamilyDecs =
                                instantiatedFacilityDecl.getConceptDeclaredTypes();
                        for (TypeFamilyDec typeFamilyDec : typeFamilyDecs) {
                            List<VarExp> defVarsExp = new ArrayList<>();
                            List<Exp> qualifiedDefVars = new ArrayList<>();
                            for (MathDefVariableDec mathDefVariableDec : typeFamilyDec.getDefinitionVarList()) {
                                MathVarDec mathVarDec = mathDefVariableDec.getVariable();

                                // Convert to VarExp
                                defVarsExp.add(Utilities.createVarExp(mathVarDec.getLocation().clone(),
                                        null, mathVarDec.getName().clone(),
                                        mathVarDec.getMathType(), null));

                                // Convert to VarExp with the facility qualifier name.
                                qualifiedDefVars.add(Utilities.createVarExp(mathVarDec.getLocation().clone(),
                                        facName.clone(), mathVarDec.getName().clone(),
                                        mathVarDec.getMathType(), null));
                            }

                            // Replace in clause
                            modifiedClauseExp =
                                    replaceFormalWithActual(modifiedClauseExp,
                                            defVarsExp, qualifiedDefVars);
                        }

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
     * @param importStrategy The import strategy to use.
     * @param facilityStrategy The facility strategy to use.
     * @param scope The module scope to start our search.
     *
     * @return An {@link OperationEntry} from the
     *         symbol table.
     */
    public static OperationEntry searchOperation(Location loc,
            PosSymbol qualifier, PosSymbol name, List<PTType> argTypes,
            ImportStrategy importStrategy, FacilityStrategy facilityStrategy,
            ModuleScope scope) {
        // Query for the corresponding operation
        OperationEntry op = null;
        try {
            op =
                    scope.queryForOne(new OperationQuery(qualifier, name,
                            argTypes, importStrategy, facilityStrategy));
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
            // Return the appropriate program type
            SymbolTableEntry ste = entries.get(0);
            if (ste instanceof FacilityTypeRepresentationEntry) {
                retEntry = ste.toFacilityTypeRepresentationEntry(loc);
            }
            else if (ste instanceof TypeRepresentationEntry) {
                retEntry = ste.toTypeRepresentationEntry(loc);
            }
            else {
                retEntry = ste.toProgramTypeEntry(loc);
            }
        }
        else {
            //This should be caught earlier, when the duplicate type is
            //created
            throw new RuntimeException();
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

}