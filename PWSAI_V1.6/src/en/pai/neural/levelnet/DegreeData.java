package en.pai.neural.levelnet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import en.pai.main.Main;
import en.pai.neural.TrainingData;
import en.pai.neural.TrainingUnit;
import en.pai.objects.Student;

public class DegreeData extends TrainingData {
	public ArrayList<TrainingUnit> getSuitableUnits() {
		ArrayList<TrainingUnit> result = new ArrayList<TrainingUnit>();
		for (String studentID:Main.students.keySet()) {
			Student currentStudent = Main.students.get(studentID);
			if (currentStudent.hasDegree()) {
				if (currentStudent.hasDataFor("VWO", 3)) {
					DegreeStudentUnit currentStudentUnit = new DegreeStudentUnit(currentStudent, "VWO", 3);
					if (currentStudentUnit.dataAmountCount() >= 9) {
						result.add(currentStudentUnit);
					}
				} 
				if (currentStudent.hasDataFor("HAVO", 3)) {
					DegreeStudentUnit currentStudentUnit = new DegreeStudentUnit(currentStudent, "HAVO", 3);
					if (currentStudentUnit.dataAmountCount() >= 9) {
						result.add(currentStudentUnit);
					}
				} 
				if (currentStudent.hasDataFor("VMBO", 2)) {
					DegreeStudentUnit currentStudentUnit = new DegreeStudentUnit(currentStudent, "VMBO", 2);
					if (currentStudentUnit.dataAmountCount() >= 9) {
						result.add(currentStudentUnit);
					}
				}
			}
		}
		return result;
	}
	

}
