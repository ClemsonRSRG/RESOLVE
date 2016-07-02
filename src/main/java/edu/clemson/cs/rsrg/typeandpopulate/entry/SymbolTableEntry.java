/**
 * SymbolTableEntry.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.entry;

/**
 * TODO: Refactor this class
 */
public class SymbolTableEntry {

    public enum Quantification {
        NONE {

            @Override
            public String toString() {
                return "None";
            }

        },
        UNIVERSAL {

            @Override
            public String toString() {
                return "Universal";
            }

        },
        EXISTENTIAL {

            @Override
            public String toString() {
                return "Existential";
            }

        },
        UNIQUE {

            @Override
            public String toString() {
                return "Unique";
            }

        },
    }

}