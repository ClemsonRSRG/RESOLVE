package edu.clemson.cs.r2jt.proving;

public interface Transformer<S, D> {

    public D transform(S source);
}
