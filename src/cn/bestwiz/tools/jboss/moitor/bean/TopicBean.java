package cn.bestwiz.tools.jboss.moitor.bean;

public class TopicBean extends MessageBean{
	
	int allMessageCount;

	int allSubscriptionsCount;
	
	public int getAllMessageCount() {
		return allMessageCount;
	}

	public int getAllSubscriptionsCount() {
		return allSubscriptionsCount;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		sb.append(",allMessageCount="+allMessageCount);
		sb.append(",allSubscriptionsCount="+allSubscriptionsCount);
		return sb.toString();
	}
}
