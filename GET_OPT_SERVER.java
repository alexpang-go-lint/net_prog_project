/*
 * Student:    Trung Nguyen, Yat Shing Pang
 * Email:      tnguyen2013@my.fit.edu, apang2013@my.fit.edu
 * Course:     CSE 4232
 * Project:    GOSSIP P2P, Milestone 4
*/

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

public class GET_OPT_SERVER {

    private static int port;
    private static String path;

    public GET_OPT_SERVER(int port, String path){
        this.port = port;
        this.path = path;
    }

    public void getOpt(CommandLineParser parser, String[] userIn){

        Options options = new Options();
        Option op_port = Option.builder("p")
                                       .longOpt("port")
                                       .desc("port")
                                       .hasArg()
                                       .valueSeparator()
                                       .build();
        Option dir = Option.builder("d")
                                       .longOpt("directory")
                                       .desc("directory")
                                       .hasArg()
                                       .valueSeparator()
                                       .build();
        Option help = Option.builder("h")
                                    .longOpt("help")
                                    .build();

        options.addOption(op_port);
        options.addOption(dir);
        options.addOption(help);


        try {
            CommandLine input = parser.parse(options, userIn);

            HelpFormatter formatter = new HelpFormatter();

            for (Option option : input.getOptions()) {

                if (option.getOpt().equals("h")) {
                    formatter.printHelp("Usage:", options);
                } else if (option.getOpt().equals("p")) {
                    System.out.println("Set port to: "+ option.getValue());
                    port = Integer.parseInt(option.getValue());
                } else if (option.getOpt().equals("d")) {
                    System.out.println("Set directory to: "+ option.getValue());
                    path = option.getValue();
                }
            }

        } catch( ParseException exp ) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }
    }

    public int getPort(){return port;}

    public String getPath(){return path;}



    public static void main(String [] args) {

    	CommandLineParser parser = new DefaultParser();
        //getOpt(parser, args);

        System.out.println(port);
        System.out.println(path);
    }
}
