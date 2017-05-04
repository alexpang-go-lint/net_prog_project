
/*
 * Student:    Trung Nguyen, Yat Shing Pang
 * Email:      tnguyen2013@my.fit.edu, apang2013@my.fit.edu
 * Course:     CSE 4232
 * Project:    GOSSIP P2P, Milestone 5
*/

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class GET_OPT_SERVER {

	private static int port;
	private static String path;

	public GET_OPT_SERVER(final int port, final String path) {
		GET_OPT_SERVER.port = port;
		GET_OPT_SERVER.path = path;
	}

	public void getOpt(final CommandLineParser parser, final String[] userIn) {

		final Options options = new Options();
		final Option op_port = Option.builder("p").longOpt("port").desc("port").hasArg().valueSeparator().build();
		final Option dir = Option.builder("d").longOpt("directory").desc("directory").hasArg().valueSeparator().build();
		final Option help = Option.builder("h").longOpt("help").build();

		options.addOption(op_port);
		options.addOption(dir);
		options.addOption(help);

		try {
			final CommandLine input = parser.parse(options, userIn);

			final HelpFormatter formatter = new HelpFormatter();

			for (final Option option : input.getOptions()) {

				if (option.getOpt().equals("h")) {
					formatter.printHelp("Usage:", options);
				} else if (option.getOpt().equals("p")) {
					System.out.println("Set port to: " + option.getValue());
					port = Integer.parseInt(option.getValue());
				} else if (option.getOpt().equals("d")) {
					System.out.println("Set directory to: " + option.getValue());
					path = option.getValue();
				}
			}

		} catch (final ParseException exp) {
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
		}
	}

	public int getPort() {
		return port;
	}

	public String getPath() {
		return path;
	}

}
