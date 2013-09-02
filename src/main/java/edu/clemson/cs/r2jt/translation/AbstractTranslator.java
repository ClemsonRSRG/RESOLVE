package edu.clemson.cs.r2jt.translation;

import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.archiving.Archiver;
import edu.clemson.cs.r2jt.compilereport.CompileReport;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.translation.bookkeeping.Bookkeeper;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerStackVisitor;
import edu.clemson.cs.r2jt.typeandpopulate.DuplicateSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.EntryTypeQuery;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleParameterization;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleScope;
import edu.clemson.cs.r2jt.typeandpopulate.NoSuchSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.NoneProvidedException;
import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.OperationEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramTypeEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTGeneric;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate.query.NameAndEntryTypeQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.OperationQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.UnqualifiedNameQuery;
import edu.clemson.cs.r2jt.utilities.SourceErrorException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Overrides translation specific visitor methods share-able 
 * between currently supported target languages (C & Java). Modify
 * as needed when/if new target languages are incorporated into the
 * project.
 * 
 * @author Markt, Welchd
 */
public abstract class AbstractTranslator extends TreeWalkerStackVisitor {

    /**
     * <p>If <code>true</code>, as we walk the tree, debug information 
     * will stream to the console. This should be <code>false</code> 
     * when the translator isn't actively being worked on.</p>
     */
    protected static final boolean PRINT_DEBUG = true;

    protected final CompileEnvironment myInstanceEnvironment;
    protected final ModuleScope myModuleScope;
    protected ErrorHandler err;

    /**
     * <p>This is where all information is ultimately stored. The 
     * <code>bookkeeper</code> (and it's concrete subclasses) handle 
     * how all information is collected, organized, and ultimately
     * arranged on the translated page via calls to concrete 
     * <code>getString</code> methods.</p>
     */
    protected Bookkeeper myBookkeeper;

    /**
     * <p>Indicates a language-specific qualification style.
     * For example, StackFac.Pop vs StackFac->Pop.</p>
     */
    protected String myQualifierSymbol;

    /**
     * <p>A pointer to the facility declaration currently undergoing
     * translation. Any enhancements accompanying this current facility 
     * declaration (if any) are enclosed within.</p>
     */
    protected FacilityEntry myCurrentFacility;

    /**
     * <p>A mapping between the actual parameters of a module and the
     * symbolTableEntries representing their formal counterparts.
     * Note that this maps parameters of <em>only</em> one level of
     * facility/facility enhancement. In other words, this map keeps 
     * track of only the argument-to-formal parameter mappings for 
     * whatever current facility/enhancement we might be walking
     * and is cleared as soon as the next is encountered or the  
     * current facility ends.</p>
     */
    protected Map<String, SymbolTableEntry> myModuleFormalParameters =
            new HashMap<String, SymbolTableEntry>();

    public AbstractTranslator(CompileEnvironment environment,
            ModuleScope scope, ModuleDec dec, ErrorHandler err) {
        this.err = err;
        myInstanceEnvironment = environment;
        myModuleScope = scope;
    }

