package edu.clemson.cs.r2jt.translation;

import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramTypeEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.OperationEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate.query.OperationQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.NameQuery;
import edu.clemson.cs.r2jt.typeandpopulate.*;

import edu.clemson.cs.r2jt.compilereport.CompileReport;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerVisitor;
import edu.clemson.cs.r2jt.translation.bookkeeping.*;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.archiving.Archiver;
import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramQualifiedEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.RepresentationTypeEntry;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTRepresentation;
import edu.clemson.cs.r2jt.utilities.SourceErrorException;

import java.util.LinkedList;
import java.util.List;
import java.io.File;

public class JavaTranslator extends TreeWalkerVisitor {

    private final CompileEnvironment env;
    private final ModuleScope myScope;
    private Bookkeeper myBookkeeper;
    private ErrorHandler err;

    private static final boolean PRINT_DEBUG = true;
    private static final String FLAG_SECTION_NAME = "Translation";
    private static final String FLAG_DESC_TRANSLATE =
            "Translate RESOLVE file to Java source file.";
    private static final String FLAG_DESC_TRANSLATE_CLEAN =
            "Regenerates Java code for all supporting RESOLVE files.";

    public static final Flag JAVA_FLAG_TRANSLATE =
            new Flag(FLAG_SECTION_NAME, "javaTranslate", FLAG_DESC_TRANSLATE);

    public static final Flag FLAG_TRANSLATE_CLEAN =
            new Flag(FLAG_SECTION_NAME, "translateClean",
                    FLAG_DESC_TRANSLATE_CLEAN);

    public JavaTranslator(CompileEnvironment env, ModuleScope scope,
            ModuleDec dec, ErrorHandler err) {

        this.err = err;
        this.env = env;
        myScope = scope;
    }

    //-------------------------------------------------------------------
    //   Visitor methods
    //-------------------------------------------------------------------

    @Override
    public void preModuleDec(ModuleDec node) {
        JavaTranslator.emitDebug("---------------------------------\n"
                + "Translate module: " + node.getName().getName()
                + "\n---------------------------------");
        if (node instanceof FacilityModuleDec) {
            String facName = node.getName().toString();
            myBookkeeper = new JavaFacilityBookkeeper(facName, true);
        }
    }

    @Override
    public void preOperationDec(OperationDec node) {
        String opName = node.getName().getName();
        String retType = "void";
        if (node.getReturnTy() != null) {
            retType = node.getReturnTy().toString();
            System.out.println("retType: " + retType);

        }
        myBookkeeper.fxnAdd(retType, opName);
    }

    @Override
    public void preCallStmt(CallStmt node) {

        String callQualifier;
        String callSrcModule;

        List<ProgramExp> args = node.getArguments();

        JavaTranslator.emitDebug("Encountered call: " + node.getName()
                + args.toString());

        if (node.getQualifier() == null) {
            callQualifier = getCallFacility(node.getName(), args);
        }
    }

    @Override
    public void preVarDec(VarDec dec) {
        String varName = dec.getName().getName();
        PTType varType = dec.getTy().getProgramTypeValue();

        String typeSpec = getTypeSpecification((NameTy) dec.getTy());
        String typeQual = getTypeFacility(typeSpec);
        //	String typeQual = ((NameTy) dec.getTy()).getQualifier().toString();
        //	if (typeQual == null) {
        //		typeQual = getTypeQualifier(typeSpec);
        //	}

        JavaTranslator.emitDebug("Translating variable: " + varName
                + " of type: " + varType + " \twith spec: " + typeSpec
                + " and qualifier: " + typeQual);
    }

    //-------------------------------------------------------------------
    //   Helper methods
    //-------------------------------------------------------------------

    public void outputCode(File outputFile) {
        if (!env.flags.isFlagSet(ResolveCompiler.FLAG_WEB)
                || env.flags.isFlagSet(Archiver.FLAG_ARCHIVE)) {

            //   String code = Formatter.formatCode(myBookkeeper.output());
            //   System.out.println(code);
            // System.out.println(myBookkeeper.output());
        }
        else {
            outputToReport(myBookkeeper.output().toString());
        }
    }

    private void outputToReport(String fileContents) {
        CompileReport report = env.getCompileReport();
        report.setTranslateSuccess();
        report.setOutput(fileContents);
    }

    public static final void setUpFlags() {}

    public static void emitDebug(String msg) {
        if (PRINT_DEBUG) {
            System.out.println(msg);
        }
    }

    /**
     * Given NameTy <code>t</code>, <code>getTypeSpecification</code> 
     * finds and returns the name of the conceptual module that 
     * defines <code>t</code>.
     * 
     * @param t A NameTy.
     * @return A string containing <code>t</code>'s specification.
     */
    private String getTypeSpecification(NameTy t) {

        String result;
        try {
            ProgramTypeEntry type =
                    myScope
                            .queryForOne(
                                    new NameQuery(
                                            null,
                                            t.getName(),
                                            ImportStrategy.IMPORT_NAMED,
                                            MathSymbolTable.FacilityStrategy.FACILITY_INSTANTIATE,
                                            true)).toProgramTypeEntry(
                                    t.getLocation());
            result = type.getSourceModuleIdentifier().toString();

        }
        catch (NoSuchSymbolException nsse) {
            throw new RuntimeException(
                    "No specification module found for type " + t.toString());
        }
        catch (DuplicateSymbolException dse) {
            //Shouldn't be possible--NameQuery can't throw this
            throw new RuntimeException(dse);
        }
        return result;
    }

