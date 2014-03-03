/**
 * TheoremUnwrapper.java
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
package edu.clemson.cs.r2jt.proving2.utilities;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.model.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.model.Theorem;
import edu.clemson.cs.r2jt.utilities.Mapping;

/**
 *
 * @author hamptos
 */
public class TheoremUnwrapper implements Mapping<Theorem, PExp> {

    public static final TheoremUnwrapper INSTANCE = new TheoremUnwrapper();

    private TheoremUnwrapper() {

    }

    @Override
    public PExp map(Theorem input) {
        return input.getAssertion();
    }
}
