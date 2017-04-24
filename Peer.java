import java.math.BigInteger;

/*
 * Student:    Trung Nguyen, Yat Shing Pang
 * Email:      tnguyen2013@my.fit.edu, apang2013@my.fit.edu
 * Course:     CSE 4232
 * Project:    GOSSIP P2P, Milestone 4
*/

public class Peer extends ASNObj {
	String name, ip_addr;
	int port;
	final static byte TAG_AP2 = (byte) Encoder.buildASN1byteType(Encoder.CLASS_APPLICATION, 0, (byte)2);

	// Empty constructor for peers answer
	public Peer() {
	    this.name = "";
	    this.ip_addr = "";
	    this.port = 0;
	}

	public Peer(final String name, final String ip_addr, final int port) {
		this.name = name;
		this.ip_addr = ip_addr;
		this.port = port;

	}

    @Override
    public Encoder getEncoder() {
        Encoder e = new Encoder().initSequence();
        e.addToSequence(new Encoder(name).setASN1Type(Encoder.TAG_UTF8String));             // Encode string
        e.addToSequence(new Encoder(port));                                                 // Encode integer
        e.addToSequence(new Encoder(ip_addr).setASN1Type(Encoder.TAG_PrintableString));     // Encode printable string

        return e.setASN1Type(Encoder.TAG_SEQUENCE);
    }

    @Override
    public Peer decode(Decoder dec) throws ASN1DecoderFail {
        Decoder d = dec.getContent();

        String decoded_NAME = d.getFirstObject(true).getString(Encoder.TAG_UTF8String);
        BigInteger decoded_PORT = d.getFirstObject(true).getInteger();
        String decoded_IP = d.getFirstObject(true).getString(Encoder.TAG_PrintableString);

        return new Peer(decoded_NAME, decoded_IP, decoded_PORT.intValue());
    }

}
