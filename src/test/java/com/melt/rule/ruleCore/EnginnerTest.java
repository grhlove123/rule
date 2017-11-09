package com.melt.rule.ruleCore;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.melt.rule.RuleService;
import com.melt.rule.bean.RuleContext;

public class EnginnerTest {

	@Test  
    public void test() throws Exception {
		RuleService.init();
		RuleContext context = new RuleContext() ;
		context.setRuleId("1");
		JSONObject inputVar = new JSONObject() ;
		inputVar.put("name", "melt guo") ;
		context.setInputVar(inputVar);
		
		RuleService.getRuleEnginner().execute(context) ;
	}
}