    // -----------------------------------------------------------
    //   Visitor methods
    // -----------------------------------------------------------

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
    public void preFacilityDec(FacilityDec node) {

        try {
            myCurrentFacility =
                    myModuleScope.queryForOne(
                            new NameAndEntryTypeQuery(null, node.getName(),
                                    FacilityEntry.class,
                                    ImportStrategy.IMPORT_NAMED,
                                    FacilityStrategy.FACILITY_IGNORE, false))
                            .toFacilityEntry(node.getLocation());

            // First add any arguments for the specification.
            List<ModuleArgumentItem> args =
                    new LinkedList<ModuleArgumentItem>(myCurrentFacility
                            .getFacility().getSpecification().getParameters());

            List<SymbolTableEntry> formalParams =
                    new LinkedList<SymbolTableEntry>(myCurrentFacility
                            .getFacility().getSpecification().getScope(false)
                            .getFormalParameterEntries());

            try {
                // Add any arguments and formal parameters from the realization now.
                args.addAll(myCurrentFacility.getFacility().getRealization()
                        .getParameters());

                formalParams.addAll(myCurrentFacility.getFacility()
                        .getRealization().getScope(false)
                        .getFormalParameterEntries());
            }
            catch (NoneProvidedException npe) {
                // This shouldn't happen with a base facility
            }

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

                // Get any arguments for the enhancement specification.
                List<ModuleArgumentItem> moduleArgs =
                        new LinkedList<ModuleArgumentItem>(m.getParameters());

                // Now get formal parameters to the enhancement specification.
                List<SymbolTableEntry> moduleFormalParams =
                        new LinkedList<SymbolTableEntry>(m.getScope(false)
                                .getFormalParameterEntries());

                System.out.println("formal parameter size: "
                        + moduleFormalParams.size());

                for (SymbolTableEntry e : moduleFormalParams) {
                    System.out.println("FORMAL PARAM: " + e.getName());
                }
                // Combine arguments-to and formal parameters of the 
                // specification
                moduleArgs.addAll(myCurrentFacility
                        .getEnhancementRealization(m).getParameters());

                moduleFormalParams.addAll(myCurrentFacility
                        .getEnhancementRealization(m).getScope(false)
                        .getFormalParameterEntries());

                int argIndex = 0;
                for (SymbolTableEntry entry : moduleFormalParams) {
                    String argument;
                    if (moduleArgs.get(argIndex).getEvalExp() != null) {
                        argument =
                                moduleArgs.get(argIndex).getEvalExp()
                                        .toString();
                    }
                    else {
                        argument = moduleArgs.get(argIndex).getName().getName();
                    }
                    myModuleFormalParameters.put(argument, entry);
                    argIndex++;
                }
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

    // TODO : Maybe add a private operation that handles:
    //		  preFacilityOperationDec, preProcedureDec, and 
    //		  preOperationDec.

    @Override
    public void preFacilityOperationDec(FacilityOperationDec node) {

        String returnType = "void";

        if (node.getReturnTy() != null) {

            returnType =
                    getDefiningFacilityEntry(
                            node.getReturnTy().getProgramTypeValue())
                            .getFacility().getSpecification()
                            .getModuleIdentifier().toString()
                            + myQualifierSymbol
                            + node.getReturnTy().getProgramTypeValue()
                                    .toString();
        }

        AbstractTranslator.emitDebug("Adding operation/procedure: <name: '"
                + node.getName().getName() + "', return type: '" + returnType
                + "'>");
        myBookkeeper.fxnAdd(node.getName().getName(), returnType);
    }

    @Override
    public void preOperationDec(OperationDec node) {
        throw new UnsupportedOperationException(node.getClass().getName()
                + " not yet supported");
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

                AbstractTranslator.emitDebug("Adding variable: <name: '"
                        + node.getName().getName() + "', type: '"
                        + type.toString() + "', specification: '"
                        + specification + "'>");
            }
        }
    }

    @Override
    public void preParameterVarDec(ParameterVarDec node) {
    //     throw new UnsupportedOperationException(node.getClass().getName()
    //             + " not yet supported");
    }

    // -----------------------------------------------------------
    //   Helper methods
    // -----------------------------------------------------------

    /**
     * Given a PTType, <code>type</code>, this method finds the first 
     * facility declared in ModuleScope that uses <code>type</code>s 
     * defining module as its specification.
     * 
     * @param type A PTType.
     * @return The first <code>FacilityEntry</code> in scope whose 
     *		   specification field matches <code>type</code>s
     *		   <code>SourceModuleIdentifier</code>.
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
            //	  in scope whose specification matches oe's. So the 
            //	  appropriate qualifier is that facility's name.
            if (matches.size() == 1) {
                qualifier = matches.get(0).getName();
            }
            // 2. Size > 1 => multiple facilities instantiated use 
            //    oe's SourceModuleIdentifier as a specification.
            //	  Which facility's name to use as a qualifier is 
            //	  ambiguous - so off to argument examination we go.
            if (matches.size() > 1) {
                qualifier = findQualifyingArgument(oe, args);
            }
            // 3. Size == 0 => the operation owning the call is 
            //	  defined locally. So no need to qualify at all.
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

            if (arg.getProgramType().getQualifier() != null) {

                try {
                    FacilityEntry fe =
                            myModuleScope.queryForOne(
                                    new UnqualifiedNameQuery(arg
                                            .getProgramType().getQualifier()))
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
            // Qualifier not explicitly defined for this instance of
            // current argument's type. This means all we have to do
            // is find the first facility whose specification matches 
            // the call-owning operation's SourceModule and use that 
            // facility's name as the qualifier.
            else {
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
            throw new SourceErrorException();
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

    public static final void setUpFlags() {}

    public static void emitDebug(String msg) {
        if (PRINT_DEBUG) {
            System.out.println(msg);
        }
    }

    public void outputCode(File outputFile) {
        if (!myInstanceEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_WEB)
                || myInstanceEnvironment.flags.isFlagSet(Archiver.FLAG_ARCHIVE)) {

            String code = Formatter.formatCode(myBookkeeper.output());
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
