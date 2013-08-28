package com.bupt.app.multivrPC.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class WordPCMapperTest {

	@Before
	public void setUp() throws Exception {
		ApplicationContext aContext = new FileSystemXmlApplicationContext("src/main/resources/com/bupt/config/applicationContext.xml");
		WordPCMapper userMapper = aContext.getBean(WordPCMapper.class);
	}

	@After
	public void tearDown() throws Exception {
	}


    
    @Test
    public void testListResult(){
    }
    
    @Test
    public void testSelectByExample(){

		
    }

}
