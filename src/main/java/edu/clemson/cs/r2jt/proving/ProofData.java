package edu.clemson.cs.r2jt.proving;

import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * <p>The <code>ProofData</code> class is an immutable class that represents the
 * state of a proof in progress.  It differs from <code>Metrics</code> in that,
 * unlike <code>Metrics</code> which contains data about the entire proof
 * search, <code>ProofData</code> contains only information about the currently
 * considered proof--i.e., data about the steps taken.</p>
 * 
 * <p><code>ProofData</code> is designed to be dynamically extensible so that
 * individual <code>TransformationChooser</code>s may record proof-state 
 * information.</p>
 */
public class ProofData {
	private Deque<VC> myPastStates = new LinkedList<VC>();
	
	private Map<TransformerData, Object> myTransformerData = 
		new HashMap<TransformerData, Object>();
	
	public ProofData addStep(VC step) {
		ProofData retval = copy();
		retval.myPastStates.push(step);
		
		return retval;
	}
	
	public ProofData popStep() {
		ProofData retval = copy();
		retval.myPastStates.pop();
		
		return retval;
	}
	
	public Iterator<VC> stepIterator() {
		return new ImmutableIteratorWrapper<VC>(myPastStates.iterator());
	}
	
	public ProofData putAttribute(TransformationChooser c, 
			Object key, Object value) {
		
		ProofData retval = copy();
		
		TransformerData t = new TransformerData(c, key);
		
		retval.myTransformerData.put(t, value);
		
		return retval;
	}
	
	public boolean attributeDefined(TransformationChooser c, Object key) {
		
		TransformerData t = new TransformerData(c, key);
		
		return myTransformerData.containsKey(t);
	}
	
	public Object getAttribute(TransformationChooser c, Object key) {
		return myTransformerData.get(new TransformerData(c, key));
	}
	
	public ProofData copy() {
		ProofData retval = new ProofData();
		retval.myPastStates = new LinkedList<VC>(myPastStates);
		retval.myTransformerData = 
			new HashMap<TransformerData, Object>(myTransformerData);
		
		return retval;
	}
	
	private static class TransformerData {
		public TransformationChooser chooser;
		public Object key;
		
		public TransformerData(TransformationChooser c, Object k) {
			chooser = c;
			key = k;
		}
		
		public int hashCode() {
			return chooser.hashCode() + key.hashCode();
		}
		
		public boolean equals(Object o) {
			boolean retval = (o instanceof TransformerData);
			
			if (retval) {
				TransformerData oAsTransformerData = (TransformerData) o;
				
				retval = oAsTransformerData.chooser.equals(chooser) &&
						oAsTransformerData.key.equals(key);
			}
			
			return retval;
		}
	}
}
