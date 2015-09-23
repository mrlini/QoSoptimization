package org.utcluj.util.dateReale;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.utcluj.util.BaseActivityXmlModel;
import org.utcluj.util.MacroActivityCollection;
import org.utcluj.util.opencsv.CSVReader;

import com.ec.util.MersenneTwisterFast;
import com.thoughtworks.xstream.XStream;

/**
 * Creez MacroActivitatile reale... bag picioru' in ele dependente #$%^&*
 * 
 * @author mihai
 * 
 */
public class MacroActivitiesRealDataMihai {

	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		String adrDateReale = "src/org/utcluj/util/dateReale/dateRealeQoS.csv";
		String numeFisierDateReale = "MacroActivitiesReal/MacroActivitiesReal";
		String adrDateRealeNormalizate = "src/org/utcluj/util/dateReale/dateRealePrelucrateSIsiNormate.csv";
		String numeFisierDateRealeNormate = "MacroActivitiesRealNormalized/MacroActivitiesRealNormalized";

//		createFiles(adrDateReale, numeFisierDateReale);
		createFiles(adrDateRealeNormalizate, numeFisierDateRealeNormate);
	}

	@SuppressWarnings("resource")
	private static void createFiles(String adrDate, String numeFisierDate)
			throws FileNotFoundException, IOException {

		List<String[]> date = new CSVReader(new FileReader(adrDate)).readAll();

		for (int n = 5; n < 105; n = n + 5) {
			// n - nr activitatilor concrete/MacroActivitate

			// numele MacroActivitatilor
			String[] numeMA = { "FindNearestAirportService",
					"ProposeMedicalFlightService",
					"CreateMedicalFlightAccountService",
					"BookMedicalFlightService" };

			MersenneTwisterFast random = new MersenneTwisterFast();
			MacroActivityCollection m = new MacroActivityCollection();

			for (int i = 0; i < numeMA.length; i++) {
				for (int j = 0; j < n; j++) {
					int indexDate = random.nextInt(date.size());
					// cost
					double c = 0;
					// response time
					double t = Double.parseDouble(date.get(indexDate)[0]);
					// rating
					double rt = 0;
					// availability
					double a = Double.parseDouble(date.get(indexDate)[1]);
					// throughput
					double thr = Double.parseDouble(date.get(indexDate)[2]);
					// successability
					double s = Double.parseDouble(date.get(indexDate)[3]);
					// reliability
					double rel = Double.parseDouble(date.get(indexDate)[4]);
					
					double latency = Double.parseDouble(date.get(indexDate)[7]);

					m.add(new BaseActivityXmlModel(numeMA[i], numeMA[i] + j,
							"", c, rt, t, a, thr, s, rel, latency));
				}// j
			}// i
			String headderXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
			saveStringToFile(numeFisierDate + n + ".xml", headderXML
					+ convertToXML(m));
		}// n
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
