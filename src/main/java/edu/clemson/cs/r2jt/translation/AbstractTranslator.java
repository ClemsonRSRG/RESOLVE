package edu.clemson.cs.r2jt.translation;

import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.archiving.Archiver;
import edu.clemson.cs.r2jt.compilereport.CompileReport;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.translation.bookkeeping.Bookkeeper;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerVisitor;
import edu.clemson.cs.r2jt.typeandpopulate.*;
import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.OperationEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramTypeEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTGeneric;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate.query.NameAndEntryTypeQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.OperationQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.UnqualifiedNameQuery;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.utilities.SourceErrorException;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>Overrides visitor methods shareable between currently supported target
 * languages (C & Java). Modify this as needed whenever/if ever new target
 * languages are incorporated into the project.</p>
 */
public class AbstractTranslator extends TreeWalkerVisitor {

    /**
     * <p>If <code>true</code>, as we walk the tree, debug information will
     * be streamed to the console. This should be <code>false</code> when the
     * translator isn't actively being worked on.</p>
     */
    protected static final boolean PRINT_DEBUG = true;

    protected final CompileEnvironment myInstanceEnvironment;
    protected final ModuleScope myModuleScope;

    /**
     * <p>This is where all information collected during the treewalk here
     * goes. The <code>Bookkeeper</code> and its concrete subclasses handle
     * how all information is collected, organized, and ultimately arranged
     * on the translated page via calls to override <code>getString</code>
     * methods.</p>
     */
    protected Bookkeeper myBookkeeper;

    /**
     * <p>Abstracts away a language-specific qualification style. For example,
     * with <code>StackFac.Pop</code> vs <code></code>StackFac->Pop</code>
     * <code>myQualifier</code> symbol could be either "." or "->".</p>
     */
    protected String myQualifierSymbol;

    /**
     * <p>While walking a <code>FacilityDec</code>, this maintains a pointer
     * to that facility, and, by extension, enhancements enclosed within. If we
     * havent encountered a <code>FacilityDec</code> or we're done walking one,
     * this is set to <code>null</code>.</p>
     */
    protected FacilityEntry myCurrentFacility;

    /**
     * <p>A mapping between the actual parameters of a module and the
     * <code>SymbolTableEntry</code>s representing their formal counterparts.
     * Note: this <em>only</em> keeps track of one level of facility/enhancement.
     * That is, this map handles argument-to-formal parameter mappings for the
     * specification AND realization of whatever facility dec or enhancement we
     * are currently walking and is cleared as soon as the next is encountered
     * or the current facility ends.</p>
     */
    protected Map<String, SymbolTableEntry> myModuleFormalParameters =
            new HashMap<String, SymbolTableEntry>();

    public AbstractTranslator(CompileEnvironment environment,
            ModuleScope scope, ModuleDec dec) {
        myInstanceEnvironment = environment;
        myModuleScope = scope;
    }

    //-------------------------------------------------------------------
    //   Visitor methods
    //-------------------------------------------------------------------

    @Override
    public void preVariableExp(VariableExp node) {
        // VariableExps should be unqualified. So pass false.
        buildProgramExpArgument(node.toString(), node.getProgramType(), false);
    }

    @Override
    public void preProgramStringExp(ProgramStringExp node) {
        // The "value" of the node here is a string so we pass it as-is.
        buildProgramExpArgument(node.getValue(), node.getProgramType(), true);
    }

    @Override
    public void preProgramIntegerExp(ProgramIntegerExp node) {
        buildProgramExpArgument(node.toString(), node.getProgramType(), true);
    }

    @Override
    public void preProgramOpExp(ProgramOpExp node) {

        String errorMsg =
                "ProgramOpExp encountered!! This should have been converted to "
                        + "a programParamExp in preprocessing. Until a fix is introduced, "
                        + "if you must, write things like 'I+3', etc. as Sum(I, 3)";
        throw new SourceErrorException(errorMsg, node.getLocation());
    }

