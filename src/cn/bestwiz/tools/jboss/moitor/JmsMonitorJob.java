package cn.bestwiz.tools.jboss.moitor;

public interface JmsMonitorJob<T> {
	void execute(T o);
	void setTriggerCount(int i);
}
