package cn.bestwiz.tools.jboss.jms;

import java.io.Serializable;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;

import cn.bestwiz.tools.jboss.util.LogUtil;

/**
 * 
 * @author Roger Sun
 * @author panxy
 */
public class SimpleReceiver {
	private static final Log m_log = LogUtil.getLog(SimpleReceiver.class);
	// 依赖属�??
	private final String m_destString; public String getDestString(){return this.m_destString;}
	private Provider provider = null;
	
	// 依赖与provider
	private Session m_session = null;
	private MessageConsumer m_receiver = null;
	
	// 守护,同步消息模式
	private boolean m_isDaemon = false;
	
	public SimpleReceiver(String url,String dest) throws JMSException{
		this.m_destString = dest;
		this.provider = JmsConfig.getProvider(url,dest);
		this.createConsumer();
	}

	public SimpleReceiver(String url,String dest,boolean isDaemon) throws JMSException{
		this.m_destString = dest;
		this.provider = JmsConfig.getProvider(url,dest);
		this.m_isDaemon = isDaemon;
		this.createConsumer();
	}
	
	/**
	 * <pre>
	 *       启动 JMS 消息接收器�?? 
	 *       (1)为当前类设置 MessageListener 
	 *       (2)连接 JMS Provider 
	 *       (3)启动 MessageListner
	 * </pre>
	 * @deprecated 启动过程已经在构造函数中完成�?
	 */
	public void start() {
		
	}
	
	/**
	 * 启动Receiver
	 * @throws JMSException 
	 * 
	 * @throws JMSException
	 * @throws NamingException
	 */
	void createConsumer() throws JMSException{
		try {
			this.provider.addReceiver(this);
			this.createConsumerDirectly();
		} catch (Exception e) {
			m_log.error("start error",e);
			throw new JMSException("createConsumer failed");
		}
	}
	
	
	/**
	 * 启动Receiver
	 * 
	 * @throws JMSException
	 * @throws NamingException
	 * @throws cn.bestwiz.jhf.core.jms.exception.JMSException 
	 */
	void createConsumerDirectly() throws JMSException, NamingException, JMSException{
		m_log.debug("==Panxy== "+this.m_destString+" Connecting");
		Connection m_conn= provider.getConnection();
		m_session = m_conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
//		m_conn.setExceptionListener(getExceptionListener());
		m_conn.start();
		m_receiver = m_session.createConsumer(provider.getDestination(m_destString));
		this.run();
		m_log.info("==Panxy== "+this.m_destString+" Succeed to connect to "+provider.getUsingVendor().getUrl());
	}
	
	private void run() throws JMSException{
		if (m_isDaemon) {
			Thread receiver = new Thread( new SynchMsgReceiver() );
			receiver.setDaemon(m_isDaemon);
			receiver.start();
		} else {
			m_receiver.setMessageListener(new AsynchMsgReceiver());
		}
	}
	
	/**
	 * <pre>
	 *      (1)关闭�?Listener
	 *      (2)关闭本类实例
	 * </pre>
	 * @throws JMSException 
	 * @throws JMSException 
	 */
	public void close() {
		m_log.debug("===JMS=== Begin to shutdown Receiver For "+this.m_destString);
		try {
			if (m_receiver != null)
				m_receiver.close();
			if (m_session != null)
				m_session.close();
		} catch (JMSException e) {
			m_log.error("===JMS=== Shutdown Receiver Failed for "+this.m_destString,e);
		}finally{
			this.provider.removeReceiver(this);
		}
		m_log.info("===JMS=== Shutdown Receiver successed For "+this.m_destString);
	}
	
	
	private List<SimpleCallback> callbackList = Collections.synchronizedList(new ArrayList<SimpleCallback>());

	/**
	 * 添加 SimpleCallback
	 */
	public void addCallback(SimpleCallback obj) {
		if (!callbackList.contains(obj) && null != obj) {
			callbackList.add(obj);
		}
	}

	/**
	 * 删除 SimpleCallback
	 */
	public void removeCallback(SimpleCallback obj) {
		if (callbackList.contains(obj) && null != obj) {
			callbackList.remove(obj);
		}
	}

	/**
	 * �? 同步方式 接收 Message时的 接收线程�?
	 * @see AsynchMsgReceiver
	 */
	class SynchMsgReceiver implements Runnable {

		public void run() {
			try {
				while( !Thread.interrupted() ) {
    				Message msg = m_receiver.receive();
    				if ( null == msg ) break ;
    				executeMsg(msg);
    			}
    			
    		} catch( org.jboss.mq.SpyJMSException ex ){	
    			if( ex.getLinkedException() instanceof InterruptedException
    					|| ex.getLinkedException() instanceof ClosedChannelException) {
    				m_log.info( "SynchMsgReceiver is interrupted" );
    			}
    			else {
    				m_log.error("SynchMsgReceiver failed", ex );
    			}
     		} catch(Exception ex ){
     			m_log.error("SynchMsgReceiver failed", ex );
    		} 
		}
		
	}
	
	/**
	 * 处理接收到的 Message
	 * @param msg 接收到的JMS Message
	 * @author Roger Sun <roger@bestwiz.cn>
	 */
	private void executeMsg(Message msg) {
		try {
			if (msg == null || !(msg instanceof ObjectMessage)) {
				m_log.error("===SimpleReceiver===: The message is invalid");
				return;
			}
			ObjectMessage om = (ObjectMessage) msg;
			Object o = om.getObject();
			if (o == null || !(o instanceof Serializable)) {
				m_log.error("===SimpleReceiver===: The message is invalid");
				return;
			}
			Serializable serial = (Serializable) o;
			Iterator<SimpleCallback> ite = callbackList.iterator();
			while (ite.hasNext()) {
				SimpleCallback callback = ite.next();
				callback.onMessage(serial);
			}

		} catch (Exception ex) {
			m_log.error("===JMS=== " + ex.getMessage(),ex);
		}
	}
	
	
	/**
	 * 以异步方式接收Message时的消息监听�?
	 * @see SynchMsgReceiver
	 */
	class AsynchMsgReceiver implements MessageListener {

		/**
		 * 运行 callBackList中的 各Callback�? onMessage 方法
		 */
		public void onMessage(Message msg) {
			executeMsg(msg);
		}
	}
}