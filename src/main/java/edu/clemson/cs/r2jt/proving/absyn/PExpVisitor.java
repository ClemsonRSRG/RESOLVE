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

public abstract class PExpVisitor {

    public void beginPExp(PExp p) {}

    public void beginPSymbol(PSymbol p) {}

    public void beginPrefixPSymbol(PSymbol p) {}

    public void beginInfixPSymbol(PSymbol p) {}

    public void beginOutfixPSymbol(PSymbol p) {}

    public void beginPostfixPSymbol(PSymbol p) {}

    public void beginPAlternatives(PAlternatives p) {}

    public void beginPLambda(PLambda p) {}

    public void beginChildren(PExp p) {}

    public void fencepostPSymbol(PSymbol p) {}

    public void fencepostPrefixPSymbol(PSymbol p) {}

    public void fencepostInfixPSymbol(PSymbol p) {}

    public void fencepostOutfixPSymbol(PSymbol p) {}

    public void fencepostPostfixPSymbol(PSymbol p) {}

    public void fencepostPAlternatives(PAlternatives p) {}

    public void endChildren(PExp p) {}

    public void endPExp(PExp p) {}

    public void endPSymbol(PSymbol p) {}

    public void endPrefixPSymbol(PSymbol p) {}

    public void endInfixPSymbol(PSymbol p) {}

    public void endOutfixPSymbol(PSymbol p) {}

    public void endPostfixPSymbol(PSymbol p) {}

    public void endPAlternatives(PAlternatives p) {}

    public void endPLambda(PLambda p) {}
}
