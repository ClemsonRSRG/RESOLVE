/*
 * Prover.java
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
package edu.clemson.cs.r2jt.rewriteprover;

import edu.clemson.cs.r2jt.Main;
import edu.clemson.cs.r2jt.misc.Flag;
import edu.clemson.cs.r2jt.misc.FlagDependencies;
import edu.clemson.cs.r2jt.misc.FlagManager;

/**
 * <p>
 * The <code>Prover</code> accepts as its input <em>verification conditions</em>
 * (VCs) and attempts
 * to resolve each VC to <code>true</code> using various logical steps.
 * </p>
 * 
 * TODO : Currently this will only properly prove VCs originating from the
 * "target file", which is
 * the file named at the command line rather than any files imported from that
 * file. This is because
 * the Environment only gives us a handle on the target file and not the
 * currently compiled file.
 * This is ok for the moment because it seems the Verifier works this way as
 * well (see
 * getMainFile()) inside it. But eventually we want to prove VCs for imported
 * files as well. So for
 * right now, we consider exactly those theories availale to us from the target
 * file, even if we're
 * trying to import a different file on the WAY to proving the target file. -HwS
 * 
 * TODO : Theorems are drawn only from Theories named in the target file and not
 * from those theories
 * included from the theories named. That is, if I include String_Theory and
 * String_Theory "uses"
 * Boolean_Theory, the thoerems from Boolean_Theory will not be imported. -HwS
 * 
 * @author H. Smith
 */
public final class Prover {

    private static final double FITNESS_THRESHOLD = 0.8;
    public static final String FLAG_SECTION_NAME = "Proving";
    private static final String FLAG_DESC_PROVE =
            "Verify program with RESOLVE's integrated prover.";
    private static final String FLAG_DESC_LEGACY_PROVE =
            "Verify program with RESOLVE's legacy integrated prover.";
    private static final String FLAG_DESC_LEGACY_PROVE_ALIAS =
            "An alias for -prove.";
    private static final String FLAG_DESC_DEBUG =
            "Prompt user to guide the prover step-by-step.  May be used with "
                    + "either the -prove or -altprove options.";
    private static final String FLAG_DESC_VERBOSE =
            "Prints prover debugging information.  May be used with either the "
                    + "-prove or -altprove options.";
    private static final String FLAG_DESC_NOGUI =
            "Supresses any graphical interfaces so that the compiler can be run "
                    + "headlessly.";

    public static final String FLAG_TIMEOUT_ARG_NAME = "milliseconds";

    private static final String[] FLAG_TIMEOUT_ARGS = { FLAG_TIMEOUT_ARG_NAME };

    /**
     * <p>
     * The main prover flag. Causes the integrated prover to attempt to dispatch
     * generated VCs.
     * </p>
     */
    public static final Flag FLAG_PROVE =
            new Flag(FLAG_SECTION_NAME, "altprove", FLAG_DESC_PROVE);

    /**
     * <p>
     * The legacy prover flag. In place for backwards compatibility--causes the
     * integrated prover to
     * attempt to dispatch generated VCs using the original prover circa January
     * 2010.
     * </p>
     */
    public static final Flag FLAG_LEGACY_PROVE =
            new Flag(FLAG_SECTION_NAME, "prove", FLAG_DESC_LEGACY_PROVE);

    private static final String FLAG_DESC_TIMEOUT =
            "Takes a number of " + "milliseconds to use as a timeout for "
                    + FLAG_LEGACY_PROVE.invocation + "  Incompatible with "
                    + FLAG_PROVE.invocation + ".";

    /**
     * <p>
     * An alias for FLAG_LEGACY_PROVE. Also for backward compatibility.
     * </p>
     */
    private static final Flag FLAG_LEGACY_PROVE_ALIAS =
            new Flag(FLAG_SECTION_NAME, "quickprove",
                    FLAG_DESC_LEGACY_PROVE_ALIAS, Flag.Type.HIDDEN);
    /**
     * <p>
     * Causes the prover to prompt the user to choose a theorem at each step,
     * rather than choosing one
     * on its own.
     * </p>
     */
    public static final Flag FLAG_DEBUG = new Flag(FLAG_SECTION_NAME,
            "debugprove", FLAG_DESC_DEBUG, Flag.Type.HIDDEN);

    /**
     * <p>
     * Puts a rough timeout on the old prover.
     * </p>
     */
    public static final Flag FLAG_TIMEOUT = new Flag(FLAG_SECTION_NAME,
            "timeout", FLAG_DESC_TIMEOUT, FLAG_TIMEOUT_ARGS, Flag.Type.HIDDEN);

    /**
     * <p>
     * Prints additional debugging information.
     * </p>
     */
    public static final Flag FLAG_VERBOSE = new Flag(FLAG_SECTION_NAME,
            "verboseprove", FLAG_DESC_VERBOSE, Flag.Type.HIDDEN);
    public static final Flag FLAG_NOGUI =
            new Flag(Main.FLAG_SECTION_GENERAL, "noGUI", FLAG_DESC_NOGUI);

    /**
     * <p>
     * An auxiliary flag implied by any flag that attempts to do some proving.
     * This is a single flag
     * that can be checked to find out if either the old or new prover is
     * active.
     * </p>
     */
    public static final Flag FLAG_SOME_PROVER =
            new Flag(Main.FLAG_SECTION_GENERAL, "someprover", "aux",
                    Flag.Type.AUXILIARY);

    private final long TIMEOUT = Integer.MAX_VALUE;

    public void createFlags(FlagManager m) {}

    public static void setUpFlags() {
        Flag[] someProveFlag = { FLAG_LEGACY_PROVE, FLAG_PROVE };
        FlagDependencies.addRequires(FLAG_DEBUG, someProveFlag);
        FlagDependencies.addRequires(FLAG_VERBOSE, someProveFlag);

        FlagDependencies.addImplies(FLAG_LEGACY_PROVE_ALIAS, FLAG_LEGACY_PROVE);

        FlagDependencies.addExcludes(FLAG_LEGACY_PROVE, FLAG_PROVE);

        FlagDependencies.addExcludes(FLAG_NOGUI, FLAG_DEBUG);

        FlagDependencies.addImplies(FLAG_PROVE, FLAG_SOME_PROVER);
        FlagDependencies.addImplies(FLAG_LEGACY_PROVE, FLAG_SOME_PROVER);

        FlagDependencies.addExcludes(FLAG_PROVE, FLAG_TIMEOUT);
    }
}
