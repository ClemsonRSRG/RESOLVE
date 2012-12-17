package edu.clemson.cs.r2jt.mathtype;

public class TypeMismatchException extends Exception {

    private static final long serialVersionUID = 1L;

    private final MTType myExpectedType;
    private final MTType myFoundType;

    public TypeMismatchException(MTType expected, MTType found) {
        myExpectedType = expected;
        myFoundType = found;
    }

    public MTType getExpected() {
        return myExpectedType;
    }

    public MTType getFound() {
        return myFoundType;
    }
}
