/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.translation;

import java.util.StringTokenizer;
import java.util.LinkedList;
import java.util.Deque;
import java.util.Date;
import java.io.File;

import edu.clemson.cs.r2jt.compilereport.CompileReport;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerVisitor;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.archiving.Archiver;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.data.*;

/**
 *
 * @author Welch D
 */
public class JavaTranslator extends TreeWalkerVisitor {

    // ===========================================================
    // Variables & Flags
    // ===========================================================

    private ErrorHandler err;
    private StringBuffer allStmt;
    private final CompileEnvironment env;
    JavaTranslationOrganizer organizer;

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

    public JavaTranslator(CompileEnvironment env, ModuleDec dec,
            ErrorHandler err) {
        this.err = err;
        this.env = env;
        organizer = new JavaTranslationOrganizer();
    }

    // ===========================================================
    // TreeWalker Visitor Methods
    // ===========================================================

    /**
     *
     * Visit methods 
     */

    @Override
    public void preUsesItem(UsesItem data) {
        // todo: prepend each usesitem w/ a pkg path string
        // when a clean solution for retrieval is found

        // note: preUsesItem also imports theory files used 
        // by the module. I doubt we want to print these. What 
        // is the proper way to go about filtering them out?

        organizer.appendToHeader("import <pkg.path> ");
        organizer.appendToHeader(data.getName());
        organizer.appendToHeader(".*;\n");
    }

    @Override
    public void preModuleDec(ModuleDec dec) {

        // The organizer's vague-sounding "header" string consists of:
        // 1. The decorative resolve-compiler message/comment block
        // 2. The package containing the current file
        // 3. The standard import packages

        // The organizer's "rest" string is everything after
        // (and including) the outermost "public ... { ... }"

        File srcFile = dec.getName().getFile();
        organizer.appendToHeader(buildHeaderComment(dec));
        organizer.appendToHeader("package ");
        organizer.appendToHeader(formPkgPath(srcFile));
        organizer.appendToHeader(";\n\n");
        organizer.appendToHeader("import RESOLVE.*;\n");
    }

    @Override
    public void preFacilityModuleDec(FacilityModuleDec data) {
        organizer.appendToRest("\n");
        organizer.appendToRest("class ");
        organizer.appendToRest(data.getName().toString());
        organizer.appendToRest(" {\n");
        // note: closing brace is done in the organizer's toString
    }

    @Override
    public void postFacilityModuleDec(FacilityModuleDec data) {
    // automate me
    /*  organizer.appendToRest("\n\t public ");
    	organizer.appendToRest(data.getName().toString());
    	organizer.appendToRest("() {}"); */
    }

    @Override
    public void preFacilityOperationDec(FacilityOperationDec data) {
        PosSymbol returnType = null;
        if (data.getReturnTy() != null) {
            returnType = ((NameTy) data.getReturnTy()).getName();
        }

        organizer.addFunction("public ", returnType, data.getName());
    }

    @Override
    public void preParameterVarDec(ParameterVarDec dec) {

        PosSymbol varType = ((NameTy) dec.getTy()).getName();
        PosSymbol varName = dec.getName();

        String typeAndVarName = organizer.formParameter(varType, varName);
        organizer.appendToCurrParamList(typeAndVarName);

    }

    @Override
    public void preVarDec(VarDec dec) {
        System.out.println(dec.getName().toString());
    }

    @Override
    public void postFacilityOperationDec(FacilityOperationDec data) {}

    @Override
    public void preOperationDec(OperationDec data) {
        PosSymbol returnType = null;
        if (data.getReturnTy() != null) {
            returnType = ((NameTy) data.getReturnTy()).getName();
        }
        organizer.addFunction("", returnType, data.getName());
    }

    // ===========================================================
    // Misc Helper Methods
    // ===========================================================

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

        //Get rid of the actual file--we only care about the path to it
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

    private String buildHeaderComment(ModuleDec dec) {

        String targetFileName = dec.getName().getFile().toString();

        String[] temp = targetFileName.split("\\\\");
        String fileName = temp[temp.length - 1];
        return "//\n" + "// Generated by the Resolve to Java Translator" + "\n"
                + "// from file:  " + fileName + "\n" + "// on:         "
                + new Date() + "\n" + "//\n";
    }

    public String output() {
        return organizer.toString();
    }

    public void outputCode(File outputFile) {

        if (!env.flags.isFlagSet(ResolveCompiler.FLAG_WEB)
                || env.flags.isFlagSet(Archiver.FLAG_ARCHIVE)) {
            System.out.println(output());
        }
        else {
            outputToReport(output());
        }
    }

    private void outputToReport(String fileContents) {
        CompileReport report = env.getCompileReport();
        report.setTranslateSuccess();
        report.setOutput(fileContents);
    }

    public static final void setUpFlags() {

    }

}