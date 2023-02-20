/*
 * MathSymbolEntry.java
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
package edu.clemson.rsrg.typeandpopulate.entry;

import edu.clemson.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.rsrg.typeandpopulate.exception.BindingException;
import edu.clemson.rsrg.typeandpopulate.exception.NoSolutionException;
import edu.clemson.rsrg.typeandpopulate.exception.SymbolNotOfKindTypeException;
import edu.clemson.rsrg.typeandpopulate.mathtypes.*;
import edu.clemson.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.rsrg.typeandpopulate.query.GenericProgramTypeQuery;
import edu.clemson.rsrg.typeandpopulate.symboltables.Scope;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.rsrg.typeandpopulate.typevisitor.ContainsNamedTypeChecker;
import edu.clemson.rsrg.typeandpopulate.typevisitor.VariableReplacingVisitor;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.*;

/**
 * <p>
 * This creates a symbol table entry for a mathematical symbol.
 * </p>
 *
 * @version 2.0
 */
public class MathSymbolEntry extends SymbolTableEntry {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The entry's mathematical type.
     * </p>
     */
    private final MTType myType;

    /**
     * <p>
     * The entry's mathematical type value.
     * </p>
     */
    private final MTType myTypeValue;

    /**
     * <p>
     * The entry's quantifier (if any).
     * </p>
     */
    private final Quantification myQuantification;

    /**
     * <p>
     * Math symbols that represent definitions can take parameters, which may contain implicit type parameters that
     * cause the definition's true type to change based on the type of arguments that end up actually passed. These
     * parameters are represented in this map, with the key giving the name of the type parameter (which will then
     * behave as a normal, bound, named type within the definition's type) and the value giving the type bounds of the
     * parameter.
     * </p>
     */
    private final Map<String, MTType> mySchematicTypes = new HashMap<>();

