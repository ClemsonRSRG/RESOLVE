/*
 * This software is released under the new BSD 2006 license.
 * 
 * Note the new BSD license is equivalent to the MIT License, except for the
 * no-endorsement final clause.
 * 
 * Copyright (c) 2007, Clemson University
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the Clemson University nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This sofware has been developed by past and present members of the
 * Reusable Sofware Research Group (RSRG) in the School of Computing at
 * Clemson University. Contributors to the initial version are:
 * 
 * Steven Atkinson
 * Greg Kulczycki
 * Kunal Chopra
 * John Hunt
 * Heather Keown
 * Ben Markle
 * Kim Roche
 * Murali Sitaraman
 * Hampton Smith
 */
package edu.clemson.cs.r2jt.proving2;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.clemson.cs.r2jt.Main;
import edu.clemson.cs.r2jt.absyn.Dec;
import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.MathAssertionDec;
import edu.clemson.cs.r2jt.absyn.MathModuleDec;
import edu.clemson.cs.r2jt.absyn.ModuleDec;
import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.scope.ModuleScope;
import edu.clemson.cs.r2jt.scope.OldSymbolTable;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.utilities.FlagDependencies;
import edu.clemson.cs.r2jt.verification.Verifier;

/**
 * <p>
 * The <code>Prover</code> accepts as its input <em>verification conditions</em>
 * (VCs) and attempts to resolve each VC to <code>true</code> using various
 * logical steps.
 * </p>
 * 
 * TODO : Currently this will only properly prove VCs originating from the
 * "target file", which is the file named at the command line rather than any
 * files imported from that file. This is because the Environment only gives us
 * a handle on the target file and not the currently compiled file. This is ok
 * for the moment because it seems the Verifier works this way as well (see
 * getMainFile()) inside it. But eventually we want to prove VCs for imported
 * files as well. So for right now, we consider exactly those theories available
 * to us from the target file, even if we're trying to import a different file
 * on the WAY to proving the target file. -HwS
 * 
 * @author H. Smith
 */
public final class Prover2 {

    private static final String FLAG_SECTION_NAME = "Proving";
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
            "Prints prover debugging information.  May be used with either "
                    + "the -prove or -altprove options.";
    private static final String FLAG_DESC_NOGUI =
            "Supresses any graphical interfaces so that the compiler can be "
                    + "run headlessly.";
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
     * integrated prover to attempt to dispatch generated VCs using the original
     * prover circa January 2010.
     * </p>
     */
    public static final Flag FLAG_LEGACY_PROVE =
            new Flag(FLAG_SECTION_NAME, "prove", FLAG_DESC_LEGACY_PROVE);
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
     * rather than choosing one on its own.
     * </p>
     */
    public static final Flag FLAG_DEBUG =
            new Flag(FLAG_SECTION_NAME, "debugprove", FLAG_DESC_DEBUG,
                    Flag.Type.HIDDEN);
    /**
     * <p>
     * Prints additional debugging information.
     * </p>
     */
    public static final Flag FLAG_VERBOSE =
            new Flag(FLAG_SECTION_NAME, "verboseprove", FLAG_DESC_VERBOSE,
                    Flag.Type.HIDDEN);
    public static final Flag FLAG_NOGUI =
            new Flag(Main.FLAG_SECTION_GENERAL, "noGUI", FLAG_DESC_NOGUI);

    /**
     * <p>
     * An auxiliary flag implied by any flag that attempts to do some proving.
     * This is a single flag that can be checked to find out if either the old
     * or new prover is active.
     * </p>
     */
    public static final Flag FLAG_SOME_PROVER =
            new Flag(Main.FLAG_SECTION_GENERAL, "someprover", "aux",
                    Flag.Type.AUXILIARY);

    public static ProverResult prove(Iterable<VC> vcs,
            CompileEnvironment instanceEnvironment) {

        Map<String, PExp> theorems = buildTheories(instanceEnvironment);

        return proveVCs(vcs, theorems, instanceEnvironment);
    }

    /**
     * <p>
     * Builds a list of <code>TheoremEntry</code>s representing all available
     * theorems for Theories currently in scope of the "target file".
     * </p>
     * 
     * @return The list of Theorems.
     */
    private static Map<String, PExp> buildTheories(
            CompileEnvironment environment) {
        Map<String, PExp> result = new HashMap<String, PExp>();

        File targetFileName = environment.getTargetFile();
        ModuleID targetFileID = environment.getModuleID(targetFileName);
        List<ModuleID> availableTheories =
                environment.getTheories(targetFileID);

        Exp curTheorem;

        // Add local axioms to the library
        ModuleDec targetDec = environment.getModuleDec(targetFileID);
        addLocalAxioms(targetDec, result);

        // Add any kind of mathematical assertions from any included library
        OldSymbolTable curSymbolTable;
        ModuleScope bindingsInScope;
        List<Symbol> symbolsInScope;
        for (ModuleID curModule : availableTheories) {
            curSymbolTable = environment.getSymbolTable(curModule);
            bindingsInScope = curSymbolTable.getModuleScope();
            symbolsInScope = bindingsInScope.getLocalTheoremNames();

            for (Symbol s : symbolsInScope) {

                curTheorem = bindingsInScope.getLocalTheorem(s).getValue();
                addTheoremToLibrary(s.getName(), curTheorem, result);
            }
        }

        return result;
    }

