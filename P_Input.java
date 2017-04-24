import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/*
 Sample input:

*/
public class P_Input {
	final static int max = 1000;
	static int available_peers = 1000;
	static int available_gossips = 1000;
	static int num_peers = 0;
	static int num_gossips = 0;
	static String time_pattern = "yyyy-MM-dd-hh-mm-ss-SSS'Z'";
	static SimpleDateFormat df = new SimpleDateFormat(time_pattern);
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
			//final BufferedReader outerr = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			p.waitFor();
			path = out.readLine();
            path = path.substring(0, path.length()-6) + ("data/");
            //System.out.println(path);
		} catch (IOException | InterruptedException e) {
			System.out.println("Invalid path: " + e);
			System.exit(0);
		}

		return path;

	}

	public String processInput(final String in, String fd) {
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

	static String createNewPeer(final Peer[] peer, String name, int port, String ip_addr,
			final boolean writeToFile, final String fd, File directory) {
	    int oldPort = 0;
	    String oldIP_addr = "";
		// Search for existing peer
		String toPrint = "Peer exists, address updated\n";
		boolean peer_exists = false;
		for (int i = 0; i < num_peers; i++) {
			if (peer[i].name.equals(name)) {
				// Peer exists
				// Update ip and port
			    oldPort = peer[i].port;
			    oldIP_addr = peer[i].ip_addr;
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
	                
                    bw.write(name);
                    bw.newLine();
                    bw.write(port);
                    bw.newLine();
                    bw.write(ip_addr);
                    bw.newLine();
                    
                    Date currentDate = null;
                    try {
                        currentDate = df.parse(new Date().toString());
                    } catch (ParseException e) {
                        
                    }
                    bw.write(currentDate.toString());
                    
	                bw.close();
	            } catch (final IOException e) {
	                
	            }
			}
			num_peers++;
			available_peers--;
		} else {
		    /*
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
                
                // Rewrite all contents
                for (int i = 0; i < num_peers; i++) {
                    ip_addr = peer[i].ip_addr;
                    p = ""+ peer[i].port;
                    name = peer[i].name;
                    bw.write(name);
                    bw.newLine();
                    bw.write(p);
                    bw.newLine();
                    bw.write(ip_addr);
                    bw.newLine();
                    
                }

                bw.close();
            } catch (final IOException e) {}
            */
		    String oldPeerFile = fd+"peer1.txt";
	        String newPeerFile = fd+"tmp.txt";

	        BufferedReader br = null;
	        BufferedWriter bw = null;
	        try {
	           br = new BufferedReader(new FileReader(oldPeerFile));
	           bw = new BufferedWriter(new FileWriter(newPeerFile));
	           String line;
	           while ((line = br.readLine()) != null) {
	              if (line.equals(name)) {
	                  // Found the one we are replacing
	                  // Replace the port and address, but don't change the date
	                  // First write the name
	                  bw.write(line);
	                  bw.newLine();
	                  
	                  // Then replace the port
	                  line = br.readLine();
	                  line = line.replace(Integer.toString(oldPort), Integer.toString(port));
	                  bw.write(line);
	                  bw.newLine();

	                  // Then replace the ip address
	                  line = br.readLine();
	                  line = line.replace(oldIP_addr, ip_addr);
	                  bw.write(line);
	                  bw.newLine();
	                  
	                  // Leave the date alone
	              } else {
	                  // Not the one we are replacing, just write
	                  bw.write(line);
	                  bw.newLine();
	              }
	           }
	        } catch (Exception e) {
	           
	        } finally {
	           try {
	              if(br != null)
	                 br.close();
	           } catch (IOException e) {
	              //
	           }
	           try {
	              if(bw != null)
	                 bw.close();
	           } catch (IOException e) {
	              //
	           }
	        }
	        // Once everything is complete, delete old file..
	        File oldFile = new File(oldPeerFile);
	        oldFile.delete();

	        // And rename tmp file's name to old file name
	        File newFile = new File(newPeerFile);
	        newFile.renameTo(oldFile);
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
						
						// Check if date is 2 days old
						final String d = sc.next();
						Date peerDate = null;
						try {
				            
				            peerDate = df.parse(d);
				        } catch (final ParseException e) {
				            System.out.println("Error on parsing date " + e);
				        }
						
						Instant currentD = new Date().toInstant();
						Instant peerD = peerDate.toInstant();
						
						// 60 seconds * 60 minutes * 24 hours * 2 = 2 days in unit of seconds
						currentD.minusSeconds(60 * 60 * 24 * 2);
						
						if (currentD.compareTo(peerD) > 0) {
						    // Add this peer to memory, because it is less than 2 days old
						    createNewPeer(peer, name, port, ip_addr, false, fd, directory);
						} else {
						    // Forget this peer
						}
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
			}
		}

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
	    ArrayList<Peer> allPeers = new ArrayList<Peer>();
	    for (int i = 0; i < num_peers; i++) {
	        allPeers.add(peer[i]);
	        
	    }
	    
	    return allPeers;
	}
	
	public ArrayList<Gossip> getAllGossips() {
        ArrayList<Gossip> allGossips = new ArrayList<Gossip>();
        for (int i = 0; i < num_gossips; i++) {
            allGossips.add(gossip[i]);
        }
        
        return allGossips;
    }
}
