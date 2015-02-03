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

import java.util.Map;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.proving.absyn.PExp;

public class Consequent extends ImmutableConjuncts {

    public Consequent(Exp e) {
        super(e);
    }

    public Consequent(PExp e) {
        super(e);
    }

    public Consequent(Iterable<PExp> i) {
        super(i);
    }

    @Override
    public Consequent substitute(Map<PExp, PExp> mapping) {
        ImmutableConjuncts genericRetval = super.substitute(mapping);
        return new Consequent(genericRetval);
    }

    @Override
    public Consequent appended(Iterable<PExp> i) {
        ImmutableConjuncts genericRetval = super.appended(i);
        return new Consequent(genericRetval);
    }

    @Override
    public Consequent eliminateObviousConjuncts() {
        ImmutableConjuncts genericRetval = super.eliminateObviousConjuncts();
        return new Consequent(genericRetval);
    }

    @Override
    public Consequent removed(int index) {
        ImmutableConjuncts genericRetval = super.removed(index);
        return new Consequent(genericRetval);
    }

    @Override
    public Consequent eliminateRedundantConjuncts() {
        ImmutableConjuncts genericRetval = super.eliminateRedundantConjuncts();
        return new Consequent(genericRetval);
    }

    public Antecedent assumed() {
        return new Antecedent(this);
    }
}
