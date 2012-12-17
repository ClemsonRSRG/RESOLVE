package edu.clemson.cs.r2jt.typereasoning;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.InfixExp;
import edu.clemson.cs.r2jt.absyn.TupleExp;
import edu.clemson.cs.r2jt.absyn.VarExp;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.mathtype.*;
import edu.clemson.cs.r2jt.population.MathPopulator;
import edu.clemson.cs.r2jt.utilities.HardCoded;
import edu.clemson.cs.r2jt.utilities.SourceErrorException;

import java.util.*;

/**
 * Represents a directed graph of types, where edges between types
 * indicate a possible coercion that the type checker can perform.
 */
public class TypeGraph {

    public final ExpValuePathStrategy EXP_VALUE_PATH =
            new ExpValuePathStrategy();
    public final MTTypeValuePathStrategy MTTYPE_VALUE_PATH =
            new MTTypeValuePathStrategy();

    public final MTType ELEMENT = new MTProper(this, "Element");
    public final MTType ENTITY = new MTProper(this, "Entity");
    public final MTProper MTYPE = new MTProper(this, null, true, "MType");

    public final MTProper SET = new MTProper(this, MTYPE, true, "SSet");
    public final MTProper BOOLEAN = new MTProper(this, MTYPE, false, "B");
    public final MTProper Z = new MTProper(this, MTYPE, false, "Z");
    public final MTProper ATOM = new MTProper(this, MTYPE, false, "Atom");
    public final MTProper VOID = new MTProper(this, MTYPE, false, "Void");
    public final MTProper EMPTY_SET =
            new MTProper(this, MTYPE, false, "Empty_Set");

    private final static FunctionApplicationFactory POWERTYPE_APPLICATION =
            new PowertypeApplicationFactory();
    private final static FunctionApplicationFactory UNION_APPLICATION =
            new UnionApplicationFactory();
    private final static FunctionApplicationFactory INTERSECT_APPLICATION =
            new IntersectApplicationFactory();
    private final static FunctionApplicationFactory FUNCTION_CONSTRUCTOR_APPLICATION =
            new FunctionConstructorApplicationFactory();
    private final static FunctionApplicationFactory CARTESIAN_PRODUCT_APPLICATION =
            new CartesianProductApplicationFactory();

    public final MTFunction POWERTYPE =
            new MTFunction(this, true, POWERTYPE_APPLICATION, MTYPE, MTYPE);
    public final MTFunction UNION =
            new MTFunction(this, UNION_APPLICATION, MTYPE, MTYPE, MTYPE);
    public final MTFunction INTERSECT =
            new MTFunction(this, INTERSECT_APPLICATION, MTYPE, MTYPE, MTYPE);
    public final MTFunction FUNCTION =
            new MTFunction(this, FUNCTION_CONSTRUCTOR_APPLICATION, MTYPE,
                    MTYPE, MTYPE);
    public final MTFunction CROSS =
            new MTFunction(this, CARTESIAN_PRODUCT_APPLICATION, MTYPE, MTYPE,
                    MTYPE);

    public final MTFunction AND =
            new MTFunction(this, BOOLEAN, BOOLEAN, BOOLEAN);
    public final MTFunction NOT = new MTFunction(this, BOOLEAN, BOOLEAN);

    private final HashMap<MTType, TypeNode> myTypeNodes;

    public TypeGraph() {
        this.myTypeNodes = new HashMap<MTType, TypeNode>();
    }

    private Map<MTType, Map<String, MTType>> getSyntacticSubtypesWithRelationships(
            MTType query) {

        Map<MTType, Map<String, MTType>> result =
                new HashMap<MTType, Map<String, MTType>>();

        Map<String, MTType> bindings;

        for (MTType potential : myTypeNodes.keySet()) {
            try {
                bindings = query.getSyntacticSubtypeBindings(potential);
                result.put(potential, new HashMap<String, MTType>(bindings));
            }
            catch (NoSolutionException nse) {}
        }

        return result;
    }

