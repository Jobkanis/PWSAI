package en.pwsai.main;

import java.io.IOException;
import java.util.ArrayList;

import en.pwsai.objects.Student;
import en.pwsai.util.IO;
import en.pwsai.util.StringUtils;

public class Main {
	
	private static ArrayList<Student> students = new ArrayList<Student>();
	public static final int[] yearWeightsVWO = {1, 1, 2, 2, 3, 3};
	public static final int[] yearWeightsHAVO = {1, 1, 2, 2, 3};
	public static final int[] yearWeightsVMBO = {1, 2, 2, 3};
	public static final int 
	linear = 1, 
	exponential = 2; 
	public static final int
	VWO = 0,
	HAVO = 1,
	VMBO = 2;
	
	
	public static void main(String[] args) {
		init();
		double[] totalChances = totalChances(students.get(835), exponential, 3.0);
		double success = 0, failure = 0;
		for(Student s:students) {
			
			if (s.degreeLevel() != "NONE") {
				double[] VWOLower = new double[] {s.getVWOGrade(1), s.getVWOGrade(2), s.getVWOGrade(3), 0, 0, 0};
				double[] HAVOLower = new double[] {s.getHAVOGrade(1), s.getHAVOGrade(2), s.getHAVOGrade(3), 0, 0};
				double[] VMBOLower = new double[] {s.getVMBOGrade(1), s.getVMBOGrade(2), 0, 0};
				Student lower = new Student(-1, VWOLower, HAVOLower, VMBOLower);
				if (lower.hasData()) {
					double[] prediction = totalChances(lower, linear, 3);
					System.out.println(s);
					System.out.println(prediction[0]);
					System.out.println(prediction[1]);
					System.out.println(prediction[2]);
					switch(s.degreeLevel()) {
						case("VWO"):
							if (prediction[VWO] > 0.7) {
								success += 1;
							} else {
								failure += 1;
							}
							break;
						case("HAVO"):
							if (prediction[HAVO] > 0.7) {
								success += 1;
							} else {
								failure += 1;
							}
							break;
						case("VMBO"):
							if (prediction[VMBO] > 0.7) {
								success += 1;
							} else {
								failure += 1;
							}
							break;
					}
				}
					
			}
		}
		System.out.println("SuccessRate: " + success/(success+failure));
	}
	
	public static double[] totalChances(Student student, int interpolation, double interpolationRange) {
		int VWOYearsOfData = 0;
		double[] totalVWOChances = {0, 0, 0};
		for (int i = 1; i <= 6; i++) {
			if (student.getGrade(VWO, i) != 0) {
				double[] VWOiChances = calcChances(VWO, i, student.getGrade(VWO, i), interpolation, interpolationRange);
				totalVWOChances[0] += VWOiChances[0] * yearWeightsVWO[i-1];
				totalVWOChances[1] += VWOiChances[1] * yearWeightsVWO[i-1];
				totalVWOChances[2] += VWOiChances[2] * yearWeightsVWO[i-1];
				VWOYearsOfData+=yearWeightsVWO[i-1];
			}
		}
		
		int HAVOYearsOfData = 0;
		double[] totalHAVOChances = {0, 0, 0};
		for (int i = 1; i <= 5; i++) {
			if (student.getGrade(HAVO, i) != 0) {
				double[] HAVOiChances = calcChances(HAVO, i, student.getGrade(HAVO, i), interpolation, interpolationRange);
				totalHAVOChances[0] += HAVOiChances[0] * yearWeightsHAVO[i-1];
				totalHAVOChances[1] += HAVOiChances[1] * yearWeightsHAVO[i-1];
				totalHAVOChances[2] += HAVOiChances[2] * yearWeightsHAVO[i-1];
				HAVOYearsOfData+=yearWeightsHAVO[i-1];
			}
		}
		
		int VMBOYearsOfData = 0;
		double[] totalVMBOChances = {0, 0, 0};
		for (int i = 1; i <= 4; i++) {
			if (student.getGrade(VMBO, i) != 0) {
				double[] VMBOiChances = calcChances(VMBO, i, student.getGrade(VMBO, i), interpolation, interpolationRange);
				totalVMBOChances[0] += VMBOiChances[0] * yearWeightsVMBO[i-1];
				totalVMBOChances[1] += VMBOiChances[1] * yearWeightsVMBO[i-1];
				totalVMBOChances[2] += VMBOiChances[2] * yearWeightsVMBO[i-1];
				VMBOYearsOfData+=yearWeightsVMBO[i-1];
			}
		}
		
		double[] totalTotalChances = {	(totalVWOChances[0] + totalHAVOChances[0] + totalVMBOChances[0])/(VWOYearsOfData+HAVOYearsOfData+VMBOYearsOfData),
										(totalVWOChances[1] + totalHAVOChances[1] + totalVMBOChances[1])/(VWOYearsOfData+HAVOYearsOfData+VMBOYearsOfData),
										(totalVWOChances[2] + totalHAVOChances[2] + totalVMBOChances[2])/(VWOYearsOfData+HAVOYearsOfData+VMBOYearsOfData)};
		
		return totalTotalChances;
	}
	
