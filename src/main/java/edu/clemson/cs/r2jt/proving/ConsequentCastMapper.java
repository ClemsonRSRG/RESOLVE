package edu.clemson.cs.r2jt.proving;

public class ConsequentCastMapper
        implements
            Mapper<ImmutableConjuncts, Consequent> {

    @Override
    public Consequent map(ImmutableConjuncts i) {
        return new Consequent(i);
    }
}
