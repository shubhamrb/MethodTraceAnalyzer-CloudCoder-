package graphicalUserInterface;

import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import dataparser.DataParser;
import dataparser.LogTraceMethod;

// This class creates the chart to be shown on the GraphViewerFrame
public class LogChartCreator {
	private JFreeChart chart;
	public LogChartCreator(DataParser p){
	  final DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
	  List<LogTraceMethod> methods = p.sortByRuntime();
	  if(methods.size()>=30){
		  methods = methods.subList(0, 30);
	  }
	  for(LogTraceMethod met: methods){
		  dataset.addValue( met.getRuntime() , met.getMethodsName()+" ("+met.getId()+")" , "" );        
	  }
	  chart = ChartFactory.createBarChart(
			   "Method Runtime Comparision", // Title
			   "Methods", // x-axis Label
			   "Run Time", // y-axis Label
			   dataset, // Dataset
			   PlotOrientation.VERTICAL, // Plot Orientation
			   true, // Show Legend
			   true, // Use tooltips
			   false // Configure chart to generate URLs?
			);
	}
		
	public JFreeChart getChart(){
		return chart;
	}
}
