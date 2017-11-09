package com.melt.rule.ruleCore;

import com.melt.rule.RuleEnginner;
import com.melt.rule.RuleServiceLoader;

public class ServiceLoaderTest {

	public static void main(String[] args) throws Exception {
//		Map<String,Class<?>> list = RuleServiceLoader.loade(IFunction.class) ;
//		System.out.println(list);
//		IFunction funciton = RuleServiceLoader.newServicesImplInstance("mongo.SayHello", IFunction.class) ;
//		System.out.println(funciton);
//		funciton.run(null, null);
		
		RuleServiceLoader.load(RuleEnginner.class) ;
		
		RuleEnginner handler = RuleServiceLoader.newServicesImplInstance("handler.mongo.MongoAppHandler", RuleEnginner.class) ;
		handler.execute(null);
	}

}