    /**
     * <p>Returns <code>true</code> <strong>iff</strong> every value in
     * <code>subtype</code> must necessarily be in <code>supertype</code>.</p>
     * 
     * @param subtype A type to test if it is subsumed by 
     *     <code>supertype</code>.
     * @param supertype A type to test if it subsumes <code>subtype</code>.
     *     
     * @return Returns <code>true</code> <strong>iff</strong> every value in
     *     <code>subtype</code> must necessarily be in <code>supertype</code>.
     */
    public boolean isSubtype(MTType subtype, MTType supertype) {
        boolean result;

        try {
            result =
                    supertype.equals(ENTITY) || supertype.equals(MTYPE)
                            || subtype.equals(supertype)
                            || subtype.isSyntacticSubtypeOf(supertype);
        }
        catch (NoSuchElementException nsee) {
            //Syntactic subtype checker freaks out (rightly) if there are
            //free variables in the expression, but the next check will deal
            //correctly with them.
            result = false;
        }

        if (!result) {
            result =
                    isKnownToBeIn(subtype, new MTPowertypeApplication(this,
                            supertype));
        }

        return result;
    }

    /**
     * <p>Returns <code>true</code> <strong>iff</strong> <code>value</code>
     * is known to definitely be a member of <code>expected</code>.</p>
     * 
     * <p>I.e., this is the same as asking if
     * <code>getValidTypeConditions(value, expected)</code> 1) doesn't throw an
     * exception and 2) returns a value for which <code>isLiteralTrue()</code>
     * returns <code>true</code>.</p>
     * 
     * @param value The <code>RESOLVE</code> value to test for membership. 
     * @param expected A <code>RESOLVE</code> type against which to test
     *                 membership.
     *                 
     * @return <code>true</code> <strong>iff</strong> <code>value</code> is
     *         definitely in <code>expected</code>.
     */
    public boolean isKnownToBeIn(Exp value, MTType expected) {
        boolean result;

        try {
            Exp conditions = getValidTypeConditions(value, expected);
            result = conditions.isLiteralTrue();
        }
        catch (TypeMismatchException e) {
            result = false;
        }

        return result;
    }

    /**
     * <p>Returns <code>true</code> <strong>iff</strong> <code>value</code>, 
     * which is known to identify a <strong>MType</strong>, is known to 
     * definitely be a member of <code>expected</code>.</p>
     * 
     * <p>I.e., this is the same as asking if
     * <code>getValidTypeConditions(value, expected)</code> 1) doesn't throw an
     * exception and 2) returns a value for which <code>isLiteralTrue()</code>
     * returns <code>true</code>.</p>
     * 
     * @param value The <code>RESOLVE</code> value to test for membership. 
     * @param expected A <code>RESOLVE</code> type against which to test
     *                 membership.
     *                 
     * @return <code>true</code> <strong>iff</strong> <code>value</code> is
     *         definitely in <code>expected</code>.
     */
    public boolean isKnownToBeIn(MTType value, MTType expected) {
        boolean result;

        //If the type of the given value is a subtype of the expected type, then
        //its value must necessarily be in the expected type.  Note we can't
        //reason about the type of MTYPE, so we exclude it
        result = (value != MTYPE) && isSubtype(value.getType(), expected);

        if (!result) {
            try {
                Exp conditions = getValidTypeConditions(value, expected);
                result = conditions.isLiteralTrue();
            }
            catch (TypeMismatchException e) {
                result = false;
            }
        }

        return result;
    }

