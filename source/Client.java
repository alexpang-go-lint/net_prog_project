/*
 * Student:    Trung Nguyen, Yat Shing Pang
 * Email:      tnguyen2013@my.fit.edu, apang2013@my.fit.edu
 * Course:     CSE 4232
 * Project:    GOSSIP P2P, Milestone 4
*/

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.ArrayList;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;

import java.net.Socket;

/*GOSSIP:mBHL7IKilvdcOFKR03ASvBNX//ypQkTRUvilYmB1/OY=:2017-01-09-16-18-20-001Z:Tom eats Jerry%
*
*/


public class Client {

    final private static int MSGSIZE = 1000;
    static byte[] serverRes = new byte[MSGSIZE];
    private static String userIn;
    private static Socket skt;
    private static BufferedReader stdin;

    public static void main(String[] args){
        final CommandLineParser parser = new DefaultParser();
        GET_OPT_CLIENT opt = new GET_OPT_CLIENT();
        String message = "";
        String ip = "";
        int port = 0;

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

        ip = opt.getIP();
        port = opt.getPort();
        if (!opt.getMsg().isEmpty() && !opt.getMsg().equals("") && !opt.getMsg().equals(null))
            message = getGOSSIP(opt.getMsg(), opt.getTime());


        try{

            skt = new Socket(ip, port);
            stdin = new BufferedReader( new InputStreamReader(System.in));
            final OutputStream out = skt.getOutputStream();
            final InputStream in = skt.getInputStream();

            int numByte = in.read(serverRes);

            // First process the message (if included in the options)
            System.out.println("S: " + (new String(serverRes, 0 , numByte, "latin1")));
            if (!opt.getMsg().isEmpty() && !opt.getMsg().equals("") && !opt.getMsg().equals(null)) {
                out.write((message + "\n").getBytes("latin1"));
                numByte = in.read(serverRes);
                System.out.println("S: " + (new String(serverRes, 0, numByte, "latin1")));
            }

            // Start looping for user input
            for(;;){
                userIn = stdin.readLine();
                if (userIn.equals("break")) {
                    break;

                } else if (!userIn.contains("PEER") && !userIn.contains("PEERS?") && !userIn.contains("GOSSIP:")) {
                    // User input only has message
                    // Construct GOSSIP:...:...:...
                    userIn = getGOSSIP(userIn, "");
                }

                out.write((userIn + "\n").getBytes("latin1"));
                numByte = in.read(serverRes);
                System.out.println("S: " + (new String(serverRes, 0, numByte, "latin1")));
            }
          }catch(final IOException e){
              System.err.println(e);
        }
    }
    public static String getGOSSIP(String msg, String d) {
        Runtime rt = Runtime.getRuntime();
        String time_pattern = "yyyy-MM-dd-hh-mm-ss-SSS'Z'";

        SimpleDateFormat df = new SimpleDateFormat(time_pattern);
        Date date = new Date();

        if (d.equals("")) {
            d = df.format(date);
        }

        // SHA-256 encoding
        String[] commands = {
        "/bin/sh",
        "-c",
        "echo -n \"" + d + ":" + msg + "\"| sha256sum -b | cut -f 1 -d ' '| xxd -r -p | uuencode -m -"
        };
        Process proc = null;
        try {
            proc = rt.exec(commands);
            proc.waitFor();
        } catch (IOException e1) {
            System.out.println(e1);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        BufferedReader stdInput = new BufferedReader(new
             InputStreamReader(proc.getInputStream()));

        // read the output from the command and concat everything
        String s = "GOSSIP:";
        try {
                stdInput.readLine();
                s = s + stdInput.readLine() + ":";
        } catch (IOException e) {
            System.out.println(e);
        }
        s += d + ":" + msg + "%";


        return s;
    }

}
