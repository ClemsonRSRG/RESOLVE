package edu.clemson.cs.r2jt.mathtype;

import java.util.Collections;
import java.util.List;

public class DummyIdentifierResolver extends IdentifierResolver {

    @Override
    public MathSymbolTableEntry getInnermostBinding(String name,
            MathSymbolTable.ImportStrategy importStrategy)
            throws NoSuchSymbolException {

        throw new NoSuchSymbolException(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MathSymbolTableEntry> getAllBindings(String name,
            MathSymbolTable.ImportStrategy importStrategy) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public void buildAllBindingsList(String name,
            List<MathSymbolTableEntry> accumulator,
            MathSymbolTable.ImportStrategy importStrategy) {

    //This space intentionally left blank
    }
}
