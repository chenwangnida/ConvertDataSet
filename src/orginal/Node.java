package orginal;

import java.util.HashSet;
import java.util.Set;

public class Node implements Cloneable {
	private String name;
	private double[] qos;
	private Set<String> inputs;
	private Set<String> outputs;

	public Node(String name, double[] qos, Set<String> inputs, Set<String> outputs) {
		this.name = name;
		this.qos = qos;
		this.inputs = inputs;
		this.outputs = outputs;
	}

	public double[] getQos() {
		return qos;
	}

	public Set<String> getInputs() {
		return inputs;
	}

	public Set<String> getOutputs() {
		return outputs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setQos(double[] qos) {
		this.qos = qos;
	}

	public void setInputs(Set<String> inputs) {
		this.inputs = inputs;
	}

	public void setOutputs(Set<String> outputs) {
		this.outputs = outputs;
	}

	public Node clone(GenerateQoS qosGenerator) {
		String name1 = this.name.concat("_1");
		double[] qos1 = new double[4];
		qos1[ConvertWS.TIME] = qosGenerator.timeDistribution.sample();
		qos1[ConvertWS.COST] = qosGenerator.costDistribution.sample();
		qos1[ConvertWS.AVAILABILITY] = qosGenerator.availDistribution.sample() / 100;
		qos1[ConvertWS.RELIABILITY] = qosGenerator.reliaDistribution.sample() / 100;
		Set<String> inputs1 = new HashSet<String>();
		Set<String> outputs1 = new HashSet<String>();
		inputs1.addAll(getInputs());
		outputs1.addAll(getOutputs());

		Node node = new Node(name1, qos1, inputs1, outputs1);
		return node;
	}

}