    /**
     * <p>
     * This map represents all the generic types we have encountered in this context.
     * </p>
     */
    private final Map<String, MTType> myGenericsInDefiningContext = new HashMap<>();

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a symbol table entry for a mathematical symbol.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param name
     *            Name associated with this entry.
     * @param q
     *            The quantifier (if any) associated with this entry.
     * @param definingElement
     *            The element that created this entry.
     * @param type
     *            The mathematical type associated with this entry.
     * @param typeValue
     *            The mathematical type value associated with this entry.
     * @param schematicTypes
     *            A map from the names of implicit type parameters contained in the definition to their bounding types.
     *            May be <code>null</code>, which will be interpreted as the empty map.
     * @param genericsInDefiningContext
     *            A map from names of generic types to their bounding types.
     * @param sourceModule
     *            The module where this entry was created from.
     */
    public MathSymbolEntry(TypeGraph g, String name, Quantification q, ResolveConceptualElement definingElement,
            MTType type, MTType typeValue, Map<String, MTType> schematicTypes,
            Map<String, MTType> genericsInDefiningContext, ModuleIdentifier sourceModule) {
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
        } else if (type.isKnownToContainOnlyMTypes()) {
            myTypeValue = new MTProper(g, type, type.membersKnownToContainOnlyMTypes(), name);
        } else {
            myTypeValue = null;
        }
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Assuming this symbol represents a symbol with function type, returns a new entry representing a "version" of this
     * function in which all {@link MTNamed} types that are components of its type have been filled in based on the
     * provided arguments. This is accomplished in two phases: first, any explicit type parameters are filled in (i.e.,
     * if the function takes a type as a parameter and then that type is used later, later usages will be correctly
     * substituted with whatever type was passed); then, implicit type parameters are filled in by binding the types of
     * each actual argument to the expected type of the formal parameter, then performing replacements in the remaining
     * argument types. Any formal parameter that is <em>not</em> schematized (i.e., does not contain an {@link MTNamed})
     * will not attempt to bind to its argument (thus permitting later type flexibility via type theorem). As a result,
     * simply because a call to this method succeeds <em>does not mean</em> the arguments are valid for the types of the
     * parameters of this function: the types of arguments corresponding to non-schematized formal parameters may not
     * match even if a call to this method succeeds.
     * </p>
     *
     * <p>
     * If the provided arguments will not deschematize against the formal parameter types, this method will throw a
     * {@link NoSolutionException}. This may occur because the argument count is not correct, because an actual argument
     * corresponding to a formal parameter that expects an explicit type parameter is not a type, because the type of an
     * actual argument does not bind against its corresponding formal parameter, or because one of the types inferred
     * during that binding is not within its bounds. If a call to this method yields a {@link NoSolutionException}, the
     * provided arguments are definitely unacceptable for a call to this function.
     * </p>
     *
     * @param arguments
     *            Arguments to the mathematical function.
     * @param callingContext
     *            The current scope we are calling from.
     * @param definitionSchematicTypes
     *            The schematic types from the definition.
     *
     * @return A {@link MathSymbolEntry} with the arguments deschematized against the formal parameters.
     *
     * @throws NoSolutionException
     *             We couldn't deschematize this function.
     */
    public final MathSymbolEntry deschematize(List<Exp> arguments, Scope callingContext,
            Map<String, MTType> definitionSchematicTypes) throws NoSolutionException {
        if (!(myType instanceof MTFunction)) {
            throw new NoSolutionException("Expecting MTFunction, found: " + myType.getClass().getSimpleName(),
                    new IllegalStateException());
        }

        List<MTType> formalParameterTypes = getParameterTypes(((MTFunction) myType));
        List<MTType> actualArgumentTypes = getArgumentTypes(arguments);

        if (formalParameterTypes.size() != actualArgumentTypes.size()) {
            throw new NoSolutionException("Unequal formal and actual argument sizes.", new IllegalStateException());
        }

        List<ProgramTypeEntry> callingContextProgramGenerics = callingContext.query(GenericProgramTypeQuery.INSTANCE);
        Map<String, MTType> callingContextMathGenerics = new HashMap<>(definitionSchematicTypes);

        MathSymbolEntry mathGeneric;
        for (ProgramTypeEntry e : callingContextProgramGenerics) {
            // This is guaranteed not to fail--all program types can be coerced
            // to math types, so the passed location is irrelevant
            mathGeneric = e.toMathSymbolEntry(null);

            callingContextMathGenerics.put(mathGeneric.getName(), mathGeneric.myType);
        }

        Iterator<MTType> argumentTypeIter = actualArgumentTypes.iterator();
        Map<String, MTType> bindingsSoFar = new HashMap<>();
        Map<String, MTType> iterationBindings;
        MTType argumentType;
        try {
            for (MTType formalParameterType : formalParameterTypes) {
                formalParameterType = formalParameterType.getCopyWithVariablesSubstituted(bindingsSoFar);

                // We know arguments and formalParameterTypes are the same
                // length, see above
                argumentType = argumentTypeIter.next();

                if (containsSchematicType(formalParameterType)) {
                    iterationBindings = argumentType.bindTo(formalParameterType, callingContextMathGenerics,
                            mySchematicTypes);

                    bindingsSoFar.putAll(iterationBindings);
                }
            }
        } catch (BindingException be) {
            throw new NoSolutionException(
                    "Error while attempting to bind the actual arguments to the formal parameters.",
                    new IllegalStateException());
        }

        MTType newTypeValue = null;

        if (myTypeValue != null) {
            newTypeValue = myTypeValue.getCopyWithVariablesSubstituted(bindingsSoFar);
        }

        MTType newType = ((MTFunction) myType.getCopyWithVariablesSubstituted(bindingsSoFar)).deschematize(arguments);

        return new MathSymbolEntry(myType.getTypeGraph(), getName(), myQuantification, getDefiningElement(), newType,
                newTypeValue, null, myGenericsInDefiningContext, getSourceModuleIdentifier());
    }

    /**
     * <p>
     * This method returns a description associated with this entry.
     * </p>
     *
     * @return A string.
     */
    @Override
    public final String getEntryTypeDescription() {
        return "a math symbol";
    }

    /**
     * <p>
     * This method returns the quantifier for this entry.
     * </p>
     *
     * @return A {@link Quantification} object.
     */
    public final Quantification getQuantification() {
        return myQuantification;
    }

    /**
     * <p>
     * This method returns the schemematic type bounds for a given type names.
     * </p>
     *
     * @param name
     *            Type name in string format.
     *
     * @return The associated {@link MTType} if found, otherwise it throws a {@link NoSuchElementException}.
     */
    public final MTType getSchematicTypeBounds(String name) {
        if (!mySchematicTypes.containsKey(name)) {
            throw new NoSuchElementException();
        }

        return mySchematicTypes.get(name);
    }

    /**
     * <p>
     * This returns all the schematic type names.
     * </p>
     *
     * @return A {@link Set} containing all the type names.
     */
    public final Set<String> getSchematicTypeNames() {
        return new HashSet<>(mySchematicTypes.keySet());
    }

    /**
     * <p>
     * This method gets the mathematical type associated with this object.
     * </p>
     *
     * @return The {@link MTType} type object.
     */
    public final MTType getType() {
        return myType;
    }

    /**
     * <p>
     * This method gets the mathematical type value associated with this object.
     * </p>
     *
     * @return The {@link MTType} type object.
     *
     * @throws SymbolNotOfKindTypeException
     *             We are trying to get a {@code null} type value.
     */
    public final MTType getTypeValue() throws SymbolNotOfKindTypeException {
        if (myTypeValue == null) {
            throw new SymbolNotOfKindTypeException("Null type value!");
        }

        return myTypeValue;
    }

