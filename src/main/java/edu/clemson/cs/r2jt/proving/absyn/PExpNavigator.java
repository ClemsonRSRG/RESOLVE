package edu.clemson.cs.r2jt.proving.absyn;

import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * <p>A <code>PExpNavigator</code> provides a mechanism for iterating over each
 * node in the tree-structure of a 
 * {@link edu.clemson.cs.r2jt.proving.absyn.PExp PExp} by moving a cursor from
 * node to node.  The order of this traversal is undefined.  Modified versions 
 * of the original <code>PExp</code> can be obtained based on the current cursor
 * position, i.e. by replacing the last visited node with another.</p> 
 */
public class PExpNavigator {
	private Deque<PExpSubexpressionIterator> myNavigationStack = 
		new LinkedList<PExpSubexpressionIterator>();
	
	private final PExp myOriginalExpression;
	
	private boolean myReturnedTopLevelFlag = false;
	
	public PExpNavigator(PExp expression) {
		myNavigationStack.push(expression.getSubExpressionIterator());
		myOriginalExpression = expression;
	}
	
	public boolean hasNext() {
		
		//TODO : This actually violates the contract for this component--a call
		//       to hasNext() breaks the functionality of replaceLast() until
		//       next() is called
		while (!(myNavigationStack.isEmpty() || 
				myNavigationStack.peek().hasNext())) {
			
			myNavigationStack.pop();
		}
		
		return !myNavigationStack.isEmpty();
	}
	
	public PExp next() {
		PExp retval;
		
		if (myReturnedTopLevelFlag) {
			while (!(myNavigationStack.isEmpty() || 
					myNavigationStack.peek().hasNext())) {
				
				myNavigationStack.pop();
			}
			
			if (myNavigationStack.isEmpty()) {
				if (myReturnedTopLevelFlag) {
					throw new NoSuchElementException();
				}
				else {
					//TODO : This looks like dead code to me... should be 
					//removed and tested.
					retval = myOriginalExpression;
					myReturnedTopLevelFlag = true;
				}
			}
			else {
				retval = myNavigationStack.peek().next();
				myNavigationStack.push(retval.getSubExpressionIterator());
			}	
		}
		else {
			retval = myOriginalExpression;
			myReturnedTopLevelFlag = true;
		}

		return retval;
	}
	
	public PExp replaceLast(PExp e) {
		PExp retval = e;
		
		boolean first = true;
		for (PExpSubexpressionIterator i : myNavigationStack) {
			if (first) {
				first = false;
			}
			else {
				retval = i.replaceLast(retval);
			}
		}
		
		return retval;
	}
}
