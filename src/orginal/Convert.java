package orginal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import randy.owl.types.OWLClass;
import randy.owl.types.OWLInst;
import randy.owl.types.OWLSubClassOf;
import randy.owl.types.Ontology;
import randy.owl.types.RDF;
import randy.owl.types.RDFType;

public class Convert {

	// Constants with of order of QoS attributes
	public static final int TIME = 0;
	public static final int COST = 1;
	public static final int AVAILABILITY = 2;
	public static final int RELIABILITY = 3;

	public static Set<String> taskInput;
	public static Set<String> taskOutput;

	public Map<String, Node> serviceMap = new HashMap<String, Node>();
	public Map<String, TaxonomyNode> taxonomyMap = new HashMap<String, TaxonomyNode>();

	public static void main(String[] args) {
		Convert cvt = new Convert();
		cvt.parseWSCServiceFile("./Testset01/services-output.xml");
		cvt.parseWSCTaskFile("./Testset01/problem.xml");
		cvt.parseWSCTaxonomyFile("./Testset01/taxonomy.xml");
//		cvt.parseWSCTaxonomyFile("./01/taxonomy.xml");
	}

	public static Set<String> getTaskInput() {
		return taskInput;
	}

	public static void setTaskInput(Set<String> taskInput) {
		Convert.taskInput = taskInput;
	}

	public static Set<String> getTaskOutput() {
		return taskOutput;
	}

	public static void setTaskOutput(Set<String> taskOutput) {
		Convert.taskOutput = taskOutput;
	}

	public Map<String, Node> getServiceMap() {
		return serviceMap;
	}

	public void setServiceMap(Map<String, Node> serviceMap) {
		this.serviceMap = serviceMap;
	}

	public Map<String, TaxonomyNode> getTaxonomyMap() {
		return taxonomyMap;
	}

	public void setTaxonomyMap(Map<String, TaxonomyNode> taxonomyMap) {
		this.taxonomyMap = taxonomyMap;
	}

	/**
	 * Parses the WSC task file with the given name, extracting input and output
	 * values to be used as the composition task.
	 *
	 * @param fileName
	 */
	private void parseWSCTaskFile(String fileName) {
		try {
			File fXmlFile = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			org.w3c.dom.Node provided = doc.getElementsByTagName("provided").item(0);
			NodeList providedList = ((Element) provided).getElementsByTagName("instance");
			taskInput = new HashSet<String>();
			for (int i = 0; i < providedList.getLength(); i++) {
				org.w3c.dom.Node item = providedList.item(i);
				Element e = (Element) item;
				taskInput.add(e.getAttribute("name"));
			}

			org.w3c.dom.Node wanted = doc.getElementsByTagName("wanted").item(0);
			NodeList wantedList = ((Element) wanted).getElementsByTagName("instance");
			taskOutput = new HashSet<String>();
			for (int i = 0; i < wantedList.getLength(); i++) {
				org.w3c.dom.Node item = wantedList.item(i);
				Element e = (Element) item;
				taskOutput.add(e.getAttribute("name"));
			}
		} catch (ParserConfigurationException e) {
			System.out.println("Task file parsing failed...");
			e.printStackTrace();
		} catch (SAXException e) {
			System.out.println("Task file parsing failed...");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Task file parsing failed...");
			e.printStackTrace();
		}
	}

	/**
	 * Parses the WSC Web service file with the given name, creating Web
	 * services based on this information and saving them to the service map.
	 *
	 * @param fileName
	 */
	private void parseWSCServiceFile(String fileName) {
		Set<String> inputs = new HashSet<String>();
		Set<String> outputs = new HashSet<String>();
		double[] qos = new double[4];

		try {
			File fXmlFile = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			NodeList nList = doc.getElementsByTagName("service");

			for (int i = 0; i < nList.getLength(); i++) {
				org.w3c.dom.Node nNode = nList.item(i);
				Element eElement = (Element) nNode;
				// service name, for example serv904934656
				String name = eElement.getAttribute("name");

				qos[TIME] = Double.valueOf(eElement.getAttribute("Res"));
				qos[COST] = Double.valueOf(eElement.getAttribute("Pri"));
				qos[AVAILABILITY] = Double.valueOf(eElement.getAttribute("Ava"));
				qos[RELIABILITY] = Double.valueOf(eElement.getAttribute("Rel"));

				// Get inputs, instance name, for example inst995667695
				org.w3c.dom.Node inputNode = eElement.getElementsByTagName("inputs").item(0);
				NodeList inputNodes = ((Element) inputNode).getElementsByTagName("instance");
				for (int j = 0; j < inputNodes.getLength(); j++) {
					org.w3c.dom.Node in = inputNodes.item(j);
					Element e = (Element) in;
					inputs.add(e.getAttribute("name"));
				}

				// Get outputs instance name, for example inst1348768777
				org.w3c.dom.Node outputNode = eElement.getElementsByTagName("outputs").item(0);
				NodeList outputNodes = ((Element) outputNode).getElementsByTagName("instance");
				for (int j = 0; j < outputNodes.getLength(); j++) {
					org.w3c.dom.Node out = outputNodes.item(j);
					Element e = (Element) out;
					outputs.add(e.getAttribute("name"));
				}

				Node ws = new Node(name, qos, inputs, outputs);
				serviceMap.put(name, ws);
				inputs = new HashSet<String>();
				outputs = new HashSet<String>();
				qos = new double[4];
			}
		} catch (IOException ioe) {
			System.out.println("Service file parsing failed...");
		} catch (ParserConfigurationException e) {
			System.out.println("Service file parsing failed...");
		} catch (SAXException e) {
			System.out.println("Service file parsing failed...");
		}
	}

