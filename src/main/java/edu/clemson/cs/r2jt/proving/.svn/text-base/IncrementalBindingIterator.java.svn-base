package edu.clemson.cs.r2jt.proving;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.clemson.cs.r2jt.proving.absyn.BindingException;
import edu.clemson.cs.r2jt.proving.absyn.PExp;

/**
 * <p>An <code>IncrementalBindingIterator</code> returns successive possible 
 * bindings of a universally quantified <code>Exp</code> to a set of available 
 * facts, possibly given a set of assumed bindings.  All possible bindings will 
 * be returned, but the order they will be returned in is undefined.</p>
 * 
 * <p>As an example, if the <code>Exp</code> is 
 * <code>For all i, j : Z, i &lt; j</code> and the set of facts is:</p>
 * 
 * <ul>
 *   <li><code>a &lt; b</code></li>
 *   <li><code>c &lt; b</code></li>
 *   <li><code>d &lt; c</code></li>
 * </ul>
 * 
 * And there is only one assumed binding, namely:
 * 
 * <ul>
 *   <li><code>j --&gt; b</code></li>
 * </ul>
 * 
 * <p>Then <code>IncrementalBindingIterator</code> might first return the 
 * binding:</p>
 * 
 * <ul>
 *   <li><code>j --&gt; b</code></li>
 *   <li><code>i --&gt; a</code></li>
 * </ul>
 * 
 * <p>And then:</p>
 * 
 * <ul>
 *   <li><code>j --&gt; b</code></li>
 *   <li><code>i --&gt; c</code></li>
 * </ul>
 * 
 * <p>After which it will return nothing else.  Note that by time the 
 * <code>Exp</code> makes its way to this class, quantifiers should already have
 * been eliminated and propagated down to the variables themselves (that is, the
 * variable nodes should reflect their quantified state, not a top-level 
 * quantifying expression.)</p>
 */
public class IncrementalBindingIterator implements Iterator<Map<PExp, PExp>> {

	private static final Map<PExp, PExp> EMPTY_MAP = new HashMap<PExp, PExp>();
	
	private final PExp myPattern;
	private final Iterator<PExp> myFacts;
	
	private final Map<PExp, PExp> myAssumedBindings;
	
	private Map<PExp, PExp> myCurrentIncrementalBindings;
	
	public IncrementalBindingIterator(PExp pattern, Iterator<PExp> facts, 
			Map<PExp, PExp> assumedBindings) {
		
		myPattern = pattern.substitute(assumedBindings);
		myFacts = facts;
		myAssumedBindings = assumedBindings;
		
		setUpNext();
	}
	
	public IncrementalBindingIterator(PExp pattern, Iterable<PExp> facts, 
			Map<PExp, PExp> assumedBindings) {
		this(pattern, facts.iterator(), assumedBindings);
	}
	
	public IncrementalBindingIterator(PExp pattern, Iterator<PExp> facts) {
		this(pattern, facts, EMPTY_MAP);
	}
	
	public IncrementalBindingIterator(PExp pattern, Iterable<PExp> facts) {
		this(pattern, facts.iterator(), EMPTY_MAP);
	}
	
	private void setUpNext() {
		
		myCurrentIncrementalBindings = null;
		while (myCurrentIncrementalBindings == null && myFacts.hasNext()) {
			PExp fact = myFacts.next().substitute(myAssumedBindings);
			
			try {
				myCurrentIncrementalBindings = myPattern.bindTo(fact);
			}
			catch (BindingException e) {
				
			}
		}
	}
	
	@Override
	public boolean hasNext() {
		return myCurrentIncrementalBindings != null;
	}

	@Override
	public Map<PExp, PExp> next() {
		Map<PExp, PExp> retval = new HashMap<PExp, PExp>();
		
		//TODO : Might be good, eventually, to have a "chaining map" that makes
		//       this a constant-time operation
		retval.putAll(myCurrentIncrementalBindings);
		retval.putAll(myAssumedBindings);
		
		setUpNext();
		
		return retval;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
