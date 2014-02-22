package edu.clemson.cs.r2jt.translation;

import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.archiving.Archiver;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerStackVisitor;
import edu.clemson.cs.r2jt.typeandpopulate.*;
import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.OperationEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramTypeEntry;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.*;
import edu.clemson.cs.r2jt.typeandpopulate.query.OperationQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.UnqualifiedNameQuery;
import edu.clemson.cs.r2jt.utilities.SourceErrorException;
import org.stringtemplate.v4.*;

import java.io.File;
import java.util.*;

public abstract class AbstractTranslator extends TreeWalkerStackVisitor {

    protected static final boolean PRINT_DEBUG = true;

    protected final CompileEnvironment myInstanceEnvironment;
    protected ModuleScope myScope = null;

    /**
     * <p>This gives us access to additional <code>ModuleScope</code>s. This
     * comes in handy for <code>ConceptBodyModuleDec</code> translation.</p>
     */
    protected final MathSymbolTableBuilder myBuilder;

    /**
     * <p>A pointer to a <code>SymbolTableEntry</code> that corresponds to
     * the <code>FacilityDec</code> currently being walked.  If one isn't
     * being walked, this should be <code>null</code>.</p>
     */
    protected FacilityEntry myCurrentFacilityEntry = null;

    /**
     * <p>The <code>STGroup</code> that houses all templates used by a
     * given target language.</p>
     */
    protected STGroup myGroup;

    /**
     * <p>The top of this <code>Stack</code> maintains a reference to the
     * template actively being built or added to, and the bottom refers to
     * <code>module</code> - the outermost enclosing template for all target
     * languages.</p>
     *
     * <p>Proper usage should generally involve: Pushing in <tt>pre</tt>,
     * modifying top arbitrarily with <tt>pre</tt>'s children, popping in the
     * corresponding <tt>post</tt>, then adding the popped template to the
     * appropriate enclosing template (i.e. the new/current top).</p>
     */
    protected Stack<ST> myActiveTemplates = new Stack<ST>();

    /**
     * <p>This set keeps track of any additional <code>includes</code> or
     * <code>imports</code> needed to run the translated file. We call
     * it <em>dynamic</em> since only certain nodes trigger additions to this
     * set (i.e. <code>FacilityDec</code> nodes).</p>
     */
    protected Set<String> myDynamicImports = new HashSet<String>();

    /**
     * <p>This flag is <code>true</code> when walking the children of a
     * <code>whileStmtChanging</code> clause; <code>false</code> otherwise.</p>
     */
    // TODO : This global can be safely removed once walk methods for virtual
    //        list nodes are fixed. Talk to Blair about this.
    protected boolean myWhileStmtChangingClause = false;

    public AbstractTranslator(CompileEnvironment env, ScopeRepository repo) {
        myInstanceEnvironment = env;
        myBuilder = (MathSymbolTableBuilder) repo;
    }

    //-------------------------------------------------------------------
    //   Visitor methods
    //-------------------------------------------------------------------

    @Override
    public void preModuleDec(ModuleDec node) {

        try {
            myScope = myBuilder.getModuleScope(new ModuleIdentifier(node));

            ST outermostEnclosingTemplate = myGroup.getInstanceOf("module");

            outermostEnclosingTemplate.add("includes", myGroup.getInstanceOf(
                    "include").add("directories", "RESOLVE"));

            myActiveTemplates.push(outermostEnclosingTemplate);

            AbstractTranslator.emitDebug("----------------------------------\n"
                    + "Translate: " + node.getName().getName()
                    + "\n----------------------------------");
        }
        catch (NoSuchSymbolException nsse) {
            noSuchModule(node.getName());
            throw new RuntimeException();
        }
    }

