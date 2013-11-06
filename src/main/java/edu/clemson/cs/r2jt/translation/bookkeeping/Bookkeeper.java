package edu.clemson.cs.r2jt.translation.bookkeeping;

public interface Bookkeeper {

    public void addUses(String uses);

    public void addConstructor(String constructor);

    // -----------------------------------------------------------
    //   Facility methods
    // -----------------------------------------------------------

    public void facAdd(String name, String concept, String realiz);

    public void facAddParameter(String parameter);

    public void facAddEnhancement(String name, String realiz);

    public void facAddEnhancementParameter(String parameter);

    public boolean facEnhancementIsOpen();

    public void facEnhancementEnd();

    public void facEnd();

    // -----------------------------------------------------------
    //   Function methods
    // -----------------------------------------------------------

    public void fxnAdd(String type, String name);

    public boolean fxnIsOpen();

    public void fxnAddParameter(String parameter);

    public void fxnAddVariableDeclaration(String variable);

    public void fxnAppendTo(String stmt);

    public void fxnEnd();

    /**
     * <p>Returns a string representation of the module undergoing
     * translation into Java, C, etc.</p>
     */
    public String output();
}
