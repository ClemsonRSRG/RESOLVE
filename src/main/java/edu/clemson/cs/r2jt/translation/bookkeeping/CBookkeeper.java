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

    protected String conceptName;

    /*C SPECIFIC HELPERS */
    public void setConceptName(String name) {
        conceptName = name;
    }

    public void getConceptName(String name) {
        conceptName = name;
    }

    public CBookkeeper(String name, Boolean isRealiz) {
        super(name, isRealiz);
    }

    /* End C SPECIFIC HELPERS */

    @Override
    public void facAdd(String name, String concept, String realiz) {
        FacilityDeclBook f;
        f = new CFacilityDeclBook(name, concept, realiz);
        facilityList.add(f);
        currentFacility = f;
    }

    /* FunctionBook Adders */

    @Override
    public void fxnAdd(String retType, String funcName) {
        FunctionBook f;
        f = new CFunctionBook(retType, funcName, isRealization);
        functionList.add(f);
        currentFunction = f;
    }

    @Override
    public String output() {
        return "empty";
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

class CFacilityDeclBook extends FacilityDeclBook {

    CFacilityDeclBook(String name, String concept, String realiz) {
        super(name, concept, realiz);
    }

    @Override
    String getString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

class CFunctionBook extends FunctionBook {

    CFunctionBook(String returnType, String name, boolean hasBody) {
        super(returnType, name, hasBody);
    }

    @Override
    String getString() {
        return "GETSTRINGFORFUNCTIONBOOK";
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
