package en.pai.objects;

import java.util.HashMap;

/**
 * A class containing all information about a specific student
 * @author Julian van der Weijden
 */
public class Student {
	private String studentID;
	
	/**
	 * A hashmap containing all grades in the following fashion:
	 * [Level(STR), [Year(INT), [Subject(STR), Grade(FLT)]]];
	 */
	private HashMap<String, 
					HashMap<Integer, 
						HashMap<String, Float>>> grades = new HashMap<String, HashMap<Integer, HashMap<String, Float>>>();
	
	/**
	 * A hashmap containing the CE grades in the following fashion:
	 * [Subject(STR), CEGrade(FLT)];
	 */
	private HashMap<String, Float> CE = new HashMap<String, Float>();
	
	/**
	 * A hashmap containing the SE grades in the following fashion:
	 * [Subject(STR), SEGrade(FLT)];
	 */
	private HashMap<String, Float> SE = new HashMap<String, Float>();
	
	/**
	 * The level at which someone finished highschool
	 */
	private String degreeLevel = "NULL";
	
	private HashMap<Integer, String> profiles = new HashMap<Integer, String>();
	
	private HashMap<String, Integer> seniorOverrideCounts = new HashMap<String, Integer>();
	
	public Student(String studentID) {
		this.studentID = studentID;
	}
	
	/**
	 * Adds a grade to this Student, and warns you if any data gets overridden
	 * @param level The level of education
	 * @param year The year of education
	 * @param subject The subject of the grade
	 * @param grade The grade itself
	 */
	public void addGrade(String level, int year, String subject, float grade) {
		if (!grades.containsKey(level)) {
			grades.put(level, new HashMap<Integer, HashMap<String, Float>>());
		}
		if (!grades.get(level).containsKey(year)) {
			grades.get(level).put(year, new HashMap<String, Float>());
		}
		Float previousValue = grades.get(level).get(year).put(subject, grade);
		
		if (previousValue != null) {
			System.out.println("Grade overridden: [StudentID=" + studentID + ",Level=" + level + ",Year=" + year + ",Subject=" + subject + "]"
								+ ", Old value was: [" + previousValue + "], New value is: [" + grade + "]");
			if ((level.equals("VWO") && year >= 4) || (level.equals("HAVO") && year >= 4) || (level.equals("VMBO") && year >= 3)) {
				if (seniorOverrideCounts.containsKey(subject)) {
					seniorOverrideCounts.put(subject, seniorOverrideCounts.get(subject)+1);
				} else {
					seniorOverrideCounts.put(subject, 1);
				}
			}
		}
	}
	
	/**
	 * Sets the SE grade for a specific subject and warns when data gets overridden
	 * @param subject The subject
	 * @param grade The SE grade
	 */
	public void setSE(String subject, float grade) {
		Float previousValue = SE.put(subject, grade);
		if (previousValue != null) {
			System.out.println("SE Grade overridden: [StudentID=" + studentID + ", Subject=" + subject + "]"
								+ ", Old value was: [" + previousValue + "], New value is: [" + grade + "]");
			
		}
	}
	
	/**
	 * Sets the CE grade for a specific subject and warns when data gets overridden
	 * @param subject The subject
	 * @param grade The CE grade
	 */
	public void setCE(String subject, float grade) {
		CE.put(subject, grade);
	}
	
	public HashMap<String, Float> getCEs() {
		return CE;
	}
	
	public boolean hasCE() {
		return !CE.isEmpty();
	}
	
	/**
	 * Sets the level at which someone has finished highschool
	 * @param degreeLevel
	 */
	public void setDegreeLevel(String degreeLevel) {
		this.degreeLevel = degreeLevel;
	}
	
	public float getGrade(String level, int year, String subject) {
		Float result = grades.get(level).get(year).get(subject);
		if (result == null) {
			return 0;
		} else {
			return result;
		}
	}
	
	public boolean hasDataFor(String level, int year) {
		return grades.get(level) != null && grades.get(level).get(year) != null && !grades.get(level).get(year).isEmpty();
	}
	
	public boolean hasDegree() {
		return !degreeLevel.equals("NULL");
	}
	
	public String getDegreeLevel() {
		return degreeLevel;
	}
	
	public String toString() {
		return "Student(ID=" + studentID + ")";
	}
	
	public String getLatestProfile() {
		int highestKey = 0;
		for (Integer I:profiles.keySet()) {
			if (I > highestKey) {
				highestKey = I;
			}
		}
		return profiles.get(highestKey);
	}
	
	public boolean hasProfile() {
		return !profiles.isEmpty();
	}
	
	public HashMap<Integer, String> getProfiles() {
		return profiles;
	}

	public void addProfile(Integer year, String profile) {
		profiles.put(year, profile);
	}
	
	public int countSeniorInterrupts() {
		int interrupts = 0;
		for (String key:seniorOverrideCounts.keySet()) {
			if (seniorOverrideCounts.get(key) > interrupts) {
				interrupts = seniorOverrideCounts.get(key);
			}
		}
		return interrupts;
	}
}
