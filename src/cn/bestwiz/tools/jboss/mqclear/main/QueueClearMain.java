package cn.bestwiz.tools.jboss.mqclear.main;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import cn.bestwiz.tools.jboss.jms.JmsConfig;
import cn.bestwiz.tools.jboss.jms.SimpleCallback;
import cn.bestwiz.tools.jboss.jms.SimpleReceiver;

public class QueueClearMain {
	
	private static String defaultPort = "1099" ;
	private static SimpleReceiver rec = null;
	
	private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");
	
	static {
		String configPath = System.getProperty("configPath");
		if(configPath==null){
			System.setProperty("configPath", "E:\\eclipse\\workspace\\jboss_monitor\\conf");
		}
		String PROCESS_NAME = System.getProperty("PROCESS_NAME");
		if(PROCESS_NAME==null){
			System.setProperty("PROCESS_NAME","QueueCleaner");
		}
	}
	
	private static void setParameter(String ip){
		if(ip.contains(":")){
			System.setProperty(JmsConfig.vendorUrl, ip);
		}else{
			System.setProperty(JmsConfig.vendorUrl, ip+":"+defaultPort);
		}
	}
	
	private static void startReceiver(String dest){
		try {
			rec = new SimpleReceiver(System.getProperty(JmsConfig.vendorUrl),dest);
			rec.addCallback(new SimpleCallback(){

				public void onMessage(Serializable message) {
					throw new RuntimeException("test");
//					System.out.println("received time: "+format.format(new Date()));
				}
				
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		// args[0] 为服务器 比如: 10.15.2.32:1099 或则 10.15.2.32不带段口默认为1099
		if(args.length>0&&args[0]!=null&&(!args[0].equals(""))){
			System.out.println("args[0](Ip) is "+args[0]);
			setParameter(args[0]);
		}else{
			System.out.println("please enter an Ip!!");
			return;
		}
		
		// 启动receiver
		startReceiver(args[1]);
	}
}
