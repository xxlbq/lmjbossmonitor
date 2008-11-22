package cn.bestwiz.tools.jboss.jms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;

import cn.bestwiz.tools.jboss.util.LogUtil;

/**
 * 
 * @author Roger Sun
 * @author panxy
 */
public class SimpleSender {
	private static final Log m_log = LogUtil.getLog(SimpleSender.class);

	// 依赖属�??
	private final String m_destString; public String getDestString(){return this.m_destString;}
	private Provider provider = null;
    
	private Object mutex = new Object();
	// 依赖与provider
	private Session m_session = null;
	// 确定发�?�类�? Queue Topic
	private Destination m_destination = null;
	private MessageProducer m_sender = null;
    
	// 由于连接失败的导致的没有发�?�成功的消息
	private List<Serializable> lstFailedMsg = Collections.synchronizedList(new ArrayList<Serializable>());
	
	private static Map<String, SimpleSender> m_senders = Collections.synchronizedMap(new HashMap<String, SimpleSender>());
	
	/**
	 * 返回当前类的实例
	 * @param dest 发�?�目�?
	 * @return
	 * @throws JMSException
	 */
	public static synchronized SimpleSender getInstance(String url,String dest) throws JMSException{
		String key = url + dest;
		SimpleSender sender = m_senders.get(key);
		if (sender == null) {
			sender = new SimpleSender(url,dest);
			m_senders.put(key, sender);
		}
		return sender;
	}
	
	private SimpleSender(String url,String dest){
		this.m_destString = dest;
		this.provider = JmsConfig.getProvider(url,dest);
		this.createProducer();
	}
	
	/**
	 * 启动Receiver
	 * 
	 * @throws JMSException
	 * @throws NamingException
	 */
	void createProducer(){
		try {
			this.provider.addSender(this);
			this.createProducerDirectly();
		} catch (Exception e) {
			m_log.error("createProducer failed",e);
		}
	}
	
	/**
	 * 启动Sender
	 * 
	 * @throws JMSException
	 * @throws NamingException
	 * @throws cn.bestwiz.jhf.core.jms.exception.JMSException 
	 */
	void createProducerDirectly() throws JMSException, NamingException, JMSException{
		m_log.debug("==Panxy== "+this.m_destString+" Connecting ");

		
		Connection m_conn = provider.getConnection();
		m_session = m_conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		m_conn.start();
		this.m_destination = provider.getUsingVendor().getDestination(m_destString);
		m_sender = m_session.createProducer(this.m_destination);
		m_log.info("==Panxy== "+this.m_destString+" Succeed to connect to "+provider.getUsingVendor().getUrl());
		// 发�?�由于切换Vendor失败的消�?
		synchronized (mutex) {
			if(lstFailedMsg.size()!=0){
				m_log.info("Sending Cached Failure Messages,Count of Messages is "+lstFailedMsg.size());
				for (Serializable element : lstFailedMsg) {
					this.doSend(element);
					m_log.info("Cached Failure Message["+element+"] has been sent out!!");
				}
				m_log.info("All Cached Failure Messages has been sent successfully. "+lstFailedMsg.size()+" in total");
				lstFailedMsg.clear();
			}
		}
	}
	
	
	/**
	 * 发�?�消�?
	 * 
	 * @param msg
	 * @throws Exception
	 */
	public void sendMessage(Serializable msg) throws JMSException {
		this.doSend(msg);
	}
	
	
	/**
	 * 发�?�消息的真正处理方法
	 * 
	 * @param msg
	 * @throws JMSException
	 */
	private void doSend(Serializable msg) throws JMSException {
		m_log.debug("===[JMS]=== Sending msg is:" + msg.toString());
		if (m_destination == null) {
			m_log.error("===[JMS]=== I'm sorry, but the destination is null, please check it.");
			throw new JMSException("The destination is null");
		}

		if (msg == null) {
			m_log.error("===[JMS]=== Hoops,, the message is null, please check it.");
			throw new JMSException("The message is null");
		}

		try {
			ObjectMessage message = m_session.createObjectMessage();
			message.setObject(msg);
			// message.setJMSDestination(m_destination);

			if (m_destination instanceof Queue) {
				message.setJMSType("Queue");
			} else if (m_destination instanceof Topic) {
				message.setJMSType("Topic");
			}

//			m_sender = m_session.createProducer(m_destination);

//			m_log.debug("===[roger]=== sending message: " + message);
			// m_log.debug("===[roger]=== sender is: " + sender);
			m_sender.send(message, DeliveryMode.NON_PERSISTENT, 4, 0);
			// sender.send(message,DeliveryMode.PERSISTENT,4,0);

			m_log.debug("===[roger]=== sending over");
		} catch (Exception e) {
			m_log.error("Cant Send Message ",e);
			m_log.info("Cache Message["+msg+"]");
			lstFailedMsg.add(msg);
			throw new JMSException("Cant Send Message ");
		}
	}
	
	/**
	 * 关闭
	 */
	public void close() {
		m_log.debug("===JMS=== Begin to shutdown Sender["+this.m_destString+"]");

		try {
			if (m_sender != null)
				m_sender.close();
			if (m_session != null)
				m_session.close();
		} catch (javax.jms.JMSException e) {
			m_log.error("===JMS=== Shutdown error for "+this.m_destString,e);
		} finally {
			this.provider.removeSender(this);
		}
		m_log.info("===JMS=== Shutdown Sender["+this.m_destString+"] successed");
	}
}
