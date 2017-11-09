/**
 * 
 */
package com.melt.rule;

import com.alibaba.fastjson.JSONObject;
import com.melt.rule.bean.RuleContext;
import com.melt.rule.exception.RuleException;

/**
 * 引擎
 * @author melt
 *
 */
public interface RuleEnginner {
	/**
	 * 引擎入口
	 * @author melt
	 * @date 2017-11-08
	 * @param context
	 * @return
	 * @throws RuleException
	 */
	JSONObject execute(RuleContext context) throws RuleException ;
	
}