    /**
     * <p>Currently we aren't recognizing <p>ProgramParamExp</p> correctly. Sami took
     * it out temporarily until Blair and him can come up with an agree on a suitable
     * fix. For more information and status updates in the meantime, refer to pivotal
     * story #54742626</p>
     */
    @Override
    public void preProgramParamExp(ProgramParamExp node) {
        PTType type = node.getProgramType();
        String qualifier = getDefiningFacilityEntry(type).getName();

        myBookkeeper.fxnAppendTo(qualifier + myQualifierSymbol
                + node.getName().getName() + "(");
    }

    @Override
    public void midProgramParamExpArguments(ProgramParamExp node,
            ProgramExp previous, ProgramExp next) {

        if (next != null && previous != null) {
            myBookkeeper.fxnAppendTo(", ");
        }
    }

    @Override
    public void postProgramParamExp(ProgramParamExp node) {
        myBookkeeper.fxnAppendTo(")");
    }

    @Override
    public void midCallStmtArguments(CallStmt node, ProgramExp previous,
            ProgramExp next) {
        if (previous != null && next != null) {
            myBookkeeper.fxnAppendTo(", ");
        }
    }

    @Override
    public void postCallStmt(CallStmt node) {
        myBookkeeper.fxnAppendTo(");");
    }

    @Override
    public void preIfStmt(IfStmt node) {
        myBookkeeper.fxnAppendTo("if(");
    }

    @Override
    public void preIfStmtThenclause(IfStmt node) {
        myBookkeeper.fxnAppendTo(") {");
    }

    @Override
    public void postIfStmtThenclause(IfStmt node) {
        myBookkeeper.fxnAppendTo("}");
    }

    @Override
    public void preWhileStmt(WhileStmt node) {
        myBookkeeper.fxnAppendTo("while(");
    }

    @Override
    public void preWhileStmtStatements(WhileStmt node) {
        myBookkeeper.fxnAppendTo(") {");
    }

    @Override
    public void postWhileStmt(WhileStmt node) {
        myBookkeeper.fxnAppendTo("}");
    }

    @Override
    public void preTypeDec(TypeDec node) {
        myBookkeeper.addConstructor(node.getName().getName());
    }

    @Override
    public void preFacilityDec(FacilityDec node) {

        try {
            myCurrentFacility =
                    myModuleScope
                            .queryForOne(
                                    new NameAndEntryTypeQuery(
                                            null,
                                            node.getName(),
                                            FacilityEntry.class,
                                            MathSymbolTable.ImportStrategy.IMPORT_NAMED,
                                            MathSymbolTable.FacilityStrategy.FACILITY_IGNORE,
                                            false)).toFacilityEntry(
                                    node.getLocation());

            SpecRealizationPairing pair = myCurrentFacility.getFacility();
            ModuleParameterization specification = pair.getSpecification();
            ModuleParameterization realization = pair.getRealization();

            buildParameterBindings(node.getName(), specification, realization);
        }
        catch (NoneProvidedException npe) {
            // I think this should've already been caught..
        }
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(null, node.getName());
        }
        catch (DuplicateSymbolException dse) {
            throw new RuntimeException(dse);
        }

