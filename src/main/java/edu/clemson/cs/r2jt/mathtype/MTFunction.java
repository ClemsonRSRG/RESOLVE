package edu.clemson.cs.r2jt.mathtype;

import edu.clemson.cs.r2jt.absyn.DefinitionDec;
import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.MathVarDec;
import edu.clemson.cs.r2jt.typereasoning.TypeComparison;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MTFunction extends MTAbstract<MTFunction> {

    private static final int BASE_HASH = "MTFunction".hashCode();
    private static final FunctionApplicationFactory DEFAULT_FACTORY =
            new VanillaFunctionApplicationFactory();

    /**
     * <p>In cases where myDomain is an instance of MTCartesian, the names of
     * the original parameters are stored in the tags of that cartesian product.
     * However, when myDomain is another type, we represent a function with
     * a SINGLE PARAMETER and we have no way to embed the name of our parameter.
     * In the latter case, this field will reflect the parameter name (or be 
     * null if we represent a function with un-named parameters).  In the former
     * case, the value of this field is undefined.</p>
     */
    private final String mySingleParameterName;

    private final MTType myDomain;
    private final MTType myRange;
    private final boolean myRestrictionFlag;
    private final FunctionApplicationFactory myFunctionApplicationFactory;

    private List<MTType> myComponents;

    public MTFunction(TypeGraph g, MTType range, List<MTType> paramTypes,
            String singleParameterName) {
        this(g, false, DEFAULT_FACTORY, range, Collections
                .singletonList(singleParameterName), paramTypes);
    }

    public MTFunction(TypeGraph g, MTType range, MTType... paramTypes) {
        this(g, false, range, paramTypes);
    }

    public MTFunction(TypeGraph g, MTType range, List<MTType> paramTypes) {
        this(g, false, range, paramTypes);
    }

    /**
     * This assumes that d has some parameters in its .getParams().
     */
    public MTFunction(TypeGraph g, DefinitionDec d) {
        this(g, false, DEFAULT_FACTORY, d.getReturnTy().getMathTypeValue(),
                getParamNames(d.getParameters()), getParamTypes(d
                        .getParameters()));
    }

    public MTFunction(TypeGraph g, boolean elementsRestrict, MTType range,
            MTType... paramTypes) {

        this(g, false, DEFAULT_FACTORY, range, paramTypes);
    }

    public MTFunction(TypeGraph g, boolean elementsRestrict, MTType range,
            List<MTType> paramTypes) {

        this(g, false, DEFAULT_FACTORY, range, paramTypes);
    }

    public MTFunction(TypeGraph g, FunctionApplicationFactory apply,
            MTType range, MTType... paramTypes) {

        this(g, false, apply, range, paramTypes);
    }

    public MTFunction(TypeGraph g, FunctionApplicationFactory apply,
            MTType range, List<MTType> paramTypes) {

        this(g, false, apply, range, paramTypes);
    }

    public MTFunction(TypeGraph g, boolean elementsRestrict,
            FunctionApplicationFactory apply, MTType range,
            MTType... paramTypes) {

        this(g, elementsRestrict, apply, range, Arrays.asList(paramTypes));
    }

    public MTFunction(TypeGraph g, boolean elementsRestrict,
            FunctionApplicationFactory apply, MTType range,
            List<MTType> paramTypes) {

        this(g, elementsRestrict, apply, range,
                buildNullNameListOfEqualLength(paramTypes), paramTypes);
    }

    private MTFunction(TypeGraph g, boolean elementsRestrict,
            FunctionApplicationFactory apply, MTType range,
            List<String> paramNames, List<MTType> paramTypes) {

        super(g);

        if (paramNames.size() == 1) {
            mySingleParameterName = paramNames.get(0);
        }
        else {
            mySingleParameterName = null;
        }

        myDomain = buildParameterType(g, paramNames, paramTypes);
        myRange = range;
        myRestrictionFlag = elementsRestrict;

        List<MTType> components = new LinkedList<MTType>();
        components.add(myDomain);
        components.add(myRange);
        myComponents = Collections.unmodifiableList(components);

        myFunctionApplicationFactory = apply;
    }

    /**
     * <p>Takes a set of typed parameters as they would be passed to a function
     * of this type, and returns a new function type with the same number of
     * parameters as this one, but any unbound type variables that appear as
     * top level parameters "filled in".</p>
     * 
     * <p>So, for example, if this function type were 
     * <code>(T : MType, S : Str(R : MType), t : T) -> (T * S)</code> and you 
     * were to provide the parameters 
     * <code>(Z, {true, false, false}, false)</code>, this method would return 
     * the function type 
     * <code>(T : MType, S : Str(R : MType), t : Z) -> (Z * S)</code>. Note that
     * the parameters in this example <em>do not</em> type against the given
     * function type, but that is irrelevant to this method.  The unbound
     * type variable <code>T</code> would be matched to <code>Z</code> and 
     * replaced throughout, while the unbound type variable <code>S</code> would
     * not be bound, since it is not at the top level.  Note for simplicity of
     * implementation that the top-level type parameter itself remains unchanged
     * (i.e., in theory you could later pass a <em>different</em> type to 
     * <code>T</code>, despite having already "decided" that <code>T</code> is 
     * <code>Z</code>.  This shouldn't be a problem in the normal type checking 
     * algorithm, but being certain not to abuse this is the client's 
     * responsibility.</p>
     * 
     * <p>If the parameters cannot be applied to this function type, either
     * because of an inappropriate number of parameters, or a parameter value
     * offered that is not within the bounds of a type parameter, this function
     * will throw a <code>NoSolutionException</code>.</p>
     * 
     * @param parameters Some typed parameters.
     * 
     * @return A version of this function type with any top-level type 
     *         parameters filled in.
     */
    public MTFunction deschematize(List<Exp> parameters)
            throws NoSolutionException {

        Map<String, MTType> concreteValues = null;

        if (myDomain.equals(myTypeGraph.VOID)) {
            if (!parameters.isEmpty()) {
                throw new NoSolutionException();
            }
        }
        else {
            concreteValues = new HashMap<String, MTType>();

            if (myDomain instanceof MTCartesian) {
                MTCartesian domainAsMTCartesian = (MTCartesian) myDomain;

                int domainSize = domainAsMTCartesian.size();
                int parametersSize = parameters.size();

                if (domainSize != parametersSize) {
                    throw new NoSolutionException();
                }

                for (int i = 0; i < domainSize; i++) {
                    deschematizeParameter(domainAsMTCartesian.getTag(i),
                            domainAsMTCartesian.getFactor(i),
                            parameters.get(i), concreteValues);
                }
            }
            else {

                if (parameters.size() != 1) {
                    throw new NoSolutionException();
                }

                deschematizeParameter(mySingleParameterName, myDomain,
                        parameters.get(0), concreteValues);
            }
        }

        return (MTFunction) getCopyWithVariablesSubstituted(concreteValues);
    }

    private void deschematizeParameter(String formalParameterName,
            MTType formalParameterType, Exp actualParameter,
            Map<String, MTType> accumulatedConcreteValues)
            throws NoSolutionException {

        formalParameterType =
                formalParameterType
                        .getCopyWithVariablesSubstituted(accumulatedConcreteValues);

        if (formalParameterType.isKnownToContainOnlyMTypes()) {
            MTType actualParameterMathTypeValue =
                    actualParameter.getMathTypeValue();

            if (actualParameterMathTypeValue == null
                    || !myTypeGraph.isKnownToBeIn(actualParameterMathTypeValue,
                            formalParameterType)) {
                throw new NoSolutionException();
            }

            accumulatedConcreteValues.put(formalParameterName, actualParameter
                    .getMathTypeValue());
        }
    }

    /**
     * <p>Applies the given type comparison function to each of the expressions
     * in <code>parameters</code>, returning <code>true</code> 
     * <strong>iff</strong> the comparison returns true for each parameter.</p>
     * 
     * <p>The comparison is guaranteed to be applied to the parameters in the
     * order returned by <code>parameters</code>' iterator, and thus the 
     * comparison may accumulate data about, for example, parameterized types
     * as it goes.  However, if the comparison returns <code>false</code> for
     * any individual parameter, then further comparison behavior is undefined.
     * That is, in this case this method will return <code>false</code> and the 
     * comparison may be applied to none, some, or all of the remaining 
     * parameters.</p>
     * 
     * @param parameters
     * @param comparison
     * @return 
     */
    public boolean parametersMatch(List<Exp> parameters,
            TypeComparison<Exp, MTType> comparison) {

        boolean result = false;

        if (myDomain == myTypeGraph.VOID) {
            result = (parameters.isEmpty());
        }
        else {
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
                        result =
                                comparison.compare(parameter, parameter
                                        .getMathType(), domainAsMTCartesian
                                        .getFactor(i));

                        i++;
                    }
                }
            }

            if (!result && (parameters.size() == 1)) {
                Exp parameter = parameters.get(0);
                result =
                        comparison.compare(parameter, parameter.getMathType(),
                                myDomain);
            }
        }

        return result;
    }

    public boolean parameterTypesMatch(MTFunction other,
            Comparator<MTType> comparison) {

        MTType otherDomain = other.getDomain();

        boolean result;

        if (myDomain instanceof MTCartesian) {
            result = otherDomain instanceof MTCartesian;

            if (result) {
                MTCartesian domainAsMTCartesian = (MTCartesian) myDomain;
                MTCartesian otherDomainAsMTCartesian =
                        (MTCartesian) otherDomain;

                int domainSize = domainAsMTCartesian.size();
                int otherDomainSize = otherDomainAsMTCartesian.size();

                result = (domainSize == otherDomainSize);

                if (result) {
                    int i = 0;
                    while (result && i < domainSize) {
                        result =
                                (comparison.compare(domainAsMTCartesian
                                        .getFactor(i), otherDomainAsMTCartesian
                                        .getFactor(i)) == 0);

                        i++;
                    }
                }
            }
        }
        else {
            result = (comparison.compare(myDomain, otherDomain) == 0);
        }

        return result;
    }

    private static List<String> getParamNames(
            edu.clemson.cs.r2jt.collections.List<MathVarDec> params) {

        List<String> names = new LinkedList<String>();

        for (MathVarDec d : params) {
            names.add(d.getName().getName());
        }

        return names;
    }

    private static List<MTType> getParamTypes(
            edu.clemson.cs.r2jt.collections.List<MathVarDec> params) {

        List<MTType> names = new LinkedList<MTType>();

        for (MathVarDec d : params) {
            names.add(d.getMathType());
        }

        return names;
    }

    private static List<String> buildNullNameListOfEqualLength(
            List<MTType> original) {

        List<String> names = new LinkedList<String>();
        for (@SuppressWarnings("unused")
        MTType t : original) {
            names.add(null);
        }

        return names;
    }

    public MTType getDomain() {
        return myDomain;
    }

    public MTType getRange() {
        return myRange;
    }

    public String getSingleParameterName() {
        return mySingleParameterName;
    }

    public MTType getApplicationType(String calledAsName, List<MTType> arguments) {

        return myFunctionApplicationFactory.buildFunctionApplication(
                myTypeGraph, this, calledAsName, arguments);
    }

    @Override
    public boolean isKnownToContainOnlyMTypes() {
        return false;
    }

    public boolean applicationResultsKnownToContainOnlyRestrictions() {
        return myRestrictionFlag;
    }

    @Override
    public int getHashCode() {
        return BASE_HASH + (myDomain.hashCode() * 31) + myRange.hashCode();
    }

    @Override
    public String toString() {
        return "(" + myDomain.toString() + " -> " + myRange.toString() + ")";
    }

    @Override
    public void accept(TypeVisitor v) {
        v.beginMTType(this);
        v.beginMTAbstract(this);
        v.beginMTFunction(this);

        v.beginChildren(this);

        myDomain.accept(v);
        myRange.accept(v);

        v.endChildren(this);

        v.endMTFunction(this);
        v.endMTAbstract(this);
        v.endMTType(this);
    }

    @Override
    public List<MTType> getComponentTypes() {
        return myComponents;
    }

    @Override
    public MTType withComponentReplaced(int index, MTType newType) {
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

        return new MTFunction(getTypeGraph(), myRestrictionFlag, newRange,
                newDomain);
    }

    public static MTType buildParameterType(TypeGraph g, List<MTType> paramTypes) {

        return buildParameterType(g,
                buildNullNameListOfEqualLength(paramTypes), paramTypes);
    }

    public static MTType buildParameterType(TypeGraph g,
            List<String> paramNames, List<MTType> paramTypes) {

        MTType result;

        switch (paramTypes.size()) {
        case 0:
            result = g.VOID;
            break;
        case 1:
            result = paramTypes.get(0);
            break;
        default:
            List<MTCartesian.Element> elements =
                    new LinkedList<MTCartesian.Element>();

            Iterator<String> namesIter = paramNames.iterator();
            Iterator<MTType> typesIter = paramTypes.iterator();
            while (namesIter.hasNext()) {
                elements.add(new MTCartesian.Element(namesIter.next(),
                        typesIter.next()));
            }

            result = new MTCartesian(g, elements);
        }

        return result;
    }

    private static class VanillaFunctionApplicationFactory
            implements
                FunctionApplicationFactory {

        @Override
        public MTType buildFunctionApplication(TypeGraph g, MTFunction f,
                String calledAsName, List<MTType> arguments) {
            return new MTFunctionApplication(g, f, calledAsName, arguments);
        }
    }
}
