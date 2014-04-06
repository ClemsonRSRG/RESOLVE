/**
 * Symbol.java
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
package edu.clemson.cs.r2jt.congruenceclassprover;

/**
 * Created by mike on 4/3/2014.
 */
public class Symbol {

    public static enum Quantification {

        NONE {

            protected Quantification flipped() {
                return NONE;
            }
        },
        FOR_ALL {

            protected Quantification flipped() {
                return THERE_EXISTS;
            }
        },
        THERE_EXISTS {

            protected Quantification flipped() {
                return FOR_ALL;
            }
        };

        protected abstract Quantification flipped();
    }
}
