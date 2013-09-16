package edu.clemson.cs.r2jt.proving2.gui;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol;
import edu.clemson.cs.r2jt.proving.immutableadts.EmptyImmutableList;
import edu.clemson.cs.r2jt.proving.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.proving2.Antecedent;
import edu.clemson.cs.r2jt.proving2.Consequent;
import edu.clemson.cs.r2jt.proving2.DummyTheorem;
import edu.clemson.cs.r2jt.proving2.model.Theorem;
import edu.clemson.cs.r2jt.proving2.VC;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.transformations.EliminateTrueConjunctInConsequent;
import edu.clemson.cs.r2jt.proving2.transformations.ExistentialInstantiation;
import edu.clemson.cs.r2jt.proving2.transformations.ReplaceSymmetricEqualityWithTrueInConsequent;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import edu.clemson.cs.r2jt.proving2.utilities.MapOfLists;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTableBuilder;
import edu.clemson.cs.r2jt.utilities.FlagDependencies;
import edu.clemson.cs.r2jt.utilities.FlagDependencyException;
import edu.clemson.cs.r2jt.utilities.FlagManager;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class JProverFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String CONTROLS_HIDDEN = "hidden";
    private static final String CONTROLS_VISIBLE = "visible";
    private static final Color POSSIBLE_THEOREM = new Color(200, 255, 200);
    private static final Color POSSIBLE_THEOREM_HOVER = new Color(0, 255, 0);
    private static final Color POSSIBLE_APPLICATION = new Color(200, 200, 200);
    private static final Color POSSIBLE_APPLICATION_HOVER =
            new Color(0, 255, 255);
    private final ApplicationCanceller APPLICATION_CANCELLER =
            new ApplicationCanceller();
    private final ApplicationApplier APPLICATION_APPLIER =
            new ApplicationApplier();
    private final LocalTheoremSelect LOCAL_THEOREM_SELECT =
            new LocalTheoremSelect();
    private final EnterTheoremSelectionOnModelChange TO_THEOREM_SELECTION =
            new EnterTheoremSelectionOnModelChange();
    private JProverStateDisplay myProverStateDisplay;
    private final JCheckBox myDetailsCheckBox = new JCheckBox("Details");
    private JComponent myDetailsArea;
    private JList myTheoremList;
    private JProofDisplay myProofDisplay;
    private JLabel myProvingLabel = new JLabel();
    private JButton myNextVCButton = new JButton("VC>");
    private JButton myLastVCButton = new JButton("<VC");
    private JButton mySkipButton = new JButton("Skip VC");
    private JButton myCancelButton = new JButton("Cancel");
    private JButton myPlayButton;
    private JButton myPauseButton;
    private JButton myStopButton;
    private JButton myStepButton;
    private final CardLayout myOptionalTransportLayout = new CardLayout();
    private final JPanel myOptionalTransportPanel =
            new JPanel(myOptionalTransportLayout);
    private JScrollPane myTopLevelScrollWrapper;
    private JComponent myBasicArea;
    private Set<PExp> myGlobalTheoremAssertions = new HashSet<PExp>();
    private final MapOfLists<Site, Application> myLoadedApplications =
            new MapOfLists<Site, Application>();
    private final Map<Site, MouseListener> myTheoremAppliers =
            new HashMap<Site, MouseListener>();
    private final Map<Site, MouseListener> myLocalTheoremSelectors =
            new HashMap<Site, MouseListener>();
    private boolean myInteractiveModeFlag = true;

    public static void main(String[] args) throws FlagDependencyException {
        MathSymbolTableBuilder bldr = new MathSymbolTableBuilder();

        FlagDependencies.seal();

        List<PExp> conjuncts = new LinkedList<PExp>();

        MTType bool = bldr.getTypeGraph().BOOLEAN;

        List<PExp> expArgs = new LinkedList<PExp>();
        expArgs.add(new PSymbol(bool, null, "x"));
        expArgs.add(new PSymbol(bool, null, "y"));

        conjuncts.add(new PSymbol(bool, null, "=", expArgs,
                PSymbol.DisplayType.INFIX));

        expArgs.clear();
        expArgs.add(new PSymbol(bool, null, "z"));

        conjuncts.add(new PSymbol(bool, null, "not", expArgs,
                PSymbol.DisplayType.PREFIX));

        Antecedent a = new Antecedent(conjuncts);

        conjuncts.clear();
        expArgs.clear();

        expArgs.add(new PSymbol(bool, null, "b"));
        expArgs.add(new PSymbol(bool, null, "c"));

        PExp first =
                new PSymbol(bool, null, "+", expArgs, PSymbol.DisplayType.INFIX);

        expArgs.clear();
        expArgs.add(new PSymbol(bool, null, "a"));
        expArgs.add(first);

        PExp cc =
                new PSymbol(bool, null, "*", expArgs, PSymbol.DisplayType.INFIX);

        conjuncts.add(cc);

        Consequent c = new Consequent(conjuncts);

        VC vc = new VC("0_1", a, c);
        JProverFrame p =
                new JProverFrame(new PerVCProverModel(bldr.getTypeGraph(),
                "0_1", vc, new EmptyImmutableList<Theorem>()));
        p.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        p.setVisible(true);

        int[] path = {1};
        /*NodeIdentifier nid = new NodeIdentifier(cc, path);

         p.highlightPExp(nid, new Color(200, 200, 200));*/
    }

    public JProverFrame(final PerVCProverModel m) {
        try {
            myPlayButton =
                    new JButton(new ImageIcon(ImageIO.read(ClassLoader
                    .getSystemResource("images/play.png"))));
            myPlayButton.setToolTipText("Start Automated Prover");

            myPauseButton =
                    new JButton(new ImageIcon(ImageIO.read(ClassLoader
                    .getSystemResource("images/pause.png"))));
            myPauseButton.setToolTipText("Enter Interactive Mode");

            myStepButton =
                    new JButton(new ImageIcon(ImageIO.read(ClassLoader
                    .getSystemResource("images/step.png"))));
            myStepButton.setToolTipText("Step Automated Prover");

            myStopButton =
                    new JButton(new ImageIcon(ImageIO.read(ClassLoader
                    .getSystemResource("images/stop.png"))));
            myStopButton.setToolTipText("Clear Proof Progress");
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        myBasicArea = buildBasicPanel();

        ImmutableList<Theorem> globalTheorems = m.getTheoremLibrary();

        myProofDisplay = new JProofDisplay(m);
        myProverStateDisplay = new JProverStateDisplay(m);
        myProverStateDisplay.addMouseListener(APPLICATION_CANCELLER);

        myDetailsArea = buildDetailsArea();

        setLayout(new BorderLayout());

        JPanel topLevel = new JPanel();
        LayoutManager topLevelLayout = new BorderLayout();
        topLevel.setLayout(topLevelLayout);

        topLevel.add(myBasicArea, BorderLayout.NORTH);
        topLevel.add(myDetailsArea, BorderLayout.CENTER);

        myTopLevelScrollWrapper = new JScrollPane(topLevel);
        add(myTopLevelScrollWrapper, BorderLayout.CENTER);

        myDetailsArea.setVisible(false);
        myDetailsCheckBox.addChangeListener(new DetailsDisplayer());

        myCancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });

        setGlobalTheorems(globalTheorems);

        setModel(m);
        setInteractiveMode(true);

        pack();
    }

    public void setGlobalTheorems(Iterable<Theorem> globalTheorems) {
        myGlobalTheoremAssertions.clear();

        DefaultListModel m = new DefaultListModel();
        for (Theorem t : globalTheorems) {
            myGlobalTheoremAssertions.add(t.getAssertion());
            m.addElement(t);
        }
        m.addElement(new DummyTheorem(ExistentialInstantiation.INSTANCE));
        myTheoremList.setModel(m);
        if (!FlagManager.getInstance().isFlagSet("nodebug")) {
            System.out.println("setGlobalTheorems");
        }
        prepForTheoremSelection();
    }

    public void setInteractiveMode(final boolean interactive) {
        boolean changed =
                (interactive && !myInteractiveModeFlag)
                || (!interactive && myInteractiveModeFlag);

        if (changed) {
            myInteractiveModeFlag = interactive;

            if (interactive) {
                myProverStateDisplay.getModel().setChangeEventMode(
                        PerVCProverModel.ChangeEventMode.ALWAYS);
                myProverStateDisplay.getModel().addChangeListener(
                        TO_THEOREM_SELECTION);
                myProverStateDisplay.getModel().touch();
                prepForTheoremSelection();
            } else {
                myProverStateDisplay.getModel().setChangeEventMode(
                        PerVCProverModel.ChangeEventMode.INTERMITTENT);
                removeClickTargetsFromModel();
                myProverStateDisplay.getModel().removeChangeListener(
                        TO_THEOREM_SELECTION);
            }
        }
    }

    private void removeClickTargetsFromModel() {
        for (Map.Entry<Site, MouseListener> selector : myLocalTheoremSelectors
                .entrySet()) {

            myProverStateDisplay.removeMouseListener(selector.getKey(),
                    selector.getValue());
        }
        for (Map.Entry<Site, MouseListener> applier : myTheoremAppliers
                .entrySet()) {

            myProverStateDisplay.removeMouseListener(applier.getKey(), applier
                    .getValue());
        }
    }

    public void addNextVCButtonActionListener(ActionListener l) {
        myNextVCButton.addActionListener(l);
        mySkipButton.addActionListener(l);
    }

    public void addLastVCButtonActionListener(ActionListener l) {
        myLastVCButton.addActionListener(l);
    }

    public void addPlayButtonActionListener(ActionListener l) {
        myPlayButton.addActionListener(l);
    }

    public void addPauseButtonActionListener(ActionListener l) {
        myPauseButton.addActionListener(l);
    }

    public void addStopButtonActionListener(ActionListener l) {
        myStopButton.addActionListener(l);
    }

    public void addStepButtonActionListener(ActionListener l) {
        myStepButton.addActionListener(l);
    }

    public void setModel(PerVCProverModel model) {
        //Clean up the old model
        removeClickTargetsFromModel();
        myProverStateDisplay.getModel().removeChangeListener(
                TO_THEOREM_SELECTION);

        //Reflect the new model in all subcomponents
        myProverStateDisplay.setModel(model);
        myProofDisplay.setModel(model);
        myProvingLabel.setText("Proving " + model.getTheoremName() + "...");

        if (myInteractiveModeFlag) {
            //Set up the new model
            model.setChangeEventMode(PerVCProverModel.ChangeEventMode.ALWAYS);
            model.addChangeListener(TO_THEOREM_SELECTION);

            //Put us into theorem selection state
            prepForTheoremSelection();
        } else {
            model
                    .setChangeEventMode(PerVCProverModel.ChangeEventMode.INTERMITTENT);
        }
    }

    public PerVCProverModel getModel() {
        return myProverStateDisplay.getModel();
    }

    private void prepForTheoremApplication(Theorem theorem) {
        removeClickTargetsFromModel();

        myLoadedApplications.clear();
        myProverStateDisplay.clearHighlights();
        myTheoremAppliers.clear();

        myProverStateDisplay.addMouseListener(APPLICATION_CANCELLER);

        activateTransformations(theorem.getTransformations());
    }

    private void activateTransformations(List<Transformation> transformations) {
        PerVCProverModel model = myProverStateDisplay.getModel();

        Iterator<Application> applications;
        Application application;
        for (Transformation t : transformations) {
            applications = t.getApplications(model);

            while (applications.hasNext()) {
                application = applications.next();

                for (Site nid : application.involvedSubExpressions()) {
                    myProverStateDisplay.highlightPExp(nid,
                            POSSIBLE_APPLICATION);
                    myLoadedApplications.putElement(nid, application);
                    myProverStateDisplay.addMouseListener(nid,
                            APPLICATION_APPLIER);
                    myTheoremAppliers.put(nid, APPLICATION_APPLIER);
                }
            }
        }
    }

    private void prepForTheoremSelection() {
        removeClickTargetsFromModel();

        myLoadedApplications.clear();
        myProverStateDisplay.clearHighlights();
        myTheoremAppliers.clear();
        myTheoremList.getSelectionModel().clearSelection();

        myProverStateDisplay.removeMouseListener(APPLICATION_CANCELLER);

        Iterator<Site> antecedents =
                myProverStateDisplay.getModel()
                .topLevelAntecedentSiteIterator();
        Site antecedent;
        while (antecedents.hasNext()) {
            antecedent = antecedents.next();

            myProverStateDisplay.addMouseListener(antecedent,
                    LOCAL_THEOREM_SELECT);
            myProverStateDisplay.highlightPExp(antecedent, POSSIBLE_THEOREM);
            myLocalTheoremSelectors.put(antecedent, LOCAL_THEOREM_SELECT);
        }

        List<Transformation> defaultTransforms =
                new LinkedList<Transformation>();
        defaultTransforms.add(EliminateTrueConjunctInConsequent.INSTANCE);
        defaultTransforms
                .add(ReplaceSymmetricEqualityWithTrueInConsequent.INSTANCE);

        activateTransformations(defaultTransforms);
    }

    public void highlightPExp(Site s, Color c) {
        myProverStateDisplay.highlightPExp(s, c);
    }

    private JComponent buildDetailsArea() {
        JPanel panel = new JPanel(new BorderLayout());

        JSplitPane split =
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
                buildProofStatusArea(), buildTheoremListPanel());
        split.setResizeWeight(1);

        panel.add(split, BorderLayout.CENTER);

        return panel;
    }

    private JComponent buildProofStatusArea() {
        return new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
                buildProofStateDisplayArea(), buildProofArea());
    }

    private JComponent buildProofStateDisplayArea() {
        JScrollPane scroll = new JScrollPane(myProverStateDisplay);

        return scroll;
    }

    private JComponent buildProofArea() {
        return myProofDisplay;
    }

    private JPanel buildTheoremListPanel() {
        JPanel theoremListPanel = new JPanel(new BorderLayout());

        myTheoremList = new JList();
        myTheoremList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        myTheoremList.getSelectionModel().addListSelectionListener(
                new GlobalTheoremSelect());

        JScrollPane theoremView = new JScrollPane(myTheoremList);
        theoremView
                .setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        theoremView
                .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JTextField search = new JTextField();
        search.getDocument().addDocumentListener(new TheoremSearch(search));

        theoremListPanel.add(search, BorderLayout.NORTH);
        theoremListPanel.add(theoremView, BorderLayout.CENTER);

        JMenuBar filterListBar = new JMenuBar();
        JMenu filterList = new JMenu("Filters");

        JMenuItem showAll = new JMenuItem("Clear All Filters");
        filterList.add(showAll);
        filterList.add(new JSeparator());

        filterList.add(new JCheckBoxMenuItem("Filter Unapplicable"));
        filterList.add(new JCheckBoxMenuItem("Filter Complexifying"));

        filterListBar.add(filterList);

        theoremListPanel.add(filterListBar, BorderLayout.SOUTH);

        return theoremListPanel;
    }

    private JPanel buildBasicPanel() {
        JPanel basicPanel = new JPanel(new BorderLayout());
        basicPanel.add(myProvingLabel, BorderLayout.NORTH);
        basicPanel.add(buildProgressPanel(), BorderLayout.CENTER);
        basicPanel.add(buildButtonPanel(), BorderLayout.SOUTH);

        return basicPanel;
    }

    private JPanel buildProgressPanel() {
        JPanel verticalProgressPanel = new JPanel();
        BoxLayout verticalProgressPanelLayout =
                new BoxLayout(verticalProgressPanel, BoxLayout.Y_AXIS);
        verticalProgressPanel.setLayout(verticalProgressPanelLayout);

        verticalProgressPanel.add(Box.createVerticalGlue());

        JPanel horizontalProgressPanel = new JPanel();
        BoxLayout horizontalProgressPanelLayout =
                new BoxLayout(horizontalProgressPanel, BoxLayout.X_AXIS);
        horizontalProgressPanel.setLayout(horizontalProgressPanelLayout);

        horizontalProgressPanel.add(Box.createHorizontalStrut(20));

        JProgressBar progress = new JProgressBar(0, 100);
        progress.setValue(33);
        progress.setStringPainted(true);

        horizontalProgressPanel.add(progress);
        horizontalProgressPanel.add(Box.createHorizontalStrut(20));

        verticalProgressPanel.add(horizontalProgressPanel);
        verticalProgressPanel.add(Box.createVerticalGlue());

        return verticalProgressPanel;
    }

    private JPanel buildButtonPanel() {
        JPanel buttonPanel = new JPanel();
        BoxLayout buttonPanelLayout =
                new BoxLayout(buttonPanel, BoxLayout.X_AXIS);
        buttonPanel.setLayout(buttonPanelLayout);

        buttonPanel.add(myDetailsCheckBox);
        buttonPanel.add(Box.createHorizontalStrut(10));

        JPanel blankPanel = new JPanel();
        myOptionalTransportPanel.add(blankPanel, CONTROLS_HIDDEN);
        myOptionalTransportPanel.add(buildTransportControlPanel(),
                CONTROLS_VISIBLE);

        //buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(myOptionalTransportPanel);
        //buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(myCancelButton);
        buttonPanel.add(mySkipButton);

        return buttonPanel;
    }

    private JPanel buildTransportControlPanel() {
        JPanel transportControlPanel = new JPanel();
        BoxLayout transportControlPanelLayout =
                new BoxLayout(transportControlPanel, BoxLayout.X_AXIS);
        transportControlPanel.setLayout(transportControlPanelLayout);
        transportControlPanel.add(myPlayButton);
        transportControlPanel.add(myStepButton);
        transportControlPanel.add(myPauseButton);
        transportControlPanel.add(myStopButton);
        transportControlPanel.add(Box.createHorizontalStrut(4));
        transportControlPanel.add(myLastVCButton);
        transportControlPanel.add(myNextVCButton);

        return transportControlPanel;
    }

    private class ApplicationCanceller extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                System.out.println("ApplicationCanceller");
                prepForTheoremSelection();
            }
        }
    }

    private class ApplicationApplier extends MouseAdapter {

        @Override
        public void mouseEntered(MouseEvent e) {
            myProverStateDisplay.highlightPExp((Site) e.getSource(),
                    POSSIBLE_APPLICATION_HOVER);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            myProverStateDisplay.highlightPExp((Site) e.getSource(),
                    POSSIBLE_APPLICATION);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                List<Application> applications =
                        myLoadedApplications.getList((Site) e.getSource());

                if (applications.isEmpty()) {
                    throw new RuntimeException("This can't be!");
                } else if (applications.size() == 1) {
                    applications.get(0).apply(myProverStateDisplay.getModel());
                } else {
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem menuItem;

                    for (Application a : applications) {
                        menuItem = new JMenuItem(a.description());
                        menuItem.addActionListener(new ApplicationSelect(a));
                        popup.add(menuItem);
                    }

                    popup.show(myProverStateDisplay, e.getX(), e.getY());
                }

                //Note that the change in the underlying model will kick us back
                //into theorem selection mode
            }
        }
    }

    private class ApplicationSelect implements ActionListener {

        private final Application myApplication;

        public ApplicationSelect(Application a) {
            myApplication = a;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            myApplication.apply(myProverStateDisplay.getModel());
        }
    }

    private class LocalTheoremSelect extends MouseAdapter {

        @Override
        public void mouseClicked(final MouseEvent e) {
            System.out.println("LocalTheoremSelect");
            prepForTheoremApplication((Theorem) ((Site) e.getSource()).conjunct);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            myProverStateDisplay.highlightPExp((Site) e.getSource(),
                    POSSIBLE_THEOREM_HOVER);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            myProverStateDisplay.highlightPExp((Site) e.getSource(),
                    POSSIBLE_THEOREM);
        }
    }

    private class GlobalTheoremSelect implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent event) {
            if (!event.getValueIsAdjusting()
                    && myTheoremList.getSelectedValue() != null) {
                myProverStateDisplay.clearHighlights();
                myLoadedApplications.clear();
                for (Map.Entry<Site, MouseListener> applier : myTheoremAppliers
                        .entrySet()) {

                    myProverStateDisplay.removeMouseListener(applier.getKey(),
                            applier.getValue());
                }
                myTheoremAppliers.clear();

                System.out.println("GlobalTheoremSelect");
                prepForTheoremApplication((Theorem) myTheoremList
                        .getSelectedValue());

            }
        }
    }

    private class EnterTheoremSelectionOnModelChange implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            if (!FlagManager.getInstance().isFlagSet("nodebug")) {
                System.out
                        .println("JProverFrame.EnterTheoremSelectionOnModelChange - enter");
            }
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (!FlagManager.getInstance().isFlagSet("nodebug")) {
                        System.out
                                .println("JProverFrame.EnterTheoremSelectionOnModelChange - enterJob");
                    }
                    prepForTheoremSelection();
                    if (!FlagManager.getInstance().isFlagSet("nodebug")) {
                        System.out
                                .println("JProverFrame.EnterTheoremSelectionOnModelChange - exitJob");
                    }
                }
            });
            if (!FlagManager.getInstance().isFlagSet("nodebug")) {
                System.out
                        .println("JProverFrame.EnterTheoremSelectionOnModelChange - exit");
            }
        }
    }

    private class TheoremSearch implements DocumentListener {

        private final JTextField mySearchField;

        public TheoremSearch(JTextField searchField) {
            mySearchField = searchField;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            changedUpdate(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            changedUpdate(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            ImmutableList<Theorem> theorems =
                    myProverStateDisplay.getModel().getGlobalTheoremLibrary();

            String searchText = mySearchField.getText().toLowerCase().trim();
            if (searchText.isEmpty()) {
                setGlobalTheorems(theorems);
            } else {
                //All this complicated stuff turns a "simple" regex (i.e., one
                //that only recognizes "*" for "anything" and "\*" for "literal
                //star" into an "official" regex supported by Pattern
                String pattern = ".*?";
                String[] literalStarSplit = searchText.split("\\\\\\*");

                if (searchText.startsWith("\\*")) {
                    pattern += "\\*";
                }

                boolean firstNonLiteralStarChunk = true;
                for (String nonLiteralStarChunk : literalStarSplit) {
                    if (firstNonLiteralStarChunk) {
                        firstNonLiteralStarChunk = false;
                    } else {
                        pattern += "\\*";
                    }

                    String[] literalChunks = nonLiteralStarChunk.split("\\*");

                    boolean firstLiteralChunk = true;
                    for (String literalChunk : literalChunks) {
                        if (firstLiteralChunk) {
                            firstLiteralChunk = false;
                        } else {
                            pattern += ".*?";
                        }

                        pattern += Pattern.quote(literalChunk.toLowerCase());
                    }
                }
                if (searchText.endsWith("\\*")) {
                    pattern += "\\*";
                }

                pattern += ".*?";

                //Now that we have an official regex, we select the theorems
                //that match
                List<Theorem> matchingTheorems = new LinkedList<Theorem>();
                for (Theorem t : theorems) {
                    if (Pattern.matches(pattern, t.getAssertion().toString()
                            .toLowerCase())) {
                        matchingTheorems.add(t);
                    }
                }

                setGlobalTheorems(matchingTheorems);
            }
        }
    }

    private class DetailsDisplayer implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent arg0) {
            boolean checked = myDetailsCheckBox.isSelected();

            if (checked != myDetailsArea.isVisible()) {
                myDetailsArea.setVisible(checked);

                String state;
                if (checked) {
                    state = CONTROLS_VISIBLE;
                } else {
                    state = CONTROLS_HIDDEN;
                }
                myOptionalTransportLayout.show(myOptionalTransportPanel, state);

                /*myBasicArea.setPreferredSize(myBasicArea.getSize());
                 JProverFrame.this.setPreferredSize(null);
                 myTopLevelScrollWrapper.setPreferredSize(null);

                 JProverFrame.this.pack();

                 myTopLevelScrollWrapper.setPreferredSize(
                 JProverFrame.this.getPreferredSize());
                 JProverFrame.this.setPreferredSize(
                 JProverFrame.this.getPreferredSize());
                 myBasicArea.setPreferredSize(null);

                 if (checked) {
                 myDetailsArea.setPreferredSize(null);
                 }*/

                JProverFrame.this.pack();
                myDetailsArea
                        .setPreferredSize(myDetailsArea.getPreferredSize());
            }
        }
    }
}
