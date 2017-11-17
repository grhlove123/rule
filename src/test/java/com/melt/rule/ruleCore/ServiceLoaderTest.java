package com.melt.rule.ruleCore;

import com.melt.rule.RuleServiceLoader;
import com.melt.rule.function.IFunction;

import java.util.Map;

public class ServiceLoaderTest {

	public static void main(String[] args) throws Exception {
		Map<String,Class<?>> list = RuleServiceLoader.load(IFunction.class) ;
		System.out.println(list);
		IFunction funciton = RuleServiceLoader.newServicesImplInstance("mongo.InsertOne", IFunction.class) ;
		System.out.println(funciton);
//		funciton.run(null, null);
		
//		RuleServiceLoader.load(RuleEnginner.class) ;
//
//		RuleEnginner handler = RuleServiceLoader.newServicesImplInstance("handler.mongo.MongoAppHandler", RuleEnginner.class) ;
//		handler.execute(null);
	}

}
