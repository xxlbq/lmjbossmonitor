package cn.bestwiz.tools.jboss.mqtest.main;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;

import cn.bestwiz.tools.jboss.moitor.action.MailAction;
import cn.bestwiz.tools.jboss.moitor.main.ConfigFileParser;
import cn.bestwiz.tools.jboss.util.LogUtil;
import cn.bestwiz.tools.jboss.util.PropertiesLoader;

public class MqMonitor {
	
	static Properties CONF;
	static {
		String configPath = System.getProperty("configPath");
		if(configPath==null){
			System.setProperty("configPath", "E:\\eclipse\\workspace\\jboss_monitor\\conf");
		}
		String PROCESS_NAME = System.getProperty("PROCESS_NAME");
		if(PROCESS_NAME==null){
			System.setProperty("PROCESS_NAME","MqService");
		}
		try {
			CONF = PropertiesLoader.getProperties("MqMonitor.properties");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static MailAction mqMonitorHaMailAction = ConfigFileParser.parseMailActionFile("mqMonitorHa");
	private static final Log m_log = LogUtil.getLog(MqMonitor.class);
	
	
	private static int retryTimes = 10;
	private static long checkInterval = Long.valueOf(MqMonitor.CONF.getProperty("checkInterval"));
	
	

	
	public static void main(String[] args) {
		List<String> ipList = getIpList();
		List<MqService> mqList = getMqList(ipList);
		List<MqService> failedMqList = new ArrayList<MqService>();
		try {
			Thread.sleep(1000*10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(true){
			if(mqList.isEmpty()){
				m_log.info("no monitoring mq!!");
				break;
			}
			failedMqList.clear();
			for (MqService service : mqList) {
				boolean status = service.check(retryTimes);
				if(status==false){
					vendorFailedHandler(service);
					failedMqList.add(service);
				}else{
					vedorPassHandler(service);
				}
			}
			mqList.removeAll(failedMqList);
			try {
				Thread.sleep(checkInterval);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void vedorPassHandler(MqService service) {
		m_log.info(service.vendorTag+" is ok");
		
	}

	private static void vendorFailedHandler(MqService service) {
		m_log.error(service.vendorTag+" is down!!");
		try {
			MqMonitorDao.updateMqDown(service.ip);
			sendFailedMail(service);
		} catch (SQLException e) {
			m_log.error("update error",e);
			e.printStackTrace();
		} catch (Exception e) {
			m_log.error("update error",e);
			e.printStackTrace();
		}
	}

	private static void sendFailedMail(MqService service) throws Exception{
		Map<String, String> map = new HashMap<String, String>();
		map.put("$Ip", service.ip);
		mqMonitorHaMailAction.sendMail(map);
	}
	
	private static List<MqService> getMqList(List<String> ipList) {
		List<MqService> lst = new ArrayList<MqService>();
		for (String ip : ipList) {
			MqService tmp = MqService.startMonitor(ip);
			if(tmp!=null){
				lst.add(tmp);
			}
		}
		return lst;
	}

	private static List<String> getIpList() {
//		List<String> lst = new ArrayList<String>();
//		lst.add("10.15.3.74");
//		lst.add("10.15.2.32");
		List<String> lst = null;
		try {
			lst = MqMonitorDao.getMqList();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lst;
	}
	
//	public static void main(String[] args) throws Exception {
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("$Ip", "10.15.2.32");
//		mqMonitorHaMailAction.sendMail(map);
//	}
}