    @Override
    public void preUsesItem(UsesItem node) {
        try {
            FacilityEntry e =
                    myScope.queryForOne(
                            new UnqualifiedNameQuery(node.getName().getName()))
                            .toFacilityEntry(null);

            String spec =
                    e.getFacility().getSpecification().getModuleIdentifier()
                            .toString();

            List<String> pathPieces = getPathList(getFile(null, spec));

            myActiveTemplates.firstElement().add(
                    "includes",
                    myGroup.getInstanceOf("include").add("directories",
                            pathPieces));
        }
        catch (NoSuchSymbolException nsse) {
            // TODO: Hack Hack. Figure out a way to do this properly.
            // things like static_array_template show up here.
        }
        catch (DuplicateSymbolException dse) {
            throw new RuntimeException(dse);
        }
    }

    @Override
    public void preCallStmt(CallStmt node) {

        ST callStmt;
        String qualifier =
                getCallQualifier(node.getQualifier(), node.getName(), node
                        .getArguments());

        if (qualifier != null) {
            callStmt =
                    myGroup.getInstanceOf("qualified_call").add("name",
                            node.getName().getName()).add("qualifier",
                            qualifier);
        }
        else {
            callStmt =
                    myGroup.getInstanceOf("unqualified_call").add("name",
                            node.getName().getName());
        }

        myActiveTemplates.push(callStmt);
    }

    @Override
    public void postCallStmt(CallStmt node) {
        ST callStmt = myActiveTemplates.pop();
        myActiveTemplates.peek().add("stmts", callStmt);
    }

    @Override
    public void preWhileStmt(WhileStmt node) {
        ST whileStmt = myGroup.getInstanceOf("while");
        myActiveTemplates.push(whileStmt);
    }

    @Override
    public void postWhileStmt(WhileStmt node) {
        ST whileStmt = myActiveTemplates.pop();
        myActiveTemplates.peek().add("stmts", whileStmt);
    }

    @Override
    public void preWhileStmtChanging(WhileStmt node) {
        myWhileStmtChangingClause = true;
    }

    @Override
    public void postWhileStmtChanging(WhileStmt node) {
        myWhileStmtChangingClause = false;
    }

    @Override
    public void preFuncAssignStmt(FuncAssignStmt node) {

        String qualifier =
                getDefiningFacilityEntry(node.getVar().getProgramType())
                        .getName();

        ST assignStmt =
                myGroup.getInstanceOf("qualified_call").add("name", "assign")
                        .add("qualifier", qualifier);

        myActiveTemplates.push(assignStmt);
    }

    @Override
    public void postFuncAssignStmt(FuncAssignStmt node) {
        ST assignStmt = myActiveTemplates.pop();
        myActiveTemplates.peek().add("stmts", assignStmt);
    }

    @Override
    public void preProgramIntegerExp(ProgramIntegerExp node) {

        ST integerExp =
                myGroup.getInstanceOf("var_init").add("type",
                        getVariableTypeTemplate(node.getProgramType()));

        integerExp.add("facility",
                getDefiningFacilityEntry(node.getProgramType()).getName()).add(
                "arguments", node.getValue());

        myActiveTemplates.peek().add("arguments", integerExp);
    }

    public void preProgramStringExp(ProgramStringExp node) {

        ST stringExp =
                myGroup.getInstanceOf("var_init").add("type",
                        getVariableTypeTemplate(node.getProgramType()));

        stringExp.add("facility",
                getDefiningFacilityEntry(node.getProgramType()).getName()).add(
                "arguments", node.getValue());

        myActiveTemplates.peek().add("arguments", stringExp);
    }

    @Override
    public void preProgramParamExp(ProgramParamExp node) {

        ST paramExp;

        String qualifier =
                getCallQualifier(null, node.getName(), node.getArguments());

        if (qualifier != null) {
            paramExp =
                    myGroup.getInstanceOf("qualified_param_exp").add(
                            "qualifier", qualifier).add("name",
                            node.getName().getName());
        }
        else {
            paramExp =
                    myGroup.getInstanceOf("unqualified_param_exp").add("name",
                            node.getName().getName());
        }

        myActiveTemplates.push(paramExp);
    }

