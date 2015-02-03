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
package edu.clemson.cs.r2jt.typeandpopulate;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

public class AlphaEquivalencyChecker extends SymmetricBoundVariableVisitor {

    private static final int POOL_SIZE = 3;

    /**
     * <p>An object pool to cut down on the creation of 
     * AlphaEquivalencyCheckers.</p>
     */
    private final Deque<AlphaEquivalencyChecker> myCheckerPool;

    private boolean myResult;

    public AlphaEquivalencyChecker() {
        myCheckerPool = new ArrayDeque<AlphaEquivalencyChecker>(POOL_SIZE);

        for (int i = 0; i < POOL_SIZE; i++) {
            myCheckerPool.push(new AlphaEquivalencyChecker(myCheckerPool));
        }
    }

    private AlphaEquivalencyChecker(Deque<AlphaEquivalencyChecker> pool) {
        myCheckerPool = pool;
    }

    private AlphaEquivalencyChecker(boolean dummy) {
        myCheckerPool = new ArrayDeque<AlphaEquivalencyChecker>();
    }

    public boolean getResult() {
        return myResult;
    }

    @Override
    public void reset() {
        super.reset();
        myResult = true;
    }

    @Override
    public boolean beginMTFunctionApplication(MTFunctionApplication t1,
            MTFunctionApplication t2) {

        //myResult = (t1.getName().equals(t2.getName()));

        // Not sure if this is the right fix.... -BD
        myResult = (t1.getHashCode() == t2.getHashCode());

        return myResult;
    }

    @Override
    public boolean beginMTNamed(MTNamed t1, MTNamed t2) {
        //TODO: This doesn't deal correctly with multiple appearances of a
        //variable

        if (!t1.name.equals(t2.name)) {
            MTType t1Value;
            MTType t2Value;
            try {
                t1Value = getInnermostBinding1(t1.name);
                t2Value = getInnermostBinding2(t2.name);

                AlphaEquivalencyChecker alphaEq = getChecker();
                alphaEq.visit(t1Value, t2Value);
                myResult = alphaEq.getResult();
                returnChecker(alphaEq);
            }
            catch (NoSuchElementException nsee) {
                //We have no information about the named types--but we know they
                //aren't named the same, so...
                myResult = false;
            }
        }

        return myResult;
    }

    @Override
    public boolean beginMTProper(MTProper t1, MTProper t2) {
        if (t1 != t2) {
            myResult = false;
        }

        return myResult;
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
        myResult = false;
        return myResult;
    }

    private AlphaEquivalencyChecker getChecker() {
        AlphaEquivalencyChecker result;

        if (myCheckerPool.isEmpty()) {
            result = new AlphaEquivalencyChecker(false);
        }
        else {
            result = myCheckerPool.pop();
        }

        return result;
    }

    private void returnChecker(AlphaEquivalencyChecker c) {
        if (myCheckerPool.size() < POOL_SIZE) {
            myCheckerPool.push(c);
        }
    }
}
