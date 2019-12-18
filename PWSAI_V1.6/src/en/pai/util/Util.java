package en.pai.util;

public class Util {
	public static float toUnitInterval(float number, float lower, float upper) {
		if (number < lower) {
			return 0;
		} else if (number > upper) {
			return 1;
		}
		return (number-lower)/(upper-lower);
	}
}
