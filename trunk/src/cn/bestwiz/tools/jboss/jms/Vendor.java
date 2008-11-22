package cn.bestwiz.tools.jboss.jms;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.springframework.mail.MailException;

import cn.bestwiz.tools.jboss.util.LogUtil;

/**
 * 
 * @author panxy
 */
public class Vendor {
	
	private static final Log m_log = LogUtil.getLog(Vendor.class);
	
	// 对应数据JHF_JMS_VENDOR.JMS_VENDOR_NAME
	private String name = null;
	private String url = null;
	// 
	private int status = 0;
	private String urlPkgPrefixes = "org.jboss.naming:org.jnp.interfaces";
	private String contextFactoryClass = "org.jnp.interfaces.NamingContextFactory";
	private String connFactoryJNDIName = "ConnectionFactory";
	
	private Set<Provider> lstProvider = new HashSet<Provider>();
	
	private InitialContext context = null;
	private ConnectionFactory connectionFactory = null;
	private Connection connection = null;
	
	public Vendor(){
	}
	
	public Vendor(String url){
		this.url=url;
	}
	
	public synchronized void clean(){
		context = null;
		connectionFactory = null;
		connection = null;
		this.setStatus(JmsVendorStatusEnum.UNKNOWN_ENUM.getValue());
	}
	
	public synchronized ConnectionFactory getConnectionFactory() throws NamingException {
		if( this.connectionFactory == null ){
			this.connectionFactory = (ConnectionFactory)getContext().
				lookup(this.getConnFactoryJNDIName());
		}
		return connectionFactory;
	}
	
	private synchronized Connection getConnectionAgain() throws JMSException, NamingException{
		if( this.connection == null ){
			connection = getConnectionFactory().createConnection();
			connection.setExceptionListener(getExceptionListener());
		}
		this.setStatus(JmsVendorStatusEnum.RUNNING_ENUM.getValue());
		return connection;
	}
	
	public synchronized Connection getConnection() throws JMSException, NamingException {
		if( this.connection == null ){
			try {
				connection = getConnectionFactory().createConnection();
				connection.setExceptionListener(getExceptionListener());
			} catch (Exception e) {
				getConnectionAgain();
			}
		}
		this.setStatus(JmsVendorStatusEnum.RUNNING_ENUM.getValue());
		return connection;
	}
	
	public Destination getDestination(String dest) throws NamingException {
		return (Destination)getContext().lookup(dest);
	}
	
	private synchronized InitialContext getContext() throws NamingException {
		if( this.context == null ){
			Properties env = new Properties();
			env.put(Context.PROVIDER_URL, this.getUrl());
			env.put(Context.INITIAL_CONTEXT_FACTORY, this.getContextFactoryClass());
			env.put(Context.URL_PKG_PREFIXES, this.getUrlPkgPrefixes());
			this.context = new InitialContext(env);
		}
		return this.context;
	}
	
	/**
	 * 运行中出异常处理
	 * 1.重新连接�?�?
	 * 2.更改自身状�??
	 * 3.如果1失败通知�?有provider
	 * 4.更新数据�?
	 */
	// TODO
	private void runningExceptionHanlder(){
		this.setStatus(JmsVendorStatusEnum.DOWN_ENUM.getValue());
		try {
			for (Provider provider : lstProvider) {
				m_log.info("inform  Failuew to provider["+provider.getName()+"]");
				provider.vendorFailedHandler(this);
			}
		} catch (JMSException e) {
			m_log.error("reconnection failed",e);
		}
		// 关闭JBOSS
//		try {
//			this.shutDownVendorBySh("sh jbossshutdown.sh");
//		} catch (IOException e) {
//			m_log.error("shutdown Vendor(Jboss) failed!",e);
//		}
		
		try {
			this.sendMail();
		} catch (MailException e) {
			m_log.error("sendMail failed!",e);
		}
	}
	
	
	/**
	 * 关闭VENDOR
	 * 
	 * @param shutdownProcName
	 * @throws IOException
	 */
	private void shutDownVendorBySh(String shutdownProcName) throws IOException{
		String shutdownSh = null;
		String[] splited = this.getUrl().split(":");
    	shutdownSh =  shutdownProcName + "  " +  splited[0];  	
    	Runtime.getRuntime().exec(shutdownSh) ;  
	}
	
