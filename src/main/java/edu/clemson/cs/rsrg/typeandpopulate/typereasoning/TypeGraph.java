/*
 * TypeGraph.java
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
package edu.clemson.cs.rsrg.typeandpopulate.typereasoning;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.cs.rsrg.init.CompileEnvironment;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.StatusHandler;
import edu.clemson.cs.rsrg.typeandpopulate.Populator;
import edu.clemson.cs.rsrg.typeandpopulate.entry.MathSymbolEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSolutionException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.TypeMismatchException;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.*;
import edu.clemson.cs.rsrg.typeandpopulate.query.UnqualifiedNameQuery;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.Scope;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.relationships.EqualsPredicate;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.relationships.TypeRelationship;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.relationships.TypeRelationshipPredicate;
import edu.clemson.cs.rsrg.typeandpopulate.typevisitor.CanonicalizingVisitor;
import edu.clemson.cs.rsrg.typeandpopulate.typevisitor.UnboundTypeAccumulator;
import edu.clemson.cs.rsrg.typeandpopulate.typevisitor.VariableReplacingVisitor;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.FunctionApplicationFactory;
import java.util.*;

/**
 * <p>Represents a directed graph of types, where edges between types
 * indicate a possible coercion that the type checker can perform.</p>
 *
 * @version 2.0
 */
public class TypeGraph {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>A set of non-thread-safe resources to be used during general type
     * reasoning. This really doesn't belong here, but anything that's reasoning
     * about types should already have access to a type graph, and only one type
     * graph is created per thread, so this is a convenient place to put it.</p>
     */
    public final PerThreadReasoningResources threadResources =
            new PerThreadReasoningResources();

    /** <p>A {@link NodePairPathStrategy} for {@link Exp}.</p> */
    private final ExpValuePathStrategy EXP_VALUE_PATH =
            new ExpValuePathStrategy();

    /** <p>A {@link NodePairPathStrategy} for {@link MTType}.</p> */
    private final MTTypeValuePathStrategy MTTYPE_VALUE_PATH =
            new MTTypeValuePathStrategy();

    /** <p>This contains all mathematical nodes for this graph.</p> */
    private final HashMap<MTType, TypeNode> myTypeNodes;

    /** <p>This contains all established relationships for mathematical subtypes.</p> */
    private final Set<EstablishedRelationship> myEstablishedSubtypes =
            new HashSet<>();

    /** <p>This contains all established relationships for mathematical elements.</p> */
    private final Set<EstablishedRelationship> myEstablishedElements =
            new HashSet<>();

    /**
     * <p>The current job's compilation environment
     * that stores all necessary objects and flags.</p>
     */
    private final CompileEnvironment myCompileEnvironment;

    /**
     * <p>This is the status handler for the RESOLVE compiler.</p>
     */
    private final StatusHandler myStatusHandler;

    // ===========================================================
    // Function Factories
    // ===========================================================

    /** <p>Factory for creating <code>Powerclass</code> applications.</p> */
    private final static FunctionApplicationFactory POWERCLASS_APPLICATION =
            new PowerclassApplicationFactory();

    /** <p>Factory for creating <code>Powerset</code> applications.</p> */
    private final static FunctionApplicationFactory POWERSET_APPLICATION =
            new PowersetApplicationFactory();

    /** <p>Factory for creating <code>Union</code> applications.</p> */
    private final static FunctionApplicationFactory UNION_APPLICATION =
            new UnionApplicationFactory();

    /** <p>Factory for creating <code>Intersection</code> applications.</p> */
    private final static FunctionApplicationFactory INTERSECT_APPLICATION =
            new IntersectApplicationFactory();

    /** <p>Factory for creating <code>Function</code> constructor applications.</p> */
    private final static FunctionApplicationFactory FUNCTION_CONSTRUCTOR_APPLICATION =
            new FunctionConstructorApplicationFactory();

    /** <p>Factory for creating <code>Cartesian Product</code> applications.</p> */
    private final static FunctionApplicationFactory CARTESIAN_PRODUCT_APPLICATION =
            new CartesianProductApplicationFactory();

    // ===========================================================
    // Global Mathematical Types
    // ===========================================================

    /** <p><code>Element</code></p> */
    public final MTType ELEMENT = new MTProper(this, "Element");

    /** <p><code>Entity</code></p> */
    public final MTType ENTITY = new MTProper(this, "Entity");

    /** <p><code>Class</code></p> */
    public final MTProper CLS = new MTProper(this, null, true, "Cls");

    /** <p><code>SSet</code></p> */
    public final MTProper SSET = new MTProper(this, CLS, true, "SSet");

    /** <p><code>Boolean</code></p> */
    public final MTProper BOOLEAN = new MTProper(this, SSET, false, "B");

    /** <p><code>R</code></p> */
    public final MTProper R = new MTProper(this, CLS, false, "R");

    /** <p><code>Atom</code></p> */
    public final MTProper ATOM = new MTProper(this, CLS, false, "Atom");

    /** <p><code>Void</code></p> */
    public final MTProper VOID = new MTProper(this, CLS, false, "Void");

    /** <p><code>Empty_Class</code></p> */
    public final MTProper EMPTY_CLASS =
            new MTProper(this, CLS, false, "Empty_Class");

    /** <p><code>Empty_Set</code></p> */
    public final MTProper EMPTY_SET =
            new MTProper(this, SSET, false, "Empty_Set");

    /** <p><code>Receptacles</code></p> */
    public final MTProper RECEPTACLES =
            new MTProper(this, SSET, false, "Receptacles");

    // ===========================================================
    // CLS Functions
    // ===========================================================

    /** <p>{@code Powerclass} function</p> */
    public final MTFunction POWERCLASS =
            new MTFunction(this, true, POWERCLASS_APPLICATION, CLS, CLS);

    /** <p>{@code CLS} union function</p> */
    public final MTFunction UNION =
            new MTFunction(this, UNION_APPLICATION, CLS, CLS, CLS);

    /** <p>{@code CLS} intersection function</p> */
    public final MTFunction INTERSECT =
            new MTFunction(this, INTERSECT_APPLICATION, CLS, CLS, CLS);

    /** <p>{@code CLS} function operator</p> */
    public final MTFunction CLS_FUNCTION =
            new MTFunction(this, FUNCTION_CONSTRUCTOR_APPLICATION, CLS, CLS,
                    CLS);

    /** <p>{@code CLS} Cartesian product function</p> */
    public final MTFunction CLS_CROSS =
            new MTFunction(this, CARTESIAN_PRODUCT_APPLICATION, CLS, CLS, CLS);

