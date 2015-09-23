package org.utcluj.util;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.utcluj.util.opencsv.CSVParser;
import org.utcluj.util.opencsv.CSVReader;

import com.thoughtworks.xstream.XStream;

import ec.util.MersenneTwisterFast;

/**
 * Generarea aleatoare de servicii concrete si scrierea acestora intr-un fisier
 * XML
 */
public class BpelActivitiesRealDataGenerator {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		List<String[]> realDataList = new CSVReader(new FileReader("avail_rt.csv")).readAll();
				
		int limit = 1;
		List fitnessList = new ArrayList();

		String nume = "BpelActivities/BpelActivities"; // numele fisierului
		// xml
		// int n = 5; // nr activitatilor concrete/MacroActivitate
		for (int n = 5; n < 100; n = n + 5) {
			// numele MacroActivitatilor
			//String[] numeMA = { "MedicalServiceA", "MedicalServiceB", "MedicalServiceC", "MedicalServiceD", "MedicalServiceE"};
			List<String> numeMA = new ArrayList<String>();
			
			for (int i = 0; i < 5; i ++) 
				for (int j = 0; j < 26; j ++) {
				
					numeMA.add("WebService_" + (char)(65 + i) + "" + (char)(65 + j));
				}
			
			// Random r = new Random(); // java.util.Random not very good
			MersenneTwisterFast r = new MersenneTwisterFast();

			r.setSeed(System.nanoTime());
			MacroActivityCollection m = new MacroActivityCollection();
			double currentFitness = 0;

			for (int i = 0; i < numeMA.size(); i++) {
				for (int j = 0; j < n; j++) {

					int realDataIndex = r.nextInt(realDataList.size());
					
					
					// cost
					double c = r.nextGaussian() + 5;
					if (c < 0)
						c = 0;
					if (c > 10)
						c = 10;
					// normalize cost
					c = (double) c / 10.0;

					// time
					double t;
					t = Double.parseDouble(realDataList.get(realDataIndex)[1]);/*r.nextGaussian() + 5;
					if (t < 0.1)
						t = 0.1;
					if (t > 10)
						t = 10;
					// normalize time
					t = (double) t / 10.0;*/
					
					// rating
					double rt;
					rt = Double.parseDouble(realDataList.get(realDataIndex)[0]);
					/*r.nextGaussian() + 5;
					if (rt < 0.1)
						rt = 0.1;
					if (rt > 10)
						rt = 10;
					// normalize rating
					rt = (double) rt / 10.0;*/
					
					
					
					m.add(new BaseActivityXmlModel(numeMA.get(i), numeMA.get(i) + "_" + (j + 1), "", c, rt, t, 0, 0, 0, 0, 0));
				}
			}
			
			//m.sort();
			
			String headderXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
			saveStringToFile(nume + n + ".xml", headderXML + convertToXML(m));
		}
		
	}

	/**
	 * Salvarea unui string intr-un fisier
	 * 
	 * @param fileName
	 *            - numele fisierului
	 * @param saveString
	 *            - textul care va fi scris in fisier
	 */
	public static void saveStringToFile(String fileName, String saveString) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(fileName));
			try {
				bw.write(saveString);
			} finally {
				bw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Converteste un obiect de tipul McroActivities intr-un string in format
	 * XML
	 * 
	 * @param ma
	 *            - obiectul McroActivities
	 * @return
	 */
	public static String convertToXML(MacroActivityCollection ma) {
		XStream xs = new XStream();
		xs.alias("MacroActivities", MacroActivityCollection.class);
		xs.alias("BaseActivity", BaseActivityXmlModel.class);
		xs.addImplicitCollection(MacroActivityCollection.class, "activitati");
		return xs.toXML(ma);
	}
}
