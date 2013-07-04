/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.translation.bookkeeping;

/**
 *
 * @author
 * Mark
 * T
 */
public class CBookkeeper extends AbstractBookkeeper {

    /**
     * Construct a supervisor to manage Java modules undergoing 
     * translation.
     */
    public CBookkeeper(String name, Boolean isRealiz) {
        super(name, isRealiz);
    }

    @Override
    public void facAdd(String name, String concept, String realiz) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /* FunctionBook Adders */

    @Override
    public void fxnAdd(String retType, String funcName) {
        /*FunctionBook f;
        f = new CFunctionBook(retType, funcName, isRealization);
        functionList.add(f);
        currentFunction = f;*/
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String output() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
