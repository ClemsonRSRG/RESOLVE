package edu.clemson.cs.r2jt.utilities;

/**
 * <p>A three-parameter mapping.</p>
 */
public interface Mapping3<P1, P2, P3, R> {

    public R map(P1 p1, P2 p2, P3 p3);
}
