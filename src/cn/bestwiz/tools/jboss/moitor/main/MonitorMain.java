package cn.bestwiz.tools.jboss.moitor.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.management.ObjectName;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.jmx.adaptor.rmi.RMIAdaptor;

import cn.bestwiz.tools.jboss.moitor.JmsMonitorJob;
import cn.bestwiz.tools.jboss.moitor.bean.MessageBean;
import cn.bestwiz.tools.jboss.moitor.service.Service;

public class MonitorMain {
	public static String IP_ADDRESS;
	// 
	private static List<JmsMonitorJob> getJobs() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		Map<String,Integer> lstJobName = ConfigFileParser.parseJobFile();
		List<JmsMonitorJob> lstJobs = new ArrayList<JmsMonitorJob>();
		for (Map.Entry<String,Integer> jobName : lstJobName.entrySet()) {
			JmsMonitorJob job = (JmsMonitorJob)Class.forName(jobName.getKey()).newInstance();
			job.setTriggerCount(jobName.getValue());
			lstJobs.add(job);
		}
		return lstJobs;
	}
	
	//
	@SuppressWarnings("unchecked")
	private static List<ObjectName> getObjectNames(RMIAdaptor server) throws IOException{
		List<ObjectName> lst = new ArrayList<ObjectName>();
		Set<ObjectName> set = server.queryNames(null, null);
		for (ObjectName name : set) {
			if(name.getDomain().equals("jboss.mq.destination")){
				lst.add(name);
			}
		}
		return lst;
	}
	
	private static RMIAdaptor getRMIAdaptor(String ipAddress) throws NamingException{
        Properties pro = new Properties();
        pro.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        pro.setProperty("java.naming.provider.url", ipAddress);
        pro.setProperty("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
        InitialContext ic = new InitialContext(pro);
        RMIAdaptor server = (RMIAdaptor) ic.lookup("jmx/rmi/RMIAdaptor");
        return server;
	}
	
	public static void main(String[] args) throws Exception {
		// 1. init args
		// Ip
		IP_ADDRESS = args[0];
		System.out.println("IP_ADDRESS="+IP_ADDRESS);
		// Jobs
		List<JmsMonitorJob> lstJobs = getJobs();
		// RMIAdaptor server
		RMIAdaptor server = getRMIAdaptor(IP_ADDRESS);
		// ObjectName
		List<ObjectName> lstObjectName = getObjectNames(server);
		// Monitor Service
        Service service = new Service(lstJobs);
		// 3. bussiness handle
		
			// For Each Dest 
				// 1. init bean
				// 2. doing our bussiness 
        
        for (ObjectName name : lstObjectName) {
        	MessageBean b = MessageBean.getMessageBean(server, name);
        	if(b!=null){
        		service.doJobs(b);
        	}
		}
        
		// 4. finish
        

    }
}
