package cn.bestwiz.tools.jboss.moitor.jobs;

import cn.bestwiz.tools.jboss.moitor.JmsQueueMonitorJob;
import cn.bestwiz.tools.jboss.moitor.action.PerformAction;
import cn.bestwiz.tools.jboss.moitor.bean.QueueBean;

public class QueueOverstockJob implements JmsQueueMonitorJob{

	private int i;
	
	public void execute(QueueBean o) {
		// TODO Auto-generated method stub
		System.out.println("QueueOverstockJob execute!! "+o);
		try {
			if(o.getQueueDepth()>=i){
				System.out.println("	"+o.getName()+" OverStock : QueueDepth="+o.getQueueDepth());
				PerformAction.sendOverStockMail(o);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		if(o.getQueueDepth()>0){
//			PerformAction.sendMail();
//		}
	}
	
	public void setTriggerCount(int i) {
		this.i = i;
	}
}
