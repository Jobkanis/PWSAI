package en.pwsai.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class IO {
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
}
