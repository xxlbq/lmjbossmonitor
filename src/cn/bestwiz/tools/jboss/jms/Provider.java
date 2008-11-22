package cn.bestwiz.tools.jboss.jms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;

import cn.bestwiz.tools.jboss.mqtest.bean.JmsMasterSlaveEnum;
import cn.bestwiz.tools.jboss.util.LogUtil;

/**
 * 
 * @author panxy
 */
public class Provider {
	private static final Log m_log = LogUtil.getLog(Provider.class);
	// Constant
	private static int MASTER = JmsMasterSlaveEnum.MASTER_ENUM.getValue();
	private static int SLAVE = JmsMasterSlaveEnum.SLAVE_ENUM.getValue();
	
	// 对应数据JHF_JMS_PROVIDER.JMS_PROVIDER_NAME
	private String name = null;
	private Vendor masterVendor = null;
	private Vendor slaveVendor = null;
	// #0 NOT_USING #1 MASTER #2 SLAVE
	private int usingProvider;

	private List<SimpleReceiver> lstReceiver = Collections.synchronizedList(new ArrayList<SimpleReceiver>());
	private List<SimpleSender> lstSender = Collections.synchronizedList(new ArrayList<SimpleSender>());
	

//	private Connection connection = null;
	
	public Provider(){
	}
	
	
	/**
	 * 添加Sender
	 * 
	 * @param sender
	 * @return
	 */
	public boolean addSender(SimpleSender sender){
		return lstSender.add(sender);
	}
	
	/**
	 * 移除Sender
	 * 
	 * @param sender
	 * @return
	 */
	public boolean removeSender(SimpleSender sender){
		return lstSender.remove(sender);
	}
	
	/**
	 * 添加Receiver
	 * 
	 * @param receiver
	 * @return
	 */
	public boolean addReceiver(SimpleReceiver receiver){
		return lstReceiver.add(receiver);
	}
	
	/**
	 * 移除Receiver
	 * 
	 * @param receiver
	 * @return
	 */
	public boolean removeReceiver(SimpleReceiver receiver){
		return lstReceiver.remove(receiver);
	}
	
	/**
	 * �?有Receiver重新连接
	 * @throws NamingException 
	 * 
	 */
	public void allReceiversSendersReconnect() {
		try {
			for (SimpleReceiver receiver : lstReceiver) {
				m_log.info("reCreate Consumer For "+receiver.getDestString());
				receiver.createConsumerDirectly();
			}
			// Sender发�?�等待一段时�?(考虑到确定其他进程的Receiver启动,在启动Sender,减少异进程Topic Message丢失的可�?)
			Thread.sleep(1000*20);
			for (SimpleSender sender : lstSender) {
				m_log.info("reCreate Producer For "+sender.getDestString());
				sender.createProducerDirectly();
			}
		} catch (Exception e) {
			m_log.error("allReceiversSendersReconnect Failed",e);
		}
	}
	
	
	/**
	 * Vendor出问题的处理方法
	 * 1.判断该vedor是否正在使用,不使用则忽略
	 * 2.若该vedor是正在使用的则切换vendor
	 * 3.更新DB
	 * 4.通知�?有sender和receiver
	 * @throws cn.bestwiz.jhf.core.jms.exception.JMSException 
	 */
	public void vendorFailedHandler(Vendor v) throws JMSException{
		m_log.trace("receiver "+v.getName()+" failure!!");
		if(this.usingProvider!=JmsMasterSlaveEnum.NOT_USING_ENUM.getValue()){
			Vendor nowUsingVendor = null;
			if(this.usingProvider==JmsMasterSlaveEnum.MASTER_ENUM.getValue()){
				nowUsingVendor = this.masterVendor;
			}else if(this.usingProvider==JmsMasterSlaveEnum.SLAVE_ENUM.getValue()){
				nowUsingVendor = this.slaveVendor;
			}else{
				throw new JMSException("SHOULD NEVER GET HERE!!!");
			}
			if(nowUsingVendor.getName().equals(v.getName())){
				// 当前Vendor失败
				m_log.trace(v.getName()+" is running Vendor for "+this.name+"!! ");
				v.setStatus(JmsVendorStatusEnum.DOWN_ENUM.getValue());
				this.changeVendor();
				this.allReceiversSendersReconnect();
			}else{
				// 非当前Vendor失败
				m_log.trace(v.getName()+" is NOT running Vendor for "+this.name+"!! ");
			}
		}
	}
	
	public synchronized Connection getConnection() throws JMSException {
		try {
			return this.getUsingVendor().getConnection();
		} catch (Exception e) {
			this.getUsingVendor().setStatus(JmsVendorStatusEnum.DOWN_ENUM.getValue());
			this.changeVendor();
			return this.getConnection();
		}
	}
	
	/**
	 * 切换jms
	 * 
	 * @return
	 * @throws cn.bestwiz.jhf.core.jms.exception.JMSException 
	 */
	// 如果没有对usingVendor初始化则切换到master�?
	public boolean changeVendor() throws JMSException{
		if(usingProvider==MASTER){
			// 从MASTER切换到SLAVE
			if(this.slaveVendor.getStatus()
					!=JmsVendorStatusEnum.DOWN_ENUM.getValue()){
				usingProvider = SLAVE;
			}else{
				if(this.masterVendor.getStatus()
						==JmsVendorStatusEnum.DOWN_ENUM.getValue()){
					// 从VM储存信息来看2个都down
					this.noVendorAliveHandler();
				}else{
					usingProvider = MASTER;
				}
			}
			m_log.debug("==Panxy== changeProvider to SLAVE");
			updateDB(SLAVE);
		}else if(usingProvider==SLAVE||usingProvider==JmsMasterSlaveEnum.NOT_USING_ENUM.getValue()){
			// 从SLAVE切换到MASTER
			if(this.masterVendor.getStatus()
					!=JmsVendorStatusEnum.DOWN_ENUM.getValue()){
				usingProvider = MASTER;
			}else{
				if(this.slaveVendor.getStatus()
						==JmsVendorStatusEnum.DOWN_ENUM.getValue()){
					// 从VM储存信息来看2个都down
					this.noVendorAliveHandler();
				}else{
					usingProvider = SLAVE;
				}
			}
			m_log.debug("==Panxy== changeProvider to MASTER");
			updateDB(MASTER);
		}else{
			m_log.debug("==Panxy== should never get here!!");
		}
		return true;
	}
	
