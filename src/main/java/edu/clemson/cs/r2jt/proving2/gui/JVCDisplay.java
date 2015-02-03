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
package edu.clemson.cs.r2jt.proving2.gui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import edu.clemson.cs.r2jt.proving.absyn.IdentifiedNodes;
import edu.clemson.cs.r2jt.proving.absyn.IdentifiedNodesVisitor;
import edu.clemson.cs.r2jt.proving.absyn.NodeIdentifier;
import edu.clemson.cs.r2jt.proving.absyn.NodeIdentifyingVisitor;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.VC;

public class JVCDisplay extends JTextPane {

    private static final long serialVersionUID = 1L;

    private static final Object PEXP_ID_KEY = new Object();

    private final DisplayConstructingVisitor myDisplayer =
            new DisplayConstructingVisitor();

    private final Highlighter myHighlighter = new Highlighter();

    private StyledDocument myDocument = getStyledDocument();

    private VC myVC;

    private Map<NodeIdentifier, Integer> myNodeToStart =
            new HashMap<NodeIdentifier, Integer>();

    private Map<NodeIdentifier, Integer> myNodeToEnd =
            new HashMap<NodeIdentifier, Integer>();

    private int myHighlightStart, myHighlightEnd;

    private final Map<PExp, IdentifiedNodes<Color>> myHighlightedNodes =
            new HashMap<PExp, IdentifiedNodes<Color>>();

    public JVCDisplay() {
        setEditable(false);

        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                int caretPosition = viewToModel(e.getPoint());

                if (caretPosition != -1) {
                    Element c = myDocument.getCharacterElement(caretPosition);
                    AttributeSet attrs = c.getAttributes();
                    NodeIdentifier id =
                            (NodeIdentifier) attrs.getAttribute(PEXP_ID_KEY);

                    if (id == null) {
                        moveCaretPosition(getCaretPosition());
                    }
                    else {
                        resetHighlights();

                        myHighlightStart = myNodeToStart.get(id);
                        myHighlightEnd = myNodeToEnd.get(id);

                        MutableAttributeSet highlight =
                                new SimpleAttributeSet();
                        highlight.addAttribute(StyleConstants.Background,
                                new Color(150, 200, 200));

                        myDocument.setCharacterAttributes(myHighlightStart,
                                (myHighlightEnd - myHighlightStart), highlight,
                                false);
                    }
                }
            }
        });
    }

    public void highlightPExp(NodeIdentifier id, Color c) {
        PExp p = id.getRoot();

        if (!myHighlightedNodes.containsKey(p)) {
            myHighlightedNodes.put(id.getRoot(), new IdentifiedNodes<Color>(id
                    .getRoot()));
        }

        myHighlightedNodes.get(p).put(id, c);
        resetHighlights();
    }

    private void resetHighlights() {
        MutableAttributeSet blank = new SimpleAttributeSet();
        blank.addAttribute(StyleConstants.Background, getBackground());
        myDocument.setCharacterAttributes(0, myDocument.getLength(), blank,
                false);

        for (IdentifiedNodes<Color> nodes : myHighlightedNodes.values()) {
            nodes.traverse(myHighlighter);
        }
    }

    public void setVC(VC p) {
        myVC = p;

        setText("");

        p.processStringRepresentation(myDisplayer, myDisplayer);
    }

    public VC getVC() {
        return myVC;
    }

    private class DisplayConstructingVisitor extends NodeIdentifyingVisitor
            implements
                Appendable {

        public void doBeginPExp(PExp p) {
            myNodeToStart.put(getID(), myDocument.getLength());
        }

        public void doEndPExp(PExp p) {
            myNodeToEnd.put(getID(), myDocument.getLength());
        }

        @Override
        public Appendable append(CharSequence csq) throws IOException {
            String chars = csq.toString();

            MutableAttributeSet pexpIDAttr = new SimpleAttributeSet();

            NodeIdentifier id = getID();
            if (id != null) {
                pexpIDAttr.addAttribute(PEXP_ID_KEY, id);
            }

            try {
                myDocument.insertString(myDocument.getLength(), chars,
                        pexpIDAttr);
            }
            catch (BadLocationException e) {
                //Shouldn't be possible
                throw new RuntimeException(e);
            }

            return this;
        }

        @Override
        public Appendable append(char c) throws IOException {
            return append("" + c);
        }

        @Override
        public Appendable append(CharSequence csq, int start, int end)
                throws IOException {
            return append(csq.subSequence(start, end));
        }
    }

    private class Highlighter implements IdentifiedNodesVisitor<Color> {

        @Override
        public void visit(NodeIdentifier id, Color data) {
            MutableAttributeSet bgColor = new SimpleAttributeSet();
            bgColor.addAttribute(StyleConstants.Background, data);

            int start = myNodeToStart.get(id);
            int end = myNodeToEnd.get(id);
            myDocument.setCharacterAttributes(start, end - start, bgColor,
                    false);
        }
    }
}
