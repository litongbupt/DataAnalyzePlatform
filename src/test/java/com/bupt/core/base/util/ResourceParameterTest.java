package com.bupt.core.base.util;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.bupt.core.base.dto.ResouInfoDTO;

public class ResourceParameterTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Map<String, ResouInfoDTO> resourceInfos = ExportParameter.getExportInfos();
		System.out.println(resourceInfos);
//		Set<Entry<String, ResouInfoDto>> entrySet = resourceInfos.entrySet();
//		for (Entry<String, ResouInfoDto> entry : entrySet) {
//			System.out.println(entry.toString());
//		}
	}

}
