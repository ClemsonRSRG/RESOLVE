/**
 * CongruenceClassProver.java
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
package edu.clemson.cs.r2jt.congruenceclassprover;

import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.rewriteprover.Prover;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PSymbol;
import edu.clemson.cs.r2jt.rewriteprover.Metrics;
import edu.clemson.cs.r2jt.rewriteprover.ProverListener;
import edu.clemson.cs.r2jt.rewriteprover.VC;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel;
import edu.clemson.cs.r2jt.typeandpopulate.*;
import edu.clemson.cs.r2jt.typeandpopulate.query.EntryTypeQuery;
import edu.clemson.cs.r2jt.typeandpopulate.entry.TheoremEntry;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import edu.clemson.cs.r2jt.misc.Flag;
import edu.clemson.cs.r2jt.misc.FlagDependencies;
import edu.clemson.cs.r2jt.misc.FlagManager;
import edu.clemson.cs.r2jt.vcgeneration.VCGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by mike on 4/4/2014.
 */
public final class CongruenceClassProver {

    public static final Flag FLAG_PROVE =
            new Flag(Prover.FLAG_SECTION_NAME, "ccprove",
                    "congruence closure based prover");
    private final List<VerificationConditionCongruenceClosureImpl> m_ccVCs;
    private final List<TheoremCongruenceClosureImpl> m_theorems;
    private final int MAX_ITERATIONS = 1024;
    private final CompileEnvironment m_environment;
    private final ModuleScope m_scope;
    private String m_results;
    private final long DEFAULTTIMEOUT = 15000;
    private final boolean SHOWRESULTSIFNOTPROVED = true;
    private final TypeGraph m_typeGraph;
    private boolean printVCEachStep = false;

    // only for webide ////////////////////////////////////
    private final PerVCProverModel[] myModels;
    private ProverListener myProverListener;
    private long myTimeout;
    private long totalTime = 0;
    private int numUsesBeforeQuit;
    private final int DEFAULTTRIES = -1;
    private static final String[] NUMTRIES_ARGS = { "numtries" };
    public static final Flag FLAG_NUMTRIES =
            new Flag("Proving", "num_tries",
                    "Prover will halt after this many timeouts.",
                    NUMTRIES_ARGS, Flag.Type.HIDDEN);

    ///////////////////////////////////////////////////////
    public static void setUpFlags() {
        /*FlagDependencies.addExcludes(FLAG_PROVE, Prover.FLAG_PROVE);
        FlagDependencies.addExcludes(FLAG_PROVE, Prover.FLAG_LEGACY_PROVE);
        FlagDependencies.addImplies(FLAG_PROVE, Prover.FLAG_SOME_PROVER);
         */

        // for new vc gen
        FlagDependencies.addImplies(CongruenceClassProver.FLAG_PROVE,
                VCGenerator.FLAG_ALTVERIFY_VC);
        FlagDependencies.addRequires(CongruenceClassProver.FLAG_NUMTRIES,
                CongruenceClassProver.FLAG_PROVE);
    }

