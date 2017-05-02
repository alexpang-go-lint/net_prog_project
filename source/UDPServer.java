
/*  Student:    Trung Nguyen
    Email:      tnguyen2013@my.fit.edu
    Course:     CSE 4232
    Project:    GOSSIP P2P, Milestone 5 */

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPServer extends Thread {

	private final DatagramSocket udpSocket;
	private final String path;
	private final int delay;

	public UDPServer(final int port, final String path, final int delay) throws SocketException {

		udpSocket = new DatagramSocket(port);
		this.path = path;
		this.delay = delay;
	}

	@SuppressWarnings("static-access")
	@Override
	public void run() {
		// System.out.println("start thread");
		try {
			this.sleep(delay);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		final byte[] data = new byte[1024];

		while (true) {
			final DatagramPacket msg = new DatagramPacket(data, data.length);
			boolean isGossip = false, isPeer = false, isQuery = false;
			try {
				udpSocket.receive(msg);
			} catch (final IOException e) {
				e.printStackTrace();
				continue;
			}

			final int port = msg.getPort();
			final InetAddress addr = msg.getAddress();

			@SuppressWarnings("unused")
			String input;

			try {
				input = new String(msg.getData(), "latin1");
				// System.out.println("server connected");
				// System.out.println(input);
			} catch (final UnsupportedEncodingException e) {
				e.printStackTrace();
				continue;
			}

			String toProcess = "";
			Decoder d = new Decoder(input.getBytes());
			if(d.tagVal() == 3){
	        	toProcess = "PEERS?";
	        }else if(d.tagVal() == 1){
		        Gossip decoded_G;
		        try {
		            decoded_G = new Gossip().decode(d);
		            toProcess = "GOSSIP:" + decoded_G.SHA_256 + ":" + decoded_G.str_date + ":" + decoded_G.msg;
		            isGossip = true;
		        } catch (ASN1DecoderFail e3) {
		            /* Really BAD practice */
		            // Not gossip, do nothing
		        }
	        }else if(d.tagVal() == 2){
		        Peer decoded_P;
		        try {
		            decoded_P = new Peer().decode(d);
		            toProcess = "PEER:" + decoded_P.name + ":PORT=" + decoded_P.port + ":IP=" + decoded_P.ip_addr;
		            isPeer = true;

		        } catch (ASN1DecoderFail e3) {
		            // Not peer, do nothing
		        }
	        }else if(d.tagVal() == 4){
		        Leave decoded_leave;
		        try {
		            decoded_leave = new Leave().decode(d);
		            toProcess = "LEAVE:" + decoded_leave.name;
		        } catch (ASN1DecoderFail e3) {
		            // Not peer, do nothing
		        }
	        }

			try {
				final P_Input p = new P_Input();
				final String output = p.processInput(toProcess, path);
				DatagramPacket outPack = null;

				if (output.contains("Error")) {
					final byte[] outMsg = output.getBytes("latin1");
					outPack = new DatagramPacket(outMsg, outMsg.length, addr, port);
				} else {
					final byte[] outMsg = output.getBytes("latin1");
					outPack = new DatagramPacket(outMsg, outMsg.length, addr, port);
					// System.out.println("sending data");
					// System.out.println(data);
					if (toProcess.contains("GOSSIP:")) {
    					// Broadcast
					    final int[] ports = p.getPorts();
    					final String[] ips = p.getIPs();

    					if (ips != null) {
    	                    final UDPBroadcast bc = new UDPBroadcast(port, output, ips, ports);
    	                    bc.broadCast();
    					}
					}
				}
				if (isGossip) {

				} else if (isPeer) {

				} else if (isQuery) {

			        PeersAnswer pa = new PeersAnswer(p.getAllPeers());
			        Encoder e = pa.getEncoder();
				    outPack = new DatagramPacket(e.getBytes(), e.getBytes().length, addr, port);
				}
				try {
					this.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				udpSocket.send(outPack);
				// System.out.println("Data sent to address: "+addr.toString()+"
				// port: "+port);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}
}
