package edu.clemson.cs.r2jt.absynnew;

/**
 * <p>A <code>CircularDependencyException</code> indicates an unresolvable
 * circular dependency between two (or more) modules.</p>
 */
public class CircularDependencyException extends RuntimeException {

    /**
     * <p>Creates a new <code>CircularDependencyException</code> with the given
     * message.</p>
     *
     * @param msg The message.
     */
    public CircularDependencyException(String msg) {
        super(msg);
    }

}