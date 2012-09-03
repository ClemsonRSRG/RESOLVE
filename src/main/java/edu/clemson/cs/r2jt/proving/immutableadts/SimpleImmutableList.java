package edu.clemson.cs.r2jt.proving.immutableadts;

import java.util.Iterator;

public interface SimpleImmutableList<E> extends Iterable<E> {
	
	/*
	 * "Mutator" methods that return a new, changed version of this list
	 */
	
	public SimpleImmutableList<E> appended(E e);
	public SimpleImmutableList<E> appended(SimpleImmutableList<E> l);
	public SimpleImmutableList<E> appended(Iterable<E> i);
	
	public SimpleImmutableList<E> removed(int index);
	
	public SimpleImmutableList<E> set(int index, E e);
	
	public SimpleImmutableList<E> insert(int index, E e);
	public SimpleImmutableList<E> insert(int index, SimpleImmutableList<E> e);
	
	
	/*
	 * Methods that return a view of this list
	 */
	
	public SimpleImmutableList<E> subList(int startIndex, int length);
	public SimpleImmutableList<E> tail(int startIndex);
	public SimpleImmutableList<E> head(int length);
	
	
	/*
	 * Methods for getting out elements
	 */
	
	public E first();
	public E get(int index);
	public Iterator<E> iterator();
	
	
	/*
	 * Utility methods.
	 */
	
	public int size();
}
