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

import edu.clemson.cs.r2jt.proving.Prover;
import edu.clemson.cs.r2jt.proving2.VC;
import edu.clemson.cs.r2jt.proving2.justifications.Library;
import edu.clemson.cs.r2jt.proving2.model.Theorem;
import edu.clemson.cs.r2jt.typeandpopulate.EntryTypeQuery;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleScope;
import edu.clemson.cs.r2jt.typeandpopulate.entry.TheoremEntry;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.utilities.FlagDependencies;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mike on 4/4/2014.
 */
public class CongruenceClassProver {

    public static final Flag FLAG_PROVE =
            new Flag(Prover.FLAG_SECTION_NAME, "ccprove",
                    "congruence closure based prover");
    private final List<VC> m_VCs;
    private List<VerificationConditionCongruenceClosureImpl> m_ccVCs;
    private List<TheoremCongruenceClosureImpl> m_theorems;
    private ModuleScope m_scope;

    public static void setUpFlags() {
        FlagDependencies.addExcludes(FLAG_PROVE, Prover.FLAG_PROVE);
        FlagDependencies.addExcludes(FLAG_PROVE, Prover.FLAG_LEGACY_PROVE);
        FlagDependencies.addImplies(FLAG_PROVE, Prover.FLAG_SOME_PROVER);
    }

    public CongruenceClassProver(List<VC> vcs, ModuleScope scope) {
        System.out.println("in ccprover");
        m_VCs = vcs;
        m_scope = scope;
        
        m_ccVCs = new ArrayList<VerificationConditionCongruenceClosureImpl>();
        for(VC vc : m_VCs){
            m_ccVCs.add(new VerificationConditionCongruenceClosureImpl(vc));
        }
        
        m_theorems = new ArrayList<TheoremCongruenceClosureImpl>();
        List<TheoremEntry> theoremEntries =
                scope.query(new EntryTypeQuery(TheoremEntry.class,
                        MathSymbolTable.ImportStrategy.IMPORT_RECURSIVE,
                        MathSymbolTable.FacilityStrategy.FACILITY_IGNORE));
        for (TheoremEntry e : theoremEntries) {
            if(TheoremCongruenceClosureImpl.canProcess(e.getAssertion()))
            m_theorems.add(new TheoremCongruenceClosureImpl(e.getAssertion()));
            else System.out.println("Can't process " + e.getAssertion());
        }
    }

    public void start() {
        
        for(VerificationConditionCongruenceClosureImpl vcc:m_ccVCs){
            //System.out.println(vcc);
            for(TheoremCongruenceClosureImpl th:m_theorems){
                if(vcc.isProved()) break;
                th.applyTo(vcc);
            //System.out.println(th);
            }
        }
        for(TheoremCongruenceClosureImpl th:m_theorems){
            //System.out.println(th);
        }
    }
}
