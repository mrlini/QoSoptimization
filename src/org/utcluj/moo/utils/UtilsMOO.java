package org.utcluj.moo.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

import cern.jet.math.Functions;

public class UtilsMOO {
	
	public static final String MET_NSGA2 = "nsga2";
	public static final String MET_SPEA2 = "spea2";
	public static final String MET_GDE3 = "gde3";
	public static final String MET_MOEAD = "moead";
	public static final String MET_POSDE = "posde";
	public static final String MET_eMyDE = "eMyDE";
	public static final String MET_DEMO = "demo";
	public static final String MET_NSGA2_Lorenz = "nsga2Lorenz";
	public static final String MET_GDE3_Lorenz = "gde3Lorenz";
	public static final String MET_DE_Lorenz = "deLorenz";

	public final static String NROBJ = "nrObj";
	public final static String TASKCOUNT = "tasks";
	public final static String ALTERNATIVE = "alternatives";
	public final static String ACTIVITIES = "activities";
	public final static String EXTERNAL_POP = "dimEP";
	public final static String NR_VECINI = "nrVeciniMOEAD";

	private static UtilsMOO instance = null;

	private UtilsMOO() {
	}

	public static UtilsMOO getInstance() {
		if (instance == null)
			instance = new UtilsMOO();
		return instance;
	}

	/**
	 * Metoda calculeaza distanta Euclidiana intre 2 puncte date ca si un vector
	 * de double
	 * 
	 * @param a
	 *            - punctul a
	 * @param b
	 *            - punctul b
	 * @return distanta euclidiana intre puncte
	 **/
	public double distanta(double[] a, double[] b) {
		double dist = 0.0;
		for (int i = 0; i < a.length; i++)
			dist += Functions.pow.apply(a[i] - b[i], 2.0);
		// dist += Math.pow(a[i] - b[i], 2.0);
		// return Math.sqrt(dist);
		return Functions.sqrt.apply(dist);
	}

	/**
	 * Metoda preia distanta intre un pct si cel mai apropiat pct dintr-un front
	 * dat. Frontul este sub forma <code>double [][]</code>
	 * 
	 * @param point
	 *            punctul
	 * @param front
	 *            frontul ce contine celelalte pct
	 * @return dist minima intre front si punctul dat
	 **/
	public double distMinPctFront(double[] point, double[][] front) {
		double mindist = distanta(point, front[0]);

		for (int i = 1; i < front.length; i++) {
			double aux = distanta(point, front[i]);
			if (aux < mindist) {
				mindist = aux;
			}
		}
		return mindist;
	}

	/**
	 * Iau distanta intre un pct si cel mai apropiat pct dintr-un front dat,
	 * aceasta dist > 0
	 * 
	 * @param point
	 *            punctul de referinta
	 * @param front
	 *            frontul ce contine pct
	 * @return dist minima (mai mare ca 0) intre front si punctul dat the front
	 */
	public double distCatreCelMaiApropiatPct(double[] point, double[][] front) {
		double minDistance = Double.MAX_VALUE;

		for (int i = 0; i < front.length; i++) {
			double aux = distanta(point, front[i]);
			if ((aux < minDistance) && (aux > 0.0)) {
				minDistance = aux;
			}
		}
		return minDistance;
	}
	
	/**
	 * Metoda citeste un front (matrice) salvat pe disc, pe coloane sunt salvate obiectivele
	 * 
	 * <p>
	 * Sa am grija ca <b>fisierului citit trebuie sa se termine cu enter</b> (ultima linie sa 
	 * fie goala) altfel la citire pierd o linie
	 * </p>
	 * 
	 * @param adresa
	 * @param nrOb
	 * @return
	 */
	public double[][] citireFrontTeoretic(String adresa, int nrOb) {
		List<double[]> front = new ArrayList<double[]>();
		
		int j = 0;
		StreamTokenizer parser;

		try {
			parser = new StreamTokenizer(new FileReader(adresa));
			parser.eolIsSignificant(true);
			double[] val = new double[nrOb];
			while (parser.nextToken() != StreamTokenizer.TT_EOF) {
				if (parser.ttype == StreamTokenizer.TT_NUMBER) {
					val[j] = parser.nval;
					j++;
				} else if (parser.ttype == StreamTokenizer.TT_EOL) {
					j = 0;
					front.add(val);
					val = new double[nrOb];
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//copiez intro matrice
		int dimFront = front.size();
		double[][] frontT = new double[dimFront][nrOb];
		for(int i=0;i<dimFront;i++){
			frontT[i] = front.get(i);
		}
		
		return frontT;
	}
	
	/**
	 * Scrierea unei matrici pe disc
	 * 
	 * @param adresa - adresa la care scriu
	 * @param adaugaLaFisier - true=>fac append, false=>sterg continutul si scriu doar matricea
	 * @param matrice - matricea care va fi scrisa pe disc
	 */
	public void scrieMatrice(String adresa, boolean adaugaLaFisier, double[][] matrice) {
		File f = new File(adresa);
		String linie = "";
		
		try{
			BufferedWriter b = new BufferedWriter(new FileWriter(f, adaugaLaFisier));
			for(int i=0;i<matrice.length;i++){
				linie = "";
				for(double d: matrice[i])
					linie += d + "\t";
				b.write(linie);
				b.newLine();
			}
			b.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Metoda scrie un sir intr-un fisier, pot seta dimensiunea buffer-ului de scriere
	 * @param adresa
	 * @param adaugaLaFisier
	 * @param bufferSize - cati mega biti sa aiba buffer-ul de scriere
	 * @param stringDeScris
	 */
	public void scrieString(String adresa, boolean adaugaLaFisier, int bufferSize, String stringDeScris){
		int meg = (int) Functions.pow.apply(1024, 2);
		
		try{
			BufferedWriter buf = new BufferedWriter(new FileWriter(adresa, adaugaLaFisier), bufferSize*meg);
			buf.write(stringDeScris);
			buf.newLine();
			buf.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
