
public class PrintableString extends ASNObj{

	final static byte TAG_UN19 = (byte) Encoder.buildASN1byteType(Encoder.CLASS_UNIVERSAL, Encoder.PC_PRIMITIVE, (byte)19);
	String str = null;
	
	public PrintableString(String str){
		this.str = str;
	}

	@Override
	public Encoder getEncoder() {
		Encoder e = new Encoder(str).setASN1Type(Encoder.TAG_OCTET_STRING);
		return e.setASN1Type(TAG_UN19);
	}

	@Override
	public Object decode(Decoder dec) throws ASN1DecoderFail {
		Decoder d = dec.getContent();
		str = d.getFirstObject(true).getString(Encoder.TAG_OCTET_STRING);
		return this;
	}
}
