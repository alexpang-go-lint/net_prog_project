/*
 * Student:    Trung Nguyen, Yat Shing Pang
 * Email:      tnguyen2013@my.fit.edu, apang2013@my.fit.edu
 * Course:     CSE 4232
 * Project:    GOSSIP P2P, Milestone 4
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;

public class UI {

	static String getPath() {
		String path = null;
		Process p;
		try {
			p = Runtime.getRuntime().exec("pwd");
			final InputStream stdout = p.getInputStream();
			final BufferedReader out = new BufferedReader(new InputStreamReader(stdout));
			final BufferedReader outerr = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			p.waitFor();
			path = out.readLine();
			// Truncate the "bin/"
            		path = path.substring(0, path.length()-3) + ("data/");
            		//System.out.println(path);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("Invalid path: " + e);
			System.exit(0);
		}

		return path;

	}

	public static void main(String[] args) throws IOException, InterruptedException{

		final CommandLineParser parser = new DefaultParser();
        GET_OPT_UI opt = new GET_OPT_UI();
        String message = "";
        String ip = "";
        int port = 0;
        String path = "";
        String protocol = "";

        // Handles the bug where the message is enclosed in double quotes in bash script
        // The double quotes will get truncated when it is passed to the java program
        // e.g. -m "Tom eats Jerry" as an argument in bash will become
        //      -m Tom eats Jerry
        // Java will then treat the Tom eats Jerry as separate arguments instead of one
        // Reconstruct all the command line arguments and put message back in quotes
        for (int i = 0; i < args.length; i++) {
            if (args[i].contains("-m")) {

                ArrayList<String> newArgs = new ArrayList<String>();

                for (int n = 0; n <= i; n++) {
                    // Add all existing arguments (server ip and port and -m)
                    newArgs.add(args[n]);

                }

                // At the message
                int n = i+1;
                String wholeMsg = "\"";

                while (n < (args.length) && !args[n].equals("-t")) {
                    // Concat the separated message
                    wholeMsg += args[n] + " ";

                    n++;
                }
                // Trim extra white space at the end
                wholeMsg = wholeMsg.trim();
                wholeMsg += "\"";    // Enclose

                // Add as whole argument
                newArgs.add(wholeMsg);

                // Add the rest of the arguments (timestamp)
                while (n < args.length) {
                    newArgs.add(args[n]);
                    n++;
                }
                // Convert back to string array
                args = newArgs.toArray(new String[newArgs.size()]);
            }
        }

		opt.getOpt(parser, args);
		port = opt.getPort();
		path = opt.getPath();
		protocol = opt.getProtocol();

		if(protocol.equals("")){
			protocol = "TCP";
		}

		// No path for data directory specified, use default
		if (path.equals("")) {
			//path = "C:/Users/43kon4251/Documents/";
			path = getPath();
		    System.out.println("No data file path specified, using default path.");
	    }
        System.out.println("Server started at port: " + port
                            +"\nData file path: " + path);
		// create thread 1 listen for TCP 1 listen to UDP
		final Thread tcp = new TCPThread(port, path);
		final Thread udp = new UDPServer(port, path);
		final Thread client = new thread_client(protocol, ip, port);
		// start the thread
		tcp.start();
		udp.start();
		client.start();
		tcp.join();
		udp.join();

	}

}
