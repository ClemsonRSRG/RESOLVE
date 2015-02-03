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
package edu.clemson.cs.r2jt.type;

import edu.clemson.cs.r2jt.absyn.InfixExp;
import edu.clemson.cs.r2jt.analysis.TypeResolutionException;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.scope.Binding;
import edu.clemson.cs.r2jt.scope.ScopeID;

/**
 * @author Chuck
 * This is the built-in class for is_in statements. This statement
 * return type will be BooleanType
 *
 */
public class IsInType extends Type {

    private Type retType = BooleanType.INSTANCE;

    private PosSymbol name;

    private List<Type> args = new List<Type>();

    public IsInType(PosSymbol name, List<Type> args) {
        this.name = name;
        this.args = args;
    }

    public void setArgs(List<Type> args) {
        this.args = args;
    }

    public Type getRetType(InfixExp exp) throws TypeResolutionException {
        //if(exp.getBType() instanceof IsInType){
        //Type t1 = getMathExpType(exp.getLeft());
        //Type t2 = getMathExpType(exp.getRight());
        return retType;
    }

    @Override
    public String asString() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TypeName getProgramName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getRelativeName(Location loc) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Type instantiate(ScopeID sid, Binding binding) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Type toMath() {
        // TODO Auto-generated method stub
        return null;
    }
}
