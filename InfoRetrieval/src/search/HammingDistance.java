package search;
import java.lang.Object;

public class HammingDistance {

	public int HammingDistance(String v1, String v2) {
		char[] s1 = v1.toCharArray();
		char[] s2 = v2.toCharArray();

		int shorter = Math.min(s1.length, s2.length);
		int longest = Math.max(s1.length, s2.length);

		int result = 0;
		for (int i=0; i<shorter; i++) {
			if (s1[i] != s2[i]) result++;
		}

		result += longest - shorter;

		return result;
	}
}
