/*
 * Student:    Trung Nguyen, Yat Shing Pang
 * Email:      tnguyen2013@my.fit.edu, apang2013@my.fit.edu
 * Course:     CSE 4232
 * Project:    GOSSIP P2P, Milestone 5
*/

public class PeersQuery extends ASNObj {
	final static byte TAG_AP3 = Encoder.buildASN1byteType(Encoder.CLASS_APPLICATION, 0, (byte) 3);

	// Returns null

	@Override
	public Encoder getEncoder() {
		final Encoder e = new Encoder();
		// e.addToSequence(new Encoder().getNullEncoder().setNull());
		return e.setASN1Type(TAG_AP3);
	}

	@Override
	public PeersQuery decode(final Decoder dec) throws ASN1DecoderFail {

		return this;
	}

}
