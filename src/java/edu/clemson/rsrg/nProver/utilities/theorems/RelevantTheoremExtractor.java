/*
 * RelevantTheoremExtractor.java
 * ---------------------------------
 * Copyright (c) 2024
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.nProver.utilities.theorems;

import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.rsrg.prover.utilities.theorems.Theorem;
import edu.clemson.rsrg.typeandpopulate.entry.TheoremEntry;
import edu.clemson.rsrg.typeandpopulate.query.EntryTypeQuery;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTable;
import edu.clemson.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import edu.clemson.rsrg.vcgeneration.sequents.Sequent;

import java.util.*;

public class RelevantTheoremExtractor {

    private final ModuleScope myCurrentModuleScope;

    private List<TheoremEntry> programTheorems = null;

    public RelevantTheoremExtractor(ModuleScope scope) {
        myCurrentModuleScope = scope;
    }

    // this query should happen once as the collection is for the entire program. The design is just for performance
    public void theoremEntryQuery() {
        // ist<TheoremEntry> te = null;
        programTheorems = myCurrentModuleScope.query(new EntryTypeQuery<TheoremEntry>(TheoremEntry.class,
                MathSymbolTable.ImportStrategy.IMPORT_NAMED, MathSymbolTable.FacilityStrategy.FACILITY_IGNORE));
        System.err.println(programTheorems.size());
    }

    // get all those theorems including all operators and constants on the sequent VC to be proved
    public List<TheoremEntry> getSequentVCTheorems(Map<String, Integer> expLabels) {

        // set of relevant theorems to the sequent VC
        List<TheoremEntry> relevantTheorems = new ArrayList<>();
        // set of theorem operators
        Set<Exp> setOfTheoremOperators;

        List<String> vcOperators = new ArrayList<>();

        vcOperators.addAll(expLabels.keySet());
        Boolean fullyContained = false;

        // check theorem operators if they are in the VC, all theorem operators have to be in the VC operators
        for (TheoremEntry theoremEntry : programTheorems) {
            // System.out.println(theoremEntry.getAssertion());
            setOfTheoremOperators = theoremEntry.getOperators();

            // for each operator in the theoremEntry
            for (Exp e : setOfTheoremOperators) {
                // check to see if the operator e is in the sequent VC operator list
                if (vcOperators.contains(e.toString())) {
                    fullyContained = true;
                } else {
                    fullyContained = false;
                    break;
                }
            }
            // add the theoremEntry to the list
            if (fullyContained) {
                relevantTheorems.add(theoremEntry);
            }
            // just to be sure nothing is left in the set
            setOfTheoremOperators.clear();
        }
        System.out.print("Number of relevant theorems: ");
        System.out.println(relevantTheorems.size());
        return relevantTheorems;
    }
}
