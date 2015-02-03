/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.clemson.cs.r2jt.translation;

import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.archiving.Archiver;
import edu.clemson.cs.r2jt.compilereport.CompileReport;
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
import edu.clemson.cs.r2jt.typeandpopulate.query.EntryTypeQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.OperationQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.UnqualifiedNameQuery;
import edu.clemson.cs.r2jt.utilities.SourceErrorException;
import org.stringtemplate.v4.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Pattern;

public abstract class AbstractTranslator extends TreeWalkerStackVisitor {

    protected static final boolean PRINT_DEBUG = false;

    protected final CompileEnvironment myInstanceEnvironment;

    protected ModuleScope myScope = null;
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
     * <code>shell</code> - the outermost enclosing template for all
     * currently supported target languages.</p>
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
     * set (i.e. <code>FacilityDec</code>s).</p>
     */
    protected Set<String> myDynamicImports = new HashSet<String>();

    protected final String[] noTranslate =
            { "Std_Boolean_Fac.fa", "Std_Char_Str_Fac.fa",
                    "Std_Character_Fac.fa", "Std_Integer_Fac.fa",
                    "Std_Boolean_Realiz", "Integer_Template.co",
                    "Character_Template.co", "Char_Str_Template.co",
                    "Seq_Input_Template.co", "Seq_Output_Template.co",
                    "Print.co", "Std_Location_Linking_Realiz.rb" };

    /**
     * <p>This flag is <code>true</code> when walking the children of a
     * <code>WhileStmtChanging</code> clause; <code>false</code> otherwise.</p>
     */
    // TODO : This global can be safely removed once walk methods for virtual
    //        list nodes are fixed. Talk to Blair about this.
    protected boolean myWhileStmtChangingClause = false;
    protected boolean myWalkingInitFinalItemFlag = false;
    /**
     * <p>This stores the facility qualifier when we encounter a
     * <code>ProgramDotExp</code>. Once we are done walking the
     * <code>ProgramDotExp</code>, its value becomes Null again.</p>
     */
    private PosSymbol myFacilityQualifier;

