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
import edu.clemson.cs.r2jt.misc.Flag;
import edu.clemson.cs.r2jt.misc.FlagDependencies;
import edu.clemson.cs.r2jt.misc.FlagManager;
import edu.clemson.cs.r2jt.rewriteprover.Metrics;
import edu.clemson.cs.r2jt.rewriteprover.Prover;
import edu.clemson.cs.r2jt.rewriteprover.ProverListener;
import edu.clemson.cs.r2jt.rewriteprover.VC;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PSymbol;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleScope;
import edu.clemson.cs.r2jt.typeandpopulate.SymbolNotOfKindTypeException;
import edu.clemson.cs.r2jt.typeandpopulate.entry.MathSymbolEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.TheoremEntry;
import edu.clemson.cs.r2jt.typeandpopulate.query.EntryTypeQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.NameQuery;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
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
    private static final String[] NUMTRIES_ARGS = {"numtries"};
    public static final Flag FLAG_NUMTRIES =
            new Flag("Proving", "num_tries",
                    "Prover will halt after this many timeouts.",
                    NUMTRIES_ARGS, Flag.Type.HIDDEN);
    private final List<VerificationConditionCongruenceClosureImpl> m_ccVCs;
    private final List<TheoremCongruenceClosureImpl> m_theorems;
    private final int MAX_ITERATIONS = 1024;
    private final CompileEnvironment m_environment;
    private final ModuleScope m_scope;
    private final long DEFAULTTIMEOUT = 5000;
    private final boolean SHOWRESULTSIFNOTPROVED = true;
    private final TypeGraph m_typeGraph;
    // only for webide ////////////////////////////////////
    private final PerVCProverModel[] myModels;
    private final int numUsesBeforeQuit; // weird bug if this isn't final
    private final int DEFAULTTRIES = -1;
    private String m_results;
    private boolean printVCEachStep = false;
    private long theoremSelectTime = 0;
    private long resultSelectTime = 0;
    private long searchTime = 0;
    private long constructionTime = 0;
    private long applyTime = 0;
    private ProverListener myProverListener;
    private long myTimeout;
    private long totalTime = 0;

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
        } else {
            myTimeout = DEFAULTTIMEOUT;
        }
        if (environment.flags.isFlagSet(CongruenceClassProver.FLAG_NUMTRIES)) {
            numUsesBeforeQuit =
                    Integer.parseInt(environment.flags.getFlagArgument(
                            CongruenceClassProver.FLAG_NUMTRIES, "numtries"));
        } else {
            numUsesBeforeQuit = DEFAULTTRIES;
        }

        ///////////////////////////////////////////////////////////////
        totalTime = System.currentTimeMillis();
        m_typeGraph = g;
        m_ccVCs = new ArrayList<VerificationConditionCongruenceClosureImpl>();
        int i = 0;

        m_theorems = new ArrayList<TheoremCongruenceClosureImpl>();
        List<TheoremEntry> theoremEntries =
                scope.query(new EntryTypeQuery(TheoremEntry.class,
                        MathSymbolTable.ImportStrategy.IMPORT_RECURSIVE,
                        MathSymbolTable.FacilityStrategy.FACILITY_IGNORE));
        List<edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry> z_entries =
                scope.query(new NameQuery(null, "Z",
                        MathSymbolTable.ImportStrategy.IMPORT_RECURSIVE,
                        MathSymbolTable.FacilityStrategy.FACILITY_INSTANTIATE,
                        false));
        MTType z = null;
        if (z_entries != null && z_entries.size() > 0) {
            MathSymbolEntry z_e = (MathSymbolEntry) z_entries.get(0);
            try {
                z = z_e.getTypeValue();
            } catch (SymbolNotOfKindTypeException e) {
                e.printStackTrace();
            }
        }

        List<edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry> n_entries =
                scope.query(new NameQuery(null, "N",
                        MathSymbolTable.ImportStrategy.IMPORT_RECURSIVE,
                        MathSymbolTable.FacilityStrategy.FACILITY_INSTANTIATE,
                        false));
        MTType n = null;
        if (n_entries != null && n_entries.size() > 0) {
            MathSymbolEntry n_e = (MathSymbolEntry) n_entries.get(0);
            try {
                n = n_e.getTypeValue();
            } catch (SymbolNotOfKindTypeException e) {
                e.printStackTrace();
            }
        }
        for (VC vc : vcs) {
            // make every PExp a PSymbol
            vc.convertAllToPsymbols(m_typeGraph);
            m_ccVCs.add(new VerificationConditionCongruenceClosureImpl(g, vc,
                    z, n));
            myModels[i++] = (new PerVCProverModel(g, vc.getName(), vc, null));

        }
        for (TheoremEntry e : theoremEntries) {
            PExp assertion =
                    Utilities.replacePExp(e.getAssertion(), m_typeGraph, z, n);
            String eName = e.getName();
            if (assertion.isEquality()
                    && assertion.getQuantifiedVariables().size() > 0) {
                addEqualityTheorem(true, assertion, eName + "_left"); // match left
                addEqualityTheorem(false, assertion, eName + "_right"); // match right
                /*m_theorems.add(new TheoremCongruenceClosureImpl(g, assertion,
                        false, eName + "_whole")); // match whole*/
            } else {
                if (assertion.getTopLevelOperation().equals("implies")) {
                    addGoalSearchingTheorem(assertion, eName);
                }
                TheoremCongruenceClosureImpl t =
                        new TheoremCongruenceClosureImpl(g, assertion, false,
                                eName);
                m_theorems.add(t);
                //addContrapositive(assertion, eName);
            }
        }
        m_environment = environment;
        m_scope = scope;
        m_results = "";

    }

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

    private void addEqualityTheorem(boolean matchLeft, PExp theorem,
                                    String thName) {
        PExp lhs, rhs;

        if (matchLeft) {
            lhs = theorem.getSubExpressions().get(0);
            rhs = theorem.getSubExpressions().get(1);
        } else {
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
        m_theorems.add(t);
    }

    // forall x. p(x) -> q(x) to
    // forall x,y,_g.((q(x) = _g) and p(x) = y)
    //              -> (_g = (y or _g)) :: y -> g with or and =
    // the above is too restrictive
    // forall x. p(x) -> q(x) to
    // forall x,y,_g.((q(x) = _g) )
    //              -> (_g = (p(x) or _g))
    // the idea is to find q(x) = g, then add all p(x,y) we can find to goal
    private void addGoalSearchingTheorem(PExp theorem, String name) {
        // search method will do a search for each current goal, replacing _g with goal in the binding map
        ArrayList<PExp> args = new ArrayList<PExp>();
        PSymbol goal = new PSymbol(m_typeGraph.BOOLEAN, null, "_g", PSymbol.Quantification.FOR_ALL);
        args.add(theorem.getSubExpressions().get(1));
        args.add(goal);
        PSymbol ant = new PSymbol(m_typeGraph.BOOLEAN, null, "=", args);
        args.clear();
        args.add(theorem.getSubExpressions().get(0));
        args.add(goal);
        PSymbol pOrG = new PSymbol(m_typeGraph.BOOLEAN, null, "or", args);
        args.clear();
        args.add(pOrG);
        args.add(goal);
        PSymbol consq = new PSymbol(m_typeGraph.BOOLEAN, null, "=", args);
        args.clear();
        args.add(ant);
        args.add(consq);
        PSymbol gSTheorem = new PSymbol(m_typeGraph.BOOLEAN, null, "implies", args);
        TheoremCongruenceClosureImpl t =
                new TheoremCongruenceClosureImpl(m_typeGraph, gSTheorem, false,
                        name + "_goalSearch");
        m_theorems.add(t);
    }
    // forall x. p(x) -> q(x) to
    // forall x,y,_g.((q(x) = _g) and p(x) = y)
    //              -> (_g = (p(x) or _g)) :: p(x) -> g with or and =
/*    private void addGoalSearchingTheorem(PExp theorem, String name) {
        // search method will do a search for each current goal, replacing _g with goal in the binding map
        ArrayList<PExp> args = new ArrayList<PExp>();
        PSymbol goal = new PSymbol(m_typeGraph.BOOLEAN, null, "_g", PSymbol.Quantification.FOR_ALL);
        args.add(theorem.getSubExpressions().get(1));
        args.add(goal);
        PSymbol qEqG = new PSymbol(m_typeGraph.BOOLEAN, null, "=", args);
        args.clear();
        PSymbol y = new PSymbol(m_typeGraph.BOOLEAN, null, "_y", PSymbol.Quantification.FOR_ALL);
        args.add(theorem.getSubExpressions().get(0));
        args.add(y);
        PSymbol pEqY = new PSymbol(m_typeGraph.BOOLEAN, null, "=", args);
        args.clear();
        args.add(qEqG);
        args.add(pEqY);
        PSymbol ant = new PSymbol(m_typeGraph.BOOLEAN, null, "and", args);
        args.clear();
        args.add(theorem.getSubExpressions().get(0));
        args.add(goal);
        PSymbol pOrG = new PSymbol(m_typeGraph.BOOLEAN, null, "or", args);
        args.clear();
        args.add(pOrG);
        args.add(goal);
        PSymbol consq = new PSymbol(m_typeGraph.BOOLEAN, null, "=", args);
        args.clear();
        args.add(ant);
        args.add(consq);
        PSymbol gSTheorem = new PSymbol(m_typeGraph.BOOLEAN, null, "implies", args);
        TheoremCongruenceClosureImpl t =
                new TheoremCongruenceClosureImpl(m_typeGraph, gSTheorem, false,
                        name + "_goalSearch");
        m_theorems.add(t);
    }
*/
    public void start() throws IOException {

        String summary = "";
        int i = 0;
        int numUnproved = 0;
        for (VerificationConditionCongruenceClosureImpl vcc : m_ccVCs) {
            //printVCEachStep = true;
            //if (!vcc.m_name.equals("0_3")) continue;
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
            } else if (proved
                    .equals(VerificationConditionCongruenceClosureImpl.STATUS.FALSE_ASSUMPTION)) {
                whyQuit += " Proved (Assumption(s) false) ";
            } else if (proved
                    .equals(VerificationConditionCongruenceClosureImpl.STATUS.STILL_EVALUATING)) {
                whyQuit += " Out of theorems, or timed out ";
                numUnproved++;
            } else
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
                "Elapsed time from construction: "
                        + totalTime
                        + " ms"
                        + "\n"
                        + "constructionTime:\t"
                        + 100
                        * (float) constructionTime
                        / totalTime
                        + "%\n"
                        + "theoremSelectTime:\t"
                        + 100
                        * (float) theoremSelectTime
                        / totalTime
                        + "%\n"
                        + "resultSelectTime:\t"
                        + 100
                        * (float) resultSelectTime
                        / totalTime
                        + "%\n"
                        + "searchTime:\t\t\t"
                        + 100
                        * (float) searchTime
                        / totalTime
                        + "%\n"
                        + "applyTime:\t\t\t"
                        + 100
                        * (float) applyTime
                        / totalTime
                        + "%\n"
                        + "not counted:\t\t"
                        + 100
                        * (float) (totalTime - (constructionTime
                        + theoremSelectTime + resultSelectTime
                        + searchTime + applyTime)) / totalTime + "%\n";
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

        int iteration = 0;
        chooseNewTheorem:
        while (status
                .equals(VerificationConditionCongruenceClosureImpl.STATUS.STILL_EVALUATING)
                && System.currentTimeMillis() <= endTime) {
            long time_at_theorem_pq_creation = System.currentTimeMillis();
            iteration++;
            // ++++++ Creates new PQ with all the theorems
            TheoremPrioritizer rankedTheorems =
                    new TheoremPrioritizer(theoremsForThisVC,
                            theoremAppliedCount, vcc.getRegistry());
            theoremSelectTime +=
                    System.currentTimeMillis() - time_at_theorem_pq_creation;
            int max_Theorems_to_choose = 1;
            int num_Theorems_chosen = 0;
            while (!rankedTheorems.m_pQueue.isEmpty()
                    && status
                    .equals(VerificationConditionCongruenceClosureImpl.STATUS.STILL_EVALUATING)
                    && (num_Theorems_chosen < max_Theorems_to_choose)) {
                // +++++++ Chooses top of uninstantiated theorem PQ
                long time_at_selection = System.currentTimeMillis();
                int theoremScore = rankedTheorems.m_pQueue.peek().m_score;
                TheoremCongruenceClosureImpl cur = rankedTheorems.poll();
                // Mark as used
                int count = 0;
                if (theoremAppliedCount.containsKey(cur.m_name))
                    count = theoremAppliedCount.get(cur.m_name);
                theoremAppliedCount.put(cur.m_name, ++count);
                // We are using it, even if it makes no difference
                theoremSelectTime +=
                        System.currentTimeMillis() - time_at_selection;
                long t0 = System.currentTimeMillis();
                ArrayList<InsertExpWithJustification> instantiatedTheorems =
                        cur.applyTo(vcc, endTime);
                searchTime += System.currentTimeMillis() - t0;
                if (instantiatedTheorems != null
                        && instantiatedTheorems.size() != 0) {
                    long t1 = System.currentTimeMillis();
                    InstantiatedTheoremPrioritizer instPQ =
                            new InstantiatedTheoremPrioritizer(
                                    instantiatedTheorems, vcc);
                    resultSelectTime += System.currentTimeMillis() - t1;
                    String substitutionMade = "";
                    while (!instPQ.m_pQueue.isEmpty() && (instPQ.m_pQueue.peek().m_score == 0 || substitutionMade.equals(""))) {
                        PExpWithScore curP = instPQ.m_pQueue.poll();

                        if (!applied.contains(curP.m_theorem.toString())) {
                            long t2 = System.currentTimeMillis();
                            substitutionMade =
                                    vcc
                                            .getConjunct()
                                            .addExpressionAndTrackChanges(
                                                    curP.m_theorem,
                                                    endTime,
                                                    curP.m_theoremDefinitionString);
                            applyTime += System.currentTimeMillis() - t2;
                            applied.add(curP.m_theorem.toString());
                            if (cur.m_noQuants) {
                                theoremsForThisVC.remove(cur);
                            }
                        }
                        if (!substitutionMade.equals("")) {
                            long curTime = System.currentTimeMillis();
                            theseResults +=
                                    "Iter:"
                                            + iteration
                                            + " Iter Time: "
                                            + (curTime - time_at_theorem_pq_creation)
                                            + " Search Time for this theorem: "
                                            + (curTime - time_at_selection)
                                            + " Elapsed Time: "
                                            + (curTime - startTime) + "\n["
                                            + theoremScore + "]"
                                            + curP.toString() + "\t"
                                            + substitutionMade + "\n\n";
                            if (printVCEachStep)
                                theseResults += vcc.toString();
                            status = vcc.isProved();
                            num_Theorems_chosen++;
                            //continue chooseNewTheorem;
                        }

                    }
                    if (substitutionMade == "") {
                        theseResults +=
                                "Emptied queue for "
                                        + cur.m_name
                                        + " with no new results ["
                                        + (System.currentTimeMillis() - time_at_selection)
                                        + "ms]\n\n";
                    }
                } else {
                    theseResults +=
                            "Could not find any matches for "
                                    + cur.m_name
                                    + "["
                                    + (System.currentTimeMillis() - time_at_selection)
                                    + "ms]\n\n";
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
