package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

/**
 * <p>A <code>NaiveTransformationChooser</code> simply delivers its library of
 * transformations in order, completely ignoring any data about the VC or the
 * state of the proof.</p>
 */
public class NaiveTransformationChooser extends AbstractTransformationChooser {

    public NaiveTransformationChooser(Iterable<VCTransformer> library) {
        super(library);
    }

    @Override
    public Iterator<ProofPathSuggestion> doSuggestTransformations(VC vc,
            int curLength, Metrics metrics, ProofData d,
            Iterable<VCTransformer> localTheorems) {

        return new LazyMappingIterator<VCTransformer, ProofPathSuggestion>(
                getTransformerLibrary().iterator(),
                new StaticProofDataSuggestionMapping(d));
    }

}
