package cn.bestwiz.tools.jboss.util;

import java.io.File;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * 记录日志的实用类�?
 * @author  JHF Team<jhf@bestwiz.cn> 			
 * @copyright 2006, BestWiz(Dalian) Co.,Ltd		
 * @version $Id: LogUtil.java,v 1.2 2007/11/07 06:03:46 panxy Exp $
 **/
public class LogUtil {
	private static String propertiesFilename = "jhfLog4j.properties";
	static {
		Properties p = null;
		try {
			p = PropertiesLoader.getProperties(propertiesFilename);
		} catch (java.io.FileNotFoundException e) {
		}
		if(p != null) {
			PropertyConfigurator.configure(p);
		} else {
			System.err.println("LogUtil could not find " + propertiesFilename);
			System.err.println("LogUtil will use default configuration");
			BasicConfigurator.configure();
		}
		
		setUpLog4jForRolloverIfNecessary();
	}

	
	public static Log getLog(Class _class) {
		return LogFactory.getLog(_class);
	}
	/**
	 * Since default core's properties-way log4j setup will override Rollover's xml-way setup,<br>
	 * this method is added to void this problem.<br>
	 * if Rollover's xml configuration file exists, DomConfigurator will be used to setup log4j.<br>
	 * ADDED by DarrenWang @ 2007-02-02
	 */
	private static void setUpLog4jForRolloverIfNecessary() 
	{
		String rolloverLogConfigLocation = "conf/log4j/log4j.xml";
		
		File configFile = new File(rolloverLogConfigLocation);
		if(!configFile.exists())
			return;
		
		DOMConfigurator.configure(rolloverLogConfigLocation);
	}

	public static Log getLog(String className) {
		return LogFactory.getLog(className);
	}

	/**
	 * Prevent the default constructor.
	 */
	private LogUtil() {}

}
