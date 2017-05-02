/*
 * Student:    Trung Nguyen, Yat Shing Pang
 * Email:      tnguyen2013@my.fit.edu, apang2013@my.fit.edu
 * Course:     CSE 4232
 * Project:    GOSSIP P2P, Milestone 5
*/

public class Peer extends ASNObjArrayable {
	String name, ip_addr;
	int port;
	final static byte TAG_AP2 = Encoder.buildASN1byteType(Encoder.CLASS_APPLICATION, Encoder.PC_CONSTRUCTED, (byte) 2);

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
		final Encoder e = new Encoder().initSequence();
		e.addToSequence(new Encoder(name).setASN1Type(Encoder.TAG_UTF8String)); // Encode
																				// string
		e.addToSequence(new Encoder(port).setASN1Type(Encoder.TAG_INTEGER)); // Encode
																				// integers
		e.addToSequence(new Encoder(ip_addr).setASN1Type(Encoder.TAG_PrintableString)); // Encode
																						// printable
																						// string

		return e.setASN1Type(TAG_AP2);
	}

	@Override
	public Peer decode(final Decoder dec) throws ASN1DecoderFail {
		final Decoder d = dec.getContent();

		name = d.getFirstObject(true).getString(Encoder.TAG_UTF8String);
		port = d.getFirstObject(true).getInteger(Encoder.TAG_INTEGER).intValue();
		ip_addr = d.getFirstObject(true).getString(Encoder.TAG_PrintableString);

		return this; // new Peer(decoded_NAME, decoded_IP,
						// decoded_PORT.intValue());
	}

	@Override
	public Peer instance() throws CloneNotSupportedException {
		return new Peer();
	}
}
