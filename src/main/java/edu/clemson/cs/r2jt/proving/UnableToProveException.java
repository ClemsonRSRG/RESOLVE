/**
 * UnableToProveException.java
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
 * <p>An exception indicating that some VC could not be proved in a reasonable
 * amount of time by the available logical methods.  It may still be true, the
 * system simply cannot prove it.</p>
 * 
 * @author H. Smith
 */
public class UnableToProveException extends ProverException {

    private static final long serialVersionUID = 1L;

    public UnableToProveException(Metrics metrics) {
        super(metrics);
    }

    public UnableToProveException(String msg, AssertiveCode VC, Metrics metrics) {
        super(msg, VC, metrics);
    }
}
