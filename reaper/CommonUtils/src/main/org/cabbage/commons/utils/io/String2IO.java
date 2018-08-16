package org.cabbage.commons.utils.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class String2IO {

	public static InputStream string2InputStream(String src, String encoding) {
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(src.getBytes(encoding));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return is;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String2IO.string2InputStream("abasdf", "utf-8");
	}

}