	/**
	 * 
	 * @param level
	 * The level of education of the processed Student in Integer form. Either {@link #VWO} {@link #HAVO} or {@link #VMBO}
	 * @param year
	 * The year of education, ranging anywhere from 1-6
	 * @param inputGrade
	 * The grade of the processed Student, in the year given.
	 * @param interpolation
	 * The interpolation to be used within the calculations. Either {@link #linear} or {@link #exponential}
	 * @param interpolationRange
	 * The range from which to interpolate
	 * @return
	 * An array of the calculated chances in a specific year. All results will be between 0 and 1 and are ordered like this: 0=VWO, 1=HAVO, 2=VMBO
	 */
	public static double[] calcChances(int level, int year, double inputGrade, int interpolation, double interpolationRange) {
		ArrayList<Double> grades = new ArrayList<Double>();
		ArrayList<String> degreeLevels = new ArrayList<String>();
		for (Student s:students) {
			double trainingGrade = s.getGrade(level, year);
			String degreeLevel = s.degreeLevel();
			if (trainingGrade != 0 && !degreeLevel.equals("NONE")) {
				grades.add(trainingGrade);
				degreeLevels.add(degreeLevel);
			}
		}
		
		double VWOWeight = 0, HAVOWeight = 0, VMBOWeight = 0;
		for (int i = 0; i < grades.size(); i++) {
			double gradeDifference = Math.abs(inputGrade-grades.get(i));
			double currentWeight;
			if (gradeDifference != 0) {
				currentWeight = Math.max(Math.pow(Math.max(1.0-(gradeDifference/interpolationRange), 0), interpolation), 0);
			} else {
				currentWeight = 1;
			}
			if (degreeLevels.get(i).equals("VWO")) {
				VWOWeight+=currentWeight;
			} else if (degreeLevels.get(i).equals("HAVO")) {
				HAVOWeight+=currentWeight;
			} else if (degreeLevels.get(i).equals("VMBO")) {
				VMBOWeight+=currentWeight;
			} 
		}
		
		double totalWeight = VWOWeight + HAVOWeight + VMBOWeight;
		double VWOChance = VWOWeight/totalWeight, HAVOChance = HAVOWeight/totalWeight, VMBOChance = VMBOWeight/totalWeight;
		return new double[] {VWOChance, HAVOChance, VMBOChance};
	}
	
	public static void init() {
		ArrayList<String> rows = null;
		try {
			rows = IO.fileToArrayList("res/DataSchoolsucces.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int row = 1;
		String[] RowData;
		double[] VWOGrades = new double[6];
		double[] HAVOGrades = new double[5];
		double[] VMBOGrades = new double[4];
		
		while ((RowData = StringUtils.seperateCommaString(rows.get(row))) != null) {
			VWOGrades[0] = StringUtils.getGradeFromString(RowData[1]);
			VWOGrades[1] = StringUtils.getGradeFromString(RowData[2]);
			VWOGrades[2] = StringUtils.getGradeFromString(RowData[3]);
			VWOGrades[3] = StringUtils.getGradeFromString(RowData[4]);
			VWOGrades[4] = StringUtils.getGradeFromString(RowData[5]);
			VWOGrades[5] = StringUtils.getGradeFromString(RowData[6]);
			
			HAVOGrades[0] = StringUtils.getGradeFromString(RowData[8]);
			HAVOGrades[1] = StringUtils.getGradeFromString(RowData[9]);
			HAVOGrades[2] = StringUtils.getGradeFromString(RowData[10]);
			HAVOGrades[3] = StringUtils.getGradeFromString(RowData[11]);
			HAVOGrades[4] = StringUtils.getGradeFromString(RowData[12]);
			
			VMBOGrades[0] = StringUtils.getGradeFromString(RowData[15]);
			VMBOGrades[1] = StringUtils.getGradeFromString(RowData[16]);
			VMBOGrades[2] = StringUtils.getGradeFromString(RowData[17]);
			VMBOGrades[3] = StringUtils.getGradeFromString(RowData[18]);
			
			students.add(new Student(Integer.valueOf(RowData[0]), VWOGrades.clone(), HAVOGrades.clone(), VMBOGrades.clone()));
			
			row++;
		}
	}
	
	
}
