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

public class CopyOfService {
	
	// Message beans
	private List<QueueBean> lstQueueBean = new ArrayList<QueueBean>();
	private List<TopicBean> lstTopicBean = new ArrayList<TopicBean>();
	
	// Jobs
	private List<JmsQueueMonitorJob> lstQueueJob = new ArrayList<JmsQueueMonitorJob>();
	private List<JmsTopicMonitorJob> lstTopicJob = new ArrayList<JmsTopicMonitorJob>();
	private List<JmsAllMonitorJob> lstAllJob = new ArrayList<JmsAllMonitorJob>();
	
	public CopyOfService(List<MessageBean> lstBeans,List<JmsMonitorJob> lstJobs){
		// seperate beans
		for (MessageBean bean : lstBeans) {
			if (bean instanceof QueueBean) {
				QueueBean qBean = (QueueBean) bean;
				lstQueueBean.add(qBean);
			}else if (bean instanceof TopicBean) {
				TopicBean tBean = (TopicBean) bean;
				lstTopicBean.add(tBean);
			}else {
				// TODO Error
				
			}
		}
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
	
	public void doJobs(){
		for (QueueBean bean : lstQueueBean) {
			this.doAllJob(bean);
			this.doQueueJob(bean);
		}
		for (TopicBean bean : lstTopicBean) {
			this.doAllJob(bean);
			this.doTopicJob(bean);
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
