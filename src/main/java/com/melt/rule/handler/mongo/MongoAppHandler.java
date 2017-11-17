package com.melt.rule.handler.mongo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.melt.rule.RuleServiceLoader;
import com.melt.rule.bean.HandlerType;
import com.melt.rule.bean.RuleConstant;
import com.melt.rule.bean.RuleContext;
import com.melt.rule.bean.ThreadLocalHolder;
import com.melt.rule.exception.RuleException;
import com.melt.rule.exception.RuleRuntimeException;
import com.melt.rule.function.IFunction;
import com.melt.rule.handler.AppHandler;
import com.melt.rule.utils.Assert;
import com.melt.rule.utils.CollectionUtils;
import com.melt.rule.utils.MongoUtils;
import com.melt.rule.utils.RulePropertyUtils;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MongoAppHandler extends AppHandler{

	private static final Logger logger = LoggerFactory.getLogger(MongoAppHandler.class);
	private static volatile MongoClient mongoClient = null;


	@Override
	public HandlerType getHandlerType() {
		return HandlerType.MONGO;
	}

	@Override
	public void init() throws RuleException {
		if (mongoClient != null){
			logger.warn("handler init method is called only when the service is started");
			return;
		}

//		logger.info("rule handler type is [{}]", getHandlerType().name());
		String ruleHost = RulePropertyUtils.getProperty("rule.metadata.mongo.host") ;
		String rulePort = RulePropertyUtils.getProperty("rule.metadata.mongo.port") ;
		logger.info("load rule metadata mongo client host:[{}]",ruleHost);
		logger.info("load rule metadata mongo client port:[{}]",rulePort);
		mongoClient = new MongoClient(ruleHost, Integer.parseInt(rulePort));
		logger.info("load rule metadata mongo client success !");
		MongoUtils.mongoClient = mongoClient ;
		List<String> colList = mongoClient.getDatabase(RuleConstant.RULE_METADATA_DB).listCollectionNames()
				.into(new ArrayList<>());

		if (CollectionUtils.isEmpty(colList)){
			throw new RuleException("rule metadata [" + RuleConstant.RULE_METADATA_DB +"] database found no tables");
		}

		if(!colList.contains(RuleConstant.FUN_DEFINITION)){
			logger.warn("rule metadata [{}] database not found function definition table [{}]",
					RuleConstant.RULE_METADATA_DB,RuleConstant.FUN_DEFINITION);
		}

		if(!colList.contains(RuleConstant.STRATEGY)){
			logger.warn("rule metadata [{}] database not found strategy table [{}]",
					RuleConstant.RULE_METADATA_DB,RuleConstant.STRATEGY);
		}
		// 加载规则
		List<Document> rules = MongoUtils.query(RuleConstant.RULE_METADATA_DB,RuleConstant.STRATEGY,new Document()) ;
		if (!CollectionUtils.isEmpty(rules)){
			rules.forEach(r -> {
				logger.debug("load strategy : {}",r);
				RuleConstant.STRATEGY_META.put(r.get("_id").toString(),r) ;
			});
		}


		mongoClient.close();
		mongoClient = null ;

		String host = RulePropertyUtils.getProperty("mongo.host") ;
		String port = RulePropertyUtils.getProperty("mongo.port") ;
		logger.info("load rule business mongo client host:[{}]",host);
		logger.info("load rule business mongo client port:[{}]",port);
		/**
		 * 业务操作客户端
		 */
		MongoClientOptions.Builder build = new MongoClientOptions.Builder();
		build.connectionsPerHost(50);   //与目标数据库能够建立的最大connection数量为50
		build.threadsAllowedToBlockForConnectionMultiplier(50); //如果当前所有的connection都在使用中，则每个connection上可以有50个线程排队等待
        /*
         * 一个线程访问数据库的时候，在成功获取到一个可用数据库连接之前的最长等待时间为2分钟
         * 这里比较危险，如果超过maxWaitTime都没有获取到这个连接的话，该线程就会抛出Exception
         * 故这里设置的maxWaitTime应该足够大，以免由于排队线程过多造成的数据库访问失败
         */
		build.maxWaitTime(1000*60*2);
		build.connectTimeout(1000*60*1);    //与数据库建立连接的timeout设置为1分钟

		MongoClientOptions myOptions = build.build();
		ServerAddress serverAddress = new ServerAddress(host, Integer.parseInt(port));
		mongoClient = new MongoClient(serverAddress,myOptions);
		MongoUtils.mongoClient = mongoClient ;
		logger.info("load rule business mongo client success !");

	}

	@Override
	public boolean preHandle(RuleContext context) throws RuleException {
		return true;
	}

	@Override
	public JSONObject handle(RuleContext context) throws RuleException {
		if(context == null){
			throw new RuleException("rule context not allowed to be null! ") ;
		}

		String ruleId = context.getRuleId() ;
		if(StringUtils.isBlank(ruleId)){
			throw new RuleException("ruleId not allowed to be null! ") ;
		}

		if(CollectionUtils.isEmpty(context.getInputVar())){
			throw new RuleException("inputVar not allowed to be null! ") ;
		}


		//TODO 检查是否存在fun这样的集合，这个应该服务启动下检查
		ThreadLocalHolder.set(RuleConstant.MONGO_CLIENT_KEY, mongoClient) ;
		//TODO 通过ruleId查询functionList
		Document document = RuleConstant.STRATEGY_META.get(ruleId) ;
		Assert.notNull(document,"rule id ["+ ruleId +"] not exist ");
		JSONObject ruleJson = new JSONObject(document);
		JSONArray funs = ruleJson.getJSONArray("funs") ;
		Assert.notEmpty(funs,"rule funs is empty ");
		// 整个规则
		context.putTmpVarValue(RuleConstant.TMP_ENTIRE_RULE,ruleJson);
		// 规则函数列表
		context.putTmpVarValue(RuleConstant.TMP_FUN_LIST,funs);
//		context.putTmpVarValue(RuleConstant.TMP_FUN_LENGTH,funs.size());
		// 全局dbName
		context.putTmpVarValue(RuleConstant.TMP_DBNAME,ruleJson.get(RuleConstant.TMP_DBNAME));
		// 全局colName
		context.putTmpVarValue(RuleConstant.TMP_COLNAME,ruleJson.get(RuleConstant.TMP_COLNAME));
//		int index = 0;
		for(Object f : funs){
			JSONObject tmp = (JSONObject) f;
//			index++;
			context.putTmpVarValue(RuleConstant.TMP_CURRENT_FUN,tmp);
//			context.putTmpVarValue(RuleConstant.TMP_FUN_INDEX,index++);
			try {
				IFunction fun = RuleServiceLoader.newServicesImplInstance(tmp.getString("funName"), IFunction.class) ;
				fun.run(context);
			} catch (Exception e) {
				throw new RuleRuntimeException(e) ;
			}
		}



//		List<String> funList = Arrays.asList("mongo.SayHello") ;
//		funList.forEach(k ->{
//			try {
//				IFunction fun = RuleServiceLoader.newServicesImplInstance(k, IFunction.class) ;
//				fun.run(context);
//			} catch (Exception e) {
//				throw new RuleRuntimeException(e) ;
//			}
//		});
		
		
		return context.getResultVar();
	}

	@Override
	public boolean postHandle(RuleContext context) throws RuleException {
		return true;
	}

	

	public static MongoClient getMongoClient() {
		return mongoClient;
	}

	public static void setMongoClient(MongoClient mongoClient) {
		MongoAppHandler.mongoClient = mongoClient;
	}
}
