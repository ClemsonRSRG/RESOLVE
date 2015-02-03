/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
            if (Utilities.isLiteralTrue(curExp)
                    || Utilities.isSymmetricEquality(curExp)) {
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
        for (int curUniqueIndex = 0; curUniqueIndex < size(); curUniqueIndex++) {

            curExp = get(curUniqueIndex);

            for (int compareIndex = curUniqueIndex + 1; compareIndex < size(); compareIndex++) {

                while (compareIndex < size()
                        && curExp.equivalent(get(compareIndex))) {
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
            while (retval && myElements.hasNext()) {
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
            while (retval && myElements.hasNext() && oElements.hasNext()) {
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
