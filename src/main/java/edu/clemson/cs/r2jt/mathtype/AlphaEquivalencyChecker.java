package edu.clemson.cs.r2jt.mathtype;

import java.util.NoSuchElementException;

public class AlphaEquivalencyChecker extends SymmetricBoundVariableVisitor {

    @Override
    public boolean beginMTNamed(MTNamed t1, MTNamed t2) {
        //TODO: This doesn't deal correctly with multiple appearances of a
        //variable

        if (!t1.name.equals(t2.name)) {
            MTType t1Value = null;
            MTType t2Value = null;
            try {
                t1Value = getInnermostBinding1(t1.name);
                t2Value = getInnermostBinding2(t2.name);
            }
            catch (NoSuchElementException nsee) {
                //We have no information about the named types--but we know they
                //aren't named the same, so...
                throw new IllegalArgumentException(new TypeMismatchException(
                        t1, t2));
            }

            SymmetricVisitor alphaEq = new AlphaEquivalencyChecker();

            alphaEq.visit(t1Value, t2Value);
        }

        return true;
    }

    @Override
    public boolean beginMTProper(MTProper t1, MTProper t2) {
        if (t1 != t2) {
            throw new IllegalArgumentException(
                    new TypeMismatchException(t1, t2));
        }

        return true;
    }

    @Override
    public boolean beginMTSetRestriction(MTSetRestriction t1,
            MTSetRestriction t2) {

        //TODO:
        //We really need a way to check the expression embedded in each set
        //restriction for alpha-equivalency.  We don't have one, so for the
        //moment, we throw an exception
        throw new RuntimeException("Can't check set restrictions for "
                + "alpha equivalency.");
    }

    @Override
    public boolean mismatch(MTType t1, MTType t2) {
        throw new IllegalArgumentException(new TypeMismatchException(t1, t2));
    }
}
