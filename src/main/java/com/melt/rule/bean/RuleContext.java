package com.melt.rule.bean;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;
import com.melt.rule.exception.RuleRuntimeException;

/**
 * 上下文对象
 * author: melt
 * date: 2017-11-06
 */
public class RuleContext implements Serializable {

	private static final long serialVersionUID = -7071863801857925084L;
	/**
	 * 规则ID
	 */
	private String ruleId ;
	/**
	 * 输入参数，只能赋值一次
	 */
	private JSONObject inputVar ;
	/**
	 * 计算过程的临时变量
	 */
	private JSONObject tmpVar ;
	
	/**
	 * 输出结果，只能赋值一次
	 */
	private JSONObject resultVar ;
//	/**
//	 * 处理器类型，mongo,mysql,redis..
//	 */
//	private int handlerType ;

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public JSONObject getInputVar() {
		return inputVar;
	}

	public void setInputVar(JSONObject inputVar) {
		/**
		 * 只能赋值一次，保留原始输入
		 */
		if(this.inputVar != null){
			throw new RuleRuntimeException("inputVar is not null,This value can only be assigned once!") ;
		}
		this.inputVar = inputVar;
	}

	public JSONObject getTmpVar() {
		return tmpVar;
	}

	public void setTmpVar(JSONObject tmpVar) {
		this.tmpVar = tmpVar;
	}

	public JSONObject getResultVar() {
		return resultVar;
	}

	
	public void setResultVar(JSONObject resultVar) {
		/**
		 * 只能赋值一次，只有规则最后才给其赋值，原样给前端输出
		 */
		if(this.resultVar != null){
			throw new RuleRuntimeException("resultVar is not null,This value can only be assigned once!") ;
		}
		this.resultVar = resultVar;
	}

//	public int getHandlerType() {
//		return handlerType;
//	}
//
//	public void setHandlerType(int handlerType) {
//		this.handlerType = handlerType;
//	}
	
}
