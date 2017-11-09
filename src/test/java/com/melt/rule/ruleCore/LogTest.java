package com.melt.rule.ruleCore;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTest {
	Logger log = LoggerFactory.getLogger(LogTest.class);
	  
	  
    @Test  
    public void test()  
    {  
    	log.debug("debug()...");  
    	log.info("info()...");  
    	log.error("error()...");  
    }  
}
