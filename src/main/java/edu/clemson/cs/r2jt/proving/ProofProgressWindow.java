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

class ProofProgressWindow extends JFrame implements ActionListener, 
		ProverListener {
	private static final long serialVersionUID = 5289008712520472101L;
	
	private JProgressBar myProgressBar;
	
	private ActionCanceller myCanceller;
	
	public ProofProgressWindow(final String name, 
			final ActionCanceller canceller) {
		
		myCanceller = canceller;
		
		setTitle("Proof progress...");
		
		myProgressBar= new JProgressBar(0, 10000);
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
