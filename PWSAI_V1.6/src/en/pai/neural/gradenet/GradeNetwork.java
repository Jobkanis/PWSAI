package en.pai.neural.gradenet;

import en.pai.neural.Network;
import en.pai.neural.TrainingData;

public class GradeNetwork extends Network {
	public GradeNetwork() {
		super(new GradeData());
	}

	public String getTypeCode() {
		return "GRD";
	}

}
