
/*
 * Student:    Trung Nguyen, Yat Shing Pang
 * Email:      tnguyen2013@my.fit.edu, apang2013@my.fit.edu
 * Course:     CSE 4232
 * Project:    GOSSIP P2P, Milestone 5
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

	public thread_client(final String type, final String addr, final int port) {

		this.protocol = type;
		this.addr = addr;
		this.port = port;

	}

	@Override
	public void run() {
		if (protocol.equals("TCP")) {
			try {

				skt = new Socket(addr, port);
				stdin = new BufferedReader(new InputStreamReader(System.in));
				final OutputStream out = skt.getOutputStream();
				final InputStream in = skt.getInputStream();

				int numByte = in.read(serverRes);

				// First process the message (if included in the options)
				System.out.println((new String(serverRes, 0, numByte, "latin1")));
				/*
				 * if (!opt.getMsg().isEmpty() && !opt.getMsg().equals("") &&
				 * !opt.getMsg().equals(null)) { out.write((message +
				 * "\n").getBytes("latin1")); numByte = in.read(serverRes);
				 * System.out.println("S: " + (new String(serverRes, 0, numByte,
				 * "latin1"))); }
				 */

				// Start looping for user input
				for (;;) {
					boolean isQuery = false;
					userIn = stdin.readLine();
					if (!userIn.contains("PEER") && !userIn.contains("PEERS?") && !userIn.contains("GOSSIP:")
							&& !userIn.contains("LEAVE:")) {
						// User input only has message
						// Construct GOSSIP:...:...:...
						userIn = getGOSSIP(userIn, "");
					}

					// Encode
					if (userIn.contains("GOSSIP:")) {

						final String[] str = userIn.split(":");
						final Gossip g = new Gossip(str[3], str[1], str[2]);
						final Encoder e = g.getEncoder();
						userIn = new String(e.getBytes());

					} else if (userIn.contains("PEER:")) {

						final String[] str = userIn.split(":");
						final String ip = str[3].substring(3);
						final int prt = Integer.parseInt(str[2].substring(5));
						final Peer p = new Peer(str[1], ip, prt);
						final Encoder e = p.getEncoder();
						userIn = new String(e.getBytes());

					} else if (userIn.contains("PEERS?")) {
						// Encodes in null
						final PeersQuery pq = new PeersQuery();

						final Encoder e = pq.getEncoder();
						userIn = new String(e.getBytes());
						isQuery = true;
					} else if (userIn.contains("LEAVE")) {
						// Encodes in null
						final String[] str = userIn.split(":");
						final String name = str[1];
						final Leave leave_user = new Leave(name);

						final Encoder e = leave_user.getEncoder();
						userIn = new String(e.getBytes());
					}

					out.write((userIn + "\n").getBytes("latin1"));
					numByte = in.read(serverRes);

					if (isQuery) {
						// Decode Peers? response
						final Decoder d = new Decoder(serverRes);
						final ArrayList<Peer> list = new ArrayList<Peer>();
						PeersAnswer decoded_pa = new PeersAnswer(list);
						try {
							decoded_pa = new PeersAnswer().decode(d);

							String toPrint = "PEERS|" + decoded_pa.peers.size() + "|";
							for (int i = 0; i < decoded_pa.peers.size(); i++) {
								toPrint += decoded_pa.peers.get(i).name + ":" + "PORT=" + decoded_pa.peers.get(i).port
										+ ":" + "IP=" + decoded_pa.peers.get(i).ip_addr + "|";
							}

							toPrint += "%";
							System.out.println(toPrint);
						} catch (final ASN1DecoderFail e1) {

						}

					} else {
						System.out.println((new String(serverRes, 0, numByte, "latin1")));
					}
				}
			} catch (final IOException e) {
				System.err.println(e);
			}
		} else {
			try {
				boolean isQuery = false;

				final DatagramSocket UDPSocket = new DatagramSocket();
				final Scanner sc = new Scanner(System.in);
				final InetAddress host = InetAddress.getByName(addr);
				final DatagramPacket recv = new DatagramPacket(serverRes, MSGSIZE);

				System.out.println("Enter the message: ");
				userIn = sc.nextLine();
				if (!userIn.contains("PEER") && !userIn.contains("PEERS?") && !userIn.contains("GOSSIP:")) {
					// User input only has message
					// Construct GOSSIP:...:...:...
					userIn = getGOSSIP(userIn, "");
				}

				// Encode
				if (userIn.contains("GOSSIP:")) {

					final String[] str = userIn.split(":");
					final Gossip g = new Gossip(str[3], str[1], str[2]);
					final Encoder e = g.getEncoder();
					userIn = new String(e.getBytes());

				} else if (userIn.contains("PEER:")) {

					final String[] str = userIn.split(":");
					final String ip = str[3].substring(3);
					final int prt = Integer.parseInt(str[2].substring(5));
					final Peer p = new Peer(str[1], ip, prt);
					final Encoder e = p.getEncoder();
					userIn = new String(e.getBytes());

				} else if (userIn.contains("PEERS?")) {
					// Encodes in null
					final PeersQuery pq = new PeersQuery();

					final Encoder e = pq.getEncoder();
					userIn = new String(e.getBytes());
					isQuery = true;
				} else if (userIn.contains("LEAVE:")) {
					// Encodes in null
					final String[] str = userIn.split(":");
					final String name = str[1];
					final Leave leave_user = new Leave(name);

					final Encoder e = leave_user.getEncoder();
					userIn = new String(e.getBytes());
				}

				final byte[] msg = userIn.getBytes("latin1");

				final DatagramPacket packet = new DatagramPacket(msg, msg.length);
				UDPSocket.connect(host, port);
				UDPSocket.send(packet);
				if (isQuery) {
					// Try decoding the response
					UDPSocket.receive(recv);
					final byte[] b = recv.getData();

					final Decoder d = new Decoder(b);
					if(d.tagVal() == 1){
						try {
							Gossip decoded_gossip = new Gossip().decode(d);
							
							String toPrint = "GOSSIP:" + decoded_gossip.SHA_256 + ":" + decoded_gossip.str_date + ":" + decoded_gossip.msg + "%";
							System.out.println(toPrint);
						} catch (ASN1DecoderFail e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else {
						final ArrayList<Peer> list = new ArrayList<Peer>();
						PeersAnswer decoded_pa = new PeersAnswer(list);
						try {
							decoded_pa = new PeersAnswer().decode(d);
	
							String toPrint = "PEERS|" + decoded_pa.peers.size() + "|";
							for (int i = 0; i < decoded_pa.peers.size(); i++) {
								toPrint += decoded_pa.peers.get(i).name + ":" + "PORT=" + decoded_pa.peers.get(i).port + ":"
										+ "IP=" + decoded_pa.peers.get(i).ip_addr + "|";
							}
	
							toPrint += "%";
							System.out.println(toPrint);
	
						} catch (final ASN1DecoderFail e1) {
	
						}
					}

				} else {
					while (true) {
						UDPSocket.receive(recv);

						System.out.println(new String(recv.getData(), 0, recv.getLength()));
					}
				}
				sc.close();
				UDPSocket.close();
			} catch (final IOException e) {
				System.err.println("===== " + e);
			}
		}
	}

	public static String getGOSSIP(final String msg, String d) {
		final Runtime rt = Runtime.getRuntime();
		final String time_pattern = "yyyy-MM-dd-hh-mm-ss-SSS'Z'";

		final SimpleDateFormat df = new SimpleDateFormat(time_pattern);
		final Date date = new Date();

		if (d.equals("")) {
			d = df.format(date);
		}

		// SHA-256 encoding
		final String[] commands = { "/bin/sh", "-c",
				"echo -n \"" + d + ":" + msg + "\"| sha256sum -b | cut -f 1 -d ' '| xxd -r -p | uuencode -m -" };
		Process proc = null;
		try {
			proc = rt.exec(commands);
			proc.waitFor();
		} catch (final IOException e1) {
			System.out.println(e1);
		} catch (final InterruptedException e) {
			System.out.println(e);
		}
		final BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

		// read the output from the command and concat everything
		String s = "GOSSIP:";
		try {
			stdInput.readLine();
			s = s + stdInput.readLine() + ":";
		} catch (final IOException e) {
			System.out.println(e);
		}
		s += d + ":" + msg + "%";

		return s;
	}
}
