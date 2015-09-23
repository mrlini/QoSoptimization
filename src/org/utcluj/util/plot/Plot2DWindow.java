package org.utcluj.util.plot;

import jahuwaldt.plot.CircleSymbol;
import jahuwaldt.plot.ContourPlot;
import jahuwaldt.plot.Plot2D;
import jahuwaldt.plot.PlotDatum;
import jahuwaldt.plot.PlotPanel;
import jahuwaldt.plot.PlotRun;
import jahuwaldt.plot.PlotSymbol;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Plot2DWindow extends JFrame {

	// -------------------------------------------------------------------------
	/**
	 * Creates a plot window that displays the specified plot panel.
	 * 
	 * @param name
	 *            The title to show in the window's title bar.
	 * @param plot
	 *            The plot panel to be displayed in this window.
	 **/
	public Plot2DWindow(String name, PlotPanel plot) {
		super(name);
		getContentPane().add(plot);
	}

	// -------------------------------------------------------------------------

	/**
	 * A simple method to test this PlotWindow by creating a couple of Plot2D
	 * plots and putting them in windows.
	 **/
	public static void main(String[] args) {
		// Create a more complicated 2D contour plot of 3D data.
		// zArr is the Z component of the data points.
		double[][] zArr = { { 0.5, 1.1, 1.5, 1, 2.0, 3, 3, 2, 1, 0.1 },
				{ 1.0, 1.5, 3.0, 5, 6.0, 2, 1, 1.2, 1, 4 },
				{ 0.9, 2.0, 2.1, 3, 6.2, 7, 3, 2, 1, 1.4 },
				{ 1.0, 1.5, 3.0, 4, 6.0, 5, 2, 1.5, 1, 2 },
				{ 0.8, 2.0, 3.0, 3, 4.0, 4, 3, 2.4, 2, 3 },
				{ 0.6, 1.1, 1.5, 1, 4.0, 3.5, 3, 2, 3, 4 },
				{ 1.0, 1.5, 3.0, 5, 6.0, 2, 1, 1.2, 2.7, 4 },
				{ 0.8, 2.0, 3.0, 3, 5.5, 6, 3, 2, 1, 1.4 },
				{ 1.0, 1.5, 3.0, 4, 6.0, 5, 2, 1, 0.5, 0.2 } };

		// xArr2 & yArr2 are the X & Y components of the data points (evenly
		// gridded in this case).
		double[][] xArr2, yArr2;

		// Create a simple grid of X & Y to go with our Z data.
		int ni = zArr.length;
		int nj = zArr[0].length;
		xArr2 = new double[ni][nj];
		yArr2 = new double[ni][nj];
		for (int i = 0; i < ni; ++i) {
			for (int j = 0; j < nj; ++j) {
				xArr2[i][j] = j;
				yArr2[i][j] = i;
			}
		}
		Plot2D aPlot = new ContourPlot(xArr2, yArr2, zArr, 12, false,
				"Test Contour Plot", "X Axis", "Y Axis", null, null);

		// Colorize the contours.
		((ContourPlot) aPlot).colorizeContours(Color.blue, Color.red);

		// Create a run that contains the original XY data points we just put
		// contours through.
		// We'll plot it with symbols so we can see the location of the original
		// data points.
		PlotSymbol symbol = new CircleSymbol();
		symbol.setBorderColor(Color.gray);
		symbol.setSize(4);
		PlotRun run = new PlotRun();
		for (int i = 0; i < ni; ++i) {
			for (int j = 0; j < nj; ++j) {
				run.add(new PlotDatum(xArr2[i][j], yArr2[i][j], false, symbol));
			}
		}

		// Add this new run of points to the plot.
		aPlot.getRuns().add(run);

		// Now proceed with creating the plot window.
		PlotPanel panel = new PlotPanel(aPlot);
		panel.setBackground(Color.white);
		JFrame window = new Plot2DWindow("ContourPlot Plot Window", panel);
		window.setSize(500, 300);
		window.setLocation(100, 100);
		window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		window.setVisible(true);

	}

	public static void drawLandscape(double[][] zArr) {
		// xArr2 & yArr2 are the X & Y components of the data points (evenly
		// gridded in this case).
		double[][] xArr2, yArr2;
		// Create a simple grid of X & Y to go with our Z data.
		int ni = zArr.length;
		int nj = zArr[0].length;
		xArr2 = new double[ni][nj];
		yArr2 = new double[ni][nj];
		for (int i = 0; i < ni; ++i) {
			for (int j = 0; j < nj; ++j) {
				xArr2[i][j] = j;
				yArr2[i][j] = i;
			}
		}
		Plot2D aPlot = new ContourPlot(xArr2, yArr2, zArr, 12, false,
				"2D Fitness landscape", "S1 index", "S2 index", null, null);
		// Colorize the contours.
		((ContourPlot) aPlot).colorizeContours(Color.blue, Color.red);
		// Create a run that contains the original XY data points we just put
		// contours through.
		// We'll plot it with symbols so we can see the location of the original
		// data points.
		PlotSymbol symbol = new CircleSymbol();
		symbol.setBorderColor(Color.gray);
		symbol.setSize(4);
		PlotRun run = new PlotRun();
		for (int i = 0; i < ni; ++i) {
			for (int j = 0; j < nj; ++j) {
				run.add(new PlotDatum(xArr2[i][j], yArr2[i][j], false, symbol));
			}
		}
		// Add this new run of points to the plot.
		aPlot.getRuns().add(run);
		// Now proceed with creating the plot window.
		PlotPanel panel = new PlotPanel(aPlot);
		panel.setBackground(Color.white);
		JFrame window = new Plot2DWindow("ContourPlot Plot Window", panel);
		window.setSize(500, 300);
		window.setLocation(100, 100);
		window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		window.setVisible(true);
	}
}
