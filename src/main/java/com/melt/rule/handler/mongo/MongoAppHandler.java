package com.melt.rule.handler.mongo;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.melt.rule.RuleServiceLoader;
import com.melt.rule.bean.RuleConstant;
import com.melt.rule.bean.RuleContext;
import com.melt.rule.bean.ThreadLocalHolder;
import com.melt.rule.exception.RuleException;
import com.melt.rule.exception.RuleRuntimeException;
import com.melt.rule.function.IFunction;
import com.melt.rule.handler.AppHandler;
import com.melt.rule.utils.RulePropertyUtils;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoException;

public class MongoAppHandler extends AppHandler{

	private static MongoClient mongoClient = null;
	
	
	@Override
	public boolean preHandle(RuleContext context) throws RuleException {
		return true;
	}

	@Override
	public JSONObject handle(RuleContext context) throws RuleException {
		if(context == null){
			throw new RuleException("rule context not allowed to be null! ") ;
		}
		if(StringUtils.isBlank(context.getRuleId())){
			throw new RuleException("rule context not allowed to be null! ") ;
		}
		ThreadLocalHolder.set(RuleConstant.MONGO_CLIENT_KEY, mongoClient) ;
		//TODO 通过ruleId查询functionList
		List<String> funList = Arrays.asList("mongo.SayHello") ;
		funList.forEach(k ->{
			try {
				IFunction fun = RuleServiceLoader.newServicesImplInstance(k, IFunction.class) ;
				fun.run(context);
			} catch (Exception e) {
				throw new RuleRuntimeException(e) ;
			}
		});
		
		
		return context.getResultVar();
	}

	@Override
	public boolean postHandle(RuleContext context) throws RuleException {
		return true;
	}

	

	static {
		/**
		 * 为保证mongoClient只有一个，这里用静态实现单例
		 */
		//TODO 这些参数放配置文件里，如果有mongo.properties就加载，否则不加载
		MongoClientOptions.Builder build = new MongoClientOptions.Builder();        
        build.connectionsPerHost(50);   //与目标数据库能够建立的最大connection数量为50
//        build.autoConnectRetry(true);   //自动重连数据库启动
        build.threadsAllowedToBlockForConnectionMultiplier(50); //如果当前所有的connection都在使用中，则每个connection上可以有50个线程排队等待
        /*
         * 一个线程访问数据库的时候，在成功获取到一个可用数据库连接之前的最长等待时间为2分钟
         * 这里比较危险，如果超过maxWaitTime都没有获取到这个连接的话，该线程就会抛出Exception
         * 故这里设置的maxWaitTime应该足够大，以免由于排队线程过多造成的数据库访问失败
         */
        build.maxWaitTime(1000*60*2);
        build.connectTimeout(1000*60*1);    //与数据库建立连接的timeout设置为1分钟

        MongoClientOptions myOptions = build.build();       
        try {
            //数据库连接实例
            mongoClient = new MongoClient(RulePropertyUtils.getProperty("mongo.host"), myOptions);          
        } catch (MongoException e){
            e.printStackTrace();
        }
	}



	public static MongoClient getMongoClient() {
		return mongoClient;
	}

	public static void setMongoClient(MongoClient mongoClient) {
		MongoAppHandler.mongoClient = mongoClient;
	}
}
