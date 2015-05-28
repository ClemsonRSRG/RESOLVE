/**
 * TheoremApplication.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover.justifications;

import edu.clemson.cs.r2jt.rewriteprover.transformations.Transformation;

/**
 *
 * @author hamptos
 */
public class TheoremApplication implements Justification {

    private final Transformation myTransformation;

    public TheoremApplication(Transformation t) {
        myTransformation = t;
    }
}
