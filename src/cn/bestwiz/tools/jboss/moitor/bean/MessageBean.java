package cn.bestwiz.tools.jboss.moitor.bean;

import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.jboss.jmx.adaptor.rmi.RMIAdaptor;

public abstract class MessageBean {
	protected String canonicalName;
	
	// For Example: losscutRateQueue
	protected String name;
	
	// 3 for normal: more code info please check jboss mq doc
	protected int state;

	// message Type Topic or Queue
	protected String type;
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("canonicalName="+canonicalName);
		sb.append(",name="+name);
		sb.append(",state="+state);
		sb.append(",type="+type);
		return sb.toString();
	}
	
	public static MessageBean getMessageBean(RMIAdaptor server,String objectName) throws MalformedObjectNameException, NullPointerException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException{
		ObjectName on = new ObjectName(objectName);
		return getMessageBean(server, on);
	}
	
	public static MessageBean getMessageBean(RMIAdaptor server,ObjectName on) throws MalformedObjectNameException, NullPointerException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException{
		String canonicalName = on.getCanonicalName();
		MessageBean bean = null;
		if(canonicalName.contains("service=Topic")){
			TopicBean tbean = new TopicBean();
			tbean.type = "Topic";
			tbean.allSubscriptionsCount = (Integer)(server.getAttribute(on, "AllSubscriptionsCount"));
			tbean.allMessageCount = (Integer)(server.getAttribute(on, "AllMessageCount"));
			bean = tbean;
		}else if(canonicalName.contains("service=Queue")){
			QueueBean qbean = new QueueBean();
			qbean.type = "Queue";
			qbean.receiversCount = (Integer)(server.getAttribute(on, "ReceiversCount"));
			qbean.queueDepth = (Integer)(server.getAttribute(on, "QueueDepth"));
			bean = qbean;
		}else{
			System.err.println(on.getCanonicalName());
			return null;
		}
		bean.canonicalName = canonicalName;
		bean.name = (String)(server.getAttribute(on, "Name"));
		bean.state = (Integer)(server.getAttribute(on, "State"));
		return bean;
	}
	
	public String getName() {
		return name;
	}

	public int getState() {
		return state;
	}

	public String getCanonicalName() {
		return canonicalName;
	}
}
