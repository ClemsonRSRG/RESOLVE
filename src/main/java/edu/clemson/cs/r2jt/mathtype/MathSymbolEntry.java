package edu.clemson.cs.r2jt.mathtype;

import edu.clemson.cs.r2jt.absyn.Exp;
import java.util.Map;

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class MathSymbolEntry extends SymbolTableEntry {

    private final MTType myType;
    private final MTType myTypeValue;
    private final Quantification myQuantification;

    /**
     * <p>Math symbols that represent definitions can take parameters, which may
     * contain implicit type parameters that cause the definition's true type
     * to change based on the type of arguments that end up actually passed.
     * These parameters are represented in this map, with the key giving the
     * name of the type parameter (which will then behave as a normal, bound,
     * named type within the definition's type) and the value giving the type
     * bounds of the parameter.</p>
     */
    private final Map<String, MTType> mySchematicTypes =
            new HashMap<String, MTType>();

    private final Map<String, MTType> myGenericsInDefiningContext =
            new HashMap<String, MTType>();

    /**
     * @param g
     * @param name
     * @param q
     * @param definingElement
     * @param type
     * @param typeValue
     * @param schematicTypes A map from the names of implicit type parameters
     *              contained in the definition to their bounding types.  May be
     *              <code>null</code>, which will be interpreted as the empty 
     *              map.
     * @param sourceModule 
     */
    public MathSymbolEntry(TypeGraph g, String name, Quantification q,
            ResolveConceptualElement definingElement, MTType type,
            MTType typeValue, Map<String, MTType> schematicTypes,
            Map<String, MTType> genericsInDefiningContext,
            ModuleIdentifier sourceModule) {
        super(name, definingElement, sourceModule);

        if (genericsInDefiningContext != null) {
            mySchematicTypes.putAll(genericsInDefiningContext);
            myGenericsInDefiningContext.putAll(genericsInDefiningContext);
        }

        if (schematicTypes != null) {
            mySchematicTypes.putAll(schematicTypes);
        }

        myType = type;
        myQuantification = q;
        if (typeValue != null) {
            myTypeValue = typeValue;
        }
        else if (type.isKnownToContainOnlyMTypes()) {
            myTypeValue =
                    new MTProper(g, type, type
                            .membersKnownToContainOnlyMTypes(), name);
        }
        else {
            myTypeValue = null;
        }
    }

    public Set<String> getSchematicTypeNames() {
        return new HashSet<String>(mySchematicTypes.keySet());
    }

    public MTType getSchematicTypeBounds(String name) {
        if (!mySchematicTypes.containsKey(name)) {
            throw new NoSuchElementException();
        }

        return mySchematicTypes.get(name);
    }

    /**
     * <p>Assuming this symbol represents a symbol with function type, returns
     * a new entry representing a "version" of this function in which all
     * <code>MTNamed</code> types that are components of its type have been
     * filled in based on the provided arguments.  This is accomplished in two
     * phases: first, any explicit type parameters are filled in (i.e., if the
     * function takes a type as a parameter and then that type is used later,
     * later usages will be correctly substituted with whatever type was 
     * passed); then, implicit type parameters are filled in by binding the
     * types of each actual argument to the expected type of the formal 
     * parameter, then performing replacements in the remaining argument types.
     * Any formal parameter that is <em>not</em> schematized (i.e., does not 
     * contain an <code>MTNamed</code>) will not attempt to bind to its 
     * argument (thus permitting later type flexibility via type theorem).  As a
     * result, simply because a call to this method succeeds <em>does not 
     * mean</em> those arguments are valid for the types of the parameters of
     * this function: the types of arguments corresponding to non-schematized 
     * formal parameters may not match even if a call to this method succeeds.
     * </p>
     * 
     * <p>If the provided arguments will not deschematize against the formal
     * parameter types, this method will throw a {@link NoSolutionException 
     *   NoSolutionException}.  This may occur because the argument count is not
     * correct, because an actual argument corresponding to a formal parameter
     * that expects an explicit type parameter is not a type, because the type
     * of an actual argument does not bind against its corresponding formal
     * parameter, or because one of the types inferred during that binding is 
     * not within its bounds.  If a call to this method yields a 
     * <code>NoSolutionException</code>, the provided arguments are definitely
     * unacceptable for a call to this function.</p>
     * 
     * @param arguments
     * @return
     * @throws NoSolutionException 
     */
    public MathSymbolEntry deschematize(List<Exp> arguments,
            Scope callingContext) throws NoSolutionException {

        if (!(myType instanceof MTFunction)) {
            throw new NoSolutionException();
        }

        List<MTType> formalParameterTypes =
                getParameterTypes(((MTFunction) myType));

        if (formalParameterTypes.size() != arguments.size()) {
            throw new NoSolutionException();
        }

        List<ProgramTypeEntry> callingContextProgramGenerics =
                callingContext.query(GenericQuery.INSTANCE);
        Map<String, MTType> callingContextMathGenerics =
                new HashMap<String, MTType>();

        MathSymbolEntry mathGeneric;
        for (ProgramTypeEntry e : callingContextProgramGenerics) {
            //This guaranteed not to fail--all program types can be coerced to
            //math types, so the passed location is irrelevant
            mathGeneric = e.toMathSymbolEntry(null);

            callingContextMathGenerics.put(mathGeneric.getName(),
                    mathGeneric.myType);
        }

        Iterator<Exp> argumentIter = arguments.iterator();
        Map<String, MTType> bindingsSoFar = new HashMap<String, MTType>();
        Map<String, MTType> iterationBindings;
        MTType argumentType;
        try {
            for (MTType formalParameterType : formalParameterTypes) {
                formalParameterType =
                        formalParameterType
                                .getCopyWithVariablesSubstituted(bindingsSoFar);

                //We know arguments and formalParameterTypes are the same 
                //length, see above
                argumentType = argumentIter.next().getMathType();

                if (containsSchematicType(formalParameterType)) {
                    iterationBindings =
                            argumentType.bindTo(formalParameterType,
                                    callingContextMathGenerics,
                                    mySchematicTypes);
                    bindingsSoFar.putAll(iterationBindings);
                }
            }
        }
        catch (BindingException be) {
            throw new NoSolutionException();
        }

        MTType newTypeValue = null;

        if (myTypeValue != null) {
            newTypeValue =
                    myTypeValue.getCopyWithVariablesSubstituted(bindingsSoFar);
        }

        MTType newType =
                ((MTFunction) myType
                        .getCopyWithVariablesSubstituted(bindingsSoFar))
                        .deschematize(arguments);

        return new MathSymbolEntry(myType.getTypeGraph(), getName(),
                myQuantification, getDefiningElement(), newType, newTypeValue,
                null, myGenericsInDefiningContext, getSourceModuleIdentifier());
    }

    private static List<MTType> getParameterTypes(MTFunction source) {
        MTType domain = source.getDomain();
        List<MTType> result = new LinkedList<MTType>();

        if (domain instanceof MTCartesian) {
            MTCartesian domainAsMTCartesian = (MTCartesian) domain;

            int size = domainAsMTCartesian.size();
            for (int i = 0; i < size; i++) {
                result.add(domainAsMTCartesian.getFactor(i));
            }
        }
        else {
            if (!domain.equals(source.getTypeGraph().VOID)) {
                result.add(domain);
            }
        }

        return result;
    }

    private boolean containsSchematicType(MTType t) {
        ContainsNamedTypeChecker checker =
                new ContainsNamedTypeChecker(mySchematicTypes.keySet());

        t.accept(checker);

        return checker.getResult();
    }

    public MTType getType() {
        return myType;
    }

    public Quantification getQuantification() {
        return myQuantification;
    }

    public MTType getTypeValue() throws SymbolNotOfKindTypeException {
        if (myTypeValue == null) {
            throw new SymbolNotOfKindTypeException();
        }

        return myTypeValue;
    }

    @Override
    public String toString() {
        return getSourceModuleIdentifier() + "." + getName() + "\t\t"
                + myQuantification + "\t\tOf type: " + myType
                + "\t\t Defines type: " + myTypeValue;
    }

    @Override
    public MathSymbolEntry toMathSymbolEntry(Location l) {
        return this;
    }

    @Override
    public String getEntryTypeDescription() {
        return "a math symbol";
    }

    @Override
    public MathSymbolEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {

        //Any type that appears in our list of schematic types shadows any 
        //possible reference to a generic type
        genericInstantiations =
                new HashMap<String, PTType>(genericInstantiations);
        for (String schematicType : mySchematicTypes.keySet()) {
            genericInstantiations.remove(schematicType);
        }

        Map<String, MTType> genericMathematicalInstantiations =
                SymbolTableEntry.buildMathTypeGenerics(genericInstantiations);

        VariableReplacingVisitor typeSubstitutor =
                new VariableReplacingVisitor(genericMathematicalInstantiations);
        myType.accept(typeSubstitutor);

        MTType instantiatedTypeValue = null;
        if (myTypeValue != null) {
            VariableReplacingVisitor typeValueSubstitutor =
                    new VariableReplacingVisitor(
                            genericMathematicalInstantiations);
            myTypeValue.accept(typeValueSubstitutor);
            instantiatedTypeValue = typeValueSubstitutor.getFinalExpression();
        }

        Map<String, MTType> newGenericsInDefiningContext =
                new HashMap<String, MTType>(myGenericsInDefiningContext);
        newGenericsInDefiningContext.keySet().removeAll(
                genericInstantiations.keySet());

        return new MathSymbolEntry(myType.getTypeGraph(), getName(),
                getQuantification(), getDefiningElement(), typeSubstitutor
                        .getFinalExpression(), instantiatedTypeValue,
                mySchematicTypes, newGenericsInDefiningContext,
                getSourceModuleIdentifier());
    }
}
