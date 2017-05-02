
/*
 * Student:    Trung Nguyen, Yat Shing Pang
 * Email:      tnguyen2013@my.fit.edu, apang2013@my.fit.edu
 * Course:     CSE 4232
 * Project:    GOSSIP P2P, Milestone 5
*/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class P_Input {
	final static int max = 1000;
	static int available_peers = 1000;
	static int available_gossips = 1000;
	static int num_peers = 0;
	static int num_gossips = 0;

	static boolean discarded = false;

	static Peer[] peer = new Peer[max];
	static Gossip[] gossip = new Gossip[max];

	static String getPath() {
		String path = null;
		Process p;
		try {
			p = Runtime.getRuntime().exec("pwd");
			final InputStream stdout = p.getInputStream();
			final BufferedReader out = new BufferedReader(new InputStreamReader(stdout));
			// final BufferedReader outerr = new BufferedReader(new
			// InputStreamReader(p.getErrorStream()));

			p.waitFor();
			path = out.readLine();
			path = path.substring(0, path.length() - 6) + ("data/");
			// System.out.println(path);
		} catch (IOException | InterruptedException e) {
			System.out.println("Invalid path: " + e);
			System.exit(0);
		}

		return path;

	}

	public String processInput(final String in, String fd) throws IOException {
		Scanner sc = null;
		String toPrint = null;

		if (fd.isEmpty()) {
			// The path is not specified, store in the source folder by default
			fd = getPath();
		}

		final File directory = new File(fd);
		readExisting(fd, directory);

		// Finished loading preexisting items, see if input is
		// "GOSSIP" or "PEER" or "PEERS"
		sc = new Scanner(in);
		sc.useDelimiter(":");

		if (in.contains("GOSSIP")) {
			// Check if gossip exists
			// Gossip definition
			discarded = false;
			sc.next();
			final String SHA_256 = sc.next();

			// Date
			final String date = sc.next();
			// Error check

			String msg = sc.next();
			// Last character is %
			for (int k = 0; k < msg.length(); k++)
				if (msg.charAt(k) == '%')
					msg = msg.substring(0, k);

			toPrint = createNewGossip(gossip, SHA_256, date, msg, true, fd, directory);

		} else if (in.contains("PEERS?")) {
			// Get a list of all the projects

			toPrint = "PEERS|" + num_peers + "|";
			for (int i = 0; i < num_peers; i++) {
				toPrint += peer[i].name + ":" + "PORT=" + peer[i].port + ":" + "IP=" + peer[i].ip_addr + "|";
			}

			toPrint += "%";

		} else if (in.contains("PEER:")) {
			sc.next();
			// Name
			final String name = sc.next();

			// port
			String port = sc.next();
			port = port.substring(5, port.length());
			final int p = Integer.parseInt(port);

			// ip
			String ip = sc.next();
			ip = ip.substring(3, ip.length() - 1);

			toPrint = createNewPeer(peer, name, p, ip, true, fd, directory);

		} else if (in.contains("LEAVE:")) {
			sc.next();
			// Name
			final String name = sc.next();

			toPrint = leaveUser(name, fd);
		} else {
			toPrint = ("Error: Unrecognizeable command");
		}

		sc.close();
		discarded = false;
		return toPrint;
	}

	static String createNewGossip(final Gossip[] gossip, final String SHA_256, final String date, final String msg,
			final boolean writeToFile, final String fd, File directory) {
		boolean g_exists = false;
		String toPrint = null;
		for (int i = 0; i < num_gossips; i++) {
			if (gossip[i] != null)
				if (gossip[i].msg.equals(msg)) {
					toPrint = "DISCARDED.";
					g_exists = true;
					discarded = true;
				}

		}

		// Create gossip object
		if (!g_exists) {
			toPrint = ("Added new gossip: " + msg + "\n" + "With date: " + date + "\n" + "With SHA_256: " + SHA_256
					+ "\n");
			gossip[num_gossips] = new Gossip(msg, SHA_256, date);

			// Write on the file
			if (writeToFile) {
				try {
					// No preexisting file
					directory = new File(fd + "gossip1.txt");
					if (!directory.exists()) {

						directory.createNewFile();
					}
					final FileWriter fw = new FileWriter(directory, true);
					final BufferedWriter bw = new BufferedWriter(fw);

					bw.write(SHA_256);
					bw.newLine();
					bw.write(date);
					bw.newLine();
					bw.write(msg);
					bw.newLine();

					bw.close();
				} catch (final IOException e) {
					toPrint = "" + (e);
				}
			}
			available_gossips--;
			num_gossips++;
		}

		return toPrint;
	}

	static String createNewPeer(final Peer[] peer, String name, final int port, String ip_addr,
			final boolean writeToFile, final String fd, File directory) {
		// Search for existing peer
		String toPrint = "Peer exists, address updated\n";
		boolean peer_exists = false;
		for (int i = 0; i < num_peers; i++) {
			if (peer[i].name.equals(name)) {
				// Peer exists
				// Update ip and port
				peer[i].port = port;
				peer[i].ip_addr = ip_addr;
				peer_exists = true;
			}
		}

		if (!peer_exists) {
			peer[num_peers] = new Peer(name, ip_addr, port);
			toPrint = "New peer added\n";
			if (writeToFile) {
				try {
					// No preexisting file
					directory = new File(fd + "peer1.txt");
					if (!directory.exists()) {
						directory.createNewFile();
					}
					final FileWriter fw = new FileWriter(directory, true);
					final BufferedWriter bw = new BufferedWriter(fw);
					final String p = Integer.toString(port);

					bw.write(name);
					bw.newLine();
					bw.write(p);
					bw.newLine();
					bw.write(ip_addr);
					bw.newLine();

					bw.close();
				} catch (final IOException e) {

				}
			}
			num_peers++;
			available_peers--;
		} else {

			try {
				// Completely rewrite the file
				// Is easier than changing the contents in the file
				directory = new File(fd + "peer1.txt");
				if (!directory.exists()) {
					directory.createNewFile();
				}
				final FileWriter fw = new FileWriter(directory);
				final BufferedWriter bw = new BufferedWriter(fw);
				String p = Integer.toString(port);

				// Rewrite all contensts
				for (int i = 0; i < num_peers; i++) {
					ip_addr = peer[i].ip_addr;
					p = "" + peer[i].port;
					name = peer[i].name;
					bw.write(name);
					bw.newLine();
					bw.write(p);
					bw.newLine();
					bw.write(ip_addr);
					bw.newLine();

				}

				bw.close();
			} catch (final IOException e) {

			}
		}
		return toPrint;
	}

	static void readExisting(final String fd, File directory) {
		// Check if there are pre-existing gossips/peers.
		// There are existing gossips or peers definitions, load into memory
		final String[] existing_items = directory.list();
		Scanner sc = null;
		for (int i = 0; i < existing_items.length; i++) {
			// Read existing file
			if (new File(fd + existing_items[i]).isFile()) {
				directory = new File(fd + existing_items[i]);
				try {
					sc = new Scanner(directory);
				} catch (final FileNotFoundException e) {

					System.exit(0);
				}
				if (existing_items[i].contains("peer")) {
					while (sc.hasNext()) {
						final String name = sc.next();
						final int port = Integer.parseInt(sc.next());
						final String ip_addr = sc.next();

						createNewPeer(peer, name, port, ip_addr, false, fd, directory);
					}
				} else if (existing_items[i].contains("gossip")) {
					while (sc.hasNext()) {
						// Gossip
						final String SHA_256 = sc.nextLine();
						final String date = sc.nextLine();
						final String msg = sc.nextLine();

						createNewGossip(gossip, SHA_256, date, msg, false, fd, directory);
					}
				}
				sc.close();
			}
		}

	}

	public String leaveUser(final String name, final String fd) throws IOException {
		String result = null;
		for (int i = 0; i < num_peers; i++) {
			if (peer[i] != null && peer[i].name.equals(name)) {
				final File directory = new File(fd + "peer1.txt");
				final File tempFile = new File(fd + "tempPeer.txt");
				if (!directory.exists()) {
					break;
				}
				if (!tempFile.exists()) {
					tempFile.createNewFile();
				}
				final BufferedReader reader = new BufferedReader(new FileReader(directory));
				final BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
				String currentLine;

				while ((currentLine = reader.readLine()) != null) {
					// trim newline when comparing with lineToRemove
					final String trimmedLine = currentLine.trim();
					if (trimmedLine.equals(name)) {
						reader.readLine();
						reader.readLine();
						continue;
					}
					writer.write(currentLine + System.getProperty("line.separator"));
				}
				writer.close();
				reader.close();
				if (!directory.delete()) {
					System.out.println("Can't delete file");
				}
				if (!tempFile.renameTo(directory)) {
					System.out.println("Can't rename file");
				}
			}
		}
		final File directory = new File(fd);
		readExisting(fd, directory);
		result = "Left " + name;
		return result;
	}

	public int[] getPorts() {
		int[] ports = null;
		if (num_peers > 0) {
			ports = new int[num_peers];
			for (int i = 0; i < num_peers; i++)
				ports[i] = peer[i].port;
		}
		return ports;

	}

	public String[] getIPs() {
		String[] ip = null;
		if (num_peers > 0) {
			ip = new String[num_peers];
			for (int i = 0; i < num_peers; i++)
				ip[i] = peer[i].ip_addr;
		}
		return ip;
	}

	public boolean isDiscarded() {
		return discarded;
	}

	public ArrayList<Peer> getAllPeers() {
		final ArrayList<Peer> allPeers = new ArrayList<Peer>();
		for (int i = 0; i < num_peers; i++) {
			allPeers.add(peer[i]);

		}

		return allPeers;
	}

	public ArrayList<Gossip> getAllGossips() {
		final ArrayList<Gossip> allGossips = new ArrayList<Gossip>();
		for (int i = 0; i < num_gossips; i++) {
			allGossips.add(gossip[i]);
		}

		return allGossips;
	}
}
