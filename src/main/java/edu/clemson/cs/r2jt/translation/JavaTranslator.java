/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.translation;

import java.io.File;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.StringTokenizer;

import edu.clemson.cs.r2jt.typeandpopulate.FinalizedScope;

import edu.clemson.cs.r2jt.compilereport.CompileReport;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerVisitor;
import edu.clemson.cs.r2jt.translation.supervising.*;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.archiving.Archiver;
import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.data.*;

import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable;

/**
 *
 * @author Welch D
 */
public class JavaTranslator extends TreeWalkerVisitor {

    // ===========================================================
    // Variables & Flags
    // ===========================================================

    private ErrorHandler err;
    private final CompileEnvironment env;
    private final MathSymbolTable table;
    Supervisor mySupervisor;

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

    // ===========================================================
    // Constructor(s)
    // ===========================================================

    public JavaTranslator(CompileEnvironment env, MathSymbolTable tbl,
            ModuleDec dec, ErrorHandler err) {
        this.err = err;
        this.env = env;
        this.table = tbl;
        File srcFile = dec.getName().getFile();
    }

    // ===========================================================
    // TreeWalker Visitor Methods
    // ===========================================================

    // this preUsesItem method should change once a better sol. is 
    // found.
    @Override
    public void preUsesItem(UsesItem data) {

        ModuleID id = ModuleID.createFacilityID(data.getName());
        ModuleID conId = ModuleID.createConceptID(data.getName());

        if (env.contains(id)) {
            ModuleDec dec = env.getModuleDec(id);
            FacilityDec fdec = ((ShortFacilityModuleDec) (dec)).getDec();
            PosSymbol cname = fdec.getConceptName();
            ModuleID cid = ModuleID.createConceptID(cname);

            String imp = "import " + formPkgPath(env.getFile(cid)) + ".*;";
            mySupervisor.addUses(imp);
        }
        if (env.contains(conId)) {
            String imp = "import " + formPkgPath(env.getFile(conId)) + ".*;";
            mySupervisor.addUses(imp);
        }
    }

    @Override
    public void preFacilityModuleDec(FacilityModuleDec data) {
        String facName = data.getName().toString();
        mySupervisor = new JavaFacilitySupervisor(facName);
    }

    @Override
    public void preFacilityOperationDec(FacilityOperationDec data) {
        // PosSymbol retType = null;
        // System.out.println(data.getReturnTy().toString());
        // if (data.getReturnTy() != null) {
        //     System.out.println(data.getReturnTy().toString());
        //retType = ((NameTy) data.getReturnTy()).getName();
        // }
        mySupervisor.fxnAdd("void ", data.getName().toString());
    }

    @Override
    public void preParameterVarDec(ParameterVarDec dec) {
        String paramVariable = dec.getName().toString();
        mySupervisor.fxnAddParam(paramVariable);
    }

    @Override
    public void preVarDec(VarDec dec) {
        //PosSymbol varType = ((NameTy) dec.getTy()).getName();
        //PosSymbol varName = dec.getName();
        //FinalizedScope scope = table.getScope(dec);

        /*SymbolTableEntry test = table.getScope(dec).queryForOne(new UnqualifiedNameQuery(
                                        dec.getName().toString()));*/
        //   table.getScope(dec).query(
        //           new NameQuery(null, dec.getName().toString()));

        mySupervisor.fxnAddVarDecl(dec.getName().toString());
    }

    @Override
    public void preFuncAssignStmt(FuncAssignStmt data) {
        mySupervisor.fxnAppendTo("TEMP.");
        mySupervisor.fxnAppendTo("assign(");
        mySupervisor.fxnAppendTo(data.getVar().toString());
    }

