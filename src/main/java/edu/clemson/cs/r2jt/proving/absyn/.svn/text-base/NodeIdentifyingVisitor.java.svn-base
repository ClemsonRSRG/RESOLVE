package edu.clemson.cs.r2jt.proving.absyn;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

public class NodeIdentifyingVisitor extends PExpVisitor {
	
	private Deque<Integer> myIndices = new LinkedList<Integer>();
	private LinkedList<NodeIdentifier> myIDs = new LinkedList<NodeIdentifier>();
	private PExp myRoot;
	
	protected NodeIdentifier getID() {
		return myIDs.peek();
	}
	
	protected NodeIdentifier getID(int levels) {
		return myIDs.get(levels);
	}
	
	protected int getDepth() {
		return myIDs.size();
	}
	
	public final void beginPExp(PExp p) {		
		
		myIndices.push(0);  //We start at the zeroth child
		
		if (myRoot == null) {
			myRoot = p;
		}

		myIDs.push(buildID(myRoot, myIndices));
		
		doBeginPExp(p);
	}
	
	private static NodeIdentifier buildID(PExp root, Deque<Integer> indices) {
		int[] idIndices = new int[indices.size() - 1]; 
		
		//The ID should reflect all but the last index
		Iterator<Integer> indicesIter = indices.descendingIterator();
		for (int i = 0; i < (indices.size() - 1); i++) {
			idIndices[i] = indicesIter.next();
		}
		
		return new NodeIdentifier(root, idIndices);
	}
	
	public void doBeginPExp(PExp p) {
		
	}
	
	public final void endPExp(PExp p) { 
		doEndPExp(p);
		
		if (p != myRoot) {
			//We're not visiting any more children at this level (because the
			//level just ended!)
			myIndices.pop();
			
			//Increment to the next potential child index
			int i = myIndices.pop();
			myIndices.push(i + 1);
		}
		
		//We just left a PExp, so get rid of its ID
		myIDs.pop();
		
		if (p == myRoot) {
			myRoot = null;
			myIndices.pop();
		}
	}
	
	public void doEndPExp(PExp p) {
		
	}
}
