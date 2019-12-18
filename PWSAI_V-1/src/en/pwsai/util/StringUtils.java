package en.pwsai.util;

public class StringUtils {
	public static String[] seperateCommaString(String input) {
		String[] result = input.split(",");
		if (result.length == 0) {
			return null;
		} else {
			return result;
		}
	}
	public static double getGradeFromString(String gradeString) {
		try {
			return Double.valueOf(gradeString);
		} catch (Exception e) {
			return 0;
		}
	}
}
