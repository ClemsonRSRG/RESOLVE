/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.gui;

import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.proofsteps.ProofStep;
import edu.clemson.cs.r2jt.utilities.FlagManager;
import java.awt.BorderLayout;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author hamptos
 */
public class JProofDisplay extends JPanel {

    private final ModelChanged MODEL_CHANGED = new ModelChanged();
    private JList myStepList = new JList();
    private PerVCProverModel myModel;

    public JProofDisplay(PerVCProverModel m) {
        setLayout(new BorderLayout());

        add(new JScrollPane(myStepList));

        myStepList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        myStepList.addListSelectionListener(new ProofStepUndoer());

        setModel(m);
    }

    public void setModel(PerVCProverModel m) {
        if (myModel != null) {
            myModel.removeChangeListener(MODEL_CHANGED);
        }

        myModel = m;
        myModel.addChangeListener(MODEL_CHANGED);

        refreshProofSteps();
    }

    private void refreshProofSteps() {
        DefaultListModel m = new DefaultListModel();
        for (ProofStep s : myModel.getProofSteps()) {
            m.addElement(s);
        }
        myStepList.setModel(m);
    }

    private class ProofStepUndoer implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()
                    && myStepList.getSelectedValue() != null) {

                int selectedIndex = myStepList.getSelectedIndex();
                int numSteps = myModel.getProofSteps().size();

                int numToUndo = numSteps - selectedIndex;

                myModel.removeChangeListener(MODEL_CHANGED);
                myStepList.removeListSelectionListener(this);
                for (int i = 0; i < numToUndo; i++) {
                    myModel.undoLastProofStep();
                }
                myStepList.addListSelectionListener(this);
                myModel.addChangeListener(MODEL_CHANGED);
                refreshProofSteps();
            }
        }
    }

    private class ModelChanged implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            if (!FlagManager.getInstance().isFlagSet("nodebug")) {
                System.out.println("JProofDisplay - stateChanged()");
            }
            refreshProofSteps();
        }
    }
}
