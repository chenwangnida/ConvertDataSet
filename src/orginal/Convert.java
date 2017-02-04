package orginal;

import java.io.File;
import java.io.FileOutputStream;
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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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

	public static Map<String, Node> serviceMap = new HashMap<String, Node>();
	public static Map<String, Node> OWLTCMap = new HashMap<String, Node>();

	public Map<String, TaxonomyNode> taxonomyMap = new HashMap<String, TaxonomyNode>();

	public static void main(String[] args) {
		Convert cvt = new Convert();
		// cvt.parseWSCTaskFile("./Testset01/problem.xml");

		// /ConvertDataSet/owlstc01/owlsTCTaxonomy.xml
		 cvt.parseWSCTaxonomyFile("./owlstc01/owlsTCTaxonomy.xml");
//		cvt.parseOWLServiceFile("./owlstc01/owlsTCServices.xml");
//		cvt.parseWSCServiceFile("./Testset05/services-output.xml");
//		cvt.CreateMECE(serviceMap);

		// cvt.CreateMECE(null);

		// cvt.parseWSCTaxonomyFile("./debugTestDataSet/testTaxonomySet.xml");

		// cvt.parseWSCTaxonomyFile("./01/taxonomy.xml");
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

	private void parseOWLServiceFile(String fileName) {
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

				// qos[TIME] = Double.valueOf(eElement.getAttribute("Res"));
				// qos[COST] = Double.valueOf(eElement.getAttribute("Pri"));
				// qos[AVAILABILITY] =
				// Double.valueOf(eElement.getAttribute("Ava"));
				// qos[RELIABILITY] =
				// Double.valueOf(eElement.getAttribute("Rel"));

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
				OWLTCMap.put(name, ws);
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

	private void CreateMECE(Map<String, Node> serviceMap2) {
		
		List<Node> OWLTCMapList = new ArrayList<Node>();
		OWLTCMapList.addAll(OWLTCMap.values());
		
		List<Node> serviceMapList = new ArrayList<Node>();
		serviceMapList.addAll(serviceMap.values());
		
		
		
		for(int i=0; i<OWLTCMapList.size();i++){		

			double[] qos = new double[4];                                                                     
			qos = serviceMapList.get(i).getQos();		
			OWLTCMapList.get(i).setQos(qos);			
		}
		

		
		
		
		
		Document dom;

		// instance of a DocumentBuilderFactory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			// use factory to get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			// create instance of DOM
			dom = db.newDocument();

			// create the root element
			Element rootEle = dom.createElement("services");

			// create data elements and place them under root
			

			for (Node node : OWLTCMapList) {

				Element e = dom.createElement("service");
				e.setAttribute("name", node.getName());
				e.setAttribute("Res", node.getQos()[TIME] + "");
				e.setAttribute("Ava", node.getQos()[AVAILABILITY] + "");
				e.setAttribute("Rel", node.getQos()[RELIABILITY] + "");
				e.setAttribute("Pri", node.getQos()[COST] + "");

				Element inputs = dom.createElement("inputs");
				e.appendChild(inputs);
				for (String in : node.getInputs()) {
					Element instance = dom.createElement("instance");
					instance.setAttribute("name", in);
					inputs.appendChild(instance);

				}

				Element outputs = dom.createElement("outputs");
				e.appendChild(outputs);
				for (String ou : node.getOutputs()) {

					Element instance1 = dom.createElement("instance");
					instance1.setAttribute("name", ou);

					outputs.appendChild(instance1);

				}

				rootEle.appendChild(e);
			}
			dom.appendChild(rootEle);

			try {
				Transformer tr = TransformerFactory.newInstance().newTransformer();
				tr.setOutputProperty(OutputKeys.INDENT, "yes");
				tr.setOutputProperty(OutputKeys.METHOD, "xml");
				tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				// tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "roles.dtd");
				tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

				// send DOM to file
				tr.transform(new DOMSource(dom),
						new StreamResult(new FileOutputStream("owlstc01/services-output.xml")));

			} catch (TransformerException te) {
				System.out.println(te.getMessage());
			} catch (IOException ioe) {
				System.out.println(ioe.getMessage());
			}
		} catch (ParserConfigurationException pce) {
			System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
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
			// createXML(taxonomyMap);
			createXML4OWLTC(taxonomyMap);

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
			File file = new File("CovertTestSet01/taxonomy.owl");

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

	// create XML for OWLTC
	public void createXML4OWLTC(Map<String, TaxonomyNode> taxonomyMap) throws JAXBException {

		System.out.println(taxonomyMap.size());

		RDF rdf = new RDF();
		Ontology ontology = new Ontology();
		List<OWLClass> owlClassList = new ArrayList<OWLClass>();
		List<OWLInst> owlInstList = new ArrayList<OWLInst>();

		ontology.setAbout("");
		rdf.setOntology(ontology);

		for (String key : taxonomyMap.keySet()) {

			if (!key.equals("")) {
				// if (key.contains("con")) {
				OWLClass owlClass = new OWLClass();
				OWLSubClassOf owlSubClassOf = new OWLSubClassOf();
//				System.out.println(key + ":key");

				owlClass.setID(key);
				String resource = taxonomyMap.get(key).parents.get(0).getValue();
				if (!resource.equals("")) {
					owlSubClassOf.setResource("#" + resource);
					owlClass.setSubClassOf(owlSubClassOf);
				}

				owlClassList.add(owlClass);

				// }
				// if (key.contains("inst")) {
				OWLInst owlInst = new OWLInst();
				owlInst.setID(key);
//				String rdfTypeStr = taxonomyMap.get(key).parents.get(0).getValue();
				String rdfTypeStr = taxonomyMap.get(key).getValue();

				RDFType rdftype = new RDFType();
				rdftype.setResource("#" + rdfTypeStr);
				owlInst.setRdfType(rdftype);

				owlInstList.add(owlInst);
				// }
			}

		}
		System.out.println("No.Concept: " + owlClassList.size());

		rdf.setOwlClassList(owlClassList);
		rdf.setOwlInstList(owlInstList);

		// File file = new File("Testconvertdataset/taxonomy.owl");
		File file = new File("owlstc01/taxonomy.owl");
		JAXBContext jaxbContext = JAXBContext.newInstance(RDF.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		jaxbMarshaller.marshal(rdf, file);
//		jaxbMarshaller.marshal(rdf, System.out);

	}

	// create XML for WSC

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
				if (!resource.equals("")) {
					owlSubClassOf.setResource("#" + resource);
					owlClass.setSubClassOf(owlSubClassOf);
				}

				owlClassList.add(owlClass);

			}
			if (key.contains("inst")) {
				OWLInst owlInst = new OWLInst();
				owlInst.setID(key);
				String rdfTypeStr = taxonomyMap.get(key).parents.get(0).getValue();
				RDFType rdftype = new RDFType();
				rdftype.setResource("#" + rdfTypeStr);
				owlInst.setRdfType(rdftype);

				owlInstList.add(owlInst);
			}

		}
		System.out.println("No.Concept: " + owlClassList.size());

		rdf.setOwlClassList(owlClassList);
		rdf.setOwlInstList(owlInstList);

		// File file = new File("Testconvertdataset/taxonomy.owl");
		File file = new File("WSC09TestSet05/taxonomy.owl");
		JAXBContext jaxbContext = JAXBContext.newInstance(RDF.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		jaxbMarshaller.marshal(rdf, file);
		jaxbMarshaller.marshal(rdf, System.out);

	}

	// public void createDebugXML(Map<String, TaxonomyNode> taxonomyMap) throws
	// JAXBException {
	//
	// System.out.println(taxonomyMap.size());
	//
	// RDF rdf = new RDF();
	// Ontology ontology = new Ontology();
	// List<OWLClass> owlClassList = new ArrayList<OWLClass>();
	// List<OWLInst> owlInstList = new ArrayList<OWLInst>();
	//
	// ontology.setAbout("");
	// rdf.setOntology(ontology);
	//
	// for (String key : taxonomyMap.keySet()) {
	// if (key.contains("@")) {
	// OWLClass owlClass = new OWLClass();
	// OWLSubClassOf owlSubClassOf = new OWLSubClassOf();
	//
	// owlClass.setID(key.substring(1));
	// String resource = taxonomyMap.get(key).parents.get(0).getValue();
	// if (!resource.equals("")) {
	// owlSubClassOf.setResource("#" + resource);
	// owlClass.setSubClassOf(owlSubClassOf);
	// }
	//
	// owlClassList.add(owlClass);
	//
	// } if (key.contains("$")) {
	// OWLInst owlInst = new OWLInst();
	// owlInst.setID(key.substring(1));
	// String rdfTypeStr = taxonomyMap.get(key).parents.get(0).getValue();
	// RDFType rdftype = new RDFType();
	// rdftype.setResource("#" + rdfTypeStr);
	// owlInst.setRdfType(rdftype);
	//
	// owlInstList.add(owlInst);
	// }
	//
	// }
	// System.out.println("No.Concept: " + owlClassList.size());
	//
	// rdf.setOwlClassList(owlClassList);
	// rdf.setOwlInstList(owlInstList);
	//
	// File file = new File("CovertTestSet02/taxonomy.owl");
	//// File file = new File("debugTestDataSet/taxonomySet.owl");
	// JAXBContext jaxbContext = JAXBContext.newInstance(RDF.class);
	// Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	//
	// // output pretty printed
	// jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	//
	// jaxbMarshaller.marshal(rdf, file);
	// jaxbMarshaller.marshal(rdf, System.out);
	//
	// }

}