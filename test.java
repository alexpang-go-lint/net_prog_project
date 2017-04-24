import java.util.ArrayList;




public class test {
    
    public static void main(String[] args) {
        // Test gossip
        String gossip_input = "GOSSIP:mBHL7IKilvdcOFKR03ASvBNX//ypQkTRUvilYmB1/OY=:2017-01-09-16-18-20-001Z:Tom eats Jerry%";
        String[] split = gossip_input.split(":");
   
        Gossip gossip = new Gossip(split[3], split[1], split[2]);
        Encoder e = gossip.getEncoder();
        Decoder d = new Decoder(e.getBytes());
        Gossip g;
        try {
            g = new Gossip().decode(d);
            System.out.println(g.msg);
            System.out.println(g.SHA_256);
            System.out.println(g.date);
        } catch (ASN1DecoderFail e3) {
            System.out.println("Error on decoding gossip: " + e3);
        }
        
        // Test Peer
        String peer_input = "PEER:John:PORT=2356:IP=163.118.239.68%";
        split = peer_input.split(":");
        
        Peer peer = new Peer(split[1], split[3], Integer.parseInt(split[2].substring(5, split[2].length())));
        
        e = peer.getEncoder();
        d = new Decoder(e.getBytes());
        Peer p;
        try {
            p = new Peer().decode(d);
            System.out.println(p.name);
            System.out.println(p.ip_addr);
            System.out.println(p.port);
        } catch (ASN1DecoderFail e3) {
            System.out.println("Error on decoding peer: " + e3);
        }
        
        // Test peers query
        PeersQuery pq = new PeersQuery();
        
        e = pq.getEncoder();
        d = new Decoder(e.getBytes());
        PeersQuery p_q;
        try {
            p_q = new PeersQuery().decode(d);
            System.out.println(p_q);
        } catch (ASN1DecoderFail e1) {
            System.out.println("Error on decoding pq: " + e1);
        }
        
        // Test peers answer
        ArrayList<Peer> testPeerList = new ArrayList<Peer>();
        testPeerList.add(peer);
        peer_input = "PEER:Alex:PORT=2356:IP=163.118.239.68%";
        split = peer_input.split(":");
        
        peer = new Peer(split[1], split[3], Integer.parseInt(split[2].substring(5, split[2].length())));
        
        testPeerList.add(peer);
        peer_input = "PEER:ohyeah:PORT=2356:IP=163.118.239.68%";
        split = peer_input.split(":");
        
        peer = new Peer(split[1], split[3], Integer.parseInt(split[2].substring(5, split[2].length())));
        
        testPeerList.add(peer);
        
        PeersAnswer pa = new PeersAnswer(testPeerList);
        
        e = pa.getEncoder();
        d = new Decoder(e.getBytes());
        ArrayList<Peer> decoded_pa = new ArrayList<Peer>();
        try {
            decoded_pa = new PeersAnswer().decode(d);
            for (int i = 0; i < decoded_pa.size(); i++) {
                System.out.println(decoded_pa.get(i).name);
                System.out.println(decoded_pa.get(i).ip_addr);
                System.out.println(decoded_pa.get(i).port);
            }
        } catch (ASN1DecoderFail e1) {
            
        }
    }
}
