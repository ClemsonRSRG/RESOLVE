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
package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

/**
 *
 * @author hamptos
 */
public class DebugIterator implements Iterator {

    private final Iterator myBaseIterator;
    private final String myStartMessage;
    private final String myEndMessage;

    private boolean myGotFirstFlag = false;
    private boolean myFoundEndFlag = false;

    public DebugIterator(Iterator i, String start, String end) {
        myBaseIterator = i;
        myStartMessage = start;
        myEndMessage = end;
    }

    @Override
    public boolean hasNext() {
        boolean result = myBaseIterator.hasNext();

        if (!result && !myFoundEndFlag) {
            System.out.println(myEndMessage);
            myFoundEndFlag = true;
        }

        return myBaseIterator.hasNext();
    }

    @Override
    public Object next() {
        if (!myGotFirstFlag) {
            System.out.println(myStartMessage);
            myGotFirstFlag = true;
        }

        return myBaseIterator.next();
    }

    @Override
    public void remove() {
        myBaseIterator.remove();
    }
}
