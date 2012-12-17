package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

import edu.clemson.cs.r2jt.absyn.BetweenExp;
import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.InfixExp;
import edu.clemson.cs.r2jt.collections.List;

public class Conjuncts extends List<Exp> {
	private static final long serialVersionUID = -2390059781932222577L;

	public Conjuncts(Exp e) {
		splitIntoConjuncts(e);
	}
	
	public Conjuncts(List<Exp> exps) {
		for (Exp e : exps) {
			add(e);
		}
	}
	
	/**
	 * <p>Splits <code>e</code> into conjuncts (X <em>and</em> Y <em>and</em>
	 * Z <em>and</em> ...) by adding each conjunct to this list.</p>
	 * 
	 * @param e The expression to split into conjuncts.
	 */
	private void splitIntoConjuncts(Exp e) {
		if (e instanceof InfixExp && Utilities.isAndExp((InfixExp) e)) {
			InfixExp eAsInfix = (InfixExp) e;
			splitIntoConjuncts(eAsInfix.getLeft());
			splitIntoConjuncts(eAsInfix.getRight());
		}
		else if (e instanceof BetweenExp) {
			BetweenExp eAsBetween = (BetweenExp) e;
			List<Exp> subexpressions = eAsBetween.getLessExps();
			
			for (Exp sub : subexpressions) {
				splitIntoConjuncts(sub);
			}
		}
		else {
			add(e);
		}
	}
	

	/**
	 * <p>Eliminates expressions from <code>expressions</code> that are very
	 * obviously <code>true</code>.  Examples are the actual "true" value and
	 * equalities with the same thing on the left and right side.</p>
	 *  
	 * @param expressions The expressions to process.
	 */
	public void eliminateObviousConjunctsInPlace() {
		Exp curExp;
		Iterator<Exp> iter = iterator();
		while (iter.hasNext()) {
			curExp = iter.next();
			if (Utilities.isLiteralTrue(curExp) || 
					Utilities.isSymmetricEquality(curExp)) {
				iter.remove();
			}
		}
	}
	
	public void eliminateEquivalentConjunctsInPlace(Exp e) {
		Exp curExp;
		Iterator<Exp> iter = iterator();
		while (iter.hasNext()) {
			curExp = iter.next();
			if (curExp.equivalent(e)) {
				iter.remove();
			}
		}
	}
	
	public void eliminateRedundantConjuncts() {

		Exp curExp;
		for (int curUniqueIndex = 0; curUniqueIndex < size();
				curUniqueIndex++) {
			
			curExp = get(curUniqueIndex);
			
			for (int compareIndex = curUniqueIndex + 1; compareIndex < size();
					compareIndex++) {
				
				while (compareIndex < size() &&
						curExp.equivalent(get(compareIndex))) {
					remove(compareIndex);
				}
			}
		}
	}
	
	public boolean equivalent(List<Exp> otherConjuncts) {
		boolean retval = (otherConjuncts.size() == size());
		
		if (retval) {
			Iterator<Exp> myElements = iterator();
			Iterator<Exp> otherElements = otherConjuncts.iterator();
			while(retval && myElements.hasNext()) {
				retval = myElements.next().equivalent(otherElements.next());
			}
		}
		
		return retval;
	}
	
	public boolean equivalent(Exp e) {
		return equivalent(new Conjuncts(e));
	}
	
	public boolean equals(Object o) {
		boolean retval = o instanceof List<?>;
		
		if (retval) {
			List<?> otherList = (List<?>) o;
			
			Iterator<?> myElements = iterator();
			Iterator<?> oElements = otherList.iterator();
			while(retval && myElements.hasNext() && oElements.hasNext()) {
				retval = myElements.next().equals(oElements.next());
			}
			
			retval &= !(myElements.hasNext() || myElements.hasNext());
		}
		
		return retval;
	}
	
	public String toString() {
		String retval = "";
		
		boolean first = true;
		for (Exp e : this) {
			if (!first) {
				retval += " and \n";
			}
			retval += (e.toString(0));
			first = false;
		}
		
		retval += "\n";
		return retval;
	}
}
