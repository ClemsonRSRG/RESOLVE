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

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.OperationDec;
import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.ProcedureDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.AbstractFunctionExp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.LambdaExp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.treewalk.TreeWalkerVisitor;
import edu.clemson.cs.rsrg.typeandpopulate.entry.ProgramTypeEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.*;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.rsrg.typeandpopulate.query.NameQuery;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeComparison;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;

import java.util.*;

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

    /** <p>The symbol table we are currently building.</p> */
    private final MathSymbolTableBuilder myBuilder;

    /**
     * <p>A mapping from generic types that appear in the module to the math
     * types that bound their possible values.</p>
     */
    private final Map<String, MTType> myGenericTypes = new HashMap<>();

    /**
     * <p>This is the math type graph that indicates relationship
     * between different math types.</p>
     */
    private final TypeGraph myTypeGraph;

    /**
     * <p>An helper value that helps evaluate mathematical type values.</p>
     */
    private int myTypeValueDepth = 0;

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
        myBuilder = builder;
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

    // -----------------------------------------------------------
    // Math Type-Related
    // -----------------------------------------------------------

    /**
     * <p>Add a new binding for a {@link ResolveConceptualElement}.</p>
     *
     * @param name Name of the entry.
     * @param l Location where this element was found.
     * @param q A quantifier.
     * @param definingElement The object that is receiving the binding.
     * @param type The mathematical type associated with the object.
     * @param typeValue The mathematical type value associated with the object.
     * @param schematicTypes The schematic types associated with the object.
     *
     * @return A new {@link SymbolTableEntry} with the types bound to the object.
     */
    private SymbolTableEntry addBinding(String name, Location l, SymbolTableEntry.Quantification q, ResolveConceptualElement definingElement, MTType type, MTType typeValue, Map<String, MTType> schematicTypes) {
        if (type == null) {
            throw new NullPointerException();
        }
        else {
            try {
                return myBuilder.getInnermostActiveScope().addBinding(name, q, definingElement, type, typeValue, schematicTypes, myGenericTypes);
            }
            catch (DuplicateSymbolException dse) {
                duplicateSymbol(name, l);
                throw new RuntimeException(); //This will never fire
            }
        }
    }

    /**
     * <p>Add a new binding for a {@link ResolveConceptualElement} with no quantification.</p>
     *
     * @param name Name of the entry.
     * @param l Location where this element was found.
     * @param definingElement The object that is receiving the binding.
     * @param type The mathematical type associated with the object.
     * @param typeValue The mathematical type value associated with the object.
     * @param schematicTypes The schematic types associated with the object.
     *
     * @return A new {@link SymbolTableEntry} with the types bound to the object.
     */
    private SymbolTableEntry addBinding(String name, Location l, ResolveConceptualElement definingElement, MTType type, MTType typeValue, Map<String, MTType> schematicTypes) {
        return addBinding(name, l, SymbolTableEntry.Quantification.NONE, definingElement, type, typeValue, schematicTypes);
    }

    /**
     * <p>Add a new binding for a {@link ResolveConceptualElement} with
     * no mathematical type value.</p>
     *
     * @param name Name of the entry.
     * @param l Location where this element was found.
     * @param q A quantifier.
     * @param definingElement The object that is receiving the binding.
     * @param type The mathematical type associated with the object.
     * @param schematicTypes The schematic types associated with the object.
     *
     * @return A new {@link SymbolTableEntry} with the types bound to the object.
     */
    private SymbolTableEntry addBinding(String name, Location l, SymbolTableEntry.Quantification q, ResolveConceptualElement definingElement, MTType type, Map<String, MTType> schematicTypes) {
        return addBinding(name, l, q, definingElement, type, null, schematicTypes);
    }

    /**
     * <p>Add a new binding for a {@link ResolveConceptualElement} with no quantification
     * and no mathematical type value.</p>
     *
     * @param name Name of the entry.
     * @param l Location where this element was found.
     * @param definingElement The object that is receiving the binding.
     * @param type The mathematical type associated with the object.
     * @param schematicTypes The schematic types associated with the object.
     *
     * @return A new {@link SymbolTableEntry} with the types bound to the object.
     */
    private SymbolTableEntry addBinding(String name, Location l, ResolveConceptualElement definingElement, MTType type, Map<String, MTType> schematicTypes) {
        return addBinding(name, l, SymbolTableEntry.Quantification.NONE, definingElement, type, null, schematicTypes);
    }

    /**
     * <p>An helper method that indicates we are beginning to evaluate a type value node.</p>
     */
    private void enteringTypeValueNode() {
        myTypeValueDepth++;
    }

    /**
     * <p>An helper method that indicates we are leaving a type value node.</p>
     */
    private void leavingTypeValueNode() {
        myTypeValueDepth--;
    }

    // -----------------------------------------------------------
    // Program Type-Related
    // -----------------------------------------------------------

    /**
     * <p>An helper method that returns the built-in <code>Character</code>
     * program type.</p>
     *
     * @param l A {@link Location} in file.
     *
     * @return A {@link PTType}.
     */
    private PTType getCharProgramType(Location l) {
        PTType result;

        try {
            ProgramTypeEntry type =
                    myBuilder.getInnermostActiveScope().queryForOne(
                            new NameQuery(null, "Character",
                                    ImportStrategy.IMPORT_NAMED,
                                    FacilityStrategy.FACILITY_INSTANTIATE,
                                    false)).toProgramTypeEntry(l);

            result = type.getProgramType();
        }
        catch (NoSuchSymbolException nsse) {
            throw new RuntimeException("No program Character type in scope???");
        }
        catch (DuplicateSymbolException dse) {
            //Shouldn't be possible--NameQuery can't throw this
            throw new RuntimeException(dse);
        }

        return result;
    }

    /**
     * <p>An helper method that returns the built-in <code>Integer</code>
     * program type.</p>
     *
     * @param l A {@link Location} in file.
     *
     * @return A {@link PTType}.
     */
    private PTType getIntegerProgramType(Location l) {
        PTType result;

        try {
            ProgramTypeEntry type =
                    myBuilder.getInnermostActiveScope().queryForOne(
                            new NameQuery(null, "Integer",
                                    ImportStrategy.IMPORT_NAMED,
                                    FacilityStrategy.FACILITY_INSTANTIATE,
                                    false)).toProgramTypeEntry(l);

            result = type.getProgramType();
        }
        catch (NoSuchSymbolException nsse) {
            throw new RuntimeException("No program Integer type in scope???");
        }
        catch (DuplicateSymbolException dse) {
            //Shouldn't be possible--NameQuery can't throw this
            throw new RuntimeException(dse);
        }

        return result;
    }

    /**
     * <p>An helper method that returns the built-in <code>Char_Str</code>
     * program type.</p>
     *
     * @param l A {@link Location} in file.
     *
     * @return A {@link PTType}.
     */
    private PTType getStringProgramType(Location l) {
        PTType result;

        try {
            ProgramTypeEntry type =
                    myBuilder.getInnermostActiveScope().queryForOne(
                            new NameQuery(null, "Char_Str",
                                    ImportStrategy.IMPORT_NAMED,
                                    FacilityStrategy.FACILITY_INSTANTIATE,
                                    false)).toProgramTypeEntry(l);

            result = type.getProgramType();
        }
        catch (NoSuchSymbolException nsse) {
            throw new RuntimeException("No program String type in scope???");
        }
        catch (DuplicateSymbolException dse) {
            //Shouldn't be possible--NameQuery can't throw this
            throw new RuntimeException(dse);
        }

        return result;
    }

    // -----------------------------------------------------------
    // Operation-Related
    // -----------------------------------------------------------

    /**
     * <p>Obtains the list of all <code>OperationDec</code> and
     * <code>ProcedureDec</code>.</p>
     *
     * @param decs List of all declarations.
     *
     * @return List containing only operations.
     */
    private List<Dec> getOperationDecs(List<Dec> decs) {
        List<Dec> decList = new LinkedList<>();
        for (Dec d : decs) {
            if (d instanceof OperationDec || d instanceof ProcedureDec) {
                decList.add(d);
            }
        }
        return decList;
    }

    /**
     * <p>Checks to see if all operation specified by concept/enhancement
     * are implemented by the corresponding realization.</p>
     *
     * @param location The module that called this method.
     * @param specDecs List of decs of the Concept/Enhancement
     * @param realizationDecs List of decs of the realization.
     */
    private void implementAllOper(Location location, List<Dec> specDecs, List<Dec> realizationDecs) {
        List<Dec> opDecList1 = getOperationDecs(specDecs);
        List<Dec> opDecList2 = getOperationDecs(realizationDecs);

        for (Dec d1 : opDecList1) {
            boolean inRealization = false;
            for (Dec d2 : opDecList2) {
                if (d1.getName().equals(d2.getName())) {
                    inRealization = true;
                }
            }
            if (!inRealization) {
                throw new SourceErrorException("Operation " + d1.getName()
                        + " not implemented by the realization.", location);
            }
        }
    }

    // -----------------------------------------------------------
    // Error Handling
    // -----------------------------------------------------------

    /**
     * <p>An helper method that indicates we have an ambiguous symbol.</p>
     *
     * @param symbol The symbol represented as a {@link PosSymbol}.
     * @param candidates List of symbol entries that match {@code symbol}.
     * @param <T> A type that extends from {@link SymbolTableEntry}.
     */
    private <T extends SymbolTableEntry> void ambiguousSymbol(PosSymbol symbol, List<T> candidates) {
        ambiguousSymbol(symbol.getName(), symbol.getLocation(), candidates);
    }

    /**
     * <p>An helper method that indicates we have an ambiguous symbol.</p>
     *
     * @param symbolName The symbol represented as a {@link String}.
     * @param l The location where {@code symbolName} was found.
     * @param candidates List of symbol entries that match {@code symbolName}.
     * @param <T> A type that extends from {@link SymbolTableEntry}.
     */
    private <T extends SymbolTableEntry> void ambiguousSymbol(String symbolName, Location l, List<T> candidates) {
        String message = "Ambiguous symbol.  Candidates: ";

        boolean first = true;
        for (SymbolTableEntry candidate : candidates) {
            if (first) {
                first = false;
            }
            else {
                message += ", ";
            }

            message +=
                    candidate.getSourceModuleIdentifier()
                            .fullyQualifiedRepresentation(symbolName);
        }

        message += ".  Consider qualifying.";

        throw new SourceErrorException(message, l);
    }

    /**
     * <p>An helper method that indicates we have a duplicate symbol.</p>
     *
     * @param symbol The symbol represented as a {@link PosSymbol}.
     */
    private void duplicateSymbol(PosSymbol symbol) {
        duplicateSymbol(symbol.getName(), symbol.getLocation());
    }

    /**
     * <p>An helper method that indicates we have a duplicate symbol.</p>
     *
     * @param symbol The symbol represented as a {@link PosSymbol}.
     * @param l The location where {@code symbol} was found.
     */
    private void duplicateSymbol(String symbol, Location l) {
        throw new SourceErrorException("Duplicate symbol: " + symbol, l);
    }

    /**
     * <p>An helper method that indicates the expected type differs
     * from the one we found.</p>
     *
     * @param e Expression that is being evaluated.
     * @param expectedType The expected type for {@code e}.
     */
    private void expected(Exp e, MTType expectedType) {
        throw new SourceErrorException("Expected: " + expectedType
                + "\nFound: " + e.getMathType(), e.getLocation());
    }

    /**
     * <p>An helper method that indicates the type we found is not known
     * to be in the expected type.</p>
     *
     * @param e Expression that is being evaluated.
     * @param expectedType The expected type for {@code e}.
     */
    private void expectType(Exp e, MTType expectedType) {
        if (!myTypeGraph.isKnownToBeIn(e, expectedType)) {
            expected(e, expectedType);
        }
    }

    /**
     * <p>An helper method that indicates that the symbol table entry is not
     * known to be a type.</p>
     *
     * @param entry An entry in our symbol table.
     * @param l The location where this entry was found.
     */
    private void notAType(SymbolTableEntry entry, Location l) {
        throw new SourceErrorException(entry.getSourceModuleIdentifier()
                .fullyQualifiedRepresentation(entry.getName())
                + " is not known to be a type.", l);
    }

    /**
     * <p>An helper method that indicates that the evaluated expression is not
     * known to be a type.</p>
     *
     * @param e An expression that is being evaluated.
     */
    private void notAType(Exp e) {
        throw new SourceErrorException("Not known to be a type.", e.getLocation());
    }

    /**
     * <p>An helper method that indicates that a module with the specified name
     * cannot be found.</p>
     *
     * @param qualifier The name of a module.
     */
    private void noSuchModule(PosSymbol qualifier) {
        throw new SourceErrorException("Module does not exist or is not in scope.", qualifier);
    }

    /**
     * <p>An helper method that indicates that a symbol with the specified qualifier
     * and name cannot be found.</p>
     *
     * @param qualifier The module qualifier for the symbol.
     * @param symbol The name of the symbol represented as a {@link PosSymbol}.
     */
    private void noSuchSymbol(PosSymbol qualifier, PosSymbol symbol) {
        noSuchSymbol(qualifier, symbol.getName(), symbol.getLocation());
    }

    /**
     * <p>An helper method that indicates that a symbol with the specified qualifier
     * and name cannot be found.</p>
     *
     * @param qualifier The module qualifier for the symbol.
     * @param symbolName The name of the symbol represented as a {@link String}.
     * @param l The location where this symbol was found.
     */
    private void noSuchSymbol(PosSymbol qualifier, String symbolName, Location l) {
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