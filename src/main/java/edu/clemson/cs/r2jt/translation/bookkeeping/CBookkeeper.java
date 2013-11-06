package edu.clemson.cs.r2jt.translation.bookkeeping;

public class CBookkeeper extends AbstractBookkeeper {

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
        CFacilityDeclBook f;
        f = new CFacilityDeclBook(name, concept, realiz);
        myFacilityList.add(f);
        myCurrentFacility = f;
    }

    /* FunctionBook Adders */

    @Override
    public void fxnAdd(String retType, String funcName) {
    //  FunctionBook f;
    //  f = new CFunctionBook(retType, funcName, isRealization);
    //  myFunctionList.add(f);
    //  myCurrentFunction = f;
    }

    @Override
    public String output() {
        return "empty";
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

class CFacilityDeclBook extends AbstractFacilityDecBook {

    CFacilityDeclBook(String name, String concept, String realiz) {
        super(name, concept, realiz);
    }

    @Override
    String getString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

/*class CFunctionBook extends FunctionBook {

 CFunctionBook(String returnType, String name, boolean hasBody) {
 super(returnType, name, hasBody);
 }

 @Override
 String getString() {
 return "GETSTRINGFORFUNCTIONBOOK";
 //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
 }
 }*/

