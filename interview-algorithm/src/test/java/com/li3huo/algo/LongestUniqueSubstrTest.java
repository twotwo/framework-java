package test.java.com.li3huo.algo;

import static org.junit.Assert.assertEquals;
import java.util.HashSet;
import org.junit.Test;

/**
 * LongestUniqueSubstrTest
 */
public class LongestUniqueSubstrTest {
  public int lengthOfLongestSubstring(String s) {
    // 实现代码，并给出必要的注释
    int res = 0, left = 0, right = 0;
    HashSet<Character> t = new HashSet<Character>();
    while (right < s.length()) {
      if (!t.contains(s.charAt(right))) {
        t.add(s.charAt(right++));
        res = Math.max(res, t.size());
      } else {
        t.remove(s.charAt(left++));
      }
    }
    return res;
  }

  /**
   * mvn test -Dtest=LongestUniqueSubstrTest
   */
  @Test
  public void testLongestSubstringLength() {
    assertEquals(3, lengthOfLongestSubstring("pwwkew"));
  }

}