    public CongruenceClassProver(TypeGraph g, List<VC> vcs, ModuleScope scope,
            CompileEnvironment environment, ProverListener listener) {

        // Only for web ide //////////////////////////////////////////
        myModels = new PerVCProverModel[vcs.size()];
        if (listener != null) {
            myProverListener = listener;
        }
        if (environment.flags.isFlagSet(Prover.FLAG_TIMEOUT)) {
            myTimeout =
                    Integer.parseInt(environment.flags.getFlagArgument(
                            Prover.FLAG_TIMEOUT, Prover.FLAG_TIMEOUT_ARG_NAME));
        }
        else {
            myTimeout = DEFAULTTIMEOUT;
        }
        if (environment.flags.isFlagSet(CongruenceClassProver.FLAG_NUMTRIES)) {
            numUsesBeforeQuit =
                    Integer.parseInt(environment.flags.getFlagArgument(
                            CongruenceClassProver.FLAG_NUMTRIES, "numtries"));
        }
        else {
            numUsesBeforeQuit = DEFAULTTRIES;
        }

        ///////////////////////////////////////////////////////////////
        totalTime = System.currentTimeMillis();
        m_typeGraph = g;
        m_ccVCs = new ArrayList<VerificationConditionCongruenceClosureImpl>();
        int i = 0;

        for (VC vc : vcs) {
            // make every PExp a PSymbol
            vc.convertAllToPsymbols(m_typeGraph);
            m_ccVCs.add(new VerificationConditionCongruenceClosureImpl(g, vc));
            myModels[i++] = (new PerVCProverModel(g, vc.getName(), vc, null));

        }
        m_theorems = new ArrayList<TheoremCongruenceClosureImpl>();
        List<TheoremEntry> theoremEntries =
                scope.query(new EntryTypeQuery(TheoremEntry.class,
                        MathSymbolTable.ImportStrategy.IMPORT_RECURSIVE,
                        MathSymbolTable.FacilityStrategy.FACILITY_IGNORE));

        for (TheoremEntry e : theoremEntries) {
            PExp assertion = e.getAssertion();
            String eName = e.getName();
            if (assertion.isEquality()) {
                addEqualityTheorem(true, assertion, eName);
                addEqualityTheorem(false, assertion, eName);
            }
            else {
                TheoremCongruenceClosureImpl t =
                        new TheoremCongruenceClosureImpl(g, assertion, false,
                                eName);
                if (!t.m_unneeded) {
                    m_theorems.add(t);
                }
                //addContrapositive(assertion, eName);
            }
        }
        insertDefaultTheorems();
        m_environment = environment;
        m_scope = scope;
        m_results = "";

    }

    private void insertDefaultTheorems() {
        MTType B = m_typeGraph.BOOLEAN;
        PSymbol p = new PSymbol(B, null, "p", PSymbol.Quantification.FOR_ALL);
        PSymbol q = new PSymbol(B, null, "q", PSymbol.Quantification.FOR_ALL);
        ArrayList<PExp> args = new ArrayList<PExp>();
        // p = not q implies q = not p
        args.add(q);
        PSymbol not_q = new PSymbol(B, null, "not", args);
        args.clear();
        args.add(p);
        args.add(not_q);
        PSymbol ant = new PSymbol(B, null, "=", args);
        args.clear();
        args.add(p);
        PSymbol not_p = new PSymbol(B, null, "not", args);
        args.clear();
        args.add(q);
        args.add(not_p);
        PSymbol succ = new PSymbol(B, null, "=", args);
        args.clear();
        args.add(ant);
        args.add(succ);
        PSymbol th1 = new PSymbol(B, null, "implies", args);
        m_theorems.add(new TheoremCongruenceClosureImpl(m_typeGraph, th1,
                false, "Default theorem 1"));
        // not not p = p
        args.clear();
        args.add(not_p);
        PSymbol nnp = new PSymbol(B, null, "not", args);
        args.clear();
        args.add(nnp);
        args.add(p);
        PSymbol th2 = new PSymbol(B, null, "=", args);
        addEqualityTheorem(true, th2, "Default theorem 2");
        // p and p = p
        args.clear();
        args.add(p);
        args.add(p);
        PSymbol lhs = new PSymbol(B, null, "and", args);
        args.clear();
        args.add(lhs);
        args.add(p);
        PSymbol th3 = new PSymbol(B, null, "=", args);
        addEqualityTheorem(true, th3, "Default theorem 3");

        // p and true = p
        args.clear();
        args.add(p);
        PSymbol t = new PSymbol(B, null, "true");
        args.add(t);
        PSymbol pandt = new PSymbol(B, null, "and", args);
        args.clear();
        args.add(pandt);
        args.add(p);
        PSymbol th4 = new PSymbol(B, null, "=", args);
        addEqualityTheorem(true, th4, "Default theorem 4");

        // not p = true implies p = false
        args.clear();
        args.add(not_p);
        args.add(t);
        ant = new PSymbol(B, null, "=", args);
        args.clear();
        PSymbol f = new PSymbol(B, null, "false");
        args.add(p);
        args.add(f);
        PSymbol suc = new PSymbol(B, null, "=", args);
        args.clear();
        args.add(ant);
        args.add(suc);
        PSymbol th5 = new PSymbol(B, null, "implies", args);
        m_theorems.add(new TheoremCongruenceClosureImpl(m_typeGraph, th5,
                false, "Default theorem 5"));
    }

