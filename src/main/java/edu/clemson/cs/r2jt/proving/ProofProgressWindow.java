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
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
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
package edu.clemson.cs.r2jt.proving;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

class ProofProgressWindow extends JFrame
        implements
            ActionListener,
            ProverListener {

    private static final long serialVersionUID = 5289008712520472101L;

    private JProgressBar myProgressBar;

    private ActionCanceller myCanceller;

    public ProofProgressWindow(final String name,
            final ActionCanceller canceller) {

        myCanceller = canceller;

        setTitle("Proof progress...");

        myProgressBar = new JProgressBar(0, 10000);
        myProgressBar.setStringPainted(true);
        myProgressBar.setValue(0);
        myProgressBar.setPreferredSize(new Dimension(350, 20));

        JButton cancel = new JButton("Skip VC");
        cancel.addActionListener(this);

        Container pane = getContentPane();
        pane.setLayout(new FlowLayout());

        JLabel proving = new JLabel("Trying to \nprove " + name + "...");
        proving.setPreferredSize(new Dimension(350, 20));

        JLabel searched = new JLabel("Proof space searched:");
        searched.setPreferredSize(new Dimension(350, 20));
        searched.setVerticalAlignment(JLabel.BOTTOM);

        pane.add(proving);
        pane.add(searched);
        pane.add(myProgressBar);
        pane.add(cancel);

        setSize(375, 150);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setActionCanceller(ActionCanceller c) {
        myCanceller = c;
    }

    public void setProgress(double progress) {
        myProgressBar.setValue((int) (10000 * progress));
        repaint();
    }

    public void done() {
        dispose();
    }

    public void actionPerformed(ActionEvent e) {
        myCanceller.cancel();
    }

    public void progressUpdate(double progress) {
        myProgressBar.setValue((int) (10000 * progress));
        repaint();
    }
}
