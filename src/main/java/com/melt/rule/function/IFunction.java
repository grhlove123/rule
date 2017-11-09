package com.melt.rule.function;

import com.alibaba.fastjson.JSONObject;
import com.melt.rule.bean.RuleContext;

/**
 * 函数接口(单个)
 * @author melt
 * @date 2017-11-06
 */
public interface IFunction {
	/**
	 * 方法主体，所有扩展函数都要实现本方法
	 * @author melt
	 * @date 2017-11-06
	 * @param context	规则上下文
	 * @return true 方法执行成功，否则失败
	 */
	boolean run(RuleContext context) ;

}
