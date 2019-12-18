package en.pai.neural.customnet;

import en.pai.neural.Network;
import en.pai.neural.TrainingData;

public class CustomNetwork extends Network {
	public CustomNetwork() {
		super(new CustomData());
	}

	public String getTypeCode() {
		return "CST";
	}

}
