package en.pai.neural;

import java.util.ArrayList;
import java.util.Random;

public abstract class TrainingData {
	protected final float trainingPercentage = 0.8f;
	protected abstract ArrayList<TrainingUnit> getSuitableUnits();
	
	private Random random = new Random(17);
	protected ArrayList<TrainingUnit> trainingUnits = new ArrayList<TrainingUnit>();
	protected ArrayList<TrainingUnit> testingUnits = new ArrayList<TrainingUnit>();
	
	public TrainingData() {
		ArrayList<TrainingUnit> suitableUnits = (ArrayList<TrainingUnit>)getSuitableUnits().clone();
		testingUnits = suitableUnits;
		
		random.setSeed(17);
		int randomValueCount = (int)(testingUnits.size()*trainingPercentage);
		for (int i = 0; i < randomValueCount; i++) {
			int unitIndex = random.nextInt(testingUnits.size());
			trainingUnits.add(testingUnits.get(unitIndex));
			testingUnits.remove(unitIndex);
		}
	}
	
	public ArrayList<TrainingUnit> getTrainingUnits() {
		return trainingUnits;
	}
	public ArrayList<TrainingUnit> getTestingUnits() {
		return testingUnits;
	}
	private ArrayList<String> possibleOutputs;
	protected ArrayList<String> getPossibleOutputs() {
		if (possibleOutputs != null) {
			return possibleOutputs;
		}
		ArrayList<TrainingUnit> trainingUnits = getTrainingUnits();
		ArrayList<String> result = new ArrayList<String>();
		for (TrainingUnit t:trainingUnits) {
			if (!result.contains(t.getOutput())) {
				result.add(t.getOutput());
			}
		}
		possibleOutputs = result;
		return result;
	};
}
