// Marius C. Silaghi Feb 2003

import java.net.*;
import java.io.*;

public class hello_client{
    final private static int MAXMSG=100;
    static byte[] b=new byte[MAXMSG];
    public static void main(String args[]) {
	Socket sock;
	String input;
	BufferedReader kbdReader;
	int port=3333;
	if(args.length>0) port=Integer.parseInt(args[0]);
	try{
	    sock=new Socket("127.0.0.1", port);
	    InputStream in=sock.getInputStream();
	    OutputStream out=sock.getOutputStream();
	    kbdReader = new BufferedReader (new InputStreamReader(System.in));

	    int readNr=in.read(b);
	    System.out.print(new String(b,0,readNr,"latin1")); // Print Hello
	    for(;;){
		input=kbdReader.readLine();
		out.write((input+"\n").getBytes("latin1"));
		out.flush();
		readNr=in.read(b);
		if(readNr==-1) break;
		System.out.print(new String(b,0,readNr,"latin1"));
	    }
	}catch(IOException e){
	    System.err.println("End "+e);
	}
    }
}
