package org.utcluj.moo.optimizareServiciiReale;

public class Params {

	private static Params instance = null;

	/*
	 * folosesc acesti vectori pentru a memora, la citirea planului, valorile
	 * minime si maxime pentru atributele QoS: index 0 - throughput, index 1 -
	 * availanility si index 2 - response time
	 */
	private static double[] minAttr = new double[3];
	private static double[] maxAttr = new double[3];
	
	boolean pondereaza = true;

	private Params() {
	}

	public static Params getInstance() {
		if (instance == null) {
			instance = new Params();
		}
		return instance;
	}

	public void resetMinMax() {
		for (int i = 0; i < minAttr.length; i++) {
			minAttr[i] = Double.POSITIVE_INFINITY;
			maxAttr[i] = Double.NEGATIVE_INFINITY;
		}
	}

	public static double[] getMinAttr() {
		return minAttr;
	}

	/**
	 * 
	 * @param index
	 * @return
	 */
	public static double getSpMinAttr(int index) {
		return minAttr[index];
	}

	public static void setMinAttr(double[] minAttr) {
		Params.minAttr = minAttr;
	}

	/**
	 * 
	 * @param index
	 * @param spMinAttr
	 */
	public static void setSpMinAttr(int index, double spMinAttr) {
		Params.minAttr[index] = spMinAttr;
	}

	public static double[] getMaxAttr() {
		return maxAttr;
	}

	/**
	 * 
	 * @param index
	 * @return
	 */
	public static double getSpMaxAttr(int index) {
		return maxAttr[index];
	}

	public static void setMaxAttr(double[] maxAttr) {
		Params.maxAttr = maxAttr;
	}

	/**
	 * 
	 * @param index
	 * @param spMaxAttr
	 */
	public static void setSpMaxAttr(int index, double spMaxAttr) {
		Params.maxAttr[index] = spMaxAttr;
	}

	public boolean isPondereaza() {
		return pondereaza;
	}

	public void setPondereaza(boolean pondereaza) {
		this.pondereaza = pondereaza;
	}

}