    @Override
    public void postProgramParamExp(ProgramParamExp node) {
        ST paramExp = myActiveTemplates.pop();
        myActiveTemplates.peek().add("arguments", paramExp);
    }

    @Override
    public boolean walkVariableRecordExp(VariableRecordExp node) {
        return true;
    }

    @Override
    public void preVariableDotExp(VariableDotExp node) {

        PTType type = node.getSegments().get(0).getProgramType();

        ST dotExp =
                myGroup.getInstanceOf("variable_dot_exp").add("modulename",
                        myScope.getDefiningElement().getName().getName()).add(
                        "typename", getTypeName(type));

        myActiveTemplates.push(dotExp);
    }

    @Override
    public void postVariableDotExp(VariableDotExp node) {
        ST dotExp = myActiveTemplates.pop();
        myActiveTemplates.peek().add("arguments", dotExp);
    }

    @Override
    public void preFacilityOperationDec(FacilityOperationDec node) {
        ST operation =
                getOperationLikeTemplate((node.getReturnTy() != null) ? node
                        .getReturnTy().getProgramTypeValue() : null, node
                        .getName().getName(), true);

        myActiveTemplates.push(operation);

        if (node.getReturnTy() != null) {
            addVariableTemplate(node.getReturnTy().getProgramTypeValue(), node
                    .getName().getName());
        }
    }

    @Override
    public void preOperationDec(OperationDec node) {
        ST operation =
                getOperationLikeTemplate((node.getReturnTy() != null) ? node
                        .getReturnTy().getProgramTypeValue() : null, node
                        .getName().getName(), false);

        myActiveTemplates.push(operation);
    }

    @Override
    public void preProcedureDec(ProcedureDec node) {
        ST operation =
                getOperationLikeTemplate((node.getReturnTy() != null) ? node
                        .getReturnTy().getProgramTypeValue() : null, node
                        .getName().getName(), true);

        myActiveTemplates.push(operation);

        if (node.getReturnTy() != null) {
            addVariableTemplate(node.getReturnTy().getProgramTypeValue(), node
                    .getName().getName());
        }
    }

    @Override
    public void postOperationDec(OperationDec node) {
        ST operation = myActiveTemplates.pop();
        myActiveTemplates.peek().add("functions", operation);
    }

    @Override
    public void postProcedureDec(ProcedureDec node) {

        if (node.getReturnTy() != null) {
            ST returnStmt =
                    myGroup.getInstanceOf("return_stmt").add("name",
                            node.getName().getName());

            myActiveTemplates.peek().add("stmts", returnStmt);
        }

        ST operation = myActiveTemplates.pop();
        myActiveTemplates.peek().add("functions", operation);
    }

    @Override
    public void postFacilityOperationDec(FacilityOperationDec node) {

        if (node.getReturnTy() != null) {
            ST returnStmt =
                    myGroup.getInstanceOf("return_stmt").add("name",
                            node.getName().getName());

            myActiveTemplates.peek().add("stmts", returnStmt);
        }

        ST operation = myActiveTemplates.pop();
        myActiveTemplates.peek().add("functions", operation);
    }

    @Override
    public void preVarDec(VarDec node) {
        addVariableTemplate(node.getTy().getProgramTypeValue(), node.getName()
                .getName());
    }

    @Override
    public void preParameterVarDec(ParameterVarDec node) {

        PTType type = node.getTy().getProgramTypeValue();

        ST parameter =
                myGroup.getInstanceOf("parameter").add("type",
                        getParameterTypeTemplate(type)).add("name",
                        node.getName().getName());

        myActiveTemplates.peek().add("parameters", parameter);
    }

