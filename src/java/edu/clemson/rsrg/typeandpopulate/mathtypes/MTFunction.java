/*
 * MTFunction.java
 * ---------------------------------
 * Copyright (c) 2023
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.typeandpopulate.mathtypes;

import edu.clemson.rsrg.absyn.declarations.mathdecl.MathDefinitionDec;
import edu.clemson.rsrg.absyn.declarations.variabledecl.MathVarDec;
import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.LambdaExp;
import edu.clemson.rsrg.typeandpopulate.exception.NoSolutionException;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeComparison;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.rsrg.typeandpopulate.typevisitor.TypeVisitor;
import edu.clemson.rsrg.typeandpopulate.utilities.FunctionApplicationFactory;
import java.util.*;

/**
 * <p>
 * A function type.
 * </p>
 *
 * @version 2.0
 */
public class MTFunction extends MTAbstract<MTFunction> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * An integer value that helps us retrieve the hashcode for this class.
     * </p>
     */
    private static final int BASE_HASH = "MTFunction".hashCode();

    /**
     * <p>
     * The default factory used to create applications of this function.
     * </p>
     */
    private static final FunctionApplicationFactory DEFAULT_FACTORY = new VanillaFunctionApplicationFactory();

    /**
     * <p>
     * In cases where myDomain is an instance of {@link MTCartesian}, the names of the original parameters are stored in
     * the tags of that cartesian product. However, when myDomain is another type, we represent a function with a SINGLE
     * PARAMETER and we have no way to embed the name of our parameter. In the latter case, this field will reflect the
     * parameter name (or be null if we represent a function with un-named parameters). In the former case, the value of
     * this field is undefined.
     * </p>
     */
    private final String mySingleParameterName;

    /**
     * <p>
     * The domain for this type.
     * </p>
     */
    private final MTType myDomain;

    /**
     * <p>
     * The range for this type.
     * </p>
     */
    private final MTType myRange;

    /**
     * <p>
     * The restriction flag for the elements in this type.
     * </p>
     */
    private final boolean myRestrictionFlag;

    /**
     * <p>
     * A factory used to create applications of this function.
     * </p>
     */
    private final FunctionApplicationFactory myFunctionApplicationFactory;

    /**
     * <p>
     * The list of components in this type.
     * </p>
     */
    private List<MTType> myComponents;

    /**
     * <p>
     * The list of parameter types in this type.
     * </p>
     */
    private List<MTType> myParamTypes;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a function type where the we have a single parameter.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param range
     *            The range for this type.
     * @param paramTypes
     *            The types for each parameter.
     * @param singleParameterName
     *            The string name for the single parameter.
     */
    public MTFunction(TypeGraph g, MTType range, List<MTType> paramTypes, String singleParameterName) {
        this(g, false, DEFAULT_FACTORY, range, Collections.singletonList(singleParameterName), paramTypes);
    }

    /**
     * <p>
     * This constructs a function type where the elements are not restricted.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param range
     *            The range for this type.
     * @param paramTypes
     *            The types for each parameter.
     */
    public MTFunction(TypeGraph g, MTType range, MTType... paramTypes) {
        this(g, false, range, paramTypes);
    }

    /**
     * <p>
     * This constructs a function type where the elements are not restricted.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param range
     *            The range for this type.
     * @param paramTypes
     *            The types for each parameter.
     */
    public MTFunction(TypeGraph g, MTType range, List<MTType> paramTypes) {
        this(g, false, range, paramTypes);
    }

    /**
     * <p>
     * This is constructs a function type from a {@link MathDefinitionDec}, where we assumes that {code d} has some
     * parameters obtained from {@link MathDefinitionDec#getParameters()}.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param d
     *            A {@link MathDefinitionDec}.
     */
    public MTFunction(TypeGraph g, MathDefinitionDec d) {
        this(g, false, DEFAULT_FACTORY, d.getReturnTy().getMathTypeValue(), getParamNames(d.getParameters()),
                getParamTypes(d.getParameters()));
    }

    /**
     * <p>
     * This constructs a function type from a {@link LambdaExp}.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param l
     *            A {@link LambdaExp}.
     */
    public MTFunction(TypeGraph g, LambdaExp l) {
        this(g, false, DEFAULT_FACTORY, l.getMathType(), getParamNames(l.getParameters()),
                getParamTypes(l.getParameters()));
    }

    /**
     * <p>
     * This constructs a function type where we use the default factory to construct any function applications for this
     * type.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param elementsRestrict
     *            A flag indicating if the elements are restricted or not.
     * @param range
     *            The range for this type.
     * @param paramTypes
     *            The types for each parameter.
     */
    public MTFunction(TypeGraph g, boolean elementsRestrict, MTType range, MTType... paramTypes) {
        this(g, elementsRestrict, DEFAULT_FACTORY, range, paramTypes);
    }

    /**
     * <p>
     * This is constructs a function type where we use the default factory to construct any function applications for
     * this type.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param elementsRestrict
     *            A flag indicating if the elements are restricted or not.
     * @param range
     *            The range for this type.
     * @param paramTypes
     *            The types for each parameter.
     */
    public MTFunction(TypeGraph g, boolean elementsRestrict, MTType range, List<MTType> paramTypes) {
        this(g, elementsRestrict, DEFAULT_FACTORY, range, paramTypes);
    }

    /**
     * <p>
     * This constructs a function type where the elements are not restricted.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param apply
     *            The function application factory to be used.
     * @param range
     *            The range for this type.
     * @param paramTypes
     *            The types for each parameter.
     */
    public MTFunction(TypeGraph g, FunctionApplicationFactory apply, MTType range, MTType... paramTypes) {
        this(g, false, apply, range, paramTypes);
    }

    /**
     * <p>
     * This is constructs a function type where the elements are not restricted.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param apply
     *            The function application factory to be used.
     * @param range
     *            The range for this type.
     * @param paramTypes
     *            The types for each parameter.
     */
    public MTFunction(TypeGraph g, FunctionApplicationFactory apply, MTType range, List<MTType> paramTypes) {
        this(g, false, apply, range, paramTypes);
    }

    /**
     * <p>
     * This constructs a function type with {@code null} names for each parameter.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param elementsRestrict
     *            A flag indicating if the elements are restricted or not.
     * @param apply
     *            The function application factory to be used.
     * @param range
     *            The range for this type.
     * @param paramTypes
     *            The types for each parameter.
     */
    public MTFunction(TypeGraph g, boolean elementsRestrict, FunctionApplicationFactory apply, MTType range,
            MTType... paramTypes) {
        this(g, elementsRestrict, apply, range, Arrays.asList(paramTypes));
    }

    /**
     * <p>
     * This constructs a function type with {@code null} names for each parameter.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param elementsRestrict
     *            A flag indicating if the elements are restricted or not.
     * @param apply
     *            The function application factory to be used.
     * @param range
     *            The range for this type.
     * @param paramTypes
     *            The types for each parameter.
     */
    public MTFunction(TypeGraph g, boolean elementsRestrict, FunctionApplicationFactory apply, MTType range,
            List<MTType> paramTypes) {
        this(g, elementsRestrict, apply, range, buildNullNameListOfEqualLength(paramTypes), paramTypes);
    }

    /**
     * <p>
     * This constructs a function type.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param elementsRestrict
     *            A flag indicating if the elements are restricted or not.
     * @param apply
     *            The function application factory to be used.
     * @param range
     *            The range for this type.
     * @param paramNames
     *            The names for each parameter.
     * @param paramTypes
     *            The types for each parameter.
     */
    private MTFunction(TypeGraph g, boolean elementsRestrict, FunctionApplicationFactory apply, MTType range,
            List<String> paramNames, List<MTType> paramTypes) {
        super(g);
        myParamTypes = paramTypes;
        if (paramNames.size() == 1) {
            mySingleParameterName = paramNames.get(0);
        } else {
            mySingleParameterName = null;
        }

        myDomain = buildParameterType(g, paramNames, paramTypes);
        myRange = range;
        myRestrictionFlag = elementsRestrict;

        List<MTType> components = new LinkedList<>();
        components.add(myDomain);
        components.add(myRange);
        myComponents = Collections.unmodifiableList(components);

        myFunctionApplicationFactory = apply;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method is the {@code accept()} method in a visitor pattern for invoking an instance of {@link TypeVisitor}.
     * </p>
     *
     * @param v
     *            A visitor for types.
     */
    @Override
    public final void accept(TypeVisitor v) {
        acceptOpen(v);

        v.beginChildren(this);

        myDomain.accept(v);
        myRange.accept(v);

        v.endChildren(this);

        acceptClose(v);
    }

    /**
     * <p>
     * This method implements the post-visit method for invoking an instance of {@link TypeVisitor}.
     * </p>
     *
     * @param v
     *            A visitor for types.
     */
    @Override
    public final void acceptClose(TypeVisitor v) {
        v.endMTFunction(this);
        v.endMTAbstract(this);
        v.endMTType(this);
    }

    /**
     * <p>
     * This method implements the pre-visit method for invoking an instance of {@link TypeVisitor}.
     * </p>
     *
     * @param v
     *            A visitor for types.
     */
    @Override
    public final void acceptOpen(TypeVisitor v) {
        v.beginMTType(this);
        v.beginMTAbstract(this);
        v.beginMTFunction(this);
    }

    /**
     * <p>
     * This method constructs a {@link MTType} to represent the list of parameters.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param paramTypes
     *            The types for each of the parameters.
     *
     * @return A {@link MTType} representing the parameters.
     */
    public static MTType buildParameterType(TypeGraph g, List<MTType> paramTypes) {
        return buildParameterType(g, buildNullNameListOfEqualLength(paramTypes), paramTypes);
    }

    /**
     * <p>
     * This method constructs a {@link MTType} to represent the list of parameters.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param paramNames
     *            The names for each of the parameters.
     * @param paramTypes
     *            The types for each of the parameters.
     *
     * @return A {@link MTType} representing the parameters.
     */
    public static MTType buildParameterType(TypeGraph g, List<String> paramNames, List<MTType> paramTypes) {
        MTType result;

        switch (paramTypes.size()) {
        case 0:
            result = g.VOID;
            break;
        case 1:
            result = paramTypes.get(0);
            break;
        default:
            List<MTCartesian.Element> elements = new LinkedList<>();

            Iterator<String> namesIter = paramNames.iterator();
            Iterator<MTType> typesIter = paramTypes.iterator();
            while (namesIter.hasNext()) {
                elements.add(new MTCartesian.Element(namesIter.next(), typesIter.next()));
            }

            result = new MTCartesian(g, elements);
        }

        return result;
    }

    /**
     * <p>
     * Takes a set of typed parameters as they would be passed to a function of this type, and returns a new function
     * type with the same number of parameters as this one, but any unbound type variables that appear as top level
     * parameters "filled in".
     * </p>
     *
     * <p>
     * So, for example, if this function type were {@code (T : MType, S : Str(R : MType), t : T) -> (T * S)} and you
     * were to provide the parameters {@code (Z, {true, false, false}, false)}, this method would return the function
     * type {@code (T : MType, S : Str(R : MType), t : Z) -> (Z * S)}. Note that the parameters in this example <em>do
     * not</em> type against the given function type, but that is irrelevant to this method. The unbound type variable
     * <code>T</code> would be matched to <code>Z</code> and replaced throughout, while the unbound type variable
     * <code>R</code> would not be bound, since it is not at the top level. Note for simplicity of implementation that
     * the top-level type parameter itself remains unchanged (i.e., in theory you could later pass a <em>different</em>
     * type to <code>T</code>, despite having already "decided" that <code>T</code> is <code>Z</code>. This shouldn't be
     * a problem in the normal type checking algorithm, but being certain not to abuse this is the client's
     * responsibility.
     * </p>
     *
     * <p>
     * If the parameters cannot be applied to this function type, either because of an inappropriate number of
     * parameters, or a parameter value offered that is not within the bounds of a type parameter, this function will
     * throw a <code>NoSolutionException</code>.
     * </p>
     *
     * @param parameters
     *            Some typed parameters.
     *
     * @return A version of this function type with any top-level type parameters filled in.
     */
    public final MTFunction deschematize(List<Exp> parameters) throws NoSolutionException {
        Map<String, MTType> concreteValues = null;

        if (myDomain.equals(myTypeGraph.VOID)) {
            if (!parameters.isEmpty()) {
                throw new NoSolutionException("Non-empty parameter list.", new IllegalStateException());
            }
        } else {
            concreteValues = new HashMap<>();

            if (myDomain instanceof MTCartesian) {
                MTCartesian domainAsMTCartesian = (MTCartesian) myDomain;

                int domainSize = domainAsMTCartesian.size();
                int parametersSize = parameters.size();

                if (domainSize != parametersSize) {
                    if (parametersSize == 1 && mySingleParameterName != null) {
                        deschematizeParameter(mySingleParameterName, myDomain, parameters.get(0), concreteValues);
                    } else {
                        throw new NoSolutionException("More than one parameter.", new IllegalArgumentException());
                    }
                } else {
                    for (int i = 0; i < domainSize; i++) {
                        deschematizeParameter(domainAsMTCartesian.getTag(i), domainAsMTCartesian.getFactor(i),
                                parameters.get(i), concreteValues);
                    }
                }
            } else {

                if (parameters.size() != 1) {
                    throw new NoSolutionException("Not one parameter.", new IllegalArgumentException());
                }

                deschematizeParameter(mySingleParameterName, myDomain, parameters.get(0), concreteValues);
            }
        }

        return (MTFunction) getCopyWithVariablesSubstituted(concreteValues);
    }

    /**
     * <p>
     * This method returns a new {@link MTType} from applying this function type.
     * </p>
     *
     * @param calledAsName
     *            The name to be used for this function application.
     * @param arguments
     *            The list of arguments to this function.
     *
     * @return A {@link MTType} resulting from the application.
     */
    public MTType getApplicationType(String calledAsName, List<MTType> arguments) {
        return myFunctionApplicationFactory.buildFunctionApplication(myTypeGraph, this, calledAsName, arguments);
    }

    /**
     * <p>
     * This method returns a list of {@link MTType}s that are part of this type.
     * </p>
     *
     * @return The list of {@link MTType}s in this function type.
     */
    @Override
    public final List<MTType> getComponentTypes() {
        return myComponents;
    }

    /**
     * <p>
     * This method returns the domain for this {@link MTFunction}.
     * </p>
     *
     * @return A {@link MTType} representing the function domain.
     */
    public final MTType getDomain() {
        return myDomain;
    }

    /**
     * <p>
     * This method returns the parameters as a string.
     * </p>
     *
     * @return A string representation of the parameters.
     */
    public final String getParamString() {
        String rString;
        if (myDomain.getClass().getSimpleName().equals("MTCartesian")) {
            MTCartesian dC = (MTCartesian) myDomain;
            rString = dC.getParamString();
        } else {
            rString = myDomain.toString();
        }

        return rString;
    }

    /**
     * <p>
     * This method returns the range for this {@link MTFunction}.
     * </p>
     *
     * @return A {@link MTType} representing the function range.
     */
    public final MTType getRange() {
        return myRange;
    }

    /**
     * <p>
     * This method returns the name for the parameter.
     * </p>
     *
     * @return A string representing the parameter name.
     */
    public final String getSingleParameterName() {
        return mySingleParameterName;
    }

    /**
     * <p>
     * Indicates that this type is known to contain only elements <em>that are themselves</em> types. Practically, this
     * answers the question, "can an instance of this type itself be used as a type?"
     * </p>
     *
     * @return {@code true} if it can, {@code false} otherwise.
     */
    @Override
    public final boolean isKnownToContainOnlyMTypes() {
        return false;
    }

    /**
     * <p>
     * Applies the given type comparison function to each of the expressions in <code>parameters</code>, returning
     * <code>true</code> <strong>iff</strong> the comparison returns true for each parameter.
     * </p>
     *
     * <p>
     * The comparison is guaranteed to be applied to the parameters in the order returned by <code>parameters</code>'
     * iterator, and thus the comparison may accumulate data about, for example, parameterized types as it goes.
     * However, if the comparison returns <code>false</code> for any individual parameter, then further comparison
     * behavior is undefined. That is, in this case this method will return <code>false</code> and the comparison may be
     * applied to none, some, or all of the remaining parameters.
     * </p>
     *
     * @param parameters
     *            List of {@link Exp}s.
     * @param comparison
     *            A comparator to be used for a map of {@link Exp}s to {@link MTType}s.
     *
     * @return {@code true} if the parameters match, {@code false} otherwise.
     */
    public final boolean parametersMatch(List<Exp> parameters, TypeComparison<Exp, MTType> comparison) {
        boolean result = false;

        if (myDomain == myTypeGraph.VOID) {
            result = (parameters.isEmpty());
        } else {
            if (myDomain instanceof MTCartesian) {
                MTCartesian domainAsMTCartesian = (MTCartesian) myDomain;

                int domainSize = domainAsMTCartesian.size();
                int parametersSize = parameters.size();

                result = (domainSize == parametersSize);

                if (result) {
                    int i = 0;
                    Exp parameter;
                    while (result && i < domainSize) {

                        parameter = parameters.get(i);
                        result = comparison.compare(parameter, parameter.getMathType(),
                                domainAsMTCartesian.getFactor(i));

                        i++;
                    }
                }
            }

            if (!result && (parameters.size() == 1)) {
                Exp parameter = parameters.get(0);
                result = comparison.compare(parameter, parameter.getMathType(), myDomain);
            }
        }

        return result;
    }

    /**
     * <p>
     * This method checks to see if the parameters in this {@link MTFunction} match the other {@link MTFunction}.
     * </p>
     *
     * @param other
     *            The other {@link MTFunction} to be compared.
     * @param comparison
     *            A comparator to be used for {@link MTType}s.
     *
     * @return {@code true} if the parameter types match, {@code false} otherwise.
     */
    public final boolean parameterTypesMatch(MTFunction other, Comparator<MTType> comparison) {
        MTType otherDomain = other.getDomain();

        boolean result;

        if (myDomain instanceof MTCartesian) {
            result = otherDomain instanceof MTCartesian;

            if (result) {
                MTCartesian domainAsMTCartesian = (MTCartesian) myDomain;
                MTCartesian otherDomainAsMTCartesian = (MTCartesian) otherDomain;

                int domainSize = domainAsMTCartesian.size();
                int otherDomainSize = otherDomainAsMTCartesian.size();

                result = (domainSize == otherDomainSize);

                if (result) {
                    int i = 0;
                    while (result && i < domainSize) {
                        result = (comparison.compare(domainAsMTCartesian.getFactor(i),
                                otherDomainAsMTCartesian.getFactor(i)) == 0);

                        i++;
                    }
                }
            }
        } else {
            result = (comparison.compare(myDomain, otherDomain) == 0);
        }

        return result;
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
        return "(" + myDomain.toString() + " -> " + myRange.toString() + ")";
    }

    /**
     * <p>
     * This method attempts to replace a component type at the specified index.
     * </p>
     *
     * @param index
     *            Index to a component type.
     * @param newType
     *            The {@link MTType} to replace the one in our component list.
     *
     * @return A new {@link MTFunction} with the type at the specified index replaced with {@code newType}.
     */
    @Override
    public final MTType withComponentReplaced(int index, MTType newType) {
        MTType newDomain = myDomain;
        MTType newRange = myRange;

        switch (index) {
        case 0:
            newDomain = newType;
            break;
        case 1:
            newRange = newType;
            break;
        default:
            throw new IndexOutOfBoundsException();
        }

        return new MTFunction(getTypeGraph(), myRestrictionFlag, newRange, newDomain);
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>
     * This is just a template method to <em>force</em> all concrete subclasses of {@link MTType} to implement
     * <code>hashCode()</code>, as the type resolution algorithm depends on it being implemented sensibly.
     * </p>
     *
     * @return A hashcode consistent with <code>equals()</code> and thus alpha-equivalency.
     */
    @Override
    protected final int getHashCode() {
        return BASE_HASH + (myDomain.hashCode() * 31) + myRange.hashCode();
    }

    // ===========================================================
    // Package Private Methods
    // ===========================================================

    /**
     * <p>
     * This method returns whether or not applying the function results in some kind of restriction.
     * </p>
     *
     * @return {@code true} if this function is restricted, {@code false} otherwise.
     */
    final boolean applicationResultsKnownToContainOnlyRestrictions() {
        return myRestrictionFlag;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * This is an helper method that builds an list of {@code null} elements of the same size as the original list.
     * </p>
     *
     * @param original
     *            List of original {@link MTType}s.
     *
     * @return A list containing {@code null}.
     */
    private static List<String> buildNullNameListOfEqualLength(List<MTType> original) {
        List<String> names = new LinkedList<>();
        for (int i = 0; i < original.size(); i++) {
            names.add(null);
        }

        return names;
    }

    /**
     * <p>
     * This is an helper method that helps deschematize a parameter.
     * </p>
     *
     * @param formalParameterName
     *            The formal name for the parameter.
     * @param formalParameterType
     *            The formal type for the parameter.
     * @param actualParameter
     *            The actual parameter.
     * @param accumulatedConcreteValues
     *            The map containing all the accumulated concrete types.
     *
     * @throws NoSolutionException
     *             {@code actualParameterMathTypeValue} not known to be in {@code formalParameterType}.
     */
    private void deschematizeParameter(String formalParameterName, MTType formalParameterType, Exp actualParameter,
            Map<String, MTType> accumulatedConcreteValues) throws NoSolutionException {
        formalParameterType = formalParameterType.getCopyWithVariablesSubstituted(accumulatedConcreteValues);

        if (formalParameterType.isKnownToContainOnlyMTypes()) {
            MTType actualParameterMathType = actualParameter.getMathType()
                    .getCopyWithVariablesSubstituted(accumulatedConcreteValues);
            MTType actualParameterMathTypeValue = actualParameter.getMathTypeValue();

            // YS - Not sure if this is the right fix, but below is the explanation for my logic:
            // If our actual parameter's math type is equal to the formal parameter type,
            // there is nothing to deschematize. We add it to our accumulated concrete values
            // and move on.
            // TODO: Is isKnownToBeIn(actualParameterMathType, formalParameterType) a condition we need to
            // consider?
            if (!actualParameterMathType.equals(formalParameterType)) {
                // YS - So we know the actual parameter's type doesn't match formal
                // parameter type. We then proceed to check if the actual parameter's math type
                // is a MTFunctionApplication.
                if (actualParameterMathType instanceof MTFunctionApplication) {
                    // YS - Now we check to see if this function application generates the formal
                    // parameter type. If yes, our actual parameter type value is simply the
                    // function application itself. Otherwise, we throw an error.
                    // TODO: Is isKnownToBeIn(appliedFunctionType.getRange(), formalParameterType) a condition
                    // we need to consider?
                    MTFunction appliedFunctionType = ((MTFunctionApplication) actualParameterMathType).getFunction();
                    if (!appliedFunctionType.getRange().equals(formalParameterType)) {
                        throw new NoSolutionException(
                                "Parameter has a function application that generated an unexpected type: "
                                        + appliedFunctionType.getRange(),
                                new IllegalArgumentException());
                    } else {
                        actualParameterMathTypeValue = actualParameterMathType;
                    }
                }
                // YS - We might need to check the case where the formalParameterType is a
                // MTFunctionApplication
                else if (formalParameterType instanceof MTFunctionApplication) {
                    // YS - Now we check to see if the function application can use the
                    // actualParameter's type. If yes, our actual parameter type value is simply the
                    // function application itself. Otherwise, we throw an error.
                    // TODO: Is isKnownToBeIn(actualParameterMathType, appliedFunctionType.getRange()) a
                    // condition we need to consider?
                    MTFunction appliedFunctionType = ((MTFunctionApplication) formalParameterType).getFunction();
                    if (!myTypeGraph.isKnownToBeIn(actualParameter, appliedFunctionType.getRange())) {
                        throw new NoSolutionException(
                                "Parameter has a function application that generated an unexpected type: "
                                        + appliedFunctionType.getRange(),
                                new IllegalArgumentException());
                    } else {
                        actualParameterMathTypeValue = formalParameterType;
                    }
                } else {
                    // YS - For all other math types, we check its type value. If the type value is null or
                    // is not known to be in the formal parameter type, it is an error.
                    // Otherwise we use the type value and add it as a new concrete value.
                    if (actualParameterMathTypeValue == null) {
                        throw new NoSolutionException("Parameter has a null type value.",
                                new IllegalArgumentException());
                    } else if (!myTypeGraph.isKnownToBeIn(actualParameterMathTypeValue, formalParameterType)) {
                        throw new NoSolutionException(
                                "Parameter is not known to be in: " + actualParameterMathTypeValue,
                                new IllegalArgumentException());
                    }
                }
            } else {
                actualParameterMathTypeValue = actualParameterMathType;
            }

            accumulatedConcreteValues.put(formalParameterName, actualParameterMathTypeValue);
        }
    }

    /**
     * <p>
     * This is an helper method that returns a list names corresponding to each of the {@link MathVarDec}s.
     * </p>
     *
     * @param params
     *            List of {@link MathVarDec}s.
     *
     * @return The list of names corresponding to each variable.
     */
    private static List<String> getParamNames(List<MathVarDec> params) {
        List<String> names = new LinkedList<>();

        for (MathVarDec d : params) {
            names.add(d.getName().getName());
        }

        return names;
    }

    /**
     * <p>
     * This is an helper method that returns a list {@link MTType}s corresponding to each of the {@link MathVarDec}s.
     * </p>
     *
     * @param params
     *            List of {@link MathVarDec}s.
     *
     * @return The list of {@link MTType} corresponding to each variable.
     */
    private static List<MTType> getParamTypes(List<MathVarDec> params) {
        List<MTType> names = new LinkedList<>();

        for (MathVarDec d : params) {
            names.add(d.getMathType());
        }

        return names;
    }

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>
     * This is the default implementation that can be used to build {@link MTFunctionApplication}s for this function
     * type.
     * </p>
     */
    private static class VanillaFunctionApplicationFactory implements FunctionApplicationFactory {

        /**
         * <p>
         * This method returns a {@link MTType} resulting from a function application.
         * </p>
         *
         * @param g
         *            The current type graph.
         * @param f
         *            The function to be applied.
         * @param calledAsName
         *            The name for this function application type.
         * @param arguments
         *            List of arguments for applying the function.
         *
         * @return A function application {@link MTType}.
         */
        @Override
        public MTType buildFunctionApplication(TypeGraph g, MTFunction f, String calledAsName, List<MTType> arguments) {
            return new MTFunctionApplication(g, f, calledAsName, arguments);
        }

    }

}