    /**
     * <p>
     * This method converts a generic {@link SymbolTableEntry} to an entry that has all the generic types and variables
     * replaced with actual values.
     * </p>
     *
     * @param genericInstantiations
     *            Map containing all the instantiations.
     * @param instantiatingFacility
     *            Facility that instantiated this type.
     *
     * @return A {@link MathSymbolEntry} that has been instantiated.
     */
    @Override
    public final MathSymbolEntry instantiateGenerics(Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        // Any type that appears in our list of schematic types shadows any
        // possible reference to a generic type
        genericInstantiations = new HashMap<>(genericInstantiations);
        for (String schematicType : mySchematicTypes.keySet()) {
            genericInstantiations.remove(schematicType);
        }

        Map<String, MTType> genericMathematicalInstantiations = SymbolTableEntry
                .buildMathTypeGenerics(genericInstantiations);

        VariableReplacingVisitor typeSubstitutor = new VariableReplacingVisitor(genericMathematicalInstantiations);
        myType.accept(typeSubstitutor);

        MTType instantiatedTypeValue = null;
        if (myTypeValue != null) {
            VariableReplacingVisitor typeValueSubstitutor = new VariableReplacingVisitor(
                    genericMathematicalInstantiations);
            myTypeValue.accept(typeValueSubstitutor);
            instantiatedTypeValue = typeValueSubstitutor.getFinalExpression();
        }

        Map<String, MTType> newGenericsInDefiningContext = new HashMap<>(myGenericsInDefiningContext);
        newGenericsInDefiningContext.keySet().removeAll(genericInstantiations.keySet());

        return new MathSymbolEntry(myType.getTypeGraph(), getName(), getQuantification(), getDefiningElement(),
                typeSubstitutor.getFinalExpression(), instantiatedTypeValue, mySchematicTypes,
                newGenericsInDefiningContext, getSourceModuleIdentifier());
    }

    /**
     * <p>
     * This method will attempt to convert this {@link SymbolTableEntry} into a {@link MathSymbolEntry}.
     * </p>
     *
     * @param l
     *            Location where we encountered this entry.
     *
     * @return A {@link MathSymbolEntry} if possible. Otherwise, it throws a {@link SourceErrorException}.
     */
    @Override
    public final MathSymbolEntry toMathSymbolEntry(Location l) {
        return this;
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
        return getSourceModuleIdentifier() + "." + getName() + "\t\t" + myQuantification + "\t\tOf type: " + myType
                + "\t\t Defines type: " + myTypeValue;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * Given {@code t}, we check to see if this type is contained in any of our schematic types.
     * </p>
     *
     * @param t
     *            A {@link MTType}.
     *
     * @return {@code true} if {@code t} contained inside a schematic type, {@code false} otherwise.
     */
    private boolean containsSchematicType(MTType t) {
        ContainsNamedTypeChecker checker = new ContainsNamedTypeChecker(mySchematicTypes.keySet());

        t.accept(checker);

        return checker.getResult();
    }

    /**
     * <p>
     * This method returns a list of mathematical types that can be expanded from {@code t}.
     * </p>
     *
     * @param t
     *            A {@link MTType}.
     *
     * @return A list containing all the expanded {@link MTType}s.
     */
    private static List<MTType> expandAsNeeded(MTType t) {
        List<MTType> result = new LinkedList<>();

        if (t instanceof MTCartesian) {
            MTCartesian domainAsMTCartesian = (MTCartesian) t;

            int size = domainAsMTCartesian.size();
            for (int i = 0; i < size; i++) {
                result.add(domainAsMTCartesian.getFactor(i));
            }
        } else {
            if (!t.equals(t.getTypeGraph().VOID)) {
                result.add(t);
            }
        }

        return result;
    }

    /**
     * <p>
     * This method returns the mathematical types associated with the provided arguments.
     * </p>
     *
     * @param arguments
     *            The arguments to a mathematical function.
     *
     * @return A list of {@link MTType}s.
     */
    private static List<MTType> getArgumentTypes(List<Exp> arguments) {
        List<MTType> result;

        if (arguments.size() == 1) {
            result = expandAsNeeded(arguments.get(0).getMathType());
        } else {
            result = new LinkedList<>();
            for (Exp e : arguments) {
                result.add(e.getMathType());
            }
        }

        return result;
    }

    /**
     * <p>
     * Given a mathematical function, we return the list of parameter types associated with it.
     * </p>
     *
     * @param source
     *            A {@link MTFunction}.
     *
     * @return The list of parameter {@link MTType}s.
     */
    private static List<MTType> getParameterTypes(MTFunction source) {
        MTType domain = source.getDomain();
        List<MTType> result = expandAsNeeded(domain);

        return result;
    }

}
