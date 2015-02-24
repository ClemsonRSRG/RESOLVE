/**
 * PopulatingVisitor.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate2;

import edu.clemson.cs.r2jt.absynnew.*;
import edu.clemson.cs.r2jt.absynnew.ImportCollectionAST.ImportType;
import edu.clemson.cs.r2jt.absynnew.decl.*;
import edu.clemson.cs.r2jt.absynnew.decl.MathDefinitionAST.DefinitionType;
import edu.clemson.cs.r2jt.absynnew.expr.*;
import edu.clemson.cs.r2jt.misc.SrcErrorException;
import edu.clemson.cs.r2jt.misc.Utils;
import edu.clemson.cs.r2jt.misc.Utils.Indirect;
import edu.clemson.cs.r2jt.misc.HardCoded2;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.*;
import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.*;
import edu.clemson.cs.r2jt.typeandpopulate2.query.*;
import edu.clemson.cs.r2jt.typereasoning2.TypeComparison;
import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;
import edu.clemson.cs.r2jt.typeandpopulate2.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate2.MathSymbolTable.FacilityStrategy;
import org.antlr.v4.runtime.Token;

import java.util.*;

public class PopulatingVisitor extends TreeWalkerVisitor {

    private static final boolean PRINT_DEBUG = true;

    private static final TypeComparison<MathSymbolAST, MTFunction> EXACT_DOMAIN_MATCH =
            new ExactDomainMatch();

    private static final Comparator<MTType> EXACT_PARAMETER_MATCH =
            new ExactParameterMatch();

    private final TypeComparison<MathSymbolAST, MTFunction> INEXACT_DOMAIN_MATCH =
            new InexactDomainMatch();

    private final TypeComparison<ExprAST, MTType> INEXACT_PARAMETER_MATCH =
            new InexactParameterMatch();

    private MathSymbolTableBuilder myBuilder;
    private ModuleScopeBuilder myCurModuleScope;

    private int myTypeValueDepth = 0;
    private int myExpressionDepth = 0;

    private boolean myInTypeTheoremBindingExpFlag = false;

    private boolean myWalkingModuleParameterFlag = false;

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
     * <p>When parsing a type realization declaration, this is set to the
     * entry corresponding to the conceptual declaration from the concept.  When
     * not inside such a declaration, this will be null.</p>
     */
    private ProgramTypeDefinitionEntry myTypeDefinitionEntry;
    private PTRepresentation myPTRepresentationType;

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

    /**
     * <p>While walking the parameters of a definition, this flag will be set
     * to true.</p>
     */
    private boolean myDefinitionParameterSectionFlag = false;

    /**
     * <p>While we walk the children of a direct definition, this will be set
     * with a pointer to the definition declaration we are walking, otherwise
     * it will be null.  Note that definitions cannot be nested, so there's
     * no need for a stack.</p>
     */
    private MathDefinitionAST myCurrentDirectDefinition, myCurrentDefinition;

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

    private MathSymbolEntry myExemplarEntry;

    private final TypeGraph myTypeGraph;

    public PopulatingVisitor(MathSymbolTableBuilder builder) {
        myActiveQuantifications.push(SymbolTableEntry.Quantification.NONE);
        myTypeGraph = builder.getTypeGraph();
        myBuilder = builder;
    }

    public TypeGraph getTypeGraph() {
        return myTypeGraph;
    }

    //-------------------------------------------------------------------
    //   Visitor methods
    //-------------------------------------------------------------------

    @Override
    public void preModuleAST(ModuleAST e) {
        PopulatingVisitor.emitDebug("----------------------\nmodule "
                + e.getName().getText() + "\n----------------------");
        myCurModuleScope = myBuilder.startModuleScope(e);
    }

    @Override
    public void preImportCollectionAST(ImportCollectionAST e) {
        for (Token importRequest : e.getImportsExcluding(ImportType.EXTERNAL)) {
            myCurModuleScope.addImport(new ModuleIdentifier(importRequest));
        }
    }

    @Override
    public void preMathLambdaAST(MathLambdaAST e) {
        myBuilder.startScope(e);
        emitDebug("lambda expression " + e);
        myDefinitionParameterSectionFlag = true;
    }

    @Override
    public void midMathLambdaAST(MathLambdaAST e, ResolveAST next,
            ResolveAST previous) {
        if (next == e.getBody()) {
            myDefinitionParameterSectionFlag = false;
        }
    }

    @Override
    public void postMathLambdaAST(MathLambdaAST e) {
        myBuilder.endScope();

        List<MTType> parameterTypes = new LinkedList<MTType>();
        for (MathVariableAST p : e.getParameters()) {
            parameterTypes.add(p.getSyntaxType().getMathTypeValue());
        }
        e.setMathType(new MTFunction(myTypeGraph, e.getBody().getMathType(),
                parameterTypes));
    }

    @Override
    public void preOperationSigAST(OperationSigAST e) {
        myBuilder.startScope(e);
        myCurrentParameters = new ArrayList<ProgramParameterEntry>();
    }

    @Override
    public void midOperationSigAST(OperationSigAST e, ResolveAST previous,
            ResolveAST next) {
        if (previous == e.getReturnType() && e.getReturnType() != null) {
            try {
                //Inside the operation's assertions, the name of the operation
                //refers to its return value
                myBuilder.getInnermostActiveScope().addBinding(
                        e.getName().getText(), e,
                        e.getReturnType().getMathTypeValue());
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
    public void postOperationSigAST(OperationSigAST e) {
        myBuilder.endScope();
        putOperationLikeThingInSymbolTable(e.getName(), e.getReturnType(), e);
        myCurrentParameters = null;
    }

    @Override
    public void preOperationImplAST(OperationImplAST e) {
        try {
            //Figure out what Operation we correspond to (we don't use
            //OperationQuery because we want to check parameter types
            //separately in postProcedureDec)
            myCorrespondingOperation =
                    myBuilder.getInnermostActiveScope().queryForOne(
                            new NameAndEntryTypeQuery(null, e.getName(),
                                    OperationEntry.class,
                                    ImportStrategy.IMPORT_NAMED,
                                    FacilityStrategy.FACILITY_IGNORE, false))
                            .toOperationEntry(e.getName());

            myBuilder.startScope(e);
            myCurrentParameters = new LinkedList<ProgramParameterEntry>();
        }
        catch (NoSuchSymbolException nsse) {
            throw new SrcErrorException("Procedure " + e.getName().getText()
                    + " does not implement any known operation.", e.getName());
        }
        catch (DuplicateSymbolException dse) {
            //We should have caught this before now, like when we defined the
            //duplicate Operation
            throw new RuntimeException("Duplicate Operations for "
                    + e.getName().getText() + "?");
        }
    }

    @Override
    public void midOperationImplAST(OperationImplAST e, ResolveAST previous,
            ResolveAST next) {
        if (previous != null && previous == e.getReturnType()) {
            try {
                myBuilder.getInnermostActiveScope().addProgramVariable(
                        e.getName().getText(), e,
                        e.getReturnType().getProgramTypeValue());
            }
            catch (DuplicateSymbolException dse) {
                duplicateSymbol(e.getName());
            }
        }
    }

    @Override
    public void postOperationImplAST(OperationImplAST e) {
        myBuilder.endScope();

        //We're about to throw away all information about procedure parameters,
        //since they're redundant anyway.  So we sanity-check them first.
        TypeAST returnTy = e.getReturnType();
        PTType returnType;
        if (returnTy == null) {
            returnType = PTVoid.getInstance(myTypeGraph);
        }
        else {
            returnType = returnTy.getProgramTypeValue();
        }
        if (!returnType.equals(myCorrespondingOperation.getReturnType())) {
            throw new SrcErrorException("Procedure return type does "
                    + "not correspond to the return type of the operation "
                    + "it implements.  \n\nExpected type: "
                    + myCorrespondingOperation.getReturnType() + " ("
                    + myCorrespondingOperation.getSourceModuleIdentifier()
                    + "." + myCorrespondingOperation.getName() + ")\n\n"
                    + "Found type: " + returnType, e.getStart());
        }
        if (myCorrespondingOperation.getParameters().size() != myCurrentParameters
                .size()) {
            throw new SrcErrorException("Procedure parameter count "
                    + "does not correspond to the parameter count of the "
                    + "operation it implements. \n\nExpected count: "
                    + myCorrespondingOperation.getParameters().size() + " ("
                    + myCorrespondingOperation.getSourceModuleIdentifier()
                    + "." + myCorrespondingOperation.getName() + ")\n\n"
                    + "Found count: " + myCurrentParameters.size(), e
                    .getStart());
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
                throw new SrcErrorException(curOpParam.getParameterMode()
                        + "-mode parameter "
                        + "cannot be implemented with "
                        + curProcParam.getParameterMode()
                        + " mode.  "
                        + "Select one of these valid modes instead: "
                        + Arrays.toString(curOpParam.getParameterMode()
                                .getValidImplementationModes()), curProcParam
                        .getDefiningElement().getStart());
            }
            if (!curProcParam.getDeclaredType().acceptableFor(
                    curOpParam.getDeclaredType())) {
                throw new SrcErrorException("Parameter type does not "
                        + "match corresponding operation parameter type."
                        + "\n\nExpected: " + curOpParam.getDeclaredType()
                        + " (" + curOpParam.getSourceModuleIdentifier() + "."
                        + myCorrespondingOperation.getName() + ")\n\n"
                        + "Found: " + curProcParam.getDeclaredType(),
                        curProcParam.getDefiningElement().getStart());
            }
            if (!curOpParam.getName().equals(curProcParam.getName())) {
                throw new SrcErrorException("Parmeter name does not "
                        + "match corresponding operation parameter name."
                        + "\n\nExpected name: " + curOpParam.getName() + " ("
                        + curOpParam.getSourceModuleIdentifier() + "."
                        + myCorrespondingOperation.getName() + ")\n\n"
                        + "Found name: " + curProcParam.getName(), curProcParam
                        .getDefiningElement().getStart());
            }
        }
        try {
            myBuilder.getInnermostActiveScope().addProcedure(
                    e.getName().getText(), e, myCorrespondingOperation);
        }
        catch (DuplicateSymbolException dse) {
            duplicateSymbol(e.getName());
        }
        myCurrentParameters = null;
    }

    private void putOperationLikeThingInSymbolTable(Token name,
            TypeAST returnTy, ResolveAST o) {
        try {
            PTType returnType;
            if (returnTy == null) {
                returnType = PTVoid.getInstance(myTypeGraph);
            }
            else {
                returnType = returnTy.getProgramTypeValue();
            }
            myBuilder.getInnermostActiveScope().addOperation(name.getText(), o,
                    myCurrentParameters, returnType);
        }
        catch (DuplicateSymbolException dse) {
            duplicateSymbol(name);
        }
    }

    @Override
    public void preModuleParameterAST(ModuleParameterAST e) {
        myWalkingModuleParameterFlag = true;
    }

    @Override
    public void postModuleParameterAST(ModuleParameterAST e) {
        if (!(e.getPayload() instanceof OperationSigAST)) {

            if (e.getPayload().getMathType() == null) {
                throw new RuntimeException(e.getPayload().getClass()
                        + " has a null math type");
            }
            e.setMathType(e.getPayload().getMathType());
        }
        else {
            MTType t = ((OperationSigAST) e.getPayload()).getMathType();
            if (t == null) {
                t = myTypeGraph.VOID;
            }
            e.setMathType(t);
        }
        myWalkingModuleParameterFlag = false;
    }

    @Override
    public void postTypeParameterAST(TypeParameterAST e) {
        try {
            String paramName = e.getName().getText();

            myBuilder.getInnermostActiveScope().addFormalParameter(paramName,
                    e, ProgramParameterEntry.ParameterMode.TYPE,
                    new PTElement(myTypeGraph));

            myGenericTypes.put(paramName, myTypeGraph.CLS);
            e.setMathType(myTypeGraph.CLS);
        }
        catch (DuplicateSymbolException dse) {
            duplicateSymbol(e.getName());
        }
    }

    @Override
    public void postParameterAST(ParameterAST e) {
        ProgramParameterEntry.ParameterMode mode = e.getMode();
        try {
            ProgramParameterEntry paramEntry =
                    myBuilder.getInnermostActiveScope().addFormalParameter(
                            e.getName().getText(), e, mode,
                            e.getType().getProgramTypeValue());
            if (!myWalkingModuleParameterFlag) {
                myCurrentParameters.add(paramEntry);
            }
        }
        catch (DuplicateSymbolException dse) {
            duplicateSymbol(e.getName());
        }
        e.setMathType(e.getType().getMathTypeValue());
    }

    @Override
    public void preTypeRepresentationAST(TypeRepresentationAST r) {
        myBuilder.startScope(r);
        Token type = r.getName();

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
                    es.get(0).toProgramTypeDefinitionEntry(type);
        }
    }

    @Override
    public void midTypeRepresentationAST(TypeRepresentationAST e,
            ResolveAST previous, ResolveAST next) {

        if (previous instanceof TypeAST) {
            //We've finished the representation and are about to parse
            //conventions, etc.  We introduce the exemplar gets added as
            //a program variable with the appropriate type.
            myPTRepresentationType =
                    new PTRepresentation(myTypeGraph, ((TypeAST) previous)
                            .getProgramTypeValue(), myTypeDefinitionEntry);
            try {
                myBuilder.getInnermostActiveScope().addProgramVariable(
                        myTypeDefinitionEntry.getProgramType()
                                .getExemplarName(), e, myPTRepresentationType);
            }
            catch (DuplicateSymbolException dse) {
                //This shouldn't be possible--the type declaration has a
                //scope all its own and we're the first ones to get to
                //introduce anything
                throw new RuntimeException(dse);
            }
        }
    }

    @Override
    public void postTypeRepresentationAST(TypeRepresentationAST r) {
        myBuilder.endScope();

        try {
            myBuilder.getInnermostActiveScope().addRepresentationTypeEntry(
                    r.getName().getText(), r, myTypeDefinitionEntry,
                    myPTRepresentationType, r.getConvention(),
                    r.getCorrespondence());
        }
        catch (DuplicateSymbolException dse) {
            duplicateSymbol(r.getName());
        }

        myPTRepresentationType = null;
        myTypeDefinitionEntry = null;
    }

    @Override
    public void postFacilityAST(FacilityAST e) {
        try {
            myBuilder.getInnermostActiveScope().addFacility(e);
        }
        catch (DuplicateSymbolException dse) {
            duplicateSymbol(e.getName());
        }
    }

    @Override
    public void preTypeModelAST(TypeModelAST e) {
        myBuilder.startScope(e);
    }

    @Override
    public void midTypeModelAST(TypeModelAST e, ResolveAST previous,
            ResolveAST next) {

        if (previous == e.getModel()) {
            try {
                myExemplarEntry =
                        myBuilder.getInnermostActiveScope().addBinding(
                                e.getExemplar().getText(), e,
                                e.getModel().getMathTypeValue());
            }
            catch (DuplicateSymbolException dse) {
                //This shouldn't be possible--the type declaration has a
                //scope all its own and we're the first ones to get to
                //introduce anything
                throw new RuntimeException(dse);
            }
        }
    }

    @Override
    public void postTypeModelAST(TypeModelAST e) {
        myBuilder.endScope();
        try {
            myBuilder.getInnermostActiveScope().addProgramTypeDefinition(
                    e.getName().getText(), e, e.getModel().getMathTypeValue(),
                    myExemplarEntry);

            myExemplarEntry = null;
        }
        catch (DuplicateSymbolException dse) {
            duplicateSymbol(e.getName());
        }
    }

    @Override
    public void postNamedTypeAST(NamedTypeAST e) {
        //Note that all mathematical types are MathTypeASTs, so this must
        //be in a program-type syntactic slot.
        Token tySymbol = e.getName();
        Token tyQualifier = e.getQualifier();
        String tyName = tySymbol.getText();

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
                                    e.getName());

            e.setProgramTypeValue(type.getProgramType());
            e.setMathType(myTypeGraph.CLS);
            e.setMathTypeValue(type.getModelType());
        }
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(tyQualifier, tySymbol);
        }
        catch (DuplicateSymbolException dse) {
            //TODO : Error gracefully
            throw new RuntimeException(dse);
        }
    }

    @Override
    public boolean walkMathTypeAssertionAST(MathTypeAssertionAST e) {
        preMathTypeAssertionAST(e);

        //If we exist as an implicit type parameter, there's no way our
        //expression can know its own type (that's defined by the asserted Ty),
        //so we skip walking it and let postMathTypeAssertionAST() set its type
        //for it
        if (myTypeValueDepth == 0) {
            TreeWalker.walk(this, e.getExpression());
        }
        TreeWalker.walk(this, e.getAssertedType());
        postMathTypeAssertionAST(e);
        return true;
    }

    @Override
    public void postMathTypeAssertionAST(MathTypeAssertionAST e) {
        if (myTypeValueDepth == 0
                && (myExpressionDepth > 2 || !myInTypeTheoremBindingExpFlag)) {
            throw new SrcErrorException("this construct only permitted in "
                    + "type declarations or in expressions matching: \n\n"
                    + "   Type Theorem <name>: <quantifiers>, \n"
                    + "       [<condition> implies] <expression> : "
                    + "<assertedType>", e.getStart());
        }
        else if (myActiveQuantifications.size() > 0
                && myActiveQuantifications.peek() != SymbolTableEntry.Quantification.NONE) {
            throw new SrcErrorException(
                    "implicit types are not permitted inside "
                            + "quantified variable declarations; "
                            + "quantify the type explicitly instead.", e
                            .getStart());
        }
        //Note that postTypeTheoremDec() checks the "form" of a type theorem at
        //the top two levels.  So all we're checking for here is that the type
        //assertion didn't happen deeper than that (where it shouldn't appear).

        //If we're the assertion of a type theorem, then postTypeTheoremDec()
        //will take care of any logic.  If we're part of a type declaration,
        //on the other hand, we've got some bookkeeping to do...
        if (myTypeValueDepth > 0) {
            try {
                MathSymbolAST nodeExp = (MathSymbolAST) e.getExpression();
                try {
                    myBuilder.getInnermostActiveScope().addBinding(
                            nodeExp.getName().getText(),
                            SymbolTableEntry.Quantification.UNIVERSAL, e,
                            e.getAssertedType().getMathType());
                    e.setMathType(e.getAssertedType().getMathType());
                    e.setMathTypeValue(new MTNamed(myTypeGraph, nodeExp
                            .getName().getText()));

                    //See walkTypeAssertionExp(): we are responsible for
                    //setting the VarExp's type.
                    nodeExp.setMathType(e.getAssertedType().getMathType());
                    e.setMathTypeValue(new MTNamed(myTypeGraph, nodeExp
                            .getName().getText()));

                    if (myDefinitionNamedTypes.contains(nodeExp.getName()
                            .getText())) {
                        //Regardless of where in the expression it appears, an
                        //implicit type parameter exists at the top level of a
                        //definition, and thus a definition that contains, e.g.,
                        //an implicit type parameter T cannot make reference
                        //to some existing type with that name (except via full
                        //qualification), thus the introduction of an implicit
                        //type parameter must precede any use of that
                        //parameter's name, even if the name exists in-scope
                        //before the parameter is declared
                        throw new SrcErrorException("introduction of "
                                + "implicit type parameter must precede any "
                                + "use of that variable name", nodeExp
                                .getStart());
                    }

                    //Note that a redudantly named type parameter would be
                    //caught when we add a symbol to the symbol table, so no
                    //need to check here
                    myDefinitionSchematicTypes.put(nodeExp.getName().getText(),
                            e.getAssertedType().getMathType());

                    PopulatingVisitor.emitDebug("added schematic variable: "
                            + nodeExp.getName().getText());
                }
                catch (DuplicateSymbolException dse) {
                    duplicateSymbol(nodeExp.getName());
                }
            }
            catch (ClassCastException cce) {
                throw new SrcErrorException("must be a variable name ", e
                        .getExpression().getStart());
            }
        }
        else {
            e.setMathType(myTypeGraph.BOOLEAN);
        }
    }

    @Override
    public void preMathDefinitionAST(MathDefinitionAST e) {
        myBuilder.startScope(e);

        if (!(e.getDefinitionType() == DefinitionType.INDUCTIVE)) {
            myCurrentDirectDefinition = e;
        }
        // Keep track also of any definition (inductive or direct)
        myCurrentDefinition = e;

        myDefinitionSchematicTypes.clear();
        myDefinitionNamedTypes.clear();
        myDefinitionParameterSectionFlag = true;
    }

    @Override
    public void midMathDefinitionAST(MathDefinitionAST e, ResolveAST previous,
            ResolveAST next) {

        // If we've just processed the definition's 'return type' and we're
        // also inductive, so we need to add a binding representing our own
        // signature in order have the ability to recursively be refer to
        // ourself in the body.
        if (next == e.getReturnType()) {
            myDefinitionParameterSectionFlag = false;
        }
        if (previous == e.getReturnType()
                && e.getDefinitionType() == DefinitionType.INDUCTIVE) {
            MTType declaredType = e.getReturnType().getMathTypeValue();

            if (!e.getParameters().isEmpty()) {
                declaredType = new MTFunction(myTypeGraph, e);
            }

            //Note that, even if typeValue is null at this point, if declaredType
            //returns true from knownToContainOnlyMTypes(), a new type value will
            //still be created by the symbol table
            addBinding(e.getName(), e, declaredType, null,
                    myDefinitionSchematicTypes);
            e.setMathType(declaredType);
        }
    }

    @Override
    public void postMathDefinitionAST(MathDefinitionAST e) {
        myBuilder.endScope();

        //All definitions must have a non-null 'return type'. This is checked
        //each time a definition is constructed, so no need to recheck here.
        MTType declaredType = e.getReturnType().getMathTypeValue();

        if (e.getDefinitionBody() != null) {
            expectType(e.getDefinitionBody(), declaredType);
        }
        else if (e.getDefinitionType() == DefinitionType.INDUCTIVE) {
            expectType(e.getInductiveBaseCase(), myTypeGraph.BOOLEAN);
            expectType(e.getInductiveHypothesis(), myTypeGraph.BOOLEAN);
        }

        List<MathVariableAST> parameters = e.getParameters();
        if (!parameters.isEmpty()) {
            declaredType = new MTFunction(myTypeGraph, e);
        }

        MTType typeValue = null;
        if (e.getDefinitionBody() != null) {
            typeValue = e.getDefinitionBody().getMathTypeValue();
        }

        //Note that, even if typeValue is null at this point, if declaredType
        //returns true from knownToContainOnlyMTypes(), a new type value will
        //still be created by the symbol table
        addBinding(e.getName(), e, declaredType, typeValue,
                myDefinitionSchematicTypes);

        PopulatingVisitor.emitDebug("new definition " + e.getName().getText()
                + " of type " + declaredType
                + ((typeValue != null) ? " with type value " + typeValue : ""));

        myCurrentDirectDefinition = null;
        myDefinitionSchematicTypes.clear();
        e.setMathType(declaredType);
    }

    @Override
    public void postMathTheoremAST(MathTheoremAST e) {
        expectType(e.getAssertion(), myTypeGraph.BOOLEAN);
        try {
            myBuilder.getInnermostActiveScope().addTheorem(
                    e.getName().getText(), e);
        }
        catch (DuplicateSymbolException dse) {
            duplicateSymbol(e.getName());
        }
        myDefinitionSchematicTypes.clear();
        PopulatingVisitor.emitDebug("new theorem " + e.getName().getText());
    }

    @Override
    public void postModuleArgumentAST(ModuleArgumentAST e) {
        e.setProgramTypeValue(e.getArgumentExpr().getProgramType());
    }

    @Override
    public void postProgNameRefAST(ProgNameRefAST e) {
        try {
            ProgramVariableEntry entry =
                    myBuilder.getInnermostActiveScope().queryForOne(
                            new ProgramVariableQuery(e.getQualifier(), e
                                    .getName()));

            e.setProgramType(entry.getProgramType());
            //Handle math typing stuff
            postSymbolExp(e.getQualifier(), e.getName(), e);
        }
        //try again in here with the other query...
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(e.getQualifier(), e.getName());
        }
        catch (DuplicateSymbolException dse) {
            throw new RuntimeException("ToDo"); //TODO
        }
    }

    @Override
    public void postProgIntegerRefAST(ProgLiteralRefAST.ProgIntegerRefAST e) {
        e.setProgramType(getIntegerProgramType());
        e.setMathType(myTypeGraph.Z);

        String typeValueDesc = "";
        if (e.getMathTypeValue() != null) {
            typeValueDesc =
                    ", referencing math type " + e.getMathTypeValue() + " ("
                            + e.getMathTypeValue().getClass() + ")";
        }
        PopulatingVisitor.emitDebug("processed symbol " + e + " with type "
                + e.getMathType() + typeValueDesc);
    }

    @Override
    public void preMathQuantifiedAST(MathQuantifiedAST e) {
        PopulatingVisitor.emitDebug("entering preMathQuantifiedAST...");
        myBuilder.startScope(e);
    }

    @Override
    public boolean walkMathQuantifiedAST(MathQuantifiedAST e) {
        preMathQuantifiedAST(e);

        PopulatingVisitor.emitDebug("entering walkMathQuantifiedAST...");
        List<MathVariableAST> vars = e.getQuantifiedVariables();
        SymbolTableEntry.Quantification quantification = e.getQuantification();

        myActiveQuantifications.push(quantification);
        for (MathVariableAST v : vars) {
            TreeWalker.walk(this, v);
        }
        myActiveQuantifications.pop();

        myActiveQuantifications.push(SymbolTableEntry.Quantification.NONE);
        TreeWalker.walk(this, e.getAssertion());
        myActiveQuantifications.pop();

        PopulatingVisitor.emitDebug("exiting walkQuantExp.");
        postMathQuantifiedAST(e);
        //This indicates that we've overrided the default
        return true;
    }

    @Override
    public void postMathQuantifiedAST(MathQuantifiedAST e) {
        myBuilder.endScope();

        expectType(e.getAssertion(), myTypeGraph.BOOLEAN);
        e.setMathType(myTypeGraph.BOOLEAN);
    }

    @Override
    public void preMathTypeTheoremAST(MathTypeTheoremAST e) {
        myBuilder.startScope(e);
        myInTypeTheoremBindingExpFlag = false;
        myActiveQuantifications.push(SymbolTableEntry.Quantification.UNIVERSAL);
    }

    @Override
    public void midMathTypeTheoremAST(MathTypeTheoremAST e,
            ResolveAST previous, ResolveAST next) {
        //We've just processed all universal variables for this type theorem,
        // so we pop a level of quantification off the stack
        if (next == e.getAssertion()) {
            myActiveQuantifications.pop();
            myInTypeTheoremBindingExpFlag = true;
        }
    }

    @Override
    public void postMathTypeTheoremAST(MathTypeTheoremAST e) {
        e.setMathType(myTypeGraph.BOOLEAN);
        ExprAST assertion = e.getAssertion();

        ExprAST condition;
        ExprAST bindingExpression;
        MathTypeAST typeExp;

        try {
            if (assertion instanceof MathSymbolAST) {
                MathSymbolAST assertionAsMathSym = (MathSymbolAST) assertion;
                String operator = assertionAsMathSym.getName().getText();

                if (operator.equals("implies")) {
                    if (assertionAsMathSym.getArguments().size() != 2) {
                        throw new RuntimeException("implies function without"
                                + "arguments??");
                    }
                    condition = assertionAsMathSym.getArguments().get(0);
                    assertion = assertionAsMathSym.getArguments().get(1);
                }
                else {
                    throw new ClassCastException();
                }
            }
            else {
                condition = myTypeGraph.getTrueVarExp();
            }

            MathTypeAssertionAST assertionAsTAE =
                    (MathTypeAssertionAST) assertion;

            bindingExpression = assertionAsTAE.getExpression();
            typeExp = assertionAsTAE.getAssertedType();

            try {
                myTypeGraph.addRelationship(bindingExpression, typeExp
                        .getMathTypeValue(), condition, myBuilder
                        .getInnermostActiveScope());
            }
            catch (IllegalArgumentException iae) {
                throw new SrcErrorException(iae.getMessage(), e.getStart());
            }
        }
        catch (ClassCastException cse) {
            throw new SrcErrorException("top level of type theorem "
                    + "assertion must be 'implies' or ':'", assertion
                    .getStart());
        }
        myBuilder.endScope();
    }

    @Override
    public void postVariableAST(VariableAST e) {
        MTType mathTypeValue = e.getType().getMathTypeValue();
        String varName = e.getName().getText();

        e.setMathType(mathTypeValue);
        try {
            myBuilder.getInnermostActiveScope().addProgramVariable(varName,
                    e, e.getType().getProgramTypeValue());
        }
        catch (DuplicateSymbolException dse) {
            duplicateSymbol(e.getName());
        }
        PopulatingVisitor.emitDebug("  New program variable: " + varName
                + " of type " + mathTypeValue.toString()
                + " with quantification NONE");
    }

    @Override
    public void postMathVariableAST(MathVariableAST e) {
        MTType mathTypeValue = e.getSyntaxType().getMathTypeValue();
        String varName = e.getName().getText();

        if (myCurrentDirectDefinition != null
                && mathTypeValue.isKnownToContainOnlyMTypes()
                && myDefinitionNamedTypes.contains(varName)) {

            throw new SrcErrorException("introduction of type "
                    + "parameter must precede any use of that variable "
                    + "name", e.getStart());
        }

        if ((myDefinitionParameterSectionFlag || (myActiveQuantifications
                .size() > 0 && myActiveQuantifications.peek() != SymbolTableEntry.Quantification.NONE))
                && mathTypeValue.isKnownToContainOnlyMTypes()) {
            myDefinitionSchematicTypes.put(varName, mathTypeValue);
        }

        /*if (myDefinitionParameterSectionFlag
                && mathTypeValue.isKnownToContainOnlyMTypes()) {
            myDefinitionSchematicTypes.put(varName, mathTypeValue);
        }*/

        e.setMathType(mathTypeValue);

        SymbolTableEntry.Quantification q;
        if (myDefinitionParameterSectionFlag && myTypeValueDepth == 0) {
            q = SymbolTableEntry.Quantification.UNIVERSAL;
        }
        else {
            q = myActiveQuantifications.peek();
        }

        addBinding(e.getName(), q, e, mathTypeValue, null);

        PopulatingVisitor.emitDebug("  new variable " + varName + " of type "
                + mathTypeValue.toString() + " with quantification " + q);
    }

    @Override
    public void postMathTupleAST(MathTupleAST e) {
        //See the note in MathTupleAST on why it isn't a MathSymbolAST
        List<MTCartesian.Element> fieldTypes =
                new LinkedList<MTCartesian.Element>();

        for (ExprAST field : e.getFields()) {
            fieldTypes.add(new MTCartesian.Element(field.getMathType()));
        }
        e.setMathType(new MTCartesian(myTypeGraph, fieldTypes));
    }

    @Override
    public void postRecordTypeAST(RecordTypeAST e) {
        Map<String, PTType> fieldMap = new HashMap<String, PTType>();
        List<VariableAST> fields = e.getFields();

        for (VariableAST field : fields) {
            fieldMap.put(field.getName().getText(), field.getType()
                    .getProgramTypeValue());
        }
        PTRecord record = new PTRecord(myTypeGraph, fieldMap);

        e.setProgramTypeValue(record);
        e.setMathType(myTypeGraph.CLS);
        e.setMathTypeValue(record.toMath());
    }

    @Override
    public void preMathTypeAST(MathTypeAST e) {
        myTypeValueDepth++;
    }

    @Override
    public void postMathTypeAST(MathTypeAST e) {
        myTypeValueDepth--;

        ExprAST typeExp = e.getUnderlyingExpr();
        MTType mathType = typeExp.getMathType();
        MTType mathTypeValue = typeExp.getMathTypeValue();

        if (mathTypeValue == null) {
            notAType(typeExp);
        }
        e.setMathType(mathType);
        e.setMathTypeValue(mathTypeValue);
    }

    @Override
    public void preExprAST(ExprAST e) {
        myExpressionDepth++;
    }

    @Override
    public void postExprAST(ExprAST e) {

        if (e.getMathType() == null) {
            throw new RuntimeException("expression " + e + " (" + e.getClass()
                    + ") has left population without a math type");
        }
        if (e instanceof ProgExprAST
                && ((ProgExprAST) e).getProgramType() == null) {
            throw new RuntimeException("program expression " + e + " ("
                    + e.getClass() + ") has left population without "
                    + "a program type");
        }
        myExpressionDepth--;
    }

    @Override
    public void postAny(ResolveAST e) {
        if (e instanceof TypeAST) {
            TypeAST eAsTypeNode = (TypeAST) e;
            if (eAsTypeNode.getMathTypeValue() == null) {
                throw new RuntimeException("TypeAST node " + e + "("
                        + e.getClass() + ") "
                        + "got through the populator with no "
                        + "math type value");
            }
            if (!(e instanceof MathTypeAST)
                    && eAsTypeNode.getProgramTypeValue() == null) {
                throw new RuntimeException("MathTypeAST node " + e + " ("
                        + e.getClass() + ") got through the "
                        + "populator with no program type value");
            }
        }
    }

    @Override
    public void postMathSetAST(MathSetAST e) {
        e.setMathType(myTypeGraph.CLS);

        List<ExprAST> elements = e.getElements();

        if (elements.isEmpty()) {
            e.setMathTypeValue(myTypeGraph.EMPTY_SET);
        }
        //Todo: temp, generalize this.
        else if (elements.size() == 1) {
            e.setMathTypeValue(new MTPowertypeApplication(myTypeGraph, elements
                    .get(0).getMathType()));
        }
        else { // {1, true} --> Powerset(N) union Powerset(B)
            /*for (Expression exp : e.getElements()) {
                e.setMathTypeValue(new MTPowertypeApplication(myTypeGraph, exp
                        .getMathType()));
            }*/
        }
    }

    @Override
    public void postProgOperationRefAST(ProgOperationRefAST e) {
        List<ProgExprAST> args = e.getArguments();

        List<PTType> argTypes = new LinkedList<PTType>();
        for (ProgExprAST arg : args) {
            argTypes.add(arg.getProgramType());
        }

        try {
            OperationEntry op =
                    myBuilder.getInnermostActiveScope().queryForOne(
                            new OperationQuery(e.getQualifier(), e.getName(),
                                    argTypes));

            e.setProgramType(op.getReturnType());
            e.setMathType(op.getReturnType().toMath());
        }
        catch (NoSuchSymbolException nsse) {
            throw new SrcErrorException("No operation found corresponding "
                    + "the call with the specified arguments: ", e.getStart());
        }
        catch (DuplicateSymbolException dse) {
            duplicateSymbol(e.getName());
        }
    }

    @Override
    public boolean walkProgDotAST(ProgDotAST e) {

        preAny(e);
        preExprAST(e);
        preProgExprAST(e);
        preProgDotAST(e);

        postProgDotAST(e);
        postProgExprAST(e);
        postExprAST(e);
        postAny(e);

        return true;
    }

    @Override
    public void preProgDotAST(ProgDotAST e) {
        //Dot expressions are handled ridiculously, even for this compiler, so
        //this method just deals with the cases we've encountered so far and
        //lots of assumptions are made.  Expect it to break frequently when you
        //encounter some new case

        Token firstNameTok = e.getSegments().get(0).getName();
        String firstName = firstNameTok.getText();

        try {
            ProgramVariableEntry eEntry =
                    myBuilder.getInnermostActiveScope().queryForOne(
                            new NameQuery(null, firstName))
                            .toProgramVariableEntry(firstNameTok);
            e.getSegments().get(0).setProgramType(eEntry.getProgramType());
            e.getSegments().get(0)
                    .setMathType(eEntry.getProgramType().toMath());

            PTType eType = eEntry.getProgramType();

            if (eType instanceof PTRepresentation) {
                eType = ((PTRepresentation) eType).getBaseType();
            }

            PTRecord recordType = (PTRecord) eType;
            String fieldName = e.getSegments().get(1).getName().getText();

            PTType fieldType = recordType.getFieldType(fieldName);

            if (fieldType == null) {
                throw new RuntimeException("Could not retrieve type of "
                        + " field '" + fieldName
                        + "'. Either it doesn't exist "
                        + "in the record or it's missing a type.");
            }

            e.getSegments().get(1).setProgramType(fieldType);
            e.setProgramType(fieldType);

            e.getSegments().get(1).setMathType(fieldType.toMath());
            e.setMathType(fieldType.toMath());
        }
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(null, firstNameTok);
        }
        catch (DuplicateSymbolException dse) {
            //This flavor of name query shouldn't be able to throw this--we're
            //only looking in the local module so there's no overloading
            throw new RuntimeException(dse);
        }
    }

    @Override
    public boolean walkMathDotAST(MathDotAST e) {
        preAny(e);
        preExprAST(e);
        preMathDotAST(e);

        Indirect<MathSymbolAST> lastGoodOut =
                new Utils.Indirect<MathSymbolAST>();
        Iterator<MathSymbolAST> segments = e.getSegments().iterator();
        MathSymbolEntry entry = getTopLevelValue(segments, lastGoodOut);

        Token lastGood = lastGoodOut.data.getStart();

        MTType curType = entry.getType();
        MTCartesian curTypeCartesian;
        MathSymbolAST nextSegment = lastGoodOut.data, lastSegment;
        while (segments.hasNext()) {
            lastSegment = nextSegment;
            nextSegment = segments.next();

            String segmentName = nextSegment.getName().getText();
            try {
                curTypeCartesian = (MTCartesian) curType;
                curType = curTypeCartesian.getFactor(segmentName);
            }
            catch (ClassCastException cce) {
                curType =
                        HardCoded2.getMetaFieldType(myTypeGraph, lastSegment,
                                segmentName);

                if (curType == null) {
                    throw new SrcErrorException("Value not a tuple.", lastGood);
                }
            }
            catch (NoSuchElementException nsee) {
                curType =
                        HardCoded2.getMetaFieldType(myTypeGraph, lastSegment,
                                segmentName);

                if (curType == null) {
                    throw new SrcErrorException("No such factor.", lastGood);
                }
            }

            //getName() would have thrown an exception if nextSegment wasn't
            //a VarExp or a FunctionExp.  In the former case, we're good to
            //go--but in the latter case, we still need to typecheck
            //parameters, assure they match the signature, and adjust
            //curType to reflect the RANGE of the function type rather than
            //the entire type
            if (nextSegment.isFunction()) {
                curType = applyFunction(nextSegment, curType);
            }

            nextSegment.setMathType(curType);
            lastGood = nextSegment.getStart();
        }
        postMathDotAST(e);
        postExprAST(e);
        postAny(e);

        return true;
    }

    @Override
    public void postMathDotAST(MathDotAST e) {
        //Might already have been set in preDotExp(), in which case our children
        //weren't visited
        if (e.getMathType() == null) {
            List<MathSymbolAST> segments = e.getSegments();
            ExprAST lastSeg = segments.get(segments.size() - 1);

            e.setMathType(lastSeg.getMathType());
            e.setMathTypeValue(lastSeg.getMathTypeValue());
        }
    }

    /**
     * <p>This method has to do an annoying amount of work, so pay attention:
     * takes an iterator over segments as returned from DotExp.getSegments().
     * Either the first segment or first two segments will be advanced over
     * from the iterator, depending on whether this method determines the DotExp
     * refers to a local value (one segment), is a qualified name referring to
     * a value in another module (two segments), or is a Conc expression (two
     * segments).  The segments will receive appropriate types.  The data field
     * of lastGood will be set with the location of the last segment read.
     * Then, the <code>MathSymbolEntry</code> corresponding to the correct
     * top-level value will be returned.</p>
     */
    private MathSymbolEntry getTopLevelValue(Iterator<MathSymbolAST> segments,
            Indirect<MathSymbolAST> lastGood) {
        MathSymbolEntry result = null;
        MathSymbolAST first = segments.next();
        Token firstName = first.getName();

        //First, we'll see if we're a Conc expression
        if (firstName.getText().equals("Conc")) {
            //Awesome.  We better be in a type definition and our second segment
            //better refer to the exemplar
            MathSymbolAST second = segments.next();

            if (!second.toString().equals(
                    myTypeDefinitionEntry.getProgramType().getExemplarName())) {
                throw new RuntimeException("No idea what's going on here.");
            }

            //The Conc segment doesn't have a sensible type, but we'll set one
            //for completeness.
            first.setMathType(myTypeGraph.BOOLEAN);
            second.setMathType(myTypeDefinitionEntry.getModelType());
            result = myTypeDefinitionEntry.getExemplar();

            lastGood.data = second;
        }
        else {
            //Next, we'll see if there's a locally-accessible symbol with this
            //name
            try {
                result =
                        myBuilder
                                .getInnermostActiveScope()
                                .queryForOne(
                                        new NameQuery(
                                                first.getQualifier(),
                                                firstName,
                                                ImportStrategy.IMPORT_NAMED,
                                                FacilityStrategy.FACILITY_IGNORE,
                                                true)).toMathSymbolEntry(
                                        first.getStart());

                //There is.  Cool.  We type it and we're done
                lastGood.data = first;
                first.setMathType(result.getType());
                try {
                    first.setMathTypeValue(result.getTypeValue());
                }
                catch (SymbolNotOfKindTypeException snokte) {

                }
            }
            catch (NoSuchSymbolException nsse) {
                noSuchSymbol(first.getQualifier(), first.getName());
            }
            catch (DuplicateSymbolException dse) {
                duplicateSymbol(firstName);
                throw new RuntimeException(); //This will never fire
            }
        }
        return result;
    }

    private MTType applyFunction(MathSymbolAST functionSegment, MTType type) {
        MTType result;

        try {
            MTFunction functionType = (MTFunction) type;

            //Ok, we need to type check our arguments before we can
            //continue
            for (ExprAST arg : functionSegment.getArguments()) {
                TreeWalker.walk(this, arg);
            }
            if (!INEXACT_DOMAIN_MATCH.compare(functionSegment, functionSegment
                    .getConservativePreApplicationType(myTypeGraph),
                    functionType)) {
                throw new SrcErrorException("Parameters do not "
                        + "match function range.\n\nExpected: "
                        + functionType.getDomain()
                        + "\nFound:    "
                        + functionSegment.getConservativePreApplicationType(
                                myTypeGraph).getDomain(), functionSegment
                        .getStart());
            }

            result = functionType.getRange();
        }
        catch (ClassCastException cce) {
            throw new SrcErrorException("Not a function.", functionSegment
                    .getStart());
        }

        return result;
    }

    @Override
    public void postMathSymbolAST(MathSymbolAST e) {
        if (!e.isFunction()) { // constant, variable, or literal.
            MathSymbolEntry intendedEntry =
                    postSymbolExp(e.getQualifier(), e.getName(), e);

            if (myTypeValueDepth > 0 && e.getQualifier() == null) {
                try {
                    intendedEntry.getTypeValue();
                    myDefinitionNamedTypes.add(intendedEntry.getName());
                }
                catch (SymbolNotOfKindTypeException snokte) {
                    //No problem, just don't need to add it
                }
            }
            e.setQuantification(intendedEntry.getQuantification());
        }
        else { // if MathSymbolAST is a function (Powerset(.), Str(Entry), etc)
            MTFunction foundExpType;
            foundExpType = e.getConservativePreApplicationType(myTypeGraph);

            PopulatingVisitor.emitDebug("expression: " + e + " of type "
                    + foundExpType);

            MathSymbolEntry intendedEntry = getIntendedFunction(e);

            MTFunction expectedType = (MTFunction) intendedEntry.getType();

            //We know we match expectedType--otherwise the above would have
            //thrown an exception.
            e.setMathType(expectedType.getRange());
            e.setQuantification(intendedEntry.getQuantification());

            if (myTypeValueDepth > 0) {
                //I had better identify a type
                MTFunction entryType = (MTFunction) intendedEntry.getType();

                List<MTType> arguments = new LinkedList<MTType>();
                MTType argTypeValue;
                for (ExprAST arg : e.getArguments()) {
                    argTypeValue = arg.getMathTypeValue();

                    if (argTypeValue == null) {
                        notAType(arg);
                    }
                    arguments.add(argTypeValue);
                }

                e.setMathTypeValue(entryType.getApplicationType(intendedEntry
                        .getName(), arguments));
            }
        }
    }

    private MathSymbolEntry postSymbolExp(Token qualifier, Token symbolName,
            ExprAST node) {
        MathSymbolEntry intendedEntry =
                getIntendedEntry(qualifier, symbolName, node);
        node.setMathType(intendedEntry.getType());

        setSymbolTypeValue(node, symbolName.getText(), intendedEntry);

        String typeValueDesc = "";

        if (node.getMathTypeValue() != null) {
            typeValueDesc =
                    ", referencing math type " + node.getMathTypeValue() + " ("
                            + node.getMathTypeValue().getClass() + ")";
        }

        PopulatingVisitor.emitDebug("processed symbol " + symbolName
                + " with type " + node.getMathType() + typeValueDesc);

        return intendedEntry;
    }

    /**
     * <p>For a given <code>AbstractFunctionExp</code>, finds the entry in the
     * symbol table to which it refers.  For a complete discussion of the
     * algorithm used, see
     * <a href="http://sourceforge.net/apps/mediawiki/resolve/index.php?title=Package_Search_Algorithm">
     * Package Search Algorithm</a>.</p>
     */
    private MathSymbolEntry getIntendedFunction(MathSymbolAST e) {

        //TODO : All this logic should be encapsulated into a SymbolQuery called
        //       MathFunctionQuery.

        MTFunction eType = e.getConservativePreApplicationType(myTypeGraph);

        Token eOperator = e.getName();
        String eOperatorString = eOperator.getText();

        List<MathSymbolEntry> sameNameFunctions =
                myBuilder.getInnermostActiveScope()
                        .query(
                                new MathFunctionNamedQuery(e.getQualifier(),
                                        eOperator));

        if (sameNameFunctions.isEmpty()) {
            throw new SrcErrorException("no such function ", e.getName());
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
                        "no function applicable for " + "domain: "
                                + eType.getDomain() + "\n\ncandidates:\n";

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
                    throw new SrcErrorException("no such function ", e
                            .getStart());
                }
                throw new SrcErrorException(errorMessage, e.getStart());
            }
        }

        if (intendedEntry.getDefiningElement() == myCurrentDirectDefinition) {
            throw new SrcErrorException("direct definition cannot "
                    + "contain recursive call ", e.getStart());
        }

        MTFunction intendedEntryType = (MTFunction) intendedEntry.getType();

        PopulatingVisitor.emitDebug("matching " + eOperator + " : " + eType
                + " to " + intendedEntry.getName() + " : " + intendedEntryType);

        return intendedEntry;
    }

    private void setSymbolTypeValue(ExprAST node, String symbolName,
            MathSymbolEntry intendedEntry) {

        try {
            if (intendedEntry.getQuantification() == SymbolTableEntry.Quantification.NONE) {
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
                notAType(intendedEntry, node.getStart());
            }
        }
    }

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

    private MathSymbolEntry getIntendedEntry(Token qualifier, Token symbolName,
            ExprAST node) {
        MathSymbolEntry result;

        try {
            result =
                    myBuilder.getInnermostActiveScope().queryForOne(
                            new MathSymbolQuery(qualifier, symbolName));
        }
        catch (DuplicateSymbolException dse) {
            duplicateSymbol(symbolName);
            throw new RuntimeException(); //This will never fire
        }
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(qualifier, symbolName);
            throw new RuntimeException(); //This will never fire
        }
        return result;
    }

    @Override
    public void postModuleAST(ModuleAST e) {
        myBuilder.endScope();
        PopulatingVisitor.emitDebug("end populator\n----------------------\n");
    }

    //-------------------------------------------------------------------
    //   Error handling
    //-------------------------------------------------------------------

    public void noSuchModule(Token qualifier) {
        throw new SrcErrorException(
                "module does not exist or is not in scope ", qualifier);
    }

    public void noSuchSymbol(Token qualifier, Token symbol) {
        String message;

        if (qualifier == null) {
            message = "no such symbol " + symbol.getText();
            throw new SrcErrorException(message, symbol);
        }
        else {
            message =
                    "no such symbol in module " + qualifier.getText() + "."
                            + symbol.getText();
            throw new SrcErrorException(message, qualifier);
        }
    }

    public <T extends SymbolTableEntry> void ambiguousSymbol(Token symbol,
            List<T> candidates) {
        String message = "ambiguous symbol;  candidates: ";

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
                            .fullyQualifiedRepresentation(symbol.getText());
        }
        throw new SrcErrorException(message, symbol);
    }

    public void notAType(SymbolTableEntry entry, Token t) {
        throw new SrcErrorException(entry.getSourceModuleIdentifier()
                .fullyQualifiedRepresentation(entry.getName())
                + " is not known to be a type.", t);
    }

    public void notAType(ExprAST e) {
        throw new SrcErrorException("not known to be a type", e.getStart());
    }

    public void expected(ExprAST e, MTType expectedType) {
        throw new SrcErrorException("expected: " + expectedType + ";  found: "
                + e.getMathType(), e.getStart());
    }

    public void duplicateSymbol(Token symbol) {
        throw new SrcErrorException("duplicate symbol: " + symbol.getText(),
                symbol);
    }

    public void expectType(ExprAST e, MTType expectedType) {
        if (!myTypeGraph.isKnownToBeIn(e, expectedType)) {
            expected(e, expectedType);
        }
    }

    private SymbolTableEntry addBinding(Token name,
            SymbolTableEntry.Quantification q, ResolveAST definingElement,
            MTType type, MTType typeValue, Map<String, MTType> schematicTypes) {
        if (type == null) {
            throw new NullPointerException();
        }
        else {
            try {
                return myBuilder.getInnermostActiveScope().addBinding(
                        name.getText(), q, definingElement, type, typeValue,
                        schematicTypes, myGenericTypes);
            }
            catch (DuplicateSymbolException dse) {
                duplicateSymbol(name);
                throw new RuntimeException(); //This will never fire
            }
        }
    }

    private SymbolTableEntry addBinding(Token name, ResolveAST definingElement,
            MTType type, MTType typeValue, Map<String, MTType> schematicTypes) {
        return addBinding(name, SymbolTableEntry.Quantification.NONE,
                definingElement, type, typeValue, schematicTypes);
    }

    private SymbolTableEntry addBinding(Token name,
            SymbolTableEntry.Quantification q, ResolveAST definingElement,
            MTType type, Map<String, MTType> schematicTypes) {
        return addBinding(name, q, definingElement, type, null, schematicTypes);
    }

    private SymbolTableEntry addBinding(Token name, ResolveAST definingElement,
            MTType type, Map<String, MTType> schematicTypes) {
        return addBinding(name, SymbolTableEntry.Quantification.NONE,
                definingElement, type, null, schematicTypes);
    }

    private MathSymbolEntry getExactDomainTypeMatch(MathSymbolAST e,
            List<MathSymbolEntry> candidates) throws NoSolutionException {
        return getDomainTypeMatch(e, candidates, EXACT_DOMAIN_MATCH);
    }

    private MathSymbolEntry getInexactDomainTypeMatch(MathSymbolAST e,
            List<MathSymbolEntry> candidates) throws NoSolutionException {
        return getDomainTypeMatch(e, candidates, INEXACT_DOMAIN_MATCH);
    }

    private MathSymbolEntry getDomainTypeMatch(MathSymbolAST e,
            List<MathSymbolEntry> candidates,
            TypeComparison<MathSymbolAST, MTFunction> comparison)
            throws NoSolutionException {
        MTFunction eType = e.getConservativePreApplicationType(myTypeGraph);

        MathSymbolEntry match = null;

        MTFunction candidateType;
        for (MathSymbolEntry candidate : candidates) {
            if (candidate.getType() instanceof MTFunction) {

                try {
                    candidate =
                            candidate.deschematize(e.getArguments(), myBuilder
                                    .getInnermostActiveScope(),
                                    myDefinitionSchematicTypes);
                    candidateType = (MTFunction) candidate.getType();
                    emitDebug(candidate.getType() + " deschematizes to "
                            + candidateType);

                    if (comparison.compare(e, eType, candidateType)) {

                        if (match != null) {
                            throw new SrcErrorException("multiple "
                                    + comparison.description() + " domain "
                                    + "matches; for example, "
                                    + match.getName() + " : " + match.getType()
                                    + " and " + candidate.getName() + " : "
                                    + candidate.getType()
                                    + " -- consider explicitly qualifying.", e
                                    .getStart());
                        }

                        match = candidate;
                    }
                }
                catch (NoSolutionException nse) {
                    //couldn't deschematize--try the next one
                    emitDebug(candidate.getType() + " doesn't deschematize "
                            + "against " + e.getArguments());
                }
            }
        }
        if (match == null) {
            throw NoSolutionException.INSTANCE;
        }
        return match;
    }

    public static void emitDebug(String msg) {
        if (PRINT_DEBUG) {
            System.out.println(msg);
        }
    }

    //-------------------------------------------------------------------
    //   Helper classes
    //-------------------------------------------------------------------

    private static class ExactDomainMatch
            implements
                TypeComparison<MathSymbolAST, MTFunction> {

        @Override
        public boolean compare(MathSymbolAST foundValue, MTFunction foundType,
                MTFunction expectedType) {

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
                TypeComparison<MathSymbolAST, MTFunction> {

        @Override
        public boolean compare(MathSymbolAST foundValue, MTFunction foundType,
                MTFunction expectedType) {

            return expectedType.parametersMatch(foundValue.getArguments(),
                    INEXACT_PARAMETER_MATCH);
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

    private class InexactParameterMatch
            implements
                TypeComparison<ExprAST, MTType> {

        @Override
        public boolean compare(ExprAST foundValue, MTType foundType,
                MTType expectedType) {

            boolean result =
                    myTypeGraph.isKnownToBeIn(foundValue, expectedType);

            //Todo: I'm not currently considering lambdas.
            if (!result && foundValue instanceof MathLambdaAST
                    && expectedType instanceof MTFunction) {
                MathLambdaAST foundValueAsLambda = (MathLambdaAST) foundValue;
                MTFunction expectedTypeAsFunction = (MTFunction) expectedType;

                result =
                        myTypeGraph.isSubtype(foundValueAsLambda.getMathType()
                                .getDomain(), expectedTypeAsFunction
                                .getDomain())
                                && myTypeGraph.isKnownToBeIn(foundValueAsLambda
                                        .getBody(), expectedTypeAsFunction
                                        .getRange());
            }
            return result;
        }

        @Override
        public String description() {
            return "inexact";
        }
    }
}
