package edu.clemson.cs.r2jt.population;

import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.treewalk.*;
import edu.clemson.cs.r2jt.typeandpopulate.DuplicateSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.MTCartesian;
import edu.clemson.cs.r2jt.typeandpopulate.MTFunction;
import edu.clemson.cs.r2jt.typeandpopulate.MTNamed;
import edu.clemson.cs.r2jt.typeandpopulate.MTPowertypeApplication;
import edu.clemson.cs.r2jt.typeandpopulate.MTSetRestriction;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTableBuilder;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleScope;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleScopeBuilder;
import edu.clemson.cs.r2jt.typeandpopulate.NoSolutionException;
import edu.clemson.cs.r2jt.typeandpopulate.NoSuchSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.ParameterGenericApplyingVisitor;
import edu.clemson.cs.r2jt.typeandpopulate.SymbolNotOfKindTypeException;
import edu.clemson.cs.r2jt.typeandpopulate.VariableReplacingVisitor;
import edu.clemson.cs.r2jt.typeandpopulate.entry.MathSymbolEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.OperationEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramParameterEntry.ParameterMode;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramTypeDefinitionEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramTypeEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramVariableEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry.Quantification;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTElement;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTRecord;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTRepresentation;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTVoid;
import edu.clemson.cs.r2jt.typeandpopulate.query.MathFunctionNamedQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.MathSymbolQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.NameAndEntryTypeQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.NameQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.OperationQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.ProgramVariableQuery;
import edu.clemson.cs.r2jt.typereasoning.*;
import edu.clemson.cs.r2jt.utilities.SourceErrorException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class Populator extends TreeWalkerVisitor {

    private static final boolean PRINT_DEBUG = false;

    private static final TypeComparison<AbstractFunctionExp, MTFunction> EXACT_DOMAIN_MATCH =
            new ExactDomainMatch();

    private static final Comparator<MTType> EXACT_PARAMETER_MATCH =
            new ExactParameterMatch();

    private final TypeComparison<AbstractFunctionExp, MTFunction> INEXACT_DOMAIN_MATCH =
            new InexactDomainMatch();

    private final TypeComparison<Exp, MTType> INEXACT_PARAMETER_MATCH =
            new InexactParameterMatch();

    private MathSymbolTableBuilder myBuilder;
    private ModuleScopeBuilder myCurModuleScope;

    private int myTypeValueDepth = 0;
    private int myExpressionDepth = 0;

    private boolean myInTypeTheoremBindingExpFlag = false;

    /**
     * <p>Any quantification-introducing syntactic node (like, e.g., a 
     * QuantExp), introduces a level to this stack to reflect the quantification 
     * that should be applied to named variables as they are encountered.  Note 
     * that this may change as the children of the node are processed--for 
     * example, MathVarDecs found in the declaration portion of a QuantExp 
     * should have quantification (universal or existential) applied, while 
     * those found in the body of the QuantExp should have no quantification 
     * (unless there is an embedded QuantExp).  In this case, QuantExp should 
     * <em>not</em> remove its layer, but rather change it to 
     * MathSymbolTableEntry.None.</p>
     * 
     * <p>This stack is never empty, but rather the bottom layer is always
     * MathSymbolTableEntry.None.</p>
     */
    private Deque<SymbolTableEntry.Quantification> myActiveQuantifications =
            new LinkedList<SymbolTableEntry.Quantification>();

    private final TypeGraph myTypeGraph;
    private TreeWalker myWalker;

    /**
     * <p>While we walk the children of a procedure definition, this will be set
     * to the ProcedureDec.  Otherwise it will be null.</p>
     */
    private ProcedureDec myCurrentProcedure;

    /**
     * <p>While we walk the children of a direct definition, this will be set
     * with a pointer to the definition declaration we are walking, otherwise
     * it will be null.  Note that definitions cannot be nested, so there's
     * no need for a stack.</p>
     */
    private DefinitionDec myCurrentDirectDefinition;

    private Map<String, MTType> myDefinitionSchematicTypes =
            new HashMap<String, MTType>();

    /**
     * <p>This simply enables an error check--as a definition uses named types,
     * we keep track of them, and when an implicit type is introduced, we make
     * sure that it hasn't been "used" yet, thus leading to a confusing scenario
     * where some instances of the name should refer to a type already in scope
     * as the definition is declared and other instance refer to the implicit
     * type parameter.</p>
     */
    private Set<String> myDefinitionNamedTypes = new HashSet<String>();

    /**
     * <p>While walking a procedure, this is set to the entry for the operation 
     * or FacilityOperation that the procedure is attempting to implement.</p>
     * 
     * <p><strong>INVARIANT:</strong> 
     * <code>myCorrespondingOperation != null</code> <em>implies</em> 
     * <code>myCurrentParameters != null</code>.</p>
     */
    private OperationEntry myCorrespondingOperation;

    /**
     * <p>While we walk the children of an operation, FacilityOperation, or
     * procedure, this list will contain all formal parameters encountered so 
     * far, otherwise it will be null.  Since none of these structures can be
     * be nested, there's no need for a stack.</p>
     * 
     * <p>If you need to distinguish if you're in the middle of an 
     * operation/FacilityOperation or a procedure, check 
     * myCorrespondingOperation.</p>
     */
    private List<ProgramParameterEntry> myCurrentParameters;

    /**
     * <p>A mapping from generic types that appear in the module to the math
     * types that bound their possible values.</p>
     */
    private Map<String, MTType> myGenericTypes = new HashMap<String, MTType>();

    /**
     * <p>When parsing a type realization declaration, this is set to the 
     * entry corresponding to the conceptual declaration from the concept.  When
     * not inside such a declaration, this will be null.</p>
     */
    private ProgramTypeDefinitionEntry myTypeDefinitionEntry;

    public Populator(MathSymbolTableBuilder builder) {
        myActiveQuantifications.push(SymbolTableEntry.Quantification.NONE);

        myTypeGraph = builder.getTypeGraph();
        myBuilder = builder;
    }

    public void setTreeWalker(TreeWalker w) {
        //TODO : This is required by an annoying circular dependency.  Ideally,
        //       the methods of TreeWalker should just be static so that an
        //       instance is not required
        myWalker = w;
    }

    //-------------------------------------------------------------------
    //   Visitor methods
    //-------------------------------------------------------------------

    @Override
    public void preModuleDec(ModuleDec node) {
        Populator.emitDebug("----------------------\nBEGIN MATH POPULATOR");
        myCurModuleScope = myBuilder.startModuleScope(node);
    }

    @Override
    public void preEnhancementModuleDec(EnhancementModuleDec enhancement) {

        //Enhancements implicitly import the concepts they enhance
        myCurModuleScope.addImport(new ModuleIdentifier(enhancement
                .getConceptName().getName()));
    }

    @Override
    public void preConceptBodyModuleDec(ConceptBodyModuleDec conceptBody) {

        //Concept realizations implicitly import the concepts they realize
        myCurModuleScope.addImport(new ModuleIdentifier(conceptBody
                .getConceptName().getName()));
    }

    @Override
    public void preEnhancementBodyModuleDec(
            EnhancementBodyModuleDec enhancementRealization) {

        //Enhancement realizations implicitly import the concepts they enhance
        //and the enhancements they realize
        myCurModuleScope.addImport(new ModuleIdentifier(enhancementRealization
                .getConceptName().getName()));
        myCurModuleScope.addImport(new ModuleIdentifier(enhancementRealization
                .getEnhancementName().getName()));
    }

    @Override
    public void postUsesItem(UsesItem uses) {
        myCurModuleScope.addImport(new ModuleIdentifier(uses));
    }

    @Override
    public void preLambdaExp(LambdaExp l) {
        myBuilder.startScope(l);
    }

    @Override
    public void postLambdaExp(LambdaExp l) {
        myBuilder.endScope();

        l.setMathType(new MTFunction(myTypeGraph, l.getBody().getMathType(), l
                .getTy().getMathType()));
    }

    @Override
    public void postAltItemExp(AltItemExp e) {
        if (e.getTest() != null) {
            expectType(e.getTest(), myTypeGraph.BOOLEAN);
        }

        e.setMathType(e.getAssignment().getMathType());
        e.setMathTypeValue(e.getAssignment().getMathTypeValue());
    }

    @Override
    public void postAlternativeExp(AlternativeExp e) {

        MTType establishedType = null;
        MTType establishedTypeValue = null;
        for (AltItemExp alt : e.getAlternatives()) {
            if (establishedType == null) {
                establishedType = alt.getAssignment().getMathType();
                establishedTypeValue = alt.getAssignment().getMathTypeValue();
            }
            else {
                expectType(alt, establishedType);
            }
        }

        e.setMathType(establishedType);
        e.setMathTypeValue(establishedTypeValue);
    }

    @Override
    public void postConstantParamDec(ConstantParamDec param) {
        try {
            String paramName = param.getName().getName();

            myBuilder.getInnermostActiveScope().addFormalParameter(paramName,
                    param, ParameterMode.EVALUATES,
                    param.getTy().getProgramTypeValue());
            param.setMathType(param.getTy().getMathTypeValue());
        }
        catch (DuplicateSymbolException dse) {
            duplicateSymbol(param.getName().getName(), param.getName()
                    .getLocation());
        }
    }

    @Override
    public void postConceptTypeParamDec(ConceptTypeParamDec param) {
        try {
            String paramName = param.getName().getName();

            myBuilder.getInnermostActiveScope().addFormalParameter(paramName,
                    param, ParameterMode.TYPE, new PTElement(myTypeGraph));

            myGenericTypes.put(paramName, myTypeGraph.MTYPE);
            param.setMathType(myTypeGraph.MTYPE);
        }
        catch (DuplicateSymbolException dse) {
            duplicateSymbol(param.getName().getName(), param.getName()
                    .getLocation());
        }
    }

    @Override
    public void postStructureExp(StructureExp structure) {
        //TODO: Remove the StructureExps from where they appear--they're no 
        //      longer used

        //Type it so we don't get an error
        structure.setMathType(myTypeGraph.ENTITY);
    }

    @Override
    public void preFacilityOperationDec(FacilityOperationDec dec) {
        myBuilder.startScope(dec);
        myCurrentParameters = new LinkedList<ProgramParameterEntry>();

    }

    @Override
    public void postModuleParameterDec(ModuleParameterDec d) {
        if (d.getWrappedDec().getMathType() == null) {
            throw new RuntimeException(d.getWrappedDec().getClass()
                    + " has null type");
        }

        d.setMathType(d.getWrappedDec().getMathType());
    }

    @Override
    public void preCrossTypeExpression(CrossTypeExpression e) {
        myTypeValueDepth++;
    }

    @Override
    public void postCrossTypeExpression(CrossTypeExpression e) {
        int fieldCount = e.getFieldCount();
        List<MTCartesian.Element> fieldTypes =
                new LinkedList<MTCartesian.Element>();

        PosSymbol psTag;
        String tag;
        for (int i = 0; i < fieldCount; i++) {

            psTag = e.getTag(i);
            if (psTag != null) {
                tag = psTag.getName();
            }
            else {
                tag = null;
            }

            fieldTypes.add(new MTCartesian.Element(tag, e.getField(i)
                    .getMathTypeValue()));
        }

        e.setMathType(myTypeGraph.MTYPE);
        e.setMathTypeValue(new MTCartesian(myTypeGraph, fieldTypes));

        myTypeValueDepth--;
    }

    @Override
    public void preProcedureDec(ProcedureDec dec) {
        myCurrentProcedure = dec;

        try {
            //Figure out what Operation we correspond to (we don't use 
            //OperationQuery because we want to check parameter types 
            //separately in postProcedureDec)
            myCorrespondingOperation =
                    myBuilder.getInnermostActiveScope().queryForOne(
                            new NameAndEntryTypeQuery(null, dec.getName(),
                                    OperationEntry.class,
                                    ImportStrategy.IMPORT_NAMED,
                                    FacilityStrategy.FACILITY_IGNORE, false))
                            .toOperationEntry(dec.getLocation());

            myBuilder.startScope(dec);

            myCurrentParameters = new LinkedList<ProgramParameterEntry>();
        }
        catch (NoSuchSymbolException nsse) {
            throw new SourceErrorException("Procedure "
                    + dec.getName().getName()
                    + " does not implement any known operation.", dec.getName()
                    .getLocation());
        }
        catch (DuplicateSymbolException dse) {
            //We should have caught this before now, like when we defined the
            //duplicate Operation
            throw new RuntimeException("Duplicate Operations for "
                    + dec.getName().getName() + "?");
        }
    }

    @Override
    public boolean walkFuncAssignStmt(FuncAssignStmt s) {
        preAny(s);
        preStatement(s);
        preFuncAssignStmt(s);

        boolean walkVar = true;
        VariableExp v = s.getVar();
        if (v instanceof VariableNameExp) {
            VariableNameExp vAsNVE = (VariableNameExp) v;

            walkVar =
                    !vAsNVE.getName().getName().equals(
                            myCurrentProcedure.getName().getName());
        }

        if (walkVar) {
            myWalker.visit(v);
        }
        else {
            v.setMathType(myCurrentProcedure.getReturnTy().getMathTypeValue());
        }
        myWalker.visit(s.getAssign());

        postFuncAssignStmt(s);
        postStatement(s);
        postAny(s);

        return true;
    }

    @Override
    public void postProcedureDec(ProcedureDec dec) {
        myBuilder.endScope();

        //We're about to throw away all information about procedure parameters,
        //since they're redundant anyway.  So we sanity-check them first.
        Ty returnTy = dec.getReturnTy();
        PTType returnType;
        if (returnTy == null) {
            returnType = PTVoid.getInstance(myTypeGraph);
        }
        else {
            returnType = returnTy.getProgramTypeValue();
        }

        if (!returnType.equals(myCorrespondingOperation.getReturnType())) {
            throw new SourceErrorException("Procedure return type does "
                    + "not correspond to the return type of the operation "
                    + "it implements.  \n\nExpected type: "
                    + myCorrespondingOperation.getReturnType() + " ("
                    + myCorrespondingOperation.getSourceModuleIdentifier()
                    + "." + myCorrespondingOperation.getName() + ")\n\n"
                    + "Found type: " + returnType, dec.getLocation());
        }

        if (myCorrespondingOperation.getParameters().size() != myCurrentParameters
                .size()) {
            throw new SourceErrorException("Procedure parameter count "
                    + "does not correspond to the parameter count of the "
                    + "operation it implements. \n\nExpected count: "
                    + myCorrespondingOperation.getParameters().size() + " ("
                    + myCorrespondingOperation.getSourceModuleIdentifier()
                    + "." + myCorrespondingOperation.getName() + ")\n\n"
                    + "Found count: " + myCurrentParameters.size(), dec
                    .getLocation());
        }

        Iterator<ProgramParameterEntry> opParams =
                myCorrespondingOperation.getParameters().iterator();
        Iterator<ProgramParameterEntry> procParams =
                myCurrentParameters.iterator();
        ProgramParameterEntry curOpParam, curProcParam;
        while (opParams.hasNext()) {
            curOpParam = opParams.next();
            curProcParam = procParams.next();

            if (!curOpParam.getParameterMode().canBeImplementedWith(
                    curProcParam.getParameterMode())) {
                throw new SourceErrorException(curOpParam.getParameterMode()
                        + "-mode parameter "
                        + "cannot be implemented with "
                        + curProcParam.getParameterMode()
                        + " mode.  "
                        + "Select one of these valid modes instead: "
                        + Arrays.toString(curOpParam.getParameterMode()
                                .getValidImplementationModes()), curProcParam
                        .getDefiningElement().getLocation());
            }

            if (!curProcParam.getDeclaredType().acceptableFor(
                    curOpParam.getDeclaredType())) {
                throw new SourceErrorException("Parameter type does not "
                        + "match corresponding operation parameter type."
                        + "\n\nExpected: " + curOpParam.getDeclaredType()
                        + " (" + curOpParam.getSourceModuleIdentifier() + "."
                        + myCorrespondingOperation.getName() + ")\n\n"
                        + "Found: " + curProcParam.getDeclaredType(),
                        curProcParam.getDefiningElement().getLocation());
            }

            if (!curOpParam.getName().equals(curProcParam.getName())) {
                throw new SourceErrorException("Parmeter name does not "
                        + "match corresponding operation parameter name."
                        + "\n\nExpected name: " + curOpParam.getName() + " ("
                        + curOpParam.getSourceModuleIdentifier() + "."
                        + myCorrespondingOperation.getName() + ")\n\n"
                        + "Found name: " + curProcParam.getName(), curProcParam
                        .getDefiningElement().getLocation());
            }
        }

        try {
            myBuilder.getInnermostActiveScope().addProcedure(
                    dec.getName().getName(), dec, myCorrespondingOperation);
        }
        catch (DuplicateSymbolException dse) {
            duplicateSymbol(dec.getName().getName(), dec.getName()
                    .getLocation());
        }

        myCurrentParameters = null;
        myCurrentProcedure = null;
    }

    @Override
    public void preOperationDec(OperationDec dec) {
        myBuilder.startScope(dec);

        myCurrentParameters = new LinkedList<ProgramParameterEntry>();
    }

    @Override
    public void midOperationDec(OperationDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {

        if (prevChild == node.getReturnTy() && node.getReturnTy() != null) {
            try {
                //Inside the operation's assertions, the name of the operation
                //refers to its return value
                myBuilder.getInnermostActiveScope().addBinding(
                        node.getName().getName(), node,
                        node.getReturnTy().getMathTypeValue());
            }
            catch (DuplicateSymbolException dse) {
                //This shouldn't be possible--the operation declaration has a 
                //scope all its own and we're the first ones to get to
                //introduce anything
                throw new RuntimeException(dse);
            }
        }
    }

    @Override
    public void postOperationDec(OperationDec dec) {
        myBuilder.endScope();

        try {
            Ty returnTy = dec.getReturnTy();
            PTType returnType;
            if (returnTy == null) {
                returnType = PTVoid.getInstance(myTypeGraph);
            }
            else {
                returnType = returnTy.getProgramTypeValue();
            }

            myBuilder.getInnermostActiveScope().addOperation(
                    dec.getName().getName(), dec, myCurrentParameters,
                    returnType);
        }
        catch (DuplicateSymbolException dse) {
            duplicateSymbol(dec.getName().getName(), dec.getName()
                    .getLocation());
        }

        myCurrentParameters = null;
    }

    @Override
    public void postFacilityOperationDec(FacilityOperationDec dec) {
        myBuilder.endScope();
        myCurrentParameters = null;

    }

    @Override
    public void postParameterVarDec(ParameterVarDec dec) {

        ParameterMode mode =
                ProgramParameterEntry.OLD_TO_NEW_MODE.get(dec.getMode());

        if (mode == null) {
            throw new RuntimeException("Unexpected parameter mode: "
                    + dec.getMode());
        }

        try {
            ProgramParameterEntry paramEntry =
                    myBuilder.getInnermostActiveScope().addFormalParameter(
                            dec.getName().getName(), dec, mode,
                            dec.getTy().getProgramTypeValue());
            myCurrentParameters.add(paramEntry);
        }
        catch (DuplicateSymbolException e) {
            duplicateSymbol(dec.getName().getName(), dec.getName()
                    .getLocation());
        }
    }

    @Override
    public void preRepresentationDec(RepresentationDec r) {
        myBuilder.startScope(r);

        PosSymbol type = r.getName();

        List<SymbolTableEntry> es =
                myBuilder.getInnermostActiveScope().query(
                        new NameQuery(null, type, ImportStrategy.IMPORT_NAMED,
                                FacilityStrategy.FACILITY_IGNORE, false));

        if (es.isEmpty()) {
            noSuchSymbol(null, type);
        }
        else if (es.size() > 1) {
            ambiguousSymbol(type, es);
        }
        else {
            myTypeDefinitionEntry =
                    es.get(0).toProgramTypeDefinitionEntry(r.getLocation());
        }
    }

    @Override
    public void midRepresentationDec(RepresentationDec r,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {

        if (prevChild instanceof Ty) {
            //We've finished the representation and are about to parse 
            //conventions, etc.  We introduce the exemplar with the appropriate
            //type
            addBinding(
                    myTypeDefinitionEntry.getProgramType().getExemplarName(), r
                            .getName().getLocation(), r, r.getRepresentation()
                            .getMathTypeValue(), myGenericTypes);
        }
    }

    @Override
    public void postRepresentationDec(RepresentationDec r) {
        myBuilder.endScope();

        try {
            myBuilder.getInnermostActiveScope().addRepresentationTypeEntry(
                    r.getName().getName(), r, myTypeDefinitionEntry,
                    r.getRepresentation().getProgramTypeValue(),
                    r.getConvention(), r.getCorrespondence());
        }
        catch (DuplicateSymbolException dse) {
            duplicateSymbol(r.getName());
        }

        myTypeDefinitionEntry = null;
    }

    @Override
    public void preTypeDec(TypeDec dec) {
        myBuilder.startScope(dec);
    }

    @Override
    public void midTypeDec(TypeDec dec, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {

        if (prevChild == dec.getModel()) {
            //We've parsed the model, but nothing else, so we can add our 
            //exemplar to scope if we've got one
            PosSymbol exemplar = dec.getExemplar();

            if (exemplar != null) {
                try {
                    myBuilder.getInnermostActiveScope().addBinding(
                            exemplar.getName(), dec,
                            dec.getModel().getMathTypeValue());
                }
                catch (DuplicateSymbolException dse) {
                    //This shouldn't be possible--the type declaration has a 
                    //scope all its own and we're the first ones to get to
                    //introduce anything
                    throw new RuntimeException(dse);
                }
            }
        }
    }

    @Override
    public void postTypeDec(TypeDec dec) {
        myBuilder.endScope();

        try {
            myBuilder.getInnermostActiveScope().addProgramTypeDefinition(
                    dec.getName().getName(), dec,
                    dec.getModel().getMathTypeValue());
        }
        catch (DuplicateSymbolException dse) {
            duplicateSymbol(dec.getName().getName(), dec.getName()
                    .getLocation());
        }
    }

    @Override
    public void postRecordTy(RecordTy ty) {
        Map<String, PTType> fieldMap = new HashMap<String, PTType>();
        List<VarDec> fields = ty.getFields();
        for (VarDec field : fields) {
            fieldMap.put(field.getName().getName(), field.getTy()
                    .getProgramTypeValue());
        }

        PTRecord record = new PTRecord(myTypeGraph, fieldMap);

        ty.setProgramTypeValue(record);
        ty.setMathType(myTypeGraph.MTYPE);
        ty.setMathTypeValue(record.toMath());
    }

    @Override
    public void postNameTy(NameTy ty) {
        //Note that all mathematical types are ArbitraryExpTys, so this must
        //be in a program-type syntactic slot.

        PosSymbol tySymbol = ty.getName();
        PosSymbol tyQualifier = ty.getQualifier();
        Location tyLocation = tySymbol.getLocation();
        String tyName = tySymbol.getName();

        try {
            ProgramTypeEntry type =
                    myBuilder
                            .getInnermostActiveScope()
                            .queryForOne(
                                    new NameQuery(
                                            tyQualifier,
                                            tySymbol,
                                            ImportStrategy.IMPORT_NAMED,
                                            FacilityStrategy.FACILITY_INSTANTIATE,
                                            true)).toProgramTypeEntry(
                                    tyLocation);

            ty.setProgramTypeValue(type.getProgramType());
            ty.setMathType(myTypeGraph.MTYPE);
            ty.setMathTypeValue(type.getModelType());
        }
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(tyQualifier, tyName, tyLocation);
        }
        catch (DuplicateSymbolException dse) {
            //TODO : Error gracefully
            throw new RuntimeException(dse);
        }
    }

    @Override
    public void postFacilityDec(FacilityDec facility) {
        try {
            myBuilder.getInnermostActiveScope().addFacility(facility);
        }
        catch (DuplicateSymbolException dse) {
            duplicateSymbol(facility.getName().getName(), facility.getName()
                    .getLocation());
        }
    }

    @Override
    public void postMathAssertionDec(MathAssertionDec node) {
        //if (node.getAssertion() != null) {
        expectType(node.getAssertion(), myTypeGraph.BOOLEAN);
        //}

        String name = node.getName().getName();
        try {

            myBuilder.getInnermostActiveScope().addTheorem(name, node);
        }
        catch (DuplicateSymbolException dse) {
            duplicateSymbol(name, node.getName().getLocation());
        }

        Populator.emitDebug("New theorem: " + name);
    }

    @Override
    public void postMathVarDec(MathVarDec node) {

        MTType mathTypeValue = node.getTy().getMathTypeValue();
        String varName = node.getName().getName();

        if (myCurrentDirectDefinition != null
                && mathTypeValue.isKnownToContainOnlyMTypes()
                && myDefinitionNamedTypes.contains(varName)) {
            throw new SourceErrorException("Introduction of type parameter "
                    + "must precede any use of that variable name.", node
                    .getLocation());
        }

        node.setMathType(mathTypeValue);

        SymbolTableEntry.Quantification q =
                SymbolTableEntry.Quantification.UNIVERSAL;
        //TODO : the for-alls in a type theorem should be normal QuantExps and
        //       the quantification here should depend on the innermost QuantExp
        addBinding(varName, node.getName().getLocation(), q, node,
                mathTypeValue, null);

        Populator.emitDebug("  New variable: " + varName + " of type "
                + mathTypeValue.toString() + " with quantification " + q);
    }

    @Override
    public void postVarDec(VarDec programVar) {

        MTType mathTypeValue = programVar.getTy().getMathTypeValue();
        String varName = programVar.getName().getName();

        programVar.setMathType(mathTypeValue);

        try {
            myBuilder.getInnermostActiveScope().addProgramVariable(varName,
                    programVar, programVar.getTy().getProgramTypeValue());
        }
        catch (DuplicateSymbolException dse) {
            duplicateSymbol(varName, programVar.getLocation());
        }

        Populator.emitDebug("  New program variable: " + varName + " of type "
                + mathTypeValue.toString() + " with quantification NONE");
    }

    @Override
    public void postVariableNameExp(VariableNameExp node) {
        try {
            ProgramVariableEntry entry =
                    myBuilder.getInnermostActiveScope().queryForOne(
                            new ProgramVariableQuery(node.getQualifier(), node
                                    .getName()));

            node.setProgramType(entry.getProgramType());

            //Handle math typing stuff
            postSymbolExp(node.getQualifier(), node.getName().getName(), node);
        }
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(node.getQualifier(), node.getName().getName(), node
                    .getLocation());
        }
        catch (DuplicateSymbolException dse) {
            throw new RuntimeException("ToDo"); //TODO
        }
    }

    @Override
    public void postProgramParamExp(ProgramParamExp node) {
        List<ProgramExp> args = node.getArguments();

        List<PTType> argTypes = new LinkedList<PTType>();
        for (ProgramExp arg : args) {
            argTypes.add(arg.getProgramType());
        }

        try {
            OperationEntry op =
                    myBuilder.getInnermostActiveScope().queryForOne(
                            new OperationQuery(null, node.getName(), argTypes));

            node.setProgramType(op.getReturnType());
            node.setMathType(op.getReturnType().toMath());
        }
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(null, node.getName().getName(), node.getLocation());
        }
        catch (DuplicateSymbolException dse) {
            //This should be caught earlier, when the duplicate operation is
            //created
            throw new RuntimeException(dse);
        }
    }

    @Override
    public void postOldExp(OldExp exp) {
        exp.setMathType(exp.getExp().getMathType());
        exp.setMathTypeValue(exp.getExp().getMathTypeValue());
    }

    @Override
    public boolean walkTypeAssertionExp(TypeAssertionExp node) {

        preTypeAssertionExp(node);

        //If we exist as an implicit type parameter, there's no way our 
        //expression can know its own type (that's defined by the asserted Ty),
        //so we skip walking it and let postTypeAssertionExp() set its type for
        //it
        if (myTypeValueDepth == 0) {
            myWalker.visit(node.getExp());
        }

        myWalker.visit(node.getAssertedTy());

        postTypeAssertionExp(node);

        return true;
    }

    @Override
    public void postTypeAssertionExp(TypeAssertionExp node) {
        if (myTypeValueDepth == 0
                && (myExpressionDepth > 2 || !myInTypeTheoremBindingExpFlag)) {
            throw new SourceErrorException("This construct only permitted in "
                    + "type declarations or in expressions matching: \n\n"
                    + "   Type Theorem <name>: <quantifiers>, \n"
                    + "       [<condition> implies] <expression> : "
                    + "<assertedType>", node.getLocation());
        }
        //Note that postTypeTheoremDec() checks the "form" of a type theorem at
        //the top two levels.  So all we're checking for here is that the type
        //assertion didn't happen deeper than that (where it shouldn't appear).

        //If we're the assertion of a type theorem, then postTypeTheoremDec()
        //will take care of any logic.  If we're part of a type declaration,
        //on the other hand, we've got some bookkeeping to do...
        if (myTypeValueDepth > 0) {
            try {
                VarExp nodeExp = (VarExp) node.getExp();
                try {
                    myBuilder.getInnermostActiveScope().addBinding(
                            nodeExp.getName().getName(),
                            SymbolTableEntry.Quantification.UNIVERSAL, node,
                            node.getAssertedTy().getMathType());
                    node.setMathType(node.getAssertedTy().getMathType());
                    node.setMathTypeValue(new MTNamed(myTypeGraph, nodeExp
                            .getName().getName()));

                    //See walkTypeAssertionExp(): we are responsible for 
                    //setting the VarExp's type.
                    nodeExp.setMathType(node.getAssertedTy().getMathType());
                    node.setMathTypeValue(new MTNamed(myTypeGraph, nodeExp
                            .getName().getName()));

                    if (myDefinitionNamedTypes.contains(nodeExp.getName()
                            .getName())) {
                        //Regardless of where in the expression it appears, an
                        //implicit type parameter exists at the top level of a
                        //definition, and thus a definition that contains, e.g.,
                        //an implicit type parameter T cannot make reference
                        //to some existing type with that name (except via full
                        //qualification), thus the introduction of an implicit
                        //type parameter must precede any use of that 
                        //parameter's name, even if the name exists in-scope
                        //before the parameter is declared
                        throw new SourceErrorException("Introduction of "
                                + "implicit type parameter must precede any "
                                + "use of that variable name.", nodeExp
                                .getLocation());
                    }

                    //Note that a redudantly named type parameter would be 
                    //caught when we add a symbol to the symbol table, so no
                    //need to check here
                    myDefinitionSchematicTypes.put(nodeExp.getName().getName(),
                            node.getAssertedTy().getMathType());
                }
                catch (DuplicateSymbolException dse) {
                    duplicateSymbol(nodeExp.getName().getName(), nodeExp
                            .getLocation());
                }
            }
            catch (ClassCastException cce) {
                throw new SourceErrorException("Must be a variable name.", node
                        .getExp().getLocation());
            }
        }
        else {
            node.setMathType(myTypeGraph.BOOLEAN);
        }
    }

    @Override
    public void preDefinitionDec(DefinitionDec node) {
        myBuilder.startScope(node);

        if (!node.isInductive()) {
            myCurrentDirectDefinition = node;
        }

        myDefinitionSchematicTypes.clear();
        myDefinitionNamedTypes.clear();
    }

    @Override
    public void postDefinitionDec(DefinitionDec node) {
        myBuilder.endScope();

        MTType declaredType = node.getReturnTy().getMathTypeValue();

        if (node.getDefinition() != null) {
            expectType(node.getDefinition(), declaredType);
        }
        else if (node.isInductive()) {
            expectType(node.getBase(), myTypeGraph.BOOLEAN);
            expectType(node.getHypothesis(), myTypeGraph.BOOLEAN);
        }

        List<MathVarDec> listVarDec = node.getParameters();
        if (listVarDec != null) {
            declaredType = new MTFunction(myTypeGraph, node);
        }

        String definitionSymbol = node.getName().getName();

        MTType typeValue = null;
        if (node.getDefinition() != null) {
            typeValue = node.getDefinition().getMathTypeValue();
        }

        //Note that, even if typeValue is null at this point, if declaredType
        //returns true from knownToContainOnlyMTypes(), a new type value will
        //still be created by the symbol table
        addBinding(definitionSymbol, node.getName().getLocation(), node,
                declaredType, typeValue, myDefinitionSchematicTypes);

        Populator.emitDebug("New definition: " + definitionSymbol + " of type "
                + declaredType
                + ((typeValue != null) ? " with type value " + typeValue : ""));

        myCurrentDirectDefinition = null;
    }

    @Override
    public void preQuantExp(QuantExp node) {
        Populator.emitDebug("Entering preQuantExp...");
        myBuilder.startScope(node);
    }

    @Override
    public boolean walkQuantExp(QuantExp node) {
        preQuantExp(node);

        Populator.emitDebug("Entering walkQuantExp...");
        List<MathVarDec> vars = node.getVars();

        SymbolTableEntry.Quantification quantification;
        switch (node.getOperator()) {
        case QuantExp.EXISTS:
            quantification = SymbolTableEntry.Quantification.EXISTENTIAL;
            break;
        case QuantExp.FORALL:
            quantification = SymbolTableEntry.Quantification.UNIVERSAL;
            break;
        default:
            throw new RuntimeException("Unrecognized quantification type: "
                    + node.getOperator());
        }

        myActiveQuantifications.push(quantification);
        for (MathVarDec v : vars) {
            myWalker.visit(v);
        }
        myActiveQuantifications.pop();

        myActiveQuantifications.push(SymbolTableEntry.Quantification.NONE);
        myWalker.visit(node.getBody());
        myActiveQuantifications.pop();

        Populator.emitDebug("Exiting walkQuantExp.");

        postQuantExp(node);

        //This indicates that we've overrided the default
        return true;
    }

    @Override
    public void postQuantExp(QuantExp node) {
        myBuilder.endScope();

        expectType(node.getBody(), myTypeGraph.BOOLEAN);
        node.setMathType(myTypeGraph.BOOLEAN);
    }

    @Override
    public void postIfExp(IfExp exp) {
        //An "if expression" is a functional condition, as in the following 
        //example:
        //   x = (if (y > 0) then y else -y)
        //Its condition had better be a boolean.  Its type resolves to the 
        //shared type of its branches.

        //TODO : Currently, the parser permits the else clause to be optional.
        //       That is nonsense in a functional context and should be fixed.
        if (exp.getElseclause() == null) {
            throw new RuntimeException("IfExp has no else clause.  The "
                    + "parser should be changed to disallow this and this "
                    + "error should be removed.");
        }

        expectType(exp.getTest(), myTypeGraph.BOOLEAN);

        Exp ifClause = exp.getThenclause();
        Exp elseClause = exp.getElseclause();

        MTType ifType = ifClause.getMathType();
        MTType elseType = elseClause.getMathType();

        boolean ifIsSuperType = myTypeGraph.isSubtype(elseType, ifType);

        //One of these had better be a (non-strict) subtype of the other
        if (!ifIsSuperType && !myTypeGraph.isSubtype(ifType, elseType)) {
            throw new SourceErrorException("Branches must share a type.\n"
                    + "If branch:   " + ifType + "\n" + "Else branch: "
                    + elseType, exp.getLocation());
        }

        MTType finalType, finalTypeValue;
        if (ifIsSuperType) {
            finalType = ifType;
            finalTypeValue = ifClause.getMathTypeValue();
        }
        else {
            finalType = elseType;
            finalTypeValue = elseClause.getMathTypeValue();
        }

        exp.setMathType(finalType);
        exp.setMathTypeValue(finalTypeValue);
    }

    @Override
    public void preArbitraryExpTy(ArbitraryExpTy node) {
        enteringTypeValueNode();
    }

    @Override
    public void postSetExp(SetExp e) {
        MathVarDec varDec = e.getVar();
        MTType varType = varDec.getMathType();

        Exp body = e.getBody();

        expectType(body, myTypeGraph.BOOLEAN);

        if (e.getWhere() != null) {
            body = myTypeGraph.formConjunct(e.getWhere(), body);
        }

        e.setMathType(new MTSetRestriction(myTypeGraph, varType, varDec
                .getName().getName(), body));
        e.setMathTypeValue(new MTPowertypeApplication(myTypeGraph, varType));
    }

    @Override
    public void postIntegerExp(IntegerExp e) {
        postSymbolExp(e.getQualifier(), "" + e.getValue(), e);
    }

    @Override
    public void postProgramIntegerExp(ProgramIntegerExp e) {
        e.setProgramType(getIntegerProgramType());

        //Do math type stuff
        postSymbolExp(null, "" + e.getValue(), e);
    }

    @Override
    public void postProgramOpExp(ProgramOpExp e) {
        e.setProgramType(e.getProgramType(myTypeGraph));
        e.setMathType(e.getProgramType().toMath());
    }

    @Override
    public void postVarExp(VarExp e) {
        MathSymbolEntry intendedEntry =
                postSymbolExp(e.getQualifier(), e.getName().getName(), e);

        if (myTypeValueDepth > 0 && e.getQualifier() == null) {
            try {
                intendedEntry.getTypeValue();
                myDefinitionNamedTypes.add(intendedEntry.getName());
            }
            catch (SymbolNotOfKindTypeException snokte) {
                //No problem, just don't need to add it
            }
        }

        e.setQuantification(intendedEntry.getQuantification()
                .toVarExpQuantificationCode());
    }

    @Override
    public void postAbstractFunctionExp(AbstractFunctionExp foundExp) {

        MTFunction foundExpType;
        foundExpType = foundExp.getConservativePreApplicationType(myTypeGraph);

        Populator.emitDebug("Expression: " + foundExp.toString() + "("
                + foundExp.getLocation() + ") " + " of type "
                + foundExpType.toString());

        MathSymbolEntry intendedEntry = getIntendedFunction(foundExp);
        MTFunction expectedType = (MTFunction) intendedEntry.getType();

        //We know we match expectedType--otherwise the above would have thrown
        //an exception.

        foundExp.setMathType(expectedType.getRange());
        foundExp.setQuantification(intendedEntry.getQuantification());

        if (myTypeValueDepth > 0) {
            //I had better identify a type
            MTFunction entryType = (MTFunction) intendedEntry.getType();

            List<MTType> arguments = new LinkedList<MTType>();
            MTType argTypeValue;
            for (Exp arg : foundExp.getParameters()) {
                argTypeValue = arg.getMathTypeValue();

                if (argTypeValue == null) {
                    notAType(arg);
                }

                arguments.add(argTypeValue);
            }

            foundExp.setMathTypeValue(entryType.getApplicationType(
                    intendedEntry.getName(), arguments));
        }
    }

    @Override
    public void postTupleExp(TupleExp node) {
        //See the note in TupleExp on why TupleExp isn't an AbstractFunctionExp

        //This looks weird, but we're converting from the ridiculous 
        //RESOLVE-internal List into an ordinary java.util.List because we don't
        //live in bizarro-world
        List<Exp> fields = new LinkedList<Exp>(node.getFields());

        if (fields.size() < 2) {
            //We assert that this can't happen, but who knows?
            throw new RuntimeException("Unanticipated tuple size.");
        }

        List<MTCartesian.Element> fieldTypes =
                new LinkedList<MTCartesian.Element>();
        for (Exp field : fields) {
            fieldTypes.add(new MTCartesian.Element(field.getMathType()));
        }

        node.setMathType(new MTCartesian(myTypeGraph, fieldTypes));
    }

    @Override
    public void postArbitraryExpTy(ArbitraryExpTy node) {
        leavingTypeValueNode();

        Exp typeExp = node.getArbitraryExp();
        MTType mathType = typeExp.getMathType();
        MTType mathTypeValue = typeExp.getMathTypeValue();
        if (!mathType.isKnownToContainOnlyMTypes()) {
            notAType(typeExp);
        }

        node.setMathType(mathType);
        node.setMathTypeValue(mathTypeValue);
    }

    @Override
    public void preExp(Exp node) {
        myExpressionDepth++;
    }

    @Override
    public void postAny(ResolveConceptualElement e) {
        if (e instanceof Ty) {
            Ty eTy = (Ty) e;
            if (eTy.getMathTypeValue() == null) {
                throw new RuntimeException(
                        "Ty "
                                + e
                                + " ("
                                + e.getClass()
                                + ", "
                                + e.getLocation()
                                + ") got through the populator with no math type value.");
            }
            if (!(e instanceof ArbitraryExpTy)
                    && eTy.getProgramTypeValue() == null) {
                throw new RuntimeException("Ty " + e + " (" + e.getClass()
                        + ", " + e.getLocation() + ") got through the "
                        + "populator with no program type value.");
            }
        }
    }

    @Override
    public void postExp(Exp node) {

        //myMathModeFlag && 
        if (node.getMathType() == null) {
            throw new RuntimeException("Exp " + node + " (" + node.getClass()
                    + ", " + node.getLocation()
                    + ") got through the populator " + "with no math type.");
        }

        if (node instanceof ProgramExp
                && ((ProgramExp) node).getProgramType() == null) {
            throw new RuntimeException("Exp " + node + " (" + node.getClass()
                    + ", " + node.getLocation()
                    + ") got through the populator " + "with no program type.");
        }

        myExpressionDepth--;
    }

    @Override
    public void preTypeTheoremDec(TypeTheoremDec node) {
        myBuilder.startScope(node);
        myInTypeTheoremBindingExpFlag = false;
    }

    @Override
    public void postTypeTheoremDecMyUniversalVars(TypeTheoremDec node) {
        myInTypeTheoremBindingExpFlag = true;
    }

    @Override
    public void postTypeTheoremDec(TypeTheoremDec node) {
        node.setMathType(myTypeGraph.BOOLEAN);

        Exp assertion = node.getAssertion();

        Exp condition;
        Exp bindingExpression;
        ArbitraryExpTy typeExp;

        try {
            if (assertion instanceof InfixExp) {
                InfixExp assertionsAsInfixExp = (InfixExp) assertion;
                String operator = assertionsAsInfixExp.getOperatorAsString();

                if (operator.equals("implies")) {
                    condition = assertionsAsInfixExp.getLeft();
                    assertion = assertionsAsInfixExp.getRight();
                }
                else {
                    throw new ClassCastException();
                }
            }
            else {
                condition = myTypeGraph.getTrueVarExp();
            }

            TypeAssertionExp assertionAsTAE = (TypeAssertionExp) assertion;

            bindingExpression = assertionAsTAE.getExp();
            typeExp = assertionAsTAE.getAssertedTy();

            try {
                myTypeGraph.addRelationship(bindingExpression, typeExp
                        .getMathTypeValue(), condition, myBuilder
                        .getInnermostActiveScope());
            }
            catch (IllegalArgumentException iae) {
                throw new SourceErrorException(iae.getMessage(), node
                        .getLocation());
            }
        }
        catch (ClassCastException cse) {
            throw new SourceErrorException("Top level of type theorem "
                    + "assertion must be 'implies' or ':'.", assertion
                    .getLocation());
        }

        myBuilder.endScope();
    }

    @Override
    public void postModuleArgumentItem(ModuleArgumentItem i) {

        if (i.getEvalExp() != null) {
            i.setProgramTypeValue(i.getEvalExp().getProgramType());
        }
        else if (i.getName() != null) {
            List<SymbolTableEntry> es =
                    myBuilder.getInnermostActiveScope().query(
                            new NameQuery(i.getQualifier(), i.getName(),
                                    ImportStrategy.IMPORT_NAMED,
                                    FacilityStrategy.FACILITY_INSTANTIATE,
                                    false));

            if (es.isEmpty()) {
                noSuchSymbol(i.getQualifier(), i.getName());
            }
            else if (es.size() > 1) {
                ambiguousSymbol(i.getName(), es);
            }
            else {
                ProgramTypeEntry e =
                        es.get(0).toProgramTypeEntry(i.getLocation());

                i.setProgramTypeValue(e.getProgramType());
            }
        }
    }

    @Override
    public void postModuleDec(ModuleDec node) {
        myBuilder.endScope();

        Populator.emitDebug("END MATH POPULATOR\n----------------------\n");
    }

    @Override
    public void preVariableDotExp(VariableDotExp e) {
        //Dot expressions are handled ridiculously, even for this compiler, so
        //this method just deals with the cases we've encountered so far and 
        //lots of assumptions are made.  Expect it to break frequently when you
        //encounter some new case

        PosSymbol firstNamePos =
                ((VariableNameExp) e.getSegments().get(0)).getName();
        String firstName = firstNamePos.getName();

        try {
            ProgramVariableEntry eEntry =
                    myBuilder.getInnermostActiveScope().queryForOne(
                            new NameQuery(null, firstName))
                            .toProgramVariableEntry(firstNamePos.getLocation());
            e.getSegments().get(0).setProgramType(eEntry.getProgramType());
            e.getSegments().get(0)
                    .setMathType(eEntry.getProgramType().toMath());

            PTType eType = eEntry.getProgramType();

            if (eType instanceof PTRepresentation) {
                eType = ((PTRepresentation) eType).getBaseType();
            }

            PTRecord recordType = (PTRecord) eType;

            String fieldName =
                    ((VariableNameExp) e.getSegments().get(1)).getName()
                            .getName();

            PTType fieldType = recordType.getFieldType(fieldName);
            e.getSegments().get(1).setProgramType(fieldType);
            e.setProgramType(fieldType);

            e.getSegments().get(1).setMathType(fieldType.toMath());
            e.setMathType(fieldType.toMath());
        }
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(null, firstNamePos);
        }
        catch (DuplicateSymbolException dse) {
            //This flavor of name query shouldn't be able to throw this--we're
            //only looking in the local module so there's no overloading
            throw new RuntimeException(dse);
        }
    }

    @Override
    public boolean walkVariableDotExp(VariableDotExp e) {

        preAny(e);
        preExp(e);
        preProgramExp(e);
        preVariableExp(e);
        preVariableDotExp(e);

        postVariableDotExp(e);
        postVariableExp(e);
        postProgramExp(e);
        postExp(e);
        postAny(e);

        return true;
    }

    @Override
    public void preDotExp(DotExp e) {
        if (e.getSegments().get(0).toString().equals("Conc")) {
            if (e.getSegments().size() != 2
                    || !e.getSegments().get(1).toString().equals(
                            myTypeDefinitionEntry.getProgramType()
                                    .getExemplarName())) {
                throw new RuntimeException("No idea what's going on here.");
            }

            //No idea.  Conc is a made up thing.
            e.getSegments().get(0).setMathType(myTypeGraph.BOOLEAN);

            e.getSegments().get(1).setMathType(
                    myTypeDefinitionEntry.getModelType());
            e.setMathType(myTypeDefinitionEntry.getModelType());
        }
    }

    public VarExp getSegmentAsVarExp(Exp e) {
        if (e instanceof OldExp) {
            e = ((OldExp) e).getExp();
        }
        
        return (VarExp) e;
    }
    
    @Override
    public boolean walkDotExp(DotExp dot) {
        boolean conceptual = dot.getSegments().get(0).toString().equals("Conc");

        preAny(dot);
        preExp(dot);
        preDotExp(dot);

        if (!conceptual) {
            List<Exp> segments = dot.getSegments();
            VarExp first = getSegmentAsVarExp(segments.get(0));
            VarExp second = getSegmentAsVarExp(segments.get(1));

            MathSymbolEntry entry;
            int skip;
            //The first segment either identifies a locally-accessible variable 
            //or a module
            try {
                //Treat the first segment as a variable name
                entry =
                        myBuilder
                                .getInnermostActiveScope()
                                .queryForOne(
                                        new NameQuery(
                                                null,
                                                first.getName(),
                                                ImportStrategy.IMPORT_NAMED,
                                                FacilityStrategy.FACILITY_IGNORE,
                                                true)).toMathSymbolEntry(
                                        first.getLocation());
                skip = 1;
                
                //This looks a little weird--keep in mind that "first" is not
                //necessarily the Exp in segments.get(0).  segments.get(0) could
                //be an OldExp wrapping "first".  OldExp will set the type of
                //the inner expression
                segments.get(0).setMathType(entry.getType());
                try {
                    segments.get(0).setMathTypeValue(entry.getTypeValue());
                }
                catch (SymbolNotOfKindTypeException snokte) {

                }
            }
            catch (NoSuchSymbolException nsse) {
                try {
                    //Treat the first segment as a qualifier
                    entry =
                            myBuilder.getInnermostActiveScope().queryForOne(
                                    new NameQuery(first.getName(), second
                                            .getName(),
                                            ImportStrategy.IMPORT_NAMED,
                                            FacilityStrategy.FACILITY_IGNORE,
                                            true)).toMathSymbolEntry(
                                    first.getLocation());
                    skip = 2;
                    
                    //See not above on why we use segments.get(0) instead of
                    //"first"
                    segments.get(0).setMathType(myTypeGraph.BOOLEAN);
                    segments.get(1).setMathType(entry.getType());
                    try {
                        segments.get(1).setMathTypeValue(entry.getTypeValue());
                    }
                    catch (SymbolNotOfKindTypeException snokte) {

                    }
                }
                catch (NoSuchSymbolException nsse2) {
                    noSuchSymbol(first.getName(), second.getName());
                    throw new RuntimeException(); //This will never fire
                }
                catch (DuplicateSymbolException dse) {
                    //This shouldn't be possible--there can only be one symbol
                    //with the given name inside a particular module
                    throw new RuntimeException();
                }
            }
            catch (DuplicateSymbolException dse) {
                duplicateSymbol(first.getName());
                throw new RuntimeException(); //This will never fire
            }

            Location lastGood = null;
            Iterator<Exp> segmentsIter = segments.iterator();
            for (int i = 0; i < skip; i++) {
                lastGood = segmentsIter.next().getLocation();
            }

            MTType curType = entry.getType();
            MTCartesian curTypeCartesian;
            VarExp curSegment = null;
            while (segmentsIter.hasNext()) {
                try {
                    curTypeCartesian = (MTCartesian) curType;

                    try {
                        curSegment = (VarExp) segmentsIter.next();
                        curType =
                                curTypeCartesian.getFactor(curSegment.getName()
                                        .getName());
                        lastGood = curSegment.getLocation();

                        curSegment.setMathType(curType);
                        curSegment.setMathTypeValue(curType.getType());
                    }
                    catch (ClassCastException cce2) {
                        throw new RuntimeException(cce2);
                    }
                }
                catch (ClassCastException cce) {
                    throw new SourceErrorException("Value is not a tuple.",
                            lastGood);
                }
                catch (NoSuchElementException nsee) {
                    throw new SourceErrorException("No such field.", curSegment
                            .getLocation());
                }
            }
        }

        postDotExp(dot);
        postExp(dot);
        postAny(dot);

        return true;
    }

    @Override
    public void postDotExp(DotExp e) {
        //Might already have been set in preDotExp(), in which case our children
        //weren't visited
        if (e.getMathType() == null) {
            edu.clemson.cs.r2jt.collections.List<Exp> segments =
                    e.getSegments();

            Exp lastSeg = segments.get(segments.size() - 1);

            e.setMathType(lastSeg.getMathType());
            e.setMathTypeValue(lastSeg.getMathTypeValue());
        }
    }

    //-------------------------------------------------------------------
    //   Error handling
    //-------------------------------------------------------------------

    public void noSuchModule(PosSymbol qualifier) {
        throw new SourceErrorException(
                "Module does not exist or is not in scope.", qualifier);
    }

    public void noSuchSymbol(PosSymbol qualifier, PosSymbol symbol) {
        noSuchSymbol(qualifier, symbol.getName(), symbol.getLocation());
    }

    public void noSuchSymbol(PosSymbol qualifier, String symbolName, Location l) {

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

    public <T extends SymbolTableEntry> void ambiguousSymbol(PosSymbol symbol,
            List<T> candidates) {
        ambiguousSymbol(symbol.getName(), symbol.getLocation(), candidates);
    }

    public <T extends SymbolTableEntry> void ambiguousSymbol(String symbolName,
            Location l, List<T> candidates) {

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

    public void notAType(SymbolTableEntry entry, Location l) {
        throw new SourceErrorException(entry.getSourceModuleIdentifier()
                .fullyQualifiedRepresentation(entry.getName())
                + " is not known to be a type.", l);
    }

    public void notAType(Exp e) {
        throw new SourceErrorException("Not known to be a type.", e
                .getLocation());
    }

    public void expected(Exp e, MTType expectedType) {
        throw new SourceErrorException("Expected: " + expectedType
                + "\nFound: " + e.getMathType(), e.getLocation());
    }

    public void duplicateSymbol(PosSymbol symbol) {
        duplicateSymbol(symbol.getName(), symbol.getLocation());
    }

    public void duplicateSymbol(String symbol, Location l) {
        throw new SourceErrorException("Duplicate symbol: " + symbol, l);
    }

    public void expectType(Exp e, MTType expectedType) {
        if (!myTypeGraph.isKnownToBeIn(e, expectedType)) {
            expected(e, expectedType);
        }
    }

    //-------------------------------------------------------------------
    //   Helper functions
    //-------------------------------------------------------------------

    private PTType getIntegerProgramType() {
        PTType result;

        try {
            ProgramTypeEntry type =
                    myBuilder.getInnermostActiveScope().queryForOne(
                            new NameQuery(null, "Integer",
                                    ImportStrategy.IMPORT_NAMED,
                                    FacilityStrategy.FACILITY_INSTANTIATE,
                                    false)).toProgramTypeEntry(null);

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

    private SymbolTableEntry addBinding(String name, Location l,
            SymbolTableEntry.Quantification q,
            ResolveConceptualElement definingElement, MTType type,
            MTType typeValue, Map<String, MTType> schematicTypes) {
        if (type == null) {
            throw new NullPointerException();
        }
        else {
            try {
                return myBuilder.getInnermostActiveScope().addBinding(name, q,
                        definingElement, type, typeValue, schematicTypes,
                        myGenericTypes);
            }
            catch (DuplicateSymbolException dse) {
                duplicateSymbol(name, l);
                throw new RuntimeException(); //This will never fire
            }
        }
    }

    private SymbolTableEntry addBinding(String name, Location l,
            ResolveConceptualElement definingElement, MTType type,
            MTType typeValue, Map<String, MTType> schematicTypes) {
        return addBinding(name, l, SymbolTableEntry.Quantification.NONE,
                definingElement, type, typeValue, schematicTypes);
    }

    private SymbolTableEntry addBinding(String name, Location l,
            SymbolTableEntry.Quantification q,
            ResolveConceptualElement definingElement, MTType type,
            Map<String, MTType> schematicTypes) {
        return addBinding(name, l, q, definingElement, type, null,
                schematicTypes);
    }

    private SymbolTableEntry addBinding(String name, Location l,
            ResolveConceptualElement definingElement, MTType type,
            Map<String, MTType> schematicTypes) {
        return addBinding(name, l, SymbolTableEntry.Quantification.NONE,
                definingElement, type, null, schematicTypes);
    }

    private void enteringTypeValueNode() {
        myTypeValueDepth++;
    }

    private void leavingTypeValueNode() {
        myTypeValueDepth--;
    }

    private static MTFunction deschematize(MTFunction original,
            Exp soleParameter) {

        Map<String, MTType> bindings = new HashMap<String, MTType>();

        MTType domain = original.getDomain();
        if (domain instanceof MTCartesian) {
            if (!(soleParameter instanceof TupleExp)) {
                deschematize((MTCartesian) domain, (TupleExp) soleParameter,
                        bindings);
            }
        }
        else {
            MTType parameterTypeValue = soleParameter.getMathTypeValue();
            if (parameterTypeValue != null) {
                String tag = original.getSingleParameterName();

                if (tag != null) {
                    bindings.put(tag, parameterTypeValue);
                }
            }

            deschematize(original, soleParameter.getMathType(), bindings);
        }

        VariableReplacingVisitor replacer =
                new VariableReplacingVisitor(bindings);

        original.accept(replacer);

        return (MTFunction) replacer.getFinalExpression();
    }

    private static void deschematize(MTCartesian original, TupleExp value,
            Map<String, MTType> bindings) {

        int originalSize = original.size();
        if (originalSize != value.getSize()) {
            throw new IllegalArgumentException();
        }

        String tag;
        Exp field;
        MTType fieldTypeValue, factor;
        for (int i = 0; i < originalSize; i++) {
            factor = original.getFactor(i);
            field = value.getField(i);
            fieldTypeValue = field.getMathTypeValue();

            if (fieldTypeValue != null) {
                tag = original.getTag(i);

                if (tag != null) {
                    bindings.put(tag, fieldTypeValue);
                }
            }

            deschematize(factor, field.getMathType(), bindings);
        }
    }

    private static void deschematize(MTType original, MTType value,
            Map<String, MTType> bindings) {

        ParameterGenericApplyingVisitor genericApplier =
                new ParameterGenericApplyingVisitor(bindings);

        genericApplier.visit(original, value);

        if (genericApplier.encounteredError()) {
            throw new IllegalArgumentException();
        }
    }

    public static void emitDebug(String msg) {
        if (PRINT_DEBUG) {
            System.out.println(msg);
        }
    }

    private MathSymbolEntry getIntendedEntry(PosSymbol qualifier,
            String symbolName, Exp node) {

        MathSymbolEntry result;

        try {
            result =
                    myBuilder.getInnermostActiveScope().queryForOne(
                            new MathSymbolQuery(qualifier, symbolName, node
                                    .getLocation()));
        }
        catch (DuplicateSymbolException dse) {
            duplicateSymbol(symbolName, node.getLocation());
            throw new RuntimeException(); //This will never fire
        }
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(qualifier, symbolName, node.getLocation());
            throw new RuntimeException(); //This will never fire
        }

        return result;
    }

    private void setSymbolTypeValue(Exp node, String symbolName,
            MathSymbolEntry intendedEntry) {

        try {
            if (intendedEntry.getQuantification() == Quantification.NONE) {
                node.setMathTypeValue(intendedEntry.getTypeValue());
            }
            else {
                if (intendedEntry.getType().isKnownToContainOnlyMTypes()) {
                    node.setMathTypeValue(new MTNamed(myTypeGraph, symbolName));
                }
            }
        }
        catch (SymbolNotOfKindTypeException snokte) {
            if (myTypeValueDepth > 0) {
                //I had better identify a type
                notAType(intendedEntry, node.getLocation());
            }
        }
    }

    private MathSymbolEntry postSymbolExp(PosSymbol qualifier,
            String symbolName, Exp node) {

        MathSymbolEntry intendedEntry =
                getIntendedEntry(qualifier, symbolName, node);
        node.setMathType(intendedEntry.getType());

        setSymbolTypeValue(node, symbolName, intendedEntry);

        Populator.emitDebug("Processed symbol " + symbolName + " with type "
                + node.getMathType() + ", referencing math type "
                + node.getMathTypeValue());

        return intendedEntry;
    }

    /**
     * <p>For a given <code>AbstractFunctionExp</code>, finds the entry in the
     * symbol table to which it refers.  For a complete discussion of the
     * algorithm used, see <a href="http://sourceforge.net/apps/mediawiki/resolve/index.php?title=Package_Search_Algorithm">
     * Package Search Algorithm</a>.</p>
     */
    private MathSymbolEntry getIntendedFunction(AbstractFunctionExp e) {

        //TODO : All this logic should be encapsulated into a SymbolQuery called
        //       MathFunctionQuery.

        MTFunction eType = e.getConservativePreApplicationType(myTypeGraph);

        PosSymbol eOperator =
                ((AbstractFunctionExp) e).getOperatorAsPosSymbol();
        String eOperatorString = eOperator.getSymbol().getName();

        List<MathSymbolEntry> sameNameFunctions =
                myBuilder.getInnermostActiveScope().query(
                        new MathFunctionNamedQuery(e.getQualifier(), e
                                .getOperatorAsPosSymbol()));

        if (sameNameFunctions.isEmpty()) {
            throw new SourceErrorException("No such function.", e.getLocation());
        }

        MathSymbolEntry intendedEntry;
        try {
            intendedEntry = getExactDomainTypeMatch(e, sameNameFunctions);
        }
        catch (NoSolutionException nse) {
            try {
                intendedEntry = getInexactDomainTypeMatch(e, sameNameFunctions);
            }
            catch (NoSolutionException nsee2) {
                boolean foundOne = false;
                String errorMessage =
                        "No function applicable for " + "domain: "
                                + eType.getDomain() + "\n\nCandidates:\n";

                for (SymbolTableEntry entry : sameNameFunctions) {

                    if (entry instanceof MathSymbolEntry
                            && ((MathSymbolEntry) entry).getType() instanceof MTFunction) {
                        errorMessage +=
                                "\t" + entry.getName() + " : "
                                        + ((MathSymbolEntry) entry).getType()
                                        + "\n";

                        foundOne = true;
                    }
                }

                if (!foundOne) {
                    throw new SourceErrorException("No such function.", e
                            .getLocation());
                }

                throw new SourceErrorException(errorMessage, e.getLocation());
            }
        }

        if (intendedEntry.getDefiningElement() == myCurrentDirectDefinition) {
            throw new SourceErrorException("Direct definition cannot "
                    + "contain recursive call.", e.getLocation());
        }

        MTFunction intendedEntryType = (MTFunction) intendedEntry.getType();

        Populator.emitDebug("Matching " + eOperatorString + " : " + eType
                + " to " + intendedEntry.getName() + " : " + intendedEntryType
                + ".");

        return intendedEntry;
    }

    private MathSymbolEntry getExactDomainTypeMatch(AbstractFunctionExp e,
            List<MathSymbolEntry> candidates) throws NoSolutionException {

        return getDomainTypeMatch(e, candidates, EXACT_DOMAIN_MATCH);
    }

    private MathSymbolEntry getInexactDomainTypeMatch(AbstractFunctionExp e,
            List<MathSymbolEntry> candidates) throws NoSolutionException {

        return getDomainTypeMatch(e, candidates, INEXACT_DOMAIN_MATCH);
    }

    //TODO : This is a mess, clean it up!
    private MathSymbolEntry getDomainTypeMatch(AbstractFunctionExp e,
            List<MathSymbolEntry> candidates,
            TypeComparison<AbstractFunctionExp, MTFunction> comparison)
            throws NoSolutionException {

        //Exp soleParameter = e.getSoleParameter(myTypeGraph);

        MTFunction eType = e.getConservativePreApplicationType(myTypeGraph);

        /*
        eType =
                (MTFunction) eType
                        .getCopyWithVariablesSubstituted(myGenericTypes);
        e = TypeGraph.getCopyWithVariablesSubstituted(e, myGenericTypes);
         */

        MathSymbolEntry match = null;

        MTFunction candidateType;
        for (MathSymbolEntry candidate : candidates) {
            if (candidate.getType() instanceof MTFunction) {

                try {
                    candidate =
                            candidate.deschematize(e.getParameters(), myBuilder
                                    .getInnermostActiveScope());
                    candidateType = (MTFunction) candidate.getType();
                    emitDebug(candidate.getType() + " deschematizes to "
                            + candidateType);

                    if (comparison.compare(e, eType, candidateType)) {

                        if (match != null) {
                            throw new SourceErrorException("Multiple "
                                    + comparison.description() + " domain "
                                    + "matches.  For example, "
                                    + match.getName() + " : " + match.getType()
                                    + " and " + candidate.getName() + " : "
                                    + candidate.getType()
                                    + ".  Consider explicitly qualifying.", e
                                    .getLocation());
                        }

                        match = candidate;
                    }
                }
                catch (NoSolutionException nse) {
                    //couldn't deschematize--try the next one
                    emitDebug(candidate.getType() + " doesn't deschematize "
                            + "against " + e.getParameters());
                }
            }
        }

        if (match == null) {
            throw NoSolutionException.INSTANCE;
        }

        return match;
    }

    //-------------------------------------------------------------------
    //   Helper classes
    //-------------------------------------------------------------------

    private static class ExactDomainMatch
            implements
                TypeComparison<AbstractFunctionExp, MTFunction> {

        @Override
        public boolean compare(AbstractFunctionExp foundValue,
                MTFunction foundType, MTFunction expectedType) {

            return foundType.parameterTypesMatch(expectedType,
                    EXACT_PARAMETER_MATCH);
        }

        @Override
        public String description() {
            return "exact";
        }
    }

    private class InexactDomainMatch
            implements
                TypeComparison<AbstractFunctionExp, MTFunction> {

        @Override
        public boolean compare(AbstractFunctionExp foundValue,
                MTFunction foundType, MTFunction expectedType) {

            return myTypeGraph.isSubtype(foundType, expectedType);
        }

        @Override
        public String description() {
            return "inexact";
        }
    }

    private static class ExactParameterMatch implements Comparator<MTType> {

        @Override
        public int compare(MTType o1, MTType o2) {
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

    private class InexactParameterMatch implements TypeComparison<Exp, MTType> {

        @Override
        public boolean compare(Exp foundValue, MTType foundType,
                MTType expectedType) {

            return myTypeGraph.isKnownToBeIn(foundValue, expectedType);
        }

        @Override
        public String description() {
            return "inexact";
        }
    }
}
