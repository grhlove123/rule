package com.melt.rule.bean;

import com.alibaba.fastjson.JSONObject;
import com.melt.rule.exception.RuleRuntimeException;
import com.melt.rule.utils.Assert;

import java.io.Serializable;

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

	
//	public void setResultVar(JSONObject resultVar) {
//		/**
//		 * 只能赋值一次，只有规则最后才给其赋值，原样给前端输出
//		 */
//		if(this.resultVar != null){
//			throw new RuleRuntimeException("resultVar is not null,This value can only be assigned once!") ;
//		}
//		this.resultVar = resultVar;
//	}

	/**
	 * 对象放到tmpVar中
	 * @param key
	 * @param obj
	 */
	public void putTmpVarValue(String key,Object obj){
		if (tmpVar == null){
			tmpVar = new JSONObject() ;
		}
		tmpVar.put(key,obj) ;
	}

	/**
	 * 对象放到resultVar中
	 * @param key
	 * @param obj
	 */
	public void putResultVarValue(String key,Object obj){
		if (resultVar == null){
			resultVar = new JSONObject() ;
		}

		resultVar.put(key,obj) ;
	}

	/**
	 * 对象放到tmpVar或resultVar中
	 * @param key	以tmpVar或resultVar开头，最多在层，eg:tmpVar.a.b
	 * @param obj
	 */
	public void putTmpOrResultValue(String key,Object obj){
		Assert.notNull(key);
		Assert.notNull(obj);
		String[] keys = key.split("\\.") ;
		Assert.notEmpty(keys,"parameter format is wrong [" + key +"]");
		Assert.isTrue(keys.length <=3 ,"nested up to three layers");
		if (keys[0].contains("tmpVar")){
			if (keys.length == 2){
				putTmpVarValue(keys[1],obj);
			}
			if (keys.length == 3){
				if (tmpVar == null){
					tmpVar = new JSONObject() ;
				}
				tmpVar.put(keys[1],new JSONObject().put(keys[2],obj));
			}
		} else if (keys[0].contains("resultVar")){
			if (keys.length == 2){
				putResultVarValue(keys[1],obj);
			}
			if (keys.length == 3){
				if (resultVar == null){
					resultVar = new JSONObject() ;
				}
				resultVar.put(keys[1],new JSONObject().put(keys[2],obj));
			}
		} else {
			throw new RuleRuntimeException("parameter could start with [tmpVar、resultVar]") ;
		}

	}
	
}
