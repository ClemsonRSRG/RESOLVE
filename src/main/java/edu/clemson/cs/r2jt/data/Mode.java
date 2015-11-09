/**
 * Mode.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.data;

/** Provides access to type checkable variable modes. */
public class Mode {

    // ===========================================================
    // Variables
    // ===========================================================

    private String modeName;

    // ===========================================================
    // Constructors
    // ===========================================================

    private Mode(String modeName) {
        this.modeName = modeName;
    }

    // ===========================================================
    // Objects
    // ===========================================================

    public final static Mode UPDATES = new Mode("updates");
    public final static Mode RESTORES = new Mode("restores");
    public final static Mode REPLACES = new Mode("replaces");
    public final static Mode PRESERVES = new Mode("preserves");
    public final static Mode EVALUATES = new Mode("evaluates");
    public final static Mode REASSIGNS = new Mode("reassigns");
    public final static Mode CLEARS = new Mode("clears");
    public final static Mode ALTERS = new Mode("alters");
    public final static Mode STATE = new Mode("State");
    public final static Mode OPER_NAME = new Mode("Oper_Name");
    public final static Mode LOCAL = new Mode("Local");
    public final static Mode FIELD = new Mode("Field");
    // Math Variable Modes
    public final static Mode MATH = new Mode("Math");
    public final static Mode DEFINITION = new Mode("Definition");
    public final static Mode DEF_PARAM = new Mode("Def_Param");
    public final static Mode CONCEPTUAL = new Mode("Conceptual");
    public final static Mode EXEMPLAR = new Mode("Exemplar");
    public final static Mode MATH_FIELD = new Mode("Math_Field");

    // ===========================================================
    // Public Methods
    // ===========================================================

    public String getModeName() {
        return modeName;
    }

    public static boolean equals(Mode a, Mode b) {
        boolean result;
        if (a.modeName.equals(b.modeName)) {
            result = true;
        }
        else {
            result = false;
        }
        return result;
    }

    public boolean isMathMode() {
        if (equals(this, MATH) || equals(this, DEFINITION)
                || equals(this, DEF_PARAM) || equals(this, CONCEPTUAL)
                || equals(this, EXEMPLAR) || equals(this, MATH_FIELD)) {
            return true;
        }
        else
            return false;
    }

    /*
     * Implements mode compatibility
     *
     * Concept C
     *     Operation P(MODE_formal v: T)
     * end C
     *
     * Realization B
     *     Procedure P(MODE_actual v: T)
     * end B
     *
     * The table below indicates when MODE_actual is compatible with
     * MODE_formal?  Note that static variables are assigned modes,
     * and constants and locals are not relevant.
     * 
     * Read the following table as:
     *
     * MODE_actual (DOWN LEFT) can implement MODE_formal(ACROSS TOP)
     *
     * ---------------------------------------
     *      UPD  ALT  REP  CLR  RES  PRE  EVL
     * UPD   Y    Y    N?   N    N    N    N
     * ALT   N?   Y    N?*  N    N    N    N
     * REP   N?   N*   Y    N    N    N    N
     * CLR   Y    Y    Y?   Y    N    N    N
     * RES   Y    Y    N?*  N    Y    N    N
     * PRE   Y    Y    N?*  N    Y    Y    N
     * EVL   N    N    N    N    N    N    Y
     * ---------------------------------------
     * 
     *    * means was changed recently, may still need to be considered
     */
    public static boolean implementsCompatible(Mode actual, Mode formal) {
        boolean result;

        if (formal.equals(REASSIGNS) || actual.equals(REASSIGNS)) {
            result = true;
        }
        else {
            if (formal.equals(UPDATES)) {
                result = updatesImplementsCompatible(actual);
            }
            else if (formal.equals(ALTERS)) {
                result = altersImplementsCompatible(actual);
            }
            else if (formal.equals(REPLACES)) {
                result = replacesImplementsCompatible(actual);
            }
            else if (formal.equals(CLEARS)) {
                result = clearsImplementsCompatible(actual);
            }
            else if (formal.equals(RESTORES)) {
                result = restoresImplementsCompatible(actual);
            }
            else if (formal.equals(PRESERVES)) {
                result = preservesImplementsCompatible(actual);
            }
            else if (formal.equals(EVALUATES)) {
                result = evaluatesImplementsCompatible(actual);
            }
            else if (formal.equals(REASSIGNS)) {
                result = evaluatesImplementsCompatible(actual);
            }
            else {
                result = false;
            }
        }
        return result;
    }

