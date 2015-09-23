package org.utcluj.util.histogram;

import java.awt.Color;
import java.util.List;

import jhplot.H1D;
import jhplot.HPlot;

public class HistogramPlotter {

	private static HistogramPlotter instance = null;
	List vlist = null;

	private HistogramPlotter() {
	}

	public static HistogramPlotter getInstance() {
		if (instance == null)
			instance = new HistogramPlotter();
		return instance;
	}

	public void drawHistogram(List vlist) {
		this.vlist = vlist;
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private void createAndShowGUI() {
		HPlot c1 = new HPlot("Canvas", 600, 400, 1, 1);
		c1.setGTitle("Fitness histogram ");
		c1.visible(true);
		c1.setAutoRange();
		H1D h1 = new H1D("Simple1", 600, 0, 50);
		// fill with data
		for (int i = 0; i < vlist.size(); i++) {
			double val = (Double) vlist.get(i);
			h1.fill(val);
		}
		c1.draw(h1);
		h1.setColor(Color.blue);
		h1.setPenWidthErr(2);
		c1.setNameX("Fitness values");
		c1.setNameY("No. of Individuals");
		c1.setName("Histogram");
		c1.drawStatBox(h1);
		c1.update();
	}
}