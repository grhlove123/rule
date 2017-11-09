package com.melt.rule.handler;

import com.alibaba.fastjson.JSONObject;
import com.melt.rule.RuleEnginner;
import com.melt.rule.bean.RuleContext;
import com.melt.rule.exception.RuleException;

public abstract class AppHandler implements RuleEnginner {

	
	/**
	 * 在{@link #handle(RuleContext)}之前执行
	 * @param context
	 * @return
	 * @throws RuleException
	 */
	public abstract boolean preHandle(RuleContext context) throws RuleException ;
	
	public abstract JSONObject handle(RuleContext context) throws RuleException ;
	
	/**
	 * 在{@link #handle(RuleContext)}之后执行
	 * @param context
	 * @return
	 * @throws RuleException
	 */
	public abstract boolean postHandle(RuleContext context) throws RuleException ;
//	
//	/**
//	 * 获取操作DB的工具类
//	 * @return
//	 */
//	public abstract <T> T getSessionTemplate() ;
	
	@Override
	public final JSONObject execute(RuleContext context) throws RuleException {
		if(!preHandle(context)){
			//TODO debug log 
			System.out.println("preHandle fail !");
			return null ;
		}
		JSONObject jsonObj = handle(context) ;
		if(!postHandle(context)){
			//TODO debug log
			System.out.println("postHandle fail !");
			return null ;
		}
		
		return jsonObj;
	}
	
	

}
