/**
 * CongruenceClassProver.java --------------------------------- Copyright (c)
 * 2014 RESOLVE Software Research Group School of Computing Clemson University
 * All rights reserved. --------------------------------- This file is subject
 * to the terms and conditions defined in file 'LICENSE.txt', which is part of
 * this source code package.
 */
package edu.clemson.cs.r2jt.congruenceclassprover;

import edu.clemson.cs.r2jt.proving.Prover;
import edu.clemson.cs.r2jt.proving2.VC;
import edu.clemson.cs.r2jt.typeandpopulate.EntryTypeQuery;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleScope;
import edu.clemson.cs.r2jt.typeandpopulate.entry.TheoremEntry;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.utilities.FlagDependencies;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by mike on 4/4/2014.
 */
public class CongruenceClassProver {

    public static final Flag FLAG_PROVE
            = new Flag(Prover.FLAG_SECTION_NAME, "ccprove",
                    "congruence closure based prover");
    private final List<VC> m_VCs;
    private List<VerificationConditionCongruenceClosureImpl> m_ccVCs;
    private List<TheoremCongruenceClosureImpl> m_theorems;
    private ModuleScope m_scope;
    private final int MAX_ITERATIONS = 10;

    public static void setUpFlags() {
        FlagDependencies.addExcludes(FLAG_PROVE, Prover.FLAG_PROVE);
        FlagDependencies.addExcludes(FLAG_PROVE, Prover.FLAG_LEGACY_PROVE);
        FlagDependencies.addImplies(FLAG_PROVE, Prover.FLAG_SOME_PROVER);
    }

    public CongruenceClassProver(TypeGraph g, List<VC> vcs, ModuleScope scope) {
        m_VCs = vcs;
        m_scope = scope;

        m_ccVCs = new ArrayList<VerificationConditionCongruenceClosureImpl>();
        for (VC vc : m_VCs) {
            m_ccVCs.add(new VerificationConditionCongruenceClosureImpl(g, vc));
        }

        m_theorems = new ArrayList<TheoremCongruenceClosureImpl>();
        List<TheoremEntry> theoremEntries
                = scope.query(new EntryTypeQuery(TheoremEntry.class,
                                MathSymbolTable.ImportStrategy.IMPORT_RECURSIVE,
                                MathSymbolTable.FacilityStrategy.FACILITY_IGNORE));
        for (TheoremEntry e : theoremEntries) {
            //if(TheoremCongruenceClosureImpl.canProcess(e.getAssertion()))
            if (e.getAssertion().isEquality()) {
                // m_theorems.add(new TheoremCongruenceClosureImpl(g, e.getAssertion().getSubExpressions().get(1),e.getAssertion()));
            }
            m_theorems.add(new TheoremCongruenceClosureImpl(g, e.getAssertion()));
            //else System.out.println("Can't process " + e.getAssertion());
        }
    }

    public void start() {
        String r = "";
        for (VerificationConditionCongruenceClosureImpl vcc : m_ccVCs) {
            long startTime = System.nanoTime();
            if (prove(vcc)) {
                r += "Proved ";
            } else {
                r += "Insufficent data to prove ";
            }
            long endTime = System.nanoTime();
            long delayNS = endTime - startTime;
            long delayMS = TimeUnit.MILLISECONDS.convert(delayNS, TimeUnit.NANOSECONDS);
            r += vcc.m_name + "time: " + delayMS + " ms\n";
        }
        System.out.println(r);

    }

    protected boolean prove(VerificationConditionCongruenceClosureImpl vcc) {
        //for(VerificationConditionCongruenceClosureImpl vcc:m_ccVCs)
        System.out.println("vc before: " + vcc);
        String thString = "";
        int i;
        for (i = 0; !vcc.isProved() && i < MAX_ITERATIONS; ++i) {

            //Collections.reverse(m_theorems);
            for (TheoremCongruenceClosureImpl th : m_theorems) {
                if (vcc.isProved()) {
                    break;
                }
                String e = th.m_theoremString;

                String ap = th.applyTo(vcc);

                if (ap.length() == 0) {
                    ap = "match not found";
                } else {
                    thString += th.m_theoremString + "\n\t" + ap + "\n";
                }

                //System.out.println(th);
            }
        }
        System.out.println(thString);
        if (vcc.isProved()) {
            System.out.println(i + 1 + " iterations. proved vc: " + vcc);
            return true;
        }

        System.out.println("not proved vc: " + vcc);
        return false;

    }

}
