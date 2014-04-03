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
