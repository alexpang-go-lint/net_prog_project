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

    public PeersAnswer() {

    }

    public PeersAnswer(ArrayList<Peer> al_p) {
        this.peers = al_p;

    }

    @Override
    public Encoder getEncoder() {
        Encoder e = new Encoder();
        for (int i = 0; i < peers.size(); i++) {
            e.addToSequence(new Encoder(peers.get(i).name).setASN1Type(Encoder.TAG_UTF8String));             // Encode string
            e.addToSequence(new Encoder(peers.get(i).port));                                                 // Encode integer
            e.addToSequence(new Encoder(peers.get(i).ip_addr).setASN1Type(Encoder.TAG_PrintableString));     // Encode printable string
        }

        return e.setASN1Type(Encoder.TAG_SEQUENCE);
    }

    @Override
    public ArrayList<Peer> decode(Decoder dec) throws ASN1DecoderFail {
        Decoder d = dec.getContent();

        // While there are still things to decode
        while (d.tagVal() != 0) {   // Returns zero when there are no more bytes

            String decoded_NAME = d.getFirstObject(true).getString(Encoder.TAG_UTF8String);
            BigInteger decoded_PORT = d.getFirstObject(true).getInteger();
            String decoded_IP = d.getFirstObject(true).getString(Encoder.TAG_PrintableString);

            peers.add(new Peer(decoded_NAME, decoded_IP, decoded_PORT.intValue()));
        }

        return peers;
    }
}
