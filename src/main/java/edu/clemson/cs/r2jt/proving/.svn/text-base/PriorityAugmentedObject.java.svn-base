package edu.clemson.cs.r2jt.proving;

public class PriorityAugmentedObject<T> implements Comparable<PriorityAugmentedObject<T>> {

	private T myObject;
	private double myPriority;
	
	public PriorityAugmentedObject(T object, double priority) {
		myObject = object;
		myPriority = priority;
	}
	
	public double getPriority() {
		return myPriority;
	}
	
	public T getObject() {
		return myObject;
	}
	
	/**
	 * <p>Since higher priorities are better, they should come first in lists
	 * and thus higher priority values are considered to come "before" lower
	 * ones.</p>
	 */
	public int compareTo(PriorityAugmentedObject<T> other) {
		int comparison;
		
		if (myPriority < other.myPriority) {
			comparison = 1;
		}
		else if (myPriority > other.myPriority) {
			comparison = -1;
		}
		else {
			comparison = 0;
		}
		
		return comparison;
	}

}
