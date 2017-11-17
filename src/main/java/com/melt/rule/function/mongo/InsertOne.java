package com.melt.rule.function.mongo;

import com.alibaba.fastjson.JSONObject;
import com.melt.rule.bean.RuleConstant;
import com.melt.rule.bean.RuleContext;
import com.melt.rule.function.IFunction;
import com.melt.rule.utils.*;
import org.bson.Document;

/**
 * 插入单个对象
 * @author melt
 * @create 2017/11/9 16:11
 */
public class InsertOne implements IFunction {

    @Override
    public boolean run(RuleContext context) {
        JSONObject rule = context.getTmpVar().getJSONObject(RuleConstant.TMP_ENTIRE_RULE) ;
        JSONObject curFun = context.getTmpVar().getJSONObject(RuleConstant.TMP_CURRENT_FUN) ;
//        int index = context.getTmpVar().getIntValue(RuleConstant.TMP_FUN_INDEX);
        String dbName = curFun.getString("dbName") ;
        String colName = curFun.getString("colName") ;

        if(StringUtils.isEmpty(dbName)){
            dbName = rule.getString("dbName") ;
        }

        if(StringUtils.isEmpty(colName)){
            colName = rule.getString("colName") ;
        }
        /**
         * 为空，默认库
         */
        if(StringUtils.isEmpty(dbName)){
            dbName = RulePropertyUtils.getProperty("mongo.default.dbName");
        }
        String varInput = curFun.getString("input") ;
        String varOutput = curFun.getString("output") ;

        JSONObject data = (JSONObject)JsonUtils.getValue(varInput,context) ;

        Assert.notNull(data,"insert object is null");
        String id = MongoUtils.insertOne(dbName,colName,new Document(data));
//        context.putTmpVarValue(RuleConstant.TMP_VAR_PRIFF + index,id);
        if (!StringUtils.isEmpty(varOutput)){
            context.putTmpOrResultValue(varOutput,id);
        }

        return true;
    }
}
