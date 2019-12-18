package en.pai.neural.profilenet;

import java.util.ArrayList;

import en.pai.main.Main;
import en.pai.neural.TrainingData;
import en.pai.neural.TrainingUnit;
import en.pai.neural.levelnet.DegreeStudentUnit;
import en.pai.objects.Student;

public class ProfileDataM extends TrainingData {
	protected ArrayList<TrainingUnit> getSuitableUnits() {
		ArrayList<TrainingUnit> result = new ArrayList<TrainingUnit>();
		for (String studentID:Main.students.keySet()) {
			Student currentStudent = Main.students.get(studentID);
			if (currentStudent.hasProfile() && currentStudent.hasDegree()) {
				if (	!currentStudent.getLatestProfile().equalsIgnoreCase("Natuur en Gezondheid") && !currentStudent.getLatestProfile().equalsIgnoreCase("Natuur en Techniek") && 
						!currentStudent.getLatestProfile().equalsIgnoreCase("Economie en Maatschappij") && !currentStudent.getLatestProfile().equalsIgnoreCase("Cultuur en Maatschappij") && 
						currentStudent.hasDataFor("VMBO", 2) && currentStudent.getDegreeLevel().equalsIgnoreCase("VMBO")) {
					ProfileStudentUnit currentStudentUnit = new ProfileStudentUnit(currentStudent, "VMBO", 2);
					if (currentStudentUnit.dataAmountCount() >= 9) {
						result.add(currentStudentUnit);
					}
				} 
			}
		}
		return result;
	}
	
}
