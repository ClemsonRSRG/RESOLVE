/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.entry.TheoremEntry;
import edu.clemson.cs.r2jt.mathtype.EntryTypeQuery;
import edu.clemson.cs.r2jt.mathtype.MathSymbolTable;
import edu.clemson.cs.r2jt.mathtype.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.mathtype.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.mathtype.ModuleScope;
import edu.clemson.cs.r2jt.mathtype.ScopeRepository;
import edu.clemson.cs.r2jt.mathtype.SymbolTable;
import edu.clemson.cs.r2jt.proving.Prover;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.utilities.FlagDependencies;
import edu.clemson.cs.r2jt.verification.Verifier;
import java.util.List;
import javax.swing.JFrame;

/**
 *
 * @author hamptos
 */
public class AlgebraicProver {

    private static final String FLAG_DESC_NEW_PROVE =
            "Verify target file with RESOLVE's integrated prover.";

    /**
     * <p>
     * The main prover flag. Causes the integrated prover to attempt to dispatch
     * generated VCs.
     * </p>
     */
    public static final Flag FLAG_PROVE =
            new Flag(Prover.FLAG_SECTION_NAME, "newprove", FLAG_DESC_NEW_PROVE);

    public static void setUpFlags() {
        FlagDependencies.addExcludes(FLAG_PROVE, Prover.FLAG_PROVE);
        FlagDependencies.addExcludes(FLAG_PROVE, Prover.FLAG_LEGACY_PROVE);

        FlagDependencies.addImplies(FLAG_PROVE, Prover.FLAG_SOME_PROVER);
    }

    public AlgebraicProver(List<VC> vcs, ModuleScope scope) {

        List<TheoremEntry> theorems = scope.query(new EntryTypeQuery(
                TheoremEntry.class, ImportStrategy.IMPORT_RECURSIVE, 
                FacilityStrategy.FACILITY_IGNORE));
        
        JProverFrame proverPanel = new JProverFrame();
        proverPanel.setVC(vcs.get(0));
        proverPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        proverPanel.setVisible(true);
    }
}