    @Override
    public void postModuleDec(ModuleDec node) {

        myActiveTemplates.firstElement().add("includes", myDynamicImports);

        AbstractTranslator.emitDebug("----------------------------------\n"
                + "End: " + node.getName().getName()
                + "\n----------------------------------");
    }

    //-------------------------------------------------------------------
    //   Helper methods
    //-------------------------------------------------------------------

    protected abstract ST getVariableTypeTemplate(PTType type);

    protected abstract ST getOperationTypeTemplate(PTType type);

    protected abstract ST getParameterTypeTemplate(PTType type);

    protected abstract String getFunctionModifier();

    /**
     * <p>Creates, fills-in, and inserts a formed <code>parameter</code>
     * template into the active template.</p>
     *
     * @param type A <code>PTType</code>.
     * @param name A string containing the name of the parameter.
     */
    protected void addParameterTemplate(PTType type, String name) {
        ST parameter =
                myGroup.getInstanceOf("parameter").add("type",
                        getVariableTypeTemplate(type)).add("name", name);

        myActiveTemplates.peek().add("parameters", parameter);
    }

    /**
     * Places both generic variables and "regular" variables into..
     * @param type
     * @param name
     */
    protected void addVariableTemplate(PTType type, String name) {
        ST init, variable;

        if (type instanceof PTGeneric) {
            init =
                    myGroup.getInstanceOf("rtype_init").add("typeName",
                            getTypeName(type));
        }
        else {
            init =
                    myGroup.getInstanceOf("var_init").add("type",
                            getVariableTypeTemplate(type)).add("facility",
                            getDefiningFacilityEntry(type).getName());
        }
        variable =
                myGroup.getInstanceOf("var_decl").add("name", name).add("type",
                        getVariableTypeTemplate(type)).add("init", init);

        myActiveTemplates.peek().add("variables", variable);
    }

    /**
     * <p></p>
     * @param returnType
     * @param name
     *
     * @param hasBody
     */
    protected ST getOperationLikeTemplate(PTType returnType, String name,
            boolean hasBody) {

        String attributeName = (hasBody) ? "function_def" : "function_decl";

        ST operationLikeThingy =
                myGroup.getInstanceOf(attributeName).add("name", name).add(
                        "modifier", getFunctionModifier());

        operationLikeThingy.add("type",
                (returnType != null) ? getOperationTypeTemplate(returnType)
                        : "void");

        return operationLikeThingy;
    }

    /**
     * <p>Returns a <code>List</code> of <code>ProgramParameterEntry</code>s
     * representing the formal params of module <code>moduleName</code>.</p>
     *
     * @param moduleName A <code>PosSymbol</code> containing the name of the
     *                   module whose parameters
     * @return The formal parameters.
     */
    protected List<ProgramParameterEntry> getModuleFormalParameters(
            PosSymbol moduleName) {
        try {
            ModuleDec spec =
                    myBuilder.getModuleScope(
                            new ModuleIdentifier(moduleName.getName()))
                            .getDefiningElement();

            return myBuilder.getScope(spec).getFormalParameterEntries();
        }
        catch (NoSuchSymbolException nsse) {
            noSuchModule(moduleName);
            throw new RuntimeException();
        }
    }

    /**
     * <p>Retrieves the <code>name</code> of a <code>PTType</code>. The
     * <code>PTType</code> baseclass by itself doesn't provide this
     * functionality. This method goes through the trouble of casting to the
     * correct subclass so we can use the <code>getName</code> method.</p>
     *
     * @param type A <code>PTType</code>.
     * @return <code>type</code>'s actual name rather than the more easily
     *         accessible <code>toString</code> representation.
     */
    protected String getTypeName(PTType type) {

        String result;

        if (type == null) {
            return null;
        }
        if (type instanceof PTElement) {
            // Not sure under what conditions this would appear in output.
            result = "PTELEMENT";
        }
        else if (type instanceof PTGeneric) {
            result = ((PTGeneric) type).getName();
        }
        else if (type instanceof PTRepresentation) {
            result = ((PTRepresentation) type).getFamily().getName();
        }
        else if (type instanceof PTFamily) {
            result = ((PTFamily) type).getName();
        }
        else {
            throw new UnsupportedOperationException("Translation has "
                    + "encountered an unrecognized PTType: " + type.toString()
                    + ". Backing out.");
        }
        return result;
    }

