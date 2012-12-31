package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.mathtype.MTType;
import edu.clemson.cs.r2jt.mathtype.MathSymbolTableBuilder;
import edu.clemson.cs.r2jt.proving.absyn.NodeIdentifier;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol;
import edu.clemson.cs.r2jt.proving.immutableadts.EmptyImmutableList;
import edu.clemson.cs.r2jt.proving.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.proving.immutableadts.SimpleImmutableList;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import edu.clemson.cs.r2jt.proving2.utilities.MapOfLists;
import edu.clemson.cs.r2jt.utilities.FlagDependencies;
import edu.clemson.cs.r2jt.utilities.FlagDependencyException;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
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
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class JProverFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String CONTROLS_HIDDEN = "hidden";
    private static final String CONTROLS_VISIBLE = "visible";
    
    private final ApplicationApplier APPLICATION_APPLIER = 
            new ApplicationApplier();
    
    private final LocalTheoremSelect LOCAL_THEOREM_SELECT =
            new LocalTheoremSelect();

    private final JProverFrame PARENT_THIS = this;

    private final JProverStateDisplay myProverStateDisplay;

    private final JCheckBox myDetailsCheckBox = new JCheckBox("Details");
    private final JComponent myDetailsArea;
    private JList myTheoremList;
    private JProofDisplay myProofDisplay;

    private final CardLayout myOptionalTransportLayout = new CardLayout();
    private final JPanel myOptionalTransportPanel =
            new JPanel(myOptionalTransportLayout);

    private final JComponent myBasicArea = buildBasicPanel();

    private ImmutableList<Theorem> myGlobalTheorems;
    private Set<PExp> myGlobalTheoremAssertions = new HashSet<PExp>();
    
    private final MapOfLists<Site, Application> myLoadedApplications = 
            new MapOfLists<Site, Application>();
    
    private final Map<Site, MouseListener> myTheoremAppliers = 
            new HashMap<Site, MouseListener>();
    
    private final Map<Site, MouseListener> myLocalTheoremSelectors =
            new HashMap<Site, MouseListener>();
    
    public static void main(String[] args) throws FlagDependencyException {
        JProverFrame p = new JProverFrame();
        p.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        p.setVisible(true);

        FlagDependencies.seal();

        MathSymbolTableBuilder bldr = new MathSymbolTableBuilder();

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
        p.setModel(new PerVCProverModel(vc, new EmptyImmutableList<Theorem>()));

        int[] path = { 1 };
        /*NodeIdentifier nid = new NodeIdentifier(cc, path);

        p.highlightPExp(nid, new Color(200, 200, 200));*/
    }

    public JProverFrame() {
        this(new PerVCProverModel(new LinkedList<PExp>(),
                new LinkedList<PExp>(), new EmptyImmutableList()));
    }

    public JProverFrame(PerVCProverModel m) {
        ImmutableList<Theorem> globalTheorems = m.getTheoremLibrary();
        
        myProofDisplay = new JProofDisplay(m);
        myProverStateDisplay = new JProverStateDisplay(m);
        m.setChangeEventMode(PerVCProverModel.ChangeEventMode.ALWAYS);
        myDetailsArea = buildDetailsArea();

        setLayout(new BorderLayout());

        JPanel topLevel = new JPanel();
        LayoutManager topLevelLayout = new BorderLayout();
        topLevel.setLayout(topLevelLayout);

        topLevel.add(myBasicArea, BorderLayout.NORTH);
        topLevel.add(myDetailsArea, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(topLevel);
        add(scroll, BorderLayout.CENTER);

        myDetailsArea.setVisible(false);
        myDetailsCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent arg0) {
                boolean checked = myDetailsCheckBox.isSelected();

                if (checked != myDetailsArea.isVisible()) {
                    myDetailsArea.setVisible(checked);

                    String state;
                    if (checked) {
                        state = CONTROLS_VISIBLE;
                    }
                    else {
                        myDetailsArea.setPreferredSize(myDetailsArea.getSize());
                        state = CONTROLS_HIDDEN;
                    }
                    myOptionalTransportLayout.show(myOptionalTransportPanel,
                            state);

                    myBasicArea.setPreferredSize(myBasicArea.getSize());
                    PARENT_THIS.pack();
                    myBasicArea.setPreferredSize(null);

                    if (checked) {
                        myDetailsArea.setPreferredSize(null);
                    }
                }
            }
        });

        setGlobalTheorems(globalTheorems);
        
        pack();
    }

    public void setGlobalTheorems(ImmutableList<Theorem> globalTheorems) {
        myGlobalTheorems = globalTheorems;
        myGlobalTheoremAssertions.clear();
        
        DefaultListModel m = new DefaultListModel();
        for (Theorem t : globalTheorems) {
            myGlobalTheoremAssertions.add(t.getAssertion());
            m.addElement(t);
        }
        myTheoremList.setModel(m);
    }

    public void setModel(PerVCProverModel model) {
        myProverStateDisplay.setModel(model);
        myProofDisplay.setModel(model);
        model.setChangeEventMode(PerVCProverModel.ChangeEventMode.ALWAYS);
    }

    public PerVCProverModel getModel() {
        return myProverStateDisplay.getModel();
    }
    
    private void prepForTheoremApplication(Theorem theorem) {
        for (Map.Entry<Site, MouseListener> selector : 
                myLocalTheoremSelectors.entrySet()) {

            myProverStateDisplay.removeMouseListener(selector.getKey(), 
                    selector.getValue());
        }
        
        myLoadedApplications.clear();
        myProverStateDisplay.clearHighlights();
        myTheoremAppliers.clear();
        
        List<Transformation> transformations = 
                theorem.getTransformations(myGlobalTheoremAssertions);

        PerVCProverModel model = myProverStateDisplay.getModel();

        Iterator<Application> applications;
        Application application;
        for (Transformation t : transformations) {
            applications = t.getApplications(model);

            while (applications.hasNext()) {
                application = applications.next();

                for (Site nid : application.involvedSubExpressions()) {
                    myProverStateDisplay.highlightPExp(nid, Color.LIGHT_GRAY);
                    myLoadedApplications.putElement(nid, application);
                    myProverStateDisplay.addMouseListener(nid, 
                            APPLICATION_APPLIER);
                    myTheoremAppliers.put(nid, APPLICATION_APPLIER);
                }
            }
        }
    }
    
    private void prepForTheoremSelection() {
        for (Map.Entry<Site, MouseListener> applier : 
                myTheoremAppliers.entrySet()) {

            myProverStateDisplay.removeMouseListener(applier.getKey(), 
                    applier.getValue());
        }
        myLoadedApplications.clear();
        myProverStateDisplay.clearHighlights();
        myTheoremAppliers.clear();
        myTheoremList.getSelectionModel().clearSelection();
        
        Iterator<Site> antecedents = myProverStateDisplay.getModel()
                .topLevelAntecedentSiteIterator();
        Site antecedent;
        while (antecedents.hasNext()) {
            antecedent = antecedents.next();
            myProverStateDisplay.addMouseListener(antecedent, 
                    LOCAL_THEOREM_SELECT);
            myLocalTheoremSelectors.put(antecedent, LOCAL_THEOREM_SELECT);
        }
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

        theoremListPanel.add(new JTextField(), BorderLayout.NORTH);
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
        basicPanel.add(new JLabel("Proving VC 0_1..."), BorderLayout.NORTH);
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

        JProgressBar progress = new JProgressBar();
        progress = new JProgressBar(0, 100);
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
        buttonPanel.add(new JButton("Cancel"));
        buttonPanel.add(new JButton("Skip >>>"));

        return buttonPanel;
    }

    private JPanel buildTransportControlPanel() {
        JPanel transportControlPanel = new JPanel();
        BoxLayout transportControlPanelLayout =
                new BoxLayout(transportControlPanel, BoxLayout.X_AXIS);
        transportControlPanel.setLayout(transportControlPanelLayout);
        transportControlPanel.add(new JButton(">"));
        transportControlPanel.add(new JButton("||"));
        transportControlPanel.add(new JButton("@"));
        transportControlPanel.add(Box.createHorizontalStrut(4));
        transportControlPanel.add(new JButton("<VC"));
        transportControlPanel.add(new JButton("VC>"));

        return transportControlPanel;
    }
    
    private class ApplicationApplier extends MouseAdapter {
        
        @Override
        public void mouseEntered(MouseEvent e) {
            myProverStateDisplay.highlightPExp((Site) e.getSource(), 
                    Color.CYAN.brighter().brighter());
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            myProverStateDisplay.highlightPExp((Site) e.getSource(), 
                    Color.LIGHT_GRAY);
        }
        
        @Override
        public void mouseClicked(MouseEvent e) {
            List<Application> applications =
                    myLoadedApplications.getList((Site) e.getSource());
            
            if (applications.isEmpty()) {
                throw new RuntimeException("This can't be!");
            }
            else if (applications.size() == 1) {
                applications.get(0).apply(myProverStateDisplay.getModel());
            }
            else {
                throw new RuntimeException("MUUUULLLLTIIIIIBAAAAALLLL!!!  BLEERN!!!!  BLEEEEEEERN!!");
            }
            
            //Can't change the list of listeners in the listener
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    prepForTheoremSelection();
                }
            });
        }
    }
    
    private class LocalTheoremSelect extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            prepForTheoremApplication(
                    myProverStateDisplay.getModel().getLocalTheoremAncestor(
                        (Site) e.getSource()));
        }
        
        @Override
        public void mouseEntered(MouseEvent e) {
            myProverStateDisplay.highlightPExp((Site) e.getSource(), 
                    Color.CYAN.brighter().brighter());
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            myProverStateDisplay.highlightPExp((Site) e.getSource(), 
                    Color.LIGHT_GRAY);
        }
    }
    
    private class GlobalTheoremSelect implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent event) {
            if (!event.getValueIsAdjusting() && 
                    myTheoremList.getSelectedValue() != null) {
                myProverStateDisplay.clearHighlights();
                myLoadedApplications.clear();
                for (Map.Entry<Site, MouseListener> applier : 
                            myTheoremAppliers.entrySet()) {

                    myProverStateDisplay.removeMouseListener(applier.getKey(), 
                            applier.getValue());
                }
                myTheoremAppliers.clear();

                prepForTheoremApplication(
                        (Theorem) myTheoremList.getSelectedValue());
                
                
            }
        }
    }
}
