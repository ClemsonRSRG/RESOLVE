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
package edu.clemson.cs.r2jt.proving2.model;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PExpVisitor;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author hamptos
 */
public class ProverModelVisitor extends PExpVisitor {

    private Deque<Integer> myIndices = new LinkedList<Integer>();
    private LinkedList<Site> myIDs = new LinkedList<Site>();
    private Site myRoot;

    private final PerVCProverModel myModel;
    private Conjunct myConjunct;

    public ProverModelVisitor(PerVCProverModel model) {
        myModel = model;
        myConjunct = null;
    }

    public void setConjunct(Conjunct c) {
        myConjunct = c;
    }

    protected Site getID() {
        return myIDs.peek();
    }

    protected Site getID(int levels) {
        return myIDs.get(levels);
    }

    protected int getDepth() {
        return myIDs.size();
    }

    @Override
    public final void beginPExp(PExp p) {

        myIndices.push(0); //We start at the zeroth child

        Site s;
        if (myRoot == null) {
            myRoot = new Site(myModel, myConjunct, p);
            s = myRoot;
        }
        else {
            s = buildID(p, myIndices);
        }
        myIDs.push(s);

        doBeginPExp(p);
    }

    private Site buildID(PExp p, Deque<Integer> indices) {
        List<Integer> idIndices = new ArrayList<Integer>(indices.size() - 1);

        //The ID should reflect all but the last index
        Iterator<Integer> indicesIter = indices.descendingIterator();
        for (int i = 0; i < (indices.size() - 1); i++) {
            idIndices.add(indicesIter.next());
        }

        return new Site(myModel, myConjunct, idIndices, p);
    }

    public void doBeginPExp(PExp p) {

    }

    @Override
    public final void endPExp(PExp p) {
        doEndPExp(p);

        if (!p.equals(myRoot.exp)) {
            //We're not visiting any more children at this level (because the
            //level just ended!)
            myIndices.pop();

            //Increment to the next potential child index
            int i = myIndices.pop();
            myIndices.push(i + 1);
        }

        //We just left a PExp, so get rid of its ID
        myIDs.pop();

        if (p.equals(myRoot.exp)) {
            myRoot = null;
            myIndices.pop();
        }
    }

    public void doEndPExp(PExp p) {

    }
}