    private void addContrapositive(PExp theorem, String thName) {
        if (!theorem.getTopLevelOperation().equals("implies"))
            return;
        PExp oldAnt = theorem.getSubExpressions().get(0);
        PExp oldSuc = theorem.getSubExpressions().get(1);
        ArrayList<PExp> args = new ArrayList<PExp>();
        args.add(oldSuc);
        PExp ant =
                new PSymbol(oldSuc.getType(), oldSuc.getTypeValue(), "not",
                        args);
        args.clear();
        args.add(oldAnt);
        PExp suc =
                new PSymbol(oldAnt.getType(), oldAnt.getTypeValue(), "not",
                        args);
        args.clear();
        args.add(ant);
        args.add(suc);
        PExp contraP = new PSymbol(m_typeGraph.BOOLEAN, null, "implies", args);
        TheoremCongruenceClosureImpl contra =
                new TheoremCongruenceClosureImpl(m_typeGraph, contraP, false,
                        "Contrapositive(" + thName + ")");

        if (!contra.m_unneeded)
            m_theorems.add(contra);
    }

    private void addEqualityTheorem(boolean matchLeft, PExp theorem,
            String thName) {
        PExp lhs, rhs;

        if (matchLeft) {
            lhs = theorem.getSubExpressions().get(0);
            rhs = theorem.getSubExpressions().get(1);
        }
        else {
            lhs = theorem.getSubExpressions().get(1);
            rhs = theorem.getSubExpressions().get(0);
        }
        // Because only lhs is matched, all quantified variables used must be in lhs
        Set<PSymbol> lhsQuants = lhs.getQuantifiedVariables();
        Set<PSymbol> rhsQuants = rhs.getQuantifiedVariables();
        if (!lhsQuants.containsAll(rhsQuants)) {
            return;
        }

        TheoremCongruenceClosureImpl t =
                new TheoremCongruenceClosureImpl(m_typeGraph, lhs, theorem,
                        false, false, thName);
        if (!t.m_unneeded) {
            m_theorems.add(t);
        }

        if (lhs.isEquality()) {
            t =
                    new TheoremCongruenceClosureImpl(m_typeGraph, lhs, theorem,
                            true, false, thName);
            if (!t.m_unneeded) {
                m_theorems.add(t);
            }
        }
    }

