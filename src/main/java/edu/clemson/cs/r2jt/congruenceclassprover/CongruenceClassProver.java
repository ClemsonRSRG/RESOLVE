/**
 * CongruenceClassProver.java
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

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.proving.Antecedent;
import edu.clemson.cs.r2jt.proving.Prover;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PLambda;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol;
import edu.clemson.cs.r2jt.proving2.Consequent;
import edu.clemson.cs.r2jt.proving2.Metrics;
import edu.clemson.cs.r2jt.proving2.ProverListener;
import edu.clemson.cs.r2jt.proving2.VC;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.typeandpopulate.query.EntryTypeQuery;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleScope;
import edu.clemson.cs.r2jt.typeandpopulate.entry.TheoremEntry;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.utilities.FlagDependencies;
import edu.clemson.cs.r2jt.utilities.FlagManager;
import edu.clemson.cs.r2jt.vcgeneration.VCGenerator;
import edu.clemson.cs.r2jt.verification.Verifier;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by mike on 4/4/2014.
 */
public class CongruenceClassProver {

    public static final Flag FLAG_PROVE =
            new Flag(Prover.FLAG_SECTION_NAME, "ccprove",
                    "congruence closure based prover");
    private final List<VerificationConditionCongruenceClosureImpl> m_ccVCs;
    private final List<TheoremCongruenceClosureImpl> m_theorems;
    private final int MAX_ITERATIONS = 1024;
    private final CompileEnvironment m_environment;
    private final ModuleScope m_scope;
    private String m_results;
    private final long DEFAULTTIMEOUT = 10000;
    private final boolean SHOWRESULTSIFNOTPROVED = true;
    private final TypeGraph m_typeGraph;
    private final boolean DO_NOT_INTRODUCE_NEW_OPERATORS = true;

    // only for webide ////////////////////////////////////
    private final PerVCProverModel[] myModels;
    private final List<ProverListener> myProverListeners =
            new LinkedList<ProverListener>();
    private final long myTimeout;
    private long totalTime = 0;

    ///////////////////////////////////////////////////////
    public static void setUpFlags() {
        /*FlagDependencies.addExcludes(FLAG_PROVE, Prover.FLAG_PROVE);
        FlagDependencies.addExcludes(FLAG_PROVE, Prover.FLAG_LEGACY_PROVE);
        FlagDependencies.addImplies(FLAG_PROVE, Prover.FLAG_SOME_PROVER);
         */

        // for new vc gen
        FlagDependencies.addImplies(FLAG_PROVE, VCGenerator.FLAG_ALTVERIFY_VC);
    }

    public CongruenceClassProver(TypeGraph g, List<VC> vcs, ModuleScope scope,
            CompileEnvironment environment, ProverListener listener) {

        // Only for web ide //////////////////////////////////////////
        myModels = new PerVCProverModel[vcs.size()];
        if (listener != null) {
            myProverListeners.add(listener);
        }
        if (environment.flags.isFlagSet(Prover.FLAG_TIMEOUT)) {
            myTimeout =
                    Integer.parseInt(environment.flags.getFlagArgument(
                            Prover.FLAG_TIMEOUT, Prover.FLAG_TIMEOUT_ARG_NAME));
        }
        else {
            myTimeout = DEFAULTTIMEOUT;
        }
        ///////////////////////////////////////////////////////////////
        totalTime = System.currentTimeMillis();
        m_typeGraph = g;
        m_ccVCs = new ArrayList<VerificationConditionCongruenceClosureImpl>();
        int i = 0;

        for (VC vc : vcs) {
            //if(!vc.getName().equals("3_1")) continue;
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

            if (assertion.isEquality()) {
                addEqualityTheorem(true, assertion);
                addEqualityTheorem(false, assertion);
            }
            else {
                TheoremCongruenceClosureImpl t =
                        new TheoremCongruenceClosureImpl(g, assertion);
                if (!t.m_unneeded) {
                    m_theorems.add(t);
                }
            }
        }
        m_environment = environment;
        m_scope = scope;
        m_results = "";

    }

