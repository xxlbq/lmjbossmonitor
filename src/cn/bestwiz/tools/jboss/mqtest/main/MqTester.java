package cn.bestwiz.tools.jboss.mqtest.main;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bestwiz.tools.jboss.jms.JmsConfig;
import cn.bestwiz.tools.jboss.jms.SimpleCallback;
import cn.bestwiz.tools.jboss.jms.SimpleReceiver;
import cn.bestwiz.tools.jboss.jms.SimpleSender;


public class MqTester {
	protected static String defaultPort = "1099" ;
	protected static String defaultDest = "topic/TestJbossTopic" ;
	protected static Serializable testMessage = (String)"a";
	protected static SimpleReceiver rec = null;
	protected static SimpleSender sender = null;
	
	protected static int receivedCount = 0;
	protected static int sentCount = 0;
	
	protected static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");
	
	static {
		String configPath = System.getProperty("configPath");
		if(configPath==null){
			System.setProperty("configPath", "E:\\eclipse\\workspace\\jboss_monitor\\conf");
		}
		String PROCESS_NAME = System.getProperty("PROCESS_NAME");
		if(PROCESS_NAME==null){
			System.setProperty("PROCESS_NAME","MqTester");
		}
	}
	
	protected static void setParameter(String ip){
		if(ip.contains(":")){
			System.setProperty(JmsConfig.vendorUrl, ip);
		}else{
			System.setProperty(JmsConfig.vendorUrl, ip+":"+defaultPort);
		}
	}
	
	protected static void startReceiver(String dest){
		try {
			rec = new SimpleReceiver(System.getProperty(JmsConfig.vendorUrl),dest);
			rec.addCallback(new SimpleCallback(){

				public void onMessage(Serializable message) {
//					System.out.println(message);
					if(message.equals(testMessage)){
						System.out.println("received time: "+format.format(new Date()));
						receivedCount++;
					}
				}
				
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected static void sendMessage(String dest){
		try {
			sender = SimpleSender.getInstance(System.getProperty(JmsConfig.vendorUrl),dest);
			sender.sendMessage(testMessage);
			System.out.println("sending time: "+format.format(new Date()));
			sentCount++;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * 
	 * @param millis
	 */
	protected static void exit(long millis){
		try {
			Thread.sleep(millis);
			System.out.println();
			System.out.println("======== Total Sent Messages is "+sentCount);
			System.out.println("======== Total Received Messages is "+receivedCount);
			rec.close();
			sender.close();
//			System.exit(1);
		} catch (InterruptedException e) {
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
		startReceiver(defaultDest);
		// 发送消息
		sendMessage(defaultDest);
		
		
		exit(1000*1);
	}
}
