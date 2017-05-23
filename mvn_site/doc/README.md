# maven site sample

## Reference
 * [maven-site-plugin](https://maven.apache.org/plugins/maven-site-plugin/)
 * [cobertura-maven-plugin](http://www.mojohaus.org/cobertura-maven-plugin/)
 * []()

## Create Project

	➜  framework-java git:(master) ✗ mvn -B archetype:generate    \
	  -DarchetypeGroupId=com.li3huo                \
	  -DarchetypeArtifactId=mvn-archetype-server   \
	  -DarchetypeVersion=0.1           	           \
	  -DgroupId=com.li3huo.maven                   \
	  -DartifactId=mvn_site
	➜  framework-java git:(master) ✗ cd mvn_site
	➜  mvn_site git:(master) ✗ mvn eclipse:eclipse
	
## Gen & View Site

	➜  mvn_site git:(master) ✗ mvn site
	➜  mvn_site git:(master) ✗ open target/site/index.html