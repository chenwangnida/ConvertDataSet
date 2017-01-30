package orginal;

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

	public Node clone() {
		return new Node(name, qos, inputs, outputs);
	}

}
