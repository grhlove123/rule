package com.melt.rule.function.mongo;

import com.alibaba.fastjson.JSONObject;
import com.melt.rule.bean.RuleContext;
import com.melt.rule.function.IFunction;
import com.melt.rule.utils.Assert;
import com.melt.rule.utils.MongoUtils;
import com.melt.rule.utils.RulePropertyUtils;
import com.melt.rule.utils.StringUtils;
import org.bson.Document;

/**
 * 插入单个对象
 * @author melt
 * @create 2017/11/9 16:11
 */
public class InsertOne implements IFunction {

    @Override
    public boolean run(RuleContext context) {
        JSONObject inputVar = context.getInputVar();
        String dbName = inputVar.getString("dbName") ;
        String colName = inputVar.getString("colName") ;
        JSONObject obj = inputVar.getJSONObject("data") ;
        Assert.notNull(obj);
        /**
         * 为空，默认库
         */
        if(StringUtils.isEmpty(dbName)){
            dbName = RulePropertyUtils.getProperty("mongo.default.dbName");
        }

        String id = MongoUtils.insertOne(dbName,colName,new Document(obj));

        return false;
    }
}
