package cn.bestwiz.tools.jboss.moitor.action;

import java.util.HashMap;
import java.util.Map;

import cn.bestwiz.tools.jboss.moitor.bean.QueueBean;
import cn.bestwiz.tools.jboss.moitor.bean.TopicBean;
import cn.bestwiz.tools.jboss.moitor.main.ConfigFileParser;
import cn.bestwiz.tools.jboss.moitor.main.MonitorMain;

public class PerformAction {
	
	private static MailAction queueOverStockMailAction = ConfigFileParser.parseMailActionFile("queueOverStockMail");
	private static MailAction topicOverStockMailAction = ConfigFileParser.parseMailActionFile("topicOverStockMail");

	
	public static void sendOverStockMail(QueueBean bean) throws Exception{
		Map<String, String> mapParam = new HashMap<String, String>();
		mapParam.put("$Ip", MonitorMain.IP_ADDRESS);
		mapParam.put("$Name", bean.getName());
		mapParam.put("$CanonicalName", bean.getCanonicalName());
		mapParam.put("$QueueDepth", String.valueOf(bean.getQueueDepth()));
		mapParam.put("$ReceiversCount", String.valueOf(bean.getReceiversCount()));
		queueOverStockMailAction.sendMail(mapParam); 
	}
	
	public static void sendOverStockMail(TopicBean bean) throws Exception{
		Map<String, String> mapParam = new HashMap<String, String>();
		mapParam.put("$Ip", MonitorMain.IP_ADDRESS);
		mapParam.put("$Name", bean.getName());
		mapParam.put("$CanonicalName", bean.getCanonicalName());
		mapParam.put("$AllMessageCount", String.valueOf(bean.getAllMessageCount()));
		mapParam.put("$AllSubscriptionsCount", String.valueOf(bean.getAllSubscriptionsCount()));
		topicOverStockMailAction.sendMail(mapParam); 
	}
}
