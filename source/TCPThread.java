
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
	private final int delay;

	public TCPThread(final int port, final String path, final int delay) throws IOException {
		socket = new ServerSocket(port);
		this.path = path;
		this.delay = delay;
	}

	@Override
	public void run() {
		while (true) {
			try {
				final Socket tcpSocket = socket.accept();
				final Thread t = new TCPServer(tcpSocket, path, delay);
				t.start();
			//	t.join();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}
}
