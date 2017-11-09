package com.melt.rule.function.mongo;

import com.melt.rule.bean.RuleContext;
import com.melt.rule.function.IFunction;

public class SayHello implements IFunction {

	@Override
	public boolean run(RuleContext context) {
		System.out.println("hello rule function " + context.getInputVar().toJSONString());
		return false;
	}

}
