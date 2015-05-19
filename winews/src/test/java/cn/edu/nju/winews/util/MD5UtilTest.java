package cn.edu.nju.winews.util;

import junit.framework.TestCase;

public class MD5UtilTest extends TestCase {

	public void testMD5() {
		assertEquals("F4C991546644690E3873DE41493E696D", MD5Util.MD5("myPasswd_hello"));
	}

}
