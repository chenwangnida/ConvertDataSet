package randy.wsdl.types;

import javax.xml.bind.annotation.XmlAttribute;

public class Input {

	private String message;
	
	@XmlAttribute
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
