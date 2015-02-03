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
package edu.clemson.cs.r2jt.sanitycheck;

import java.util.Iterator;
import edu.clemson.cs.r2jt.absyn.CallStmt;
import edu.clemson.cs.r2jt.absyn.ProcedureDec;
import edu.clemson.cs.r2jt.absyn.ProgramParamExp;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.treewalk.TreeWalker;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerStackVisitor;

public class VisitorRecursiveCheck extends TreeWalkerStackVisitor {

    /* Private Variables */

    private CompileEnvironment myCompileEnvironment;
    private ProcedureDec myInitialProcedureDec;
    private Boolean isRecursive = false;

    private List<ProcedureDec> myCheckedProcedureDecs;

    /* Constructors */

    public VisitorRecursiveCheck(ProcedureDec dec, CompileEnvironment env) {
        myInitialProcedureDec = dec;
        myCompileEnvironment = env;
        // System.out.println("Proc: " + dec.getName().getName());

        myCheckedProcedureDecs = new List<ProcedureDec>();
    }

    private VisitorRecursiveCheck(ProcedureDec dec,
            List<ProcedureDec> checkedProcDec, CompileEnvironment env) {
        myInitialProcedureDec = dec;
        myCompileEnvironment = env;
        // System.out.println("Proc: " + dec.getName().getName());

        myCheckedProcedureDecs = checkedProcDec;
    }

    @Override
    public void preCallStmt(CallStmt stmt) {
        String name = stmt.getName().getName();
        // System.out.println("CallStmt: " + name);
        checkRecursion(name);
    }

    @Override
    public void preProgramParamExp(ProgramParamExp exp) {
        String name = exp.getName().getName();
        // System.out.println("ProgramParamExp: " + name);
        checkRecursion(name);
    }

    /* Helper Functions */

    public Boolean isRecursive() {
        return isRecursive;
    }

    /**
     * This method recursively uses <code>VisitorRecursiveCheck</code> to
     * determine if any of the <code>myInitialProcedureDec</code>'s function
     * calls in turn call it, causing it to be recursive.
     * 
     * @param name Name of the current stmt/exp being checked
     */
    private void checkRecursion(String name) {
        if (name.equals(myInitialProcedureDec.getName().getName())) {
            isRecursive = true;
        }
        else {
            Iterator<ProcedureDec> i =
                    myCompileEnvironment.encounteredProcedures.iterator();
            while (i.hasNext()) {
                ProcedureDec dec = i.next();
                if (!myCheckedProcedureDecs.contains(dec)) {
                    myCheckedProcedureDecs.add(dec);
                    if (name.equals(dec.getName().getName())) {
                        VisitorRecursiveCheck vrc =
                                new VisitorRecursiveCheck(
                                        myInitialProcedureDec,
                                        myCheckedProcedureDecs,
                                        myCompileEnvironment);
                        TreeWalker tw = new TreeWalker(vrc);
                        tw.visit(dec);
                        // System.out.println(dec.getName().getName() +
                        // " causes isRecursive: " + vrc.isRecursive() + " for "
                        // + myInitialProcedureDec.getName().getName());
                        isRecursive = vrc.isRecursive();
                    }
                }
            }
        }
    }
}
