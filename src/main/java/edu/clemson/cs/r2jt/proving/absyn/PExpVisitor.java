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
