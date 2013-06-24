package edu.clemson.cs.r2jt.translation.bookkeeping;

import edu.clemson.cs.r2jt.translation.bookkeeping.books.JavaFunctionBook;
import java.util.ArrayList;

/**
 *
 * @author Welch D
 */
public abstract class JavaBookkeeper implements Bookkeeper {

    private JavaFunctionBook currentFunction;

    /**
     * Name of the module we are translating (e.g., 'Stack_Template,'
     * 'Int_Do_Nothing,' etc).
     */
    protected String moduleName;

    /**
     * If we aren't translating a realization, then we don't need
     * bodies following function declarations as the files we are 
     * outputting will be class interfaces. Thus, this should only 
     * be false for Concepts and Enhancement declaration modules.
     */
    protected Boolean isRealization;

    protected ArrayList<String> constructorList;
    protected ArrayList<String> importList;
    protected ArrayList<JavaFunctionBook> functionList;

    /**
     * Construct a supervisor to manage Java modules undergoing 
     * translation.
     */
    public JavaBookkeeper(String name, Boolean isRealiz) {
        moduleName = name;
        isRealization = isRealiz;

        importList = new ArrayList();
        functionList = new ArrayList();
    }

    /**
     * Stores packages needed by the module undergoing translation.
     */
    @Override
    public void addUses(String usesName) {
        importList.add(usesName);
    }

    /** FunctionEmployee Adders */

    @Override
    public void fxnAdd(String retType, String funcName) {
        JavaFunctionBook f;
        f = new JavaFunctionBook(retType, funcName, isRealization);
        functionList.add(f);
        currentFunction = f;
    }

    @Override
    public void fxnAddParam(String parName) {
        currentFunction.addParameter(parName);
    }

    @Override
    public void fxnAddVarDecl(String varName) {
        currentFunction.addVariable(varName);
    }

    @Override
    public void fxnAppendTo(String stmt) {
        currentFunction.appendToStmt(stmt);
    }

    @Override
    public void fxnEnd() {}

    // TODO : Instead, maybe create a generic InterfaceBook with a 
    // JavaInterfaceBook subclass that handles interface constructors
    // in concepts as well as class "implements" instances in realizations
    public void addConceptConstructor(String name, String ext) {
        constructorList = new ArrayList<String>();
        String iFace = "public interface " + name + " extends " + ext + "{}";
        constructorList.add(iFace);
    }
}