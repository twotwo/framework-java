package test.java.com.li3huo.algo;

import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;

/**
 * BubbleSortTest
 */
public class BubbleSortTest {

  // 冒泡排序，a 表示数组，n 表示数组大小
  public void bubbleSort(int[] a, int n) {
    if (n <= 1)
      return;

    for (int i = 0; i < n; ++i) {
      // 提前退出冒泡循环的标志位
      boolean flag = false;
      for (int j = 0; j < n - i - 1; ++j) {
        if (a[j] > a[j + 1]) { // 交换
          int tmp = a[j];
          a[j] = a[j + 1];
          a[j + 1] = tmp;
          flag = true; // 表示有数据交换
        }
      }
      if (!flag)
        break; // 没有数据交换，提前退出
    }
  }

  /**
   * mvn test -Dtest=BubbleSortTest
   */
  @Test
  public void testBubbleSort() {
    int[] input = {4, 2, 9, 6, 23, 12, 34, 0, 1};
    bubbleSort(input, input.length);
    int[] sorted = {0, 1, 2, 4, 6, 9, 12, 23, 34};
    assertArrayEquals(input, sorted);
  }
}

