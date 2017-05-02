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
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class TCPServer extends Thread {

	private Socket sock = null;
	private String path = "";
	private int delay = 0;

	// initiate the TCP server
	public TCPServer(final Socket sock, final String path, final int delay) {
		// System.out.println("socket initiated");
		this.sock = sock;
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
		try {

			final InputStreamReader inStream = new InputStreamReader(sock.getInputStream());
			OutputStreamWriter out = new OutputStreamWriter(sock.getOutputStream());
			final BufferedReader in = new BufferedReader(inStream);
			InputStream is = sock.getInputStream();

			String input;
            System.out.println("TCP connection established");
			out.write("S: Hello\n"
			        + "S: Server will close the connection if inactive for 20 seconds.\n"
			        + "S: Enter command:\n");
			out.flush();
			/*SAMPLE INPUT
			 * GOSSIP:mBHL7IKilvdcOFKR03ASvBNX//ypQkTRUvilYmB1/OY=:2017-01-09-16-18-20-001Z:GOSSIP%
			 * PEER:John:PORT=2356:IP=163.118.239.68%
			 *
			 */
			P_Input p = new P_Input();

			while(true) {
			    boolean isGossip = false, isPeer = false, isQuery = false;
			    sock.setSoTimeout(20000);
				if ((input = in.readLine()) == null)
					break;

				String toProcess = null;

				// Decode
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
			        	//System.out.println("decoding leave");
			            decoded_leave = new Leave().decode(d);
			            toProcess = "LEAVE:" + decoded_leave.name;
			            //System.out.println("decoded leave");
			        } catch (ASN1DecoderFail e3) {
			        	System.out.println("leave decode failed");
			        }
		        }
		        //System.out.println("tag is " + d.tagVal());
		        //System.out.println("the input is " + toProcess);
				String output = p.processInput(toProcess, path);

				// GOSSIP
				if (toProcess.contains("GOSSIP:")) {
						String[] ips = p.getIPs();
						int[] ports = p.getPorts();
						// Broadcast regardless if it is discarded or not.
						if (ips != null) {

    						UDPBroadcast bc = new UDPBroadcast(sock.getPort(), toProcess, ips, ports);
    						bc.broadCast();
    						System.out.println("S: Returned to TCP");
						}
				}
				// Encode again back to client
				if (isGossip) {
				    // Nothing to encode
				} else if (isPeer) {
				    // Nothing to encode
				} else if (isQuery) {
				    // Encode all the peers, the client will handle the decoding and formatting
			        PeersAnswer pa = new PeersAnswer(p.getAllPeers());

			        Encoder e = pa.getEncoder();
				    output = new String(e.getBytes());
				    isQuery = false;
				}
				try {
					this.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				out.write(output, 0, output.length());
                for (int i = 0; i < (output.length() + 100)/100 ; i++) {

                    out.flush();
                }

			}

			sock.close();

		} catch (final IOException e) {
		}
	}
}