    public void start() throws IOException {

        String summary = "";
        int i = 0;
        int numUnproved = 0;
        for (VerificationConditionCongruenceClosureImpl vcc : m_ccVCs) {
            //printVCEachStep = true;
            //if(!vcc.m_name.equals("2_12"))continue;
            long startTime = System.nanoTime();
            String whyQuit = "";
            // Skip proof loop
            if (numUsesBeforeQuit >= 0 && numUnproved >= numUsesBeforeQuit) {
                if (myProverListener != null) {
                    myProverListener.vcResult(false, myModels[i], new Metrics(
                            0, 0));
                }
                summary += vcc.m_name + " skipped\n";
                ++i;
                continue;
            }
            VerificationConditionCongruenceClosureImpl.STATUS proved =
                    prove(vcc);
            if (proved
                    .equals(VerificationConditionCongruenceClosureImpl.STATUS.PROVED)) {
                whyQuit += " Proved ";
            }
            else if (proved
                    .equals(VerificationConditionCongruenceClosureImpl.STATUS.FALSE_ASSUMPTION)) {
                whyQuit += " Proved (Assumption(s) false) ";
            }
            else if (proved
                    .equals(VerificationConditionCongruenceClosureImpl.STATUS.STILL_EVALUATING)) {
                whyQuit += " Out of theorems, or timed out ";
                numUnproved++;
            }
            else
                whyQuit += " Goal false "; // this isn't currently reachable

            long endTime = System.nanoTime();
            long delayNS = endTime - startTime;
            long delayMS =
                    TimeUnit.MILLISECONDS
                            .convert(delayNS, TimeUnit.NANOSECONDS);
            summary += vcc.m_name + whyQuit + " time: " + delayMS + " ms\n";
            if (myProverListener != null) {
                myProverListener
                        .vcResult(
                                (proved == (VerificationConditionCongruenceClosureImpl.STATUS.PROVED) || (proved == VerificationConditionCongruenceClosureImpl.STATUS.FALSE_ASSUMPTION)),
                                myModels[i], new Metrics(delayMS, myTimeout));
            }

            i++;

        }
        totalTime = System.currentTimeMillis() - totalTime;
        summary +=
                "Elapsed time from construction: " + totalTime + " ms" + "\n";
        String div = divLine("Summary");
        summary = div + summary + div;

        if (!m_environment.isWebIDEFlagSet()) {
            if (!FlagManager.getInstance().isFlagSet("nodebug")) {
                System.out.println(m_results + summary);
            }
            m_results = summary + m_results;

            outputProofFile();
        }
    }

    private String divLine(String label) {
        if (label.length() > 78) {
            label = label.substring(0, 77);
        }
        label = " " + label + " ";
        char[] div = new char[80];
        Arrays.fill(div, '=');
        int start = 40 - label.length() / 2;
        for (int i = start, j = 0; j < label.length(); ++i, ++j) {
            div[i] = label.charAt(j);
        }
        return new String(div) + "\n";
    }

