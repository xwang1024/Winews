package cn.edu.nju.winews.dao;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.edu.nju.winews.dao.impl.VisitedDaoImpl;

public class VisitedDaoImplTest {
	VisitedDao vd;

	@Before
	public void setUp() throws Exception {
		vd = new VisitedDaoImpl();
	}

	@After
	public void tearDown() throws Exception {
		vd.clear();
	}

	@Test
	public void testIsVisited() {
		System.out.println("testIsVisited");
		// fail("Not yet implemented");
	}

	@Test
	public void testAdd() {
		String url1 = "http://test.test.test/test/test";
		String url2 = "http://test.test2.test/test/test";
		System.out.println("testAdd");
		try {
			vd.add(url1, "test");
			assertTrue(vd.isVisited(url1));
			assertFalse(vd.isVisited(url2));
			vd.add(url2, "test");
			assertTrue(vd.isVisited(url2));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testClear() {
		System.out.println("testClear");
		try {
			vd.clear();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
