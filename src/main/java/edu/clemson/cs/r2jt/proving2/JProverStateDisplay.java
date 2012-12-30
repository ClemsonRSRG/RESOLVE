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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

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
    
    private final SiteTagger<Color> myHighlightedNodes =
            new SiteTagger<Color>();
    
    private final SiteTagger<List<MouseListener>> myMouseActiveNodes =
            new SiteTagger<List<MouseListener>>();

    public JProverStateDisplay(PerVCProverModel model) {
        setEditable(false);

        setModel(model);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int caretPosition = viewToModel(e.getPoint());

                if (caretPosition != -1) {
                    Element c = myDocument.getCharacterElement(caretPosition);
                    AttributeSet attrs = c.getAttributes();
                    Site id = (Site) attrs.getAttribute(PEXP_ID_KEY);
                 
                    if (id != null) {
                        try {
                            List<MouseListener> listeners = 
                                    myMouseActiveNodes.getTag(
                                        myMouseActiveNodes
                                        .getNearestTaggedAncestor(id));
                            
                            e.setSource(id);
                            for (MouseListener l : listeners) {
                                l.mouseClicked(e);
                            }
                        }
                        catch (NoSuchElementException nsee) {
                            //Fine, nobody cares that we've been clicked
                        }
                    }
                }
            }
        });
        
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
    
    public void highlightPExp(Site s, Color c) {
        myHighlightedNodes.tagSite(s, c);
        resetHighlights();
    }
    
    public void addMouseListener(Site s, MouseListener l) {
        List<MouseListener> mouseListeners;
        
        try {
            mouseListeners = myMouseActiveNodes.getTag(s);
        }
        catch (NoSuchElementException nsee) {
            mouseListeners = new LinkedList<MouseListener>();
            myMouseActiveNodes.tagSite(s, mouseListeners);
        }
        
        mouseListeners.add(l);
    }

    public void removeMouseListener(Site s, MouseListener l) {
        try {
            List<MouseListener> mouseListeners = myMouseActiveNodes.getTag(s);
            mouseListeners.remove(l);
        }
        catch (NoSuchElementException nsee) {
            //Nothing to remove
        }
    }
    
    private void resetHighlights() {
        MutableAttributeSet blank = new SimpleAttributeSet();
        blank.addAttribute(StyleConstants.Background, getBackground());
        myDocument.setCharacterAttributes(0, myDocument.getLength(), blank,
                false);

        myHighlightedNodes.visitTaggedSites(myHighlighter);
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
        
        @Override
        public void doBeginPExp(PExp p) {
            myNodeToStart.put(getID(), myDocument.getLength());
        }

        @Override
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
    
    private class SiteTagger<T> {
        
        private Map<Site, TaggedSites<T>> myTopLevelConjunctsWithTags =
                new HashMap<Site, TaggedSites<T>>();
        
        public void tagSite(Site s, T t) {
            Site sRoot = s.root;

            if (!myTopLevelConjunctsWithTags.containsKey(sRoot)) {
                myTopLevelConjunctsWithTags.put(sRoot, 
                        new TaggedSites<T>(sRoot));
            }

            myTopLevelConjunctsWithTags.get(sRoot).put(s, t);
        }
        
        public boolean siteIsTagged(Site s) {
            boolean result;
            
            try {
                getTag(s);
                result = true;
            }
            catch (NoSuchElementException nsee) {
                result = false;
            }
            
            return result;
        }
        
        public T getTag(Site s) {
            T result;
            
            if (!myTopLevelConjunctsWithTags.containsKey(s.root)) {
                throw new NoSuchElementException();
            }
            else {
                result = myTopLevelConjunctsWithTags.get(s.root).getData(s);
            }
            
            return result;
        }
        
        public void visitTaggedSites(TaggedSiteVisitor<T> v) {
            for (TaggedSites<T> taggedSites : 
                    myTopLevelConjunctsWithTags.values()) {
                taggedSites.traverse(v);
            }
        }
        
        public Site getNearestTaggedAncestor(Site s) 
                throws NoSuchElementException {
            
            if (!myTopLevelConjunctsWithTags.containsKey(s.root)) {
                throw new NoSuchElementException();
            }
            
            return myTopLevelConjunctsWithTags.get(
                    s.root).getSmallestIdentifiedAncestor(s);
        }
        
        public void clear() {
            myTopLevelConjunctsWithTags.clear();
        }
    }
}
