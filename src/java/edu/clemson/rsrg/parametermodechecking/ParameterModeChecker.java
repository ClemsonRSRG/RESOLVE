package edu.clemson.rsrg.parametermodechecking;

import edu.clemson.rsrg.init.flag.Flag;
import edu.clemson.rsrg.treewalk.TreeWalkerVisitor;

public class ParameterModeChecker extends TreeWalkerVisitor {

    // ===========================================================
    // Flag Strings
    // ===========================================================

    private static final String FLAG_SECTION_NAME = "ParameterModeChecker";
    private static final String FLAG_DESC_CHECK_PARAM_MODE = "Check Parameter Modes.";

    // ===========================================================
    // Flags
    // ===========================================================

    /**
     * <p>
     * Tells the compiler to check parameter modes.
     * </p>
     */
    public static final Flag FLAG_CHECK_PARAMETER_MODES = new Flag(FLAG_SECTION_NAME, "VCs", FLAG_DESC_CHECK_PARAM_MODE);

}
