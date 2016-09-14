package randy.owl.types;

import javax.xml.bind.annotation.XmlAttribute;

import randy.NamespaceManager;

public class Ontology {


	private String about;

	@XmlAttribute(namespace = NamespaceManager.RDF_NAMESPACE)
	public String getAbout() {
		return about;
	}


	public void setAbout(String about) {
		this.about = about;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
