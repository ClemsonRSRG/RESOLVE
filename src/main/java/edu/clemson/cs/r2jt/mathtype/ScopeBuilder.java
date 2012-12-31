package edu.clemson.cs.r2jt.mathtype;

import java.util.LinkedList;
import java.util.List;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.FacilityDec;
import edu.clemson.cs.r2jt.absyn.FinalItem;
import edu.clemson.cs.r2jt.absyn.InitItem;
import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.absyn.TypeDec;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.mathtype.ProgramParameterEntry.ParameterMode;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.util.Map;
import java.util.Set;

/**
 * <p>A <code>ScopeBuilder</code> is a working, mutable realization of 
 * <code>Scope</code>.</p>
 * 
 * <p>Note that <code>ScopeBuilder</code> has no public constructor.  
 * <code>ScopeBuilders</code>s are acquired through calls to some of the methods
 * of {@link MathSymbolTableBuilder MathSymbolTableBuilder}.</p>
 */
public class ScopeBuilder extends SyntacticScope {

    protected final List<ScopeBuilder> myChildren =
            new LinkedList<ScopeBuilder>();

    private final TypeGraph myTypeGraph;

    ScopeBuilder(MathSymbolTableBuilder b, TypeGraph g,
            ResolveConceptualElement definingElement, Scope parent,
            ModuleIdentifier enclosingModule) {

        super(b, definingElement, parent, enclosingModule,
                new BaseSymbolTable());

        myTypeGraph = g;
    }

    void setParent(Scope parent) {
        myParent = parent;
    }

    void addChild(ScopeBuilder b) {
        myChildren.add(b);
    }

    List<ScopeBuilder> children() {
        return new LinkedList<ScopeBuilder>(myChildren);
    }

    FinalizedScope seal(MathSymbolTable finalTable) {
        return new FinalizedScope(finalTable, myDefiningElement, myRootModule,
                myParent, myBindings);
    }

    public ProgramVariableEntry addProgramVariable(String name,
            ResolveConceptualElement definingElement, PTType type)
            throws DuplicateSymbolException {

        sanityCheckBindArguments(name, definingElement, type);

        ProgramVariableEntry entry =
                new ProgramVariableEntry(name, definingElement, myRootModule,
                        type);

        myBindings.put(name, entry);

        return entry;
    }

    public FacilityEntry addFacility(FacilityDec facility)
            throws DuplicateSymbolException {

        SymbolTableEntry curLocalEntry =
                myBindings.get(facility.getName().getName());
        if (curLocalEntry != null) {
            throw new DuplicateSymbolException(curLocalEntry);
        }

        FacilityEntry entry =
                new FacilityEntry(facility, myRootModule, getSourceRepository());

        myBindings.put(facility.getName().getName(), entry);

        return entry;
    }

    public OperationEntry addOperation(String name,
            ResolveConceptualElement definingElement,
            List<ProgramParameterEntry> params, PTType returnType)
            throws DuplicateSymbolException {

        sanityCheckBindArguments(name, definingElement, returnType);

        OperationEntry entry =
                new OperationEntry(name, definingElement, myRootModule,
                        returnType, params);

        myBindings.put(name, entry);

        return entry;
    }

    public ProcedureEntry addProcedure(String name,
            ResolveConceptualElement definingElement,
            OperationEntry correspondingOperation)
            throws DuplicateSymbolException {

        sanityCheckBindArguments(name, definingElement, "");

        ProcedureEntry entry =
                new ProcedureEntry(name, definingElement, myRootModule,
                        correspondingOperation);

        myBindings.put(name, entry);

        return entry;
    }

