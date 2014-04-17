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

import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.proving.Prover;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol;
import edu.clemson.cs.r2jt.proving2.Metrics;
import edu.clemson.cs.r2jt.proving2.ProverListener;
import edu.clemson.cs.r2jt.proving2.VC;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.typeandpopulate.EntryTypeQuery;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleScope;
import edu.clemson.cs.r2jt.typeandpopulate.entry.TheoremEntry;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.utilities.FlagDependencies;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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
    private final int MAX_ITERATIONS = 5;
    private final CompileEnvironment m_environment;
    private final ModuleScope m_scope;
    private String m_results;

    // only for webide ////////////////////////////////////
    private final PerVCProverModel[] myModels;
    private final List<ProverListener> myProverListeners =
            new LinkedList<ProverListener>();
    private final long myTimeout;

    ///////////////////////////////////////////////////////

    public static void setUpFlags() {
        FlagDependencies.addExcludes(FLAG_PROVE, Prover.FLAG_PROVE);
        FlagDependencies.addExcludes(FLAG_PROVE, Prover.FLAG_LEGACY_PROVE);
        FlagDependencies.addImplies(FLAG_PROVE, Prover.FLAG_SOME_PROVER);
    }

    public CongruenceClassProver(TypeGraph g, List<VC> vcs, ModuleScope scope,
            CompileEnvironment environment, ProverListener listener) {

        // Only for web ide //////////////////////////////////////////
        if (listener != null) {
            myProverListeners.add(listener);
        }
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
            myTimeout = -1;
        }
        ///////////////////////////////////////////////////////////////

        m_ccVCs = new ArrayList<VerificationConditionCongruenceClosureImpl>();
        int i = 0;
        for (VC vc : vcs) {
            m_ccVCs.add(new VerificationConditionCongruenceClosureImpl(g, vc));
            myModels[i++] = (new PerVCProverModel(g, vc.getName(), vc, null));
        }

        m_theorems = new ArrayList<TheoremCongruenceClosureImpl>();
        List<TheoremEntry> theoremEntries =
                scope.query(new EntryTypeQuery(TheoremEntry.class,
                        MathSymbolTable.ImportStrategy.IMPORT_RECURSIVE,
                        MathSymbolTable.FacilityStrategy.FACILITY_IGNORE));
        for (TheoremEntry e : theoremEntries) {
            if (e.getAssertion().isEquality()
                    && !e.getAssertion().getQuantifiedVariables().isEmpty()) {
                // This creates a theorem like e, where if e is of the form e1 = e2, this is e2 = e1.
                // Any quantifiers used must appear in the lhs for matching.
                // if there are no quantifiers, this will have no effect.
                Set<PSymbol> newMatchQuants =
                        e.getAssertion().getSubExpressions().get(1)
                                .getQuantifiedVariables();
                if (newMatchQuants.containsAll(e.getAssertion()
                        .getSubExpressions().get(0).getQuantifiedVariables())) {
                    m_theorems.add(new TheoremCongruenceClosureImpl(g, e
                            .getAssertion().getSubExpressions().get(1), e
                            .getAssertion()));
                }
            }
            m_theorems
                    .add(new TheoremCongruenceClosureImpl(g, e.getAssertion()));
        }
        m_environment = environment;
        m_scope = scope;
        m_results = "";

    }

    public void start() throws IOException {

        String summary = "";
        int i = 0;
        for (VerificationConditionCongruenceClosureImpl vcc : m_ccVCs) {
            long startTime = System.nanoTime();
            boolean proved = prove(vcc);
            if (proved) {
                summary += "Proved ";
            }
            else {
                summary += "Insufficent data to prove ";
            }

            long endTime = System.nanoTime();
            long delayNS = endTime - startTime;
            long delayMS =
                    TimeUnit.MILLISECONDS
                            .convert(delayNS, TimeUnit.NANOSECONDS);
            summary += vcc.m_name + " time: " + delayMS + " ms\n";

            for (ProverListener l : myProverListeners) {
                l
                        .vcResult(proved, myModels[i], new Metrics(delayMS,
                                myTimeout));
            }
            i++;
        }

        String div = "===================================";
        div = div + " Summary " + div + "\n";
        summary = div + summary + div;
        System.out.println(m_results + summary);
        m_results = summary + m_results;

        outputProofFile();

    }

    protected boolean prove(VerificationConditionCongruenceClosureImpl vcc) {
        m_results += ("Before application of theorems: " + vcc + "\n");
        String thString = "";
        int i;
        for (i = 0; !vcc.isProved() && i < MAX_ITERATIONS; ++i) {

            //Collections.reverse(m_theorems);
            for (TheoremCongruenceClosureImpl th : m_theorems) {
                if (vcc.isProved()) {
                    i++; // for iterations count.
                    break;
                }
                String ap = th.applyTo(vcc);
                if (ap.length() > 0)
                    thString += th.m_theoremString + "\n" + ap + "\n";
            }
        }
        m_results += (thString);

        boolean proved = vcc.isProved();

        if (proved) {
            m_results += (i + " iterations. PROVED: VC " + vcc + "\n");
            return true;
        }

        m_results += (i + " iterations. NOT PROVED: VC " + vcc + "\n");
        return false;

    }

    private String proofFileName() {
        File file = m_environment.getTargetFile();
        ModuleID cid = m_environment.getModuleID(file);
        file = m_environment.getFile(cid);
        String filename = file.toString();
        int temp = filename.indexOf(".");
        String tempfile = filename.substring(0, temp);
        String mainFileName;

        mainFileName = tempfile + ".proof";

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
