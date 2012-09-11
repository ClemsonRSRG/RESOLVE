package edu.clemson.cs.r2jt.mathtype;

interface ImportRepository {

    public IdentifierResolver getModuleScope(ModuleIdentifier module)
            throws NoSuchSymbolException;
}
