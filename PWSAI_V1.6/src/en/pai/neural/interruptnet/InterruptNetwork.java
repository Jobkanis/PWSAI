package en.pai.neural.interruptnet;

import en.pai.neural.Network;
import en.pai.neural.TrainingData;

public class InterruptNetwork extends Network {
	public InterruptNetwork() {
		super(new InterruptData());
	}

	public String getTypeCode() {
		return "ITE";
	}

}
