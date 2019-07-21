package graphicalUserInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;

import dataparser.DataParser;
import dataparser.LogTraceMethod;
import graphicalUserInterface.ButtonRenderer;
import gui.ChartBuilder;


// This jFrame is used to show the graphs using the ChartBuilder class
public class LogChartFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	/**
	 * Create the frame.
	 */
	public LogChartFrame(final DataParser dataParser) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(300, 100, 650, 650);
		contentPane = new JPanel();
		//contentPane.setSize(500, 5000);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		ChartBuilder chart = new ChartBuilder(dataParser);
		ChartPanel cp = new ChartPanel(chart.getChart());
		cp.addChartMouseListener(new ChartMouseListener() {
			
			@Override
			public void chartMouseMoved(ChartMouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void chartMouseClicked(ChartMouseEvent e) {
				Pattern pat = Pattern.compile(".+\\s\\(([0-9]+)\\),.+");
				Matcher match = pat.matcher(e.getEntity().toString());
				if(match.matches()){
					final LogTraceMethod method = dataParser.getMethodById(Integer.parseInt((match.group(1))));
					String[] columns = new String[]{
				            "Method Name", "Class Name", "Static", "Start Time", "End Time", "Total Runtime", "Parames", "Return Type", "Stack Trace"
					};
				    Object[][] data = new Object[][] {{method.getMethodsName(), method.getClass(), method.isStatic(), method.getStartTime(), method.getEndTime(), method.getRuntime(), method.getParameters(), method.getReturnType(), "StackTrace"}};
			        final JTable table = new JTable(data, columns);
			        table.setFont(new Font("Serif", Font.PLAIN, 20));
			        table.setRowHeight(40);
			  	    table.getColumnModel().getColumn(8).setCellRenderer(new ButtonRenderer());
			  	    table.addMouseListener(new java.awt.event.MouseAdapter() {
					    @Override
					    public void mouseClicked(java.awt.event.MouseEvent evt) {
					        int row = table.rowAtPoint(evt.getPoint());
					        int col = table.columnAtPoint(evt.getPoint());
					        if (col==8) {
					        	JDialog d = new JDialog();
					        	d.setSize(500,1000);
					            d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					        	try{
						        	List<String> methodStackList = new ArrayList<String>(method.getMethodStack());
					        		d.setTitle(method.getMethodsName()+" Stack Trace");
						            DefaultTableModel model = new DefaultTableModel();
						        	JTable t = new JTable(model);
						      	    model.addColumn("<html><font size=5 color=red>"+"StackTrace"+"</font></html>");
						      	    d.setTitle("StackTrace For "+method.getMethodsName());
						      	    for(String method: methodStackList){
						    		  model.addRow(new Object[] {method});
						    	    }
						            d.add(t);
						            d.setVisible(true);
					        	}
					        	catch(NullPointerException ex){
					        		d.setVisible(true);
					        	}
					        }
					    }
					});
					JFrame frameForMethodInfo = new JFrame();
					frameForMethodInfo.setPreferredSize(new Dimension(1000, 150));
					frameForMethodInfo.add(new JScrollPane(table));
			        frameForMethodInfo.setTitle(method.getMethodsName());
			        frameForMethodInfo.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);       
			        frameForMethodInfo.pack();
			        frameForMethodInfo.setVisible(true);   
				}
			}
		});
		contentPane.setLayout(new java.awt.BorderLayout());
		contentPane.add(cp, BorderLayout.CENTER);
		setContentPane(contentPane);
		
	}
}

// Class used to render the button in the jtable
class ButtonRenderer extends JButton implements TableCellRenderer{
	public ButtonRenderer() {
		
	}
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		setText((value==null) ? "":value.toString());
		return this;
	}
}
