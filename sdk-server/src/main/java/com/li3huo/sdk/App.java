package com.li3huo.sdk;

import java.io.FileInputStream;
import java.lang.management.ManagementFactory;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.li3huo.sdk.notify.GameNotifier;
import com.li3huo.service.NettyServer;

/**
 * 应用CLI接口以及服务容器
 * 
 * @author liyan
 * 
 *         use commoms-cli for Commandline Parser
 */
public class App {

	static final Logger logger = LogManager.getLogger(App.class.getName());
	static Options options;

	/** 游戏配置参数 */
	static Properties games = new Properties();

	/** 与Build Number Maven Plugin集成, 在打包时把版本信息更新到assemble/build.properties */
	static Properties buildInfo = new Properties();

	public static String getProperty(String key, String defaultValue) {
		return games.getProperty(key.toLowerCase(), defaultValue);
	}

	/** voucher文件编号，确保本机唯一性 */
	static String pid;
	static AtomicLong voucherIndex = new AtomicLong();

	public static String getVoucherFileIndex() {
		long index = voucherIndex.addAndGet(1);
		return pid + "_" + index;
	}

	/** 业务线程池 */
	static int maximumPoolSize = 5000; // max working numbers
	static BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(maximumPoolSize);
	static ThreadPoolExecutor executor; // the executor

	/**
	 * 容器初始化
	 * 
	 * @param file
	 * @throws Exception
	 */
	public static void initConfig(String file) throws Exception {
		/** 加载全局配置文件 */
		games.load(new FileInputStream(file));

		/** 拿一下当前的进程ID，生成支付通知文件用 */
		pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

		/** 初始化版本信息 */
		buildInfo.load(App.class.getResourceAsStream("/assemble/build.properties"));
	}

	/**
	 * 执行业务逻辑
	 * 
	 * @param job
	 */
	public static void execute(Runnable job) {
		executor.execute(job);
	}

	private static CommandLine parseCommandLine(String[] args) {
		/** 初始化请求参数 */
		options = new Options();
		options.addOption("help", "print this message");
		options.addOption("version", "print the version information and exit");
		// -s or --server [port]
		options.addOption(Option.builder("s").longOpt("server").hasArg().argName("port").optionalArg(true)
				.desc("start sdk-agent server on [port]. default is 8000").build());
		// -n 500006 or --notify <game_id>
		options.addOption(Option.builder("n").longOpt("notify").hasArg().argName("game_id")
				.desc("notify voucher to game_id. load game config from conf/games.properties").build());
		// -c or --channel [port]
		options.addOption(Option.builder("c").longOpt("channel").hasArg().argName("port").optionalArg(true)
				.desc("start mock channel server on [port]. default is 8008").build());
		// -f or --file [file]
		options.addOption(Option.builder("f").longOpt("file").hasArg().argName("config").optionalArg(true)
				.desc("load the configuration file. default is conf/games.properties").build());
		CommandLineParser parser = new DefaultParser();

		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
			return cmd;
		} catch (ParseException e) {
			System.exit(-1);
			return null; // never return null
		}
	}

	public static void main(String[] args) throws Exception {

		CommandLine cmd = parseCommandLine(args);

		// 可以改成也根据参数加载
		String file = "conf/games.properties";
		if (cmd.hasOption("file")) {
			file = cmd.getOptionValue("file", "conf/games.properties");
		}

		initConfig(file);
		/** 获取对应git上的版本信息 */
		String appVersion = buildInfo.getProperty("scm.version", "Unknow");
		logger.warn("load {}... appVersion={}", file, appVersion);

		if (StringUtils.isNotBlank(games.getProperty("agent.debug"))) {
			logger.warn("enable debug mode...");
			logger.warn("current process id = " + pid);
		}

		if (cmd.hasOption("server")) {
			// io.netty.eventLoopThreads
			String port = cmd.getOptionValue("server", "8000");
			logger.warn("start agent server[{}] at port ={}", appVersion, port);
			new NettyServer(NumberUtils.toInt(port, 8000)).run();
		}

		if (cmd.hasOption("notify")) {
			String game_id = cmd.getOptionValue("notify");
			logger.warn("start notify process[{}] game_id ={}, pay_dir={}", appVersion, game_id,
					games.getProperty(game_id + ".game.pay.dir"));
			new GameNotifier(game_id);
		}

		if (cmd.hasOption("channel")) {
			games.setProperty("sim", "true");
			String port = cmd.getOptionValue("channel", "8008");
			logger.warn("start mock channel server[{}] at port ={}", appVersion, port);
			new NettyServer(NumberUtils.toInt(port, 8008)).run();
		}

		if (0 == cmd.getOptions().length + cmd.getArgList().size() || cmd.hasOption("help")) {
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("help", options);
		}

		if (cmd.hasOption("version")) {
			/**
			 * 与Build Number Maven Plugin集成,
			 * 打包时把版本信息更新到assemble/build.properties
			 */
			Properties buildInfo = new Properties();
			buildInfo.load(App.class.getResourceAsStream("/assemble/build.properties"));
			logger.info("Server Vendor:	" + buildInfo.getProperty("server.vendor", "Unknow"));
			logger.info("Server Version:	" + buildInfo.getProperty("server.version", "Unknow"));
			logger.info("SCM Tag:	" + buildInfo.getProperty("scm.tag", "Unknow"));
			logger.info("SCM Version:	" + buildInfo.getProperty("scm.version", "Unknow"));
			logger.info("Build Number:	" + buildInfo.getProperty("jenkins.number", "Unknow"));
		}

	}
}
