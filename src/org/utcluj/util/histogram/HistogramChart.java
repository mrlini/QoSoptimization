package org.utcluj.util.histogram;

import java.util.List;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

public class HistogramChart {

	private static HistogramChart instance = null;
	JFreeChart chart = null;
	
	private HistogramChart() {
	}

	public static HistogramChart getInstance() {
		if (instance == null)
			instance = new HistogramChart();
		return instance;
	}

	public void drawHistogram(List vlist) {
		double[] value = new double[vlist.size()];
		for (int i = 1; i < vlist.size(); i++) {
			value[i] = (Double) vlist.get(i);
			int number = vlist.size();
			HistogramDataset dataset = new HistogramDataset();
			dataset.setType(HistogramType.RELATIVE_FREQUENCY);
			dataset.addSeries("Histogram", value, number);
			String plotTitle = "Histogram";
			String xaxis = "number";
			String yaxis = "value";
			PlotOrientation orientation = PlotOrientation.VERTICAL;
			boolean show = false;
			boolean toolTips = false;
			boolean urls = false;
			chart = ChartFactory.createHistogram(plotTitle, xaxis, yaxis,
					dataset, orientation, show, toolTips, urls);
		}
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private void createAndShowGUI() {
		ChartPanel panel = new ChartPanel(chart);
		JFrame frame = null;
		frame = new JFrame("Fitness Histogram");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 500, 500);
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}

}