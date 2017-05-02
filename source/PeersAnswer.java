import java.math.BigInteger;
import java.util.ArrayList;

/*
 * Student:    Trung Nguyen, Yat Shing Pang
 * Email:      tnguyen2013@my.fit.edu, apang2013@my.fit.edu
 * Course:     CSE 4232
 * Project:    GOSSIP P2P, Milestone 4
*/

public class PeersAnswer extends ASNObj {
    ArrayList<Peer> peers = new ArrayList<Peer>();
	final static byte TAG_AC1 = (byte) Encoder.buildASN1byteType(Encoder.CLASS_CONTEXT, Encoder.PC_CONSTRUCTED, (byte)1);

    public PeersAnswer() {

    }

    public PeersAnswer(ArrayList<Peer> al_p) {
        this.peers = al_p;

    }

    @SuppressWarnings("static-access")
	@Override
    public Encoder getEncoder() {
        Encoder e = new Encoder().initSequence();
        e.getEncoder(peers);
        return new Encoder().initSequence().addToSequence(e).setASN1Type(TAG_AC1);
    }

    @Override
    public PeersAnswer decode(Decoder dec) throws ASN1DecoderFail {
        Decoder d = dec.getContent();
        peers = d.getFirstObject(true).getSequenceOfAL(Peer.TAG_AP2, new Peer());
/*
        // While there are still things to decode
        while (d.tagVal() != 0) {   // Returns zero when there are no more bytes

            String decoded_NAME = d.getFirstObject(true).getString(Encoder.TAG_UTF8String);
            BigInteger decoded_PORT = d.getFirstObject(true).getInteger();
            String decoded_IP = d.getFirstObject(true).getString(Encoder.TAG_PrintableString);

            peers.add(new Peer(decoded_NAME, decoded_IP, decoded_PORT.intValue()));
        }*/

        return this;
    }
}
