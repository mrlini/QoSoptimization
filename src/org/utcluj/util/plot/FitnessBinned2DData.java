package org.utcluj.util.plot;

import org.freehep.j3d.plot.*;
import java.io.*;

/**
 * A trivial implementation of Binned2DData for test purposes
 * 
 * @author Joy Kyriakopulos (joyk@fnal.gov)
 * @version $Id: FitnessBinned2DData.java,v 1.1 2011/02/24 13:33:23 my_bluey Exp $
 */

public class FitnessBinned2DData implements Binned2DData {

	private int xBins;
	private int yBins;
	private Rainbow rainbow = new Rainbow();
	private float[][] data;

	public FitnessBinned2DData(double[][] ddata) {
		super();
		this.xBins = ddata.length;
		this.yBins = ddata[0].length;
		data = new float[xBins][yBins];
		// normalize data
		double maxVal = ddata[0][0];
		for (int i = 0; i < xBins; i++) {
			for (int j = 0; j < yBins; j++) {
				if (ddata[i][j] > maxVal)
					maxVal = ddata[i][j];
			}
		}
		for (int i = 0; i < xBins; i++) {
			for (int j = 0; j < yBins; j++) {
				double normVal = ddata[i][j] / maxVal;
				data[i][j] = (float) normVal;
			}
		}
	}

	public int xBins() {
		return xBins;
	}

	public int yBins() {
		return yBins;
	}

	public float xMin() {
		return 0f;
	}

	public float xMax() {
		return 1f;
	}

	public float yMin() {
		return 0f;
	}

	public float yMax() {
		return 1f;
	}

	public float zMin() {
		return 0f;
	}

	public float zMax() {
		return 1f;
	}

	public float zAt(int xIndex, int yIndex) {
		return (float) data[xIndex][yIndex];
	}

	public javax.vecmath.Color3b colorAt(int xIndex, int yIndex) {
		return rainbow.colorFor(zAt(xIndex, yIndex));
	}
}
