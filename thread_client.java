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
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class thread_client extends Thread {

	private final String protocol;
	private final String addr;
	private final int port;

	final private static int MSGSIZE = 1000;
    static byte[] serverRes = new byte[MSGSIZE];
    private static String userIn = "";
    private static Socket skt;
    private static BufferedReader stdin;

	public thread_client(String type, String addr, int port){

		this.protocol = type;
		this.addr = addr;
		this.port = port;

	}

	@Override
	public void run() {
		if(protocol.equals("TCP")){
        	try{

                skt = new Socket(addr, port);
                stdin = new BufferedReader( new InputStreamReader(System.in));
                final OutputStream out = skt.getOutputStream();
                final InputStream in = skt.getInputStream();

                int numByte = in.read(serverRes);

                // First process the message (if included in the options)
                System.out.println((new String(serverRes, 0 , numByte, "latin1")));
                /*if (!opt.getMsg().isEmpty() && !opt.getMsg().equals("") && !opt.getMsg().equals(null)) {
                    out.write((message + "\n").getBytes("latin1"));
                    numByte = in.read(serverRes);
                    System.out.println("S: " + (new String(serverRes, 0, numByte, "latin1")));
                }*/

                // Start looping for user input
                for(;;){
                    boolean isQuery = false;
                    userIn = stdin.readLine();
                    if (!userIn.contains("PEER") && !userIn.contains("PEERS?") && !userIn.contains("GOSSIP:")) {
                        // User input only has message
                        // Construct GOSSIP:...:...:...
                        userIn = getGOSSIP(userIn, "");
                    }

                    // Encode
                    if(userIn.contains("GOSSIP:")){

                    	String[] str = userIn.split(":");
                    	Gossip g = new Gossip(str[3], str[1], str[2]);
                    	Encoder e = g.getEncoder();
                    	userIn = new String(e.getBytes());

                    }else if(userIn.contains("PEER:")){

                    	String[] str = userIn.split(":");
                    	String ip = str[3].substring(3);
                    	int prt = Integer.parseInt(str[2].substring(5));
                    	Peer p = new Peer(str[1], ip, prt);
                    	Encoder e = p.getEncoder();
                    	userIn = new String(e.getBytes());

                    }else if(userIn.contains("PEERS?")){
                        // Encodes in null
                        PeersQuery pq = new PeersQuery();

                        Encoder e = pq.getEncoder();
                        userIn = new String(e.getBytes());
                        isQuery = true;
                    }
                    else if(userIn.contains("LEAVE")){
                        // Encodes in null
                    	String[] str = userIn.split(":");
                    	String name = str[1];
                        Leave leave_user = new Leave(name);

                        Encoder e = leave_user.getEncoder();
                        userIn = new String(e.getBytes());
                    }


                    out.write((userIn + "\n").getBytes("latin1"));
                    numByte = in.read(serverRes);

                    if (isQuery) {
                        // Decode Peers? response
                        Decoder d = new Decoder(serverRes);
                        ArrayList<Peer> list = new ArrayList<Peer>();
                        PeersAnswer decoded_pa = new PeersAnswer(list);
                        try {
                            decoded_pa = new PeersAnswer().decode(d);

                            String toPrint = "PEERS|" + decoded_pa.peers.size() + "|";
                            for (int i = 0; i < decoded_pa.peers.size(); i++) {
                                toPrint += decoded_pa.peers.get(i).name +
                                        ":" + "PORT=" + decoded_pa.peers.get(i).port +
                                        ":" + "IP=" + decoded_pa.peers.get(i).ip_addr + "|";
                            }

                            toPrint += "%";
                            System.out.println(toPrint);
                        } catch (ASN1DecoderFail e1) {

                        }

                    } else {
                        System.out.println((new String(serverRes, 0, numByte, "latin1")));
                    }
                }
              }catch(final IOException e){
                  System.err.println(e);
            }
        }else{
        	try {
        	    boolean isQuery = false;

                DatagramSocket UDPSocket =new DatagramSocket();
    			Scanner sc = new Scanner(System.in);
    			InetAddress host = InetAddress.getByName(addr);
    			DatagramPacket recv = new DatagramPacket(serverRes, MSGSIZE);

                System.out.println("Enter the message: ");
                userIn = sc.nextLine();
				if (!userIn.contains("PEER") && !userIn.contains("PEERS?") && !userIn.contains("GOSSIP:")) {
                    // User input only has message
                    // Construct GOSSIP:...:...:...
                    userIn = getGOSSIP(userIn, "");
                }


    			 // Encode
                if(userIn.contains("GOSSIP:")){

                    String[] str = userIn.split(":");
                    Gossip g = new Gossip(str[3], str[1], str[2]);
                    Encoder e = g.getEncoder();
                    userIn = new String(e.getBytes());

                }else if(userIn.contains("PEER:")){

                    String[] str = userIn.split(":");
                    String ip = str[3].substring(3);
                    int prt = Integer.parseInt(str[2].substring(5));
                    Peer p = new Peer(str[1], ip, prt);
                    Encoder e = p.getEncoder();
                    userIn = new String(e.getBytes());

                }else if(userIn.contains("PEERS?")){
                    // Encodes in null
                    PeersQuery pq = new PeersQuery();

                    Encoder e = pq.getEncoder();
                    userIn = new String(e.getBytes());
                    isQuery = true;
                }

                byte[] msg = userIn.getBytes("latin1");



    			DatagramPacket packet = new DatagramPacket(msg, msg.length);
    		    UDPSocket.connect(host, port);
    		    UDPSocket.send(packet);
    		    String serverResponse = "";
    		    if (isQuery) {
    		        // Try decoding the response
    		        UDPSocket.receive(recv);
                    byte[] b = recv.getData();

                    Decoder d = new Decoder(b);
                    ArrayList<Peer> list = new ArrayList<Peer>();
                    PeersAnswer decoded_pa = new PeersAnswer(list);
                    try {
                        decoded_pa = new PeersAnswer().decode(d);

                        String toPrint = "PEERS|" + decoded_pa.peers.size() + "|";
                        for (int i = 0; i < decoded_pa.peers.size(); i++) {
                            toPrint += decoded_pa.peers.get(i).name +
                                    ":" + "PORT=" + decoded_pa.peers.get(i).port +
                                    ":" + "IP=" + decoded_pa.peers.get(i).ip_addr + "|";
                        }

                        toPrint += "%";
                        System.out.println(toPrint);

                    } catch (ASN1DecoderFail e1) {

                    }

                } else {
                    while (true) {
                        UDPSocket.receive(recv);

                        System.out.println(new String(recv.getData(), 0, recv
                                .getLength()));
                    }
                }


    		} catch (final IOException e) {
    			System.err.println("===== " + e);
    		}
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
