package com.melt.rule.ruleCore;

import com.alibaba.fastjson.JSONObject;
import com.melt.rule.RuleService;
import com.melt.rule.bean.RuleContext;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnginnerTest {
	private RuleContext context = null ;

	@Before
	public void setUp() throws Exception {
		RuleService.init();

	}

	@Test
    public void test() throws Exception {

		context = new RuleContext() ;
		context.setRuleId("1");
		JSONObject inputVar = new JSONObject() ;
		inputVar.put("name", "melt guo") ;
		context.setInputVar(inputVar);
		
		RuleService.getRuleEnginner().execute(context) ;
	}

	@Test
	public void insert()throws Exception {
		context = new RuleContext() ;
		context.setRuleId("5a0e95c99a9aefe44a81d044");
		JSONObject inputVar = new JSONObject() ;
		JSONObject data = new JSONObject() ;
		data.put("name","melt") ;
		data.put("age",30) ;
		List<JSONObject> listEdu = new ArrayList<>();
		JSONObject edu1 = new JSONObject() ;
//		edu1.put("name","guo") ;
		edu1.put("name", Arrays.asList(1,2,3)) ;
		listEdu.add(edu1);
		data.put("edu",listEdu) ;

		inputVar.put("data", data) ;
		context.setInputVar(inputVar);

		RuleService.getRuleEnginner().execute(context) ;
		System.out.println(context.getResultVar());
	}

	@Test
	public void query()throws Exception {
		context = new RuleContext() ;
		context.setRuleId("5a0edf6b7a87c6d3601bb292");
		JSONObject inputVar = new JSONObject() ;
		inputVar.put("name","melt") ;
		inputVar.put("age",20) ;
		inputVar.put("nameSort",1) ;
		inputVar.put("pageIndex",1) ;
		inputVar.put("pageSize",10) ;
		context.setInputVar(inputVar);

		RuleService.getRuleEnginner().execute(context) ;
		System.out.println(context.getResultVar());
	}
}
