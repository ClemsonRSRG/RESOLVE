package edu.clemson.cs.r2jt.proving;

import java.util.Map;

import edu.clemson.cs.r2jt.proving.absyn.BindingException;
import edu.clemson.cs.r2jt.proving.absyn.PExp;

/**
 * <p>Uses a provided search pattern to match expressions that will bind against
 * that pattern and replace them with a provided replacement pattern that will 
 * be expanded using the bindings from the original match.</p>
 * 
 * <p><strong>N.B.:</strong>  This is intended as a drop-in replacement for
 * {@link edu.clemson.cs.r2jt.proving.BindReplace BindReplace} except that it
 * operates on {@link edu.clemson.cs.r2jt.proving.absyn.PExp PExp}s rather than
 * {@link edu.clemson.cs.r2jt.absyn.Exp Exp}s.  When the new prover is complete
 * and well-tested, <code>BindReplace</code> should be removed entirely and
 * this class should be renamed.</p>
 */
public class NewBindReplace implements NewMatchReplace {

    private PExp myFindPattern, myReplacePattern;
    private Map<PExp, PExp> myBindings;

    /**
     * <p>Creates a new <code>BindReplace</code> that will replace expressions 
     * that can be bound to <code>findPattern</code> with copies of the 
     * provided replace pattern in which expansions have been made based on the
     * binding step.</p>
     * 
     * @param findPattern The pattern to bind with.
     * @param replacePattern The pattern to expand as a replacement.
     */
    public NewBindReplace(PExp findPattern, PExp replacePattern) {

        myFindPattern = findPattern;
        myReplacePattern = replacePattern;
    }

    public boolean couldReplace(PExp e) {
        myBindings = null;

        try {
            myBindings = myFindPattern.bindTo(e);
        }
        catch (BindingException ex) {

        }

        return (myBindings != null);
    }

    public PExp getReplacement() {
        return myReplacePattern.substitute(myBindings);
    }

    public String toString() {
        return "Replace " + myFindPattern + " with " + myReplacePattern;
    }

    @Override
    public PExp getExpansionTemplate() {
        return myReplacePattern;
    }

    @Override
    public PExp getPattern() {
        return myFindPattern;
    }
}
