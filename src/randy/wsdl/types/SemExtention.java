package randy.wsdl.types;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import randy.NamespaceManager;

public class SemExtention {

	private List<SemMessageExt> semMessageExtList = new ArrayList<SemMessageExt>();
	
	@XmlElement(name="semMessageExt", namespace=NamespaceManager.MECE_NAMESPACE)
	public List<SemMessageExt> getSemMessageExtList() {
		return semMessageExtList;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	

}
