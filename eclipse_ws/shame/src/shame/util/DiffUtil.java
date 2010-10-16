package shame.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiffUtil {
	static final Logger logger = LoggerFactory.getLogger(ProcessUtil.class);
	
	/**
	 * Find the longest common subsequence.
	 * 
	 * @see http://en.wikibooks.org/wiki/Algorithm_implementation/Strings/
	 *      Longest_common_subsequence
	 * @param <E>
	 *            any Object. Should properly override the <code>equals()</code>
	 *            method.
	 * @param s1
	 *            array of <code>E</code>
	 * @param s2
	 *            array of <code>E</code>
	 * @return a <code>List</code> of the elements in the longest common
	 *         subsequence between the two given <code>Array</code>-s.
	 */
	public static <E> List<E> findLongestCommonSubsequence(E[] s1, E[] s2) {
		// 2d array, initialized to zero
		int[][] num = new int[s1.length + 1][s2.length + 1];

		// actual algorithm
		for (int i = 1; i <= s1.length; i++) {
			for (int j = 1; j <= s2.length; j++) {
				if (s1[i - 1].equals(s2[j - 1])) {
					num[i][j] = 1 + num[i - 1][j - 1];
				} else {
					num[i][j] = Math.max(num[i - 1][j], num[i][j - 1]);
				}
			}
		}

		logger.debug("LCS length: " + num[s1.length][s2.length]);

		int s1position = s1.length, s2position = s2.length;
		List<E> result = new LinkedList<E>();

		while (s1position != 0 && s2position != 0) {
			if (s1[s1position - 1].equals(s2[s2position - 1])) {
				result.add(s1[s1position - 1]);
				s1position--;
				s2position--;
			} else if (num[s1position][s2position - 1] >= num[s1position - 1][s2position]) {
				s2position--;
			} else {
				s1position--;
			}
		}

		Collections.reverse(result);

		return result;
	}

}