    /**
     * <p>Returns the conditions under which <code>value</code> could be 
     * demonstrated to be a member of <code>expected</code>, given that 
     * <code>value</code> is known to be in <strong>MType</strong>.</p>
     * 
     * <p>The result is a series of disjuncts expressing possible situations 
     * under which the <code>value</code> would be known to be in 
     * <code>expected</code>.  One or more of these disjuncts may be 
     * <code>false</code>, but if one or more would have been <code>true</code>,
     * this method will simplify the result to simply <code>true</code>.</p>
     * 
     * <p>If there is no known set of circumstances under which 
     * <code>value</code> could be demonstrated a member of 
     * <code>expected</code> (i.e., if the return value would simply be
     * <code>false</code>), this method throws a 
     * <code>TypeMismatchException</code>.</p>
     * 
     * @param value The <code>RESOLVE</code> value to test for membership. 
     * @param expected A <code>RESOLVE</code> type against which to test
     *                 membership.
     *                 
     * @return The conditions under which <code>value</code> could be
     *         demonstrated to be in <code>expected</code>.
     *         
     * @throws TypeMismatchException If there are no known conditions under 
     *         which <code>value</code> could be demonstrated to be in 
     *         <code>expected</code>.
     */
    public Exp getValidTypeConditions(MTType value, MTType expected)
            throws TypeMismatchException {
        //See note in the getValidTypeConditionsTo() in TypeRelationship,
        //re: Lovecraftian nightmare-scape

        Exp result = getFalseVarExp();

        if (expected == MTYPE) {
            //Every MTType is in MType except for Entity and MType, itself
            result = getTrueVarExp();
        }
        else if (expected instanceof MTPowertypeApplication) {
            if (value.equals(EMPTY_SET)) {
                //The empty set is in all powertypes
                result = getTrueVarExp();
            }
            else {
                //If "expected" happens to be Power(t) for some t, we can 
                //"demote" value to an INSTANCE of itself (provided it is not
                //the empty set), and expected to just t
                MTPowertypeApplication expectedAsPowertypeApplication =
                        (MTPowertypeApplication) expected;

                DummyExp memberOfValue = new DummyExp(value);

                if (isKnownToBeIn(memberOfValue, expectedAsPowertypeApplication
                        .getArgument(0))) {

                    result = getTrueVarExp();
                }
            }
        }

        //If we've already established it statically, no need for further work
        if (!result.isLiteralTrue()) {
            //If we haven't...

            //At this stage, we've done everything safe and sensible that we can 
            //do if the value we're looking at exists outside Entity
            if (value == MTYPE || value == ENTITY) {
                throw new TypeMismatchException(null, expected);
            }

            try {
                Exp intermediateResult =
                        getValidTypeConditions(value, value.getType(),
                                expected, MTTYPE_VALUE_PATH);

                if (intermediateResult.isLiteralTrue()) {
                    result = intermediateResult;
                }
                else {
                    result = formDisjunct(result, intermediateResult);
                }
            }
            catch (TypeMismatchException tme) {
                if (result.isLiteralFalse()) {
                    throw tme;
                }
            }
        }

        return result;
    }

