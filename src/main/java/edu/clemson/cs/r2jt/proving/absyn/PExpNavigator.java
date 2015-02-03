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
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
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
package edu.clemson.cs.r2jt.proving.absyn;

import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * <p>A <code>PExpNavigator</code> provides a mechanism for iterating over each
 * node in the tree-structure of a 
 * {@link edu.clemson.cs.r2jt.proving.absyn.PExp PExp} by moving a cursor from
 * node to node.  The order of this traversal is undefined.  Modified versions 
 * of the original <code>PExp</code> can be obtained based on the current cursor
 * position, i.e. by replacing the last visited node with another.</p> 
 */
public class PExpNavigator {

    private Deque<PExpSubexpressionIterator> myNavigationStack =
            new LinkedList<PExpSubexpressionIterator>();

    private final PExp myOriginalExpression;

    private boolean myReturnedTopLevelFlag = false;

    public PExpNavigator(PExp expression) {
        myNavigationStack.push(expression.getSubExpressionIterator());
        myOriginalExpression = expression;
    }

    public boolean hasNext() {

        //TODO : This actually violates the contract for this component--a call
        //       to hasNext() breaks the functionality of replaceLast() until
        //       next() is called
        while (!(myNavigationStack.isEmpty() || myNavigationStack.peek()
                .hasNext())) {

            myNavigationStack.pop();
        }

        return !myNavigationStack.isEmpty();
    }

    public PExp next() {
        PExp retval;

        if (myReturnedTopLevelFlag) {
            while (!(myNavigationStack.isEmpty() || myNavigationStack.peek()
                    .hasNext())) {

                myNavigationStack.pop();
            }

            if (myNavigationStack.isEmpty()) {
                if (myReturnedTopLevelFlag) {
                    throw new NoSuchElementException();
                }
                else {
                    //TODO : This looks like dead code to me... should be 
                    //removed and tested.
                    retval = myOriginalExpression;
                    myReturnedTopLevelFlag = true;
                }
            }
            else {
                retval = myNavigationStack.peek().next();
                myNavigationStack.push(retval.getSubExpressionIterator());
            }
        }
        else {
            retval = myOriginalExpression;
            myReturnedTopLevelFlag = true;
        }

        return retval;
    }

    public PExp replaceLast(PExp e) {
        PExp retval = e;

        boolean first = true;
        for (PExpSubexpressionIterator i : myNavigationStack) {
            if (first) {
                first = false;
            }
            else {
                retval = i.replaceLast(retval);
            }
        }

        return retval;
    }
}
