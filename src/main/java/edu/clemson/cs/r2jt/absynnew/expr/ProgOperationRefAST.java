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
package edu.clemson.cs.r2jt.absynnew.expr;

import org.antlr.v4.runtime.Token;

import java.util.List;

/**
 * <p>A <code>ProgOperationRefAST</code> represents a reference within a
 * subexpression to some operation.</p>
 *
 * <p>Every call in the ast, including the 'official'
 * {@link edu.clemson.cs.r2jt.absynnew.stmt.CallAST}s should ultimately reference
 * this class. Even the primitive operations <pre>+, -, *</pre> should all
 * get converted into <code>ProgOperationRefAST</code>s with a name
 * appropriate for referencing their corresponding, formally specified
 * template operations.</p>
 */
public class ProgOperationRefAST extends ProgExprAST {

    private final Token myQualifier, myName;
    private final List<ProgExprAST> myArguments;

    public ProgOperationRefAST(Token start, Token stop, Token qualifier,
            Token name, List<ProgExprAST> arguments) {
        super(start, stop);
        myName = name;
        myQualifier = qualifier;
        myArguments = arguments;
    }

    public Token getName() {
        return myName;
    }

    public Token getQualifier() {
        return myQualifier;
    }

    public List<ProgExprAST> getArguments() {
        return myArguments;
    }

    @Override
    public boolean isLiteral() {
        return false;
    }
}