    /* while not proved do
        rank theorems
            while top rank below threshold score do
                apply top rank if not in exclusion list
                insert top rank
                add inserted expression to exclusion list
                choose new top rank

     */
    protected VerificationConditionCongruenceClosureImpl.STATUS prove(
            VerificationConditionCongruenceClosureImpl vcc) {
        long startTime = System.currentTimeMillis();
        long endTime = myTimeout + startTime;
        HashSet<String> applied = new HashSet<String>();
        Map<String, Integer> theoremAppliedCount =
                new HashMap<String, Integer>();
        VerificationConditionCongruenceClosureImpl.STATUS status =
                vcc.isProved();
        String div = divLine(vcc.m_name);
        String theseResults =
                div + ("Before application of theorems: " + vcc + "\n");
        ArrayList<TheoremCongruenceClosureImpl> theoremsForThisVC =
                new ArrayList<TheoremCongruenceClosureImpl>();
        theoremsForThisVC.addAll(m_theorems);
        // add quantified expressions local to the vc to theorems

        for (PExp p : vcc.forAllQuantifiedPExps) {
            TheoremCongruenceClosureImpl t =
                    new TheoremCongruenceClosureImpl(m_typeGraph, p, true,
                            "Created from lamba exp in VC");
            if (!t.m_unneeded) {
                theoremsForThisVC.add(t);
            }
            // make a setCons(x)
            if (p.getSubExpressions().size() == 2
                    && p.getSubExpressions().get(1).getType().isBoolean())
                vcc.assertSet(p, m_scope);

        }
        int iteration = 0;
        while (status
                .equals(VerificationConditionCongruenceClosureImpl.STATUS.STILL_EVALUATING)
                && System.currentTimeMillis() <= endTime) {
            // Rank theorems
            Map<String, Integer> vcSymbolRelevanceMap = vcc.getGoalSymbols();
            int threshold = 16 * vcSymbolRelevanceMap.keySet().size() + 1;
            TheoremPrioritizer rankedTheorems =
                    new TheoremPrioritizer(theoremsForThisVC,
                            vcSymbolRelevanceMap, threshold,
                            theoremAppliedCount, vcc.getRegistry());
            //theseResults += "Iteration " + iteration++ + "\n";
            int max_Theorems_to_choose = 1;
            int num_Theorems_chosen = 0;
            long timeAtLastIter = System.currentTimeMillis();
            while (!rankedTheorems.m_pQueue.isEmpty()
                    && status
                            .equals(VerificationConditionCongruenceClosureImpl.STATUS.STILL_EVALUATING)
                    && (num_Theorems_chosen < max_Theorems_to_choose)) {
                int theoremScore = rankedTheorems.m_pQueue.peek().m_score;
                TheoremCongruenceClosureImpl cur = rankedTheorems.poll();
                long time_at_selection = System.currentTimeMillis();
                ArrayList<InsertExpWithJustification> instantiatedTheorems =
                        cur.applyTo(vcc, endTime);
                if (instantiatedTheorems != null
                        && instantiatedTheorems.size() != 0) {
                    InstantiatedTheoremPrioritizer instPQ =
                            new InstantiatedTheoremPrioritizer(
                                    instantiatedTheorems, vcSymbolRelevanceMap,
                                    threshold, vcc.getRegistry());
                    int max_Instantiated_to_Add = 1;
                    int num_Instantiated_added = 0;
                    while (num_Instantiated_added < max_Instantiated_to_Add
                            && !instPQ.m_pQueue.isEmpty()) {
                        PExpWithScore curP = instPQ.m_pQueue.poll();

                        if (!applied.contains(curP.m_theorem.toString())) {
                            String substitutionMade =
                                    vcc
                                            .getConjunct()
                                            .addExpressionAndTrackChanges(
                                                    curP.m_theorem,
                                                    endTime,
                                                    curP.m_theoremDefinitionString);
                            if (substitutionMade != "") {
                                applied.add(curP.m_theorem.toString());
                                theseResults +=
                                        "Iter:"
                                                + ++iteration
                                                + " Iter Time: "
                                                + (System.currentTimeMillis() - time_at_selection)
                                                + " Elapsed Time: "
                                                + (System.currentTimeMillis() - startTime)
                                                + "\n[" + theoremScore + "]"
                                                + curP.toString() + "\t"
                                                + substitutionMade + "\n\n";
                                if (printVCEachStep)
                                    theseResults += vcc.toString();
                                timeAtLastIter = System.currentTimeMillis();
                                status = vcc.isProved();
                                num_Instantiated_added++;
                                int count = 0;
                                if (theoremAppliedCount
                                        .containsKey(cur.m_theoremString))
                                    count =
                                            theoremAppliedCount
                                                    .get(cur.m_theoremString);
                                theoremAppliedCount.put(cur.m_theoremString,
                                        ++count);
                                num_Theorems_chosen++;

                            }
                        }
                    }
                }
                else {
                    //theseResults +=
                    //        "Neg result on: " + cur.m_theoremString + "\n";
                }
            }
        }
        m_results += theseResults + div;
        return vcc.isProved();

    }

    private String proofFileName() {
        File file = m_environment.getTargetFile();
        ModuleID cid = m_environment.getModuleID(file);
        file = m_environment.getFile(cid);
        String filename = file.toString();
        int temp = filename.indexOf(".");
        String tempfile = filename.substring(0, temp);
        String mainFileName;
        mainFileName = tempfile + ".cc.proof";
        return mainFileName;
    }

    private void outputProofFile() throws IOException {
        FileWriter w = new FileWriter(new File(proofFileName()));

        w.write("Proofs for " + m_scope.getModuleIdentifier() + " generated "
                + new Date() + "\n\n");

        w.write(m_results);
        w.write("\n");
        w.flush();
        w.close();
    }

}