	/**
	 * Parses the WSC taxonomy file with the given name, building a tree-like
	 * structure.
	 *
	 * @param fileName
	 */
	public void parseWSCTaxonomyFile(String fileName) {
		try {
			File fXmlFile = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			NodeList taxonomyRoots = doc.getChildNodes();

			processTaxonomyChildren(null, taxonomyRoots);

			System.out.println(taxonomyMap.size());

			// generate a new Ontology files
			createXML(taxonomyMap);

		}

		catch (ParserConfigurationException e) {
			System.err.println("Taxonomy file parsing failed...");
		} catch (SAXException e) {
			System.err.println("Taxonomy file parsing failed...");
		} catch (IOException e) {
			System.err.println("Taxonomy file parsing failed...");
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void processTaxonomyChildren(TaxonomyNode parent, NodeList nodes) {
		if (nodes != null && nodes.getLength() != 0) {
			for (int i = 0; i < nodes.getLength(); i++) {
				org.w3c.dom.Node ch = nodes.item(i);

				if (!(ch instanceof Text)) {
					Element currNode = (Element) nodes.item(i);
					String value = currNode.getAttribute("name");
					TaxonomyNode taxNode = taxonomyMap.get(value);
					if (taxNode == null) {
						taxNode = new TaxonomyNode(value);
						taxonomyMap.put(value, taxNode);
					}
					if (parent != null) {
						taxNode.parents.add(parent);
						parent.children.add(taxNode);
					}

					NodeList children = currNode.getChildNodes();
					processTaxonomyChildren(taxNode, children);
				}
			}
		}
	}

	public void createXML(Map<String, TaxonomyNode> taxonomyMap) throws JAXBException {

		System.out.println(taxonomyMap.size());

		RDF rdf = new RDF();
		Ontology ontology = new Ontology();
		List<OWLClass> owlClassList = new ArrayList<OWLClass>();
		List<OWLInst> owlInstList = new ArrayList<OWLInst>();

		ontology.setAbout("");
		rdf.setOntology(ontology);

		for (String key : taxonomyMap.keySet()) {
			if (key.contains("con")) {
				OWLClass owlClass = new OWLClass();
				OWLSubClassOf owlSubClassOf = new OWLSubClassOf();

				owlClass.setID(key);
				String resource = taxonomyMap.get(key).parents.get(0).getValue();
				owlSubClassOf.setResource(resource);
				owlClass.setSubClassOf(owlSubClassOf);

				owlClassList.add(owlClass);

			}
			if (key.contains("inst")) {
				OWLInst owlInst = new OWLInst();
				owlInst.setID(key);
				String rdfTypeStr = taxonomyMap.get(key).parents.get(0).getValue();
				RDFType rdftype = new RDFType();
				rdftype.setResource(rdfTypeStr);
				owlInst.setRdfType(rdftype);

				owlInstList.add(owlInst);
			}

		}
		System.out.println("No.Concept: "+owlClassList.size());

		rdf.setOwlClassList(owlClassList);
		rdf.setOwlInstList(owlInstList);

		File file = new File("CovertTestSet01/taxonomy.owl");// ./Testset01/services-output.xml
		JAXBContext jaxbContext = JAXBContext.newInstance(RDF.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		jaxbMarshaller.marshal(rdf, file);
//		jaxbMarshaller.marshal(rdf, System.out);

	}

}