    /**
     * Given a string <code>s</code> containing a specification,
     * <code>getTypeFacility</code> returns the name of first 
     * facility whose specification matches <code>s</code>.
     * 
     * @param s A string containing a specification module-name.
     * 
     * @return A string containing the name of the first instantiated 
     *		   facility found w/ a spec matching <code>s</code>.
     */
    private String getTypeFacility(String s) {

        String result = "";
        String facSpec;

        List<FacilityEntry> facilities =
                myScope.query(new EntryTypeQuery(FacilityEntry.class,
                        ImportStrategy.IMPORT_NAMED,
                        MathSymbolTable.FacilityStrategy.FACILITY_IGNORE));

        for (FacilityEntry f : facilities) {
            facSpec =
                    f.getFacility().getSpecification().getModuleIdentifier()
                            .toString();
            if (s.equals(facSpec)) {
                result = f.getName();
            }
        }
        if (result.equals("")) {
            throw new RuntimeException(
                    "No matching facility found for specification: " + s);
        }
        return result;
    }

    /**
     * Given a PosSymbol <code>n</code> containing a callStmt's name,
     * <code>getCallFacility</code> searches through <code>n</code>'s
     * arguments, <code>args</code>, and returns the facility qualifier
     * for the call.
     * 
     * @param n A PosSymbol containing the calls name.
     * @param args The calls argument list.
     * 
     * @return A string containing the name of the call-qualifying 
     *		   facility.
     */

    private String getCallFacility(PosSymbol n, List<ProgramExp> args) {

        String resultQual;
        String callSrcModule;
        String curModuleName;

        List<PTType> argTypes = new LinkedList<PTType>();

        for (ProgramExp arg : args) {
            argTypes.add(arg.getProgramType());
        }

        try {
            // Find the module that declares the call-owning op
            OperationEntry matchingOp =
                    myScope.queryForOne(new OperationQuery(null, n, argTypes));

            callSrcModule = matchingOp.getSourceModuleIdentifier().toString();
            curModuleName = myScope.getModuleIdentifier().toString();
            // If the call's corresponding operation isn't defined 
            // locally then we have work to do. Otherwise stop.
            if (!(callSrcModule.equals(curModuleName))) {

                // If args is empty and the call-owning op isn't
                // defined in local namespace, then error.
                if (args.isEmpty()) {
                    throw new SourceErrorException(
                            "Ambiguous call. Needs qualification", n
                                    .getLocation());
                }

                // These are any parameters to the fxn housing the call.
                // example :  Oper Foo(<housing params>) {
                //				Bar(x, y, z);
                //			  }
                //	myScope.qu
                // for (ProgramParameterEntry p : matchingOp.getParameters()) {
                //     System.out.println(p.getName());
                // }
                // checkCallRecordArgs(args, houseParams, callSrcModule);
            }

        }
        catch (NoSuchSymbolException nsse) {
            System.out.println("No operation found in scope or elsewhere");
            // TODO : Error Properly.
            // noSuchSymbol(data.getQualifier(), data.getName(), data.getLocation());
        }
        catch (DuplicateSymbolException dse) {
            throw new RuntimeException(dse);
        }

        return "";

    }

    private void checkCallRecordArgs(List<ProgramExp> args,
            List<ProgramParameterEntry> houseParams, String callSrcModule) {

    /*   for (ProgramExp arg : args) {

           // All arguments from records must be VariableDotExps
           if (arg instanceof VariableDotExp) {
               System.out.println("arg: " + arg.toString());

            //   for (ProgramParameterEntry par : houseParams) {

                   // System.out.println("before rep check: " + par.getName()
                   //         + "  declared type: " + par.getDeclaredType());

                   if (par.getDeclaredType() instanceof PTRepresentation) {
                       if (((VariableDotExp) arg).getSegments().get(0)
                               .toString().equals(par.getName())) {

                           ResolveConceptualElement de =
                                   par.getDefiningElement();

                           if (de instanceof ParameterVarDec) {
                               NameTy repNameTy =
                                       ((NameTy) ((ParameterVarDec) de)
                                               .getTy());
                               String argSpec =
                                       getTypeSpecification(repNameTy);
                               //      System.out.println("record parameter "
                               //              + repNameTy.getName().getName()
                               //              + " with spec: " + argSpec);

                               //   ProgramQualifiedEntry pqe =
                               //           findRepAndGetFieldEntry(arg, repName);
                               //   if (pqe.getSpecification()
                               //           .equals(callSrcModule)) {
                               //       return pqe.getQualifier();
                           }
                       }
                   }
               }
           }
       }*/
    // if we get through the arglist w/o returning then
    // we clearly didn't find a winner and should look at
    // the rest of the non-record args.
    //return null;
    }

    //private ProgramQualifiedEntry findRepAndGetFieldEntry(ProgramExp arg,
    //         PosSymbol repName) {

    private void findRepAndGetFieldEntry(ProgramExp arg, PosSymbol repName) {

        ProgramQualifiedEntry result;
        try {
            RepresentationTypeEntry rte =
                    myScope
                            .queryForOne(
                                    new NameQuery(
                                            null,
                                            repName,
                                            ImportStrategy.IMPORT_NAMED,
                                            MathSymbolTable.FacilityStrategy.FACILITY_INSTANTIATE,
                                            true)).toRepresentationTypeEntry(
                                    null);

            // Not sure if searching around in a shut working-scope is 
            // entirely kosher. It better be since 1: The functionality
            // is there, and 2: This is the only clean, concievable way 
            // I see to access record-field information once the 
            // representation scope has been passed by in the tree.

            ResolveConceptualElement r = rte.getDefiningElement();

            String fieldStr =
                    ((VariableDotExp) arg).getSegments().get(1).toString();
        }
        catch (NoSuchSymbolException nsse) {
            System.out.println("Can't find representation by that name...");
            throw new RuntimeException(nsse);

        }
        catch (DuplicateSymbolException dse) {
            throw new RuntimeException(dse);
        }
        // return result;
    }
}
