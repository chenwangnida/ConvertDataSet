package randy.wsdl.types;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import randy.NamespaceManager;

public class SemMessageExt {

	private String id;
	private List<SemExt> semExt = new ArrayList<SemExt>();
	
	@XmlAttribute(name="id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlElement(name="semExt", namespace=NamespaceManager.MECE_NAMESPACE)
	public List<SemExt> getSemExt() {
		return semExt;
	}
	
	/**
	 * suppose id = serv638709584ResponseMessage
	 * then service id is 638709584
	 * @return
	 */
	public String getServiceID(){
		String tail = this.isRequestMessage() ? "RequestMessage" : "ResponseMessage";
		int tailBeginPos = this.id.indexOf(tail);
		return this.id.substring(4, tailBeginPos);
	}
	
	/**
	 * suppose id = serv638709584ResponseMessage
	 * then return false
	 * @return
	 */
	public boolean isRequestMessage(){
		return this.id.endsWith("RequestMessage");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	

}
