package randy.wsdl.types;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import randy.NamespaceManager;

public class SemExt {

	private String id;
	private String ontologyRef;
	
	@XmlAttribute(name="id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlElement(namespace=NamespaceManager.MECE_NAMESPACE)
	public String getOntologyRef() {
		return ontologyRef;
	}

	public void setOntologyRef(String ontologyRef) {
		this.ontologyRef = ontologyRef;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
