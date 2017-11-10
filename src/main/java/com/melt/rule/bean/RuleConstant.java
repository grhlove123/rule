package com.melt.rule.bean;

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

}
