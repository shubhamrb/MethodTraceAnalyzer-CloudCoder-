package graphicalUserInterface;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import dataparser.DataParser;
import graphicalUserInterface.LogTableCreator;

import java.awt.SystemColor;

// log file data in tabular form using TableBuilder
public class LogInfoFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8048540120803646699L;
	private JPanel contentPane;
	/**
	 * Create the frame.
	 */
	public LogInfoFrame(DataParser dataParser) {
		this.setBounds(100, 100, 800, 600);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);       //Maximize the window
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.window);
		setContentPane(contentPane);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		// Building table of analyzed log file
		LogTableCreator table = new LogTableCreator(dataParser);   //Populate data into table
	    this.setSize(300, 300);
	    this.add(new JScrollPane(table.getTable()));
	    this.setVisible(true);
	    
		
	}

}
