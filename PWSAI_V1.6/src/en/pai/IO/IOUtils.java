package en.pai.IO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
/**
 * A class containing utility functions regarding the Input and Output from and to files.
 * @author Julian van der Weijden
 */
public class IOUtils {

	/**
	 * A function to quickly read all lines of a file
	 * @param path Path of the file to be read
	 * @return All lines (separated by \n) of the file in an ArrayList
	 * @throws IOException
	 */
	public static ArrayList<String> fileToArrayList(String path) throws IOException {
		FileReader fReader = null;
		fReader = new FileReader(path);
		BufferedReader bReader = new BufferedReader(fReader);
		
		ArrayList<String> lines = new ArrayList<String>();
		String line = null;
		while ((line = bReader.readLine()) != null) {
			lines.add(line);
		}
		bReader.close();
		return lines;
	}
	
	/**
	 * A function to turn a comma separated file into a 2 dimensional array.
	 * @param path Path of the file to be read
	 * @return A 2 dimensional array containing all lines and columns of the file.
	 * @throws IOException
	 */
	public static String[][] commaSeparatedFileToArray(String path) throws IOException {
		ArrayList<String> lines = fileToArrayList(path);
		String[][] result = new String[lines.size()][lines.get(0).split(",").length];
		for (int i = 0; i < lines.size(); i++) {
			result[i] = lines.get(i).split(",");
		}
		return result;
	}
	
	/**
	 * Creates a file and writes to it, or replaces an existing file
	 * @param path The path of the file of which to write to
	 * @param lines The lines to write to the file (replaces any existing filedata)
	 * @throws IOException
	 */
	public static void write(String path, ArrayList<String> lines) throws IOException {
		Files.write(new File(path).toPath(), lines, StandardCharsets.UTF_8);
	}
}
