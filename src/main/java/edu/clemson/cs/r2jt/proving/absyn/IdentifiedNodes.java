package edu.clemson.cs.r2jt.proving.absyn;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class IdentifiedNodes<T> {

	private final PExp myRoot;
	
	private Set<NodeIdentifier> myTopLevelIdentifiedNodes = 
			new HashSet<NodeIdentifier>();
	
	private Map<NodeIdentifier, Set<NodeIdentifier>> myIdentifiedNodes = 
			new HashMap<NodeIdentifier, Set<NodeIdentifier>>();
	
	private Map<NodeIdentifier, T> myDataMap = new HashMap<NodeIdentifier, T>();
	
	public IdentifiedNodes(PExp root) {
		myRoot = root;
	}
	
	public NodeIdentifier getLargestIdentifiedAncestor(NodeIdentifier id) {
		return getContainer(id, myTopLevelIdentifiedNodes);
	}
	
	public List<NodeIdentifier> getIdentifiedAncestors(NodeIdentifier id) {
		List<NodeIdentifier> result = new LinkedList<NodeIdentifier>();
		
		try {
			getSmallestIdentifiedAncestorIn(id, myTopLevelIdentifiedNodes, 
					result);
		}
		catch (NoSuchElementException e) {
			//That's ok--if it has no ancestors, return an empty list
		}
		
		return result;
	}
	
	public NodeIdentifier getSmallestIdentifiedAncestor(NodeIdentifier id) {
		return getSmallestIdentifiedAncestorIn(id, myTopLevelIdentifiedNodes, 
				null);
	}
	
	public void traverse(IdentifiedNodesVisitor<T> v) {
		traverseAndMap(myTopLevelIdentifiedNodes, v);
	}
	
	private void traverseAndMap(Set<NodeIdentifier> s, 
			IdentifiedNodesVisitor<T> m) {
		
		for (NodeIdentifier i : s) {
			m.visit(i, myDataMap.get(i));
			traverseAndMap(myIdentifiedNodes.get(i), m);
		}
	}
	
	private NodeIdentifier getSmallestIdentifiedAncestorIn(NodeIdentifier id,
			Set<NodeIdentifier> possibleAncestors, List<NodeIdentifier> path) {
		
		NodeIdentifier result;
		
		try {
			NodeIdentifier containedIn = getContainer(id, possibleAncestors);
			
			try {
				if (path != null) {
					path.add(containedIn);
				}
				
				result = getSmallestIdentifiedAncestorIn(id,
						myIdentifiedNodes.get(containedIn), path);
			}
			catch (NoSuchElementException e) {
				result = containedIn;
			}
		}
		catch (NoSuchElementException e) {
			throw new NoSuchElementException();
		}
		
		return result;
	}
	
	public void put(NodeIdentifier id, T data) {
		if (id.getRoot() != myRoot) {
			throw new IllegalArgumentException("id belongs to a PExp " +
					"different from the one that is the root of this " + 
					this.getClass() + ".");
		}
		
		if (!myDataMap.containsKey(id)) { 
			Set<NodeIdentifier> nodeChildren = doTopLevelStuff(id, data);
			Set<NodeIdentifier> layer;
			try {
				NodeIdentifier container = getSmallestIdentifiedAncestorIn(id, 
						myTopLevelIdentifiedNodes, null);
				
				layer = myIdentifiedNodes.get(container);
			}
			catch (NoSuchElementException e) {
				layer = myTopLevelIdentifiedNodes;
			}
			
			addToLayer(id, nodeChildren, layer);
		}
	}
	
	private void addToLayer(NodeIdentifier newID, 
			Set<NodeIdentifier> newIDChildren, Set<NodeIdentifier> layer) {

		Iterator<NodeIdentifier> layerIter = layer.iterator();
		NodeIdentifier sibling;
		while (layerIter.hasNext()) {
			sibling = layerIter.next();
			if (sibling.inside(newID)) {
				layerIter.remove();
				newIDChildren.add(sibling);
			}
		}
		
		layer.add(newID);
	}
	
	public T getData(NodeIdentifier id) {
		if (!myDataMap.containsKey(id)) {
			throw new NoSuchElementException();
		}
		
		return myDataMap.get(id);
	}
	
	private Set<NodeIdentifier> doTopLevelStuff(NodeIdentifier id, T data) {
		Set<NodeIdentifier> result = new HashSet<NodeIdentifier>();
		
		myIdentifiedNodes.put(id, result);
		myDataMap.put(id, data);
		
		return result;
	}
	
	private NodeIdentifier getContainer(NodeIdentifier id, 
				Set<NodeIdentifier> setToSearch) {
		
		NodeIdentifier containedIn = null;
		
		NodeIdentifier child;
		Iterator<NodeIdentifier> children = setToSearch.iterator();
		while (containedIn == null && children.hasNext()) {
			child = children.next();
			
			if (id.inside(child)) {
				containedIn = child;
			}
		}
		
		if (containedIn == null) {
			throw new NoSuchElementException();
		}
		
		return containedIn;
	}
}
