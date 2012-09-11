package edu.clemson.cs.r2jt.proving.immutableadts;

import java.util.Iterator;

import edu.clemson.cs.r2jt.proving.ChainingIterator;

public class ImmutableListConcatenation<E> extends AbstractImmutableList<E> {

    private final SimpleImmutableList<E> myFirstList;
    private final int myFirstListSize;

    private final SimpleImmutableList<E> mySecondList;
    private final int mySecondListSize;

    private final int myTotalSize;

    public ImmutableListConcatenation(SimpleImmutableList<E> firstList,
            SimpleImmutableList<E> secondList) {

        myFirstList = firstList;
        myFirstListSize = myFirstList.size();

        mySecondList = secondList;
        mySecondListSize = mySecondList.size();

        myTotalSize = myFirstListSize + mySecondListSize;
    }

    @Override
    public E get(int index) {
        E retval;

        if (index < myFirstListSize) {
            retval = myFirstList.get(index);
        }
        else {
            retval = mySecondList.get(index - myFirstListSize);
        }

        return retval;
    }

    @Override
    public SimpleImmutableList<E> head(int length) {
        SimpleImmutableList<E> retval;

        if (length <= myFirstListSize) {
            retval = myFirstList.head(length);
        }
        else {
            retval =
                    new ImmutableListConcatenation<E>(myFirstList, mySecondList
                            .head(length - myFirstListSize));
        }

        return retval;
    }

    @Override
    public Iterator<E> iterator() {
        return new ChainingIterator<E>(myFirstList.iterator(), mySecondList
                .iterator());
    }

    @Override
    public int size() {
        return myTotalSize;
    }

    @Override
    public SimpleImmutableList<E> subList(int startIndex, int length) {
        return tail(startIndex).head(length);
    }

    @Override
    public SimpleImmutableList<E> tail(int startIndex) {
        SimpleImmutableList<E> retval;

        if (startIndex < myFirstListSize) {
            retval =
                    new ImmutableListConcatenation<E>(myFirstList
                            .tail(startIndex), mySecondList);
        }
        else {
            retval = mySecondList.tail(startIndex - myFirstListSize);
        }

        return retval;
    }
}
