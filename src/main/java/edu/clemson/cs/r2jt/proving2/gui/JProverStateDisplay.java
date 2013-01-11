package edu.clemson.cs.r2jt.proving2.gui;

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
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultStyledDocument;

public class JProverStateDisplay extends JTextPane {

    private static final long serialVersionUID = 1L;

    private static final Object PEXP_ID_KEY = new Object();

    private final ModelChanged MODEL_CHANGED = new ModelChanged();

    private DisplayConstructingVisitor myDisplayer =
            new DisplayConstructingVisitor();

    private final Highlighter myHighlighter = new Highlighter();

    private StyledDocument myDocument = getStyledDocument();

    private PerVCProverModel myProverState;

    private final Map<Site, Integer> myNodeToStart =
            new HashMap<Site, Integer>();

    private final Map<Site, Integer> myNodeToEnd = new HashMap<Site, Integer>();

    private final SiteTagger<Color> myHighlightedNodes =
            new SiteTagger<Color>();

    private final SiteTagger<List<MouseListener>> myMouseActiveNodes =
            new SiteTagger<List<MouseListener>>();

    /**
     * <p>Tracks which site the mouse is moving over.</p>
     */
    private Site myCurMouseContainer = null;

    public JProverStateDisplay(PerVCProverModel model) {
        setEditable(false);

        setModel(model);

        addMouseListener(new SiteMouseListener());
        addMouseMotionListener(new SiteMouseMotionListener());

        ToolTipManager.sharedInstance().registerComponent(this);
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
        if (myProverState != null) {
            m.removeChangeListener(MODEL_CHANGED);
        }

        myProverState = m;
        m.addChangeListener(MODEL_CHANGED);

        refreshModel();
    }

    private void refreshModel() {
        myNodeToStart.clear();
        myNodeToEnd.clear();
        myHighlightedNodes.clear();
        myMouseActiveNodes.clear();
        myCurMouseContainer = null;

        myDisplayer = new DisplayConstructingVisitor();

        setDocument(new DefaultStyledDocument());
        myDocument = (StyledDocument) getDocument();

        myProverState.processStringRepresentation(myDisplayer, myDisplayer);
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

    private static class SiteTagger<T> {

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
            for (TaggedSites<T> taggedSites : myTopLevelConjunctsWithTags
                    .values()) {
                taggedSites.traverse(v);
            }
        }

        public Site getNearestTaggedAncestor(Site s)
                throws NoSuchElementException {

            if (!myTopLevelConjunctsWithTags.containsKey(s.root)) {
                throw new NoSuchElementException();
            }

            return myTopLevelConjunctsWithTags.get(s.root)
                    .getSmallestIdentifiedAncestor(s);
        }

        public void clear() {
            myTopLevelConjunctsWithTags.clear();
        }
    }

    private Site getSiteOfEvent(MouseEvent e) {
        int caretPosition = viewToModel(e.getPoint());

        if (caretPosition == -1) {
            throw new NoSuchElementException();
        }

        Element c = myDocument.getCharacterElement(caretPosition);
        AttributeSet attrs = c.getAttributes();
        Site result = (Site) attrs.getAttribute(PEXP_ID_KEY);

        if (result == null) {
            throw new NoSuchElementException();
        }

        return result;
    }

    private Site getActiveSiteOfEvent(MouseEvent e)
            throws NoSuchElementException {

        Site result;
        Site id = getSiteOfEvent(e);
        result = myMouseActiveNodes.getNearestTaggedAncestor(id);

        return result;
    }

    private void mouseEnteredSite(Site s, MouseEvent e) {
        if (myCurMouseContainer == null || !myCurMouseContainer.equals(s)) {
            if (myCurMouseContainer != null) {
                MouseEvent exited =
                        new MouseEvent(this, MouseEvent.MOUSE_EXITED, e
                                .getWhen(), e.getModifiers(), e.getX(), e
                                .getY(), e.getXOnScreen(), e.getYOnScreen(), e
                                .getClickCount(), e.isPopupTrigger(), e
                                .getButton());
                exited.setSource(myCurMouseContainer);
                List<MouseListener> listeners =
                        myMouseActiveNodes.getTag(myCurMouseContainer);
                for (MouseListener l : listeners) {
                    l.mouseExited(exited);
                }
            }

            if (s != null) {
                MouseEvent entered =
                        new MouseEvent(this, MouseEvent.MOUSE_ENTERED, e
                                .getWhen(), e.getModifiers(), e.getX(), e
                                .getY(), e.getXOnScreen(), e.getYOnScreen(), e
                                .getClickCount(), e.isPopupTrigger(), e
                                .getButton());
                entered.setSource(s);
                List<MouseListener> listeners = myMouseActiveNodes.getTag(s);
                for (MouseListener l : listeners) {
                    l.mouseEntered(entered);
                }
            }

            myCurMouseContainer = s;
        }
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        String result;

        try {
            result = getSiteOfEvent(e).exp.toDebugString(4, 0);
        }
        catch (NoSuchElementException nsee) {
            result = null;
        }

        return result;
    }

    private class SiteMouseMotionListener implements MouseMotionListener {

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {
            try {
                Site over = getActiveSiteOfEvent(e);
                mouseEnteredSite(over, e);
            }
            catch (NoSuchElementException nsee) {
                mouseEnteredSite(null, e);
            }
        }

    }

    private class SiteMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            try {
                Site nearestAncestor = getActiveSiteOfEvent(e);

                List<MouseListener> listeners =
                        myMouseActiveNodes.getTag(nearestAncestor);

                e.setSource(nearestAncestor);
                for (MouseListener l : listeners) {
                    l.mouseClicked(e);
                }
            }
            catch (NoSuchElementException nsse) {
                //No one cares about this click
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            try {
                Site nearestAncestor = getActiveSiteOfEvent(e);

                List<MouseListener> listeners =
                        myMouseActiveNodes.getTag(nearestAncestor);

                e.setSource(nearestAncestor);
                for (MouseListener l : listeners) {
                    l.mousePressed(e);
                }
            }
            catch (NoSuchElementException nsse) {
                //No one cares about this click
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            try {
                Site nearestAncestor = getActiveSiteOfEvent(e);

                List<MouseListener> listeners =
                        myMouseActiveNodes.getTag(nearestAncestor);

                e.setSource(nearestAncestor);
                for (MouseListener l : listeners) {
                    l.mouseReleased(e);
                }
            }
            catch (NoSuchElementException nsse) {
                //No one cares about this click
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        //Mouse moved should take care of this?
        }

        @Override
        public void mouseExited(MouseEvent e) {
            mouseEnteredSite(null, e);
        }
    }

    private class ModelChanged implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            refreshModel();
        }
    }
}
