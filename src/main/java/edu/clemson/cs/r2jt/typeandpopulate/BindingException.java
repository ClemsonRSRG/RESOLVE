package edu.clemson.cs.r2jt.typeandpopulate;

public class BindingException extends Exception {

    private static final long serialVersionUID = 1L;

    public final Object found;
    public final Object expected;

    public BindingException(Object found, Object expected) {
        this.found = found;
        this.expected = expected;
    }
}
