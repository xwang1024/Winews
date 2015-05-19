package cn.edu.nju.winews.util;

import org.apache.commons.codec.binary.Base64;

/**
 * Base64工具
 * 
 * @author xin
 * @see org.apache.commons.codec.binary.Base64
 */
public class Base64Util {
	/**
	 * Base64解码
	 * 
	 * @param bytes
	 * @return
	 */
	public static String decode(final byte[] bytes) {
		return new String(Base64.decodeBase64(bytes));
	}

	/**
	 * Base64编码
	 * 
	 * @param bytes
	 * @return
	 */
	public static String encode(final byte[] bytes) {
		return new String(Base64.encodeBase64(bytes));
	}
}
