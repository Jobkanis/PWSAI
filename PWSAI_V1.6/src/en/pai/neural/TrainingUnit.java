package en.pai.neural;

public abstract class TrainingUnit {
	public abstract String[] getInputNames();
	public abstract float[] getInputs();
	public abstract String getOutput();
}
