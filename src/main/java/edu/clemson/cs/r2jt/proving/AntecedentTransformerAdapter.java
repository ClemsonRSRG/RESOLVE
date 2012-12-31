package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

import edu.clemson.cs.r2jt.utilities.Mapping;

public class AntecedentTransformerAdapter implements AntecedentTransformer {

    private static final Mapping<ImmutableConjuncts, Antecedent> MAP_TO_ANTECEDENTS =
            new AntecedentCastMapping();

    private final ConjunctsTransformer myTransformer;

    public AntecedentTransformerAdapter(ConjunctsTransformer t) {
        myTransformer = t;
    }

    public Iterator<Antecedent> transform(Antecedent original) {
        return new LazyMappingIterator<ImmutableConjuncts, Antecedent>(
                myTransformer.transform(original), MAP_TO_ANTECEDENTS);
    }

    public String toString() {
        return myTransformer.toString();
    }
}
