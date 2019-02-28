# 算法题目整理

## 题目参考

- [程序员面试最常见问题TOP 48](https://mp.weixin.qq.com/s/XikCzXNdO3ZO8Bp3-Gt-MA)
- [Top 50 Java Programs from Coding Interviews](https://javarevisited.blogspot.com/2017/07/top-50-java-programs-from-coding-Interviews.html)
- [Java Interview Programs](http://www.java2novice.com/java-interview-programs/)

## Create Project

```bash
➜  interview-algorithm git:(master) ✗ mvn archetype:generate \
  -DarchetypeGroupId=org.apache.maven.archetypes \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DarchetypeVersion=RELEASE \
  -DgroupId=com.li3huo.algo \
  -DartifactId=interview-algorithm \
  -Dversion=1.0-SNAPSHOT \
  -Dpackage=com.li3huo.algo \
  -DinteractiveMode=false
```

## Programs

- 冒泡算法 `mvn test -Dtest=BubbleSortTest`
- 整数颠倒位置 `mvn test -Dtest=NumberReverseTest` 
