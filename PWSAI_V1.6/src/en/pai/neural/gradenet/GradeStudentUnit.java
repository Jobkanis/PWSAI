package en.pai.neural.gradenet;

import en.pai.neural.TrainingUnit;
import en.pai.objects.Student;
import en.pai.util.Util;

public class GradeStudentUnit extends TrainingUnit {
	private Student student;
	private String level;
	private int year;
	public GradeStudentUnit(Student student, String level, int year) {
		this.student = student;
		this.level = level;
		this.year = year;
	}
	
	public float[] getInputs() {
		return new float[] {Util.toUnitInterval(student.getGrade(level, year, "NE"), 1, 10), 
							Util.toUnitInterval(student.getGrade(level, year, "EN"), 1, 10), 
							Util.toUnitInterval(student.getGrade(level, year, "WI"), 1, 10), 
							Util.toUnitInterval(student.getGrade(level, year, "FA"), 1, 10), 
							Util.toUnitInterval(student.getGrade(level, year, "DU"), 1, 10), 
							Util.toUnitInterval(student.getGrade(level, year, "AK"), 1, 10), 
							Util.toUnitInterval(student.getGrade(level, year, "GS"), 1, 10), 
							Util.toUnitInterval(student.getGrade(level, year, "NA"), 1, 10), 
							Util.toUnitInterval(student.getGrade(level, year, "SK"), 1, 10), 
							Util.toUnitInterval(student.getGrade(level, year, "BI"), 1, 10), 
							Util.toUnitInterval(student.getGrade(level, year, "EC"), 1, 10), 
							Util.toUnitInterval(student.getGrade(level, year, "MU"), 1, 10), 
							level.equals("VWO") ?1:0,
							level.equals("HAVO") ?1:0,
							level.equals("VMBO") ?1:0};
	}
	
	public int dataAmountCount() {
		return  ((student.getGrade(level, year, "NE") != 0)?1:0)+
				((student.getGrade(level, year, "EN") != 0)?1:0)+
				((student.getGrade(level, year, "WI") != 0)?1:0)+
				((student.getGrade(level, year, "FA") != 0)?1:0)+
				((student.getGrade(level, year, "DU") != 0)?1:0)+
				((student.getGrade(level, year, "AK") != 0)?1:0)+
				((student.getGrade(level, year, "GS") != 0)?1:0)+
				((student.getGrade(level, year, "NA") != 0)?1:0)+
				((student.getGrade(level, year, "SK") != 0)?1:0)+
				((student.getGrade(level, year, "BI") != 0)?1:0)+
				((student.getGrade(level, year, "EC") != 0)?1:0)+
				((student.getGrade(level, year, "MU") != 0)?1:0);
	}

	public String[] getInputNames() {
		return new String[] {"NE",
							"EN",
							"WI",
							"FA",
							"DU",
							"AK",
							"GS",
							"NA",
							"SK",
							"BI",
							"EC",
							"MU",
							"VWO",
							"HAVO",
							"VMBO"};
	}

	private int getLevelInteger(String level) {
		if (level.equals("VWO")) {
			return 3;
		} else if (level.equals("HAVO")) {
			return 2;
		} else if (level.equals("VMBO")) {
			return 1;
		}
		System.err.println("Level unidentified");
		return -1;
	}
	
	private static String getRange(float grade, float rangeWidth) {
		for (int i = 0; i <= 9f/rangeWidth; i++) {
			if (grade >= i*rangeWidth && grade < (i+1)*rangeWidth) {
				return String.valueOf(i*rangeWidth) + " - " + String.valueOf((i+1)*rangeWidth);
			}
		}
		return "NULL";
	}
	
	private float getAverageCE() {
		float totalCE = 0;
		for (String key:student.getCEs().keySet()) {
			totalCE += student.getCEs().get(key);
		}
		return totalCE/student.getCEs().size();
	}
	
	public String getOutput() {
		return getRange(getAverageCE(), 0.5f);
	}

}
