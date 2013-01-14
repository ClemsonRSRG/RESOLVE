package edu.clemson.cs.r2jt.typeandpopulate;

@SuppressWarnings("serial")
public class NoSuchSymbolException extends SymbolTableException {

    public NoSuchSymbolException() {
        super();
    }

    public NoSuchSymbolException(String msg) {
        super(msg);
    }

    public NoSuchSymbolException(Exception causedBy) {
        super(causedBy);
    }
}
