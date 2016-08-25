/**
 * Populator.java
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
package edu.clemson.cs.rsrg.typeandpopulate;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.AbstractFunctionExp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.LambdaExp;
import edu.clemson.cs.rsrg.treewalk.TreeWalkerVisitor;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.*;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeComparison;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.util.Comparator;

/**
 * <p>This class populates the symbol table and assigns mathematical types to the
 * provided RESOLVE abstract syntax tree. This visitor logic is implemented as a
 * a {@link TreeWalkerVisitor}.</p>
 *
 * @version 2.0
 */
public class Populator extends TreeWalkerVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Toggle this flag on if you want TypeGraph/Populator debug messages.</p> */
    private static final boolean PRINT_DEBUG = false;

    /**
     * <p>A {@link TypeComparison} for to find exact domain match between a
     * {@link AbstractFunctionExp} and a {@link MTType}.</p>
     */
    private static final TypeComparison<AbstractFunctionExp, MTFunction> EXACT_DOMAIN_MATCH =
            new ExactDomainMatch();

    /** <p>An exact parameter {@link Comparator} for {@link MTType}.</p> */
    private static final Comparator<MTType> EXACT_PARAMETER_MATCH =
            new ExactParameterMatch();

    /**
     * <p>A {@link TypeComparison} for to find inexact domain match between a
     * {@link AbstractFunctionExp} and a {@link MTType}.</p>
     */
    private final TypeComparison<AbstractFunctionExp, MTFunction> INEXACT_DOMAIN_MATCH =
            new InexactDomainMatch();

    /**
     * <p>A {@link TypeComparison} for to find inexact parameter match between a
     * {@link Exp} and a {@link MTType}.</p>
     */
    private final TypeComparison<Exp, MTType> INEXACT_PARAMETER_MATCH =
            new InexactParameterMatch();

    /**
     * <p>This is the math type graph that indicates relationship
     * between different math types.</p>
     */
    private final TypeGraph myTypeGraph;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * TODO: Refactor this and add JavaDoc.
     *
     * @param builder A scope builder for a symbol table.
     */
    public Populator(MathSymbolTableBuilder builder) {
        //myActiveQuantifications.push(SymbolTableEntry.Quantification.NONE);
        myTypeGraph = builder.getTypeGraph();
        //myBuilder = builder;
        //myFacilityQualifier = null;
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // ModuleDec
    // -----------------------------------------------------------

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method prints debugging messages if that flag is
     * enabled by the user.</p>
     *
     * @param msg Message to be displayed.
     */
    public static void emitDebug(String msg) {
        if (PRINT_DEBUG) {
            System.out.println(msg);
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>An helper class that indicates an exact domain match between an {@link AbstractFunctionExp}
     * and a {@link MTFunction}.</p>
     */
    private static class ExactDomainMatch
            implements
                TypeComparison<AbstractFunctionExp, MTFunction> {

        /**
         * <p>Takes an instance of {@link AbstractFunctionExp} and use the {@link MTType}
         * found in the expression and compare it with the expected {@link MTType}.</p>
         *
         * @param foundValue The expression to be compared.
         * @param foundType The type for the expression.
         * @param expectedType The expected type for the expression.
         *
         * @return {@code true} if {@code foundType = expectedType}, {@code false} otherwise.
         */
        @Override
        public final boolean compare(AbstractFunctionExp foundValue,
                MTFunction foundType, MTFunction expectedType) {
            return foundType.parameterTypesMatch(expectedType,
                    EXACT_PARAMETER_MATCH);
        }

        /**
         * <p>This method returns a string description for each type comparison.</p>
         *
         * @return A string.
         */
        @Override
        public final String description() {
            return "exact";
        }

    }

    /**
     * <p>An helper class that indicates an inexact domain match between an {@link AbstractFunctionExp}
     * and a {@link MTFunction}.</p>
     */
    private class InexactDomainMatch
            implements
                TypeComparison<AbstractFunctionExp, MTFunction> {

        /**
         * <p>Takes an instance of {@link AbstractFunctionExp} and use the {@link MTType}
         * found in the expression and compare it with the expected {@link MTType}.</p>
         *
         * @param foundValue The expression to be compared.
         * @param foundType The type for the expression.
         * @param expectedType The expected type for the expression.
         *
         * @return {@code true} if {@code foundType = expectedType}, {@code false} otherwise.
         */
        @Override
        public final boolean compare(AbstractFunctionExp foundValue,
                MTFunction foundType, MTFunction expectedType) {
            return expectedType.parametersMatch(foundValue.getParameters(),
                    INEXACT_PARAMETER_MATCH);
        }

        /**
         * <p>This method returns a string description for each type comparison.</p>
         *
         * @return A string.
         */
        @Override
        public final String description() {
            return "inexact";
        }

    }

    /**
     * <p>An helper class that indicates an exact parameter match between
     * two {@link MTType MTTypes}.</p>
     */
    private static class ExactParameterMatch implements Comparator<MTType> {

        /**
         * <p>Compares <code>o1</code> and <code>o2</code>.</p>
         *
         * @param o1 A mathematical type.
         * @param o2 Another mathematical type.
         *
         * @return Comparison results expressed as an integer.
         */
        @Override
        public final int compare(MTType o1, MTType o2) {
            int result;

            if (o1.equals(o2)) {
                result = 0;
            }
            else {
                result = 1;
            }

            return result;
        }

    }

    /**
     * <p>An helper class that indicates an inexact domain match between an {@link Exp}
     * and a {@link MTType}.</p>
     */
    private class InexactParameterMatch implements TypeComparison<Exp, MTType> {

        /**
         * <p>Takes an instance of {@link Exp} and use the {@link MTType}
         * found in the expression and compare it with the expected {@link MTType}.</p>
         *
         * @param foundValue The expression to be compared.
         * @param foundType The type for the expression.
         * @param expectedType The expected type for the expression.
         *
         * @return {@code true} if {@code foundType = expectedType}, {@code false} otherwise.
         */
        @Override
        public final boolean compare(Exp foundValue, MTType foundType,
                MTType expectedType) {

            boolean result =
                    myTypeGraph.isKnownToBeIn(foundValue, expectedType);

            if (!result && foundValue instanceof LambdaExp
                    && expectedType instanceof MTFunction) {
                LambdaExp foundValueAsLambda = (LambdaExp) foundValue;
                MTFunction expectedTypeAsFunction = (MTFunction) expectedType;
                MTFunction foundTypeAsFunction =
                        (MTFunction) foundValueAsLambda.getMathType();

                result =
                        myTypeGraph.isSubtype(foundTypeAsFunction.getDomain(),
                                expectedTypeAsFunction.getDomain())
                                && myTypeGraph.isKnownToBeIn(foundValueAsLambda
                                        .getBody(), expectedTypeAsFunction
                                        .getRange());
            }

            return result;
        }

        /**
         * <p>This method returns a string description for each type comparison.</p>
         *
         * @return A string.
         */
        @Override
        public final String description() {
            return "inexact";
        }

    }

}