    /**
     * <p></p>
     * @param type
     * @return
     */
    protected FacilityEntry getDefiningFacilityEntry(PTType type) {

        FacilityEntry result = null;
        String searchString = getTypeName(type);

        try {
            ProgramTypeEntry te =
                    myScope.queryForOne(
                            new UnqualifiedNameQuery(type.toString()))
                            .toProgramTypeEntry(null);

            List<FacilityEntry> facilities =
                    myScope.query(new EntryTypeQuery(FacilityEntry.class,
                            MathSymbolTable.ImportStrategy.IMPORT_NAMED,
                            MathSymbolTable.FacilityStrategy.FACILITY_IGNORE));

            for (FacilityEntry facility : facilities) {
                if (te.getSourceModuleIdentifier().equals(
                        facility.getFacility().getSpecification()
                                .getModuleIdentifier())) {

                    result = facility;
                }
            }
        }
        catch (NoSuchSymbolException nsse) {
            throw new RuntimeException("Translation unable to find a "
                    + "FacilityEntry locally or otherwise that defines type: "
                    + type.toString());
        }
        catch (DuplicateSymbolException dse) {
            throw new RuntimeException(dse); // shouldn't fire.
        }
        return result;
    }

    /**
     *
     * @param qualifier
     * @param name
     * @param args
     * @return
     */
    protected String getCallQualifier(PosSymbol qualifier, PosSymbol name,
            List<ProgramExp> args) {

        String result = null;
        List<PTType> argTypes = new LinkedList<PTType>();
        List<FacilityEntry> matches = new LinkedList<FacilityEntry>();

        if (qualifier != null) {
            return qualifier.getName();
        }
        try {

            for (ProgramExp arg : args) {
                argTypes.add(arg.getProgramType());
            }

            OperationEntry oe =
                    myScope.queryForOne(
                            new OperationQuery(null, name, argTypes))
                            .toOperationEntry(null);

            // Grab FacilityEntries in scope whose specification matches
            // oe's SourceModuleIdentifier.
            List<FacilityEntry> facilities =
                    myScope.query(new EntryTypeQuery(FacilityEntry.class,
                            MathSymbolTable.ImportStrategy.IMPORT_NAMED,
                            MathSymbolTable.FacilityStrategy.FACILITY_IGNORE));

            for (FacilityEntry f : facilities) {
                if (oe.getSourceModuleIdentifier().equals(
                        f.getFacility().getSpecification()
                                .getModuleIdentifier())) {
                    matches.add(f);
                }
            }

            // There should only be two cases:
            // 1. Size == 1 => a unique facility is instantiated
            //          in scope whose specification matches oe's. So the
            //          appropriate qualifier is that facility's name.
            if (matches.size() == 1) {
                result = matches.get(0).getName();
            }
            // 2. Size > 1 => multiple facilities instantiated use
            //          oe's SourceModuleIdentifier as a specification.
            //          Which facility's name to use as a qualifier is
            //          ambiguous -- so off to argument examination we go.
            if (matches.size() > 1) {
                result = "TEMP";
                //    result = findQualifyingArgument(oe, args);
            }
            // 3. Size == 0 => the operation owning the call is
            //          defined locally. So no need to qualify.
        }
        catch (NoSuchSymbolException nsse) {
            // FOR NOW.
            return "TEMP_QUALIFIER";
            // noSuchSymbol(qualifier, name);
        }
        catch (DuplicateSymbolException dse) {
            throw new RuntimeException(dse);
        }
        return result;
    }

