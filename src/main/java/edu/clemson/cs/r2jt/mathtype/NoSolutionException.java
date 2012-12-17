package edu.clemson.cs.r2jt.mathtype;

public class NoSolutionException extends Exception {

    private static final long serialVersionUID = 1L;

    public NoSolutionException() {
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
