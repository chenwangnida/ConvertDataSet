package randy.owl.types;

import javax.xml.bind.annotation.XmlAttribute;

public class Ontology {
	
	
	private String about;
	
	@XmlAttribute
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
