/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.justifications.Library;
import edu.clemson.cs.r2jt.mathtype.TheoremEntry;
import edu.clemson.cs.r2jt.mathtype.EntryTypeQuery;
import edu.clemson.cs.r2jt.mathtype.MathSymbolTable;
import edu.clemson.cs.r2jt.mathtype.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.mathtype.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.mathtype.ModuleScope;
import edu.clemson.cs.r2jt.mathtype.ScopeRepository;
import edu.clemson.cs.r2jt.mathtype.SymbolTable;
import edu.clemson.cs.r2jt.proving.Prover;
import edu.clemson.cs.r2jt.proving.immutableadts.ArrayBackedImmutableList;
import edu.clemson.cs.r2jt.proving.immutableadts.EmptyImmutableList;
import edu.clemson.cs.r2jt.proving.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.utilities.FlagDependencies;
import edu.clemson.cs.r2jt.verification.Verifier;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
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

    private final NextVC NEXT_VC = new NextVC();
    private final LastVC LAST_VC = new LastVC();

    private int myVCIndex;
    private final PerVCProverModel[] myModels;
    private final List<VC> myVCs;
    private final TypeGraph myTypeGraph;
    private final ImmutableList<Theorem> myTheoremLibrary;

    private final JProverFrame myUI;

    public AlgebraicProver(TypeGraph g, List<VC> vcs, ModuleScope scope) {

        myModels = new PerVCProverModel[vcs.size()];

        List<TheoremEntry> theoremEntries =
                scope.query(new EntryTypeQuery(TheoremEntry.class,
                        ImportStrategy.IMPORT_RECURSIVE,
                        FacilityStrategy.FACILITY_IGNORE));

        List<Theorem> theorems = new LinkedList<Theorem>();
        for (TheoremEntry e : theoremEntries) {
            theorems.add(new Theorem(e.getAssertion(), new Library(e)));
        }

        myTheoremLibrary = new ArrayBackedImmutableList<Theorem>(theorems);

        myModels[0] =
                new PerVCProverModel(g, vcs.get(0).getName(), vcs.get(0),
                        myTheoremLibrary);

        JProverFrame proverPanel = new JProverFrame(myModels[0]);
        proverPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        proverPanel.setVisible(true);

        proverPanel.addNextVCButtonActionListener(NEXT_VC);
        proverPanel.addLastVCButtonActionListeenr(LAST_VC);

        myTypeGraph = g;
        myVCs = vcs;
        myUI = proverPanel;
    }

    private void setVCIndex(int index) {
        myVCIndex = index;

        if (myModels[myVCIndex] == null) {
            myModels[myVCIndex] =
                    new PerVCProverModel(myTypeGraph, myVCs.get(myVCIndex)
                            .getName(), myVCs.get(myVCIndex), myTheoremLibrary);
        }

        myUI.setModel(myModels[myVCIndex]);
    }

    private class NextVC implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (myVCIndex < myModels.length - 1) {
                setVCIndex(myVCIndex + 1);
            }
        }
    }

    private class LastVC implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (myVCIndex > 0) {
                setVCIndex(myVCIndex - 1);
            }
        }
    }
}
