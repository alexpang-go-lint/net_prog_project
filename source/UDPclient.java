
// Marius C. Silaghi,  March 2003
// Hallo UDP client

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPclient {

	public static void main(final String[] args) {

		final Scanner sc = new Scanner(System.in);
		final String message = sc.nextLine();

		try {
			// read parameters
			int port = 3333;
			if (args.length > 0)
				port = Integer.parseInt(args[0]);

			String host = "127.0.0.1";
			if (args.length > 1)
				host = args[1];
			final InetAddress server = InetAddress.getByName(host);

			// create space for datagrams
			final byte[] buf = new byte[1000];
			final DatagramPacket recv = new DatagramPacket(buf, buf.length);
			final byte[] msg = message.getBytes("latin1");
			final DatagramPacket snd = new DatagramPacket(msg, msg.length);

			final DatagramSocket ds = new DatagramSocket();
			ds.connect(server, port);
			ds.send(snd); // send packet first

			ds.receive(recv); // receive greetings
			System.out.println(new String(recv.getData(), 0, recv.getLength()));

			ds.receive(recv); // receive echo
			System.out.println(new String(recv.getData(), 0, recv.getLength()));

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
