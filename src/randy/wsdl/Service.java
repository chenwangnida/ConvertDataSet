package randy.wsdl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import randy.owl.SemanticsPool;
import randy.wsdl.types.SemExt;
import randy.wsdl.types.SemMessageExt;

public class Service {

	private final String serviceID;
	private final List<String> inputList = new ArrayList<String>();
	private final List<String> outputList = new ArrayList<String>();

	public String getServiceID() {
		return this.serviceID;
	}

	public List<String> getInputList() {
		return this.inputList;
	}

	public List<String> getOutputList() {
		return this.outputList;
	}

	public Service(String name){
		this.serviceID = name;
	}

	/**
	 * check is the service is usable with the current input list
	 * 
	 * @param sp
	 * @param intputList
	 * @return
	 */
	public boolean usable(SemanticsPool sp, HashSet<String> inputSet) {
		boolean[] contained = new boolean[this.inputList.size()];
		Arrays.fill(contained, false);
		int containedInputCount = 0;
		// check if the inputSet contains all the required input
		for (String input : inputSet) {
			for (int i = 0; i < this.inputList.size(); i++) {
				if (contained[i]) {
					continue;
				}
				if (sp.isChild(input, this.inputList.get(i))) {
					contained[i] = true;
					containedInputCount++;
					// contain all the required input
					if (containedInputCount == this.inputList.size()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * using SemMEssageExt containing requestmessage and response message to
	 * create an instance of Service
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static Service fromSemMessageExts(SemMessageExt request, SemMessageExt response){
		String serviceID = request.getServiceID();
		if (response.getServiceID().equals(serviceID) == false){
			System.err.println("Service ID not match");
			return null;
		}
		if (!request.isRequestMessage() || response.isRequestMessage()){
			System.err.println("SemMessageExt type not match");
			return null;
		}
		Service service = new Service(serviceID);
		for (SemExt se : request.getSemExt()){
			int instBeginPos = se.getOntologyRef().indexOf("inst");
			service.inputList.add(se.getOntologyRef().substring(instBeginPos));
		}
		for (SemExt se : response.getSemExt()){
			int instBeginPos = se.getOntologyRef().indexOf("inst");
			service.outputList.add(se.getOntologyRef().substring(instBeginPos));
		}
		return service;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
