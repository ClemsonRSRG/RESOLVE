/**
 * DebugOptionsWindow.java
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
package edu.clemson.cs.r2jt.proving;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;

public class DebugOptionsWindow extends JDialog implements ItemListener {

    private JCheckBox myBindDebugBox = new JCheckBox("Bind Debug");

    public DebugOptionsWindow(Window parent) {
        super(parent, Dialog.ModalityType.MODELESS);
        setTitle("Debug Options");
        add(myBindDebugBox);
        myBindDebugBox.addItemListener(this);
        pack();
        setVisible(true);
    }

    public void itemStateChanged(ItemEvent arg0) {
        if (arg0.getSource() == myBindDebugBox) {
            Utilities.setBindDebugFlag(myBindDebugBox.getModel().isSelected());
        }
    }
}
