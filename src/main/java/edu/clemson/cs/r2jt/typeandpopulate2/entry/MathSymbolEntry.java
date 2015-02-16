package edu.clemson.cs.r2jt.typeandpopulate2.entry;

import edu.clemson.cs.r2jt.absynnew.ResolveAST;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate2.MTProper;
import edu.clemson.cs.r2jt.typeandpopulate2.MTType;
import edu.clemson.cs.r2jt.typeandpopulate2.SymbolNotOfKindTypeException;
import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.PTType;
import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;
import org.antlr.v4.runtime.Token;

import java.util.*;

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
                           ResolveAST definingElement, MTType type,
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
    public MathSymbolEntry toMathSymbolEntry(Token l) {
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
    /*     genericInstantiations =
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
                getSourceModuleIdentifier());*/
        return null;
    }
}
