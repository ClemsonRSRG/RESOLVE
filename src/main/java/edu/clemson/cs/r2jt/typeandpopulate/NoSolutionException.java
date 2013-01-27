package edu.clemson.cs.r2jt.typeandpopulate;

public class NoSolutionException extends Exception {

    public static final NoSolutionException INSTANCE =
            new NoSolutionException();

    private static final long serialVersionUID = 1L;

    private NoSolutionException() {
        super();
    }

    public NoSolutionException(String msg) {
        super(msg);
    }

    public NoSolutionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public NoSolutionException(Throwable cause) {
        super(cause);
    }
}
