package edu.clemson.cs.r2jt.proving;

import edu.clemson.cs.r2jt.absyn.Exp;

/**
 * <p>Replaces expressions that exactly match (via <code>equals</code>()) a
 * provided expression, replacing it with a clone of a provided replacement.</p>
 * 
 * @author H. Smith
 */
public class DirectReplace implements MatchReplace {

    private final Exp myFind, myReplace;

    /**
     * <p>Creates a new <code>DirectReplace</code> which will replace instances
     * of <code>find</code> with clones of <code>replace</code>.</p>
     * 
     * @param find The thing to match against with <code>equals</code>().
     * @param replace The thing to replace matches with.
     */
    public DirectReplace(final Exp find, final Exp replace) {
        myFind = find;
        myReplace = replace;
    }

    public boolean couldReplace(Exp e) {
        return (e.equivalent(myFind));
    }

    public Exp getReplacement() {
        return Exp.copy(myReplace);
    }

    public String toString() {
        return myFind.toString(0) + " --> " + myReplace.toString(0);
    }

    @Override
    public Exp getExpansionTemplate() {
        return Exp.copy(myFind);
    }

    @Override
    public Exp getPattern() {
        return Exp.copy(myReplace);
    }
}
