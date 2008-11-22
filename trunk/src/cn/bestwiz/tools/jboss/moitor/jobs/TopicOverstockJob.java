package cn.bestwiz.tools.jboss.moitor.jobs;

import cn.bestwiz.tools.jboss.moitor.JmsTopicMonitorJob;
import cn.bestwiz.tools.jboss.moitor.action.PerformAction;
import cn.bestwiz.tools.jboss.moitor.bean.TopicBean;

public class TopicOverstockJob implements JmsTopicMonitorJob{
	
	private int i;
	
	public void execute(TopicBean o) {
		// TODO Auto-generated method stub
		System.out.println("TopicOverstockJob execute!! "+o);
		try {
			if(o.getAllMessageCount()>=i){
				System.out.println("	"+o.getName()+" OverStock : AllMessageCount="+o.getAllMessageCount());
				PerformAction.sendOverStockMail(o);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setTriggerCount(int i) {
		this.i = i;
	}
}