    private static void addLocalAxioms(ModuleDec module,
            Map<String, PExp> theorems) {

        // TODO : Eventually axioms in any type of module should be supported

        if (module instanceof MathModuleDec) {
            MathModuleDec moduleAsMathModuleDec = (MathModuleDec) module;
            List<Dec> decs = moduleAsMathModuleDec.getDecs();
            addLocalAxioms(decs, theorems);
        }
    }

    private static void addLocalAxioms(List<Dec> decs,
            Map<String, PExp> theorems) {

        for (Dec d : decs) {
            if (d instanceof MathAssertionDec) {
                MathAssertionDec dAsMathAssertionDec = (MathAssertionDec) d;

                if (dAsMathAssertionDec.getKind() == MathAssertionDec.AXIOM) {
                    Exp theorem = dAsMathAssertionDec.getAssertion();

                    addTheoremToLibrary(d.getName().getName(), theorem,
                            theorems);
                }
            }
        }
    }

    private static void addTheoremToLibrary(String name, Exp theorem,
            Map<String, PExp> theorems) {

        Exp quantifiersAppliedTheorem = Utilities.applyQuantification(theorem);

        theorems.put(name, PExp.buildPExp(quantifiersAppliedTheorem));
    }

    /**
     * <p>
     * Attempts to prove a collection of VCs. If this method returns without
     * throwing an exception, then all VCs were proved.
     * </p>
     * 
     * @param vCs
     *            A list of verification conditions in the form of
     *            <code>AssertiveCode</code>. May not be <code>null</code>.
     * @param theorems
     *            A list of theorems which may be applied. May not be
     *            <code>null</code>.
     * @param maxDepth
     *            The maximum number of substitutions the prover should attempt
     *            before giving up on a proof.
     * 
     * @throws UnableToProveException
     *             If a given VC cannot be proved in a reasonable amount of
     *             time.
     * @throws VCInconsistentException
     *             If a given VC can be proved inconsistent.
     * @throws NullPointerException
     *             If <code>vCs</code> or <code>theorems</code> is
     *             <code>null</code>.
     */
    private static ProverResult proveVCs(Iterable<VC> vcs,
            Map<String, PExp> theorems, CompileEnvironment environment) {

        ProverResult result = new ProverResult();

        Metrics metrics = new Metrics();

        VCProver p = setUpProver();

        for (VC vc : vcs) {
            result.addVCResult(vc, proveVC(vc, metrics, p));
        }

        return result;
    }

    /**
     * <p>
     * Attempts to prove a single VC. If this method returns without throwing an
     * exception, then the VC was proved.
     * </p>
     * 
     * @param vC
     *            The verification condition to be proved. May not be
     *            <code>null</code>.
     * @param theorems
     *            A list of theorems that may be applied as part of the proof.
     *            May not be <code>null</code>.
     * @param maxDepth
     *            The maximum number of steps the prover should attempt before
     *            giving up on a proof.
     * @param metrics
     *            A reference to the metrics the prover should keep on the proof
     *            in progress. May not be <code>null</code>.
     * @param p
     *            The prover to be used if we're using the new prover, or
     *            <code>null</code> if we're supposed to use to legacy prover.
     * 
     * @throws UnableToProveException
     *             If the VC cannot be proved in a reasonable amount of time.
     * @throws VCInconsistentException
     *             If the VC can be proved inconsistent.
     * @throws NullPointerException
     *             If <code>vC</code>, <code>theorems</code>, or
     *             <code>metrics</code> is <code>null</code>.
     */
    private static ProofResult proveVC(VC vC, Metrics metrics, VCProver p) {
        vC = vC.applyAntecedentExpansions();

        ActionCanceller c = new ActionCanceller();

        return p.prove(vC, new PrintingListener(), c);
    }

    private static VCProver setUpProver() {
        throw new UnsupportedOperationException();
    }

    public static void setUpFlags() {
        Flag[] someProveFlag = { FLAG_LEGACY_PROVE, FLAG_PROVE };
        FlagDependencies.addRequires(FLAG_DEBUG, someProveFlag);
        FlagDependencies.addRequires(FLAG_VERBOSE, someProveFlag);

        FlagDependencies.addImplies(FLAG_LEGACY_PROVE_ALIAS, FLAG_LEGACY_PROVE);

        FlagDependencies.addExcludes(FLAG_LEGACY_PROVE, FLAG_PROVE);

        FlagDependencies.addExcludes(FLAG_NOGUI, FLAG_DEBUG);

        FlagDependencies.addImplies(FLAG_PROVE, FLAG_SOME_PROVER);
        FlagDependencies.addImplies(FLAG_LEGACY_PROVE, FLAG_SOME_PROVER);

        FlagDependencies.addImplies(FLAG_SOME_PROVER, Verifier.FLAG_VERIFY_VC);
    }

    public static class PrintingListener implements ProverListener {

        @Override
        public void progressUpdate(double progress) {
            System.out.println("Progress: " + (progress * 100) + "%");
        }
    }
}
