package com.bupt.app.multivrPC.utils;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VRTypeUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Map<String, String> map = MultivrPCVRTypeUtils.getVRType();
		System.out.println(map);
	}

}
