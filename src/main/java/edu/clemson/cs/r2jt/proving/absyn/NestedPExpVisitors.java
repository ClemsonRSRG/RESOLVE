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
package edu.clemson.cs.r2jt.proving.absyn;

public class NestedPExpVisitors extends PExpVisitor {

    private final PExpVisitor myOuterVisitor;
    private final PExpVisitor myInnerVisitor;

    public NestedPExpVisitors(PExpVisitor outer, PExpVisitor inner) {
        myOuterVisitor = outer;
        myInnerVisitor = inner;
    }

    public void beginPExp(PExp p) {
        myOuterVisitor.beginPExp(p);
        myInnerVisitor.beginPExp(p);
    }

    public void beginPSymbol(PSymbol p) {
        myOuterVisitor.beginPSymbol(p);
        myInnerVisitor.beginPSymbol(p);
    }

    public void beginPrefixPSymbol(PSymbol p) {
        myOuterVisitor.beginPrefixPSymbol(p);
        myInnerVisitor.beginPrefixPSymbol(p);
    }

    public void beginInfixPSymbol(PSymbol p) {
        myOuterVisitor.beginInfixPSymbol(p);
        myInnerVisitor.beginInfixPSymbol(p);
    }

    public void beginOutfixPSymbol(PSymbol p) {
        myOuterVisitor.beginOutfixPSymbol(p);
        myInnerVisitor.beginOutfixPSymbol(p);
    }

    public void beginPostfixPSymbol(PSymbol p) {
        myOuterVisitor.beginPostfixPSymbol(p);
        myInnerVisitor.beginPostfixPSymbol(p);
    }

    public void beginPAlternatives(PAlternatives p) {
        myOuterVisitor.beginPAlternatives(p);
        myInnerVisitor.beginPAlternatives(p);
    }

    public void beginPLambda(PLambda p) {
        myOuterVisitor.beginPLambda(p);
        myInnerVisitor.beginPLambda(p);
    }

    public void fencepostPSymbol(PSymbol p) {
        myInnerVisitor.fencepostPSymbol(p);
        myOuterVisitor.fencepostPSymbol(p);
    }

    public void fencepostPrefixPSymbol(PSymbol p) {
        myInnerVisitor.fencepostPrefixPSymbol(p);
        myOuterVisitor.fencepostPrefixPSymbol(p);
    }

    public void fencepostInfixPSymbol(PSymbol p) {
        myInnerVisitor.fencepostInfixPSymbol(p);
        myOuterVisitor.fencepostInfixPSymbol(p);
    }

    public void fencepostOutfixPSymbol(PSymbol p) {
        myInnerVisitor.fencepostOutfixPSymbol(p);
        myOuterVisitor.fencepostOutfixPSymbol(p);
    }

    public void fencepostPostfixPSymbol(PSymbol p) {
        myInnerVisitor.fencepostPostfixPSymbol(p);
        myOuterVisitor.fencepostPostfixPSymbol(p);
    }

    public void fencepostPAlternatives(PAlternatives p) {
        myInnerVisitor.fencepostPAlternatives(p);
        myOuterVisitor.fencepostPAlternatives(p);
    }

    public void endPExp(PExp p) {
        myInnerVisitor.endPExp(p);
        myOuterVisitor.endPExp(p);
    }

    public void endPSymbol(PSymbol p) {
        myInnerVisitor.endPSymbol(p);
        myOuterVisitor.endPSymbol(p);
    }

    public void endPrefixPSymbol(PSymbol p) {
        myInnerVisitor.endPrefixPSymbol(p);
        myOuterVisitor.endPrefixPSymbol(p);
    }

    public void endInfixPSymbol(PSymbol p) {
        myInnerVisitor.endInfixPSymbol(p);
        myOuterVisitor.endInfixPSymbol(p);
    }

    public void endOutfixPSymbol(PSymbol p) {
        myInnerVisitor.endOutfixPSymbol(p);
        myOuterVisitor.endOutfixPSymbol(p);
    }

    public void endPostfixPSymbol(PSymbol p) {
        myInnerVisitor.endPostfixPSymbol(p);
        myOuterVisitor.endPostfixPSymbol(p);
    }

    public void endPAlternatives(PAlternatives p) {
        myInnerVisitor.endPAlternatives(p);
        myOuterVisitor.endPAlternatives(p);
    }

    public void endPLambda(PLambda p) {
        myInnerVisitor.endPLambda(p);
        myOuterVisitor.endPLambda(p);
    }
}
