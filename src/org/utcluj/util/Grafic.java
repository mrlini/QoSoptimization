package org.utcluj.util;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;

public class Grafic extends ApplicationFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7859159502588628692L;

	public Grafic(String title, XYDataset date) {
		super(title);
		JFreeChart chart = CreareGrafic(date);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
	}
	
	@SuppressWarnings("deprecation")
	private static JFreeChart CreareGrafic(XYDataset date) {
		String x = "nr servicii concrete", y = "pasi";
		// aici creez graficul
		JFreeChart chart = ChartFactory.createXYLineChart(null, // titlul
				x, // numele abscisei
				y, // numele ordonatei
				date, // datele
				PlotOrientation.VERTICAL, true, // legenda
				true, // tooltips
				false // urls
				);

		// parametri optionali
		chart.setBackgroundPaint(Color.white);
		// preiau o referinta care grafic
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();		
		renderer.setShapesVisible(true);		
		renderer.setShapesFilled(true);
				
		//ordonata - setez limitele, valori intregi pt coordonate
		NumberAxis ordonata = (NumberAxis) plot.getRangeAxis();
		ordonata.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		ordonata.setAutoRange(true);

		//abscisa
		NumberAxis abscisa = (NumberAxis) plot.getDomainAxis();
		abscisa.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); //abscisa afiseaza doar nr intregi
		abscisa.setRange(0, 110);
		
		return chart;
	}
	
	public static XYDataset DateFinale(int[] x, int[][] y ) {
		XYSeries[] c = new XYSeries[2];
		c[0] = new XYSeries("exhaustiv");
		c[1] = new XYSeries("hill climbing");
		
		for(int i=0;i<x.length;i++){
			c[0].add(x[i], y[0][i]);
			c[1].add(x[i], y[1][i]);
		}
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(c[0]);
		dataset.addSeries(c[1]);
		return dataset;
	}
}
