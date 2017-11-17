package com.melt.rule.utils;

import com.alibaba.fastjson.JSONObject;
import com.melt.rule.bean.RuleContext;
import com.melt.rule.exception.RuleRuntimeException;

/**
 * @author melt
 * @create 2017/11/13 14:58
 */
public class JsonUtils {

    /**
     * 获取指定参数值
     * @param key       最多支持三层input.data.v1
     * @param context   参数上下文
     * @return          值
     */
    public static Object getValue(String key, RuleContext context){
        String[] keys = key.split("\\.") ;
        System.out.println(key +"-------" + keys.length);
        //最多支持三层
        if (keys.length > 3){
            throw new RuleRuntimeException("the parameter supports up to three levels of nesting");
        }
        if (!keys[0].contains("tmpVar") && !keys[0].contains("inputVar")){
            throw new RuleRuntimeException("the parameter must start with [inputVar,tmpVar]");
        }

        JSONObject top = null ;
        if (keys[0].contains("inputVar")){
            top = (JSONObject)context.getInputVar() ;
        } else {
            top = (JSONObject)context.getInputVar() ;
        }

        if (keys.length == 2){
            return top.get(keys[1]) ;
        }
        JSONObject second = top.getJSONObject(keys[1]) ;
        return second.get(keys[2]) ;
    }
}