	private void noVendorAliveHandler() throws JMSException{
		boolean isMasterReallyDownInDB = this.masterVendor.isDownInDb();
		boolean isSlaveReallyDownInDB = this.slaveVendor.isDownInDb();
		if(isMasterReallyDownInDB&&isSlaveReallyDownInDB){
			// 2者全down
			this.updateDB(JmsMasterSlaveEnum.NOT_USING_ENUM.getValue());
			throw new JMSException(
					"Provide "+this.getName()+
					"'s Master "+this.getMasterVendor().getName()+
					" and Slave "+ this.getSlaveVendor().getName()+
					" are down.");
		}else{
			// 2者有1没down
			this.changeVendor();
		}
	}
	
	/**
	 * 获取可用的正在使用的vendor
	 * 
	 * @return
	 * @throws JMSException 
	 */
	public Vendor getUsingVendor() throws JMSException{
		if(usingProvider==MASTER){
			if(this.masterVendor.getStatus()==JmsVendorStatusEnum.DOWN_ENUM.getValue()){
				this.changeVendor();
				return this.getUsingVendor();
			}
			return this.masterVendor;
		} else if(usingProvider==SLAVE){
			if(this.slaveVendor.getStatus()==JmsVendorStatusEnum.DOWN_ENUM.getValue()){
				this.changeVendor();
				return this.getUsingVendor();
			}
			return this.slaveVendor;
		} else if(usingProvider==JmsMasterSlaveEnum.NOT_USING_ENUM.getValue()){
			this.changeVendor();
			return this.getUsingVendor();
//			if(this.masterVendor.getStatus()!=JmsVendorStatusEnum.DOWN_ENUM.getValue()){
//				// 如果masterVendor Status不是DOWN状�?? 
//				this.usingProvider = MASTER;
//			}else{
//				// 如果masterVendor Status是DOWN状�?? 
//				if(this.slaveVendor.getStatus()!=JmsVendorStatusEnum.DOWN_ENUM.getValue()){
//					// 如果slaveVendor Status不是DOWN状�?? 
//					this.usingProvider = SLAVE;	
//				}else{
//					// masterVendor �? slaveVendor全DOWN
//					
//				}
//			}
//			return this.getUsingVendor();
		} else {
			return null;
		}
	}
	
	/**
	 * 更新数据�?
	 * 
	 * @param usingVendor
	 */
	private void updateDB(int usingVendor){
//		try {
//			DbSessionFactory.beginTransaction(DbSessionFactory.MAIN);
//			JhfJmsProvider p = ((JhfJmsProvider)DAOFactory.getConfigDao().
//					getSession().
//					get(JhfJmsProvider.class, name, LockMode.UPGRADE));
//			
//			if(usingVendor!=p.getUsingVendor().intValue()){
//				p.setUsingVendor(BigDecimal.valueOf(usingVendor));
//				p.setUpdateDate(DateHelper.getSystemTimestamp());
//			}
//			DbSessionFactory.commitTransaction(DbSessionFactory.MAIN);
//		} catch (Exception e) {
//			DbSessionFactory.rollbackTransaction(DbSessionFactory.MAIN);
//			// TODO 出异常不�?
//			m_log.error("updateDB failed ", e);
//		}
	}
	
	
//	/**
//	 * 返回�?个连接中异常处理
//	 * 
//	 * @return
//	 */
//	private ExceptionListener getExceptionListener() {
//		return new ExceptionListener() {
//			private boolean flag = true;
//			public void onException(JMSException jmsException) {
//				m_log.debug("==Panxy== flag = "+flag);
//				m_log.debug("==Panxy== currentThreadId="+Thread.currentThread().getId()+",currentThreadName="+Thread.currentThread().getName()+",isDaemon="+Thread.currentThread().isDaemon());
//				if(flag){
//					m_log.debug("onException running "+jmsException);
//					changeProvider();
//					connection = null;
//					for (Receiver receiver : lstReceiver) {
//						try {
//							receiver.startDirectly();
//						} catch (JMSException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						} catch (NamingException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//					flag = false;
//				}else{
//					m_log.debug("System has handled "+jmsException);
//				}
//			}
//		};
//	}
	
	public Destination getDestination(String dest) throws NamingException, JMSException {
		return this.getUsingVendor().getDestination(dest);
	}
	
	public Vendor getMasterVendor() {
		return masterVendor;
	}

	public void setMasterVendor(Vendor masterVendor) {
		this.masterVendor = masterVendor;
	}

	public Vendor getSlaveVendor() {
		return slaveVendor;
	}

	public void setSlaveVendor(Vendor slaveVendor) {
		this.slaveVendor = slaveVendor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getUsingProvider() {
		return usingProvider;
	}

	public void setUsingProvider(int usingProvider) {
		this.usingProvider = usingProvider;
	}
	
	
}
