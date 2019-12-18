package en.pai.neural.interruptnet;

import java.util.ArrayList;
import java.util.Random;

import en.pai.main.Main;
import en.pai.neural.TrainingData;
import en.pai.neural.TrainingUnit;
import en.pai.neural.levelnet.DegreeStudentUnit;
import en.pai.neural.profilenet.ProfileDataHV;
import en.pai.objects.Student;

public class InterruptData extends TrainingData {
	public ArrayList<TrainingUnit> getSuitableUnits() {
		ArrayList<TrainingUnit> result = new ArrayList<TrainingUnit>();
		for (String studentID:Main.students.keySet()) {
			Student currentStudent = Main.students.get(studentID);
			
			if (currentStudent.hasCE()) {
				if (currentStudent.hasDataFor("VWO", 3)) {
					InterruptStudentUnit currentStudentUnit = new InterruptStudentUnit(currentStudent, "VWO", 3);
					if (currentStudentUnit.dataAmountCount() >= 9) {
						result.add(currentStudentUnit);
					}
				} 
				if (currentStudent.hasDataFor("HAVO", 3)) {
					InterruptStudentUnit currentStudentUnit = new InterruptStudentUnit(currentStudent, "HAVO", 3);
					if (currentStudentUnit.dataAmountCount() >= 9) {
						result.add(currentStudentUnit);
					}
				} 
				if (currentStudent.hasDataFor("VMBO", 2)) {
					InterruptStudentUnit currentStudentUnit = new InterruptStudentUnit(currentStudent, "VMBO", 2);
					if (currentStudentUnit.dataAmountCount() >= 9) {
						result.add(currentStudentUnit);
					}
				}
			}
		}
		return result;
	}
	
}
