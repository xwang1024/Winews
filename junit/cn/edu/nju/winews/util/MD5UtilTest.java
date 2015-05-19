package cn.edu.nju.winews.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MD5UtilTest {

	@Test
	public void testGen32bitMD5() {
		assertEquals("098f6bcd4621d373cade4e832627b4f6", MD5Util.gen32bitMD5("test"));
		assertEquals("4a7d1ed414474e4033ac29ccb8653d9b", MD5Util.gen32bitMD5("0000"));
		assertEquals("4626810a48ef3487ff7d9131ed023d90", MD5Util.gen32bitMD5("sdfas123"));
		assertEquals("008ff75fffa6f7014033fe250dca0930", MD5Util.gen32bitMD5("dfsaawer156"));
	}

}
