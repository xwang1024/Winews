package cn.edu.nju.winews.dao;

import static org.junit.Assert.fail;

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
