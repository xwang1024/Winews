package cn.edu.nju.winews.config;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NewspaperConfigManagerTest {
	NewspaperConfigManager mcm;

	@Before
	public void setUp() throws Exception {
		mcm = new NewspaperConfigManager();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNewspaperConfigManager() {
		try {
			mcm = new NewspaperConfigManager();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testReload() {
		try {
			mcm.reload();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetNewspaperList() {
		String[] arr = mcm.getNewspaperList();
		for (int i = 0; i < arr.length; i++) {
			System.out.println(arr[i]);
		}
	}

	@Test
	public void testGetCommonConfig() {
		assertEquals("xh.xhby.net", mcm.getCommonConfig("新华日报", NewspaperConfigManager.CommonConfig.domain));
		assertEquals("新华日报", mcm.getCommonConfig("新华日报", NewspaperConfigManager.CommonConfig.name));
	}

	@Test
	public void testGetUrlConfig() {
		assertEquals("http://xh.xhby.net/mp2/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]*\\\\.htm",
				mcm.getUrlConfig("新华日报", NewspaperConfigManager.UrlConfig.pattern_content));
		assertEquals("yyyy-MM/dd", mcm.getUrlConfig("新华日报", NewspaperConfigManager.UrlConfig.format_date));
	}

	@Test
	public void testGetSelector() {
		assertEquals("td[width=145]", mcm.getSelector("新华日报", NewspaperConfigManager.Selector.layout));
		assertEquals(".wz .font02:nth-child(2)", mcm.getSelector("新华日报", NewspaperConfigManager.Selector.subTitle));
	}

	@Test
	public void testToString() {
		System.out.println(mcm);
	}

	@Test
	public void testToStringSpecific() {
		System.out.println(mcm.toString("新华日报"));
	}

}