	/**
	 * 发�?�失败mail
	 * JMS VENDOR EXCEPTION
	 * =======================================
	 * vendorName : $vendorName
	 * vendorIp : $vendorUrl
	 * =======================================
	 * warn: exception does not mean this vendor is already down.
	 * @throws MailException
	 */
	private void sendMail() throws MailException{
		Map<String,Object> mapParamters = new HashMap<String, Object>();
		mapParamters.put("vendorName", this.getName());
		mapParamters.put("vendorUrl", this.getUrl());
		String[] s = new String[1];
		s[0] = "panxy@bestwiz.cn";
	}
	
	/**
	 * 返回�?个连接中异常处理
	 * 
	 * @return
	 */
	private ExceptionListener getExceptionListener() {
		return new ExceptionListener() {
			private boolean flag = true;
			public void onException(JMSException jmsException) {
				m_log.debug("==Panxy== flag = "+flag);
				m_log.debug("==Panxy== currentThreadId="+Thread.currentThread().getId()+",currentThreadName="+Thread.currentThread().getName()+",isDaemon="+Thread.currentThread().isDaemon());
				if(flag){
					m_log.debug("onException running "+jmsException);
					// 如果出异常了尝试重新获取
					clean();
					try {
						m_log.info("==Panxy== connection Exception,reconnect to "+getUrl());
						getConnectionAgain();
						for (Provider provider : lstProvider) {
							provider.allReceiversSendersReconnect();
						}
					} catch (Exception e) {
						// 如果仍然连接异常则�?�知�?有正在使用该Vendor的Provider进行处理
						m_log.info("==Panxy== reconnection Exception,reconnect to ");
						runningExceptionHanlder();
					} finally {
						flag = false;
					}
				}else{
					m_log.debug("System has handled "+jmsException);
				}
			}
		};
	}
	
	//            ---------- Getter And Setter ----------
	public String getConnFactoryJNDIName() {
		return connFactoryJNDIName;
	}
	public void setConnFactoryJNDIName(String connFactoryJNDIName) {
		this.connFactoryJNDIName = connFactoryJNDIName;
	}
	public String getContextFactoryClass() {
		return contextFactoryClass;
	}
	public void setContextFactoryClass(String contextFactoryClass) {
		this.contextFactoryClass = contextFactoryClass;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrlPkgPrefixes() {
		return urlPkgPrefixes;
	}
	public void setUrlPkgPrefixes(String urlPkgPrefixes) {
		this.urlPkgPrefixes = urlPkgPrefixes;
	}

	public boolean addProvider(Provider provider){
		return lstProvider.add(provider);
	}
	
	public boolean removeProvider(Provider provider){
		return lstProvider.remove(provider);
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		if(this.status!=status){
			this.updateToDB(status);
		}
		this.status = status;
	}
	
	boolean isDownInDb(){
		Integer status = this.updateSelfStatusFromDB();
		if(status==null||status==JmsVendorStatusEnum.DOWN_ENUM.getValue()){
			// 如果status为空(数据库异�?) 或则status为down
			return true;
		}else{
			return false;
		}
	}
	
	// 获取数据库的状�?�更新VM里对象的状�??
	private Integer updateSelfStatusFromDB(){
		Integer status = null;
//		try {
//			DbSessionFactory.beginTransaction(DbSessionFactory.MAIN);
//			status =((JhfJmsVendor)DAOFactory.getConfigDao().
//					getSession().
//					get(JhfJmsVendor.class, this.getName())).getStatus().intValue();
//			this.setStatus(status);
//			DbSessionFactory.commitTransaction(DbSessionFactory.MAIN);
//		} catch (Exception e) {
//			DbSessionFactory.rollbackTransaction(DbSessionFactory.MAIN);
//			// TODO
//			m_log.error("updateDB failed ", e);
//		}
		return status;
	}
	
	private void updateToDB(int status){
//		try {
//			DbSessionFactory.beginTransaction(DbSessionFactory.MAIN);
//			JhfJmsVendor v =(JhfJmsVendor)DAOFactory.getConfigDao().
//					getSession().
//					get(JhfJmsVendor.class, this.getName(), LockMode.UPGRADE);
//			if(status!=v.getStatus().intValue()){
//				v.setStatus(BigDecimal.valueOf(status));
//				v.setUpdateDate(DateHelper.getSystemTimestamp());
//			}
//			DbSessionFactory.commitTransaction(DbSessionFactory.MAIN);
//		} catch (Exception e) {
//			DbSessionFactory.rollbackTransaction(DbSessionFactory.MAIN);
//			// TODO
//			m_log.error("updateDB failed ", e);
//		}
	}
}
