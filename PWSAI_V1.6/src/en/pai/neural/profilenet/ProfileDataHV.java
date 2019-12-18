package en.pai.neural.profilenet;

import java.util.ArrayList;

import en.pai.main.Main;
import en.pai.neural.TrainingData;
import en.pai.neural.TrainingUnit;
import en.pai.neural.levelnet.DegreeStudentUnit;
import en.pai.objects.Student;

public class ProfileDataHV extends TrainingData {
	protected ArrayList<TrainingUnit> getSuitableUnits() {
		ArrayList<TrainingUnit> result = new ArrayList<TrainingUnit>();
		for (String studentID:Main.students.keySet()) {
			Student currentStudent = Main.students.get(studentID);
			if (currentStudent.hasProfile() && currentStudent.hasDegree()) {
				if (currentStudent.hasDataFor("VWO", 3) && currentStudent.getDegreeLevel().equalsIgnoreCase("VWO")) {
					ProfileStudentUnit currentStudentUnit = new ProfileStudentUnit(currentStudent, "VWO", 3);
					if (currentStudentUnit.dataAmountCount() >= 9) {
						result.add(currentStudentUnit);
					}
				} 
				if (currentStudent.hasDataFor("HAVO", 3) && currentStudent.getDegreeLevel().equalsIgnoreCase("HAVO")) {
					ProfileStudentUnit currentStudentUnit = new ProfileStudentUnit(currentStudent, "HAVO", 3);
					if (currentStudentUnit.dataAmountCount() >= 9) {
						result.add(currentStudentUnit);
					}
				} 
			}
		}
		return result;
	}
	
}
