/**
 * ProgramParameterEntry.java
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
public class ProgramParameterEntry {

    public enum ParameterMode {
        ALTERS {

            @Override
            public ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { ALTERS, CLEARS };
            }
        },
        UPDATES {

            @Override
            public ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { UPDATES, CLEARS, RESTORES,
                        PRESERVES };
            }
        },
        REPLACES {

            @Override
            public ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { REPLACES, CLEARS };
            }
        },
        CLEARS {

            @Override
            public ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { CLEARS };
            }
        },
        RESTORES {

            @Override
            public ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { RESTORES, PRESERVES };
            }
        },
        PRESERVES {

            @Override
            public ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { PRESERVES };
            }
        },
        EVALUATES {

            @Override
            public ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { EVALUATES };
            }
        },
        TYPE {

            @Override
            public ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { TYPE };
            }
        };

        public boolean canBeImplementedWith(ParameterMode o) {
            return contains(getValidImplementationModes(), o);
        }

        private static boolean contains(Object[] os, Object o) {
            boolean result = false;

            int i = 0;
            int osLength = os.length;
            while (!result && i < osLength) {
                result = os[i].equals(o);
                i++;
            }

            return result;
        }

        public abstract ParameterMode[] getValidImplementationModes();
    }

}