    /**
     * <p>Returns the conditions under which <code>value</code> could be 
     * demonstrated to be a member of <code>expected</code>.</p>
     * 
     * <p>The result is a series of disjuncts expressing possible situations 
     * under which the <code>value</code> would be known to be in 
     * <code>expected</code>.  One or more of these disjuncts may be 
     * <code>false</code>, but if one or more would have been <code>true</code>,
     * this method will simplify the result to simply <code>true</code>.</p>
     * 
     * <p>If there is no known set of circumstances under which 
     * <code>value</code> could be demonstrated a member of 
     * <code>expected</code> (i.e., if the return value would simply be
     * <code>false</code>), this method throws a 
     * <code>TypeMismatchException</code>.</p>
     * 
     * @param value The <code>RESOLVE</code> value to test for membership. 
     * @param expected A <code>RESOLVE</code> type against which to test
     *                 membership.
     *                 
     * @return The conditions under which <code>value</code> could be
     *         demonstrated to be in <code>expected</code>.
     *         
     * @throws TypeMismatchException If there are no known conditions under 
     *         which <code>value</code> could be demonstrated to be in 
     *         <code>expected</code>.
     */
    public Exp getValidTypeConditions(Exp value, MTType expected)
            throws TypeMismatchException {

        Exp result;

        MTType valueTypeValue = value.getMathTypeValue();
        if (expected == ENTITY && valueTypeValue != MTYPE
                && valueTypeValue != ENTITY) {
            //Every RESOLVE value is in Entity.  The only things we could get
            //passed that are "special" and not "RESOLVE values" are MType and
            //Entity itself
            result = getTrueVarExp();
        }
        else if (valueTypeValue == MTYPE || valueTypeValue == ENTITY) {
            //MType and Entity aren't in anything
            throw new TypeMismatchException(null, expected);
        }
        else if (valueTypeValue == null) {

            //expected = bindGenericSlots(value, expected);

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

    /*private static MTType bindGenericSlots(Exp value, MTType expected) {
    	
    	Map<String, MTType> bindings = new HashMap<String, MTType>();
    	bindGenericSlots(value, expected, bindings);
    	
    	
    }*/

    private void bindGenericSlots(Exp value, MTType expected,
            Map<String, MTType> bindings) {

        if (expected instanceof MTCartesian) {
            MTCartesian expectedAsMTCartesian = (MTCartesian) expected;

            if (value instanceof TupleExp) {
                TupleExp valueAsTupleExp = (TupleExp) value;

                int expectedCount = expectedAsMTCartesian.size();
                if (expectedCount != valueAsTupleExp.getSize()) {
                    throw new IllegalArgumentException();
                }

                Exp subValue;
                String tag;
                MTType subType;
                for (int i = 0; i < expectedCount; i++) {
                    tag = expectedAsMTCartesian.getTag(i);
                    subType = expectedAsMTCartesian.getFactor(i);
                    subValue = valueAsTupleExp.getField(i);

                    if (subType.isKnownToContainOnlyMTypes() && tag != null) {
                        if (bindings.containsKey(tag)) {
                            if (!this.isSubtype(subValue.getMathType(),
                                    bindings.get(tag))) {
                                throw new IllegalArgumentException();
                            }
                        }
                        else {
                            if (subValue.getMathTypeValue() == null) {
                                throw new IllegalArgumentException();
                            }

                            bindings.put(tag, subValue.getMathTypeValue());
                        }
                    }
                    else {
                        bindGenericSlots(subValue, subType, bindings);
                    }
                }
            }
            else {
                throw new IllegalArgumentException();
            }
        }
    }

    /**
     * <p>Returns the conditions under which <code>foundValue</code>, which is 
     * of type <code>foundType</code>, could be	demonstrated to be a member of 
     * <code>expected</code>.  Individual paths are tested using the given 
     * <code>pathStrategy</code> (which lets us forget about what the java
     * type of <code>foundValue</code> is&mdash;only that it's a type 
     * <code>pathStrategy</code> can handle.)</p>
     * 
     * <p>The result is a series of disjuncts expressing possible situations 
     * under which the <code>value</code> would be known to be in 
     * <code>expected</code>.  One or more of these disjuncts may be 
     * <code>false</code>, but if one or more would have been <code>true</code>,
     * this method will simplify the result to simply <code>true</code>.</p>
     * 
     * <p>If there is no known set of circumstances under which 
     * <code>value</code> could be demonstrated a member of 
     * <code>expected</code> (i.e., if the return value would simply be
     * <code>false</code>), this method throws a 
     * <code>TypeMismatchException</code>.</p>
     * 
     * @param foundValue The <code>RESOLVE</code> value to test for membership.
     * @param foundType The mathematical type of the <code>RESOLVE</code> value
     *     to test for membership. 
     * @param expected A <code>RESOLVE</code> type against which to test
     *     membership.
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

        Exp result = getFalseVarExp();

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

                    foundPath = foundPath | !newCondition.isLiteralFalse();

                    foundTrivialPath = newCondition.isLiteralTrue();

                    result = formDisjunct(newCondition, result);
                }
                catch (TypeMismatchException e) {}
            }
        }

        if (foundTrivialPath) {
            result = getTrueVarExp();
        }
        else if (!foundPath) {
            throw new TypeMismatchException(expected, foundType);
        }

        return result;
    }

    /**
     * <p>Returns the conditions required to establish that 
     * <code>foundValue</code> is a member of the type represented by 
     * <code>expectedEntry</code> along the path from <code>foundEntry</code> to
     * <code>expectedEntry</code>.  If no such conditions exist (i.e., if the
     * conditions would be <code>false</code>), throws a 
     * <code>TypeMismatchException</code>.</p>
     * 
     * @param foundValue The value we'd like to establish is in the type 
     *     represented by <code>expectedEntry</code>.
     * @param foundEntry A node in the type graph of which 
     *     <code>foundValue</code> is a syntactic subtype.
     * @param expectedEntry A node in the type graph of which representing a 
     *     type in which we would like to establish <code>foundValue</code> 
     *     resides.
     * @param pathStrategy The strategy for following the path between 
     *     <code>foundEntry</code> and <code>expectedEntry</code>.
     *     
     * @return The conditions under which the path can be followed.
     * 
     * @throws TypeMismatchException If the conditions under which the path can
     *     be followed would be <code>false</code>.
     */
    private <V> Exp getPathConditions(V foundValue,
            Map.Entry<MTType, Map<String, MTType>> foundEntry,
            Map.Entry<MTType, Map<String, MTType>> expectedEntry,
            NodePairPathStrategy<V> pathStrategy) throws TypeMismatchException {

        Map<String, MTType> combinedBindings = new HashMap<String, MTType>();

        combinedBindings.clear();
        combinedBindings.putAll(updateMapLabels(foundEntry.getValue(), "_s"));
        combinedBindings
                .putAll(updateMapLabels(expectedEntry.getValue(), "_d"));

        Exp newCondition =
                pathStrategy.getValidTypeConditionsBetween(foundValue,
                        foundEntry.getKey(), expectedEntry.getKey(),
                        combinedBindings);

        return newCondition;
    }

    /**
     * <p>Establishes that values binding to <code>bindingExpression</code> 
     * under the given environment may be considered to be of type 
     * <code>destination</code> assuming the proof obligations set forth in
     * <code>bindingCondition</code> are satisfied.</p>
     * 
     * <p><code>bindingExpression</code> must have a mathematical type set on 
     * it. I.e., it must be the case that 
     * <code>bindingExpression.getMathType() != null</code>.</p>
     * 
     * <p>Any <code>VarExp</code>s, <code>AbstractFunctionExp</code>s, and
     * <code>MTNamed</code> that appear in <code>bindingExpression</code>,
     * its associated mathematical type, or <code>destination</code> must be
     * bound under the given environment.</p>
     * 
     * <p>Conversely, any universally bound variables in the given environment 
     * must appear in one of <code>bindingExpression</code>, its associated 
     * mathematical type, or <code>destination</code>.  This gives the typing 
     * system a chance to provide concrete values to these "open" slots.</p>
     * 
     * @param bindingExpression A snippet of syntax tree describing the 
     *     structure of values covered by this new relationship.
     * @param destination The mathematical type that this new relationship 
     *     establishes values binding to <code>bindingExpression</code> inhabit.
     * @param bindingCondition Proof obligations that must be raised to 
     *     establish that a given value binding to 
     *     <code>bindingExpression</code> inhabits <code>destination</code>.
     * @param environment The environment under which 
     *     <code>bindingExpression</code>, <code>destination</code>, and
     *     <code>bindingCondition</code> should be evaluated.
     */
    public void addRelationship(Exp bindingExpression, MTType destination,
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
            bindingCondition = getTrueVarExp();
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
        Map<Exp, Exp> replacements = new HashMap<Exp, Exp>();
        for (Map.Entry<String, String> entry : environmentalToExemplar
                .entrySet()) {

            replacements.put(new VarExp(null, null, new PosSymbol(null, Symbol
                    .symbol(entry.getKey()))), new VarExp(null, null,
                    new PosSymbol(null, Symbol.symbol(entry.getValue()))));
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

        //We'd like to force the presence of the destination node
        getTypeNode(destinationCanonicalResult.canonicalType);

        MathPopulator.emitDebug("Added relationship to type node ["
                + sourceCanonicalResult.canonicalType + "]: " + relationship);
    }

    private Exp safeVariableNameUpdate(Exp original,
            Map<Exp, Exp> replacements,
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
                new LinkedList<TypeRelationshipPredicate>();
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

    private static Map<String, String> getEnvironmentalToExemplar(
            Set<String> universalVariableNames,
            Map<String, List<String>> sourceEnvironmentalToCanonical,
            Map<String, List<String>> destinationEnvironmentalToCanonical) {

        Map<String, String> environmentalToExemplar =
                new HashMap<String, String>();
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

    private static Set<String> getUniversallyQuantifiedVariables(MTType source,
            MTType destination, Scope environment,
            CanonicalizationResult sourceCanonicalResult,
            CanonicalizationResult destinationCanonicalResult) {

        Set<String> unboundTypeClosure = new HashSet<String>();
        Set<String> newUnboundTypes = new HashSet<String>();
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
        catch (NoSuchSymbolException nsse) {
            //This shouldn't be possible, the type checker would already have
            //bombed
            throw new RuntimeException(nsse);
        }
        catch (DuplicateSymbolException dse) {
            //This shouldn't be possible, the type checker would already have
            //bombed
            throw new RuntimeException(dse);
        }

        //Make sure all those variables get bound
        Set<String> remaining = new HashSet<String>(unboundTypeClosure);
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

    private TypeNode getTypeNode(MTType t) {
        TypeNode result = myTypeNodes.get(t);

        if (result == null) {
            result = new TypeNode(this, t);
            myTypeNodes.put(t, result);
        }

        return result;
    }

    public Exp getCopyWithVariableNamesChanged(Exp original,
            Map<String, String> substitutions) {

        Exp result = Exp.copy(original);

        if (result.getMathType() == null) {
            throw new RuntimeException("copy() method for class "
                    + original.getClass() + " did not properly copy the math "
                    + "type of the object.");
        }

        result.setMathType(getCopyWithVariableNamesChanged(
                result.getMathType(), substitutions));

        List<Exp> children = result.getSubExpressions();
        int childCount = children.size();
        for (int childIndex = 0; childIndex < childCount; childIndex++) {
            result.setSubExpression(childIndex,
                    getCopyWithVariableNamesChanged(children.get(childIndex),
                            substitutions));
        }

        return result;
    }

    public MTType getCopyWithVariableNamesChanged(MTType original,
            Map<String, String> substitutions) {
        VariableReplacingVisitor renamer =
                new VariableReplacingVisitor(substitutions, this);
        original.accept(renamer);
        return renamer.getFinalExpression();
    }

    public static MTType getCopyWithVariablesSubstituted(MTType original,
            Map<String, MTType> substitutions) {
        VariableReplacingVisitor renamer =
                new VariableReplacingVisitor(substitutions);
        original.accept(renamer);
        return renamer.getFinalExpression();
    }

    public static <T extends Exp> T getCopyWithVariablesSubstituted(T original,
            Map<String, MTType> substitutions) {

        @SuppressWarnings("unchecked")
        T result = (T) Exp.copy(original);
        result.setMathType(result.getMathType()
                .getCopyWithVariablesSubstituted(substitutions));

        List<Exp> children = result.getSubExpressions();
        int childCount = children.size();
        for (int childIndex = 0; childIndex < childCount; childIndex++) {
            result.setSubExpression(childIndex, TypeGraph
                    .getCopyWithVariablesSubstituted(children.get(childIndex),
                            substitutions));
        }

        return result;
    }

    private static List<TypeRelationshipPredicate> replaceInPredicates(
            List<TypeRelationshipPredicate> original,
            Map<String, String> substitutions) {

        List<TypeRelationshipPredicate> result =
                new LinkedList<TypeRelationshipPredicate>();

        for (TypeRelationshipPredicate p : original) {
            result.add(p.replaceUnboundVariablesInTypes(substitutions));
        }

        return result;
    }

    private static <K, V> Map<V, List<K>> invertMap(Map<K, V> original) {
        Map<V, List<K>> result = new HashMap<V, List<K>>();

        V entryValue;
        List<K> valueKeyList;
        for (Map.Entry<K, V> entry : original.entrySet()) {
            entryValue = entry.getValue();

            valueKeyList = result.get(entryValue);
            if (valueKeyList == null) {
                valueKeyList = new LinkedList<K>();
                result.put(entryValue, valueKeyList);
            }

            valueKeyList.add(entry.getKey());
        }

        return result;
    }

    private <T> Map<String, T> updateMapLabels(Map<String, T> original,
            String suffix) {

        Map<String, T> result = new HashMap<String, T>();
        for (Map.Entry<String, T> entry : original.entrySet()) {
            result.put(entry.getKey() + suffix, entry.getValue());
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        Set<MTType> keys = myTypeNodes.keySet();
        Iterator<MTType> iter = keys.iterator();
        while (iter.hasNext()) {
            str.append(myTypeNodes.get(iter.next()).toString());

        }

        return str.toString();
    }

    private CanonicalizationResult canonicalize(MTType t, Scope environment,
            String suffix) {

        CanonicalizingVisitor canonicalizer =
                new CanonicalizingVisitor(this, environment, suffix);

        t.accept(canonicalizer);

        return new CanonicalizationResult(canonicalizer.getFinalExpression(),
                canonicalizer.getTypePredicates(), canonicalizer
                        .getCanonicalToEnvironmentOriginalMapping());
    }

    public VarExp getNothingExp() {
        Symbol nothingSym = Symbol.symbol("nothing");
        VarExp nothingExp = new VarExp();
        PosSymbol nothingPosSym = new PosSymbol();
        nothingPosSym.setSymbol(nothingSym);
        nothingExp.setName(nothingPosSym);
        nothingExp.setMathType(VOID);
        return nothingExp;
    }

    public VarExp getTrueVarExp() {
        Symbol trueSym = Symbol.symbol("true");
        VarExp trueExp = new VarExp();
        PosSymbol truePosSym = new PosSymbol();
        truePosSym.setSymbol(trueSym);
        trueExp.setName(truePosSym);
        trueExp.setMathType(BOOLEAN);
        return trueExp;
    }

    public VarExp getFalseVarExp() {
        Symbol falseSym = Symbol.symbol("false");
        VarExp falseExp = new VarExp();
        PosSymbol falsePosSym = new PosSymbol();
        falsePosSym.setSymbol(falseSym);
        falseExp.setName(falsePosSym);
        falseExp.setMathType(BOOLEAN);
        return falseExp;
    }

    public Exp formDisjunct(Exp d1, Exp d2) {
        Symbol orSym = Symbol.symbol("or");
        PosSymbol orPosSym = new PosSymbol();
        orPosSym.setSymbol(orSym);

        InfixExp orExp = new InfixExp();
        orExp.setOpName(orPosSym);
        orExp.setLeft(d1);
        orExp.setRight(d2);

        orExp.setMathType(BOOLEAN);

        return orExp;
    }

    public InfixExp formConjunct(Exp d1, Exp d2) {
        Symbol andSym = Symbol.symbol("and");
        PosSymbol andPosSym = new PosSymbol();
        andPosSym.setSymbol(andSym);

        InfixExp orExp = new InfixExp();
        orExp.setOpName(andPosSym);
        orExp.setLeft(d1);
        orExp.setRight(d2);

        orExp.setMathType(BOOLEAN);

        return orExp;
    }

    private class CanonicalizationResult {

        public final MTType canonicalType;
        public final List<TypeRelationshipPredicate> predicates;
        public final Map<String, String> canonicalToEnvironmental;

        public CanonicalizationResult(MTType canonicalType,
                List<TypeRelationshipPredicate> predicates,
                Map<String, String> canonicalToOriginal) {
            this.canonicalType = canonicalType;
            this.predicates = predicates;
            this.canonicalToEnvironmental = canonicalToOriginal;
        }
    }

    private interface NodePairPathStrategy<V> {

        public Exp getValidTypeConditionsBetween(V sourceValue,
                MTType sourceType, MTType expectedType,
                Map<String, MTType> bindings) throws TypeMismatchException;
    }

    private class ExpValuePathStrategy implements NodePairPathStrategy<Exp> {

        @Override
        public Exp getValidTypeConditionsBetween(Exp sourceValue,
                MTType sourceType, MTType expectedType,
                Map<String, MTType> bindings) throws TypeMismatchException {

            return myTypeNodes.get(sourceType).getValidTypeConditionsTo(
                    sourceValue, expectedType, bindings);
        }
    }

    private class MTTypeValuePathStrategy
            implements
                NodePairPathStrategy<MTType> {

        @Override
        public Exp getValidTypeConditionsBetween(MTType sourceValue,
                MTType sourceType, MTType expectedType,
                Map<String, MTType> bindings) throws TypeMismatchException {

            return myTypeNodes.get(sourceType).getValidTypeConditionsTo(
                    sourceValue, expectedType, bindings);
        }
    }

    private static class PowertypeApplicationFactory
            implements
                FunctionApplicationFactory {

        @Override
        public MTType buildFunctionApplication(TypeGraph g, MTFunction f,
                String calledAsName, List<MTType> arguments) {
            return new MTPowertypeApplication(g, arguments.get(0));
        }
    }

    private static class UnionApplicationFactory
            implements
                FunctionApplicationFactory {

        @Override
        public MTType buildFunctionApplication(TypeGraph g, MTFunction f,
                String calledAsName, List<MTType> arguments) {
            return new MTUnion(g, arguments);
        }
    }

    private static class IntersectApplicationFactory
            implements
                FunctionApplicationFactory {

        @Override
        public MTType buildFunctionApplication(TypeGraph g, MTFunction f,
                String calledAsName, List<MTType> arguments) {
            return new MTUnion(g, arguments);
        }
    }

    private static class FunctionConstructorApplicationFactory
            implements
                FunctionApplicationFactory {

        @Override
        public MTType buildFunctionApplication(TypeGraph g, MTFunction f,
                String calledAsName, List<MTType> arguments) {
            return new MTFunction(g, arguments.get(1), arguments.get(0));
        }
    }

    private static class CartesianProductApplicationFactory
            implements
                FunctionApplicationFactory {

        @Override
        public MTType buildFunctionApplication(TypeGraph g, MTFunction f,
                String calledAsName, List<MTType> arguments) {
            return new MTCartesian(g,
                    new MTCartesian.Element(arguments.get(0)),
                    new MTCartesian.Element(arguments.get(1)));
        }
    }
}
