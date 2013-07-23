package edu.clemson.cs.r2jt.translation;

import edu.clemson.cs.r2jt.compilereport.CompileReport;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerVisitor;
import edu.clemson.cs.r2jt.translation.bookkeeping.*;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.archiving.Archiver;
import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.absyn.*;

import java.io.File;

public class JavaTranslator extends TreeWalkerVisitor {

    private final CompileEnvironment env;
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

    public JavaTranslator(CompileEnvironment env, ModuleDec dec,
            ErrorHandler err) {

        this.err = err;
        this.env = env;
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
    /*  String opName = node.getName().getName();
      String retType = "void";
      if (node.getReturnTy() != null) {
          retType = node.getReturnTy().toString();
          System.out.println("retType: " + retType);

      }
      myBookkeeper.fxnAdd(retType, opName);*/
    }

    @Override
    public void preCallStmt(CallStmt node) {

        if (node.getQualifier() != null) {
            System.out.println(node.getQualifier().toString() + "."
                    + node.getName());
        }
        else {
            System.out.println("Requires ADL record lookup.");
        }

        //   JavaTranslator.emitDebug("Encountered call: " + node.getName()
        //           + args.toString());
    }

    @Override
    public void preVarDec(VarDec dec) {
    //  String varName = dec.getName().getName();
    //  PTType varType = dec.getTy().getProgramTypeValue();

    // JavaTranslator.emitDebug("Translating variable: " + varName
    //         + " of type: " + varType + " \twith spec: " + typeSpec
    //         + " and qualifier: " + typeQual);
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

    private static void emitDebug(String msg) {
        if (PRINT_DEBUG) {
            System.out.println(msg);
        }
    }
}
