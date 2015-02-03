/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
