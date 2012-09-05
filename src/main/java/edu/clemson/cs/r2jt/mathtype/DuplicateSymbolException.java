package edu.clemson.cs.r2jt.mathtype;

@SuppressWarnings("serial")
public class DuplicateSymbolException extends SymbolTableException {

    private final MathSymbolTableEntry myExistingEntry;

    public DuplicateSymbolException(String s) {
        super(s);
        myExistingEntry = null;
    }

    public DuplicateSymbolException(MathSymbolTableEntry existing) {
        super();

        myExistingEntry = existing;
    }

    public DuplicateSymbolException(MathSymbolTableEntry existing, String msg) {
        super(msg);

        myExistingEntry = existing;
    }

    public MathSymbolTableEntry getExistingEntry() {
        return myExistingEntry;
    }
}