    public ProgramTypeEntry addProgramType(String name,
            TypeDec definingElement, MTType model)
            throws DuplicateSymbolException {

        sanityCheckBindArguments(name, definingElement, model);

        PosSymbol exemplarSymbol = definingElement.getExemplar();
        String exemplarName;

        if (exemplarSymbol == null) {
            exemplarName = "X";
        }
        else {
            exemplarName = exemplarSymbol.getName();
        }

        InitItem init = definingElement.getInitialization();
        FinalItem finalization = definingElement.getFinalization();

        Exp initRequires = (init == null) ? null : init.getRequires();
        Exp initEnsures = (init == null) ? null : init.getEnsures();
        Exp finalizationRequires =
                (finalization == null) ? null : finalization.getRequires();
        Exp finalizationEnsures =
                (finalization == null) ? null : finalization.getEnsures();

        ProgramTypeEntry entry =
                new ProgramTypeEntry(myTypeGraph, name, definingElement,
                        myRootModule, model, new PTFamily(model, name,
                                exemplarName, definingElement.getConstraint(),
                                initRequires, initEnsures,
                                finalizationRequires, finalizationEnsures));

        myBindings.put(name, entry);

        return entry;
    }

    public ProgramParameterEntry addFormalParameter(String name,
            ResolveConceptualElement definingElement, ParameterMode mode,
            PTType type) throws DuplicateSymbolException {

        sanityCheckBindArguments(name, definingElement, type);

        ProgramParameterEntry entry =
                new ProgramParameterEntry(myTypeGraph, name, definingElement,
                        myRootModule, type, mode);

        myBindings.put(name, entry);

        return entry;
    }

    /**
     * <p>Modifies the current working scope to add a new binding for a
     * symbol with an unqualified name, <code>name</code>, defined by the AST
     * node <code>definingElement</code> and of type <code>type</code>.</p>
     * 
     * @param name The unqualified name of the symbol.
     * @param definingElement The AST Node that introduced the symbol.
     * @param type The declared type of the symbol.
     * @param typeValue The type assigned to the symbol (can be null).
     * @param schematictypes A map from the names of any implicit type 
     *             parameters to their bounding types.  May be 
     *             <code>null</code>, which will be interpreted as the empty
     *             map.
     * 
     * @throws DuplicateSymbolException If such a symbol is already defined 
     *             directly in the scope represented by this 
     *             <code>ScopeBuilder</code>.  Note that this exception is not
     *             thrown if the symbol is defined in a parent scope or an
     *             imported module.
     */
    public MathSymbolEntry addBinding(String name,
            SymbolTableEntry.Quantification q,
            ResolveConceptualElement definingElement, MTType type,
            MTType typeValue, Map<String, MTType> schematicTypes,
            Map<String, MTType> genericsInDefiningContext)
            throws DuplicateSymbolException {

        sanityCheckBindArguments(name, definingElement, type);

        MathSymbolEntry entry =
                new MathSymbolEntry(myTypeGraph, name, q, definingElement,
                        type, typeValue, schematicTypes,
                        genericsInDefiningContext, myRootModule);

        myBindings.put(name, entry);

        return entry;
    }

    public MathSymbolEntry addBinding(String name,
            SymbolTableEntry.Quantification q,
            ResolveConceptualElement definingElement, MTType type)
            throws DuplicateSymbolException {
        return addBinding(name, q, definingElement, type, null, null, null);
    }

    public MathSymbolEntry addBinding(String name,
            ResolveConceptualElement definingElement, MTType type,
            MTType typeValue) throws DuplicateSymbolException {

        return addBinding(name, SymbolTableEntry.Quantification.NONE,
                definingElement, type, typeValue, null, null);
    }

    public MathSymbolEntry addBinding(String name,
            ResolveConceptualElement definingElement, MTType type)
            throws DuplicateSymbolException {

        return addBinding(name, SymbolTableEntry.Quantification.NONE,
                definingElement, type);
    }

    private void sanityCheckBindArguments(String name,
            ResolveConceptualElement definingElement, Object type)
            throws DuplicateSymbolException {

        SymbolTableEntry curLocalEntry = myBindings.get(name);
        if (curLocalEntry != null) {
            throw new DuplicateSymbolException(curLocalEntry);
        }

        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("Symbol table entry name must "
                    + "be non-null and contain at least one character.");
        }

        if (type == null) {
            throw new IllegalArgumentException("Symbol table entry type must "
                    + "be non-null.");
        }
    }
}