    /*
     * Procedure call mode compatibility
     *
     * Procedure P(MODE_actual v: T)
     *     begin
     *       Q(v)
     *     end
     *
     * Procedure Q(MODE_formal v: T)
     *
     * The table below indicates when MODE_actual is compatible-with
     * MODE_formal?  Note that static variables are given modes, and
     * constants behave like the preserves mode.
     * 
     * Read the following table as:
     *
     * MODE_actual (DOWN LEFT) can be passed to MODE_formal(ACROSS TOP)
     *
     * ---------------------------------------
     *      UPD  ALT  REP  CLR  RES  PRE  EVL
     * UPD   Y    Y    Y    Y    Y    Y    N
     * ALT   Y    Y    Y    Y    Y    Y    N   
     * REP   Y    Y    Y    Y    Y    Y    N
     * CLR   Y    Y    Y    Y    Y    Y    N
     * RES   Y    Y    Y    Y    Y    Y    N
     * PRE   N    N    N    N    N    Y    N
     * EVL   N    N    N    N    N    N    Y
     * LOC   Y    Y    Y    Y    Y    Y    N
     * ---------------------------------------
     */
    public static boolean callCompatible(Mode actual, Mode formal) {
        boolean result;
        if (formal.equals(UPDATES)) {
            result = updatesCallCompatible(actual);
        }
        else if (formal.equals(ALTERS)) {
            result = altersCallCompatible(actual);
        }
        else if (formal.equals(REPLACES)) {
            result = replacesCallCompatible(actual);
        }
        else if (formal.equals(CLEARS)) {
            result = clearsCallCompatible(actual);
        }
        else if (formal.equals(RESTORES)) {
            result = restoresCallCompatible(actual);
        }
        else if (formal.equals(PRESERVES)) {
            result = preservesCallCompatible(actual);
        }
        else if (formal.equals(EVALUATES)) {
            result = evaluatesCallCompatible(actual);
        }
        else if (formal.equals(REASSIGNS)) {
            result = evaluatesCallCompatible(actual);
        }
        else {
            result = false;
        }
        return result;
    }

    public String toString() {
        return modeName;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Implements compatible helpers
    // -----------------------------------------------------------

    private static boolean updatesImplementsCompatible(Mode actual) {
        boolean result;
        if /**/(actual.equals(UPDATES) || actual.equals(CLEARS)
                || actual.equals(RESTORES) || actual.equals(PRESERVES)) {
            result = true;
        }
        else {
            result = false;
        }
        return result;
    }

    private static boolean altersImplementsCompatible(Mode actual) {
        boolean result;
        if /**/(actual.equals(UPDATES) || actual.equals(ALTERS)
                || actual.equals(CLEARS) || actual.equals(RESTORES)
                || actual.equals(PRESERVES)) {
            result = true;
        }
        else {
            result = false;
        }
        return result;
    }

    private static boolean replacesImplementsCompatible(Mode actual) {
        boolean result;
        if /**/(actual.equals(REPLACES) || actual.equals(CLEARS)) {
            result = true;
        }
        else {
            result = false;
        }
        return result;
    }

    private static boolean clearsImplementsCompatible(Mode actual) {
        boolean result;
        if (actual.equals(CLEARS)) {
            result = true;
        }
        else {
            result = false;
        }
        return result;
    }

    private static boolean restoresImplementsCompatible(Mode actual) {
        boolean result;
        if /**/(actual.equals(RESTORES) || actual.equals(PRESERVES)) {
            result = true;
        }
        else {
            result = false;
        }
        return result;
    }

    private static boolean preservesImplementsCompatible(Mode actual) {
        boolean result;
        if (actual.equals(PRESERVES)) {
            result = true;
        }
        else {
            result = false;
        }
        return result;
    }

    private static boolean evaluatesImplementsCompatible(Mode actual) {
        boolean result;
        if (actual.equals(EVALUATES)) {
            result = true;
        }
        else {
            result = false;
        }
        return result;
    }

    // -----------------------------------------------------------
    // Call compatible helpers
    // -----------------------------------------------------------

    private static boolean updatesCallCompatible(Mode actual) {
        boolean result;
        if /**/(actual.equals(UPDATES) || actual.equals(ALTERS)
                || actual.equals(REPLACES) || actual.equals(CLEARS)
                || actual.equals(RESTORES) || actual.equals(LOCAL)) {
            result = true;
        }
        else {
            result = false;
        }
        return result;
    }

    private static boolean altersCallCompatible(Mode actual) {
        boolean result;
        if /**/(actual.equals(UPDATES) || actual.equals(ALTERS)
                || actual.equals(REPLACES) || actual.equals(CLEARS)
                || actual.equals(RESTORES) || actual.equals(LOCAL)) {
            result = true;
        }
        else {
            result = false;
        }
        return result;
    }

    private static boolean replacesCallCompatible(Mode actual) {
        boolean result;
        if /**/(actual.equals(UPDATES) || actual.equals(ALTERS)
                || actual.equals(REPLACES) || actual.equals(CLEARS)
                || actual.equals(RESTORES) || actual.equals(LOCAL)) {
            result = true;
        }
        else {
            result = false;
        }
        return result;
    }

    private static boolean clearsCallCompatible(Mode actual) {
        boolean result;
        if /**/(actual.equals(UPDATES) || actual.equals(ALTERS)
                || actual.equals(REPLACES) || actual.equals(CLEARS)
                || actual.equals(RESTORES) || actual.equals(LOCAL)) {
            result = true;
        }
        else {
            result = false;
        }
        return result;
    }

    private static boolean restoresCallCompatible(Mode actual) {
        boolean result;
        if /**/(actual.equals(UPDATES) || actual.equals(ALTERS)
                || actual.equals(REPLACES) || actual.equals(CLEARS)
                || actual.equals(RESTORES) || actual.equals(LOCAL)) {
            result = true;
        }
        else {
            result = false;
        }
        return result;
    }

    private static boolean preservesCallCompatible(Mode actual) {
        boolean result;
        if /**/(actual.equals(PRESERVES) || actual.equals(LOCAL)) {
            result = true;
        }
        else {
            result = false;
        }
        return result;
    }

    private static boolean evaluatesCallCompatible(Mode actual) {
        boolean result;
        if (actual.equals(EVALUATES)) {
            result = true;
        }
        else {
            result = false;
        }
        return result;
    }
}
