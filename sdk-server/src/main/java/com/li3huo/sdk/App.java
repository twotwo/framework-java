package com.li3huo.sdk;

import java.io.FileInputStream;
import java.lang.management.ManagementFactory;
import java.util.Properties;
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

	/** 确保voucher文件的唯一性 */
	static String pid;
	static AtomicLong voucherIndex = new AtomicLong();

	public static String getVoucherFileIndex() {
		long index = voucherIndex.addAndGet(1);
		return pid + "_" + index;
	}

	public static String getProperty(String key, String defaultValue) {
		return games.getProperty(key.toLowerCase(), defaultValue);
	}

	private static synchronized Options getOptions() {
		if (null == options) {

			options = new Options();
			options.addOption("help", "print this message");
			options.addOption("version", "print the version information and exit");
			// -s or --server [port]
			options.addOption(Option.builder("s").longOpt("server").hasArg().argName("port").optionalArg(true)
					.desc("start sdk-agent server on [port]. default is 8000").build());
			options.addOption(Option.builder("n").longOpt("notify").hasArg().argName("game_id")
					.desc("notify voucher to game_id. load game config from conf/games.properties").build());
		}

		return options;
	}

	private static CommandLine parseCommandLine(String[] args) {
		Options options = getOptions();

		CommandLineParser parser = new DefaultParser();

		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
			// logger.debug("options: " + cmd.getOptions().length);
			// logger.debug("args: " + cmd.getArgList().size());
			return cmd;
		} catch (ParseException e) {
			logger.fatal("parseCommandLine(): failed to parse options!", e);
			System.exit(-1);
			return null; // never return null
		}
	}

	public static void initConfig(String file) throws Exception {
		games.load(new FileInputStream(file));
		/** 拿一下当前的进程ID，生成支付通知文件用 */
		pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
	}

	public static void main(String[] args) throws Exception {

		CommandLine cmd = parseCommandLine(args);
		logger.warn("load commandline...");
		// 可以改成也根据参数加载
		String file = "conf/games.properties";
		initConfig(file);
		logger.warn("load conf/games.properties...");
		if (StringUtils.isNotBlank(games.getProperty("agent.debug"))) {
			logger.warn("enable debug mode...");
			logger.warn("current process id = " + pid);
		}

		if (0 == cmd.getOptions().length + cmd.getArgList().size() || cmd.hasOption("help")) {
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("help", options);
			return;
		}

		if (cmd.hasOption("version")) {
			// @TODO 与Build Number Maven Plugin集成
			return;
		}

		if (cmd.hasOption("server")) {
			String port = cmd.getOptionValue("server", "8000");
			logger.warn("start netty server at port " + port);
			new NettyServer(NumberUtils.toInt(port, 8000)).run();
		}

		if (cmd.hasOption("notify")) {
			String game_id = cmd.getOptionValue("notify");
			logger.warn("starting notify process[" + game_id + "]...");
			logger.warn("load from " + games.getProperty(game_id + ".game.pay.dir") + " to "
					+ games.getProperty(game_id + ".game.pay.url"));
			new Thread(new GameNotifier(game_id)).start();

		}

	}
}