        myBookkeeper.facAdd(node.getName().getName(), node.getConceptName()
                .getName(), node.getBodyName().getName());
        AbstractTranslator.emitDebug("Entering FacilityDec");
    }

    @Override
    public void preEnhancementBodyItem(EnhancementBodyItem node) {
        myModuleFormalParameters.clear();

        for (ModuleParameterization m : myCurrentFacility.getEnhancements()) {

            if (m.getModuleIdentifier().toString().equals(
                    node.getName().getName())) {

                ModuleParameterization realization =
                        myCurrentFacility.getEnhancementRealization(m);
                buildParameterBindings(node.getName(), m, realization);
            }
        }
        myBookkeeper.facAddEnhancement(node.getName().getName(), node
                .getBodyName().getName());
        AbstractTranslator.emitDebug("Entering EnhancementBodyItem");
    }

    @Override
    public void postEnhancementBodyItem(EnhancementBodyItem node) {
        myBookkeeper.facEnhancementEnd();
        AbstractTranslator.emitDebug("Leaving EnhancementBodyItem");
    }

    @Override
    public void postFacilityDec(FacilityDec node) {
        myBookkeeper.facEnd();
        myModuleFormalParameters.clear();
        myCurrentFacility = null;
        AbstractTranslator.emitDebug("Leaving FacilityDec");
    }

    @Override
    public void preFacilityOperationDec(FacilityOperationDec node) {
        addOperationLikeThingToBookkeeper(node.getName().getName(), node
                .getReturnTy(), null);
        AbstractTranslator.emitDebug("Adding facility operation: "
                + node.getName().getName());
    }

    @Override
    public void preOperationDec(OperationDec node) {
        addOperationLikeThingToBookkeeper(node.getName().getName(), node
                .getReturnTy(), null);
        AbstractTranslator.emitDebug("Adding operation: "
                + node.getName().getName());
    }

    @Override
    public void preProcedureDec(ProcedureDec node) {
        throw new UnsupportedOperationException(node.getClass().getName()
                + " not yet supported");
    }

    @Override
    public void preVarDec(VarDec node) {

        String qual, specification;
        PTType type = node.getTy().getProgramTypeValue();

        if (type instanceof PTGeneric) {
            myBookkeeper.fxnAddVariableDeclaration("RType "
                    + node.getName().getName());
        }
        else {

            // Ignore preprocessor variables: "_Integer", etc.
            if (!node.getName().getName().startsWith("_")) {

                qual = getDefiningFacilityEntry(type).getName();
                specification =
                        getDefiningFacilityEntry(type).getFacility()
                                .getSpecification().getModuleIdentifier()
                                .toString();

                if (((NameTy) node.getTy()).getQualifier() != null) {
                    qual = ((NameTy) node.getTy()).getQualifier().toString();
                }

                myBookkeeper.fxnAddVariableDeclaration(specification
                        + myQualifierSymbol + type.toString() + " "
                        + node.getName().getName() + " = " + qual
                        + myQualifierSymbol + "create" + type.toString()
                        + "();");

                AbstractTranslator
                        .emitDebug("Adding variable: "
                                + node.getName().getName() + ", type: "
                                + type.toString() + ", specification: "
                                + specification);
            }
        }
    }

    @Override
    public void preParameterVarDec(ParameterVarDec node) {

        String qual, specification, name;
        PTType type = node.getTy().getProgramTypeValue();
        name = node.getName().getName();

        if (type instanceof PTGeneric) {
            myBookkeeper.fxnAddParameter("RType " + name);
        }
        else {

            // If we are unable to find a facility owning the type,
            // then the type should be defined locally, in which
            // case our qualifier is the name of the current module.

            // TODO        :        Figure out if this is actually sound.
            if (getDefiningFacilityEntry(type) == null) {
                specification = myModuleScope.getModuleIdentifier().toString();
            }
            else {
                specification =
                        getDefiningFacilityEntry(type).getFacility()
                                .getSpecification().getModuleIdentifier()
                                .toString();
            }

            if (((NameTy) node.getTy()).getQualifier() != null) {
                qual = ((NameTy) node.getTy()).getQualifier().toString();
            }

            myBookkeeper.fxnAddParameter(specification + myQualifierSymbol
                    + type.toString() + " " + name);
        }
    }

    //-------------------------------------------------------------------
    //   Helper methods
    //-------------------------------------------------------------------

    /**
     * <p>Given a PTType, <code>type</code>, this method finds the first facility
     * declared in <code>ModuleScope</code> that uses <code>type</code>s
     * originating module as its specification.</p>
     *
     * @param type A <code>PTType</code>.
     * @return The first <code>FacilityEntry</code> in scope whose
     *                   specification field matches <code>type</code>s
     *                   <code>SourceModuleIdentifier</code>.
     */
    protected FacilityEntry getDefiningFacilityEntry(PTType type) {

        FacilityEntry result = null;
        try {
            ProgramTypeEntry te =
                    myModuleScope.queryForOne(
                            new UnqualifiedNameQuery(type.toString()))
                            .toProgramTypeEntry(null);

            List<FacilityEntry> facilities =
                    myModuleScope.query(new EntryTypeQuery(FacilityEntry.class,
                            ImportStrategy.IMPORT_NAMED,
                            FacilityStrategy.FACILITY_IGNORE));

            for (FacilityEntry facility : facilities) {
                if (te.getSourceModuleIdentifier().equals(
                        facility.getFacility().getSpecification()
                                .getModuleIdentifier())) {
                    result = facility;
                }
            }
        }
        catch (NoSuchSymbolException nsse) {
            throw new RuntimeException("No local facility available for type!?");
        }
        catch (DuplicateSymbolException dse) {
            throw new RuntimeException(dse);
        }
        return result;
    }

    /**
     * <p>This builds a map between the actual parameters of a spec-realization
     * pairing and their formal counterparts. Here is an example spec-realization
     * pairing:<code>facility stack_fac is SPEC realized by REALIZATION</code>.</p>
     *
     * <p>This map takes any actual parameters to SPEC and REALIZATION, combines
     * them into a single list, then maps each to its corresponding formal parameter
     * located in the <code>ModuleScope</code>s belonging to SPEC and REALIZATION.</p>
     *
     * @param name The name of the facility or facility enhancement we are
     *             constructing the pairing map for.
     * @param spec The <code>ModuleParameterization</code> for the SPEC portion
     *             of the facility declaration (see example above).
     * @param realization The <code>ModuleParameterization</code> for the
     *                    realization portion of the facility declaration.
     */
    public void buildParameterBindings(PosSymbol name,
            ModuleParameterization spec, ModuleParameterization realization) {

        List<ModuleArgumentItem> args =
                new LinkedList<ModuleArgumentItem>(spec.getParameters());
        args.addAll(realization.getParameters());

        List<SymbolTableEntry> formalParams =
                new LinkedList<SymbolTableEntry>(spec.getScope(false)
                        .getFormalParameterEntries());
        formalParams.addAll(realization.getScope(false)
                .getFormalParameterEntries());

        int argIndex = 0;
        for (SymbolTableEntry entry : formalParams) {

            String argument;
            if (args.get(argIndex).getEvalExp() != null) {
                argument = args.get(argIndex).getEvalExp().toString();
            }
            else {
                argument = args.get(argIndex).getName().getName();
            }
            myModuleFormalParameters.put(argument, entry);
            argIndex++;
        }
    }

    /**
     * <p>This  builds <code>ProgramExp</code> arguments that are either qualified
     * or not, depending on the <code>fullyQualified</code> flag.</p>
     *
     * <p>For example, in <code>A[3]</code>, the <code>3</code> should come out
     * as <code>Std_Integer_Fac.createInteger(3)</code>. Whereas when we encounter
     * something like: <code>Increment(I)</code>, since <code>I</code> is a
     * <code>VariableExp</code>, it should remain unchanged.</p>
     *
     * @param value A string containing what should go between the parens
     *              in the <code>createTYPE( ... )</code> output.
     * @param type The <code>PTType</code> corresponding to the type we
     *             are instantiating.
     * @param fullyQualified If <code>true</code>, the argument will be fully
     *                       qualified. If <code>false</code>, skip qualification
     *                       and just put the value.
     *                       (E.g., for <code>VariableExp</code>s).
     */
    public void buildProgramExpArgument(String value, PTType type,
            boolean fullyQualified) {

        String expression = value;
        String qualifier = getDefiningFacilityEntry(type).getName();

        if (fullyQualified) {
            expression =
                    qualifier + myQualifierSymbol + "create" + type.toString()
                            + "(" + value + ")";
        }
        // Note: While ProgramExps DO get visited when instantiating facilities, etc.
        //                  This method was only intended to handle ProgramExp arguments for functions.
        //                 Handling these types of things for module parameters and facilities is
        //                 currently "preModuleArgumentItem"s job. I intend to experiment with this
        //                  division of labor further, and maybe eventually collapse the two.
        if (myBookkeeper.fxnIsOpen()) {
            myBookkeeper.fxnAppendTo(expression);
        }
    }

    /**
     * <p>Operations in facility modules look pretty much the same as those
     * in concepts, realizations, etc. So this method adds all things
     * operation/procedure related into the <code>Bookkeeper</code>.</p>
     *
     * @param name The name of the operation we're adding.
     * @param returnTy A possibly <code>null</code> <code>Ty</code> from the dec's
     *                 return type field.
     * @param returnStr If not <code>null</code>, this string takes precedence
     *                  over all others and will be used as the final, formed
     *                  return string. I.e., regardless of what the real return
     *                  value is, it will be set to whatever the user passes in
     *                  <code>returnStr</code>.
     */
    protected void addOperationLikeThingToBookkeeper(String name, Ty returnTy,
            String returnStr) {

        String formedReturnType = "void";
        PTType type;

        if (returnTy != null) {
            type = returnTy.getProgramTypeValue();

            // If the method has a generic return type
            if (type instanceof PTGeneric) {
                formedReturnType = "RTYPE";
            }

            // If we are unable to find a facility owning the type,
            // I.e., "getDefiningFacilityEntry" == null.
            // then the type should be defined locally, in which
            // case our qualifier is the name of the current module.
            // This occurs quite often in concept modules.
            else if (getDefiningFacilityEntry(type) == null) {
                formedReturnType =
                        myModuleScope.getModuleIdentifier().toString()
                                + myQualifierSymbol + type.toString();
            }
            // Else we are looking for a return type that is findable
            // in another module.
            else {
                formedReturnType =
                        getDefiningFacilityEntry(returnTy.getProgramTypeValue())
                                .getFacility().getSpecification()
                                .getModuleIdentifier().toString()
                                + myQualifierSymbol + type.toString();
            }
        }
        else if (returnStr != null) {
            formedReturnType = returnStr;
        }
        myBookkeeper.fxnAdd(name, formedReturnType);
    }

    protected String getIntendedCallQualifier(CallStmt node) {

        String qualifier = "";
        List<ProgramExp> args = node.getArguments();
        List<PTType> argTypes = new LinkedList<PTType>();
        List<FacilityEntry> matches = new LinkedList<FacilityEntry>();

        try {
            // First gather the call's arg types for an OperationQuery.
            for (ProgramExp arg : args) {
                argTypes.add(arg.getProgramType());
            }

            OperationEntry oe =
                    myModuleScope.queryForOne(
                            new OperationQuery(node.getQualifier(), node
                                    .getName(), argTypes)).toOperationEntry(
                            null);

            // Now grab any FacilityEntries in scope whose
            // specification matches oe's SourceModuleIdentifier.
            List<FacilityEntry> facilities =
                    myModuleScope.query(new EntryTypeQuery(FacilityEntry.class,
                            ImportStrategy.IMPORT_NAMED,
                            FacilityStrategy.FACILITY_IGNORE));

            for (FacilityEntry facility : facilities) {
                if (oe.getSourceModuleIdentifier().equals(
                        facility.getFacility().getSpecification()
                                .getModuleIdentifier())) {
                    matches.add(facility);
                }
            }

            // There should only be two cases:
            // 1. Size == 1 => a unique facility is instantiated
            //          in scope whose specification matches oe's. So the
            //          appropriate qualifier is that facility's name.
            if (matches.size() == 1) {
                qualifier = matches.get(0).getName();
            }
            // 2. Size > 1 => multiple facilities instantiated use
            //    oe's SourceModuleIdentifier as a specification.
            //          Which facility's name to use as a qualifier is
            //          ambiguous - so off to argument examination we go.
            if (matches.size() > 1) {
                qualifier = findQualifyingArgument(oe, args);
            }
            // 3. Size == 0 => the operation owning the call is
            //          defined locally. So no need to qualify at all.
        }
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(node.getQualifier(), node.getName().getName(), node
                    .getLocation());
        }
        catch (DuplicateSymbolException dse) {
            throw new RuntimeException(dse);
        }
        return qualifier;
    }

    private String findQualifyingArgument(OperationEntry operation,
            List<ProgramExp> arguments) throws SourceErrorException {

        String result = null;
        for (ProgramExp arg : arguments) {

            if (arg.getProgramType().getFacilityQualifier() != null) {

                try {
                    FacilityEntry fe =
                            myModuleScope.queryForOne(
                                    new UnqualifiedNameQuery(arg
                                            .getProgramType()
                                            .getFacilityQualifier()))
                                    .toFacilityEntry(null);

                    if (fe.getFacility().getSpecification()
                            .getModuleIdentifier().equals(
                                    operation.getSourceModuleIdentifier())) {
                        result = fe.getName();
                        break; // Leave - we've found a suitable qualifier.
                    }
                }
                catch (DuplicateSymbolException dse) {
                    throw new RuntimeException(dse);
                }
                catch (NoSuchSymbolException nsse) {
                    throw new RuntimeException(
                            "Couldn't find facility in moduleScope.");
                }
            }
            // Else a Qualifier is not explicitly defined for this instance of
            // current argument's type. This means all we have to do is find the
            // first facility whose specification matches the call-owning operation's
            // SourceModule, and use that facility's name as the qualifier.
            else {
                System.out.println("programt: "
                        + arg.getProgramType().toString());
                if (getDefiningFacilityEntry(arg.getProgramType())
                        .getFacility().getSpecification().getModuleIdentifier()
                        .equals(operation.getSourceModuleIdentifier())) {
                    result =
                            getDefiningFacilityEntry(arg.getProgramType())
                                    .getName();
                }
            }
        }
        if (result == null) {
            // TODO : sigh.
            //   throw new SourceErrorException("");
        }
        return result;
    }

    // -----------------------------------------------------------
    //   Error handling methods
    // -----------------------------------------------------------

    public void noSuchModule(PosSymbol qualifier) {
        throw new SourceErrorException(
                "Module does not exist or is not in scope.", qualifier);
    }

    public void ambiguousCall(PosSymbol symbol) {
        throw new SourceErrorException("Ambiguous call. Needs Qualification.",
                symbol);
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

    public void duplicateSymbol(PosSymbol symbol) {
        duplicateSymbol(symbol.getName(), symbol.getLocation());
    }

    public void duplicateSymbol(String symbol, Location l) {
        throw new SourceErrorException("Duplicate symbol: " + symbol, l);
    }

    // -----------------------------------------------------------
    //   Flag and output-related methods
    // -----------------------------------------------------------

    public static final void setUpFlags() {

    // TODO:         Check Prover to see the correct way to do this using
    //                         HwS's FlagDependencies system.
    }

    public static void emitDebug(String msg) {
        if (PRINT_DEBUG) {
            System.out.println(msg);
        }
    }

    public void outputCode(File outputFile) {
        if (!myInstanceEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_WEB)
                || myInstanceEnvironment.flags.isFlagSet(Archiver.FLAG_ARCHIVE)) {

            String code = CodeFormatter.formatCode(myBookkeeper.output());
            System.out.println(code);
        }
        else {
            outputToReport(myBookkeeper.output().toString());
        }
    }

    private void outputToReport(String fileContents) {
        CompileReport report = myInstanceEnvironment.getCompileReport();
        report.setTranslateSuccess();
        report.setOutput(fileContents);
    }
}
