package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
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
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.model.ProverModelVisitor;
import edu.clemson.cs.r2jt.proving2.model.TaggedSiteVisitor;
import edu.clemson.cs.r2jt.proving2.model.TaggedSites;

public class JProverStateDisplay extends JTextPane {

    private static final long serialVersionUID = 1L;

    private static final Object PEXP_ID_KEY = new Object();

    private DisplayConstructingVisitor myDisplayer =
            new DisplayConstructingVisitor();

    private final Highlighter myHighlighter = new Highlighter();

    private StyledDocument myDocument = getStyledDocument();

    private PerVCProverModel myProverState;

    private final Map<Site, Integer> myNodeToStart =
            new HashMap<Site, Integer>();

    private final Map<Site, Integer> myNodeToEnd = new HashMap<Site, Integer>();

    private int myHighlightStart, myHighlightEnd;

    /**
     * <p>A map from root sites to their TaggedSites</p>
     */
    private final Map<Site, TaggedSites<Color>> myHighlightedNodes =
            new HashMap<Site, TaggedSites<Color>>();

    public JProverStateDisplay(PerVCProverModel model) {
        setEditable(false);

        setModel(model);

        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                int caretPosition = viewToModel(e.getPoint());

                if (caretPosition != -1) {
                    Element c = myDocument.getCharacterElement(caretPosition);
                    AttributeSet attrs = c.getAttributes();
                    Site id = (Site) attrs.getAttribute(PEXP_ID_KEY);

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

    public void clearHighlights() {
        myHighlightedNodes.clear();
        resetHighlights();
    }
    
    public void highlightPExp(Site id, Color c) {
        Site p = id.root;

        if (!myHighlightedNodes.containsKey(p)) {
            myHighlightedNodes.put(id.root, new TaggedSites<Color>(id.root));
        }

        myHighlightedNodes.get(p).put(id, c);
        resetHighlights();
    }

    private void resetHighlights() {
        MutableAttributeSet blank = new SimpleAttributeSet();
        blank.addAttribute(StyleConstants.Background, getBackground());
        myDocument.setCharacterAttributes(0, myDocument.getLength(), blank,
                false);

        for (TaggedSites<Color> nodes : myHighlightedNodes.values()) {
            nodes.traverse(myHighlighter);
        }
    }

    public void setModel(PerVCProverModel m) {
        myProverState = m;
        myDisplayer = new DisplayConstructingVisitor();

        setText("");

        m.processStringRepresentation(myDisplayer, myDisplayer);
    }

    public PerVCProverModel getModel() {
        return myProverState;
    }

    private class DisplayConstructingVisitor extends ProverModelVisitor
            implements
                Appendable {

        public DisplayConstructingVisitor() {
            super(myProverState);
        }
        
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

            Site id = getID();
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

    private class Highlighter implements TaggedSiteVisitor<Color> {

        @Override
        public void visitSite(Site id, Color data) {
            MutableAttributeSet bgColor = new SimpleAttributeSet();
            bgColor.addAttribute(StyleConstants.Background, data);

            if (!myNodeToStart.containsKey(id)) {
                throw new RuntimeException("Danger Will Robinson!");
            }
            
            int start = myNodeToStart.get(id);
            int end = myNodeToEnd.get(id);
            myDocument.setCharacterAttributes(start, end - start, bgColor,
                    false);
        }
    }
}
