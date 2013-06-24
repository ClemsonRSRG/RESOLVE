/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.translation;

import edu.clemson.cs.r2jt.translation.bookkeeping.Bookkeeper;
import edu.clemson.cs.r2jt.translation.bookkeeping.JavaConceptBookkeeper;
import edu.clemson.cs.r2jt.translation.bookkeeping.JavaFacilityBookkeeper;
import java.io.File;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.StringTokenizer;

import edu.clemson.cs.r2jt.typeandpopulate.FinalizedScope;

import edu.clemson.cs.r2jt.compilereport.CompileReport;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerVisitor;
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

    private ErrorHandler err;
    private final CompileEnvironment env;
    private final MathSymbolTable table; // get rid of this. Work w/ scopes instead.
    Bookkeeper myBookkeeper;

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

    public JavaTranslator(CompileEnvironment env, MathSymbolTable tbl,
            ModuleDec dec, ErrorHandler err) {
        this.err = err;
        this.env = env;
        this.table = tbl;
        File srcFile = dec.getName().getFile();
    }

    /** Visitor Methods */

    @Override
    public void preUsesItem(UsesItem data) {

        ModuleID id = ModuleID.createFacilityID(data.getName());
        if (env.contains(id)) {

            ModuleDec dec = env.getModuleDec(id);
            if (dec instanceof ShortFacilityModuleDec) {

                FacilityDec fdec = ((ShortFacilityModuleDec) (dec)).getDec();
                PosSymbol cname = fdec.getConceptName();
                ModuleID cid = ModuleID.createConceptID(cname);
                String imp = "import " + formPkgPath(env.getFile(cid)) + ".*;";
                myBookkeeper.addUses(imp);
            }
        }
        ModuleID cid = ModuleID.createConceptID(data.getName());
        if (env.contains(cid)) {
            String imp = "import " + formPkgPath(env.getFile(cid)) + ".*;";
            myBookkeeper.addUses(imp);
        }
    }

    @Override
    public void preConceptModuleDec(ConceptModuleDec data) {
        String conceptName = data.getName().toString();
        myBookkeeper = new JavaConceptBookkeeper(conceptName, false);
    }

    @Override
    public void preTypeDec(TypeDec data) {
    //	myBookkeeper.addConceptConstructor(data.getName().toString(),"");
    // System.out.println(data.getName().toString());
    }

    @Override
    public void preOperationDec(OperationDec data) {
        String retType = "void";
        if (data.getReturnTy() != null) {
            retType = "<ReturnType>"; // obviously a placeholder.
        }
        myBookkeeper.fxnAdd(retType, data.getName().toString());
    }

    @Override
    public void preFacilityModuleDec(FacilityModuleDec data) {
        String facName = data.getName().toString();
        myBookkeeper = new JavaFacilityBookkeeper(facName, true);
    }

    @Override
    public void preFacilityOperationDec(FacilityOperationDec data) {
        myBookkeeper.fxnAdd("void", data.getName().toString());
    }

    @Override
    public void preParameterVarDec(ParameterVarDec dec) {
        myBookkeeper.fxnAddParam(dec.getName().toString());
    }

    @Override
    public void preVarDec(VarDec dec) {
        myBookkeeper.fxnAddVarDecl(dec.getName().toString());
    }

    ////////////////

    @Override
    public void preFuncAssignStmt(FuncAssignStmt data) {
        myBookkeeper.fxnAppendTo("assign(");
        myBookkeeper.fxnAppendTo(data.getVar().toString());
    }

    @Override
    public void midFuncAssignStmt(FuncAssignStmt stmt,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {
        if (prevChild != null && nextChild != null) {
            myBookkeeper.fxnAppendTo(", ");
        }
    }

    @Override
    public void postFuncAssignStmt(FuncAssignStmt data) {
        myBookkeeper.fxnAppendTo(");");
    }

    @Override
    public void preProgramParamExp(ProgramParamExp exp) {
        myBookkeeper.fxnAppendTo(exp.toString());
        myBookkeeper.fxnAppendTo("(");
    }

    @Override
    public void preProgramIntegerExp(ProgramIntegerExp exp) {
        myBookkeeper.fxnAppendTo("Std_Integer_Fac.createInteger(");
        myBookkeeper.fxnAppendTo(Integer.toString(exp.getValue()));
    }

    @Override
    public void postProgramIntegerExp(ProgramIntegerExp exp) {
        myBookkeeper.fxnAppendTo(")");
    }

    @Override
    public void postProgramParamExp(ProgramParamExp exp) {
        myBookkeeper.fxnAppendTo(")");
    }

    @Override
    public void preCallStmt(CallStmt stmt) {
        myBookkeeper.fxnAppendTo(stmt.getName().toString());
        myBookkeeper.fxnAppendTo("(");
    }

    @Override
    public void midCallStmtArguments(CallStmt node, ProgramExp previous,
            ProgramExp next) {
        if (next != null && previous != null) {
            myBookkeeper.fxnAppendTo(", ");
        }
    }

    @Override
    public void postCallStmt(CallStmt stmt) {
        myBookkeeper.fxnAppendTo(");");
    }

    /** Helper Methods */

    public void outputCode(File outputFile) {
        if (!env.flags.isFlagSet(ResolveCompiler.FLAG_WEB)
                || env.flags.isFlagSet(Archiver.FLAG_ARCHIVE)) {

            String code = Formatter.formatCode(myBookkeeper.output());
            System.out.println(code);
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
        return "//\n// Generated by the Resolve to Java Translator\n"
                + "// from file:  " + fileName + "\n// on:         "
                + new Date() + "\n" + "//\n";
    }

}