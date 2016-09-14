package randy.owl;

import java.io.FileNotFoundException;
import java.util.HashMap;

import javax.xml.bind.JAXBException;

import randy.owl.types.OWLClass;
import randy.owl.types.OWLInst;
import randy.owl.types.RDF;

public class SemanticsPool {

	private final HashMap<String, OWLClass> owlClassHashMap = new HashMap<String, OWLClass>();
	private final HashMap<String, OWLInst> owlInstHashMap = new HashMap<String, OWLInst>();


	public HashMap<String, OWLClass> getOwlClassHashMap() {
		return this.owlClassHashMap;
	}
	public HashMap<String, OWLInst> getOwlInstHashMap() {
		return this.owlInstHashMap;
	}
	public static SemanticsPool createFromXML(String filePath) throws FileNotFoundException, JAXBException{
		RDF rdf = RDF.parseXML(filePath);
		SemanticsPool sp = new SemanticsPool();
		for (OWLClass cl : rdf.getOwlClassList()){
			sp.owlClassHashMap.put(cl.getID(), cl);
			System.out.println("put class " + cl.getID());
		}
		for (OWLInst inst : rdf.getOwlInstList()){
			sp.owlInstHashMap.put(inst.getID(), inst);
			System.out.println("put inst " + inst.getID());
		}
		return sp;
	}

	/**
	 * check if sonInst equals to fatherInst or sonInst is child of fatherInst
	 *
	 * @param sonInst
	 * @param fatherInst
	 * @return
	 */
	public boolean isChild(String sonInst, String fatherInst) {
		// the reason to add .substring is the name of class in instance is
		// #conXXX
		// but the name of class in class definition is XXX
		OWLClass currentClass = this.owlClassHashMap.get(this.owlInstHashMap
				.get(sonInst).getRdfType().getResource().substring(1));
		OWLClass fatherClass = this.owlClassHashMap.get(
				this.owlInstHashMap
				.get(fatherInst).getRdfType().getResource().substring(1));

		while(true){
			if (currentClass.getID().equals(fatherClass.getID())){
				return true;
			}
			if (currentClass.getSubClassOf() == null
					|| currentClass.getSubClassOf().getResource().equals("")) {
				break;
			}
			currentClass = this.owlClassHashMap.get(currentClass.getSubClassOf()
					.getResource().substring(1));
		}
		return false;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			SemanticsPool sp = SemanticsPool.createFromXML("Taxonomy.owl");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
