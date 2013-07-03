package edu.clemson.cs.r2jt.translation.bookkeeping;

import edu.clemson.cs.r2jt.translation.bookkeeping.books.JavaFunctionBook;
import java.util.ArrayList;

/**
 *
 * @author Welch D
 */
public abstract class JavaBookkeeper extends AbstractBookkeeper {

    private JavaFunctionBook currentFunction;

    /**
     * Construct a supervisor to manage Java modules undergoing 
     * translation.
     */
    public JavaBookkeeper(String name, Boolean isRealiz) {
        super(name, isRealiz);
    }

    /* FunctionEmployee Adders */

    @Override
    public void fxnAdd(String retType, String funcName) {
        JavaFunctionBook f;
        f = new JavaFunctionBook(retType, funcName, isRealization);
        functionList.add(f);
        currentFunction = f;
    }

}