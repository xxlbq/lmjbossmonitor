package cn.bestwiz.tools.jboss.moitor.bean;

public class QueueBean extends MessageBean{
	
	int queueDepth;

	int receiversCount;
	
	public int getReceiversCount() {
		return receiversCount;
	}

	public int getQueueDepth() {
		return queueDepth;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		sb.append(",queueDepth="+queueDepth);
		sb.append(",receiversCount="+receiversCount);
		return sb.toString();
	}
}
