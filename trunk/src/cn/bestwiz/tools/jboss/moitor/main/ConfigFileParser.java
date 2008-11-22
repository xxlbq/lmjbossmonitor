package cn.bestwiz.tools.jboss.moitor.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import cn.bestwiz.tools.jboss.moitor.action.MailAction;


public class ConfigFileParser {
//	public static Log log = null;
	
	private static String destinationFileName = "jbossmq-destinations-service.xml";
	private static String jobFileName = "job.xml";
	private static String mailActionFileName = "mail-actions.xml";
	
	private static String mailActionRegex = ";";
	
	public static List<String> parseDestinationFile(){
		List<String> lstObjectName = new ArrayList<String>();
		
		String configPath = System.getProperty("configPath");
		File file = new File(configPath + File.separator + destinationFileName);
		XMLConfiguration config = null;
		try {
			config = new XMLConfiguration(file);
		} catch (ConfigurationException e) {
//			log.error("parseDestinationFile Failed", e);
			e.printStackTrace();
		}
		int total = config.getList("mbean[@name1]").size();
		for (int i = 0; i < total ; i++) {
			String name = config.getString("mbean("+i+")[@name]");
			lstObjectName.add(name);
		}
		return lstObjectName;
	}
	
	public static MailAction parseMailActionFile(String mailActionName){
		
		String configPath = System.getProperty("configPath");
		File file = new File(configPath + File.separator + mailActionFileName);
		XMLConfiguration config = null;
		try {
			config = new XMLConfiguration(file);
		} catch (ConfigurationException e) {
//			log.error("parseJobFile Failed", e);
			e.printStackTrace();
		}
		MailAction mailAction = null;
		int total = config.getList("mail[@name]").size();
		for (int i = 0; i < total ; i++) {
			String name = config.getString("mail("+i+")[@name]");
			if(mailActionName.equals(name)){
				mailAction = new MailAction();
				mailAction.setName(name);
				
				mailAction.setEncoding(config.getString("mail("+i+")[@encoding]"));

				mailAction.setSubject(config.getString("mail("+i+")[@subject]"));
				mailAction.setHost(config.getString("mail("+i+")[@host]"));
				mailAction.setFromAddress(config.getString("mail("+i+")[@fromAddress]"));
				mailAction.setToAddresss(config.getString("mail("+i+")[@toAddress]").split(mailActionRegex));
				
				String bccAdd = config.getString("mail("+i+")[@bccAddress]");
				mailAction.setBccAddresss(bccAdd==null?null:bccAdd.split(mailActionRegex));
				
				mailAction.setContentTemplete(config.getString("mail("+i+")[@content]"));
				
				
				}
		}
		
		return mailAction;
	}
	
	public static Map<String,Integer> parseJobFile(){
		Map<String,Integer> lstJobName = new HashMap<String, Integer>();
		
		String configPath = System.getProperty("configPath");
		File file = new File(configPath + File.separator + jobFileName);
		XMLConfiguration config = null;
		try {
			config = new XMLConfiguration(file);
		} catch (ConfigurationException e) {
//			log.error("parseJobFile Failed", e);
			e.printStackTrace();
		}
		int total = config.getList("job[@name]").size();
		for (int i = 0; i < total ; i++) {
			String name = config.getString("job("+i+")[@name]");
			Integer triggerCount = config.getInteger("job("+i+")[@triggercount]",0);
			lstJobName.put(name,triggerCount);
		}
		return lstJobName;
	}
	
	public static String parseMailContent(String fileName) throws IOException{
		String configPath = System.getProperty("configPath");
		File f = new File(configPath + File.separator + fileName);
		return getFileStringContent(f);
	}
	
	public static String getFileStringContent(File f) throws IOException{
		if(!f.isFile()){
			throw new UnsupportedOperationException(f.getName()+"is not a file!");
		}
		StringBuffer sb = new StringBuffer();
		FileInputStream fis = new FileInputStream(f);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		while (true) {
			String s = br.readLine();
			if( s == null){
				break;
			}
			sb.append(s).append("\n");
		}
		br.close();
		isr.close();
		fis.close();
		return sb.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(parseDestinationFile());
		System.out.println(parseJobFile());
	}
}
