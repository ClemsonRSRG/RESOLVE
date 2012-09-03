package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

/**
 * <p>A <code>Transformation</code> defines a rule which may be applied to an
 * input (perhaps in multiple different ways) to yield an output.</p>
 *
 * @param <T> The type accepted by the transformation.
 */
public interface Transformation<T> {
	public Iterator<T> transform(T original);
}
