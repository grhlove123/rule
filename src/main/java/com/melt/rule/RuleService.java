package com.melt.rule;

import com.melt.rule.exception.RuleException;
import com.melt.rule.function.IFunction;
import com.melt.rule.handler.AppHandler;
import com.melt.rule.utils.RulePropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 规则引擎服务
 * @author melt
 * @date 2017-11-08
 */
public class RuleService {
	private static final Logger logger = LoggerFactory.getLogger(RuleService.class);
	
	/**
	 * 配置初始化
	 * @author melt
	 * @date 2017-11-08
	 */
	public static void init() throws Exception{
		/**
		 * 加载配置文件
		 */
		RulePropertyUtils.load() ;
		logger.info("load rule configure file finish !");
		RuleServiceLoader.load(IFunction.class) ;
		logger.info("load rule function services finish !");
		RuleServiceLoader.load(RuleEnginner.class) ;
		logger.info("load rule handler services finish !");

		/**
		 * 初始化处理器
		 */
		((AppHandler)getRuleEnginner()).init();

	}
	
	/**
	 * 获取引擎实例
	 * @author melt
	 * @date 2017-11-08
	 * @return
	 * @throws Exception
	 */
	public static RuleEnginner getRuleEnginner() throws Exception{
		/**
		 * 服务启动先调load加载
		 */
		if(RuleServiceLoader.SERVICES_IMPL.isEmpty()){
			throw new RuleException("please invoke RuleServiceLoader.load first !") ;
		}
		Map<String,Class<?>> services = RuleServiceLoader.SERVICES_IMPL.get(RuleEnginner.class.getName()) ;
		if(services == null || services.isEmpty()){
			throw new RuleException("could not find RuleEnginner provider !") ;
		}
		/**
		 * 一个项目只允许一种类型的引擎[mongo、mysql、redis..]
		 */
		if(services.size() > 1){
			throw new RuleException("only allow one type RuleEnginner provider !") ;
		}
		return (RuleEnginner)services.values().stream().findFirst().get().newInstance() ;
	}
}
