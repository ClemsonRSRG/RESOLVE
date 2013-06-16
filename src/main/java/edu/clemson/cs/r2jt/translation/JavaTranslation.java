/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.translation;

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
public class JavaTranslation extends TreeWalkerVisitor {

    // ===========================================================
    // Variables & Flags
    // ===========================================================

    private ErrorHandler err;
    private final CompileEnvironment env;
    JavaTranslationBookkeeper bookkeeper;

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

    public JavaTranslation(CompileEnvironment env, ModuleDec dec,
            ErrorHandler err) {
        this.err = err;
        this.env = env;
        File srcFile = dec.getName().getFile();
        bookkeeper = new JavaTranslationBookkeeper(err, srcFile);
    }

    // ===========================================================
    // TreeWalker Visitor Methods
    // ===========================================================

    @Override
    public void preUsesItem(UsesItem data) {

        // This is old code. Hopefully this is only temporary
        // and can be replaced by a more elegant approach?

        // I'm confused why we need two of these.. but apparently
        // we do since env.contains(id) fails to acknowledge
        // Location_Linking_template_1 & Static_Array_Template

        ModuleID id = ModuleID.createFacilityID(data.getName());
        ModuleID conId = ModuleID.createConceptID(data.getName());

        if (env.contains(id)) {
            ModuleDec dec = env.getModuleDec(id);
            FacilityDec fdec = ((ShortFacilityModuleDec) (dec)).getDec();
            PosSymbol cname = fdec.getConceptName();
            ModuleID cid = ModuleID.createConceptID(cname);

            bookkeeper.addImport(env.getFile(cid));
        }
        if (env.contains(conId)) {
            bookkeeper.addImport(env.getFile(conId));
        }
    }

	@Override
	public void preFacilityModuleDec(FacilityModuleDec data) {
		bookkeeper.appendToRest("\n");
        bookkeeper.appendToRest("class ");
           organizer.appendToRest(data.getName().toString());
           organizer.appendToRest(" {\n");
           // note: closing brace is done in the organizer's toString
       }*/

    @Override
    public void postFacilityModuleDec(FacilityModuleDec data) {
    // automate me
    /*  organizer.appendToRest("\n\t public ");
    	organizer.appendToRest(data.getName().toString());
    	organizer.appendToRest("() {}"); */
    }

    /*    @Override
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
     organizer.addFunction("", returnType, data.getName());*/
    //  }

    // ===========================================================
    // Misc Helper Methods
    // ===========================================================

    public String output() {
        return bookkeeper.toString();
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