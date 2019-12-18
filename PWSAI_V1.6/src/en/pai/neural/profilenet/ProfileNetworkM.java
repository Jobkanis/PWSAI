package en.pai.neural.profilenet;

import java.util.ArrayList;

import en.pai.neural.Network;
import en.pai.neural.TrainingData;

public class ProfileNetworkM extends Network {
	public ProfileNetworkM() {
		super(new ProfileDataM());
	}
	public String getTypeCode() {
		return "PFM";
	}
	
}
