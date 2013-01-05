package edu.clemson.cs.r2jt.mathtype;

public class TypeMismatchException extends Exception {

    public static final TypeMismatchException INSTANCE = 
            new TypeMismatchException();
    
    private static final long serialVersionUID = 1L;
    
    private TypeMismatchException() {
    }
}
