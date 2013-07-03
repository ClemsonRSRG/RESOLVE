package edu.clemson.cs.r2jt.translation;

import java.io.File;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.StringTokenizer;

import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTableBuilder;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleScope;
import edu.clemson.cs.r2jt.compilereport.CompileReport;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerVisitor;
import edu.clemson.cs.r2jt.translation.bookkeeping.*;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.archiving.Archiver;
import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.absyn.*;

/**
 *
 * @author Welch D
 */
public class JavaTranslator extends TreeWalkerVisitor {

    private final ModuleScope myModuleScope;
    private final CompileEnvironment env;
    private Bookkeeper myBookkeeper;
    private ErrorHandler err;

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

        myModuleScope = scope;
        File srcFile = dec.getName().getFile();
    }

    /** Visitor Methods */

    // @Override
    /* public void preUsesItem(UsesItem data) {

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
     }*/

    public void preModuleDec(ModuleDec dec) {
        if (dec instanceof FacilityModuleDec) {
            String facName = dec.getName().toString();
            myBookkeeper = new JavaFacilityBookkeeper(facName, true);
        }
    }

    //Hidden. Calls can happen in preModuleDec
    /*@Override
    public void preFacilityModuleDec(FacilityModuleDec data) {
        String facName = data.getName().toString();
        myBookkeeper = new JavaFacilityBookkeeper(facName, true);
    }*/

    @Override
    public void preVarDec(VarDec dec) {

    //  try {
    //SymbolTableEntry entry =
    //  myBuilder.

    //getInnermostActiveScope().queryForOne(
    //       new NameQuery(null, dec.getName().getName()));

    // }
    /*  catch (NoSuchSymbolException nsse) {

      }
      catch (DuplicateSymbolException dse) {
          //Shouldn't be possible--NameQuery can't throw this
          throw new RuntimeException(dse);
      }*/
    //String progVarType =
    //        (dec.getTy().getProgramTypeValue()).toString();
    //SymbolTableEntry entry = myBuilder.getInnermostActiveScope().queryForOne(new NameQuery(null, progVarType,
    //                     ImportStrategy.IMPORT_RECURSIVE,
    //                     FacilityStrategy.FACILITY_INSTANTIATE, false));*/
    }

    /** Helper Methods */

    public void outputCode(File outputFile) {
        if (!env.flags.isFlagSet(ResolveCompiler.FLAG_WEB)
                || env.flags.isFlagSet(Archiver.FLAG_ARCHIVE)) {

            //   String code = Formatter.formatCode(myBookkeeper.output());
            //   System.out.println(code);
            System.out.println(myBookkeeper.output());
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