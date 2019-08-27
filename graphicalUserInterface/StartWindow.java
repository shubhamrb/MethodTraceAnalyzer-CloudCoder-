package graphicalUserInterface;

import java.awt.EventQueue;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import dataparser.*;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JButton;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

import dataparser.CommonMethods;
import dataparser.InvalidLogFileException;
import graphicalUserInterface.LogInfoFrame;
import graphicalUserInterface.LogComparisionTable;
import graphicalUserInterface.StartWindow;;

public class StartWindow {
	private static StartWindow win;
	private JFrame frame;
	private DataParser dataParser1;
	private DataParser dataParser2;
	private LogComparator logComparator;

	private static boolean state=true;

	/**
	 * Launch the application.
	 **/
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					win = new StartWindow();
					if(state)
						win.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public StartWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 250, 100);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Log Report");
		//JFrame frame2 = new JFrame();
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		String[] options2 = new String[2];
		options2[0] = new String("Get Single Log Report");
		options2[1] = new String("Compare Two Log Files");
		int result = JOptionPane.showOptionDialog(new JFrame(), "Select file Using these Option!","Log Report Generator",0, JOptionPane.INFORMATION_MESSAGE, null, options2, null);
		if(result==JOptionPane.YES_OPTION)
		{
		JButton btnFileselect = new JButton("Select Log File");
		btnFileselect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try{
					final JFileChooser jfc1 = new JFileChooser();
					
					
					FileNameExtensionFilter filter = new FileNameExtensionFilter(  			//Can choose only .log files
						    "LOG files", "log");
					jfc1.setFileFilter(filter);
					jfc1.setDialogTitle("Select new file");									//Select log files
					int returnVal1 = jfc1.showOpenDialog(frame);
					if(returnVal1 == JFileChooser.APPROVE_OPTION) {
						// Parses the file and instantiates the p1 object(because 1 is passed)
						parseLogData(jfc1.getSelectedFile(), 1);
						showLogTableAndGraph(dataParser1, jfc1.getSelectedFile().getName());
					}
				}
				catch(InvalidLogFileException ilfe){
					JOptionPane.showMessageDialog(frame, "Invalid Log File");
				}
			}
		});
		frame.getContentPane().add(btnFileselect, BorderLayout.NORTH);
	  }
	  else if(result==JOptionPane.NO_OPTION)
	  {
		  state=false;
		  try{
				final JFileChooser jfc1 = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(	//Can select only .log file
					    "LOG files", "log");
				jfc1.setFileFilter(filter);
				jfc1.setDialogTitle("Select First file");						//Select first File
				int returnVal1 = jfc1.showOpenDialog(frame);
				if(returnVal1 == JFileChooser.APPROVE_OPTION){
																				// Parse the first file with progress bar
					parseLogData(jfc1.getSelectedFile(), 1);
					try{
					JFileChooser jfc2 = new JFileChooser();
					FileNameExtensionFilter filter1 = new FileNameExtensionFilter(
						    "LOG files", "log");
					jfc2.setFileFilter(filter1);
					jfc2.setDialogTitle("Select Second file");
					int returnVal2 = jfc2.showOpenDialog(frame);
					if(returnVal2 == JFileChooser.APPROVE_OPTION)
					{
						// Parse the second file with progress bar
						parseLogData(jfc2.getSelectedFile(), 2);		
						// Then Compare them and show the comparison tables
						compareTwoLogFiles(dataParser1, dataParser2);
						showComparisionTable(logComparator);
							
					}
					JFrame frame1 = new JFrame();
					String[] options1 = new String[2];
					options1[0] = new String("First Log Report");
					options1[1] = new String("Second Log Report");
					int result2 = JOptionPane.showOptionDialog(frame1.getContentPane(), "See the log file report.","Log Report File Alert",0, JOptionPane.INFORMATION_MESSAGE, null, options1, null);
					if(result2 == JOptionPane.YES_OPTION)
					{
						parseLogData(jfc1.getSelectedFile(), 1);
						showLogTableAndGraph(dataParser1, jfc1.getSelectedFile().getName());
					}
					else if(result2 == JOptionPane.NO_OPTION) 
					{
						parseLogData(jfc2.getSelectedFile(), 2);
						showLogTableAndGraph(dataParser2, jfc2.getSelectedFile().getName());
					}
					}
				catch(InvalidLogFileException ilfe){
					JOptionPane.showMessageDialog(frame, "Invalid Log File");
				}
					
				}
			}
			catch(InvalidLogFileException ilfe){
				JOptionPane.showMessageDialog(frame, "Invalid Log File");
			}
		  
		}else {
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		}
	}
			

	
	
	// Takes the parser and displays the table and graph using the FrameForTable and GraphViewer classes 
	private void showLogTableAndGraph(DataParser dataParser, String fileName){
		final LogInfoFrame frameForResult = new LogInfoFrame(dataParser);
		final LogChartFrame graphViewer = new LogChartFrame(dataParser);
		graphViewer.setDefaultCloseOperation(LogChartFrame.DISPOSE_ON_CLOSE);
		frameForResult.setDefaultCloseOperation(LogInfoFrame.DISPOSE_ON_CLOSE);
		frameForResult.setTitle(fileName+ " Total Tracetime:"+dataParser.getTraceTime());
		graphViewer.setTitle("Graph For "+fileName);
		frameForResult.setVisible(true);
		graphViewer.setVisible(true);
	}
	// Takes the LogComparator and displays the table using TableForComparision Class 
	private void showComparisionTable(LogComparator lc){
		List<CommonMethods>[] cm = lc.getCommonCriticalMethods();
		JTable[] tables = new LogComparisionTable(cm).createTablesForComparision();
		JTable table1 = tables[0];
		JTable table2 = tables[1];
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Container c = frame.getContentPane();
		c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));;
		c.add(new JLabel("<html><span style='font-size:24px'>"+"Critical Methods in first log file"+"</span></html>"));
		c.add(new JScrollPane(table1));
		c.add(new JLabel("<html><span style='font-size:24px'>"+"Critical Methods in second log file"+"</span></html>"));;
		c.add(new JScrollPane(table2));
		frame.pack();
		frame.setTitle("Comparision");
		frame.setVisible(true);
		
	}
	
	// parsing with progress bar
	private void parseLogData(final File f, final int parserNo) throws InvalidLogFileException{
		final JDialog dialog = new JDialog(frame, true);
		dialog.setUndecorated(true);
		dialog.setLocationRelativeTo(null);
		JProgressBar bar = new JProgressBar();
		bar.setIndeterminate(true);
		bar.setStringPainted(true);
		bar.setString("    Parsing The Log File   ");
		dialog.add(bar);
		dialog.pack();
		SwingWorker<DataParser, Void> worker = new SwingWorker<DataParser, Void>(){

			@Override
			protected DataParser doInBackground() throws Exception {
				DataParser dataParser = new DataParser(f);
				return dataParser;
			}
			@Override
			protected void done(){
				try {
					if(parserNo == 1){
						dataParser1 = get();
					}
					if(parserNo == 2){
						dataParser2 = get();
					}					
				} catch (InterruptedException
						| ExecutionException e) {
					e.printStackTrace();
				}
			    dialog.dispose();
			}
			};
		worker.execute();
		dialog.setVisible(true);		
	}
	
	//method for comparing the two log files
	private void compareTwoLogFiles(final DataParser p1, final DataParser p2){
		final JDialog dialog = new JDialog(frame, true);
		dialog.setLocationRelativeTo(null);
		dialog.setUndecorated(true);
		JProgressBar bar = new JProgressBar();
		bar.setIndeterminate(true);
		bar.setStringPainted(true);
		bar.setString("    Comparing the log files    ");
		dialog.add(bar);
		dialog.pack();
		SwingWorker<LogComparator, Void> worker = new SwingWorker<LogComparator, Void>(){

			@Override
			protected LogComparator doInBackground() throws Exception {
				LogComparator lc = new LogComparator(p1, p2);
				return lc;
			}
			@Override
			protected void done(){
				try {
					logComparator = get();				
				} catch (InterruptedException
						| ExecutionException e) {
					e.printStackTrace();
				}
			    dialog.dispose();
			}
			};
		worker.execute();
		dialog.setVisible(true);	
	}

}
