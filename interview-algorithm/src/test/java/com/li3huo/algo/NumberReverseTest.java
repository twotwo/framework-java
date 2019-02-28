package test.java.com.li3huo.algo;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * NumberReverseTest
 */
public class NumberReverseTest {

  public int reverseNumber(int number) {

    int reverse = 0;
    while (number != 0) {
      reverse = (reverse * 10) + (number % 10);
      number = number / 10;
    }
    return reverse;
  }

  /**
   * mvn test -Dtest=NumberReverseTest
   */
  @Test
  public void testReverseNumber(){
    assertEquals(345678, reverseNumber(876543));
  }
}
