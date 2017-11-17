package com.melt.rule;

import com.melt.rule.exception.RuleException;
import com.melt.rule.utils.StringUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 加载 服务
 * 
 * @author melt
 * @date 2017-11-06
 */
public class RuleServiceLoader {
	private static final Logger logger = LoggerFactory.getLogger(RuleServiceLoader.class) ;
	private static final String MAPPING_CONFIG_PREFIX = "META-INF/services/rule";
	public static final Map<String,Map<String,Class<?>>> SERVICES_IMPL = new HashMap<String,Map<String,Class<?>>>() ;

	/**
	 * 加载实现类，并没有实例化
	 * @author melt
	 * @date 2017-11-07
	 * @param service
	 * @return		实现类class列表
	 * @throws Exception
	 */
	public static Map<String,Class<?>> load(Class<?> service) throws Exception {
		String clasName = service.getName() ;
		/**
		 * 已加载，直接返回
		 */
		if(SERVICES_IMPL.containsKey(clasName)){
			return SERVICES_IMPL.get(clasName) ;
		}
		
		String mappingConfigFile = MAPPING_CONFIG_PREFIX + "/" + clasName;
		// 由于一个接口的实现类可能存在多个jar包中的META-INF目录下，所以下面使用getResources返回一个URL数组
		Enumeration<URL> configFileUrls = RuleServiceLoader.class.getClassLoader().getResources(mappingConfigFile);
		if (configFileUrls == null) {
			return null;
		}
		
		Map<String,Class<?>> services = new HashMap<String,Class<?>>() ;
		while (configFileUrls.hasMoreElements()) {
			URL configFileUrl = configFileUrls.nextElement();
			String configContent = IOUtils.toString(configFileUrl.openStream());
			String[] serviceNames = configContent.split("\n");
			for (String serviceName : serviceNames) {
				Class<?> serviceClass = (Class<?>)RuleServiceLoader.class.getClassLoader().loadClass(
						StringUtils.replaceBlank(serviceName)
				);
//				Object serviceInstance = serviceClass.newInstance();
				services.put(StringUtils.replaceBlank(serviceName), serviceClass) ;
				logger.info("loaded provider {} of {}",serviceName,clasName);
			}
		}
		SERVICES_IMPL.put(clasName, services) ;
		return services;
	}
	
	/**
	 * 实例化服务 
	 * <p>
	 * service.packageName + "." + simpleName 才是服务提供者的完整路径
	 * @author melt
	 * @date 2017-11-07
	 * @param simpleName	服务名
	 * @param serviceClass		服务接口
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newServicesImplInstance(String simpleName,Class<?> serviceClass) throws Exception{
		if(simpleName == null || "".equals(simpleName)){
			throw new RuleException("simpleName is not allowed to be null") ;
		}
		if(serviceClass == null){
			throw new RuleException("load service is not allowed to be null") ;
		}
		String packName = serviceClass.getPackage().getName() ;
		/**
		 * 服务启动先调load加载
		 */
		if(SERVICES_IMPL.isEmpty()){
			throw new RuleException("please invoke RuleServiceLoader.load first !") ;
		}
		Map<String,Class<?>> services = SERVICES_IMPL.get(serviceClass.getName()) ;
		if(services == null || services.isEmpty()){
			throw new RuleException(serviceClass.getName() + " has no Provider !!") ;
		}
		String name  = packName + "." + simpleName ;
		Class<?> implService = services.get(name) ;
		if(implService == null){
			//TODO debug 打印implService列表
			throw new RuleException("could not find ["+ name + "] class !!") ;
		}
		return (T)implService.newInstance() ;
	}
	
}