    //-------------------------------------------------------------------
    //   Error handling
    //-------------------------------------------------------------------

    public void noSuchModule(PosSymbol module) {
        throw new SourceErrorException(
                "Module does not exist or is not in scope.", module);
    }

    public void noSuchSymbol(PosSymbol qualifier, PosSymbol symbol) {
        noSuchSymbol(qualifier, symbol.getName(), symbol.getLocation());
    }

    public void noSuchSymbol(PosSymbol qualifier, String symbolName, Location l) {

        String message;

        if (qualifier == null) {
            message = "Translation was unable to find symbol: " + symbolName;
        }
        else {
            message =
                    "No such symbol in module: " + qualifier.getName() + "."
                            + symbolName;
        }

        throw new SourceErrorException(message, l);
    }

    //-------------------------------------------------------------------
    //   Utility, output, and flag-related methods
    //-------------------------------------------------------------------

    /**
     * <p>Returns a <code>File</code> given either a <code>ModuleDec</code>
     * <em>or</em> a string containing the name of a
     * <code>ConceptBodyModuleDec</code>.</p>
     *
     * @param module A <code>ModuleDec</code>.
     * @param name The name of an existing <code>ConceptBodyModuleDec</code>.
     *
     * @return A <code>File</code>.
     */
    protected File getFile(ModuleDec module, String name) {

        File result;

        if (module == null && name == null) {
            throw new IllegalArgumentException("Translation requires at least"
                    + " one non-null argument to retrieve the correct file"
                    + " from the compile environment.");
        }

        if (module != null) {
            ModuleID id = ModuleID.createID(module);
            result = myInstanceEnvironment.getFile(id);
        }
        else {
            PosSymbol conceptName = new PosSymbol(null, Symbol.symbol(name));
            ModuleID id = ModuleID.createConceptID(conceptName);
            result = myInstanceEnvironment.getFile(id);
        }
        return result;
    }

    /**
     * <p>Given a <code>File</code> object, this hacky little method returns a
     * list whose elements consist of the directory names of the input file's
     * absolute path.</p>
     *
     * <p>For example, given file (with directory path) :
     *      <pre>Resolve-Workspace/RESOLVE/Main/X.fa</pre>
     *
     * <p>this method will return :
     *      <pre>[RESOLVE, Main, X]</pre>
     *
     * <p>Note that any directories prior to the root "RESOLVE" directory are
     * stripped, along with <code>source</code>'s file extension.</p>
     *
     * @param source The input <code>File</code>.
     * @return A list whose elements correspond to the path directories
     *         of <code>source</code>.
     */
    protected List<String> getPathList(File source) {

        String currentToken, path;
        boolean rootDirectoryFound = false;

        path =
                (source.exists()) ? source.getAbsolutePath() : source
                        .getParentFile().getAbsolutePath();

        List<String> result = new LinkedList<String>();
        StringTokenizer stTok = new StringTokenizer(path, File.separator);

        while (stTok.hasMoreTokens()) {
            currentToken = stTok.nextToken();

            if (currentToken.equalsIgnoreCase("RESOLVE") || rootDirectoryFound) {
                rootDirectoryFound = true;

                if (currentToken.contains(".")) {
                    currentToken =
                            currentToken
                                    .substring(0, currentToken.indexOf('.'));
                }
                result.add(currentToken);
            }
        }
        return result;
    }

    public static void emitDebug(String msg) {
        if (PRINT_DEBUG) {
            System.out.println(msg);
        }
    }

    public void outputCode(File outputFile) {
        if (!myInstanceEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_WEB)
                || myInstanceEnvironment.flags.isFlagSet(Archiver.FLAG_ARCHIVE)) {
            //    outputAsFile(outputFile.getAbsolutePath(),
            //            myOutermostEnclosingTemplate.render());
            System.out.println(Formatter.formatCode(myActiveTemplates.peek()
                    .render()));
        }
    }
}