    // ===========================================================
    // SSet Functions
    // ===========================================================

    /** <p>{@code Powerset} function</p> */
    public final MTFunction POWERSET =
            new MTFunction(this, true, POWERSET_APPLICATION, SSET, SSET);

    /** <p>{@code SSet} function operator</p> */
    public final MTFunction SSET_FUNCTION =
            new MTFunction(this, FUNCTION_CONSTRUCTOR_APPLICATION, SSET, SSET, SSET);

    /** <p>{@code SSet} Cartesian product function</p> */
    public final MTFunction SSET_CROSS =
            new MTFunction(this, CARTESIAN_PRODUCT_APPLICATION, SSET, SSET, SSET);

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a mathematical type graph.</p>
     *
     * @param compileEnvironment The current job's compilation environment
     *                           that stores all necessary objects and flags.
     */
    public TypeGraph(CompileEnvironment compileEnvironment) {
        myTypeNodes = new HashMap<>();
        myCompileEnvironment = compileEnvironment;
        myStatusHandler = myCompileEnvironment.getStatusHandler();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Establishes that values binding to <code>bindingExpression</code> under
     * the given environment may be considered to be of type
     * <code>destination</code> assuming the proof obligations set forth in
     * <code>bindingCondition</code> are satisfied.
     * </p>
     *
     * <p>
     * <code>bindingExpression</code> must have a mathematical type set on it.
     * I.e., it must be the case that
     * <code>bindingExpression.getMathType() != null</code>.
     * </p>
     *
     * <p>
     * Any <code>VarExp</code>s, <code>AbstractFunctionExp</code>s, and
     * <code>MTNamed</code> that appear in <code>bindingExpression</code>, its
     * associated mathematical type, or <code>destination</code> must be bound
     * under the given environment.
     * </p>
     *
     * <p>
     * Conversely, any universally bound variables in the given environment must
     * appear in one of <code>bindingExpression</code>, its associated
     * mathematical type, or <code>destination</code>. This gives the typing
     * system a chance to provide concrete values to these "open" slots.
     * </p>
     *
     * @param bindingExpression A snippet of syntax tree describing the
     *        structure of values covered by this new relationship.
     * @param destination The mathematical type that this new relationship
     *        establishes values binding to <code>bindingExpression</code>
     *        inhabit.
     * @param bindingCondition Proof obligations that must be raised to
     *        establish that a given value binding to
     *        <code>bindingExpression</code> inhabits <code>destination</code>.
     * @param environment The environment under which
     *        <code>bindingExpression</code>, <code>destination</code>, and
     *        <code>bindingCondition</code> should be evaluated.
     */
    public final void addRelationship(Exp bindingExpression, MTType destination,
            Exp bindingCondition, Scope environment) {
        //Sanitize and sanity check our inputs somewhat
        if (destination == null) {
            throw new IllegalArgumentException("Destination type may not be "
                    + "null.");
        }

        MTType source = bindingExpression.getMathType();
        if (source == null) {
            throw new IllegalArgumentException("bindingExpression has no "
                    + "type.");
        }

        if (bindingCondition == null) {
            bindingCondition = MathExp.getTrueVarExp(null, this);
        }

        //Canonicalize the input types
        CanonicalizationResult sourceCanonicalResult =
                canonicalize(source, environment, "s");
        CanonicalizationResult destinationCanonicalResult =
                canonicalize(destination, environment, "d");

        Set<String> universalVariableNames =
                getUniversallyQuantifiedVariables(source, destination,
                        environment, sourceCanonicalResult,
                        destinationCanonicalResult);

        Map<String, List<String>> sourceEnvironmentalToCanonical =
                invertMap(sourceCanonicalResult.canonicalToEnvironmental);
        Map<String, List<String>> destinationEnvironmentalToCanonical =
                invertMap(destinationCanonicalResult.canonicalToEnvironmental);

        //Get a mapping from environmental variables to "exemplar" variables--
        //a single representative name that either the source or destination
        //will bind for us.  There may be many choices, but they're all
        //equivalent for our purposes
        Map<String, String> environmentalToExemplar =
                getEnvironmentalToExemplar(universalVariableNames,
                        sourceEnvironmentalToCanonical,
                        destinationEnvironmentalToCanonical);

        List<TypeRelationshipPredicate> finalPredicates =
                getFinalPredicates(sourceCanonicalResult,
                        destinationCanonicalResult, environmentalToExemplar,
                        universalVariableNames, sourceEnvironmentalToCanonical,
                        destinationEnvironmentalToCanonical);

        //We can't use the binding expression as-is.  It must be updated to
        //reflect canonical variable names
        Map<Exp, Exp> replacements = new HashMap<>();
        for (Map.Entry<String, String> entry : environmentalToExemplar.entrySet()) {
            replacements.put(new VarExp(null, null, new PosSymbol(null, entry.getKey())),
                    new VarExp(null, null, new PosSymbol(null, entry.getValue())));
        }
        bindingExpression =
                safeVariableNameUpdate(bindingExpression, replacements,
                        environmentalToExemplar);

        //Ditto for the binding condition
        bindingCondition =
                safeVariableNameUpdate(bindingCondition, replacements,
                        environmentalToExemplar);

        //At last!  We can add the relationship into the graph
        TypeRelationship relationship =
                new TypeRelationship(this,
                        destinationCanonicalResult.canonicalType,
                        bindingCondition, bindingExpression, finalPredicates);
        TypeNode sourceNode = getTypeNode(sourceCanonicalResult.canonicalType);
        sourceNode.addRelationship(relationship);

        // We'd like to force the presence of the destination node
        getTypeNode(destinationCanonicalResult.canonicalType);

        // Print debugging messages if the flag is on.
        if (myCompileEnvironment.flags.isFlagSet(Populator.FLAG_POPULATOR_DEBUG)) {
            StringBuffer sb = new StringBuffer();
            sb.append("\n---------------New Type Relationship---------------\n\n");
            sb.append("Added relationship to type node [");
            sb.append(sourceCanonicalResult.canonicalType);
            sb.append("]:\n");
            sb.append(relationship);
            sb.append("\n\n---------------End New Type Relationship---------------\n");
            myStatusHandler.info(null, sb.toString());
        }
    }

    /**
     * <p>Returns a new {@link MTType} with the appropriate substitutions.</p>
     *
     * @param original Original mathematical type.
     * @param substitutions A map of substitutions.
     *
     * @return A modified {@link MTType}.
     */
    public static MTType getCopyWithVariablesSubstituted(MTType original,
            Map<String, MTType> substitutions) {
        VariableReplacingVisitor renamer =
                new VariableReplacingVisitor(substitutions);
        original.accept(renamer);

        return renamer.getFinalExpression();
    }

    /**
     * <p>Returns a new {@link Exp} with the appropriate substitutions.</p>
     *
     * @param original Original expression.
     * @param substitutions A map of substitutions.
     *
     * @return A modified {@link Exp}.
     */
    public static Exp getCopyWithVariablesSubstituted(Exp original,
            Map<String, MTType> substitutions) {

        Exp result = original.clone();
        result.setMathType(result.getMathType()
                .getCopyWithVariablesSubstituted(substitutions));

        List<Exp> children = result.getSubExpressions();
        Map<Exp, Exp> newChildrenExp = new HashMap<>();
        for (Exp currentChildExp : children) {
            newChildrenExp.put(currentChildExp.clone(),
                    TypeGraph.getCopyWithVariablesSubstituted(currentChildExp.clone(), substitutions));
        }
        result = result.substitute(newChildrenExp);

        return result;
    }

    /**
     * <p>Returns <code>true</code> <strong>iff</strong> <code>value</code> is
     * known to definitely be a member of <code>expected</code>.</p>
     *
     * <p>
     * I.e., this is the same as asking if
     * <code>getValidTypeConditions(value, expected)</code> 1) doesn't throw an
     * exception and 2) returns a value for which <code>isLiteralTrue()</code>
     * returns <code>true</code>.
     * </p>
     *
     * @param value The <code>RESOLVE</code> value to test for membership.
     * @param expected A <code>RESOLVE</code> type against which to test
     *                 membership.
     *
     * @return <code>true</code> <strong>iff</strong> <code>value</code> is
     *         definitely in <code>expected</code>.
     */
    public final boolean isKnownToBeIn(Exp value, MTType expected) {
        boolean result;

        try {
            Exp conditions = getValidTypeConditions(value, expected);
            result = MathExp.isLiteralTrue(conditions);
        }
        catch (TypeMismatchException e) {
            result = false;
        }

        return result;
    }

    /**
     * <p>Returns <code>true</code> <strong>iff</strong> <code>value</code>, which
     * is known to identify a <strong>MType</strong>, is known to definitely be
     * a member of <code>expected</code>.</p>
     *
     * <p>
     * I.e., this is the same as asking if
     * <code>getValidTypeConditions(value, expected)</code> 1) doesn't throw an
     * exception and 2) returns a value for which <code>isLiteralTrue()</code>
     * returns <code>true</code>.
     * </p>
     *
     * @param value The <code>RESOLVE</code> value to test for membership.
     * @param expected A <code>RESOLVE</code> type against which to test
     *                 membership.
     *
     * @return <code>true</code> <strong>iff</strong> <code>value</code> is
     *         definitely in <code>expected</code>.
     */
    public final boolean isKnownToBeIn(MTType value, MTType expected) {
        boolean result;

        EstablishedRelationship r =
                new EstablishedRelationship(value, expected);

        //If the type of the given value is a subtype of the expected type, then
        //its value must necessarily be in the expected type.  Note we can't
        //reason about the type of CLS, so we exclude it
        result =
                myEstablishedElements.contains(r) || (value != CLS)
                        && (value != ENTITY)
                        && isSubtype(value.getType(), expected);

        if (!result) {
            try {
                Exp conditions = getValidTypeConditions(value, expected);
                result = MathExp.isLiteralTrue(conditions);
            }
            catch (TypeMismatchException e) {
                result = false;
            }
        }

        if (result) {
            myEstablishedElements.add(r);
        }

        return result;
    }

    /**
     * <p>Returns <code>true</code> <strong>iff</strong> every value in
     * <code>subtype</code> must necessarily be in <code>supertype</code>.</p>
     *
     * @param subtype A type to test if it is subsumed by <code>supertype</code>.
     * @param supertype A type to test if it subsumes <code>subtype</code>.
     *
     * @return Returns <code>true</code> <strong>iff</strong> every value in
     *         <code>subtype</code> must necessarily be in
     *         <code>supertype</code>.
     */
    public final boolean isSubtype(MTType subtype, MTType supertype) {
        boolean result;

        EstablishedRelationship r =
                new EstablishedRelationship(subtype, supertype);

        try {
            result =
                    supertype == ENTITY || supertype == CLS
                            || myEstablishedSubtypes.contains(r)
                            || subtype.equals(supertype)
                            || subtype.isSyntacticSubtypeOf(supertype);

            // Attempt to see if the subtype's type is a subtype of
            // the supertype. This comes up in a categorical definition
            // when the parameters have been introduced, but not yet added
            // to the type graph. - YS
            if (!result && subtype.getType() != null) {
                MTType subtypetype = subtype.getType();
                EstablishedRelationship r2 =
                        new EstablishedRelationship(subtypetype, supertype);
                result =
                        myEstablishedSubtypes.contains(r2)
                                || subtypetype.equals(subtype)
                                || subtypetype.isSyntacticSubtypeOf(supertype);
            }
        }
        catch (NoSuchElementException nsee) {
            //Syntactic subtype checker freaks out (rightly) if there are
            //free variables in the expression, but the next check will deal
            //correctly with them.
            result = false;
        }

        if (!result) {
            try {
                Exp conditions =
                        getValidTypeConditions(subtype,
                                new MTPowerclassApplication(this, supertype));
                result = MathExp.isLiteralTrue(conditions);
            }
            catch (TypeMismatchException e) {
                result = false;
            }
        }

        if (result) {
            myEstablishedSubtypes.add(r);
        }

        return result;
    }

    /**
     * <p>This method returns the object in string format.</p>
     *
     * @return Object as a string.
     */
    @Override
    public final String toString() {
        StringBuilder str = new StringBuilder();

        Iterator<MTType> keysIterator = myTypeNodes.keySet().iterator();
        while (keysIterator.hasNext()) {
            MTType next = keysIterator.next();
            str.append("----> Type Node: ");
            str.append(next.toString());
            str.append("\n");
            str.append(myTypeNodes.get(next).toString());

            if (keysIterator.hasNext()) {
                str.append("\n");
            }
        }

        return str.toString();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>Creates canonical names for names in {@code t}.</p>
     *
     * @param t A mathematical type.
     * @param environment The searching scope.
     * @param suffix A suffix string for the predicate.
     *
     * @return A {@link CanonicalizationResult} object with names in {@code t}
     * being their canonical forms.
     */
    private CanonicalizationResult canonicalize(MTType t, Scope environment,
            String suffix) {
        CanonicalizingVisitor canonicalizer =
                new CanonicalizingVisitor(this, environment, suffix);

        t.accept(canonicalizer);

        return new CanonicalizationResult(canonicalizer.getFinalExpression(),
                canonicalizer.getTypePredicates(), canonicalizer
                        .getCanonicalToEnvironmentOriginalMapping());
    }

    /**
     * <p>Returns a new {@link Exp} with the appropriate substitutions.</p>
     *
     * @param original Original expression.
     * @param substitutions A map of substitutions.
     *
     * @return A modified {@link Exp}.
     */
    private Exp getCopyWithVariableNamesChanged(Exp original, Map<String, String> substitutions) {
        Exp result = original.clone();

        if (result.getMathType() == null) {
            throw new RuntimeException("copy() method for class "
                    + original.getClass() + " did not properly copy the math "
                    + "type of the object.");
        }

        result.setMathType(getCopyWithVariableNamesChanged(
                result.getMathType(), substitutions));

        List<Exp> children = result.getSubExpressions();
        Map<Exp, Exp> newChildrenExp = new HashMap<>();
        for (Exp currentChildExp : children) {
            newChildrenExp.put(currentChildExp.clone(),
                    getCopyWithVariableNamesChanged(currentChildExp.clone(), substitutions));
        }
        result.substitute(newChildrenExp);

        return result;
    }

    /**
     * <p>Returns a new {@link MTType} with the appropriate substitutions.</p>
     *
     * @param original Original mathematical type.
     * @param substitutions A map of substitutions.
     *
     * @return A modified {@link MTType}.
     */
    private MTType getCopyWithVariableNamesChanged(MTType original,
            Map<String, String> substitutions) {
        VariableReplacingVisitor renamer =
                new VariableReplacingVisitor(substitutions, this);
        original.accept(renamer);

        return renamer.getFinalExpression();
    }

    /**
     * <p>Get a mapping from environmental variables to "exemplar" variables
     * -- a single representative name that either the source or destination
     * will bind for us. There may be many choices, but they're all
     * equivalent for our purposes.</p>
     *
     * @param universalVariableNames Set containing universal variable names.
     * @param sourceEnvironmentalToCanonical A map containing conversions to canonical forms
     *                                       from a source type.
     * @param destinationEnvironmentalToCanonical A map containing conversions to canonical forms
     *                                            from a destination type.
     *
     * @return A map containing the conversion from environmental to exemplar.
     */
    private static Map<String, String> getEnvironmentalToExemplar(
            Set<String> universalVariableNames,
            Map<String, List<String>> sourceEnvironmentalToCanonical,
            Map<String, List<String>> destinationEnvironmentalToCanonical) {
        Map<String, String> environmentalToExemplar =
                new HashMap<>();
        for (String envVar : universalVariableNames) {
            if (sourceEnvironmentalToCanonical.containsKey(envVar)) {
                environmentalToExemplar.put(envVar,
                        sourceEnvironmentalToCanonical.get(envVar).get(0));
            }
            else {
                //It must be in destination because we checked above that one
                //or the other makes use of this thing
                environmentalToExemplar.put(envVar,
                        destinationEnvironmentalToCanonical.get(envVar).get(0));
            }
        }

        return environmentalToExemplar;
    }

    /**
     * <p>Returns the list of type relationships established.</p>
     *
     * @param sourceCanonicalResult Canonical results from the source type.
     * @param destinationCanonicalResult Canonical results from the destination type.
     * @param environmentalToExemplar A map of environmental names to exemplar names.
     * @param universalVariableNames Set containing universal variable names.
     * @param sourceEnvironmentalToCanonical A map containing conversions to canonical forms
     *                                       from a source type.
     * @param destinationEnvironmentalToCanonical A map containing conversions to canonical forms
     *                                            from a destination type.
     *
     * @return A list of {@link TypeRelationshipPredicate}.
     */
    private List<TypeRelationshipPredicate> getFinalPredicates(
            CanonicalizationResult sourceCanonicalResult,
            CanonicalizationResult destinationCanonicalResult,
            Map<String, String> environmentalToExemplar,
            Set<String> universalVariableNames,
            Map<String, List<String>> sourceEnvironmentalToCanonical,
            Map<String, List<String>> destinationEnvironmentalToCanonical) {
        //To begin with, the final predicates should include the predicates
        //from each canonicalization, with top-level environmental variables
        //finalized to their exemplar variable
        List<TypeRelationshipPredicate> finalPredicates =
                new LinkedList<>();
        finalPredicates.addAll(replaceInPredicates(
                sourceCanonicalResult.predicates, environmentalToExemplar));
        finalPredicates
                .addAll(replaceInPredicates(
                        destinationCanonicalResult.predicates,
                        environmentalToExemplar));

        //Finally, it's possible that the same original variable existed in each
        //of the source and destination.  We lost this info during
        //canonicalization so we re-establish it with predicates
        for (String envVar : universalVariableNames) {
            if (sourceEnvironmentalToCanonical.containsKey(envVar)
                    && destinationEnvironmentalToCanonical.containsKey(envVar)) {
                finalPredicates.add(new EqualsPredicate(this, new MTNamed(this,
                        sourceEnvironmentalToCanonical.get(envVar).get(0)),
                        new MTNamed(this, destinationEnvironmentalToCanonical
                                .get(envVar).get(0))));
            }
        }

        return finalPredicates;
    }

    /**
     * <p>
     * Returns the conditions required to establish that <code>foundValue</code>
     * is a member of the type represented by <code>expectedEntry</code> along
     * the path from <code>foundEntry</code> to <code>expectedEntry</code>. If
     * no such conditions exist (i.e., if the conditions would be
     * <code>false</code>), throws a <code>TypeMismatchException</code>.
     * </p>
     *
     * @param foundValue The value we'd like to establish is in the type
     *        represented by <code>expectedEntry</code>.
     * @param foundEntry A node in the type graph of which
     *        <code>foundValue</code> is a syntactic subtype.
     * @param expectedEntry A node in the type graph of which representing a
     *        type in which we would like to establish <code>foundValue</code>
     *        resides.
     * @param pathStrategy The strategy for following the path between
     *        <code>foundEntry</code> and <code>expectedEntry</code>.
     *
     * @return The conditions under which the path can be followed.
     *
     * @throws TypeMismatchException If the conditions under which the path can
     *         be followed would be <code>false</code>.
     */
    private <V> Exp getPathConditions(V foundValue, Map.Entry<MTType, Map<String, MTType>> foundEntry,
            Map.Entry<MTType, Map<String, MTType>> expectedEntry, NodePairPathStrategy<V> pathStrategy)
            throws TypeMismatchException {
        Map<String, MTType> combinedBindings = new HashMap<>();

        combinedBindings.clear();
        combinedBindings.putAll(updateMapLabels(foundEntry.getValue(), "_s"));
        combinedBindings
                .putAll(updateMapLabels(expectedEntry.getValue(), "_d"));

        return pathStrategy.getValidTypeConditionsBetween(foundValue, foundEntry.getKey(),
                expectedEntry.getKey(), combinedBindings);
    }

    /**
     * <p>This method returns all the syntactic subtypes associated with {@code query}
     * as well as any type relationships that has been established.</p>
     *
     * @param query A mathematical type.
     *
     * @return A map containing subtypes and associated type relationships.
     */
    private Map<MTType, Map<String, MTType>> getSyntacticSubtypesWithRelationships(MTType query) {
        Map<MTType, Map<String, MTType>> result = new HashMap<>();

        Map<String, MTType> bindings;

        for (MTType potential : myTypeNodes.keySet()) {
            try {
                bindings = query.getSyntacticSubtypeBindings(potential);
                result.put(potential, new HashMap<>(bindings));
            }
            catch (NoSolutionException nse) {}
        }

        return result;
    }

    /**
     * <p>This method returns the type node representing {@code t}.</p>
     *
     * @param t A mathematical type.
     *
     * @return A {@link TypeNode}.
     */
    private TypeNode getTypeNode(MTType t) {
        TypeNode result = myTypeNodes.get(t);

        if (result == null) {
            result = new TypeNode(this, t);
            myTypeNodes.put(t, result);
        }

        return result;
    }

    /**
     * <p>
     * Returns the conditions under which <code>value</code> could be
     * demonstrated to be a member of <code>expected</code>, given that
     * <code>value</code> is known to be in <strong>MType</strong>.
     * </p>
     *
     * <p>
     * The result is a series of disjuncts expressing possible situations under
     * which the <code>value</code> would be known to be in
     * <code>expected</code>. One or more of these disjuncts may be
     * <code>false</code>, but if one or more would have been <code>true</code>,
     * this method will simplify the result to simply <code>true</code>.
     * </p>
     *
     * <p>
     * If there is no known set of circumstances under which <code>value</code>
     * could be demonstrated a member of <code>expected</code> (i.e., if the
     * return value would simply be <code>false</code>), this method throws a
     * <code>TypeMismatchException</code>.
     * </p>
     *
     * @param value The <code>RESOLVE</code> value to test for membership.
     * @param expected A <code>RESOLVE</code> type against which to test
     *        membership.
     *
     * @return The conditions under which <code>value</code> could be
     *         demonstrated to be in <code>expected</code>.
     *
     * @throws TypeMismatchException If there are no known conditions under
     *         which <code>value</code> could be demonstrated to be in
     *         <code>expected</code>.
     */
    private Exp getValidTypeConditions(MTType value, MTType expected)
            throws TypeMismatchException {
        //See note in the getValidTypeConditionsTo() in TypeRelationship,
        //re: Lovecraftian nightmare-scape

        Exp result = MathExp.getFalseVarExp(null, this);

        if (expected == CLS) {
            //Every CLS is in CLS except for Entity and CLS, itself
            result = MathExp.getTrueVarExp(null, this);
        }
        else if (expected instanceof MTPowerclassApplication) {
            if (value.equals(EMPTY_CLASS)) {
                //The empty class is in all powerclasses
                result = MathExp.getTrueVarExp(null, this);
            }
            else {
                //If "expected" happens to be Power(t) for some t, we can
                //"demote" value to an INSTANCE of itself (provided it is not
                //the empty set), and expected to just t
                MTPowerclassApplication expectedAsPowerclassApplication =
                        (MTPowerclassApplication) expected;

                DummyExp memberOfValue = new DummyExp(null, value);

                if (isKnownToBeIn(memberOfValue, expectedAsPowerclassApplication
                        .getArgument(0))) {
                    result = MathExp.getTrueVarExp(null, this);
                }
            }
        }

        //If we've already established it statically, no need for further work
        if (!MathExp.isLiteralTrue(result)) {
            //If we haven't...

            //At this stage, we've done everything safe and sensible that we can
            //do if the value we're looking at exists outside Entity
            if (value == CLS || value == ENTITY) {
                throw new TypeMismatchException(
                        "Unexpected mathematical type: " + value);
            }

            try {
                Exp intermediateResult =
                        getValidTypeConditions(value, value.getType(),
                                expected, MTTYPE_VALUE_PATH);

                if (MathExp.isLiteralTrue(intermediateResult)) {
                    result = intermediateResult;
                }
                else {
                    result =
                            MathExp.formDisjunct(result.getLocation(), result,
                                    intermediateResult);
                }
            }
            catch (TypeMismatchException tme) {
                if (MathExp.isLiteralFalse(result)) {
                    throw tme;
                }
            }
        }

        return result;
    }

    /**
     * <p>
     * Returns the conditions under which <code>value</code> could be
     * demonstrated to be a member of <code>expected</code>.
     * </p>
     *
     * <p>
     * The result is a series of disjuncts expressing possible situations under
     * which the <code>value</code> would be known to be in
     * <code>expected</code>. One or more of these disjuncts may be
     * <code>false</code>, but if one or more would have been <code>true</code>,
     * this method will simplify the result to simply <code>true</code>.
     * </p>
     *
     * <p>
     * If there is no known set of circumstances under which <code>value</code>
     * could be demonstrated a member of <code>expected</code> (i.e., if the
     * return value would simply be <code>false</code>), this method throws a
     * <code>TypeMismatchException</code>.
     * </p>
     *
     * @param value The <code>RESOLVE</code> value to test for membership.
     * @param expected A <code>RESOLVE</code> type against which to test
     *        membership.
     *
     * @return The conditions under which <code>value</code> could be
     *         demonstrated to be in <code>expected</code>.
     *
     * @throws TypeMismatchException If there are no known conditions under
     *         which <code>value</code> could be demonstrated to be in
     *         <code>expected</code>.
     */
    private Exp getValidTypeConditions(Exp value, MTType expected)
            throws TypeMismatchException {
        Exp result;

        MTType valueTypeValue = value.getMathTypeValue();
        if (expected == ENTITY && valueTypeValue != CLS
                && valueTypeValue != ENTITY) {
            //Every RESOLVE value is in Entity.  The only things we could get
            //passed that are "special" and not "RESOLVE values" are MType and
            //Entity itself
            result = MathExp.getTrueVarExp(null, this);
        }
        else if (valueTypeValue == CLS || valueTypeValue == ENTITY) {
            //MType and Entity aren't in anything
            throw new TypeMismatchException("Unexpected mathematical type: "
                    + value);
        }
        else if (valueTypeValue == null) {
            result =
                    getValidTypeConditions(value, value.getMathType(),
                            expected, EXP_VALUE_PATH);
        }
        else {
            //We're looking at an expression that defines a type
            result = getValidTypeConditions(valueTypeValue, expected);
        }

        return result;
    }

    /**
     * <p>
     * Returns the conditions under which <code>foundValue</code>, which is of
     * type <code>foundType</code>, could be demonstrated to be a member of
     * <code>expected</code>. Individual paths are tested using the given
     * <code>pathStrategy</code> (which lets us forget about what the java type
     * of <code>foundValue</code> is&mdash;only that it's a type
     * <code>pathStrategy</code> can handle.)
     * </p>
     *
     * <p>
     * The result is a series of disjuncts expressing possible situations under
     * which the <code>value</code> would be known to be in
     * <code>expected</code>. One or more of these disjuncts may be
     * <code>false</code>, but if one or more would have been <code>true</code>,
     * this method will simplify the result to simply <code>true</code>.
     * </p>
     *
     * <p>
     * If there is no known set of circumstances under which <code>value</code>
     * could be demonstrated a member of <code>expected</code> (i.e., if the
     * return value would simply be <code>false</code>), this method throws a
     * <code>TypeMismatchException</code>.
     * </p>
     *
     * @param foundValue The <code>RESOLVE</code> value to test for membership.
     * @param foundType The mathematical type of the <code>RESOLVE</code> value
     *        to test for membership.
     * @param expected A <code>RESOLVE</code> type against which to test
     *        membership.
     *
     * @return The conditions under which <code>value</code> could be
     *         demonstrated to be in <code>expected</code>.
     *
     * @throws TypeMismatchException If there are no known conditions under
     *         which <code>value</code> could be demonstrated to be in
     *         <code>expected</code>.
     */
    private <V> Exp getValidTypeConditions(V foundValue, MTType foundType,
            MTType expected, NodePairPathStrategy<V> pathStrategy)
            throws TypeMismatchException {
        if (foundType == null) {
            throw new IllegalArgumentException(foundValue + " has no type.");
        }

        Map<MTType, Map<String, MTType>> potentialFoundNodes =
                getSyntacticSubtypesWithRelationships(foundType);
        Map<MTType, Map<String, MTType>> potentialExpectedNodes =
                getSyntacticSubtypesWithRelationships(expected);

        Exp result = MathExp.getFalseVarExp(null, this);

        Exp newCondition;

        Iterator<Map.Entry<MTType, Map<String, MTType>>> expectedEntries;
        Iterator<Map.Entry<MTType, Map<String, MTType>>> foundEntries =
                potentialFoundNodes.entrySet().iterator();
        Map.Entry<MTType, Map<String, MTType>> foundEntry, expectedEntry;

        boolean foundPath = false;

        //If foundType equals expected, we're done
        boolean foundTrivialPath = foundType.equals(expected);

        while (!foundTrivialPath && foundEntries.hasNext()) {
            foundEntry = foundEntries.next();

            expectedEntries = potentialExpectedNodes.entrySet().iterator();

            while (!foundTrivialPath && expectedEntries.hasNext()) {

                expectedEntry = expectedEntries.next();

                try {
                    newCondition =
                            getPathConditions(foundValue, foundEntry,
                                    expectedEntry, pathStrategy);

                    foundPath =
                            foundPath | !MathExp.isLiteralFalse(newCondition);

                    foundTrivialPath = MathExp.isLiteralTrue(newCondition);

                    result =
                            MathExp.formDisjunct(newCondition.getLocation(),
                                    newCondition, result);
                }
                catch (TypeMismatchException e) {}
            }
        }

        if (foundTrivialPath) {
            result = MathExp.getTrueVarExp(null, this);
        }
        else if (!foundPath) {
            throw new TypeMismatchException("No path found!");
        }

        return result;
    }

    /**
     * <p>This method returns the set of universally quantified variables in the current scope.</p>
     *
     * @param source Source mathematical type.
     * @param destination Destination mathematical type.
     * @param environment The searching scope.
     * @param sourceCanonicalResult Canonical results from the source type.
     * @param destinationCanonicalResult Canonical results from the destination type.
     *
     * @return Set containing universal variable names.
     */
    private static Set<String> getUniversallyQuantifiedVariables(MTType source, MTType destination, Scope environment,
            CanonicalizationResult sourceCanonicalResult, CanonicalizationResult destinationCanonicalResult) {
        Set<String> unboundTypeClosure = new HashSet<>();
        Set<String> newUnboundTypes = new HashSet<>();
        Set<String> nextBatch;

        UnboundTypeAccumulator uta = new UnboundTypeAccumulator(environment);
        source.accept(uta);
        destination.accept(uta);
        nextBatch = uta.getFinalUnboundNamedTypes();

        try {
            while (!nextBatch.isEmpty()) {
                newUnboundTypes.clear();
                newUnboundTypes.addAll(nextBatch);
                nextBatch.clear();

                for (String newUnboundType : newUnboundTypes) {
                    //If it wasn't a MathSymbolEntry, the type checker would
                    //already have bombed
                    MathSymbolEntry entry =
                            (MathSymbolEntry) environment
                                    .queryForOne(new UnqualifiedNameQuery(
                                            newUnboundType));

                    MTType type = entry.getType();
                    uta = new UnboundTypeAccumulator(environment);
                    type.accept(uta);
                    nextBatch.addAll(uta.getFinalUnboundNamedTypes());
                }

                unboundTypeClosure.addAll(newUnboundTypes);
                nextBatch.removeAll(newUnboundTypes);
            }
        }
        catch (NoSuchSymbolException | DuplicateSymbolException se) {
            //This shouldn't be possible, the type checker would already have
            //bombed
            throw new RuntimeException(se);
        }

        //Make sure all those variables get bound
        Set<String> remaining = new HashSet<>(unboundTypeClosure);
        remaining.removeAll(sourceCanonicalResult.canonicalToEnvironmental
                .values());
        remaining.removeAll(destinationCanonicalResult.canonicalToEnvironmental
                .values());
        if (!remaining.isEmpty()) {
            throw new IllegalArgumentException("The following universal "
                    + "type variables will not be bound: " + remaining);
        }

        return unboundTypeClosure;
    }

    /**
     * <p>An helper method that inverts entries in a map.</p>
     *
     * @param original The original map.
     * @param <K> The class associated with the map's keys.
     * @param <V> The class associated with the map's values.
     *
     * @return The inverted map.
     */
    private static <K, V> Map<V, List<K>> invertMap(Map<K, V> original) {
        Map<V, List<K>> result = new HashMap<>();

        V entryValue;
        List<K> valueKeyList;
        for (Map.Entry<K, V> entry : original.entrySet()) {
            entryValue = entry.getValue();

            valueKeyList = result.get(entryValue);
            if (valueKeyList == null) {
                valueKeyList = new LinkedList<>();
                result.put(entryValue, valueKeyList);
            }

            valueKeyList.add(entry.getKey());
        }

        return result;
    }

    /**
     * <p>An helper method that replaces {@link TypeRelationshipPredicate TypeRelationshipPredicates}.</p>
     *
     * @param original The original list.
     * @param substitutions A map of substitutions.
     *
     * @return A modified list with the substitutions.
     */
    private static List<TypeRelationshipPredicate> replaceInPredicates(
            List<TypeRelationshipPredicate> original,
            Map<String, String> substitutions) {
        List<TypeRelationshipPredicate> result = new LinkedList<>();

        for (TypeRelationshipPredicate p : original) {
            result.add(p.replaceUnboundVariablesInTypes(substitutions));
        }

        return result;
    }

    /**
     * <p>An helper method that invokes the {@link Exp#substitute(Map)} method
     * and takes care of any potential exceptions.</p>
     *
     * @param original Original expression.
     * @param replacements A map of replacement expressions.
     * @param environmentalToExemplar A map of environmental names to exemplar names.
     *
     * @return The modified expression.
     */
    private Exp safeVariableNameUpdate(Exp original, Map<Exp, Exp> replacements,
                                       Map<String, String> environmentalToExemplar) {
        MTType originalTypeValue = original.getMathTypeValue();

        original = original.substitute(replacements);

        if (original.getMathType() == null) {
            throw new RuntimeException("substitute() method for class "
                    + original.getClass() + " did not properly copy the math "
                    + "type of the object.");
        }

        if (originalTypeValue != null && original.getMathTypeValue() == null) {
            throw new RuntimeException("substitute() method for class "
                    + original.getClass() + " did not properly copy the math "
                    + "type value of the object.");
        }

        original =
                getCopyWithVariableNamesChanged(original,
                        environmentalToExemplar);

        //Straight math type is taken care of inside the above call, since the
        //math type is needed there, so no need to check it again here

        if (originalTypeValue != null && original.getMathTypeValue() == null) {
            throw new RuntimeException("copy() method for class "
                    + original.getClass() + " did not properly copy the math "
                    + "type value of the object.");
        }

        return original;
    }

    /**
     * <p>An helper method that updates entries in a map.</p>
     *
     * @param original The original map.
     * @param suffix The new suffix to be added to the map's key.
     * @param <T> The class associated with the map's values.
     *
     * @return The modified map.
     */
    private <T> Map<String, T> updateMapLabels(Map<String, T> original, String suffix) {
        Map<String, T> result = new HashMap<>();
        for (Map.Entry<String, T> entry : original.entrySet()) {
            result.put(entry.getKey() + suffix, entry.getValue());
        }

        return result;
    }

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>An helper class that helps establish canonicalization results for
     * a {@link MTType}.</p>
     */
    private class CanonicalizationResult {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /** <p>A mathematical type.</p> */
        final MTType canonicalType;

        /** <p>A list of established type relationships.</p> */
        final List<TypeRelationshipPredicate> predicates;

        /** <p>A map of conversions.</p> */
        final Map<String, String> canonicalToEnvironmental;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>This creates a canonicalization result for {@code canonicalType}.</p>
         *
         * @param canonicalType A mathematical type.
         * @param predicates A list of established type relationships.
         * @param canonicalToOriginal A map of conversions.
         */
        CanonicalizationResult(MTType canonicalType,
                List<TypeRelationshipPredicate> predicates,
                Map<String, String> canonicalToOriginal) {
            this.canonicalType = canonicalType;
            this.predicates = predicates;
            this.canonicalToEnvironmental = canonicalToOriginal;
        }

    }

    /**
     * <p>A strategy pattern interface for a class type {@code V}
     * that tests valid type conditions between a source and expected
     * {@link MTType MTTypes}.</p>
     *
     * @param <V> The class of objects to be tested.
     */
    private interface NodePairPathStrategy<V> {

        /**
         * <p>This method establishes a valid type conditions for {@code sourceValue}
         * using {@code sourceType}, {@code expectedType} and {@code bindings}.</p>
         *
         * @param sourceValue An object of type {@link V}.
         * @param sourceType The mathematical source type.
         * @param expectedType The mathematical expected type.
         * @param bindings Map of established type bindings.
         *
         * @return An {@link Exp} with the valid type conditions
         * between the types.
         *
         * @throws TypeMismatchException We cannot establish a type condition
         * between the types for {@code sourceValue}.
         */
        Exp getValidTypeConditionsBetween(V sourceValue, MTType sourceType,
                MTType expectedType, Map<String, MTType> bindings)
                throws TypeMismatchException;

    }

    /**
     * <p>An implementation of {@link NodePairPathStrategy} for {@link Exp}.</p>
     */
    private class ExpValuePathStrategy implements NodePairPathStrategy<Exp> {

        /**
         * <p>This method establishes a valid type conditions for {@code sourceValue}
         * using {@code sourceType}, {@code expectedType} and {@code bindings}.</p>
         *
         * @param sourceValue An {@link Exp}.
         * @param sourceType The mathematical source type.
         * @param expectedType The mathematical expected type.
         * @param bindings Map of established type bindings.
         *
         * @return An {@link Exp} with the valid type conditions
         * between the types.
         *
         * @throws TypeMismatchException We cannot establish a type condition
         * between the types for {@code sourceValue}.
         */
        @Override
        public final Exp getValidTypeConditionsBetween(Exp sourceValue,
                MTType sourceType, MTType expectedType,
                Map<String, MTType> bindings) throws TypeMismatchException {
            return myTypeNodes.get(sourceType).getValidTypeConditionsTo(
                    sourceValue, expectedType, bindings);
        }

    }

    /**
     * <p>An implementation of {@link NodePairPathStrategy} for {@link MTType}.</p>
     */
    private class MTTypeValuePathStrategy
            implements
                NodePairPathStrategy<MTType> {

        /**
         * <p>This method establishes a valid type conditions for {@code sourceValue}
         * using {@code sourceType}, {@code expectedType} and {@code bindings}.</p>
         *
         * @param sourceValue A {@link MTType}.
         * @param sourceType The mathematical source type.
         * @param expectedType The mathematical expected type.
         * @param bindings Map of established type bindings.
         *
         * @return An {@link Exp} with the valid type conditions
         * between the types.
         *
         * @throws TypeMismatchException We cannot establish a type condition
         * between the types for {@code sourceValue}.
         */
        @Override
        public final Exp getValidTypeConditionsBetween(MTType sourceValue,
                MTType sourceType, MTType expectedType,
                Map<String, MTType> bindings) throws TypeMismatchException {
            return myTypeNodes.get(sourceType).getValidTypeConditionsTo(
                    sourceValue, expectedType, bindings);
        }

    }

    /**
     * <p>This creates a {@link MTPowerclassApplication} type.</p>
     */
    private static class PowerclassApplicationFactory
            implements
                FunctionApplicationFactory {

        /**
         * <p>This method returns a {@link MTType} resulting from a
         * function application.</p>
         *
         * @param g The current type graph.
         * @param f The function to be applied.
         * @param calledAsName The name for this function application type.
         * @param arguments List of arguments for applying the function.
         *
         * @return A function application {@link MTType}.
         */
        @Override
        public final MTType buildFunctionApplication(TypeGraph g, MTFunction f,
                String calledAsName, List<MTType> arguments) {
            return new MTPowerclassApplication(g, arguments.get(0));
        }

    }

    /**
     * <p>This creates a {@link MTPowersetApplication} type.</p>
     */
    private static class PowersetApplicationFactory
            implements
                FunctionApplicationFactory {

        /**
         * <p>This method returns a {@link MTType} resulting from a
         * function application.</p>
         *
         * @param g The current type graph.
         * @param f The function to be applied.
         * @param calledAsName The name for this function application type.
         * @param arguments List of arguments for applying the function.
         *
         * @return A function application {@link MTType}.
         */
        @Override
        public final MTType buildFunctionApplication(TypeGraph g, MTFunction f,
                String calledAsName, List<MTType> arguments) {
            return new MTPowersetApplication(g, arguments.get(0));
        }

    }

    /**
     * <p>This creates a {@link MTUnion} type.</p>
     */
    private static class UnionApplicationFactory
            implements
                FunctionApplicationFactory {

        /**
         * <p>This method returns a {@link MTType} resulting from a
         * function application.</p>
         *
         * @param g The current type graph.
         * @param f The function to be applied.
         * @param calledAsName The name for this function application type.
         * @param arguments List of arguments for applying the function.
         *
         * @return A function application {@link MTType}.
         */
        @Override
        public final MTType buildFunctionApplication(TypeGraph g, MTFunction f,
                String calledAsName, List<MTType> arguments) {
            return new MTUnion(g, arguments);
        }

    }

    /**
     * <p>This creates a {@link MTIntersect} type.</p>
     */
    private static class IntersectApplicationFactory
            implements
                FunctionApplicationFactory {

        /**
         * <p>This method returns a {@link MTType} resulting from a
         * function application.</p>
         *
         * @param g The current type graph.
         * @param f The function to be applied.
         * @param calledAsName The name for this function application type.
         * @param arguments List of arguments for applying the function.
         *
         * @return A function application {@link MTType}.
         */
        @Override
        public final MTType buildFunctionApplication(TypeGraph g, MTFunction f,
                String calledAsName, List<MTType> arguments) {
            return new MTIntersect(g, arguments);
        }

    }

    /**
     * <p>This creates a {@link MTFunction} type.</p>
     */
    private static class FunctionConstructorApplicationFactory
            implements
                FunctionApplicationFactory {

        /**
         * <p>This method returns a {@link MTType} resulting from a
         * function application.</p>
         *
         * @param g The current type graph.
         * @param f The function to be applied.
         * @param calledAsName The name for this function application type.
         * @param arguments List of arguments for applying the function.
         *
         * @return A function application {@link MTType}.
         */
        @Override
        public final MTType buildFunctionApplication(TypeGraph g, MTFunction f,
                String calledAsName, List<MTType> arguments) {
            return new MTFunction(g, arguments.get(1), arguments.get(0));
        }

    }

    /**
     * <p>This creates a {@link MTCartesian} type.</p>
     */
    private static class CartesianProductApplicationFactory
            implements
                FunctionApplicationFactory {

        /**
         * <p>This method returns a {@link MTType} resulting from a
         * function application.</p>
         *
         * @param g The current type graph.
         * @param f The function to be applied.
         * @param calledAsName The name for this function application type.
         * @param arguments List of arguments for applying the function.
         *
         * @return A function application {@link MTType}.
         */
        @Override
        public final MTType buildFunctionApplication(TypeGraph g, MTFunction f,
                String calledAsName, List<MTType> arguments) {
            return new MTCartesian(g,
                    new MTCartesian.Element(arguments.get(0)),
                    new MTCartesian.Element(arguments.get(1)));
        }

    }

    /**
     * <p>An helper class that indicates an established type relationship
     * between two {@link MTType MTTypes}.</p>
     */
    private static class EstablishedRelationship {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /** <p>The mathematical types that has been established a type relationship.</p> */
        private final MTType myType1, myType2;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>This constructs an object that indicates that we have established a
         * type relationship between {@code t1} and {@code t2}.</p>
         *
         * @param t1 A mathematical type.
         * @param t2 Another mathematical type.
         */
        EstablishedRelationship(MTType t1, MTType t2) {
            myType1 = t1;
            myType2 = t2;
        }

        // ===========================================================
        // Public Methods
        // ===========================================================

        /**
         * <p>This method overrides the default {@code hashCode} method implementation
         * for the {@code EstablishedRelationship} class.</p>
         *
         * @return The hash code associated with the object.
         */
        @Override
        public final int hashCode() {
            return myType1.hashCode() * 31 + myType2.hashCode();
        }

        /**
         * <p>This method overrides the default {@code equals} method implementation
         * to ensure that we have a correctly established type relationship.</p>
         *
         * @param o Object to be compared.
         *
         * @return {@code true} if all the fields are equal, {@code false} otherwise.
         */
        @Override
        public final boolean equals(Object o) {
            boolean result = o instanceof EstablishedRelationship;

            if (result) {
                EstablishedRelationship oAsER = (EstablishedRelationship) o;
                result =
                        myType1.equals(oAsER.myType1)
                                && myType2.equals(oAsER.myType2);
            }

            return result;
        }

    }

}