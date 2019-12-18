package en.pai.neural.levelnet;

import en.pai.neural.Network;
import en.pai.neural.TrainingData;

public class DegreeNetwork extends Network {
	public DegreeNetwork() {
		super(new DegreeData());
	}

	public String getTypeCode() {
		return "DGS";
	}
}
