/**
 * VCInconsistentException.java
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
package edu.clemson.cs.r2jt.proving;

import edu.clemson.cs.r2jt.verification.AssertiveCode;

/**
 * <p>An exception indicating that some VC was proved impossible.</p>
 * 
 * @author H. Smith
 */
public class VCInconsistentException extends ProverException {

    private static final long serialVersionUID = -468415982459853594L;

    public VCInconsistentException(Metrics metrics) {
        super(metrics);
    }

    public VCInconsistentException(String msg, AssertiveCode VC, Metrics metrics) {
        super(msg, VC, metrics);
    }
}
