package edu.clemson.cs.r2jt.translation.bookkeeping;

/**
 *
 * @author Welch D
 */
public class JavaBookkeeper extends AbstractBookkeeper {

    public JavaBookkeeper(String moduleName, Boolean isRealiz) {
        super(moduleName, isRealiz);
    }

	// -----------------------------------------------------------
    //   FacilityBook methods
    // -----------------------------------------------------------

    @Override
    public void facAdd(String name, String concept, String realiz) {
    //     JavaFacilityDeclBook newFac;

    }

    /* FunctionBook Adders */

    @Override
    public void fxnAdd(String retType, String funcName) {
        throw new UnsupportedOperationException("Not supported yet.");

    /*    FunctionBook f;

        f = new JavaFunctionBook(retType, funcName, isRealization);
        myFunctionList.add(f);
        myCurrentFunction = f;*/
    }

    @Override
    public String output() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}



class JavaFacilityDeclBook extends FacilityDeclarationBook {

    JavaFacilityDeclBook(String name, String concept, String realiz) {
        super(name, concept, realiz);
    }

    @Override
    String getString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}