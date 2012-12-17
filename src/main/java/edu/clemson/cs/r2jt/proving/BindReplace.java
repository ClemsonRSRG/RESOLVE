package edu.clemson.cs.r2jt.proving;

import java.util.Map;

import edu.clemson.cs.r2jt.absyn.Exp;

/**
 * <p>Uses a provided search pattern to match expressions that will bind (via 
 * <code>Utilities.bind</code>()) against that pattern and replace them with a
 * provided replacement pattern that will be expanded using the bindings from 
 * the original match.</p>
 * 
 * @author H. Smith
 */
public class BindReplace implements MatchReplace {

    private Exp myFindPattern, myReplacePattern;
    private Map<Exp, Exp> myBindings;

    /**
     * <p>Creates a new <code>BindReplace</code> that will replace expressions 
     * that can be bound to <code>findPattern</code> with copies of the 
     * provided replace pattern in which expansions have been made based on the
     * binding step.</p>
     * 
     * @param findPattern The pattern to bind with.
     * @param replacePattern The pattern to expand as a replacement.
     */
    public BindReplace(Exp findPattern, Exp replacePattern) {

        myFindPattern = findPattern;
        myReplacePattern = replacePattern;
    }

    public boolean couldReplace(Exp e) {

        myBindings = Utilities.newBind(myFindPattern, e);

        return (myBindings != null);
    }

    public Exp getReplacement() {
        return myReplacePattern.substitute(myBindings);
    }

    public String toString() {
        return "Replace " + myFindPattern.toString(0) + " with "
                + myReplacePattern.toString(0);
    }

    @Override
    public Exp getExpansionTemplate() {
        return Exp.copy(myReplacePattern);
    }

    @Override
    public Exp getPattern() {
        return Exp.copy(myFindPattern);
    }
}
