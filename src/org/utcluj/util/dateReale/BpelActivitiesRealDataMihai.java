package org.utcluj.util.dateReale;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.utcluj.util.BaseActivityXmlModel;
import org.utcluj.util.MacroActivityCollection;
import org.utcluj.util.opencsv.CSVReader;

import com.thoughtworks.xstream.XStream;

import ec.util.MersenneTwister;

/**
 * Creez activitatile BPEL, parametrii sunt preluati din baza de date reale.
 * Exista si o versiune normata a parametriilor
 * 
 * @author mihai
 * 
 */
public class BpelActivitiesRealDataMihai {

	public static void main(String[] args) throws FileNotFoundException,
			IOException {

		String adrDateReale = "src/org/utcluj/util/dateReale/dateRealeQoS.csv";
		String numeFisierDateReale = "BpelRealActivities/BpelRealActivities";
		String adrDateRealeNormalizate = "src/org/utcluj/util/dateReale/dateRealePrelucrateSIsiNormate.csv";
		String numeFisierDateRealeNormate = "BpelRealActivitiesNormalized/BpelRealActivitiesNormalized";

//		createFiles(adrDateReale, numeFisierDateReale);
		createFiles(adrDateRealeNormalizate, numeFisierDateRealeNormate);
	}

	private static void createFiles(String adrDate, String numeFisierDate)
			throws FileNotFoundException, IOException {
		@SuppressWarnings("resource")
		List<String[]> date = new CSVReader(new FileReader(adrDate)).readAll();

		for (int n = 5; n < 100; n += 5) {
			List<String> numeMA = new ArrayList<String>();

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 26; j++) {
					numeMA.add("WebService_" + (char) (65 + i) + ""
							+ (char) (65 + j));
				}
			}

			MersenneTwister random = new MersenneTwister();
			MacroActivityCollection m = new MacroActivityCollection();

			for (int i = 0; i < numeMA.size(); i++) {
				for (int j = 0; j < n; j++) {
					int indexDate = random.nextInt(date.size());

					// cost
					double c = 0;
					// response time
					double t = Double.parseDouble(date.get(indexDate)[0]);
//					t = t/1000000.0;
					// rating
					double rt = 0;
					// availability
					double a = Double.parseDouble(date.get(indexDate)[1]);
//					a = a/100.0;
					// throughput
					double thr = Double.parseDouble(date.get(indexDate)[2]);
					// successability
					double s = Double.parseDouble(date.get(indexDate)[3]);
//					s = s/100.0;
					// reliability
					double rel = Double.parseDouble(date.get(indexDate)[4]);
//					rel = rel / 100.0;
					double latency = Double.parseDouble(date.get(indexDate)[7]);
//					latency = latency/1000000.0;

					m.add(new BaseActivityXmlModel(numeMA.get(i), numeMA.get(i)
							+ "_" + (j + 1), "", c, rt, t, a, thr, s, rel, latency));
				}
			}
			String headderXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
			saveStringToFile(numeFisierDate + n + ".xml", headderXML
					+ convertToXML(m));
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