    private void addEqualityTheorem(boolean matchLeft, PExp theorem) {
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
                        false);
        if (!t.m_unneeded) {
            m_theorems.add(t);
        }

        if (lhs.isEquality()) {
            t =
                    new TheoremCongruenceClosureImpl(m_typeGraph, lhs, theorem,
                            true);
            if (!t.m_unneeded) {
                m_theorems.add(t);
            }
        }
    }

    public void start() throws IOException {

        String summary = "";
        int i = 0;
        for (VerificationConditionCongruenceClosureImpl vcc : m_ccVCs) {
            long startTime = System.nanoTime();
            String whyQuit = "";
            VerificationConditionCongruenceClosureImpl.STATUS proved =
                    alt_prove(vcc);
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
            }
            else
                whyQuit += " Goal false ";

            long endTime = System.nanoTime();
            long delayNS = endTime - startTime;
            long delayMS =
                    TimeUnit.MILLISECONDS
                            .convert(delayNS, TimeUnit.NANOSECONDS);
            summary += vcc.m_name + whyQuit + " time: " + delayMS + " ms\n";

            for (ProverListener l : myProverListeners) {
                l
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
        if (!FlagManager.getInstance().isFlagSet("nodebug")) {
            System.out.println(m_results + summary);
        }
        m_results = summary + m_results;

        outputProofFile();
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
    protected VerificationConditionCongruenceClosureImpl.STATUS alt_prove(
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
        // add quantified expressions local to the vc to theorems
        for (PExp p : vcc.forAllQuantifiedPExps) {
            TheoremCongruenceClosureImpl t =
                    new TheoremCongruenceClosureImpl(m_typeGraph, p);
            if (!t.m_unneeded) {
                m_theorems.add(t);
            }
        }
        int iteration = 0;
        while (status
                .equals(VerificationConditionCongruenceClosureImpl.STATUS.STILL_EVALUATING)
                && System.currentTimeMillis() <= endTime) {
            // Rank theorems
            Map<String, Integer> vcSymbolRelevanceMap = vcc.getGoalSymbols();
            int threshold = 16 * vcSymbolRelevanceMap.keySet().size() + 1;
            TheoremPrioritizer rankedTheorems =
                    new TheoremPrioritizer(m_theorems, vcSymbolRelevanceMap,
                            threshold, theoremAppliedCount);
            //theseResults += "Iteration " + iteration++ + "\n";
            int max_Theorems_to_choose = 1;
            int num_Theorems_chosen = 0;
            while (!rankedTheorems.m_pQueue.isEmpty()
                    && status
                            .equals(VerificationConditionCongruenceClosureImpl.STATUS.STILL_EVALUATING)
                    && (num_Theorems_chosen < max_Theorems_to_choose)) {
                int theoremScore = rankedTheorems.m_pQueue.peek().m_score;
                TheoremCongruenceClosureImpl cur = rankedTheorems.poll();

                ArrayList<InsertExpWithJustification> instantiatedTheorems =
                        cur.applyTo(vcc, endTime);
                if (instantiatedTheorems != null
                        && instantiatedTheorems.size() != 0) {
                    InstantiatedTheoremPrioritizer instPQ =
                            new InstantiatedTheoremPrioritizer(
                                    instantiatedTheorems, vcSymbolRelevanceMap,
                                    threshold);
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
                                        "Iter:" + ++iteration + "\n["
                                                + theoremScore + "]"
                                                + curP.toString()
                                                + "\t" + substitutionMade + "\n\n";
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
            }
        }
        m_results += theseResults + div;
        return vcc.isProved();

    }

    protected VerificationConditionCongruenceClosureImpl.STATUS prove(
            VerificationConditionCongruenceClosureImpl vcc) {
        List<TheoremCongruenceClosureImpl> allFuncNamesInVC =
                new ArrayList<TheoremCongruenceClosureImpl>();
        List<String> theoremsApplied = new ArrayList<String>();
        // Remove theorems with no function names in the vc.
        if (vcc
                .isProved()
                .equals(
                        VerificationConditionCongruenceClosureImpl.STATUS.STILL_EVALUATING)) {
            if (DO_NOT_INTRODUCE_NEW_OPERATORS) {

                Set<String> vcFunctionNames = vcc.getFunctionNames();
                // adding operators from boolean theory to default set.
                vcFunctionNames.add("=");
                vcFunctionNames.add("true");
                vcFunctionNames.add("false");
                vcFunctionNames.add("or");
                vcFunctionNames.add("not");
                vcFunctionNames.add("and");
                vcFunctionNames.add("implies");

                // if one or more of these are used, add the rest
                HashSet<String> intTheoryNames = new HashSet<String>();
                //intTheoryNames.add(">");
                intTheoryNames.add("<");
                //intTheoryNames.add(">=");
                intTheoryNames.add("<=");
                intTheoryNames.add("/=");
                intTheoryNames.add("+");
                intTheoryNames.add("-");
                intTheoryNames.add("suc");
                intTheoryNames.add("neg");
                intTheoryNames.add("NB");
                intTheoryNames.add("Is_Neg");
                HashSet<String> intersect = new HashSet<String>(intTheoryNames);

                intersect.retainAll(vcFunctionNames);
                if (!intersect.isEmpty()) {
                    vcFunctionNames.addAll(intTheoryNames);
                }
                if (vcFunctionNames.contains("Iterated_Concatenation")) {
                    vcFunctionNames.add("K_Z");
                    vcFunctionNames.add("Summation");
                    vcFunctionNames.add("Length");
                    vcFunctionNames.add("Stringify");
                    vcFunctionNames.add("Composition_of_Z_SStr_Z");
                    vcFunctionNames.add("Composition_of_Z_Entity_SStr");
                }
                for (TheoremCongruenceClosureImpl th : m_theorems) {
                    Set<String> theoremfunctionNames = th.getFunctionNames();
                    if (vcFunctionNames.containsAll(theoremfunctionNames)) {
                        allFuncNamesInVC.add(th);

                    }
                }

            }
            else {
                allFuncNamesInVC.addAll(m_theorems);
            }
        }
        // add quantified expressions local to the vc to allFuncNamesInVC. allFuncNamesInVC is a filtered list of all available theorems
        // it resets for each vc
        for (PExp p : vcc.forAllQuantifiedPExps) {
            TheoremCongruenceClosureImpl t =
                    new TheoremCongruenceClosureImpl(m_typeGraph, p);
            if (!t.m_unneeded) {
                allFuncNamesInVC.add(t);
            }
        }
        String div = divLine(vcc.m_name);
        String theseResults =
                div + ("Before application of theorems: " + vcc + "\n");
        String thString = "";
        int i;
        long startTime = System.currentTimeMillis();
        long endTime = myTimeout + startTime;
        HashSet<String> applied = new HashSet<String>();

        for (i = 0; i < MAX_ITERATIONS
                && vcc
                        .isProved()
                        .equals(
                                VerificationConditionCongruenceClosureImpl.STATUS.STILL_EVALUATING)
                && System.currentTimeMillis() <= endTime; ++i) {
            ArrayList<InsertExpWithJustification> insertExp =
                    new ArrayList<InsertExpWithJustification>();
            for (TheoremCongruenceClosureImpl th : allFuncNamesInVC) {
                ArrayList<InsertExpWithJustification> thResult =
                        th.applyTo(vcc, endTime);
                if (thResult != null) {
                    for (InsertExpWithJustification ins : thResult) {
                        if (!applied.contains(ins.m_PExp.toString())
                                && !insertExp.contains(ins)) {
                            insertExp.add(ins);
                        }
                    }
                }
            }
            if (insertExp.isEmpty()) {
                System.err.println(vcc.m_name
                        + ". oops: no unique instantiated theorems.");
                break; // nothing else to try
            }
            Map<String, Integer> vcGoalSymbolCount = vcc.getGoalSymbols();
            int threshold = vcGoalSymbolCount.size() + 1;
            // lower thresholds: less output, more time in prioritizer
            InstantiatedTheoremPrioritizer pQ =
                    new InstantiatedTheoremPrioritizer(insertExp,
                            vcGoalSymbolCount, threshold);
            PExpWithScore curP = pQ.m_pQueue.poll();

            int maxToAdd = pQ.m_pQueue.size() * 3 / 4 + 1;
            int numAdded = 0;
            while (curP != null
                    && vcc
                            .isProved()
                            .equals(
                                    VerificationConditionCongruenceClosureImpl.STATUS.STILL_EVALUATING)
                    && System.currentTimeMillis() <= endTime) {
                if (!applied.contains(curP.m_theorem.toString())) {
                    String substitutionsMade =
                            vcc.getConjunct().addExpressionAndTrackChanges(
                                    curP.m_theorem, endTime, curP.toString())
                                    + "\n";
                    if (!substitutionsMade.equals("\n")) {
                        //thString += curP.toString();
                        //thString += substitutionsMade;
                        //String combined = curP.toString() + "\t\t" + substitutionsMade;
                        theoremsApplied.add(curP.toString());
                        //vcc.getRegistry().updateTheoremDependencyGraphKey(curP.toString(),combined);
                    }
                    applied.add(curP.m_theorem.toString());
                    numAdded++;

                }
                curP = pQ.m_pQueue.poll();

            }

        }
        //theseResults += (thString);

        // cull results
        String goalA = vcc.m_goal.get(0);
        String goalB = vcc.m_goal.get(1);
        newString: for (String th : theoremsApplied) {
            for (String gl : vcc.m_goal) {
                //if(gl.equals("true")) continue; // assuming at least one goal is not "true"
                if (!vcc.getRegistry().m_appliedTheoremDependencyGraph
                        .containsKey(th))
                    continue;
                for (Integer root : vcc.getRegistry().m_appliedTheoremDependencyGraph
                        .get(th)) {
                    if (vcc.getRegistry().areEqual(gl, root)) {
                        theseResults += th;
                        continue newString;
                    }
                }
            }
        }
        VerificationConditionCongruenceClosureImpl.STATUS proved =
                vcc.isProved();

        if (proved
                .equals(VerificationConditionCongruenceClosureImpl.STATUS.PROVED)) {
            theseResults +=
                    (i + " iterations. PROVED: VC " + vcc.m_name + "\n") + div;
            m_results += theseResults;
            return proved;
        }

        if (proved
                .equals(VerificationConditionCongruenceClosureImpl.STATUS.FALSE_ASSUMPTION)) {
            theseResults +=
                    (i + " iterations. PROVED (false assumption): VC "
                            + vcc.m_name + "\n")
                            + div;
            m_results += theseResults;
            return proved;
        }

        String whyNotProved = "";
        if (proved
                .equals(VerificationConditionCongruenceClosureImpl.STATUS.STILL_EVALUATING))
            whyNotProved = "Out of time or theorems";
        else
            whyNotProved = "Goal evaluates to false."; // not implemented
        if (SHOWRESULTSIFNOTPROVED) {
            theseResults +=
                    ("\n" + i + " iterations. NOT PROVED: " + whyNotProved
                            + " " + vcc + "\n")
                            + div;
            m_results += theseResults;
        }
        else {
            m_results +=
                    div
                            + (i + " iterations. NOT PROVED: " + whyNotProved
                                    + " " + vcc.m_name + "\n") + div;
        }
        return proved;

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

    public void addProverListener(ProverListener l) {
        myProverListeners.add(l);
    }

    public void removeProverListener(ProverListener l) {
        myProverListeners.remove(l);
    }
}