    public AbstractTranslator(CompileEnvironment env, ScopeRepository repo) {
        myInstanceEnvironment = env;
        myBuilder = (MathSymbolTableBuilder) repo;
        myFacilityQualifier = null;
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
    public void preIfStmt(IfStmt node) {
        ST ifStmt = myGroup.getInstanceOf("if");
        myActiveTemplates.push(ifStmt);
    }

    public void postIfStmt(IfStmt node) {
        ST ifStmt = myActiveTemplates.pop();
        myActiveTemplates.peek().add("stmts", ifStmt);
    }

    // TODO : This is probably going to need some tweaking once else-ifs
    //        are fixed.
    public void preIfStmtElseclause(IfStmt data) {

        //IfStmtElseClauses are nested within the tree. So if we're here,
        //add the if part to the outermost stmt block.
        ST ifPart = myActiveTemplates.pop();
        myActiveTemplates.peek().add("stmts", ifPart);

        ST elseStmt = myGroup.getInstanceOf("else");
        myActiveTemplates.push(elseStmt);
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
    public void postSwapStmt(SwapStmt node) {
        ST swapStmt = myActiveTemplates.pop();
        myActiveTemplates.peek().add("stmts", swapStmt);
    }

    @Override
    public void preProgramDotExp(ProgramDotExp node) {
        myFacilityQualifier = node.getQualifier();
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

    @Override
    public void preInitItem(InitItem e) {
        myWalkingInitFinalItemFlag = true;
    }

    @Override
    public void postInitItem(InitItem e) {
        myWalkingInitFinalItemFlag = false;
    }

    @Override
    public void preFinalItem(FinalItem e) {
        myWalkingInitFinalItemFlag = true;
    }

    @Override
    public void postFinalItem(FinalItem e) {
        myWalkingInitFinalItemFlag = false;
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
                getCallQualifier(myFacilityQualifier, node.getName(), node
                        .getArguments());

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
    public void postProgramDotExp(ProgramDotExp node) {
        myFacilityQualifier = null;
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
    public boolean walkVariableDotExp(VariableDotExp e) {

        //If we encounter a dot expression in an initialization clause,
        //we to basically pretend that its a normal name expression. This is not
        //ideal, but for now, our model of java code requires/expects this.

        //TODO: Think about cases in which the java will actually need a
        //variableDotExp, and the RESOLVE source that will elicit this.
        if (myWalkingInitFinalItemFlag) {
            preAny(e);
            preExp(e);
            preProgramExp(e);
            preVariableExp(e);

            //For now we assume we're dealing with a name, since we need to
            //initialize just the name.
            preVariableNameExp((VariableNameExp) e.getSegments().get(1));
            postVariableNameExp((VariableNameExp) e.getSegments().get(1));

            postVariableExp(e);
            postProgramExp(e);
            postExp(e);
            postAny(e);
        }
        return myWalkingInitFinalItemFlag;
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

        if (!myDynamicImports.isEmpty()) {
            myActiveTemplates.firstElement().add("includes", myDynamicImports);
        }
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
     * <p>Constructs and adds a <code>parameter</code> to the currently active
     * template.</p>
     *
     * @param type A <code>PTType</code> representing the 'declared type' of
     *             the parameter.
     * @param name The name of the parameter.
     */
    protected void addParameterTemplate(PTType type, String name) {
        ST parameter =
                myGroup.getInstanceOf("parameter").add("type",
                        getVariableTypeTemplate(type)).add("name", name);

        myActiveTemplates.peek().add("parameters", parameter);
    }

    /**
     * <p>Constructs and adds a <code>var</code> template to the currently
     * active template.</p>
     *
     * @param type A <code>PTType</code> representing the type of the
     *             variable
     * @param name The name of the variable.
     */
    protected void addVariableTemplate(PTType type, String name) {
        ST init, variable;

        if (type instanceof PTGeneric) {
            init =
                    myGroup.getInstanceOf("rtype_init").add("typeName",
                            getTypeName(type));
        }
        else if (type instanceof PTFacilityRepresentation) {
            init =
                    myGroup.getInstanceOf("facility_type_var_init").add("name",
                            getTypeName(type));
        }
        else if (getDefiningFacilityEntry(type) != null) {
            init =
                    myGroup.getInstanceOf("var_init").add("type",
                            getVariableTypeTemplate(type)).add("facility",
                            getDefiningFacilityEntry(type).getName());
        }
        else {
            init =
                    myGroup.getInstanceOf("enhancement_var_init").add("type",
                            getVariableTypeTemplate(type));
        }
        variable =
                myGroup.getInstanceOf("var_decl").add("name", name).add("type",
                        getVariableTypeTemplate(type)).add("init", init);

        AbstractTranslator.emitDebug(("Adding variable: " + name
                + " with type: " + getTypeName(type)));

        myActiveTemplates.peek().add("variables", variable);
    }

    /**
     * <p>Returns a <code>function</code> template 'filled in' with the
     * attributes provided.</p>
     *
     * @param returnType A <code>PTType</code> representative of the
     *                   function's return <code>type</code> attribute.
     * @param name       The name attribute.
     * @param hasBody    A boolean indicating whether or not the function being
     *                   created should have a body or not.
     *
     * @return A <code>function</code> template with the <code>type</code>
     *         and <code>name</code> attributes formed and filled in.
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
     * <p>Returns a list of <code>ProgramParameterEntry</code>s representing
     * the formal parameters of module <code>moduleName</code>.</p>
     *
     * @param moduleName A <code>PosSymbol</code> containing the name of the
     *                   module whose parameters
     * @return A (possibly empty) list of formal parameters.
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
     * <p>Returns the 'name' component of a <code>PTType</code>.
     *
     * @param type A <code>PTType</code>.
     *
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
        else if (type instanceof PTFacilityRepresentation) {
            result = ((PTFacilityRepresentation) type).getName();
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
     * <p>Searches for the <code>FacilityEntry</code> responsible for
     * bringing the SymbolTableEntry referenced by <code>type</code> into the
     * <code>ModuleScope</code> being translated.</p>
     *
     * @param type  The <code>PTType</code> we want symboltable info for.
     *
     * @return The <code>FacilityEntry</code> that defines <code>type</code>.
     */
    protected FacilityEntry getDefiningFacilityEntry(PTType type) {

        FacilityEntry result = null;
        //String searchString = getTypeName(type);

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

    protected String getCallQualifier(PosSymbol qualifier, PosSymbol name,
            List<ProgramExp> args) {

        String result = null;
        List<PTType> argTypes = new LinkedList<PTType>();
        FacilityEntry definingFacility = null;

        try {
            for (ProgramExp arg : args) {
                argTypes.add(arg.getProgramType());
            }

            OperationEntry oe =
                    myScope.queryForOne(
                            new OperationQuery(null, name, argTypes))
                            .toOperationEntry(null);

            // We're dealing with local operation, then no qualifier.
            if (myScope.getModuleIdentifier().equals(
                    oe.getSourceModuleIdentifier())) {
                return null;
            }
            // Grab FacilityEntries in scope whose specification matches
            // oe's SourceModuleIdentifier.
            List<FacilityEntry> facilities =
                    myScope.query(new EntryTypeQuery(FacilityEntry.class,
                            MathSymbolTable.ImportStrategy.IMPORT_NAMED,
                            MathSymbolTable.FacilityStrategy.FACILITY_IGNORE));

            boolean comesFromEnhancement = false;

            for (FacilityEntry f : facilities) {

                if (qualifier != null
                        && f.getName().equals(qualifier.getName())) {
                    definingFacility = f;
                    break;
                }

                if (oe.getSourceModuleIdentifier().equals(
                        f.getFacility().getSpecification()
                                .getModuleIdentifier())) {
                    definingFacility = f;
                }

                for (ModuleParameterization p : f.getEnhancements()) {
                    if (oe.getSourceModuleIdentifier().equals(
                            p.getModuleIdentifier())) {
                        definingFacility = f;
                        comesFromEnhancement = true;
                    }
                }
            }

            // If we're in an enhancement realization, some calls rightly won't
            // have a facility, and hence no qualifier should be returned.
            if (definingFacility == null) {
                return null;
            }

            // This is the idiotic part, really this is mixing the model and
            // view (since I've put the '(' .. ')' cast parens in but it was
            // mostly so I wouldn't have to write yet another super specific
            // template -- there is likely a more elegant way.
            if (definingFacility.getEnhancements().size() >= 2
                    && comesFromEnhancement) {
                result =
                        "((" + oe.getSourceModuleIdentifier() + ")"
                                + definingFacility.getName() + ")";
            }
            else {
                result = definingFacility.getName();
            }
        }
        catch (NoSuchSymbolException nsse) {
            throw new RuntimeException(); // Should've been caught a long
            // time ago.
        }
        catch (DuplicateSymbolException dse) {
            throw new RuntimeException(); // Should've been caught a long
            // time ago.
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
        String currentToken;
        boolean rootDirectoryFound = false;

        List<String> result = new LinkedList<String>();
        StringTokenizer stTok =
                new StringTokenizer(source.getAbsolutePath(), File.separator);

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
        // this needs some more thought, though imports are likely to be
        // adjusted quite a bit in the near future.
        result.remove(result.size() - 1);
        return result;
    }

    public boolean onNoCompileList(File file) {
        Pattern p = null;
        String fileName = file.toString();
        for (String s : noTranslate) {
            p = Pattern.compile(s);
            if (p.matcher(fileName).find()) {
                return true;
            }
        }
        return false;
    }

    public static void emitDebug(String msg) {
        if (PRINT_DEBUG) {
            System.out.println(msg);
        }
    }

    public void outputCode(File outputFile) {
        if (!myInstanceEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_WEB)
                || myInstanceEnvironment.flags.isFlagSet(Archiver.FLAG_ARCHIVE)) {
            outputAsFile(outputFile.getAbsolutePath(), myActiveTemplates.peek()
                    .render());
            // System.out.println(myActiveTemplates.peek().render());
        }
        else {
            outputToReport(myActiveTemplates.peek().render());
        }
    }

    private void outputToReport(String fileContents) {
        CompileReport report = myInstanceEnvironment.getCompileReport();
        report.setTranslateSuccess();
        report.setOutput(fileContents);
    }

    // TODO : Redo this and make it appropriate for the abstract translator.
    private void outputAsFile(String fileName, String fileContents) {
        String[] temp = fileName.split("\\.");
        fileName = temp[0] + ".java";
        if (fileContents != null && fileContents.length() > 0) {
            try {
                File outputJavaFile = new File(fileName);
                if (!outputJavaFile.exists()) {
                    outputJavaFile.createNewFile();
                }
                byte buf[] = fileContents.getBytes();
                OutputStream outFile = new FileOutputStream(outputJavaFile);
                outFile.write(buf);
                outFile.close();
            }
            catch (IOException ex) {
                //FIX: Something should be done with this exception - ya think?
            }
        }
        else {
            System.out.println("No translation available for " + fileName);
        }
    }
}