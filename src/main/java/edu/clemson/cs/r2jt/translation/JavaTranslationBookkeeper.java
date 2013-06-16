package edu.clemson.cs.r2jt.translation;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Deque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.StringTokenizer;

import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.errors.ErrorHandler;

/**
 * Collects raw data obtained from TreeWalker vists and 
 * outputs a formatted string of compile-able Java code
 * 
 * @author Welch D
 */
public class JavaTranslationBookkeeper {

    // scopes
    private ErrorHandler err;

    // everything will eventually be written to this file
    private StringBuilder currentDocument;

    // current resolve file to translate
    private File srcFile;

    // containers
    private ArrayList<String> importList;

    //private ArrayList<String> globalVarList;

    // ===========================================================
    // Constructor(s)
    // ===========================================================

    public JavaTranslationBookkeeper(ErrorHandler err, File doc) {
        this.err = err;
        this.srcFile = doc;
        importList = new ArrayList();
        currentDocument = new StringBuilder();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // The file being passed here isn't the same srcFile
    // in the constructor, but one for a usesitem!

    // It's admittedly weird reading a file here but it seems
    // like the cleanest place to do it until we find a better
    // solution for retrieving import items
    public void addImport(File file) {
        String impStr = "import " + formPkgPath(file) + ".*;\n";
        if (!importList.contains(impStr)) {
            importList.add(impStr);
        }
    }
	
	public void appendToCurrentDoc(String line) {
		currentDocument.append(line);
	}

    @Override
    public String toString() {
        currentDocument.append(buildHeaderComment(srcFile));
        currentDocument.append("package ");
        currentDocument.append(formPkgPath(srcFile));
        currentDocument.append("\n\n");
        currentDocument.append("import RESOLVE.*\n");
        for (String imp : importList) {
            currentDocument.append(imp);
        }
		currentDocument.append("\n");
        return currentDocument.toString();
    }


    // ===========================================================
    // Element Classes
    // ===========================================================
	

  /*  private class ElementFunction {

        StringBuffer functionBody;
        String retTyandName;
        String returnType;

        List<String> parameters;
        List<String> statements;
        List<String> initializedVariables;
    }*/

    /*   private class ElementFunction {

           StringBuffer functionBody;
           String retTyandName;
           String returnType;

           List<String> parameters;
           List<String> statements;
           List<String> varInit;
       }

       public void addFunction(String modifier, PosSymbol retType, PosSymbol name) {
           ElementFunction newFunc = new ElementFunction();

           newFunc.returnType = "void ";
           if (retType != null) {
               newFunc.returnType = retType.getName().toString();
           }

           newFunc.retTyandName = modifier + newFunc.returnType + name.toString();
           funcList.add(newFunc);

           newFunc.functionBody = new StringBuffer();
           newFunc.parameters = new ArrayList<String>();
           newFunc.statements = new ArrayList<String>();
           newFunc.varInit = new ArrayList<String>();
           currentFunction = newFunc;
       }*/

    /*   public void addToCurrFunction(String line) {
           currentFunction.functionBody.append(line);
       }

       public void addToCurrParamList(String line) {
           // if (currentFunction != null) { }
           currentFunction.parameters.add(line);
       }

       public void appendToHeader(String line) {
           header.append(line);
       }

       public void appendToRest(String line) {
           rest.append(line);
       }*/

    //   public String formParameter(PosSymbol type, PosSymbol par) {
    // remember to add in Integer_Template.<type>, etc, etc
    //       return type.getName().toString() + " " + par.getName().toString();
    //   }

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

    private String buildHeaderComment(File file) {
        String targetFileName = file.toString();

        String[] temp = targetFileName.split("\\\\");
        String fileName = temp[temp.length - 1];
        return "//\n" + "// Generated by the Resolve to Java Translator" + "\n"
                + "// from file:  " + fileName + "\n" + "// on:         "
                + new Date() + "\n" + "//\n";
    }
}
