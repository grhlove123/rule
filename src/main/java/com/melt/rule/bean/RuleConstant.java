package com.melt.rule.bean;

import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

public class RuleConstant {
	/**
	 * mongo连接的储存在线程变量的key
	 * <p>
	 *     扩展函数可以通过{@link ThreadLocalHolder#get(String, Class)} 获取
	 * </p>
	 */
	public static final String MONGO_CLIENT_KEY = "mongo.client.key" ;

	/**
	 * 默认库存放规则引擎元数据
	 */
	public static final String RULE_METADATA_DB = "rule_meta" ;

	/**
	 * 函数定义表
	 */
	public static final String FUN_DEFINITION = "fun" ;

	/**
	 * 策略定义表（一组函数组成）
	 */
	public static final String STRATEGY = "rule" ;
	/**
	 * 规则定义，key: ruleId  -> value: Document
	 */
	public static final Map<String,Document> STRATEGY_META = new HashMap<>();

	/**
	 * tmp 里
	 */
	public static final String TMP_DBNAME = "dbName" ;

	public static final String TMP_COLNAME = "colName" ;

	public static final String TMP_ENTIRE_RULE = "entireRule" ;

	public static final String TMP_FUN_LIST = "funList" ;

	public static final String TMP_CURRENT_FUN = "curFun" ;

//	public static final String TMP_FUN_LENGTH = "funLength" ;
//
//	public static final String TMP_FUN_INDEX = "funIndex" ;
//	//函数返回值的前缀
//	public static final String TMP_VAR_PRIFF = "funRes" ;


}
