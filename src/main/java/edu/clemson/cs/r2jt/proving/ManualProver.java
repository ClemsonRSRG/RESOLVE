package edu.clemson.cs.r2jt.proving;

import java.util.Collection;

import javax.swing.JFrame;

import edu.clemson.cs.r2jt.scope.OldSymbolTable;
import edu.clemson.cs.r2jt.verification.AssertiveCode;

public class ManualProver extends JFrame {

    private static final long serialVersionUID = 2320690572854510911L;

    public ManualProver(final OldSymbolTable symbolTable,
            final Collection<AssertiveCode> vCs, final int maxDepth)
            throws ProverException {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setVisible(true);
    }

}
