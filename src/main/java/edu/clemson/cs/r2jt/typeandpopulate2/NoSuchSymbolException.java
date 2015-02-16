/**
 * NoSuchSymbolException.java
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
package edu.clemson.cs.r2jt.typeandpopulate2;

import edu.clemson.cs.r2jt.typeandpopulate2.SymbolTableException;

@SuppressWarnings("serial")
public class NoSuchSymbolException extends SymbolTableException {

    public NoSuchSymbolException() {
        super();
    }

    public NoSuchSymbolException(String msg) {
        super(msg);
    }

    public NoSuchSymbolException(Exception causedBy) {
        super(causedBy);
    }
}
