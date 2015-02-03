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
package edu.clemson.cs.r2jt.proving2.automators;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.model.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.transformations.RemoveAntecedent;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class EliminateRedundantAntecedents implements Automator {

    public static final EliminateRedundantAntecedents INSTANCE =
            new EliminateRedundantAntecedents();

    private EliminateRedundantAntecedents() {

    }

    @Override
    public void step(Deque<Automator> stack, PerVCProverModel model) {
        Set<PExp> seen = new HashSet<PExp>();

        LocalTheorem curTheorem;
        LocalTheorem toRemove = null;
        Iterator<LocalTheorem> localTheorems =
                model.getLocalTheoremList().iterator();
        while (toRemove == null && localTheorems.hasNext()) {
            curTheorem = localTheorems.next();

            if (seen.contains(curTheorem.getAssertion())) {
                toRemove = curTheorem;
            }

            seen.add(curTheorem.getAssertion());
        }

        if (toRemove == null) {
            stack.pop();
        }
        else {
            new RemoveAntecedent(model, toRemove).getApplications(model).next()
                    .apply(model);
        }
    }
}
