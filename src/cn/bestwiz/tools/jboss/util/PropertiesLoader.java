package cn.bestwiz.tools.jboss.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;


/**
 * Helpers class, which contains static methods helper methods like loading the
 * given property file etc.
 * 
 * 其他实用方法，如读取配置文件�? 方法
 * 
 * @author JHF Team<jhf@bestwiz.cn>
 * @copyright 2006, BestWiz(Dalian) Co.,Ltd
 * @version $Id: PropertiesLoader.java,v 1.2 2007/11/07 06:03:46 panxy Exp $
 */
public final class PropertiesLoader {
	// for logging
	//private static final LogUtil m_log = LogUtil.getInstance(Helpers.class);
	private static final Log m_log = LogUtil.getLog(PropertiesLoader.class);

	// singleton
	private PropertiesLoader() {
		;
	}

	/**
	 * <pre>
	 *  读取指定 属�?�文件中�? 属�?�，
	 * </pre>
	 * 
	 * @param propertyFileName 欲读取的属�?�文�?
	 * @return Properties类型的实�?
	 * @exception java.io.FileNotFoundException 找不到属性文件的时�?�，抛出该异常�??
	 */
	public static Properties getProperties(String propertyFileName)
			throws java.io.FileNotFoundException {

		InputStream is = null;
		try {
			String configPath = System.getProperty(SystemConstants.CONFIG_PATH_KEY);
//			m_log.info("configPath=" + configPath);
            File file = null;
            if (configPath != null && !"".equals(configPath.trim())) {
                file= new File(configPath + File.separator + propertyFileName);
                is = new FileInputStream(file);
                
            } else {
                is = PropertiesLoader.class.getResourceAsStream("/cn/bestwiz/jhf/core/resources/" + propertyFileName);                
            }

			if (is == null) {
				throw new FileNotFoundException(configPath + File.separator + propertyFileName + " not found");
			}

			// load properties
			Properties props = new Properties();
			props.load(is);
			return props;

		} catch (Exception ignore) {
			ignore.printStackTrace();
			throw new java.io.FileNotFoundException(propertyFileName
					+ " not found");
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 从properties中根据名称获取�??,转化成int返回�?
	 * @param p  Properties对象
	 * @param name  Properties中key
	 * @param defaultValue  如果不存在时返回默认�? 
	 * @return int型的Properties的value
	 */
	public static int getIntegerProperty(Properties p,String name,int defaultValue) {
		String l = p.getProperty(name);
		return l == null ? defaultValue : Integer.valueOf(l).intValue();
	}
	
	/**
	 * 从properties中根据名称获取�??,转化成String返回�?
	 * @param p  Properties对象
	 * @param name  Properties中key
	 * @param defaultValue  如果不存在时返回默认�? 
	 * @return String型的Properties的value
	 */
	public static String getStringProperty(Properties p,String name,String defaultValue) {
		String propertyValue = p.getProperty(name);
		return propertyValue == null ? defaultValue : propertyValue;
	}

	/**
	 * 从properties中根据名称获取�??,转化成String返回�?
	 * @param p  Properties对象
	 * @param name  Properties中key
	 * @param defaultValue  如果不存在时返回默认�? 
	 * @return boolean型的Properties的value
	 */
	public static boolean getBooleanProperty(Properties p,String name,boolean defaultValue) {
		String propertyValue = p.getProperty(name);
		return propertyValue == null? defaultValue: new Boolean(propertyValue).booleanValue();
	}

}
