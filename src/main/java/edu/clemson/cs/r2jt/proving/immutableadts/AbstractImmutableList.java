package edu.clemson.cs.r2jt.proving.immutableadts;

import java.util.Iterator;

public abstract class AbstractImmutableList<E>
        implements
            SimpleImmutableList<E> {

    @Override
    public SimpleImmutableList<E> appended(E e) {
        return appended(new SingletonImmutableList<E>(e));
    }

    @Override
    public SimpleImmutableList<E> appended(SimpleImmutableList<E> l) {
        return new ImmutableListConcatenation<E>(this, l);
    }

    @Override
    public SimpleImmutableList<E> appended(Iterable<E> i) {
        return appended(new ImmutableList<E>(i));
    }

    @Override
    public E first() {
        return get(0);
    }

    @Override
    public SimpleImmutableList<E> removed(int index) {
        SimpleImmutableList<E> retval;

        if (index == 0) {
            retval = tail(1);
        }
        else if (index == size() - 1) {
            retval = head(index);
        }
        else {
            retval =
                    new ImmutableListConcatenation<E>(head(index),
                            tail(index + 1));
        }

        return retval;
    }

    @Override
    public SimpleImmutableList<E> set(int index, E e) {
        SimpleImmutableList<E> first, second;

        SimpleImmutableList<E> insertedList = new SingletonImmutableList<E>(e);

        if (index == 0) {
            first = insertedList;
            second = tail(1);
        }
        else if (index == size() - 1) {
            first = head(index);
            second = insertedList;
        }
        else {
            first =
                    new ImmutableListConcatenation<E>(head(index), insertedList);
            second = tail(index + 1);
        }

        return new ImmutableListConcatenation<E>(first, second);
    }

    @Override
    public SimpleImmutableList<E> insert(int index, E e) {
        return insert(index, new SingletonImmutableList<E>(e));
    }

    @Override
    public SimpleImmutableList<E> insert(int index, SimpleImmutableList<E> l) {
        SimpleImmutableList<E> first, second;

        if (index == 0) {
            first = l;
            second = this;
        }
        else if (index == size()) {
            first = this;
            second = l;
        }
        else {
            first = new ImmutableListConcatenation<E>(head(index), l);
            second = tail(index);
        }

        return new ImmutableListConcatenation<E>(first, second);
    }

    @Override
    public SimpleImmutableList<E> subList(int startIndex, int length) {
        return tail(startIndex).head(length);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer("[");

        int sizeSanityCheck = 0;

        boolean first = true;
        Iterator<E> iterator = iterator();
        while (iterator.hasNext()) {
            if (!first) {
                buffer.append(", ");
            }

            buffer.append(iterator.next());

            first = false;
            sizeSanityCheck++;
        }

        buffer.append("]");

        if (sizeSanityCheck != size()) {
            throw new RuntimeException();
        }

        return buffer.toString();
    }
}
