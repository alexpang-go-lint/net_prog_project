
/*
 * Student:    Trung Nguyen, Yat Shing Pang
 * Email:      tnguyen2013@my.fit.edu, apang2013@my.fit.edu
 * Course:     CSE 4232
 * Project:    GOSSIP P2P, Milestone 5
*/

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPBroadcast {

	private final int port;
	private final String msg;
	private final String[] ips;
	private final int[] ports;

	public UDPBroadcast(final int port, final String msg, final String[] ips, final int[] ports) {
		this.port = port;
		this.msg = msg;
		this.ips = ips;
		this.ports = ports;
	}

	public void broadCast() {

		DatagramSocket udpSocket;
		try {
			System.out.println("Server is broadcasting");
			final String[] str = msg.split(":");
			final Gossip g = new Gossip(str[3], str[1], str[2]);
			final Encoder e = g.getEncoder();
			final byte[] outMsg = e.getBytes();
			udpSocket = new DatagramSocket(port);
			udpSocket.setBroadcast(true);
			for (int i = 0; i < ports.length; i++) {
				final InetAddress temp = InetAddress.getByName(ips[i]);
				final DatagramPacket outPack = new DatagramPacket(outMsg, outMsg.length, temp, ports[i]);
				udpSocket.send(outPack);
				System.out.println("Message sent to address: " + ips[i] + " port: " + ports[i]);
			}
			udpSocket.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
