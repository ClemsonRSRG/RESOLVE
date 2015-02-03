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

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

public class GuidedListSelectIterator<T> extends JDialog implements Iterator<T> {

    public static JFrame modalParent = null;

    private static final long serialVersionUID = 3467296085087080555L;

    private JList myDisplayList;
    private T mySelection;

    private DebugOptionsWindow myDebugOptions;

    private boolean myNextReturnedFlag = true, myFinishedFlag = false;

    public GuidedListSelectIterator(String title, String text,
            Iterator<T> options) {
        super(modalParent, true);

        setTitle(title);

        Container pane = getContentPane();

        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

        List<T> optionsList = new LinkedList<T>();
        while (options.hasNext()) {
            optionsList.add(options.next());
        }

        JTextArea vcDisplay = new JTextArea(10, 1);
        vcDisplay.setLineWrap(true);
        vcDisplay.setEditable(false);
        vcDisplay.setBackground(new Color(200, 200, 200));
        vcDisplay.setAutoscrolls(true);
        add(new JScrollPane(vcDisplay));

        myDisplayList = new JList(optionsList.toArray());
        myDisplayList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        myDisplayList.setVisibleRowCount(20);
        add(new JScrollPane(myDisplayList));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        JButton chooseButton = new JButton("Choose");
        JButton doneButton = new JButton("Finish");

        chooseButton.addActionListener(new ChooseButtonListener());
        doneButton.addActionListener(new DoneButtonListener());

        buttonPanel.add(doneButton);
        buttonPanel.add(chooseButton);

        add(buttonPanel);

        pack();

        vcDisplay.setText(text);
        setLocationRelativeTo(null);
        myDebugOptions = new DebugOptionsWindow(this);
    }

    public boolean hasNext() {
        if (!myFinishedFlag && myNextReturnedFlag) {

            setVisible(true);

            if (mySelection == null) {
                myFinishedFlag = true;
            }
            else {
                myNextReturnedFlag = false;
            }
        }

        return (!myFinishedFlag && !myNextReturnedFlag);
    }

    public T next() {
        if (myFinishedFlag) {
            throw new NoSuchElementException();
        }

        myNextReturnedFlag = true;

        return mySelection;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    private class ChooseButtonListener implements ActionListener {

        @SuppressWarnings("unchecked")
        public void actionPerformed(ActionEvent e) {
            mySelection = (T) myDisplayList.getSelectedValue();
            setVisible(false);

            myDebugOptions.setVisible(false);
        }
    }

    private class DoneButtonListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            mySelection = null;
            setVisible(false);

            myDebugOptions.setVisible(false);
        }
    }
}