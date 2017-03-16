package com.li3huo.sdk;

import java.io.FileInputStream;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	static Properties games = new Properties();

	public static String getProperty(String key, String defaultValue) {
		return games.getProperty(key, defaultValue);
	}

	private static synchronized Options getOptions() {
		if (null == options) {

			Option help = new Option("help", "print this message");
			Option version = new Option("version", "print the version information and exit");
			// https://tools.ietf.org/html/rfc868
			// Option server = new Option("server", "-server <8000>\n\t\tstart a
			// netty server. default on 8000");
			Option server = Option.builder("s").longOpt("server").hasArg().argName("port").optionalArg(true)
					.desc("start a netty server on <port>. default is 8000").build();
			// server.setDescription("-server <8000>\n\t\tstart a netty server.
			// default on 8000");

			options = new Options();
			options.addOption(help);
			options.addOption(server);
			options.addOption(version);
			options.addOption("t", false, "display current time");
		}

		return options;
	}

	private static CommandLine parseCommandLine(String[] args) {
		Options options = getOptions();

		CommandLineParser parser = new DefaultParser();

		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
			logger.debug("options: " + cmd.getOptions().length);
			logger.debug("args: " + cmd.getArgList().size());
			return cmd;
		} catch (ParseException e) {
			logger.fatal("parseCommandLine(): failed to parse options!", e);
			System.exit(-1);
			return null; // never return null
		}
	}

	public static void initConfig(String file) throws Exception {
		games.load(new FileInputStream(file));
	}

	public static void main(String[] args) throws Exception {
		CommandLine cmd = parseCommandLine(args);
		logger.warn("load commandline...");
		//可以改成也根据参数加载
		String file = "conf/games.properties";
		initConfig(file);
		logger.warn("load conf/games.properties...");
		if (StringUtils.isNotBlank(games.getProperty("agent.debug"))) {
			logger.warn("enable debug mode...");
		}

		if (0 == cmd.getOptions().length + cmd.getArgList().size() || cmd.hasOption("help")) {
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("help", options);
			return;
		}

		if (cmd.hasOption("server")) {
			String port = cmd.getOptionValue("server", "8000");
			logger.warn("start netty server at port " + port);
			new NettyServer(NumberUtils.toInt(port, 8000)).run();
		}

		if (cmd.hasOption("version")) {
			// @TODO 与Build Number Maven Plugin集成
			return;
		}

		if (cmd.hasOption("t")) {
			// print the date and time
			System.out.println(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		} else {
			// print the date
			System.out.println(DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
		}
	}
}
