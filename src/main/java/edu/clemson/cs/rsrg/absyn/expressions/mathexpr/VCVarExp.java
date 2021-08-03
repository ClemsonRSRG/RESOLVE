/*
 * VCVarExp.java
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
package edu.clemson.cs.rsrg.absyn.expressions.mathexpr;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.vcgeneration.VCGenerator;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * This is the class serves as a wrapper for an {@link MathExp}. When the
 * {@link VCGenerator}
 * creates new variable expressions, it wraps the original expression inside
 * this class.
 * </p>
 *
 * @version 2.0
 */
public class VCVarExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The actual declared mathematical expression represented in the code.
     * </p>
     */
    private final Exp myOrigExp;

    /**
     * <p>
     * The state number associated with this expression.
     * </p>
     */
    private final int myStateNum;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a wrapper expression for the original mathematical
     * expression.
     * </p>
     *
     * @param l A {@link Location} representation object.
     * @param exp An {@link Exp} that represents the actual expression.
     * @param stateNum A state number.
     */
    public VCVarExp(Location l, Exp exp, int stateNum) {
        super(l);
        myOrigExp = exp;

        // Make sure it is greater than 0
        if (stateNum <= 0) {
            throw new SourceErrorException(
                    "State number needs to be greater than 1.", myLoc);
        }
        myStateNum = stateNum;

        if (exp.getMathType() != null) {
            setMathType(exp.getMathType());
        }

        if (exp.getMathTypeValue() != null) {
            setMathTypeValue(exp.getMathTypeValue());
        }
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);

        // Generate the number of primes
        StringBuilder primeString = new StringBuilder();
        for (int i = 0; i < myStateNum; i++) {
            primeString.append("'");
        }

        if (myOrigExp instanceof DotExp) {
            DotExp myOriginalExpAsDotExp = (DotExp) myOrigExp;
            List<Exp> segments = myOriginalExpAsDotExp.getSegments();

            // Print everything up to the last statement.
            for (int i = 0; i < segments.size() - 1; i++) {
                sb.append(segments.get(i).asString(0, innerIndentInc));
                sb.append(".");
            }

            // Special handling for FunctionExp inside a DotExp
            Exp lastSegment = segments.get(segments.size() - 1);
            if (lastSegment instanceof FunctionExp) {
                sb.append(functionExpWithNameModified((FunctionExp) lastSegment,
                        primeString.toString()).asString(0, innerIndentInc));
            }
            else {
                sb.append(lastSegment.asString(0, innerIndentInc));
                sb.append(primeString.toString());
            }
        }
        else if (myOrigExp instanceof FunctionExp) {
            sb.append(functionExpWithNameModified((FunctionExp) myOrigExp,
                    primeString.toString()).asString(0, innerIndentInc));
        }
        else {
            sb.append(myOrigExp.asString(0, innerIndentInc));
            sb.append(primeString.toString());
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsExp(Exp exp) {
        boolean found = false;
        if (myOrigExp != null) {
            found = myOrigExp.containsExp(exp);
        }

        return found;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsVar(String varName, boolean IsOldExp) {
        boolean found = false;
        if (myOrigExp != null) {
            if (!IsOldExp) {
                found = myOrigExp.containsVar(varName, false);
            }
        }

        return found;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        VCVarExp vcVarExp = (VCVarExp) o;

        if (myStateNum != vcVarExp.myStateNum)
            return false;
        return myOrigExp.equals(vcVarExp.myOrigExp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean retval = false;
        if (e instanceof VCVarExp) {
            VCVarExp eAsVarExp = (VCVarExp) e;
            retval = myOrigExp.equivalent(eAsVarExp.myOrigExp)
                    && myStateNum == eAsVarExp.myStateNum;
        }

        return retval;
    }

    /**
     * <p>
     * This method returns this VC variable expression's actual expression.
     * </p>
     *
     * @return The {@link Exp} that we prepended the "?" to.
     */
    public final Exp getExp() {
        return myOrigExp;
    }

    /**
     * <p>
     * This method returns the number of "?" prepended to the actual expression.
     * </p>
     *
     * @return The state number.
     */
    public final int getStateNum() {
        return myStateNum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Exp> getSubExpressions() {
        List<Exp> list = new ArrayList<>();
        list.add(myOrigExp);

        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myOrigExp.hashCode();
        result = 31 * result + myStateNum;
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp copy() {
        return new VCVarExp(cloneLocation(), myOrigExp.clone(), myStateNum);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        Map<Exp, Exp> modifiedSubstitutions =
                new LinkedHashMap<>(substitutions.size());
        for (Exp keyExp : substitutions.keySet()) {
            // YS: Remove the substitution that directly matches our inner expression.
            // We don't want to replace that since it is not a direct match.
            if (!keyExp.equivalent((myOrigExp))) {
                // YS: Special handling for FunctionExp.
                if (myOrigExp instanceof FunctionExp) {
                    FunctionExp myOrigExpAsFunctionExp =
                            (FunctionExp) myOrigExp;

                    VarExp functionNameExp =
                            (VarExp) myOrigExpAsFunctionExp.getName().clone();
                    functionNameExp.setQualifier(
                            myOrigExpAsFunctionExp.getQualifier());

                    // Only add if it doesn't match the function name
                    if (!keyExp.equivalent(functionNameExp)) {
                        modifiedSubstitutions.put(keyExp.clone(),
                                substitutions.get(keyExp).clone());
                    }
                }
                else {
                    modifiedSubstitutions.put(keyExp.clone(),
                            substitutions.get(keyExp).clone());
                }
            }
        }

        return new VCVarExp(cloneLocation(),
                myOrigExp.substitute(modifiedSubstitutions), myStateNum);
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>
     * An helper function for creating a new {@link FunctionExp} with primes
     * attached to the function
     * name.
     * </p>
     *
     * @param exp Original function expression.
     * @param primes A string containing the state number of primes.
     *
     * @return A modified function expression.
     */
    private FunctionExp functionExpWithNameModified(FunctionExp exp,
            String primes) {
        VarExp functionNameExp = exp.getName();
        PosSymbol newName = new PosSymbol(functionNameExp.getLocation().clone(),
                functionNameExp.getName().getName() + primes);
        PosSymbol newQualifier = null;
        if (exp.getQualifier() != null) {
            newQualifier = exp.getQualifier().clone();
        }

        VarExp newFunctionNameExp =
                new VarExp(functionNameExp.getLocation().clone(), newQualifier,
                        newName, functionNameExp.getQuantification());
        if (functionNameExp.isIsPrecisDefinitionName()) {
            newFunctionNameExp.setIsPrecisDefinitionName();
        }

        Exp newFunctionCaratExp = null;
        if (exp.getCaratExp() != null) {
            newFunctionCaratExp = exp.getCaratExp().clone();
        }

        List<Exp> copyArgs = new ArrayList<>();
        for (Exp argExp : exp.getArguments()) {
            copyArgs.add(argExp.clone());
        }

        return new FunctionExp(exp.getLocation().clone(), newFunctionNameExp,
                newFunctionCaratExp, copyArgs);
    }
}
