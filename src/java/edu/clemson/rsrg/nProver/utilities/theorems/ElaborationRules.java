/*
 * ElaborationRules.java
 * ---------------------------------
 * Copyright (c) 2024
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.nProver.utilities.theorems;

import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.rsrg.typeandpopulate.entry.TheoremEntry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ElaborationRules {

	private List<TheoremEntry> myRelevantTheorems;

	// private Exp resultantExpression;

	private List<ElaborationRule> myElaborationRules;

	// constructor
	public ElaborationRules(List<TheoremEntry> relevantTheorems) {
		myRelevantTheorems = relevantTheorems;
	}

	// create elaboration rules out of the list of relevant theorem
	// TODO when a theorem has only one expression, this should be treated as a
	// special case
	public List<ElaborationRule> createElaborationRules() {
		List<Exp> myTheoremExpressions;
		// list of sub sub expressions for theorems with one clause
		List<Exp> myTheoremSubExpressions;
		// pick each relevant theorem one at a time
		for (TheoremEntry t : myRelevantTheorems) {
			// get the sub expressions out of t
			myTheoremExpressions = t.getAssertion().getSubExpressions();

			// System.out.println("The size of the clause is: " +
			// myTheoremExpressions.size());
			// for one expression theorem, things will be different
			if (myTheoremExpressions.size() == 1) {
				// break down the expression further it is assumed it will be at index 0
				myTheoremSubExpressions = myTheoremExpressions.get(0).getSubExpressions();

				for (Exp exp : myTheoremSubExpressions) {
					List<Exp> copyOfMyTheoremSubExpressions = myTheoremExpressions.get(0)
							.getSubExpressions();
					// x1*x2 = x2 * x1 will create two rules with each sub exp becoming the
					// precursor
					if (isDeterministic(copyOfMyTheoremSubExpressions, exp)) {
						// exp here has to be the whole theorem assertion and not only part of
						// the expression
						ElaborationRule rule = new ElaborationRule(
								copyOfMyTheoremSubExpressions, t.getAssertion());
						myElaborationRules.add(rule);
					}
				}

			} else {
				// build the elaboration rule out of each expression by making it a resultant
				// and the reset precursors
				for (Exp te : myTheoremExpressions) {
					List<Exp> copyOfTheoremExpressions = t.getAssertion().getSubExpressions();
					// check if the rule will be deterministic, and for the moment, if not
					// deterministic ignore it
					if (isDeterministic(copyOfTheoremExpressions, te)) {
						// System.out.println("It was determinant");
						ElaborationRule rule = new ElaborationRule(copyOfTheoremExpressions,
								te);
						myElaborationRules.add(rule);
					}
				}
			}
		}
		return myElaborationRules;
	}

	// all the variables in the resultant clause can be found in the precursor
	// clauses, then it is deterministic
	public boolean isDeterministic(List<Exp> theoremExpressionList, Exp resultantExpression) {
		// remove the resultant expression from the precursor expressions
		theoremExpressionList.remove(resultantExpression);
		// get the resultant sub expressions (will be only constants and variables)
		List<Exp> resultantSubExpressions = resultantExpression.getSubExpressions();
		// Declare the set that will collect all variables in the precursor expressions
		Set<Exp> collectionOfPrecursorVars;
		// a flag to indicate if all variables in the resultant expression are in the
		// precursor expressions
		Boolean fullyContained = false;

		// collect all the variables in the precursor expressions
		collectionOfPrecursorVars = collectVariables(theoremExpressionList);

		// check if collection of precursor vars contain all e's, if so return true
		for (Exp e : resultantSubExpressions) {
			if (collectionOfPrecursorVars.contains(e)) {
				fullyContained = true;
			} else {
				fullyContained = false;
				break;
			}
		}
		if (fullyContained) {
			return true;
		} else {
			return false;
		}

	}

	// get all the elaboration rules created
	// TODO: This is not right work on this
	public List<ElaborationRule> getMyElaborationRules() {
		return myElaborationRules;
	}

	public Set<Exp> collectVariables(List<Exp> precursorExpList) {
		Set<Exp> setOfPrecursorVars = new HashSet<>();
		for (Exp e : precursorExpList) {
			for (Exp ve : e.getSubExpressions()) {
				if (ve.getClass().getSimpleName() == "VarExp") {
					setOfPrecursorVars.add(ve);
				}
			}
		}
		return setOfPrecursorVars;
	}
}
