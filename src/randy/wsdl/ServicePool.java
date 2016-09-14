package randy.wsdl;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;

import randy.owl.SemanticsPool;
import randy.wsdl.types.Definitions;

public class ServicePool {

	private final List<Service> serviceList = new LinkedList<Service>();
	private SemanticsPool semanticsPool;

	public static ServicePool parseXML(SemanticsPool semanticsPool,
			String serviceFilePath) throws FileNotFoundException, JAXBException {
		ServicePool sp = new ServicePool();
		sp.semanticsPool = semanticsPool;
		Definitions def = Definitions.parseXML(serviceFilePath);
		for (int i = 0; i < def.getSemExtension().getSemMessageExtList().size(); i+=2){
			sp.serviceList.add(Service.fromSemMessageExts(def.getSemExtension()
					.getSemMessageExtList().get(i), def.getSemExtension()
					.getSemMessageExtList().get(i + 1)));
		}
		return sp;
	}

	/**
	 * find a service that can be applied now and update the output list and
	 * delete the service
	 * 
	 * @param inputSet
	 */
	public String findPossibleService(HashSet<String> inputSet) {
		int foundServiceIndex = -1;
		for (int i = 0; i < this.serviceList.size(); i++) {
			Service s = this.serviceList.get(i);
			if (s.usable(this.semanticsPool, inputSet)) {
				foundServiceIndex = i;
				break;
			}
		}
		if (foundServiceIndex == -1) {
			return null;
		}
		Service service = this.serviceList.get(foundServiceIndex);
		this.serviceList.remove(foundServiceIndex);
		for (String output : service.getOutputList()) {
			if (!inputSet.contains(output)) {
				inputSet.add(output);
			}
		}
		return service.getServiceID();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
