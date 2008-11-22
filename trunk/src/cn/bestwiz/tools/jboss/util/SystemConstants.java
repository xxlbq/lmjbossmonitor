package cn.bestwiz.tools.jboss.util;

/**
 * Global constants variable for FX System.
 * @author Roger Sun <roger@bestwiz.cn>
 * 
 * @copyright 2006, BestWiz(Dalian) Co.,Ltd
 * @version $Id: SystemConstants.java,v 1.2 2007/11/07 06:03:46 panxy Exp $
 */
public interface SystemConstants {
	
	/**
	 * 配置文件路径�? 键名，�?�常�? configPath，整个程序不用更改�??
	 * java -DconfigPath
	 */
	public static final String CONFIG_PATH_KEY = "configPath";
	
	
	/**
	 * ehcache query cache的前�?，在ehcache.xml中，querycache中名称�?�是
	 * 定义的形式如下：
	 * CacheRegion.cn.bestwiz.jhf.component.dao.bean.main.FxCurrencyPair
	 * 
	 * 程序中如果query CachedClass, 总是如下使用�?
	 * Query   query =s.CreateQuery( hql) ;
	 * query.setCacheable(true);  
     * query.setCacheRegion(SystemConstants.QUERY_CACHE_PREFIX+CachedClass.class.getName());
     * query.list() ;
	 */
	public static final String QUERY_CACHE_PREFIX = "CacheRegion.";
	
    /**
     * 从shell文件中取得进程名PROCESS_NAME
     * 在shell中是通过java -DPROCESS_NAME= xxxx 设置�?,
     * 程序中是通过System.getProperty(SystemConstants.PROCESS_NAME)获取�?
     */
    public static final String PROCESS_NAME = "PROCESS_NAME";
    

    
}
