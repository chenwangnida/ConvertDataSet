package randy.wsdl.types;

import javax.xml.bind.annotation.XmlAttribute;

public class Message {

	
	private String name;
	
	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
