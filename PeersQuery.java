/*
 * Student:    Trung Nguyen, Yat Shing Pang
 * Email:      tnguyen2013@my.fit.edu, apang2013@my.fit.edu
 * Course:     CSE 4232
 * Project:    GOSSIP P2P, Milestone 4
*/


public class PeersQuery extends ASNObj {
    final static byte TAG_AP3 = (byte) Encoder.buildASN1byteType(Encoder.CLASS_APPLICATION, 0, (byte)3);

    // Returns null

    @SuppressWarnings("static-access")
    @Override
    public Encoder getEncoder() {
        Encoder e = new Encoder();
        //e.addToSequence(new Encoder().getNullEncoder().setNull());
        return e.setASN1Type(TAG_AP3);
    }

    @Override
    public PeersQuery decode(Decoder dec) throws ASN1DecoderFail {

        return this;
    }

}
