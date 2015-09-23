package org.utcluj.util.plot;

import javax.swing.JFrame;

import org.freehep.j3d.plot.SurfacePlot;

public class Plot3DSurface {

	private static final long serialVersionUID = 1L;
	private static Plot3DSurface instance = null;
	double[][] zArr = null;

	private Plot3DSurface() {
	}

	public static Plot3DSurface getInstace() {
		if (instance == null)
			instance = new Plot3DSurface();
		return instance;
	}

	public void drawLandscape(double[][] zArr) {
		this.zArr = zArr;
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private void createAndShowGUI() {
		// System.out.println("Surface creation...");
		SurfacePlot surf = new SurfacePlot();
		// System.out.println("Add data...");
		surf.setData(new FitnessBinned2DData(zArr));
		// surf.setXAxisLabel("S1 index");
		// surf.setYAxisLabel("S2 index");
		// surf.setZAxisLabel("Fitness");
		// System.out.println("Create frame");
		JFrame frame = new JFrame("3D Landscape");
		// System.out.println("Set up frame");
		frame.add(surf);
		frame.setSize(500, 500);
		frame.setLocation(100, 100);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
