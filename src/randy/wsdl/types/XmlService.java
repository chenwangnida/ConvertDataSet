package randy.wsdl.types;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class XmlService {

	private String name;
	private Port port;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement
	public Port getPort() {
		return port;
	}

	public void setPort(Port port) {
		this.port = port;
	}

}
