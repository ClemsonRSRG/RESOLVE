/**
 * DummyTheorem.java
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
package edu.clemson.cs.r2jt.rewriteprover;

import edu.clemson.cs.r2jt.rewriteprover.model.Theorem;
import edu.clemson.cs.r2jt.rewriteprover.transformations.Transformation;
import java.util.Collections;
import java.util.List;

/**
 * <p>Simply provides a way to construct a theorem that will return a particular
 * transformation.</p>
 */
public class DummyTheorem extends Theorem {

    private final Transformation myTransformation;

    public DummyTheorem(Transformation t) {
        super(null, null);

        myTransformation = t;
    }

    @Override
    public List<Transformation> getTransformations() {
        return Collections.singletonList(myTransformation);
    }

    @Override
    public String toString() {
        return "" + myTransformation;
    }
}
