package randy.owl.types;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import randy.NamespaceManager;
public class OWLClass {

	private String ID;
	private OWLSubClassOf subClass;


	@XmlAttribute(name="ID", namespace=NamespaceManager.RDF_NAMESPACE)
	public String getID() {
		return ID;
	}


	public void setID(String iD) {
		ID = iD;
	}


	@XmlElement(name="subClassOf", namespace=NamespaceManager.RDFS_NAMESPACE)
	public OWLSubClassOf getSubClassOf() {
		return subClass;
	}


	public void setSubClassOf(OWLSubClassOf subClass) {
		this.subClass = subClass;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
