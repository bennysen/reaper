package org.cabbage.commons.utils.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 对象压缩工具类
 * 
 * @author fhtx
 * 
 */
public final class SerializableUtils {
	public static byte[] writeObject(Serializable object) throws IOException {
		byte[] data = null;
		ByteArrayOutputStream bos = null;
		GZIPOutputStream gzout = null;
		ObjectOutputStream out = null;
		try {
			bos = new ByteArrayOutputStream();
			gzout = new GZIPOutputStream(bos);
			out = new ObjectOutputStream(gzout);
			out.writeObject(object);
			out.flush();
			gzout.finish();

			data = bos.toByteArray();
		} catch (IOException e) {
			throw e;
		} finally {
			if (null != out) {
				out.close();
			}
			if (null != gzout) {
				gzout.close();
			}
			if (null != bos) {
				bos.close();
			}
		}
		return data;
	}

	public static Serializable readObject(byte[] data) throws IOException, ClassNotFoundException {
		Serializable object = null;
		ByteArrayInputStream bis = null;
		GZIPInputStream gzin = null;
		ObjectInputStream in = null;
		try {
			bis = new ByteArrayInputStream(data);
			gzin = new GZIPInputStream(bis);
			in = new ObjectInputStream(gzin);
			object = (Serializable) in.readObject();
		} catch (IOException e) {
			throw e;
		} finally {
			if (null != in) {
				in.close();
			}
			if (null != gzin) {
				gzin.close();
			}
			if (null != bis) {
				bis.close();
			}
		}
		return object;
	}

}
