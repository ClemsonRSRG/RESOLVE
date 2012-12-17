package edu.clemson.cs.r2jt.proving;

import edu.clemson.cs.r2jt.utilities.Mapping;

public class ConsequentCastMapping
        implements
            Mapping<ImmutableConjuncts, Consequent> {

    @Override
    public Consequent map(ImmutableConjuncts i) {
        return new Consequent(i);
    }
}
