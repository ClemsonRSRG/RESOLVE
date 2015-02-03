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

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import edu.clemson.cs.r2jt.verification.AssertiveCode;

public class VCProvedException extends ProverException {

    private static final long serialVersionUID = -167079179043597290L;

    private Deque<Object> mySteps = new LinkedList<Object>();

    private VC myOriginalVC;

    public VCProvedException(Metrics metrics) {
        super(metrics);
    }

    public VCProvedException(String msg, AssertiveCode code, Metrics metrics) {
        super(msg, code, metrics);
    }

    public void addStep(Object s) {
        mySteps.add(s);
    }

    public void setOriginal(VC vc) {
        myOriginalVC = vc;
    }

    public void setMetrics(Metrics m) {
        myMetrics = m;
    }

    public String toString() {
        String retval = "";

        if (myOriginalVC != null) {
            retval +=
                    "==== Proof for VC " + myOriginalVC.getName() + " ====\n\n";

            retval += myOriginalVC + "\n";
        }

        Iterator<Object> iter = mySteps.descendingIterator();
        while (iter.hasNext()) {
            retval += iter.next();
        }

        retval += "Done.\n\n";

        return retval;
    }
}
