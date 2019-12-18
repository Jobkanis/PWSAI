package en.pai.neural.profilenet;

import java.util.ArrayList;

import en.pai.neural.Network;
import en.pai.neural.TrainingData;

public class ProfileNetworkHV extends Network {
	public ProfileNetworkHV() {
		super(new ProfileDataHV());
	}
	public String getTypeCode() {
		return "PHV";
	}
	
}
