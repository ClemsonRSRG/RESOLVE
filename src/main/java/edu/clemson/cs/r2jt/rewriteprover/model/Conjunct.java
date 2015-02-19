/**
 * Conjunct.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover.model;

import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;

/**
 * <p>A <code>Conjunct</code> represents a single mathematical statement that
 * we may assume or are trying to prove from a 
 * {@link PerVCProverModel PerVCProverModel}.  An instance of this class 
 * provides a consistent handle on a single such statement throughout its 
 * lifetime, even if its formulation changes or it is temporarily removed from
 * the model.</p>
 */
public interface Conjunct {

    public Site toSite(PerVCProverModel m);

    public PExp getExpression();

    void setExpression(PExp newValue);

    public boolean editable();

    public boolean libraryTheorem();
}
