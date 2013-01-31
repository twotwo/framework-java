/**
 * Create at Jan 31, 2013
 */
package com.li3huo.netty.startup;

import org.apache.log4j.Logger;

/**
 * @author liyan
 * 
 *         Server bootstrap
 */
public class Bootstrap {
	
	private static Logger log = Logger.getLogger(Bootstrap.class.getName());

	/**
	 * Daemon object used by main.
	 */
	private static Server daemon = null;

	private static Server getInstance() {
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (daemon == null) {
			daemon = getInstance();
			try {
				daemon.init();
			} catch (Throwable t) {
				t.printStackTrace();
				return;
			}
		}

		try {
			String command = "start";
			if (args.length > 0) {
				command = args[args.length - 1];
			}

			if (command.equals("start")) {
				// init reflaction, log4j etc...
				// ApplicationConfig.init();
				try {
					daemon.start();
				} catch (Exception e) {
					e.printStackTrace();
					log.fatal(e.getMessage());
					log.fatal("exit program. pls check and restart again.");
					System.exit(0);
				}
			} else if (command.equals("stop")) {
				log.info("Stopping...");
				daemon.stop();
			} else if (command.equals("status")) {
				log.info("Status...");
				daemon.status();
			} else {
				log.warn("Bootstrap: command \"" + command
						+ "\" does not exist.");
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}