    @Override
    public void midFuncAssignStmt(FuncAssignStmt stmt,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {
        if (prevChild != null && nextChild != null) {
            mySupervisor.fxnAppendTo(", ");
        }
    }

    @Override
    public void postFuncAssignStmt(FuncAssignStmt data) {
        mySupervisor.fxnAppendTo(");");
    }

    @Override
    public void preProgramParamExp(ProgramParamExp exp) {
        mySupervisor.fxnAppendTo("TEMP.");
        mySupervisor.fxnAppendTo(exp.toString());
        mySupervisor.fxnAppendTo("(");
    }

    @Override
    public void preProgramIntegerExp(ProgramIntegerExp exp) {
        mySupervisor.fxnAppendTo("Std_Integer_Fac.createInteger(");
        mySupervisor.fxnAppendTo(Integer.toString(exp.getValue()));
    }

    @Override
    public void postProgramIntegerExp(ProgramIntegerExp exp) {
        mySupervisor.fxnAppendTo(")");
    }

    @Override
    public void postProgramParamExp(ProgramParamExp exp) {
        mySupervisor.fxnAppendTo(")");
    }

    @Override
    public void preCallStmt(CallStmt stmt) {
        mySupervisor.fxnAppendTo(stmt.getName().toString());
        mySupervisor.fxnAppendTo("(");
    }

    @Override
    public void midCallStmtArguments(CallStmt node, ProgramExp previous,
            ProgramExp next) {
        if (next != null && previous != null) {
            mySupervisor.fxnAppendTo(", ");
        }
    }

    @Override
    public void postCallStmt(CallStmt stmt) {
        mySupervisor.fxnAppendTo(");");
    }

    // ===========================================================
    // Misc Helper Methods
    // ===========================================================

    public void outputCode(File outputFile) {
        if (!env.flags.isFlagSet(ResolveCompiler.FLAG_WEB)
                || env.flags.isFlagSet(Archiver.FLAG_ARCHIVE)) {
            System.out.println(mySupervisor.output());
        }
        else {
            outputToReport(mySupervisor.output().toString());
        }
    }

    private void outputToReport(String fileContents) {
        CompileReport report = env.getCompileReport();
        report.setTranslateSuccess();
        report.setOutput(fileContents);
    }

    public static final void setUpFlags() {}

    private String formatDoc(StringBuilder input) {

        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ';') {
                input.insert(i, "\n");
            }

        }
        return input.toString();
    }

    //This should only be temporary..
    private String formPkgPath(File file) {
        StringBuffer pkgPath = new StringBuffer();
        String filePath;
        if (file.exists()) {
            filePath = file.getAbsolutePath();
        }
        else {
            filePath = file.getParentFile().getAbsolutePath();
        }
        StringTokenizer stTok = new StringTokenizer(filePath, File.separator);
        Deque<String> tokenStack = new LinkedList<String>();

        String curToken;
        while (stTok.hasMoreTokens()) {
            curToken = stTok.nextToken();
            tokenStack.push(curToken);
        }

        //Get rid of the actual file -- we only care about the path to it
        if (file.isFile()) {
            tokenStack.pop();
        }

        curToken = "";
        boolean foundRootDirectory = false;
        while (!tokenStack.isEmpty() && !foundRootDirectory) {
            curToken = tokenStack.pop();

            if (pkgPath.length() != 0) {
                pkgPath.insert(0, '.');
            }

            pkgPath.insert(0, curToken);

            foundRootDirectory = curToken.equalsIgnoreCase("RESOLVE");
        }

        if (!foundRootDirectory) {
            err.error("Translation expects all compiled files to have a "
                    + "directory named 'RESOLVE' somewhere in their path, but "
                    + "the file:\n\t" + filePath + "\ndoes not.  Keep in mind "
                    + "that directories are case sensitive.");
        }
        return pkgPath.toString();
    }

    // This should also only be temporary..
    private String buildHeaderComment(File file) {
        String targetFileName = file.toString();

        String[] temp = targetFileName.split("\\\\");
        String fileName = temp[temp.length - 1];
        return "//\n" + "// Generated by the Resolve to Java Translator" + "\n"
                + "// from file:  " + fileName + "\n" + "// on:         "
                + new Date() + "\n" + "//\n";
    }

}