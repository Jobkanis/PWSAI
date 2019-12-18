package en.pai.neural.customnet;

import java.util.ArrayList;

import en.pai.neural.TrainingData;
import en.pai.neural.TrainingUnit;
import en.pai.neural.profilenet.ProfileDataHV;

public class CustomData extends TrainingData {
	protected ArrayList<TrainingUnit> getSuitableUnits() {
		ArrayList<TrainingUnit> customUnits = new ArrayList<TrainingUnit>();
		customUnits.add(new CustomUnit(new float[] {0f,0f,0f,0f}, new String[] {"i1", "i2", "i3", "n"},  "0"));
		customUnits.add(new CustomUnit(new float[] {0f,0f,1f,0f}, new String[] {"i1", "i2", "i3", "n"}, "1"));
		customUnits.add(new CustomUnit(new float[] {0f,1f,0f,0f}, new String[] {"i1", "i2", "i3", "n"}, "2"));
		customUnits.add(new CustomUnit(new float[] {0f,1f,1f,0f}, new String[] {"i1", "i2", "i3", "n"}, "3"));
		customUnits.add(new CustomUnit(new float[] {1f,0f,0f,0f}, new String[] {"i1", "i2", "i3", "n"}, "4"));
		customUnits.add(new CustomUnit(new float[] {1f,0f,1f,0f}, new String[] {"i1", "i2", "i3", "n"}, "5"));
		customUnits.add(new CustomUnit(new float[] {1f,1f,0f,0f}, new String[] {"i1", "i2", "i3", "n"}, "6"));
		customUnits.add(new CustomUnit(new float[] {1f,1f,1f,0f}, new String[] {"i1", "i2", "i3", "n"}, "7"));
		return customUnits;
	}
	
}
