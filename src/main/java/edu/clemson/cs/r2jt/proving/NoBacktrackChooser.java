package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.clemson.cs.r2jt.utilities.Mapping;

/**
 * <p>A <code>NoBacktrackChooser</code> wraps an existing 
 * <code>TransformationChooser</code> such that calls to 
 * {@link #suggestTransformations(VC, int, Metrics, Iterable) 
 * 		suggestTransformations()} return <code>Iterator</code>s over all the
 * <code>VCTransformer</code>s the wrapped <code>TransformationChooser</code>
 * would ordinarily suggest, themselves wrapped in 
 * {@link NoBacktrackTransformer NoBacktrackTransformer}s, up to and including
 * the first such <code>VCTransformer</code> that actually returns a transformed
 * <code>VC</code>, after which no further <code>VCTransformer</code>s are
 * returned.</p>
 */
public class NoBacktrackChooser implements TransformationChooser {

    private static final NoBacktrackWrappingMapping NO_BACKTRACK_MAP =
            new NoBacktrackWrappingMapping();

    private final TransformationChooser myBaseChooser;

    public NoBacktrackChooser(TransformationChooser base) {
        myBaseChooser = base;
    }

    @Override
    public void preoptimizeForVC(VC vc) {
        myBaseChooser.preoptimizeForVC(vc);
    }

    @Override
    public Iterator<ProofPathSuggestion> suggestTransformations(VC vc,
            int curLength, Metrics metrics, ProofData d) {

        return new ZeroOrOneIterator(
                new LazyMappingIterator<ProofPathSuggestion, ProofPathSuggestion>(
                        myBaseChooser.suggestTransformations(vc, curLength,
                                metrics, d), NO_BACKTRACK_MAP));
    }

    @Override
    public String toString() {
        return "" + myBaseChooser;
    }

    private static class ZeroOrOneIterator
            implements
                Iterator<ProofPathSuggestion> {

        private final Iterator<ProofPathSuggestion> myBaseIterator;
        private ProofPathSuggestion myLastSuggestion;

        public ZeroOrOneIterator(Iterator<ProofPathSuggestion> base) {
            myBaseIterator = base;
        }

        @Override
        public boolean hasNext() {
            boolean retval = true;

            if (myLastSuggestion != null) {
                retval =
                        !((NoBacktrackTransformer) myLastSuggestion.step)
                                .hasReturned();
            }

            retval &= myBaseIterator.hasNext();

            return retval;
        }

        @Override
        public ProofPathSuggestion next() {
            if (myLastSuggestion != null
                    && ((NoBacktrackTransformer) myLastSuggestion.step)
                            .hasReturned()) {
                throw new NoSuchElementException();
            }

            myLastSuggestion = myBaseIterator.next();

            return myLastSuggestion;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class NoBacktrackWrappingMapping
            implements
                Mapping<ProofPathSuggestion, ProofPathSuggestion> {

        @Override
        public ProofPathSuggestion map(ProofPathSuggestion i) {
            return new ProofPathSuggestion(new NoBacktrackTransformer(i.step),
                    i.data);
        }
    }

}
