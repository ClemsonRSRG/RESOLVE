package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AlternativesTransformer<T> implements Transformer<T, Iterator<T>> {

	private final Iterator<T> TYPE_SAFE_ITERATOR = null;
	
	private List<Transformer<T, Iterator<T>>> myAlternatives =
		new LinkedList<Transformer<T, Iterator<T>>>();
	
	public void addAlternative(Transformer<T, Iterator<T>> a) {
		myAlternatives.add(a);
	}
	
	@Override
	public Iterator<T> transform(T source) {
			
		Iterator<T> current;
		Iterator<T> soFar = DummyIterator.getInstance(TYPE_SAFE_ITERATOR);
		for (Transformer<T, Iterator<T>> alternative : myAlternatives) {
			
			current = alternative.transform(source);

			if (current.hasNext()) {
				soFar = new ChainingIterator<T>(soFar, current); 
							//alternative.transform(source));
			}
		}
		
		return soFar;
	}
}
