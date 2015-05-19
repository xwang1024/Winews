package cn.edu.nju.winews.util;

import junit.framework.TestCase;

public class Base64UtilTest extends TestCase {

	public void testDecode() {
		assertEquals("http://www.baidu.com/", Base64Util.decode("aHR0cDovL3d3dy5iYWlkdS5jb20v".getBytes()));
	}

	public void testEncode() {
		assertEquals("aHR0cDovL3d3dy5iYWlkdS5jb20v", Base64Util.encode("http://www.baidu.com/".getBytes()));
	}

}
