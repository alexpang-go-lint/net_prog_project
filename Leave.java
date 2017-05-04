/*
 * Student:    Trung Nguyen, Yat Shing Pang
 * Email:      tnguyen2013@my.fit.edu, apang2013@my.fit.edu
 * Course:     CSE 4232
 * Project:    GOSSIP P2P, Milestone 5
*/

public class Leave extends ASNObj {

	final static byte TAG_AP4 = Encoder.buildASN1byteType(Encoder.CLASS_APPLICATION, Encoder.PC_PRIMITIVE, (byte) 4);
	String name = null;

	public Leave() {
	}

	public Leave(final String name) {
		this.name = name;
	}

	@Override
	public Encoder getEncoder() {
		final Encoder e = new Encoder(name).setASN1Type(Encoder.TAG_UTF8String);
		return new Encoder().initSequence().addToSequence(e).setASN1Type(TAG_AP4);
	}

	@Override
	public Leave decode(final Decoder dec) throws ASN1DecoderFail {
		final Decoder d = dec.getContent();
		name = d.getFirstObject(true).getString(Encoder.TAG_UTF8String);
		return this;
	}

}
