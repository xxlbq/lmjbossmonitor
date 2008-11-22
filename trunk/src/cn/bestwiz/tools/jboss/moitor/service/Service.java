package cn.bestwiz.tools.jboss.moitor.service;

import java.util.ArrayList;
import java.util.List;

import cn.bestwiz.tools.jboss.moitor.JmsAllMonitorJob;
import cn.bestwiz.tools.jboss.moitor.JmsMonitorJob;
import cn.bestwiz.tools.jboss.moitor.JmsQueueMonitorJob;
import cn.bestwiz.tools.jboss.moitor.JmsTopicMonitorJob;
import cn.bestwiz.tools.jboss.moitor.bean.MessageBean;
import cn.bestwiz.tools.jboss.moitor.bean.QueueBean;
import cn.bestwiz.tools.jboss.moitor.bean.TopicBean;

public class Service {
	
	// Jobs
	private List<JmsQueueMonitorJob> lstQueueJob = new ArrayList<JmsQueueMonitorJob>();
	private List<JmsTopicMonitorJob> lstTopicJob = new ArrayList<JmsTopicMonitorJob>();
	private List<JmsAllMonitorJob> lstAllJob = new ArrayList<JmsAllMonitorJob>();
	
	public Service(List<JmsMonitorJob> lstJobs){
		// seperate jobs
		for (JmsMonitorJob job : lstJobs) {
			if (job instanceof JmsQueueMonitorJob) {
				JmsQueueMonitorJob qJob = (JmsQueueMonitorJob) job;
				lstQueueJob.add(qJob);
			}else if (job instanceof JmsTopicMonitorJob) {
				JmsTopicMonitorJob tJob = (JmsTopicMonitorJob) job;
				lstTopicJob.add(tJob);
			}else if (job instanceof JmsAllMonitorJob) {
				JmsAllMonitorJob aJob = (JmsAllMonitorJob) job;
				lstAllJob.add(aJob);
			}else {
				// TODO Error
			}
		}
	}
	
	public void doJobs(MessageBean bean){
		this.doAllJob(bean);
		if (bean instanceof QueueBean){
			this.doQueueJob((QueueBean)bean);
		}else if(bean instanceof TopicBean){
			this.doTopicJob((TopicBean)bean);
		}
	}
	
	private void doAllJob(MessageBean mBean){
		for (JmsAllMonitorJob job : lstAllJob) {
			job.execute(mBean);
		}
	}
	
	private void doQueueJob(QueueBean qBean){
		for (JmsQueueMonitorJob job : lstQueueJob) {
			job.execute(qBean);
		}
	}
	
	private void doTopicJob(TopicBean tBean){
		for (JmsTopicMonitorJob job : lstTopicJob) {
			job.execute(tBean);
		}
	}
}
