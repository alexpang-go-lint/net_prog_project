
/*  Student:    Trung Nguyen
    Email:      tnguyen2013@my.fit.edu
    Course:     CSE 4232
    Project:    GOSSIP P2P, Milestone 4 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;

public class NPServer {
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
            path = path.substring(0, path.length()-6) + ("data/");
            //System.out.println(path);
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            System.out.println("Invalid path: " + e);
            System.exit(0);
        }

        return path;

    }
	public static void main(final String[] args) throws IOException, InterruptedException {

		int port = 3333;
		String path = "";
		final String addr = "127.0.0.1";
		String connectionType = "TCP";
		final CommandLineParser parser = new DefaultParser();
		final GET_OPT_SERVER opt = new GET_OPT_SERVER(port, path);
		opt.getOpt(parser, args);
		port = opt.getPort();
		path = opt.getPath();
        System.out.println("Server started at port: " + port
                            +"\nData file path: " + path);
        // create thread 1 listen for TCP 1 listen to UDP
        final Thread tcp = new TCPThread(port, path, 5000);
        final Thread udp = new UDPServer(port, path, 5000);
        final Thread client = new thread_client(connectionType, addr, port);
        // start the thread
        tcp.start();
        udp.start();
        client.start();
        tcp.join();
        udp.join();
	}
}
