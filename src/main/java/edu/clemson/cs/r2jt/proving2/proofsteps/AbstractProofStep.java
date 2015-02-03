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
package edu.clemson.cs.r2jt.proving2.proofsteps;

import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.Conjunct;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public abstract class AbstractProofStep implements ProofStep {

    private final Transformation myTransformation;
    private final Application myApplication;
    private final Collection<Site> myBoundSites;

    private Set<Conjunct> myBoundConjuncts;

    public AbstractProofStep(Transformation t, Application a,
            Collection<Site> boundSites) {
        myTransformation = t;
        myApplication = a;
        myBoundSites = boundSites;
    }

    @Override
    public final Transformation getTransformation() {
        return myTransformation;
    }

    @Override
    public final Application getApplication() {
        return myApplication;
    }

    @Override
    public final Set<Conjunct> getPrerequisiteConjuncts() {
        return myApplication.getPrerequisiteConjuncts();
    }

    @Override
    public final Set<Conjunct> getBoundConjuncts() {
        if (myBoundConjuncts == null) {
            myBoundConjuncts = new HashSet<Conjunct>();

            if (myBoundSites != null) {
                for (Site s : myBoundSites) {
                    myBoundConjuncts.add(s.conjunct);
                }
            }

            myBoundConjuncts = Collections.unmodifiableSet(myBoundConjuncts);
        }

        return myBoundConjuncts;
    }

    @Override
    public final Set<Conjunct> getAffectedConjuncts() {
        return myApplication.getAffectedConjuncts();
    }

    @Override
    public final Set<Site> getAffectedSites() {
        return myApplication.getAffectedSites();
    }
}
