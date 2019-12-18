package en.pai.neural.customnet;

import en.pai.neural.TrainingUnit;

public class CustomUnit extends TrainingUnit {
	private float[] inputs;
	private String[] inputNames;
	private String output;
	
	public CustomUnit(float[] inputs, String[] inputNames, String output) {
		this.inputs = inputs;
		this.inputNames = inputNames;
		this.output = output;
	}
	
	public String[] getInputNames() {
		return inputNames;
	}

	public float[] getInputs() {
		return inputs;
	}

	public String getOutput() {
		return output;
	}

}
