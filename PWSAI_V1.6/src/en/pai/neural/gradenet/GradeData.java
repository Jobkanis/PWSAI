package en.pai.neural.gradenet;

import java.util.ArrayList;

import en.pai.main.Main;
import en.pai.neural.TrainingData;
import en.pai.neural.TrainingUnit;
import en.pai.neural.levelnet.DegreeStudentUnit;
import en.pai.neural.profilenet.ProfileDataHV;
import en.pai.objects.Student;

public class GradeData extends TrainingData {
	public ArrayList<TrainingUnit> getSuitableUnits() {
		ArrayList<TrainingUnit> result = new ArrayList<TrainingUnit>();
		for (String studentID:Main.students.keySet()) {
			Student currentStudent = Main.students.get(studentID);
			if (currentStudent.hasCE()) {
				if (currentStudent.hasDataFor("VWO", 3)) {
					GradeStudentUnit currentStudentUnit = new GradeStudentUnit(currentStudent, "VWO", 3);
					if (currentStudentUnit.dataAmountCount() >= 9) {
						result.add(currentStudentUnit);
					}
				} 
				if (currentStudent.hasDataFor("HAVO", 3)) {
					GradeStudentUnit currentStudentUnit = new GradeStudentUnit(currentStudent, "HAVO", 3);
					if (currentStudentUnit.dataAmountCount() >= 9) {
						result.add(currentStudentUnit);
					}
				} 
				if (currentStudent.hasDataFor("VMBO", 2)) {
					GradeStudentUnit currentStudentUnit = new GradeStudentUnit(currentStudent, "VMBO", 2);
					if (currentStudentUnit.dataAmountCount() >= 9) {
						result.add(currentStudentUnit);
					}
				}
			}
		}
		return result;
	}
	
}
