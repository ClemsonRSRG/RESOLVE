package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

/**
 * <p>A <code>RepeatedApplicationTransformer</code> wraps an existing
 * <code>Transformer</code> from an <code>ImmutableConjuncts</code> subtype
 * <code>T</code> to an <code>Iterator</code> over new <code>T</code>s that 
 * returns singleton suggestions (that is, each call to <code>transform()</code>
 * returns an <code>Iterator</code> over <em>exactly one</em> element).  Given
 * <code>T</code>s are transformed by repeatedly applying the wrapped
 * transformation some finite number of times.</p>
 * 
 * TODO : Maybe construct some type safety so that ONLY singleton transforms can
 *        be used?
 */
public class RepeatedApplicationTransformer<T extends ImmutableConjuncts> 
			implements Transformer<T, Iterator<T>> {

	private final Transformer<T, Iterator<T>> mySubTransformer;
	private final int myIterationCount;
	
	public RepeatedApplicationTransformer(
			Transformer<T, Iterator<T>> t, int iterations) {
		
		mySubTransformer = t;
		myIterationCount = iterations;
	}

	@Override
	public Iterator<T> transform(T original) {
		
		Iterator<T> singletonIterator;
		T soFar = original;
		for (int iteration = 0; iteration < myIterationCount; iteration++) {
			singletonIterator = mySubTransformer.transform(soFar);
			
			if (singletonIterator.hasNext()) {
				soFar = singletonIterator.next();
			}
			
			if (singletonIterator.hasNext()) {
				throw new RuntimeException("Non-singleton transform used in " +
						this.getClass());
			}
		}
		
		return new SingletonIterator<T>(soFar);
	}
	
}
