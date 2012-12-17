package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

import edu.clemson.cs.r2jt.utilities.Mapping;

/**
 * <p>A <code>LazyMappingIterator</code> wraps an <code>Iterator</code> that
 * iterates over objects of type <code>I</code> and presents an interface for
 * mapping over objects of type <code>O</code>.  A <code>Mapping</code> from
 * <code>I</code> to <code>O</code> is used to transform each object as it is
 * requested.</p>

 * @param <I> The type of the objects in the source iterator.
 * @param <O> The type of the final objects.
 */
public final class LazyMappingIterator<I, O> implements Iterator<O> {

    private final Iterator<I> mySource;
    private final Mapping<I, O> myMapper;

    public LazyMappingIterator(Iterator<I> source, Mapping<I, O> mapper) {
        mySource = source;
        myMapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return mySource.hasNext();
    }

    @Override
    public O next() {
        return myMapper.map(mySource.next());
    }

    @Override
    public void remove() {
        mySource.remove();
    }
}
