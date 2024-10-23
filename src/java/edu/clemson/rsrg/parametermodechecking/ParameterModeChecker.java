package edu.clemson.rsrg.parametermodechecking;

import edu.clemson.rsrg.init.CompileEnvironment;
import edu.clemson.rsrg.init.flag.Flag;
import edu.clemson.rsrg.treewalk.TreeWalkerVisitor;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;

public class ParameterModeChecker extends TreeWalkerVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The symbol table we are currently building.
     * </p>
     */
    private final MathSymbolTableBuilder myBuilder;

    /**
     * <p>
     * The current job's compilation environment that stores all necessary objects and flags.
     * </p>
     */
    private final CompileEnvironment myCompileEnvironment;

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
    public static final Flag FLAG_CHECK_PARAMETER_MODES = new Flag(FLAG_SECTION_NAME, "PMC", FLAG_DESC_CHECK_PARAM_MODE);

    public static void setUpFlags() {

    }

    public ParameterModeChecker(MathSymbolTableBuilder builder, CompileEnvironment compileEnvironment) {
        myCompileEnvironment = compileEnvironment;
        myBuilder = builder;
    }
}
