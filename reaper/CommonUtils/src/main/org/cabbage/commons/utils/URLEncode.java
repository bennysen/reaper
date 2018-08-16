package org.cabbage.commons.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class URLEncode {

	private static Map<String, String> MAP = new HashMap<String, String>();

	static {
		MAP.put("%00", "?");
		MAP.put("%01", "");
		MAP.put("%02", "");
		MAP.put("%03", "");
		MAP.put("%04", "");
		MAP.put("%05", "");
		MAP.put("%06", "");
		MAP.put("%07", "");
		MAP.put("%08", "退格");
		MAP.put("%09", "	");
		MAP.put("%0a", "换行");
		MAP.put("%0b", "");
		MAP.put("%0c", "");
		MAP.put("%0d", "回车");
		MAP.put("%0e", "");
		MAP.put("%0f", "");
		MAP.put("%10", "");
		MAP.put("%11", "");
		MAP.put("%12", "");
		MAP.put("%13", "");
		MAP.put("%14", "");
		MAP.put("%15", "");
		MAP.put("%16", "");
		MAP.put("%17", "");
		MAP.put("%18", "");
		MAP.put("%19", "");
		MAP.put("%1a", "");
		MAP.put("%1b", "");
		MAP.put("%1c", "");
		MAP.put("%1d", "");
		MAP.put("%1e", "");
		MAP.put("%1f", "");
		MAP.put("%20", " ");
		MAP.put("%21", "!");
		MAP.put("%22", "\"");
		MAP.put("%23", "#");
		MAP.put("%24", "$");
		MAP.put("%25", "%");
		MAP.put("%26", "&");
		MAP.put("%27", "'");
		MAP.put("%28", "(");
		MAP.put("%29", ")");
		MAP.put("%2a", "*");
		MAP.put("%2b", "+");
		MAP.put("%2c", ",");
		MAP.put("%2d", "-");
		MAP.put("%2e", ".");
		MAP.put("%2f", "/");
		MAP.put("%30", "0");
		MAP.put("%31", "1");
		MAP.put("%32", "2");
		MAP.put("%33", "3");
		MAP.put("%34", "4");
		MAP.put("%35", "5");
		MAP.put("%36", "6");
		MAP.put("%37", "7");
		MAP.put("%38", "8");
		MAP.put("%39", "9");
		MAP.put("%3a", ":");
		MAP.put("%3b", ";");
		MAP.put("%3c", "<");
		MAP.put("%3d", "=");
		MAP.put("%3e", ">");
		MAP.put("%3f", "?");
		MAP.put("%40", "@");
		MAP.put("%41", "A");
		MAP.put("%42", "B");
		MAP.put("%43", "C");
		MAP.put("%44", "D");
		MAP.put("%45", "E");
		MAP.put("%46", "F");
		MAP.put("%47", "G");
		MAP.put("%48", "H");
		MAP.put("%49", "I");
		MAP.put("%4a", "J");
		MAP.put("%4b", "K");
		MAP.put("%4c", "L");
		MAP.put("%4d", "M");
		MAP.put("%4e", "N");
		MAP.put("%4f", "O");
		MAP.put("%50", "P");
		MAP.put("%51", "Q");
		MAP.put("%52", "R");
		MAP.put("%53", "S");
		MAP.put("%54", "T");
		MAP.put("%55", "U");
		MAP.put("%56", "V");
		MAP.put("%57", "W");
		MAP.put("%58", "X");
		MAP.put("%59", "Y");
		MAP.put("%5a", "Z");
		MAP.put("%5b", "[");
		MAP.put("%5c", "\\");
		MAP.put("%5d", "]");
		MAP.put("%5e", "^");
		MAP.put("%5f", "_");
		MAP.put("%60", "`");
		MAP.put("%61", "a");
		MAP.put("%62", "b");
		MAP.put("%63", "c");
		MAP.put("%64", "d");
		MAP.put("%65", "e");
		MAP.put("%66", "f");
		MAP.put("%67", "g");
		MAP.put("%68", "h");
		MAP.put("%69", "i");
		MAP.put("%6a", "j");
		MAP.put("%6b", "k");
		MAP.put("%6c", "l");
		MAP.put("%6d", "m");
		MAP.put("%6e", "n");
		MAP.put("%6f", "o");
		MAP.put("%70", "p");
		MAP.put("%71", "q");
		MAP.put("%72", "r");
		MAP.put("%73", "s");
		MAP.put("%74", "t");
		MAP.put("%75", "u");
		MAP.put("%76", "v");
		MAP.put("%77", "w");
		MAP.put("%78", "x");
		MAP.put("%79", "y");
		MAP.put("%7a", "z");
		MAP.put("%7b", "{");
		MAP.put("%7c", "|");
		MAP.put("%7d", "}");
		MAP.put("%7e", "~");
		MAP.put("%7f", "");
		MAP.put("%80", "€");
		MAP.put("%81", "");
		MAP.put("%82", "‚");
		MAP.put("%83", "ƒ");
		MAP.put("%84", "„");
		MAP.put("%85", "乧");
		MAP.put("%86", "侕");
		MAP.put("%87", "侖");
		MAP.put("%88", "ˆ");
		MAP.put("%89", "侎");
		MAP.put("%8a", "Š");
		MAP.put("%8b", "‹");
		MAP.put("%8c", "Œ");
		MAP.put("%8d", "");
		MAP.put("%8e", "丒br>");
		MAP.put("%8f", "");
		MAP.put("%90", "");
		MAP.put("%91", "乪");
		MAP.put("%92", "乫");
		MAP.put("%93", "乬");
		MAP.put("%94", "乭");
		MAP.put("%95", "•");
		MAP.put("%96", "–");
		MAP.put("%97", "—");
		MAP.put("%98", "˜˜˜˜˜~˜");
		MAP.put("%99", "™");
		MAP.put("%9a", "š");
		MAP.put("%9b", "›");
		MAP.put("%9c", "œ");
		MAP.put("%9d", "");
		MAP.put("%9e", "丒br>Ÿ");
		MAP.put("%9f", "");
		MAP.put("%a0", "¡");
		MAP.put("%a1", "￠");
		MAP.put("%a2", "￡");
		MAP.put("%a3", "");
		MAP.put("%a4", "\\\\");
		MAP.put("%a5", "|");
		MAP.put("%a6", "仒");
		MAP.put("%a7", "丯");
		MAP.put("%a8", "©");
		MAP.put("%a9", "a");
		MAP.put("%aa", "«");
		MAP.put("%ab", "¬");
		MAP.put("%ac", "ˉ");
		MAP.put("%ad", "®");
		MAP.put("%ae", "ˉ");
		MAP.put("%af", "亱");
		MAP.put("%b0", "亇");
		MAP.put("%b1", "2");
		MAP.put("%b2", "3");
		MAP.put("%b3", "丩");
		MAP.put("%b4", "μ");
		MAP.put("%b5", "侘");
		MAP.put("%b6", "·");
		MAP.put("%b7", "¸");
		MAP.put("%b8", "1");
		MAP.put("%b9", "o");
		MAP.put("%ba", "»");
		MAP.put("%bb", "¼");
		MAP.put("%bc", "½");
		MAP.put("%bd", "¾");
		MAP.put("%be", "¿");
		MAP.put("%bf", "");
		MAP.put("%c0", "À");
		MAP.put("%c1", "Á");
		MAP.put("%c2", "Â");
		MAP.put("%c3", "Ã");
		MAP.put("%c4", "Ä");
		MAP.put("%c5", "Å");
		MAP.put("%c6", "Æ");
		MAP.put("%c7", "Ç");
		MAP.put("%c8", "È");
		MAP.put("%c9", "É");
		MAP.put("%ca", "Ê");
		MAP.put("%cb", "Ë");
		MAP.put("%cc", "Ì");
		MAP.put("%cd", "Í");
		MAP.put("%ce", "Î");
		MAP.put("%cf", "Ï");
		MAP.put("%d0", "Ð");
		MAP.put("%d1", "Ñ");
		MAP.put("%d2", "Ò");
		MAP.put("%d3", "Ó");
		MAP.put("%d4", "Ô");
		MAP.put("%d5", "Õ");
		MAP.put("%d6", "Ö");
		MAP.put("%d7", "");
		MAP.put("%d8", "Ø");
		MAP.put("%d9", "Ù");
		MAP.put("%da", "Ú");
		MAP.put("%db", "Û");
		MAP.put("%dc", "Ü");
		MAP.put("%dd", "Ý");
		MAP.put("%de", "Þ");
		MAP.put("%df", "ß");
		MAP.put("%e0", "à");
		MAP.put("%e1", "á");
		MAP.put("%e2", "â");
		MAP.put("%e3", "ã");
		MAP.put("%e4", "ä");
		MAP.put("%e5", "å");
		MAP.put("%e6", "æ");
		MAP.put("%e7", "ç");
		MAP.put("%e8", "è");
		MAP.put("%e9", "é");
		MAP.put("%ea", "ê");
		MAP.put("%eb", "ë");
		MAP.put("%ec", "ì");
		MAP.put("%ed", "í");
		MAP.put("%ee", "î");
		MAP.put("%ef", "ï");
		MAP.put("%f0", "e");
		MAP.put("%f1", "?");
		MAP.put("%f2", "ò");
		MAP.put("%f3", "ó");
		MAP.put("%f4", "ô");
		MAP.put("%f5", "õ");
		MAP.put("%f6", "ö");
		MAP.put("%f7", "亐");
		MAP.put("%f8", "ø");
		MAP.put("%f9", "ù");
		MAP.put("%fa", "ú");
		MAP.put("%fb", "û");
		MAP.put("%fc", "ü");
		MAP.put("%fd", "ý");
		MAP.put("%fe", "þ");
		MAP.put("%ff", "ÿ");

	}

	public synchronized static String getEncodeURI(String key) {
		String s = "";
		if (null != key) {
			Iterator<String> i = MAP.keySet().iterator();
			while (i.hasNext()) {
				String k = i.next();
				if (key.equals(k)) {
					s = MAP.get(key);
					break;
				}
				k = k.toUpperCase();
				if (key.equals(k)) {
					s = MAP.get(k.toLowerCase());
					break;
				}
			}
		}
		return s;
	}

	public synchronized static String replaceAllEncodeURI(String src) {
		String s = src;

		if (null != src) {
			Iterator<String> i = MAP.keySet().iterator();
			while (i.hasNext()) {
				String k = i.next();

				if (src.contains(k)) {
					try {
						src = src.replaceAll(k, MAP.get(k));
					} catch (StringIndexOutOfBoundsException e) {
						e.printStackTrace();
						System.err.println("[" + src + "].replaceAll([" + k
								+ "], [" + MAP.get(k) + "])");
					}
				}

				String k1 = k.toUpperCase();
				if (src.contains(k1)) {
					src = src.replaceAll(k1, MAP.get(k));
				}
			}
			s = src;
		}
		return s;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(replaceAllEncodeURI("nick=%E4?%A0%E8??%E8亱?%E6侖?%"));
	}

}
