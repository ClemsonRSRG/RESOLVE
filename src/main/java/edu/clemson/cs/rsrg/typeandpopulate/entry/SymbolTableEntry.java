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

import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;

import java.util.HashMap;
import java.util.Map;

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

    public static Map<String, MTType> buildMathTypeGenerics(
            Map<String, PTType> genericInstantiations) {

        Map<String, MTType> genericMathematicalInstantiations =
                new HashMap<>();

        for (Map.Entry<String, PTType> instantiation : genericInstantiations
                .entrySet()) {

            genericMathematicalInstantiations.put(instantiation.getKey(),
                    instantiation.getValue().toMath());
        }

        return genericMathematicalInstantiations;
    }
}