package en.pai.main;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import en.pai.IO.IOUtils;
import en.pai.UI.Frame;
import en.pai.neural.Network;
import en.pai.neural.customnet.CustomNetwork;
import en.pai.neural.gradenet.GradeNetwork;
import en.pai.neural.interruptnet.InterruptNetwork;
import en.pai.neural.levelnet.DegreeNetwork;
import en.pai.neural.levelnet.DegreeStudentUnit;
import en.pai.neural.profilenet.ProfileDataHV;
import en.pai.neural.profilenet.ProfileDataM;
import en.pai.neural.profilenet.ProfileNetworkHV;
import en.pai.neural.profilenet.ProfileNetworkM;
import en.pai.objects.Student;

public class Main {
	public static LinkedHashMap<String, Student> students = new LinkedHashMap<String, Student>();
	public static Frame frame;
	
	public static String status;
	public static int HiddenLayers, HiddenNeurons;
	public static String fileNameToLoad;
	public static int learnLimit = -1;
	public static long milliPause = 1;
	
	public static void main(String[] args) {
		System.out.println("Version 1.6.1\n");
		Scanner scan = new Scanner(System.in);
		
		System.out.println("Create a network or Load one? [Create/Load]");
		String newNN = scan.next();
		
		String type;
		String status = "Train";
		if (newNN.equalsIgnoreCase("Create") || newNN.equalsIgnoreCase("c")) {
			
			System.out.println("Profiles HAVO/VWO, Profiles MAVO, Levels, Grades or Interrupts? [ProfilesHV/ProfilesM/Levels/Grades/Interrupts]");
			type = scan.next();
			
			if (type.equalsIgnoreCase("ProfilesHV")) {
				type = "PHV";
			} else if (type.equalsIgnoreCase("ProfilesM")) {
				type = "PFM";
			} else if (type.equalsIgnoreCase("Levels") || type.equalsIgnoreCase("l")) {
				type = "DGS";
			} else if (type.equalsIgnoreCase("Grades") || type.equalsIgnoreCase("g")) {
				type = "GRD";
			} else if (type.equalsIgnoreCase("Interrupts") || type.equalsIgnoreCase("i")) {
				type = "ITE";
			} else if (type.equalsIgnoreCase("Custom") || type.equalsIgnoreCase("c")) {
				type = "CST";
			}
			
			System.out.println("How many hidden layers? [0-3]");
			HiddenLayers = scan.nextInt();
			System.out.println("How many neurons per layer? [0-16]");
			HiddenNeurons = scan.nextInt();
		} else {
			System.out.println("Which NN should be loaded? [Something.txt] {Needs to be from res/Saves/}");
			fileNameToLoad = scan.next();
			type = fileNameToLoad.substring(0, 3);
			
			System.out.println("Train, Test or Use? [Train/Test/Use]");
			status = scan.next();
		}
		
		
		if (status.equalsIgnoreCase("Train")) {
			System.out.println("Automatically reset at N batches trained [Number, -1 for unlimited batches]");
			learnLimit = scan.nextInt();
			
			System.out.println("Pause for M milliseconds after every batch [Number, 0 for no pause] {WARNING, 0 MIGHT FRY CPU}");
			milliPause = scan.nextInt();
			
			//System.out.println("Show Training GUI? [Yes/No]");
			//String sShowGUI = scan.next();
			//TODO Create option to not show GUI, and optimize GUI
			
			//if (sShowGUI.equalsIgnoreCase("n") || sShowGUI.equalsIgnoreCase("no")) {
			//	showGUI = false;
			//}
		}
		
		
		
		//Initialize students
		if (!type.equals("CST")) {
			initializeStudents();
		}
		System.out.println("Initialized students");
		Network network;
		if (type.equals("PHV")) {
			network = new ProfileNetworkHV();
		} else if (type.equals("PFM")) {
			network = new ProfileNetworkM();
		} else if (type.equals("CST")) {
			network = new CustomNetwork();
		} else if (type.equals("GRD")) {
			network = new GradeNetwork();
		} else if (type.equals("ITE")) {
			network = new InterruptNetwork();
		} else {
			network = new DegreeNetwork();
		} 
		
		
		//Initialize or load network
		if (fileNameToLoad != null) {
			network.load("res/Saves/" + fileNameToLoad);
		} else {
			network.init(HiddenLayers, HiddenNeurons);
		}
		
		//Train the network
		if (status.equalsIgnoreCase("Train")) {
			scan.close();
			//Create GUI
			frame = new Frame(network);
			frame.setVisible(true);
			
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					network.save("res/Saves/" + network.getTypeCode() + "OngoingSave.txt");
					scan.close();
				}
			});
			
			while (true) {
				while(network.learn()) {
					try {
						frame.repaint();
					} catch (Exception e) {
						System.out.println("Painting failed");
					}
					if (milliPause > 0) {
						try {
							Thread.sleep(milliPause);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				network.init(HiddenLayers, HiddenNeurons);
			}
		} else if (status.equalsIgnoreCase("Test")) {
			System.out.println("Accuracy = " + network.test());
		} else if (status.equalsIgnoreCase("Use")) {
			System.out.println("Student NE grade [1.0-10.0]");
			float NE = scan.nextFloat();
			System.out.println("Student EN grade [1.0-10.0]");
			float EN = scan.nextFloat();
			System.out.println("Student WI grade [1.0-10.0]");
			float WI = scan.nextFloat();
			System.out.println("Student FA grade [1.0-10.0]");
			float FA = scan.nextFloat();
			System.out.println("Student DU grade [1.0-10.0]");
			float DU = scan.nextFloat();
			System.out.println("Student AK grade [1.0-10.0]");
			float AK = scan.nextFloat();
			System.out.println("Student GS grade [1.0-10.0]");
			float GS = scan.nextFloat();
			System.out.println("Student NA grade [1.0-10.0]");
			float NA = scan.nextFloat();
			System.out.println("Student SK grade [1.0-10.0]");
			float SK = scan.nextFloat();
			System.out.println("Student BI grade [1.0-10.0]");
			float BI = scan.nextFloat();
			System.out.println("Student EC grade [1.0-10.0]");
			float EC = scan.nextFloat();
			System.out.println("Student MU grade [1.0-10.0]");
			float MU = scan.nextFloat();
			System.out.println("Student Level [VWO/HAVO/VMBO]");
			String level = scan.next();
			
			int year = -1;
			if (level.equalsIgnoreCase("VWO") || level.equalsIgnoreCase("HAVO")) {
				year = 3;
			} else if (level.equalsIgnoreCase("VMBO")) {
				year = 2;
			}
			
			Student s = new Student("007");
			s.addGrade(level, year, "NE", NE);
			s.addGrade(level, year, "EN", EN);
			s.addGrade(level, year, "WI", WI);
			s.addGrade(level, year, "FA", FA);
			s.addGrade(level, year, "DU", DU);
			s.addGrade(level, year, "AK", AK);
			s.addGrade(level, year, "GS", GS);
			s.addGrade(level, year, "NA", NA);
			s.addGrade(level, year, "SK", SK);
			s.addGrade(level, year, "BI", BI);
			s.addGrade(level, year, "EC", EC);
			s.addGrade(level, year, "MU", MU);
			
			DegreeStudentUnit su = new DegreeStudentUnit(s, level, year);
			
			System.out.println("Calculated output:" + network.calculateOutput(su));
		}
		scan.close();
	}
	
	/**
	 * A function to initialize all data, and put them into separate Student objects.
	 */
	private static void initializeStudents() {
		//Read data from file
		String[][] data = null;
		try {
			data = IOUtils.commaSeparatedFileToArray("res/DataSchoolsuccesTotaal.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Initialize all students
		Student currentStudent = null;
		String lastID = "";
		for (int i = 1; i < data.length-1; i++) {
			//Get the student ID
			String currentID = data[i][0];
			
			//Look up corresponding Student object
			if (!currentID.equals(lastID)) {
				if (!students.containsKey(currentID)) {
					currentStudent = new Student(currentID);
					students.put(currentID, currentStudent);
				} else {
					currentStudent = students.get(currentID);
				}
			}
			
			
			//Get the level of education
			String levelCode = data[i][1];
			String level;
			
			if (levelCode.contains("VWO") || levelCode.contains("GYM")) {
				level = "VWO";
			} else if (levelCode.contains("Havo")) {
				level = "HAVO";
			} else if (levelCode.contains("Mavo")) {
				level = "VMBO";
			} else if (levelCode.contains("MGL")) {
				level = "MGL";
			} else {
				level = "UNKNOWN";
			}
			
			//Get the year of education
			Pattern pattern = Pattern.compile("[0-9]");
			Matcher matcher = pattern.matcher(levelCode);
			matcher.find();
			int year = Integer.valueOf(matcher.group());
			
			//Get the subject
			String subject = data[i][3];
			
			//Get the grade
			float grade = getGradeValueFromString(data[i][4]);
			
			//Adds the grade to the Student
			if (grade != 0) {
				currentStudent.addGrade(level, year, subject, grade);
			}
			
			//Adds potential CE or SE grades to the Student
			float CEGrade = getGradeValueFromString(data[i][5]);
			if (CEGrade != 0) {
				currentStudent.setCE(subject, CEGrade);
				//Sets the degreeLevel if CE exists
				currentStudent.setDegreeLevel(level);
			}
			
			float SEGrade = getGradeValueFromString(data[i][6]);
			if (SEGrade != 0) {
				currentStudent.setSE(subject, SEGrade);
			}
			
			//TODO Change the initialization of profiles to only take the profile someone has in their last year, and not in any of the others.
			if (!data[i][16].equals("#N/A") && !data[i][16].equals("RMavo3") && !data[i][16].equals("Dienstverlening en producten")) {
				if (data[i][16].equals("Zorg en welzijn")) {
					currentStudent.addProfile(year, "Zorg en Welzijn");
				} else {
					currentStudent.addProfile(year, data[i][16]);
				}
			}
			
			lastID = currentID;
		}
	}
	
	/**
	 * Gets the grade value from a given string
	 * @param str The string to convert to a grade
	 * @return A float representing the height of the grade, or 0 when the string can't be parsed as a grade
	 */
	private static float getGradeValueFromString(String str) {
		try {
			return Float.valueOf(str);
		} catch(Exception e) {
			return 0;
		}
	}

}
