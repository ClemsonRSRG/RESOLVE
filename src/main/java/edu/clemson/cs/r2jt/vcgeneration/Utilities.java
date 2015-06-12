/**
 * Utilities.java
 * ---------------------------------
 * Copyright (c) 2015
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
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTFamily;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate.query.*;
import edu.clemson.cs.r2jt.misc.SourceErrorException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public static void expNotHandled(Exp exp, Location l) {
        String message = "Exp not handled: " + exp.toString();
        throw new SourceErrorException(message, l);
    }

    public static void illegalOperationEnsures(Location l) {
        // TODO: Move this to sanity check.
        String message =
                "Ensures clauses of operations that return a value should be of the form <OperationName> = <value>";
        throw new SourceErrorException(message, l);
    }

    public static void notAType(SymbolTableEntry entry, Location l) {
        throw new SourceErrorException(entry.getSourceModuleIdentifier()
                .fullyQualifiedRepresentation(entry.getName())
                + " is not known to be a type.", l);
    }

    public static void notInFreeVarList(PosSymbol name, Location l) {
        String message =
                "State variable " + name + " not in free variable list";
        throw new SourceErrorException(message, l);
    }

    public static void noSuchModule(Location location) {
        throw new SourceErrorException(
                "Module does not exist or is not in scope.", location);
    }

    public static void noSuchSymbol(PosSymbol qualifier, String symbolName,
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

    public static void tyNotHandled(Ty ty, Location location) {
        String message = "Ty not handled: " + ty.toString();
        throw new SourceErrorException(message, location);
    }

    // -----------------------------------------------------------
    // VC Generator Utility Methods
    // -----------------------------------------------------------

    /**
     * <p>This method checks to see if this the expression we passed
     * is either a variable expression or a dotted expression that
     * contains a variable expression in the last position.</p>
     *
     * @param exp The checking expression.
     *
     * @return True if is an expression we can replace, false otherwise.
     */
    public static boolean containsReplaceableExp(Exp exp) {
        boolean retVal = false;

        // Case #1: VarExp
        if (exp instanceof VarExp) {
            retVal = true;
        }
        // Case #2: DotExp
        else if (exp instanceof DotExp) {
            DotExp dotExp = (DotExp) exp;
            List<Exp> dotExpList = dotExp.getSegments();
            retVal =
                    containsReplaceableExp(dotExpList
                            .get(dotExpList.size() - 1));
        }

        return retVal;
    }

    /**
     * <p>Converts the different types of <code>Exp</code> to the
     * ones used by the VC Generator.</p>
     *
     * @param oldExp The expression to be converted.
     * @param scope The module scope to start our search.
     *
     * @return An <code>Exp</code>.
     */
    public static Exp convertExp(Exp oldExp, ModuleScope scope) {
        Exp retExp;

        // Case #1: ProgramIntegerExp
        if (oldExp instanceof ProgramIntegerExp) {
            IntegerExp exp = new IntegerExp();
            exp.setValue(((ProgramIntegerExp) oldExp).getValue());

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
    public static DotExp createConcVarExp(Location location, VarExp name,
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
    public static DotExp createDotExp(Location location,
            edu.clemson.cs.r2jt.collections.List<Exp> dotExpList, MTType dotType) {
        // Create the DotExp
        DotExp exp = new DotExp(location, dotExpList, null);
        exp.setMathType(dotType);
        return exp;
    }

    /**
     * <p>Creates function expression "Dur_Call" with a specified number
     * of parameters</p>
     *
     * @param loc The location where we are creating this expression.
     * @param numArg Number of Arguments.
     * @param integerType Mathematical integer type.
     * @param realType Mathematical real type.
     *
     * @return The created <code>FunctionExp</code>.
     */
    public static FunctionExp createDurCallExp(Location loc, String numArg,
            MTType integerType, MTType realType) {
        // Obtain the necessary information from the variable
        VarExp param =
                createVarExp(loc, null, createPosSymbol(numArg), integerType,
                        null);

        // Create the list of arguments to the function
        edu.clemson.cs.r2jt.collections.List<Exp> params =
                new edu.clemson.cs.r2jt.collections.List<Exp>();
        params.add(param);

        // Create the duration call exp
        FunctionExp durCallExp =
                createFunctionExp(loc, null, createPosSymbol("Dur_Call"),
                        params, realType);

        return durCallExp;
    }

    /**
     * <p>Creates function expression "F_Dur" for a specified
     * variable.</p>
     *
     * @param var Local Variable.
     * @param realType Mathematical real type.
     *
     * @return The created <code>FunctionExp</code>.
     */
    public static FunctionExp createFinalizAnyDur(VarDec var, MTType realType) {
        // Obtain the necessary information from the variable
        Ty varTy = var.getTy();
        NameTy varNameTy = (NameTy) varTy;
        VarExp param =
                createVarExp(var.getLocation(), null, var.getName(), var
                        .getTy().getMathTypeValue(), null);
        VarExp param1 =
                createVarExp(varNameTy.getLocation(), null,
                        createPosSymbol(varNameTy.getName().getName()), var
                                .getTy().getMathTypeValue(), null);

        // Create the list of arguments to the function
        edu.clemson.cs.r2jt.collections.List<Exp> params =
                new edu.clemson.cs.r2jt.collections.List<Exp>();
        params.add(param1);
        params.add(param);

        // Create the final duration
        FunctionExp finalDurAnyExp =
                createFunctionExp(var.getLocation(), null,
                        createPosSymbol("F_Dur"), params, realType);

        return finalDurAnyExp;
    }

    /**
     * <p>Creates function expression "F_Dur" for a specified
     * variable expression.</p>
     *
     * @param varExp A Variable Expression.
     * @param realType Mathematical real type.
     * @param scope The module scope to start our search.
     *
     * @return The created <code>FunctionExp</code>.
     */
    public static FunctionExp createFinalizAnyDurExp(VariableExp varExp,
            MTType realType, ModuleScope scope) {
        if (varExp.getProgramType() instanceof PTFamily) {
            PTFamily type = (PTFamily) varExp.getProgramType();
            Exp param = convertExp(varExp, scope);
            VarExp param1 =
                    createVarExp(varExp.getLocation(), null,
                            createPosSymbol(type.getName()), varExp
                                    .getMathType(), varExp.getMathTypeValue());

            // Create the list of arguments to the function
            edu.clemson.cs.r2jt.collections.List<Exp> params =
                    new edu.clemson.cs.r2jt.collections.List<Exp>();
            params.add(param1);
            params.add(param);

            // Create the final duration
            FunctionExp finalDurAnyExp =
                    createFunctionExp(varExp.getLocation(), null,
                            createPosSymbol("F_Dur"), params, realType);

            return finalDurAnyExp;
        }
        else {
            throw new RuntimeException();
        }
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
    public static FunctionExp createFunctionExp(Location location,
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
     * <p>Creates function expression "I_Dur" for a specified
     * variable.</p>
     *
     * @param var Local Variable.
     * @param realType Mathematical real type.
     *
     * @return The created <code>FunctionExp</code>.
     */
    public static FunctionExp createInitAnyDur(VarDec var, MTType realType) {
        // Obtain the necessary information from the variable
        VarExp param =
                createVarExp(var.getLocation(), null,
                        createPosSymbol(((NameTy) var.getTy()).getName()
                                .getName()), var.getTy().getMathTypeValue(),
                        null);

        // Create the list of arguments to the function
        edu.clemson.cs.r2jt.collections.List<Exp> params =
                new edu.clemson.cs.r2jt.collections.List<Exp>();
        params.add(param);

        // Create the final duration
        FunctionExp initDurExp =
                createFunctionExp(var.getLocation(), null,
                        createPosSymbol("I_Dur"), params, realType);

        return initDurExp;
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
    public static DotExp createInitExp(VarDec var, MTType mType,
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
     * <p>Creates a less than equal infix expression.</p>
     *
     * @param location Location for the new infix expression.
     * @param left The left hand side of the less than equal expression.
     * @param right The right hand side of the less than equal expression.
     * @param booleanType Mathematical boolean type.
     *
     * @return The new <code>InfixExp</code>.
     */
    public static InfixExp createLessThanEqExp(Location location, Exp left,
            Exp right, MTType booleanType) {
        // Create the "Less Than Equal" InfixExp
        InfixExp exp =
                new InfixExp(location, left, Utilities.createPosSymbol("<="),
                        right);
        exp.setMathType(booleanType);
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
    public static InfixExp createLessThanExp(Location location, Exp left,
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
    public static PosSymbol createPosSymbol(String name) {
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
    public static VarExp createPValExp(Location location, ModuleScope scope) {
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
    public static VarExp createQuestionMarkVariable(Exp exp, VarExp oldVar) {
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
    public static VarExp createVarExp(Location loc, PosSymbol qualifier,
            PosSymbol name, MTType type, MTType typeValue) {
        // Create the VarExp
        VarExp exp = new VarExp(loc, qualifier, name);
        exp.setMathType(type);
        exp.setMathTypeValue(typeValue);
        return exp;
    }

    /**
     * <p>Gets the current "Cum_Dur" expression. We should only have one in
     * the current scope.</p>
     *
     * @param searchingExp The expression we are searching for "Cum_Dur"
     *
     * @return The current "Cum_Dur".
     */
    public static String getCumDur(Exp searchingExp) {
        String cumDur = "Cum_Dur";

        // Loop until we find one
        while (!searchingExp.containsVar(cumDur, false)) {
            cumDur = "?" + cumDur;
        }

        return cumDur;
    }

    /**
     * <p>Returns the math type for "Z".</p>
     *
     * @param location Current location in the AST.
     * @param scope The module scope to start our search.
     *
     *
     * @return The <code>MTType</code> for "Z".
     */
    public static MTType getMathTypeZ(Location location, ModuleScope scope) {
        // Locate "Z" (Integer)
        MathSymbolEntry mse = searchMathSymbol(location, "Z", scope);
        MTType Z = null;
        try {
            Z = mse.getTypeValue();
        }
        catch (SymbolNotOfKindTypeException e) {
            notAType(mse, location);
        }

        return Z;
    }

    public static Set<String> getSymbols(Exp exp) {
        // Return value
        Set<String> symbolsSet = new HashSet<String>();

        // Not CharExp, DoubleExp, IntegerExp or StringExp
        if (!(exp instanceof CharExp) && !(exp instanceof DoubleExp)
                && !(exp instanceof IntegerExp) && !(exp instanceof StringExp)) {
            // DotExp
            if (exp instanceof DotExp) {
                List<Exp> segExpList = ((DotExp) exp).getSegments();
                StringBuffer currentStr = new StringBuffer();

                // Iterate through each of the segment expressions
                for (Exp e : segExpList) {
                    // For each expression, obtain the set of symbols
                    // and form a candidate expression.
                    Set<String> retSet = getSymbols(e);
                    for (String s : retSet) {
                        if (currentStr.length() != 0) {
                            currentStr.append(".");
                        }
                        currentStr.append(s);
                    }
                    symbolsSet.add(currentStr.toString());
                }
            }
            // EqualsExp
            else if (exp instanceof EqualsExp) {
                symbolsSet.addAll(getSymbols(((EqualsExp) exp).getLeft()));
                symbolsSet.addAll(getSymbols(((EqualsExp) exp).getRight()));
            }
            // FunctionExp
            else if (exp instanceof FunctionExp) {
                FunctionExp funcExp = (FunctionExp) exp;
                StringBuffer funcName = new StringBuffer();

                // Add the name of the function (including any qualifiers)
                if (funcExp.getQualifier() != null) {
                    funcName.append(funcExp.getQualifier().getName());
                    funcName.append(".");
                }
                funcName.append(funcExp.getName());
                symbolsSet.add(funcName.toString());

                // Add all the symbols in the argument list
                List<FunctionArgList> funcArgList = funcExp.getParamList();
                for (FunctionArgList f : funcArgList) {
                    List<Exp> funcArgExpList = f.getArguments();
                    for (Exp e : funcArgExpList) {
                        symbolsSet.addAll(getSymbols(e));
                    }
                }
            }
            // If Exp
            else if (exp instanceof IfExp) {
                symbolsSet.addAll(getSymbols(((IfExp) exp).getTest()));
                symbolsSet.addAll(getSymbols(((IfExp) exp).getThenclause()));
                symbolsSet.addAll(getSymbols(((IfExp) exp).getElseclause()));
            }
            // InfixExp
            else if (exp instanceof InfixExp) {
                symbolsSet.addAll(getSymbols(((InfixExp) exp).getLeft()));
                symbolsSet.addAll(getSymbols(((InfixExp) exp).getRight()));
            }
            // LambdaExp
            else if (exp instanceof LambdaExp) {
                LambdaExp lambdaExp = (LambdaExp) exp;

                // Add all the parameter variables
                List<MathVarDec> paramList = lambdaExp.getParameters();
                for (MathVarDec v : paramList) {
                    symbolsSet.add(v.getName().getName());
                }

                // Add all the symbols in the body
                symbolsSet.addAll(getSymbols(lambdaExp.getBody()));
            }
            // OldExp
            else if (exp instanceof OldExp) {
                symbolsSet.add(exp.toString(0));
            }
            // OutfixExp
            else if (exp instanceof OutfixExp) {
                symbolsSet.addAll(getSymbols(((OutfixExp) exp).getArgument()));
            }
            // PrefixExp
            else if (exp instanceof PrefixExp) {
                symbolsSet.addAll(getSymbols(((PrefixExp) exp).getArgument()));
            }
            // SetExp
            else if (exp instanceof SetExp) {
                SetExp setExp = (SetExp) exp;

                // Add all the parts that form the set expression
                symbolsSet.add(setExp.getVar().getName().getName());
                symbolsSet.addAll(getSymbols(((SetExp) exp).getWhere()));
                symbolsSet.addAll(getSymbols(((SetExp) exp).getBody()));
            }
            // SuppositionExp
            else if (exp instanceof SuppositionExp) {
                SuppositionExp suppositionExp = (SuppositionExp) exp;

                // Add all the expressions
                symbolsSet.addAll(getSymbols(suppositionExp.getExp()));

                // Add all the variables
                List<MathVarDec> varList = suppositionExp.getVars();
                for (MathVarDec v : varList) {
                    symbolsSet.add(v.getName().getName());
                }
            }
            // TupleExp
            else if (exp instanceof TupleExp) {
                TupleExp tupleExp = (TupleExp) exp;

                // Add all the expressions in the fields
                List<Exp> fieldList = tupleExp.getFields();
                for (Exp e : fieldList) {
                    symbolsSet.addAll(getSymbols(e));
                }
            }
            // VarExp
            else if (exp instanceof VarExp) {
                VarExp varExp = (VarExp) exp;
                StringBuffer varName = new StringBuffer();

                // Add the name of the variable (including any qualifiers)
                if (varExp.getQualifier() != null) {
                    varName.append(varExp.getQualifier().getName());
                    varName.append(".");
                }
                varName.append(varExp.getName());
                symbolsSet.add(varName.toString());
            }
            // Not Handled!
            else {
                expNotHandled(exp, exp.getLocation());
            }
        }

        return symbolsSet;
    }

    /**
     * <p>Get the <code>PosSymbol</code> associated with the
     * <code>VariableExp</code> left.</p>
     *
     * @param left The variable expression.
     *
     * @return The <code>PosSymbol</code> of left.
     */
    public static PosSymbol getVarName(VariableExp left) {
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
    public static boolean isLocationOperation(String name, ModuleScope scope) {
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
    public static boolean isVerificationVar(Exp name) {
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
    public static Exp negateExp(Exp exp, MTType booleanType) {
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
    public static Exp replace(Exp exp, Exp old, Exp repl) {
        // Clone old and repl and use the Exp replace to do all its work
        Exp tmp = Exp.replace(Exp.copy(exp), Exp.copy(old), Exp.copy(repl));

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
    public static Exp replaceFacilityDeclarationVariables(Exp exp,
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
    public static Exp retrieveConstraint(Location location,
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
    public static MathSymbolEntry searchMathSymbol(Location loc, String name,
            ModuleScope scope) {
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
    public static OperationProfileEntry searchOperationProfile(Location loc,
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
    public static SymbolTableEntry searchProgramType(Location loc,
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
    public static void setLocation(Exp exp, Location loc) {
        // Special handling for InfixExp
        if (exp instanceof InfixExp) {
            ((InfixExp) exp).setAllLocations(loc);
        }
        else {
            exp.setLocation(loc);
        }
    }

    public static List<Exp> splitConjunctExp(Exp exp, List<Exp> expList) {
        // Attempt to split the expression if it contains a conjunct
        if (exp instanceof InfixExp) {
            InfixExp infixExp = (InfixExp) exp;

            // Split the expression if it is a conjunct
            if (infixExp.getOpName().equals("and")) {
                expList = splitConjunctExp(infixExp.getLeft(), expList);
                expList = splitConjunctExp(infixExp.getRight(), expList);
            }
            // Otherwise simply add it to our list
            else {
                expList.add(infixExp);
            }
        }
        // Otherwise it is an individual assume statement we need to deal with.
        else {
            expList.add(exp);
        }

        return expList;
    }
}
