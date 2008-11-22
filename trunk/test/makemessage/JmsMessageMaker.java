package makemessage;

import java.util.Date;
import java.util.Iterator;

import cn.bestwiz.tools.jboss.jms.SimpleSender;
import cn.bestwiz.tools.jboss.mqtest.main.MqTester;

public class JmsMessageMaker extends MqTester{
	protected static String defaultDest = "queue/gwGsTQRequestQueue" ;
	
	protected static void sendMessage(String dest){
		try {
//			sender = SimpleSender.getInstance(dest);
			sender.sendMessage(testMessage);
			System.out.println("sending time: "+format.format(new Date()));
			sentCount++;
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
		
		for (int i = 0; i < 10; i++) {
			sendMessage(defaultDest);
		}
		
		exit(1000*5);
	}
}
