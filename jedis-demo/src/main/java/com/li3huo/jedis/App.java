package com.li3huo.jedis;

import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 
 * @author liyan
 * 
 *         use commoms-cli for Commandline Parser
 */
public class App {

	private static Options options;

	private static synchronized Options getOptions() {
		if (null == options) {

			Option help = new Option("help", "print this message");
			Option version = new Option("version", "print the version information and exit");

			options = new Options();
			options.addOption(help);
			options.addOption(version);
			options.addOption("t", false, "display current time");
		}

		return options;
	}

	private static void parseCommandLine(String[] args) {
		Options options = getOptions();

		CommandLineParser parser = new DefaultParser();

		try {
			CommandLine cmd = parser.parse(options, args);

			// System.out.println("options: "+cmd.getOptions().length);
			// System.out.println("args: "+cmd.getArgList().size());

			if (0 == cmd.getOptions().length + cmd.getArgList().size() || cmd.hasOption("help")) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("help", options);
				return;
			}
			
			if (cmd.hasOption("version")) {
				//@TODO 与Build Number Maven Plugin集成
				return;
			}

			if (cmd.hasOption("t")) {
				// print the date and time
				System.out.println(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
			} else {
				// print the date
				System.out.println(DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
			}

		} catch (ParseException ex) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + ex.getMessage());

		}

	}

	public static void main(String[] args) {
		parseCommandLine(args);
	}
}
