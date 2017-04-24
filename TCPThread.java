
/*  Student:    Trung Nguyen
    Email:      tnguyen2013@my.fit.edu
    Course:     CSE 4232
    Project:    GOSSIP P2P, Milestone 4 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPThread extends Thread {

	private final ServerSocket socket;
	private final String path;

	public TCPThread(final int port, final String path) throws IOException {
		socket = new ServerSocket(port);
		this.path = path;
	}

	@Override
	public void run() {
		while (true) {
			try {
				final Socket tcpSocket = socket.accept();
				final Thread t = new TCPServer(tcpSocket, path);
				t.start();
			//	t.join();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}
}
