
public class Leave extends ASNObj {
	
	final static byte TAG_AP4 = (byte) Encoder.buildASN1byteType(Encoder.CLASS_APPLICATION, Encoder.PC_PRIMITIVE, (byte)4);
	String name = null;

	public Leave(String name){
		this.name = name;
	}
	
	@Override
	public Encoder getEncoder() {
		Encoder e = new Encoder(name).setASN1Type(Encoder.TAG_UTF8String);
		return new Encoder().initSequence().addToSequence(e).setASN1Type(TAG_AP4);
	}

	@Override
	public Object decode(Decoder dec) throws ASN1DecoderFail {
		Decoder d = dec.getContent();
		name = d.getFirstObject(true).getString(TAG_AP4);
		return this;
	}

}