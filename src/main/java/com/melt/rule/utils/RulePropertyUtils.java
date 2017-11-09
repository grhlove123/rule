package com.melt.rule.utils;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import com.melt.rule.exception.RuleException;
import com.melt.rule.exception.RuleRuntimeException;

public class RulePropertyUtils {
	private static final String PROPERTY_FILE_NAE = "rule/rule.properties";
	private static Properties props = null;
	/**
	 * 加载规则引擎配置
	 * @author melt
	 * @date 2017-11-08
	 * @throws Exception
	 */
	public static void load() throws Exception {
		URL url = RulePropertyUtils.class.getClassLoader().getResource(PROPERTY_FILE_NAE);
		if(null == url){
			throw new RuleException("Can not find the configuration file ["+ PROPERTY_FILE_NAE +"]") ;
		}
		props = new Properties();
		if (null != url) {
			InputStream is = null;
			props.load(is = url.openStream());
			if (is != null)
				is.close();
		}
	}
	/**
	 * 获取key对应的属性
	 * @author melt
	 * @date 2017-11-08
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String getProperty(String key) {
		if(null == props){
			throw new RuleRuntimeException("please invoke RulePropertyUtils.load first !") ;
		}
		return props.getProperty(key) ;
	}

	public static void main(String[] args) throws Exception {
		load();
	}
}
