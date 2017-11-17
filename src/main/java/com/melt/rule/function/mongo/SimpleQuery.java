package com.melt.rule.function.mongo;

import com.alibaba.fastjson.JSONObject;
import com.melt.rule.bean.RuleConstant;
import com.melt.rule.bean.RuleContext;
import com.melt.rule.function.IFunction;
import com.melt.rule.utils.JsonUtils;
import com.melt.rule.utils.MongoUtils;
import com.melt.rule.utils.RulePropertyUtils;
import com.melt.rule.utils.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Arrays;
import java.util.List;

import static com.melt.rule.utils.MongoUtils.buildOrderBy;


/**
 * 简单的查询，支持:<p>
 * 查询指定字段、条件查询、分页、排序
 * </p>
 * @author melt
 * @create 2017/11/14 11:18
 */
public class SimpleQuery implements IFunction{

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
//        String varInput = curFun.getString("input") ;
        String varOutput = curFun.getString("output") ;

        String selectCol = curFun.getString("selectCol") ;
        List<String> cols = null ;
        if (StringUtils.hasLength(selectCol)){
            cols = Arrays.asList(selectCol.split(",")) ;
        }

        Bson where = MongoUtils.buildWhere(curFun,context);
        Bson orderBy = buildOrderBy(curFun, context);

        int pageSize = 0;
        int pageIndex = 0 ;
        JSONObject limit = curFun.getJSONObject("limit") ;
        if (limit != null){
            String spageIndex = limit.getString("pageIndex") ;
            String spageSize = limit.getString("pageSize") ;
            pageIndex = (int)JsonUtils.getValue(spageIndex, context);
            pageSize = (int)JsonUtils.getValue(spageSize, context);
        }
        List<Document> list = MongoUtils.query(dbName, colName, cols, where, orderBy, pageIndex, pageSize);
        System.out.println(list.size());
        if (!StringUtils.isEmpty(varOutput)){
            context.putTmpOrResultValue(varOutput,list);
        }


        return true;
    }
}
