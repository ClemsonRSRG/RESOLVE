package edu.clemson.cs.r2jt.proving.immutableadts;

import java.util.Iterator;

import edu.clemson.cs.r2jt.proving.SingletonIterator;

public class SingletonImmutableList<E> extends AbstractImmutableList<E> {

    private final EmptyImmutableList<E> EMPTY = new EmptyImmutableList<E>();

    private final E myElement;

    public SingletonImmutableList(E e) {
        myElement = e;
    }

    @Override
    public E get(int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException();
        }

        return myElement;
    }

    @Override
    public ImmutableList<E> head(int length) {
        ImmutableList<E> retval;

        switch (length) {
        case 0:
            retval = EMPTY;
            break;
        case 1:
            retval = this;
            break;
        default:
            throw new IndexOutOfBoundsException();
        }

        return retval;
    }

    @Override
    public Iterator<E> iterator() {
        return new SingletonIterator<E>(myElement);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public ImmutableList<E> tail(int startIndex) {

        ImmutableList<E> retval;

        switch (startIndex) {
        case 0:
            retval = this;
            break;
        case 1:
            retval = EMPTY;
            break;
        default:
            throw new IndexOutOfBoundsException();
        }

        return retval;
    }
}
