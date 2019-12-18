package en.pwsai.objects;

import en.pwsai.main.Main;

public class Student {
	private int ID;
	private double[] VWOGrades = new double[6];
	private double[] HAVOGrades = new double[5];
	private double[] VMBOGrades = new double[4];
	
	public Student(int ID, double[] VWOGrades, double[] HAVOGrades, double[] VMBOGrades) {
		this.ID = ID;
		this.VWOGrades = VWOGrades;
		this.HAVOGrades = HAVOGrades;
		this.VMBOGrades = VMBOGrades;
		
	}
	
	public String toString() {
		return "ID:" + ID + " " +
				"VWO:[" + VWOGrades[0] + "," + VWOGrades[1] + "," + VWOGrades[2] + "," + VWOGrades[3] + "," + VWOGrades[4] + "," + VWOGrades[5] + "]" + " " +
				"HAVO:[" + HAVOGrades[0] + "," + HAVOGrades[1] + "," + HAVOGrades[2] + "," + HAVOGrades[3] + "," + HAVOGrades[4] + "]" + " " +
				"VMBO:[" + VMBOGrades[0] + "," + VMBOGrades[1] + "," + VMBOGrades[2] + "," + VMBOGrades[3] + "]" + " " + 
				"DegreeLevel: " + degreeLevel();
	}
	
	public String degreeLevel() {
		if (VWOGrades[5] > 5.5) {
			return "VWO";
		} else if (HAVOGrades[4] > 5.5) {
			return "HAVO";
		} else if (VMBOGrades[3] > 5.5) {
			return "VMBO";
		} else {
			return "NONE";
		}
	}
	public double getGrade(int level, int year) {
		if (level == Main.VWO) {
			return getVWOGrade(year);
		} else if (level == Main.HAVO) {
			return getHAVOGrade(year);
		} else if (level == Main.VMBO) {
			return getVMBOGrade(year);
		} else {
			System.err.print("Invalid level given");
			System.exit(1);
			return 0;
		}
	}
	public double getVWOGrade(int year) {
		return VWOGrades[year-1];
	}
	public double getHAVOGrade(int year) {
		return HAVOGrades[year-1];
	}
	public double getVMBOGrade(int year) {
		return VMBOGrades[year-1];
	}
	public boolean hasData() {
		for (double d:VWOGrades) {
			if (d != 0) {
				return true;
			}
		}
		for (double d:HAVOGrades) {
			if (d != 0) {
				return true;
			}
		}
		for (double d:VMBOGrades) {
			if (d != 0) {
				return true;
			}
		}
		return false;
	}
	
}
