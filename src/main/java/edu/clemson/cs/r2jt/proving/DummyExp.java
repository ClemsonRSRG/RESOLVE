package edu.clemson.cs.r2jt.proving;

import java.util.Map;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.ResolveConceptualVisitor;
import edu.clemson.cs.r2jt.absyn.TypeResolutionVisitor;
import edu.clemson.cs.r2jt.analysis.TypeResolutionException;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.type.Type;

/**
 * A simple Exp to hack around a bug quickly.  MatchApplicator should be fixed
 * so that this is not necessary.
 * 
 * @author H. Smith
 *
 */
public class DummyExp extends Exp {

	private Exp myWrappedExpression;
	
	public DummyExp(Exp e) {
		myWrappedExpression = e;
	}
	
	public DummyExp() {
		myWrappedExpression = null;
	}
	
	public Exp getWrappedExpression() {
		return myWrappedExpression;
	}
	
	public void setWrappedExpression(Exp e) {
		myWrappedExpression = e;
	}
	
	@Override
	public void accept(ResolveConceptualVisitor v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Type accept(TypeResolutionVisitor v) throws TypeResolutionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String asString(int indent, int increment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean containsVar(String varName, boolean IsOldExp) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Location getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Exp> getSubExpressions() {
		List<Exp> list = new List<Exp>();
		
		if (myWrappedExpression != null) {
			list.add(myWrappedExpression);
		}
		
		return list;
	}

	@Override
	public void setSubExpression(int index, Exp e) {
		myWrappedExpression = e;
	}

	@Override
	protected Exp substituteChildren(Map<Exp, Exp> substitutions) {
		// TODO Auto-generated method stub
		return null;
	}

}
