package randy.owl.types;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import randy.NamespaceManager;
public class OWLInst {

	private String ID;
	private RDFType rdfType;
	
	@XmlAttribute(name="ID", namespace=NamespaceManager.RDF_NAMESPACE)
	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	@XmlElement(name="type", namespace=NamespaceManager.RDF_NAMESPACE)
	public RDFType getRdfType() {
		return rdfType;
	}

	public void setRdfType(RDFType rdfType) {
		this.rdfType = rdfType;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
