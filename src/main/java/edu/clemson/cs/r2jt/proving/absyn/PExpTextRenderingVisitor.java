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
