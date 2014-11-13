/**
 * Utilities.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.vcgeneration;

/*
 * Libraries
 */
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.data.*;
import edu.clemson.cs.r2jt.typeandpopulate.*;
import edu.clemson.cs.r2jt.typeandpopulate.entry.*;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate.query.NameQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.OperationProfileQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.OperationQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.UnqualifiedNameQuery;
import edu.clemson.cs.r2jt.misc.SourceErrorException;

import java.util.List;

/**
 * TODO: Write a description of this module
 */
public class Utilities {

    // ===========================================================
    // Public Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Error Handling
    // -----------------------------------------------------------

    protected static void expNotHandled(Exp exp, Location l) {
        String message = "Exp not handled: " + exp.toString();
        throw new SourceErrorException(message, l);
    }

    protected static void illegalOperationEnsures(Location l) {
        // TODO: Move this to sanity check.
        String message =
                "Ensures clauses of operations that return a value should be of the form <OperationName> = <value>";
        throw new SourceErrorException(message, l);
    }

    protected static void notAType(SymbolTableEntry entry, Location l) {
        throw new SourceErrorException(entry.getSourceModuleIdentifier()
                .fullyQualifiedRepresentation(entry.getName())
                + " is not known to be a type.", l);
    }

    protected static void notInFreeVarList(PosSymbol name, Location l) {
        String message =
                "State variable " + name + " not in free variable list";
        throw new SourceErrorException(message, l);
    }

    protected static void noSuchModule(Location location) {
        throw new SourceErrorException(
                "Module does not exist or is not in scope.", location);
    }

    protected static void noSuchSymbol(PosSymbol qualifier, String symbolName,
            Location l) {

        String message;

        if (qualifier == null) {
            message = "No such symbol: " + symbolName;
        }
        else {
            message =
                    "No such symbol in module: " + qualifier.getName() + "."
                            + symbolName;
        }

        throw new SourceErrorException(message, l);
    }

    protected static void tyNotHandled(Ty ty, Location location) {
        String message = "Ty not handled: " + ty.toString();
        throw new SourceErrorException(message, location);
    }

    // -----------------------------------------------------------
    // VC Generator Utility Methods
    // -----------------------------------------------------------

