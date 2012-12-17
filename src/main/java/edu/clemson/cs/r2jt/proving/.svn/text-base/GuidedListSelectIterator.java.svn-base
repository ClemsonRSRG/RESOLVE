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

public class GuidedListSelectIterator<T> extends JDialog 
		implements Iterator<T> {
	
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
		while(options.hasNext()) {
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