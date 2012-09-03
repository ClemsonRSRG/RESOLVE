package edu.clemson.cs.r2jt.sanitycheck;

/*
 * An exception to be thrown by sanity checking operations.
 * 
 * Created May 29, 2008.
 * 
 * @author Hampton Smith
 */
public class SanityCheckException extends Exception {
    public SanityCheckException() { ; }

    public SanityCheckException(String msg) {
        super(msg);
    }
}
