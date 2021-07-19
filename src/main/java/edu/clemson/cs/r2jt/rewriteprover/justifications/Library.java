/*
 * Library.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover.justifications;

import edu.clemson.cs.r2jt.typeandpopulate.entry.TheoremEntry;

/**
 *
 * @author hamptos
 */
public class Library implements Justification {

    private final TheoremEntry myEntry;

    public Library(TheoremEntry e) {
        myEntry = e;
    }
}
