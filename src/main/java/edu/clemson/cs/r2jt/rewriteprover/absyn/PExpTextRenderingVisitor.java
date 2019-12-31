/*
 * PExpTextRenderingVisitor.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover.absyn;

import java.io.IOException;

public class PExpTextRenderingVisitor extends PExpVisitor {

    private final Appendable myOutput;

    private PAlternatives myEncounteredAlternative;
    private PExp myEncounteredResult;

    public PExpTextRenderingVisitor(Appendable w) {
        myOutput = w;
    }

    public void beginPExp(PExp p) {
        if (myEncounteredAlternative != null) {
            if (myEncounteredResult == null) {
                myEncounteredResult = p;
            }
            else {
                try {
                    myEncounteredResult = null;
                    myOutput.append(", if ");
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void beginPrefixPSymbol(PSymbol p) {
        try {
            myOutput.append(p.name);

            if (p.arguments.size() > 0) {
                myOutput.append("(");
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void beginInfixPSymbol(PSymbol p) {
        try {
            myOutput.append("(");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void beginOutfixPSymbol(PSymbol p) {
        try {
            myOutput.append(p.leftPrint);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void beginPostfixPSymbol(PSymbol p) {
        try {
            if (p.arguments.size() > 0) {
                myOutput.append("(");
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void beginPAlternatives(PAlternatives p) {
        try {
            myOutput.append("{");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void beginPLambda(PLambda l) {
        try {
            myOutput.append("lambda (");

            boolean first = true;
            for (PLambda.Parameter p : l.parameters) {
                if (first) {
                    first = false;
                }
                else {
                    myOutput.append(", ");
                }

                myOutput.append("" + p);
            }

            myOutput.append(").");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void fencepostPrefixPSymbol(PSymbol p) {
        try {
            myOutput.append(", ");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void fencepostInfixPSymbol(PSymbol p) {
        try {
            myOutput.append(" " + p.name + " ");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void fencepostOutfixPSymbol(PSymbol p) {
        try {
            myOutput.append(", ");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void fencepostPostfixPSymbol(PSymbol p) {
        try {
            myOutput.append(", ");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void fencepostPAlternatives(PAlternatives p) {
        try {
            myOutput.append("; ");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void endPrefixPSymbol(PSymbol p) {
        try {
            if (p.arguments.size() > 0) {
                myOutput.append(")");
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void endInfixPSymbol(PSymbol p) {
        try {
            myOutput.append(")");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void endOutfixPSymbol(PSymbol p) {
        try {
            myOutput.append(p.rightPrint);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void endPostfixPSymbol(PSymbol p) {
        try {
            if (p.arguments.size() > 0) {
                myOutput.append(")");
            }
            myOutput.append(p.name);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void endPAlternatives(PAlternatives p) {
        try {
            myOutput.append(", otherwise}");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
