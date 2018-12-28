/*
 * DuplicateSymbolException.java
 * ---------------------------------
 * Copyright (c) 2019
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate;

import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;

@SuppressWarnings("serial")
public class DuplicateSymbolException extends SymbolTableException {

    private final SymbolTableEntry myExistingEntry;

    public DuplicateSymbolException() {
        super();
        myExistingEntry = null;
    }

    public DuplicateSymbolException(String s) {
        super(s);
        myExistingEntry = null;
    }

    public DuplicateSymbolException(SymbolTableEntry existing) {
        super();

        myExistingEntry = existing;
    }

    public DuplicateSymbolException(SymbolTableEntry existing, String msg) {
        super(msg);

        myExistingEntry = existing;
    }

    public SymbolTableEntry getExistingEntry() {
        return myExistingEntry;
    }
}
