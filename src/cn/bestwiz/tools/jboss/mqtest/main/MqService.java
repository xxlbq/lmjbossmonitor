package cn.bestwiz.tools.jboss.mqtest.main;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.jms.JMSException;

import org.apache.commons.logging.Log;

import cn.bestwiz.tools.jboss.jms.JmsConfig;
import cn.bestwiz.tools.jboss.jms.SimpleCallback;
import cn.bestwiz.tools.jboss.jms.SimpleReceiver;
import cn.bestwiz.tools.jboss.jms.SimpleSender;
import cn.bestwiz.tools.jboss.util.LogUtil;


public class MqService {
	private static final Log m_log = LogUtil.getLog(MqService.class);
	
	protected static String defaultPort = MqMonitor.CONF.getProperty("defaultPort") ;
	protected static String defaultDest = MqMonitor.CONF.getProperty("defaultDest") ;
	protected static long sendMsgIntervel = Long.valueOf(MqMonitor.CONF.getProperty("sendMsgIntervel"));
	protected static long checkMsgWarnLine = Long.valueOf(MqMonitor.CONF.getProperty("checkMsgWarnLine"));
	protected static long checkMsgErrorLine = Long.valueOf(MqMonitor.CONF.getProperty("checkMsgErrorLine"));
	
	protected Serializable testMessage = (String)"a";
	protected SimpleReceiver rec = null;
	protected SimpleSender sender = null;
	
	protected int receivedCount = 0;
	protected int sentCount = 0;
	
	private boolean runningFlag = true;
	
	
	protected String ip;
	protected String vendorTag;
	
	// last received time 
	private long ts;
	
	protected static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");
	
	public static MqService startMonitor(String ip){
		final MqService mqService = new MqService();
		mqService.setParameter(ip);
		try {
			mqService.startReceiver(defaultDest);
		} catch (JMSException e1) {
			e1.printStackTrace();
			System.err.println(ip+" cant start!!");
			m_log.error(ip+" start failed");
			return null;
		}
		new Thread(new Runnable(){

			public void run() {
				try {
					while(mqService.runningFlag){
						mqService.sendMessage(defaultDest);
						Thread.sleep(sendMsgIntervel);
					};
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}).start();
		return mqService;
	}
	
	protected void setParameter(String ip){
		if(ip.contains(":")){
			this.ip = ip;
		}else{
			this.ip = ip+":"+defaultPort;
		}
		this.vendorTag = "=Vendor["+this.ip+"]=";
		m_log.debug(vendorTag+"setVendorUrl: "+ip);
	}
	
	protected void startReceiver(String dest) throws JMSException{
			rec = new SimpleReceiver(ip,dest);
			rec.addCallback(new SimpleCallback(){

				public void onMessage(Serializable message) {
					if(message.equals(testMessage)){
						m_log.debug(vendorTag+"received msg");
						receivedCount++;
						ts = System.currentTimeMillis();
					}
				}
				
			});
	}
	
	protected void sendMessage(String dest) throws JMSException{
		sender = SimpleSender.getInstance(ip,dest);
		sender.sendMessage(testMessage);
		m_log.debug(vendorTag+"sending msg");
		sentCount++;
	}
	
//	protected boolean check(int i){
//		if(this.sentCount == this.receivedCount){
//			m_log.info("check passed");
//			return true;
//		}else if(Math.abs(this.sentCount - this.receivedCount) == 1&&i>0){
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			m_log.warn(vendorTag+"retry, times left"+i);
//			return this.check(--i);
//		}else{
//			m_log.error("failed");
//			return false;
//		}
//	}
	
	protected boolean check(int i) {
		long nowTs = System.currentTimeMillis();
		long intervel = nowTs - ts;
		if(intervel <= checkMsgWarnLine){
			m_log.debug("check passed");
			return true;
		}else if(intervel <= checkMsgErrorLine){
			m_log.debug("recheck ");
			try {
				Thread.sleep(sendMsgIntervel);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return this.check(i);
		}else{
			m_log.error("failed");
			return false;
		}
	}
	
	/**
	 * 
	 * 
	 * @param millis
	 */
	protected void exit(long millis){
		try {
			Thread.sleep(millis);
			System.out.println();
			System.out.println("======== Total Sent Messages is "+sentCount);
			System.out.println("======== Total Received Messages is "+receivedCount);
			rec.close();
			sender.close();
			this.runningFlag = false;
//			System.exit(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
