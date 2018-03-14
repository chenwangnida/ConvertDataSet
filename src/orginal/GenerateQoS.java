/*##########################################################################
## Format: QWS parameters are separated by commas (first nine)			##
## Format: (1) Response Time						##
## Format: (2) Availability							##
## Format: (3) Throughput							##
## Format: (4) Successability						##
## Format: (5) Reliability							##
## Format: (6) Compliance							##
## Format: (7) Best Practices						##
## Format: (8) Latency							##
## Format: (9) Documentation						##
## Format: (10) Service Name						##
## Format: (11) WSDL Address						##
##########################################################################*/

package orginal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.math3.distribution.EnumeratedRealDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.primitives.Doubles;

public class GenerateQoS {

	List<Double> list4time = new ArrayList<Double>();
	List<Double> list4Reliability = new ArrayList<Double>();
	List<Double> list4Availability = new ArrayList<Double>();
	List<Double> list4Cost = new ArrayList<Double>();

	EnumeratedRealDistribution timeDistribution;
	EnumeratedRealDistribution reliaDistribution;
	EnumeratedRealDistribution availDistribution;
	EnumeratedRealDistribution costDistribution;
	long seed = 1;

	public void parsesQWS() {

		// read QWS

		try {
			List<String> lines4QWS = Files.readAllLines(Paths.get(ConvertWS.qwsFilePath));
			for (String qwsParameter : lines4QWS) {
				if (!qwsParameter.startsWith("#")) {
					String[] parameters = qwsParameter.split(",");
					if (parameters.length == 11) {
						// ## Format: (1) Response Time
						list4time.add(Double.parseDouble(parameters[0]));
						// ## Format: (2) Availability
						list4Availability.add(Double.parseDouble(parameters[1]));
						// ## Format: (5) Reliability
						list4Reliability.add(Double.parseDouble(parameters[4]));
					} else {
						System.err.println("No.parameters is not right! ");
					}
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		timeDistribution = enumrealDistribution(list4time, seed);
		availDistribution = enumrealDistribution(list4Availability, seed);
		reliaDistribution = enumrealDistribution(list4Reliability, seed);

	}

	/**
	 * Parses the WSC Web service file with the given name, creating Web services
	 * based on this information and saving them to the service map.
	 *
	 * @param fileName
	 */
	public void parseWSC() {
		Set<String> inputs = new HashSet<String>();
		Set<String> outputs = new HashSet<String>();
		double[] qos = new double[4];

		try {
			File fXmlFile = new File(ConvertWS.wscFilePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			NodeList nList = doc.getElementsByTagName("service");

			for (int i = 0; i < nList.getLength(); i++) {
				org.w3c.dom.Node nNode = nList.item(i);
				Element eElement = (Element) nNode;
				list4Cost.add(Double.valueOf(eElement.getAttribute("Pri")));
			}

		} catch (IOException ioe) {
			System.out.println("Service file parsing failed...");
		} catch (ParserConfigurationException e) {
			System.out.println("Service file parsing failed...");
		} catch (SAXException e) {
			System.out.println("Service file parsing failed...");
		}

		costDistribution = enumrealDistribution(list4Cost, seed);
		// for (int i = 0; i < 1000; i++) {
		// System.out.println(costDistribution.sample());
		// }
	}

	public EnumeratedRealDistribution enumrealDistribution(List<Double> realNumbers, long seed) {
		double[] realNumbersArray = Doubles.toArray(realNumbers);
		EnumeratedRealDistribution realNumDistr = new EnumeratedRealDistribution(realNumbersArray);
		realNumDistr.reseedRandomGenerator(seed);
		return realNumDistr;
	}

	public NormalDistribution normalDistribution(List<Double> realNumbers) {
		double[] realNumbersArray = Doubles.toArray(realNumbers);
		StatisticalSummary realNumStat = new DescriptiveStatistics(realNumbersArray);

		double mean = realNumStat.getMean();
		double sd = realNumStat.getStandardDeviation();
		NormalDistribution realNumDistr = new NormalDistribution(mean, sd);
		return realNumDistr;
	}
}