package graphicalUserInterface;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import dataparser.DataParser;
import dataparser.LogTraceMethod;
import graphicalUserInterface.ButtonRenderer;


// Class to create the table for log file details like(Methods, it's runtime,class name, etc.)

public class LogTableCreator {
	//To create Table in the JFrame
	DefaultTableModel model = new DefaultTableModel();
	private JTable table = new JTable(model);
	private int serialNo=1;
	
	@SuppressWarnings("serial")
	public LogTableCreator(final DataParser p){
	  model.addColumn("<html><h1><font size=5 color=red>"+"SNo"+"</font></h1></html>");					//Adding columns into the table
	  model.addColumn("<html><h1><font size=5 color=red>"+"Runtime"+"</font></h1></html>");
	  model.addColumn("<html><h1><font size=5 color=red>"+"Method"+"</font></h1></html>");
	  model.addColumn("<html><h1><font size=5 color=red>"+"Class Name"+"</font></h1></html>");
	  model.addColumn("<html><h1><font size=5 color=red>"+"Static Or Not"+"</font></h1></html>");
	  model.addColumn("<html><h1><font size=5 color=red>"+"Start Time"+"</font></h1></html>");
	  model.addColumn("<html><h1><font size=5 color=red>"+"End Time"+"</font></h1></html>");
	  model.addColumn("<html><h1><font size=5 color=red>"+"Parameter"+"</font></h1></html>");
	  model.addColumn("<html><h1><font size=5 color=red>"+"Return Type"+"</font></h1></html>");
	  model.addColumn("<html><h1><font size=5 color=red>"+"Stack Trace"+"</font></h1></html>");
	 
	  for(LogTraceMethod met: p.getIncompleteMethods()){
		  model.addRow(new Object[] {serialNo, met.getRuntime(),met.getMethodsName(), met.getClass(), met.isStatic(), met.getStartTime(), met.getEndTime(), met.getParameters(), met.getReturnType(), "Click to see stackTrace"}); 
		  serialNo++;
	  }
	  for(LogTraceMethod met: p.sortByRuntime()){
		  model.addRow(new Object[] {serialNo, met.getRuntime(),met.getMethodsName(), met.getClass(), met.isStatic(), met.getStartTime(), met.getEndTime(), met.getParameters(), met.getReturnType(), "Click to see stackTrace"}); 
		  serialNo++;
	  }
	  table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
		  @Override
          public Component getTableCellRendererComponent(JTable table,
                  Object value, boolean isSelected, boolean hasFocus, int row, int col) {
              super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
              Double status = (Double) table.getModel().getValueAt(row, 1);
              if (status == -1) {
                  setBackground(Color.RED);
                  setForeground(Color.WHITE);
              } else {
                  setBackground(table.getBackground());
                  setForeground(table.getForeground());
              }
			return this;	 
          }   
      });
	  table.setFont(new Font("Serif", Font.PLAIN, 20));
	  table.setRowHeight(40);
	  table.getColumnModel().getColumn(9).setCellRenderer(new ButtonRenderer());
	  
	  
	  //details of any particular method on click
	  table.addMouseListener(new java.awt.event.MouseAdapter() {
		    @Override
		    public void mouseClicked(java.awt.event.MouseEvent evt) {
		        int row = table.rowAtPoint(evt.getPoint());
		        int col = table.columnAtPoint(evt.getPoint());
		        if (col==9) {
		        	LogTraceMethod m = p.getMethodById(row);
		        	JDialog dialog = new JDialog();
		        	dialog.setSize(500, 1000);
		        	dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		        	try{
			        	List<String> methodStackList = new ArrayList<String>(m.getMethodStack());
			        	dialog.setTitle(m.getMethodsName()+" Stack Trace");  						//set dialog title
			            DefaultTableModel model = new DefaultTableModel();
			        	JTable t = new JTable(model);
			      	    model.addColumn("<html><font size=5 color=red>"+"StackTrace"+"</font></html>");
			      	    for(String method: methodStackList){
			    		  model.addRow(new Object[] {method});
			    	    }
			      	  dialog.add(t);
			      	dialog.setVisible(true);
		        	}
		        	catch(NullPointerException ex){
		        		//d.add(new JLabel("StackTrace not available for this method in the log file"));
		        		dialog.setVisible(true);
		        	}
		        }
		    }
		});
}	
	public JTable getTable(){
		return table;
	}
}
