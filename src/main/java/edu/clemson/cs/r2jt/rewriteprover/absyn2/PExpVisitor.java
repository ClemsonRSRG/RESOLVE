/**
 * PExpVisitor.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover.absyn2;

public abstract class PExpVisitor {

    public void beginPExp(PExpr p) {}

    public void beginPSymbol(PSymbol p) {}

    public void beginPrefixPSymbol(PSymbol p) {}

    public void beginInfixPSymbol(PSymbol p) {}

    public void beginOutfixPSymbol(PSymbol p) {}

    public void beginPostfixPSymbol(PSymbol p) {}

    public void beginChildren(PExpr p) {}

    public void fencepostPSymbol(PSymbol p) {}

    public void fencepostPrefixPSymbol(PSymbol p) {}

    public void fencepostInfixPSymbol(PSymbol p) {}

    public void fencepostOutfixPSymbol(PSymbol p) {}

    public void fencepostPostfixPSymbol(PSymbol p) {}

    public void endChildren(PExpr p) {}

    public void endPExp(PExpr p) {}

    public void endPSymbol(PSymbol p) {}

    public void endPrefixPSymbol(PSymbol p) {}

    public void endInfixPSymbol(PSymbol p) {}

    public void endOutfixPSymbol(PSymbol p) {}

    public void endPostfixPSymbol(PSymbol p) {}

}
