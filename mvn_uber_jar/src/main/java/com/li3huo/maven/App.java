package com.li3huo.maven;

import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.li3huo.service.TimeServer;

/**
 * 
 * @author liyan
 * 
 *         use
 *         <a href="http://commons.apache.org/proper/commons-cli/usage.html">
 *         commoms-cli</a> for Commandline Parser
 */
public class App {

	static final Logger logger = LogManager.getLogger(App.class.getName());
	/** print for help */
	static Options options;

	/**
	 * 
	 * @return options
	 */
	private static Options initOptions() {

		Options options = new Options();
		options.addOption("help", "print this message");
		options.addOption("version", "print the version information and exit");
		// https://tools.ietf.org/html/rfc868
		// -s or --server [port]
		options.addOption(Option.builder("s").longOpt("server").hasArg().argName("port").optionalArg(true)
				.desc("start sdk-agent server on [port]. default is 8000").build());

		options.addOption("t", false, "display current time");

		return options;

	}

	private static CommandLine parseCommandLine(String[] args) {
		options = initOptions();

		CommandLineParser parser = new DefaultParser();

		try {
			CommandLine cmd = parser.parse(options, args);
			return cmd;
		} catch (ParseException e) {
			logger.fatal("parseCommandLine(): failed to parse options!", e);
			System.exit(-1);
			return null; // never return null
		}
	}

	public static void main(String[] args) throws Exception{
		CommandLine cmd = parseCommandLine(args);
		
		if (0 == cmd.getOptions().length + cmd.getArgList().size() || cmd.hasOption("help")) {
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("help", options);
			return;
		}
		
		if (cmd.hasOption("server")) {
			// io.netty.eventLoopThreads
			String port = cmd.getOptionValue("server", "8000");
			logger.warn("start agent server at port " + port);
			new TimeServer(NumberUtils.toInt(port, 8000)).run();
			return;
		}

		if (cmd.hasOption("version")) {
			// 与Build Number Maven Plugin集成, 打包时把版本信息更新到assemble/build.properties
			Properties buildInfo = new Properties();
			try {
				/**
				 * search "add buildnumber to META-INF/MANIFEST.mf" in pom.xml
				 * 
				 */
				InputStream is = App.class.getResourceAsStream("/assemble/build.properties");
				buildInfo.load(is);
				is.close();
			} catch (Throwable t) {
				System.err.println(t);
			}
			logger.info("Server Vendor:	" + buildInfo.getProperty("server.vendor","Unknow"));
			logger.info("Server Version:	" + buildInfo.getProperty("server.version","Unknow"));
			logger.info("SCM Tag:	" + buildInfo.getProperty("scm.tag","Unknow"));
			logger.info("SCM Version:	" + buildInfo.getProperty("scm.version","Unknow"));
			logger.info("Build Number:	" + buildInfo.getProperty("jenkins.number","Unknow"));
			
			return;
		}

		if (cmd.hasOption("t")) {
			// print the date and time
			logger.info(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		} else {
			// print the date
			logger.info(DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
		}
	}
}
