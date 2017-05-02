
/*
 * Student:    Trung Nguyen, Yat Shing Pang
 * Email:      tnguyen2013@my.fit.edu, apang2013@my.fit.edu
 * Course:     CSE 4232
 * Project:    GOSSIP P2P, Milestone 5
*/

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Gossip extends ASNObj {
	String msg, SHA_256, str_date;
	String time_pattern = "yyyy-MM-dd-hh-mm-ss-SSS'Z'";

	SimpleDateFormat df = new SimpleDateFormat(time_pattern);
	Date date;
	final static byte TAG_AP1 = Encoder.buildASN1byteType(Encoder.CLASS_APPLICATION, 0, (byte) 1);

	public Gossip() {

	}

	public Gossip(final String msg, final String SHA_256, final String date) {
		this.msg = msg;
		this.SHA_256 = SHA_256;
		this.str_date = date;
		this.date = new Date();
		try {

			this.date = df.parse(date);
		} catch (final ParseException e) {
			System.out.println("Error on parsing date " + e);
		}

	}

	@Override
	public Encoder getEncoder() {
		final Calendar c = Calendar.getInstance();
		c.setTime(date);
		final Encoder e = new Encoder().initSequence();
		e.addToSequence(new Encoder(SHA_256.getBytes()).setASN1Type(Encoder.TAG_OCTET_STRING)); // Encode
																								// octet
																								// string
		e.addToSequence(new Encoder(c)); // Encode calendar
		e.addToSequence(new Encoder(msg).setASN1Type(Encoder.TAG_UTF8String)); // Encode
																				// string

		return e.setASN1Type(TAG_AP1);
	}

	@Override
	public Gossip decode(final Decoder dec) throws ASN1DecoderFail {
		final Decoder d = dec.getContent();

		final String decoded_SHA = d.getFirstObject(true).getString(Encoder.TAG_OCTET_STRING);
		final Calendar c = d.getFirstObject(true).getGeneralizedTimeCalender_();

		final String decoded_DATE = df.format(c.getTime());

		final String decoded_MSG = d.getFirstObject(true).getString(Encoder.TAG_UTF8String);

		return new Gossip(decoded_MSG, decoded_SHA, decoded_DATE);
	}

}
