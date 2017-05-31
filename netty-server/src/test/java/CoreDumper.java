/**
 * Create at Feb 20, 2014
 */


/**
 * @author liyan
 * 
 * refer to http://fahdshariff.blogspot.co.uk/2012/08/generating-java-core-dump.html
 * 
 * 1. javac com/li3huo/util/CoreDumper.java 
 * 2. javah -jni com.li3huo.util.CoreDumper
 * 3. create CoreDumper.c
 * 4. compile
 * 4.1 gcc -fPIC -o libnativelib.so -shared -I$JAVA_HOME/include/linux/ -I$JAVA_HOME/include/ CoreDumper.c
 * 4.2 mac os x
 * gcc -fPIC -o libnativelib.o -shared -I/System/Library/Frameworks/JavaVM.framework/Headers/ CoreDumper.c
 * gcc -dynamiclib -o libnativelib.jnilib libnativelib.o
 * 5. run
 * java -Djava.library.path=. com.li3huo.util.CoreDumper
 * 6. core dump file
 * 6.1 linux
 * 6.2 mac os x
 * ulimit -c unlimited
 * $ java -Djava.library.path=.  CoreDumper
 * Invalid memory access of location 0x0 rip=0x113329f60
 * Segmentation fault: 11 (core dumped)
 * 7. dump file location
 * 7.1 linux
 * 7.2 mac
 * $ ll /cores/core.2423 
-r--------  1 liyan  admin  849743872 Feb 20 19:27 /cores/core.2423
 * 
 */
public class CoreDumper {

	// load the library
	static {
		System.loadLibrary("nativelib");
	}

	// native method declaration
	public native void core();

	public static void main(String[] args) {
		new CoreDumper().core();
	}

}
