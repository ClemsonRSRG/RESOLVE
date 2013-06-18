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
    private ElementFunction currentFunction;
    //private ElementBody body;

    private ArrayList<String> headerImportList;
    private ArrayList<ElementFunction> functionList;

    // this might be an idea... 
    //private String moduleType;

    private String headerClassDeclaration;
    private ErrorHandler err;
    private File srcFile;

    // ===========================================================
    // Constructor(s)
    // ===========================================================

    public JavaTranslationBookkeeper(ErrorHandler err, File doc) {
        this.err = err;
        this.srcFile = doc;

        functionList = new ArrayList();
        headerImportList = new ArrayList();
    }

    // ===========================================================
    // Public methods
    // ===========================================================

    public void addImport(File file) {
        String impStr = "import " + formPkgPath(file) + ".*;\n";
        if (!headerImportList.contains(impStr)) {
            headerImportList.add(impStr);
        }
    }

    public void addClassDeclaration(String line) {
        headerClassDeclaration = line;
    }

    @Override
    public String toString() {
        StringBuilder finalCode = new StringBuilder();

        finalCode.append(buildHeaderComment(srcFile));
        finalCode.append("package ");
        finalCode.append(formPkgPath(srcFile));
        finalCode.append("\n\n");
        finalCode.append("import RESOLVE.*\n");
        for (String imp : headerImportList) {
            finalCode.append(imp);
        }
        finalCode.append("\n");
        finalCode.append(headerClassDeclaration).append(" {\n");

        for (ElementFunction func : functionList) {
            finalCode.append(func.writeFunction());

        }
        return finalCode.toString();

    }

    // ===========================================================
    // Element Classes
    // ===========================================================

    // elementbody will keep track of everything inside 
    // (not including) the outermost { ... }.
    // come back to this later... I have some questions.
    /*   private class ElementBody {

           private ArrayList<String> bodyList;

           public ElementBody() {
               bodyList = new ArrayList();
           }

       }*/

    private class ElementFunction {

        private String name;
        private String returnType;
        private StringBuffer allStatements;

        private List<String> parameterList;
        private List<String> statementList;
        private List<String> variableInitList;

        // default constructor is fine in this case

        public String writeFunction() {

            StringBuilder completeFunc = new StringBuilder();
            completeFunc.append(returnType);
            completeFunc.append(name).append("(");

            for (int i = 0; i < parameterList.size(); i++) {
                completeFunc.append(parameterList.get(i));
                if (i != parameterList.size() - 1) {
                    completeFunc.append(", ");
                }
            }
            // need something in here to differentiate 
            // between facility/realization fxns and 
            // those declarations found in concept files...
            //if (returnType.equals("void ")) {
            //	fullFuncStr.append(");");
            //	return 
            //}
            completeFunc.append(") {\n");

            if (allStatements != null) {
                completeFunc.append("\t");
                completeFunc.append(allStatements);
            }
            if (!returnType.equals("void ")) {
                completeFunc.append("return ").append(name).append(";\n");
            }
            completeFunc.append("}\n\n");
            return completeFunc.toString();
        }
    }

    public void appendToStatement(String line) {
        if (currentFunction != null) {
            currentFunction.allStatements.append(line);
        }
    }

    // acc = access (i.e. public, private, etc)
    public void addFunction(String acc, PosSymbol retType, PosSymbol name) {
        ElementFunction newFunc = new ElementFunction();

        newFunc.returnType = "void ";
        if (retType != null) {
            newFunc.returnType = retType.getName().toString();
        }

        newFunc.name = name.toString();
        newFunc.allStatements = new StringBuffer();
        newFunc.parameterList = new ArrayList<String>();
        newFunc.statementList = new ArrayList<String>();
        newFunc.variableInitList = new ArrayList<String>();
        functionList.add(newFunc);
        currentFunction = newFunc;
    }

    // ===========================================================
    // 
    // ===========================================================

    // qualifiers are a going to need to be added..
    public void addFunctionInitVariable(PosSymbol type, PosSymbol name) {
        if (currentFunction != null) {
            String completeVar = formFunctionVariable(type, name);
            currentFunction.allStatements.append(completeVar);
            currentFunction.allStatements.append("\n\t");
            currentFunction.variableInitList.add(completeVar);
        }
    }

    public void addFunctionParameter(PosSymbol type, PosSymbol name) {
        if (currentFunction != null) {
            String completeParameter = formFunctionParameter(type, name);
            currentFunction.parameterList.add(completeParameter);
        }
    }

    //	public void addLineToFuncion()
    private String formFunctionParameter(PosSymbol type, PosSymbol name) {
        // remember to add in Integer_Template.<type>, etc, etc
        // right now it just does <type> <name>
        return type.getName().toString() + " " + name.getName().toString();
    }

    private String formFunctionVariable(PosSymbol type, PosSymbol name) {

        return type.getName().toString() + " " + name.getName().toString();
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