    /**
     * <p>Converts the different types of <code>Exp</code> to the
     * ones used by the VC Generator.</p>
     *
     * @param oldExp The expression to be converted.
     *
     * @return An <code>Exp</code>.
     */
    protected static Exp convertExp(Exp oldExp) {
        Exp retExp;

        // Case #1: ProgramIntegerExp
        if (oldExp instanceof ProgramIntegerExp) {
            IntegerExp exp = new IntegerExp();
            exp.setValue(((ProgramIntegerExp) oldExp).getValue());
            exp.setMathType(oldExp.getMathType());
            retExp = exp;
        }
        // Case #2: ProgramCharacterExp
        else if (oldExp instanceof ProgramCharExp) {
            CharExp exp = new CharExp();
            exp.setValue(((ProgramCharExp) oldExp).getValue());
            exp.setMathType(oldExp.getMathType());
            retExp = exp;
        }
        // Case #3: ProgramStringExp
        else if (oldExp instanceof ProgramStringExp) {
            StringExp exp = new StringExp();
            exp.setValue(((ProgramStringExp) oldExp).getValue());
            exp.setMathType(oldExp.getMathType());
            retExp = exp;
        }
        // Case #4: VariableDotExp
        else if (oldExp instanceof VariableDotExp) {
            DotExp exp = new DotExp();
            List<VariableExp> segments =
                    ((VariableDotExp) oldExp).getSegments();
            edu.clemson.cs.r2jt.collections.List<Exp> newSegments =
                    new edu.clemson.cs.r2jt.collections.List<Exp>();

            // Need to replace each of the segments in a dot expression
            MTType lastMathType = null;
            MTType lastMathTypeValue = null;
            for (VariableExp v : segments) {
                VarExp varExp = new VarExp();

                // Can only be a VariableNameExp. Anything else
                // is a case we have not handled.
                if (v instanceof VariableNameExp) {
                    varExp.setName(((VariableNameExp) v).getName());
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
            exp.setSegments(newSegments);
            exp.setMathType(lastMathType);
            exp.setMathTypeValue(lastMathTypeValue);
            retExp = exp;
        }
        // Case #5: VariableNameExp
        else if (oldExp instanceof VariableNameExp) {
            VarExp exp = new VarExp();
            exp.setName(((VariableNameExp) oldExp).getName());
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
     * <p>Creates conceptual variable expression from the
     * given name.</p>
     *
     * @param location Location that wants to create
     *                 this conceptual variable expression.
     * @param name Name of the variable expression.
     * @param concType Mathematical type of the conceptual variable.
     * @param booleanType Mathematical boolean type.
     *
     * @return The created conceptual variable as a <code>DotExp</code>.
     */
    protected static DotExp createConcVarExp(Location location, VarExp name,
            MTType concType, MTType booleanType) {
        // Create a variable that refers to the conceptual exemplar
        VarExp cName =
                Utilities.createVarExp(null, null, Utilities
                        .createPosSymbol("Conc"), booleanType, null);

        // Create Conc.[Exemplar] dotted expression
        edu.clemson.cs.r2jt.collections.List<Exp> dotExpList =
                new edu.clemson.cs.r2jt.collections.List<Exp>();
        dotExpList.add(cName);
        dotExpList.add(name);
        DotExp conceptualVar =
                Utilities.createDotExp(location, dotExpList, concType);
        return conceptualVar;
    }

    /**
     * <p>Creates a variable expression with the name
     * "Cum_Dur" and has type "R".</p>
     *
     * @param location Location that wants to create
     *                 this variable.
     *
     * @return The created <code>VarExp</code>.
     */
    protected static VarExp createCumDurExp(Location location, ModuleScope scope) {
        // Locate "R" (Real Number)
        MathSymbolEntry mse = searchMathSymbol(location, "R", scope);
        try {
            // Create a variable with the name P_val
            return createVarExp(location, null, createPosSymbol("Cum_Dur"), mse
                    .getTypeValue(), null);
        }
        catch (SymbolNotOfKindTypeException e) {
            notAType(mse, location);
        }

        return null;
    }

    /**
     * <p>Creates dotted expression with the specified list of
     * expressions.</p>
     *
     * @param location Location that wants to create
     *                 this dotted expression.
     * @param dotExpList The list of expressions that form part of
     *                   the dotted expression.
     * @param dotType Mathematical type of the dotted expression.
     *
     * @return The created <code>DotExp</code>.
     */
    protected static DotExp createDotExp(Location location,
            edu.clemson.cs.r2jt.collections.List<Exp> dotExpList, MTType dotType) {
        // Create the DotExp
        DotExp exp = new DotExp(location, dotExpList, null);
        exp.setMathType(dotType);
        return exp;
    }

    /**
     * <p>Creates function expression "F_Dur" for a specified
     * variable.</p>
     *
     * @param var Local Variable.
     * @param booleanType Mathematical boolean type.
     *
     * @return The created <code>FunctionExp</code>.
     */
    protected static FunctionExp createFinalizAnyDur(VarDec var,
            MTType booleanType) {
        // Obtain the necessary information from the variable
        Ty varTy = var.getTy();
        NameTy varNameTy = (NameTy) varTy;
        VarExp param =
                createVarExp(var.getLocation(), null, var.getName(), varNameTy
                        .getMathType(), null);
        VarExp param1 =
                createVarExp(varNameTy.getLocation(), null,
                        createPosSymbol(varNameTy.getName().getName()),
                        varNameTy.getMathType(), null);

        // Create the list of arguments to the function
        edu.clemson.cs.r2jt.collections.List<Exp> params =
                new edu.clemson.cs.r2jt.collections.List<Exp>();
        params.add(param1);
        params.add(param);

        // Create the final duration
        FunctionExp finalDurAnyExp =
                createFunctionExp(var.getLocation(), null,
                        createPosSymbol("F_Dur"), params, booleanType);

        return finalDurAnyExp;
    }

    /**
     * <p>Creates function expression with the specified
     * name and arguments.</p>
     *
     * @param location Location that wants to create
     *                 this function expression.
     * @param qualifier Qualifier for the function expression.
     * @param name Name of the function expression.
     * @param argExpList List of arguments to the function expression.
     * @param funcType Mathematical type for the function expression.
     *
     * @return The created <code>FunctionExp</code>.
     */
    protected static FunctionExp createFunctionExp(Location location,
            PosSymbol qualifier, PosSymbol name,
            edu.clemson.cs.r2jt.collections.List<Exp> argExpList,
            MTType funcType) {
        // Complicated steps to construct the argument list
        // YS: No idea why it is so complicated!
        FunctionArgList argList = new FunctionArgList();
        argList.setArguments(argExpList);
        edu.clemson.cs.r2jt.collections.List<FunctionArgList> functionArgLists =
                new edu.clemson.cs.r2jt.collections.List<FunctionArgList>();
        functionArgLists.add(argList);

        // Create the function expression
        FunctionExp exp =
                new FunctionExp(location, qualifier, name, null,
                        functionArgLists);
        exp.setMathType(funcType);

        return exp;
    }

    /**
     * <p>Returns an <code>DotExp</code> with the <code>VarDec</code>
     * and its initialization ensures clause.</p>
     *
     * @param var The declared variable.
     * @param mType CLS type.
     * @param booleanType Mathematical boolean type.
     *
     * @return The new <code>DotExp</code>.
     */
    protected static DotExp createInitExp(VarDec var, MTType mType,
            MTType booleanType) {
        // Convert the declared variable into a VarExp
        VarExp varExp =
                createVarExp(var.getLocation(), null, var.getName(), var
                        .getTy().getMathTypeValue(), null);

        // Left hand side of the expression
        VarExp left = null;

        // NameTy
        if (var.getTy() instanceof NameTy) {
            NameTy ty = (NameTy) var.getTy();
            left =
                    createVarExp(ty.getLocation(), ty.getQualifier(), ty
                            .getName(), mType, null);
        }
        else {
            tyNotHandled(var.getTy(), var.getTy().getLocation());
        }

        // Create the "Is_Initial" FunctionExp
        edu.clemson.cs.r2jt.collections.List<Exp> expList =
                new edu.clemson.cs.r2jt.collections.List<Exp>();
        expList.add(varExp);
        FunctionExp right =
                createFunctionExp(var.getLocation(), null,
                        createPosSymbol("Is_Initial"), expList, booleanType);

        // Create the DotExp
        edu.clemson.cs.r2jt.collections.List<Exp> exps =
                new edu.clemson.cs.r2jt.collections.List<Exp>();
        exps.add(left);
        exps.add(right);
        DotExp exp = createDotExp(var.getLocation(), exps, booleanType);

        return exp;
    }

    /**
     * <p>Creates a less than infix expression.</p>
     *
     * @param location Location for the new infix expression.
     * @param left The left hand side of the less than expression.
     * @param right The right hand side of the less than expression.
     * @param booleanType Mathematical boolean type.
     *
     * @return The new <code>InfixExp</code>.
     */
    protected static InfixExp createLessThanExp(Location location, Exp left,
            Exp right, MTType booleanType) {
        // Create the "Less Than" InfixExp
        InfixExp exp =
                new InfixExp(location, left, Utilities.createPosSymbol("<"),
                        right);
        exp.setMathType(booleanType);
        return exp;
    }

    /**
     * <p>Returns a newly created <code>PosSymbol</code>
     * with the string provided.</p>
     *
     * @param name String of the new <code>PosSymbol</code>.
     *
     * @return The new <code>PosSymbol</code>.
     */
    protected static PosSymbol createPosSymbol(String name) {
        // Create the PosSymbol
        PosSymbol posSym = new PosSymbol();
        posSym.setSymbol(Symbol.symbol(name));
        return posSym;
    }

    /**
     * <p>Creates a variable expression with the name
     * "P_val" and has type "N".</p>
     *
     * @param location Location that wants to create
     *                 this variable.
     * @param scope The module scope to start our search.
     *
     *
     * @return The created <code>VarExp</code>.
     */
    protected static VarExp createPValExp(Location location, ModuleScope scope) {
        // Locate "N" (Natural Number)
        MathSymbolEntry mse = searchMathSymbol(location, "N", scope);
        try {
            // Create a variable with the name P_val
            return createVarExp(location, null, createPosSymbol("P_val"), mse
                    .getTypeValue(), null);
        }
        catch (SymbolNotOfKindTypeException e) {
            notAType(mse, location);
        }

        return null;
    }

    /**
     * <p>Create a question mark variable with the oldVar
     * passed in.</p>
     *
     * @param exp The full expression clause.
     * @param oldVar The old variable expression.
     *
     * @return A new variable with the question mark in <code>VarExp</code> form.
     */
    protected static VarExp createQuestionMarkVariable(Exp exp, VarExp oldVar) {
        // Add an extra question mark to the front of oldVar
        VarExp newOldVar =
                new VarExp(null, null, createPosSymbol("?"
                        + oldVar.getName().getName()));
        newOldVar.setMathType(oldVar.getMathType());
        newOldVar.setMathTypeValue(oldVar.getMathTypeValue());

        // Applies the question mark to oldVar if it is our first time visiting.
        if (exp.containsVar(oldVar.getName().getName(), false)) {
            return createQuestionMarkVariable(exp, newOldVar);
        }
        // Don't need to apply the question mark here.
        else if (exp.containsVar(newOldVar.getName().toString(), false)) {
            return createQuestionMarkVariable(exp, newOldVar);
        }
        else {
            // Return the new variable expression with the question mark
            if (oldVar.getName().getName().charAt(0) != '?') {
                return newOldVar;
            }
        }

        // Return our old self.
        return oldVar;
    }

    /**
     * <p>Returns a newly created <code>VarExp</code>
     * with the <code>PosSymbol</code> and math type provided.</p>
     *
     * @param loc Location of the new <code>VarExp</code>.
     * @param qualifier Qualifier of the <code>VarExp</code>.
     * @param name <code>PosSymbol</code> of the new <code>VarExp</code>.
     * @param type Math type of the new <code>VarExp</code>.
     * @param typeValue Math type value of the new <code>VarExp</code>.
     *
     * @return The new <code>VarExp</code>.
     */
    protected static VarExp createVarExp(Location loc, PosSymbol qualifier,
            PosSymbol name, MTType type, MTType typeValue) {
        // Create the VarExp
        VarExp exp = new VarExp(loc, qualifier, name);
        exp.setMathType(type);
        exp.setMathTypeValue(typeValue);
        return exp;
    }

    /**
     * <p>Get the <code>PosSymbol</code> associated with the
     * <code>VariableExp</code> left.</p>
     *
     * @param left The variable expression.
     *
     * @return The <code>PosSymbol</code> of left.
     */
    protected static PosSymbol getVarName(VariableExp left) {
        // Return value
        PosSymbol name;

        // Variable Name Expression
        if (left instanceof VariableNameExp) {
            name = ((VariableNameExp) left).getName();
        }
        // Variable Dot Expression
        else if (left instanceof VariableDotExp) {
            VariableRecordExp varRecExp =
                    (VariableRecordExp) ((VariableDotExp) left)
                            .getSemanticExp();
            name = varRecExp.getName();
        }
        // Variable Record Expression
        else if (left instanceof VariableRecordExp) {
            VariableRecordExp varRecExp = (VariableRecordExp) left;
            name = varRecExp.getName();
        }
        //
        // Creates an expression with "false" as its name
        else {
            name = createPosSymbol("false");
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
     * @return True if it is a local operation, false otherwise.
     */
    protected static boolean isLocationOperation(String name, ModuleScope scope) {
        boolean isIn;

        // Query for the corresponding operation
        List<SymbolTableEntry> entries =
                scope
                        .query(new NameQuery(
                                null,
                                name,
                                MathSymbolTable.ImportStrategy.IMPORT_NONE,
                                MathSymbolTable.FacilityStrategy.FACILITY_IGNORE,
                                true));

        // Not found
        if (entries.size() == 0) {
            isIn = false;
        }
        // Found one
        else if (entries.size() == 1) {
            // If the operation is declared here, then it will be an OperationEntry.
            // Thus it is a local operation.
            if (entries.get(0) instanceof OperationEntry) {
                isIn = true;
            }
            else {
                isIn = false;
            }
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
     * <p>Checks to see if the expression passed in is a
     * verification variable or not. A verification variable
     * is either "P_val" or starts with "?".</p>
     *
     * @param name Expression that we want to check
     *
     * @return True/False
     */
    protected static boolean isVerificationVar(Exp name) {
        // VarExp
        if (name instanceof VarExp) {
            String strName = ((VarExp) name).getName().getName();
            // Case #1: Question mark variables
            if (strName.charAt(0) == '?') {
                return true;
            }
            // Case #2: P_val
            else if (strName.equals("P_val")) {
                return true;
            }
        }
        // DotExp
        else if (name instanceof DotExp) {
            // Recursively call this method until we get
            // either true or false.
            List<Exp> names = ((DotExp) name).getSegments();
            return isVerificationVar(names.get(0));
        }

        // Definitely not a verification variable.
        return false;
    }

    /**
     * <p>Negate the incoming expression.</p>
     *
     * @param exp Expression to be negated.
     * @param booleanType Mathematical boolean type.
     *
     * @return Negated expression.
     */
    protected static Exp negateExp(Exp exp, MTType booleanType) {
        Exp retExp = Exp.copy(exp);
        if (exp instanceof EqualsExp) {
            if (((EqualsExp) exp).getOperator() == EqualsExp.EQUAL)
                ((EqualsExp) retExp).setOperator(EqualsExp.NOT_EQUAL);
            else
                ((EqualsExp) retExp).setOperator(EqualsExp.EQUAL);
        }
        else if (exp instanceof PrefixExp) {
            if (((PrefixExp) exp).getSymbol().getName().toString()
                    .equals("not")) {
                retExp = ((PrefixExp) exp).getArgument();
            }
        }
        else {
            PrefixExp tmp = new PrefixExp();
            setLocation(tmp, exp.getLocation());
            tmp.setArgument(exp);
            tmp.setSymbol(createPosSymbol("not"));
            tmp.setMathType(booleanType);
            retExp = tmp;
        }
        return retExp;
    }

    /**
     * <p>Copy and replace the old <code>Exp</code>.</p>
     *
     * @param exp The <code>Exp</code> to be replaced.
     * @param old The old sub-expression of <code>exp</code>.
     * @param repl The new sub-expression of <code>exp</code>.
     *
     * @return The new <code>Exp</code>.
     */
    protected static Exp replace(Exp exp, Exp old, Exp repl) {
        // Clone old and repl and use the Exp replace to do all its work
        Exp tmp = Exp.replace(exp, Exp.copy(old), Exp.copy(repl));

        // Return the corresponding Exp
        if (tmp != null)
            return tmp;
        else
            return exp;
    }

    /**
     * <p>Replace the formal with the actual variables
     * from the facility declaration rule.</p>
     *
     * @param exp The expression to be replaced.
     * @param facParam The list of facility declaration parameter variables.
     * @param concParam The list of concept parameter variables.
     *
     * @return The modified expression.
     */
    protected static Exp replaceFacilityDeclarationVariables(Exp exp,
            List facParam, List concParam) {
        for (int i = 0; i < facParam.size(); i++) {
            if (facParam.get(i) instanceof Dec
                    && (concParam.get(i) instanceof Dec)) {
                // Both are instances of Dec
                Dec facDec = (Dec) facParam.get(i);
                Dec concDec = (Dec) concParam.get(i);

                // Variable to be replaced
                VarExp expToReplace =
                        createVarExp(facDec.getLocation(), null, facDec
                                .getName(), facDec.getMathType(), null);

                // Concept variable
                VarExp expToUse =
                        createVarExp(concDec.getLocation(), null, concDec
                                .getName(), concDec.getMathType(), null);

                // Temporary replacement to avoid formal and actuals being the same
                exp = replace(exp, expToReplace, expToUse);

                // Create a old exp from expToReplace
                OldExp r = new OldExp(null, expToReplace);
                r.setMathType(expToReplace.getMathType());

                // Create a old exp from expToUse
                OldExp u = new OldExp(null, expToUse);
                u.setMathType(expToUse.getMathType());

                // Actually perform the desired replacement
                exp = replace(exp, r, u);
            }
            else if (facParam.get(i) instanceof Dec
                    && concParam.get(i) instanceof ModuleArgumentItem) {
                // We have a ModuleArgumentItem
                Dec facDec = (Dec) facParam.get(i);
                ModuleArgumentItem concItem =
                        (ModuleArgumentItem) concParam.get(i);

                // Variable to be replaced
                VarExp expToReplace =
                        createVarExp(facDec.getLocation(), null, facDec
                                .getName(), facDec.getMathType(), null);

                // Concept variable
                VarExp expToUse = new VarExp();
                if (concItem.getName() != null) {
                    expToUse.setName(concItem.getName());
                }
                else {
                    expToUse.setName(createPosSymbol(concItem.getEvalExp()
                            .toString()));
                }

                // Set the math type for the concept variable
                if (concItem.getProgramTypeValue() != null) {
                    expToUse.setMathType(concItem.getProgramTypeValue()
                            .toMath());
                }
                else {
                    expToUse.setMathType(concItem.getMathType());
                }

                // Temporary replacement to avoid formal and actuals being the same
                exp = replace(exp, expToReplace, expToUse);

                // Create a old exp from expToReplace
                OldExp r = new OldExp(null, expToReplace);
                r.setMathType(expToReplace.getMathType());

                // Create a old exp from expToUse
                OldExp u = new OldExp(null, expToUse);
                u.setMathType(expToUse.getMathType());

                // Actually perform the desired replacement
                exp = replace(exp, r, u);
            }
        }

        return exp;
    }

    /**
     * <p>Given a programming type, locate its constraint from the
     * Symbol Table.</p>
     *
     * @param location Location for the searching type.
     * @param qualifier Qualifier for the programming type.
     * @param name Name for the programming type.
     * @param varName Name of the variable of this type.
     * @param scope The module scope to start our search.
     *
     * @return The constraint in <code>Exp</code> form if found, null otherwise.
     */
    protected static Exp retrieveConstraint(Location location,
            PosSymbol qualifier, PosSymbol name, Exp varName, ModuleScope scope) {
        Exp constraint = null;

        // Query for the type entry in the symbol table
        SymbolTableEntry ste =
                Utilities.searchProgramType(location, qualifier, name, scope);

        ProgramTypeEntry typeEntry;
        if (ste instanceof ProgramTypeEntry) {
            typeEntry = ste.toProgramTypeEntry(location);
        }
        else {
            typeEntry =
                    ste.toRepresentationTypeEntry(location)
                            .getDefiningTypeEntry();
        }

        // Make sure we don't have a generic type
        if (typeEntry.getDefiningElement() instanceof TypeDec) {
            // Obtain the original dec from the AST
            TypeDec type = (TypeDec) typeEntry.getDefiningElement();

            // Create a variable expression from the type exemplar
            VarExp exemplar =
                    Utilities.createVarExp(type.getLocation(), null, type
                            .getExemplar(), typeEntry.getModelType(), null);

            constraint =
                    replace(Exp.copy(type.getConstraint()), exemplar, varName);
        }
        else {
            notAType(typeEntry, location);
        }

        return constraint;
    }

    /**
     * <p>Given a math symbol name, locate and return
     * the <code>MathSymbolEntry</code> stored in the
     * symbol table.</p>
     *
     * @param loc The location in the AST that we are
     *            currently visiting.
     * @param name The string name of the math symbol.
     * @param scope The module scope to start our search.
     *
     * @return An <code>MathSymbolEntry</code> from the
     *         symbol table.
     */
    protected static MathSymbolEntry searchMathSymbol(Location loc,
            String name, ModuleScope scope) {
        // Query for the corresponding math symbol
        MathSymbolEntry ms = null;
        try {
            ms =
                    scope
                            .queryForOne(
                                    new UnqualifiedNameQuery(
                                            name,
                                            MathSymbolTable.ImportStrategy.IMPORT_RECURSIVE,
                                            MathSymbolTable.FacilityStrategy.FACILITY_IGNORE,
                                            true, true)).toMathSymbolEntry(loc);
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
     * types, locate and return the <code>OperationEntry</code>
     * stored in the symbol table.</p>
     *
     * @param loc The location in the AST that we are
     *            currently visiting.
     * @param qualifier The qualifier of the operation.
     * @param name The name of the operation.
     * @param argTypes The list of argument types.
     * @param scope The module scope to start our search.
     *
     * @return An <code>OperationEntry</code> from the
     *         symbol table.
     */
    protected static OperationEntry searchOperation(Location loc,
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
     * <p>Given the qualifier, name and the list of argument
     * types, locate and return the <code>OperationProfileEntry</code>
     * stored in the symbol table.</p>
     *
     * @param loc The location in the AST that we are
     *            currently visiting.
     * @param qualifier The qualifier of the operation.
     * @param name The name of the operation.
     * @param argTypes The list of argument types.
     * @param scope The module scope to start our search.
     *
     * @return An <code>OperationProfileEntry</code> from the
     *         symbol table.
     */
    protected static OperationProfileEntry searchOperationProfile(Location loc,
            PosSymbol qualifier, PosSymbol name, List<PTType> argTypes,
            ModuleScope scope) {
        // Query for the corresponding operation profile
        OperationProfileEntry ope = null;
        try {
            ope =
                    scope.queryForOne(new OperationProfileQuery(qualifier,
                            name, argTypes));
        }
        catch (NoSuchSymbolException nsse) {
            noSuchModule(loc);
        }
        catch (DuplicateSymbolException dse) {
            // This should have been caught earlier, when the duplicate operation is
            // created.
            throw new RuntimeException(dse);
        }

        return ope;
    }

    /**
     * <p>Given the name of the type locate and return
     * the <code>SymbolTableEntry</code> stored in the
     * symbol table.</p>
     *
     * @param loc The location in the AST that we are
     *            currently visiting.
     * @param qualifier The qualifier of the type.
     * @param name The name of the type.
     * @param scope The module scope to start our search.
     *
     * @return A <code>SymbolTableEntry</code> from the
     *         symbol table.
     */
    protected static SymbolTableEntry searchProgramType(Location loc,
            PosSymbol qualifier, PosSymbol name, ModuleScope scope) {
        SymbolTableEntry retEntry = null;

        List<SymbolTableEntry> entries =
                scope.query(new NameQuery(qualifier, name,
                        MathSymbolTable.ImportStrategy.IMPORT_NAMED,
                        MathSymbolTable.FacilityStrategy.FACILITY_INSTANTIATE,
                        false));

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
                if (ste instanceof RepresentationTypeEntry) {
                    retEntry = ste.toRepresentationTypeEntry(loc);
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
     * <p>Changes the <code>Exp</code> with the new
     * <code>Location</code>.</p>
     *
     * @param exp The <code>Exp</code> that needs to be modified.
     * @param loc The new <code>Location</code>.
     */
    protected static void setLocation(Exp exp, Location loc) {
        // Special handling for InfixExp
        if (exp instanceof InfixExp) {
            ((InfixExp) exp).setAllLocations(loc);
        }
        else {
            exp.setLocation(loc);
        }
    }
}
