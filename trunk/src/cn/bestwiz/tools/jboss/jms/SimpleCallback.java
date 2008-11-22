package cn.bestwiz.tools.jboss.jms;

import java.io.Serializable;

/**
 * 
 * define jms callback used for SimpleReceiver
 * 
 * @author zhouhc <zhouhc@bestwiz.cn>
 * 
 * @copyright 2006, BestWiz(Dalian) Co.,Ltd
 * 
 * @version $Id: SimpleCallback.java,v 1.2 2007/11/07 06:03:47 panxy Exp $
 */
public interface SimpleCallback {

	/**
	 * <pre>
	 * 本方法用于处理普通的 JMS消息，你的处�? Message类需要实�? 该接口，如下�?
	 * class MySimpleCallback implements SimpleCallback {
	 * 		public void onMessage(Serializable msg) {
	 * 			//process
	 * 		}
	 * }
	 * </pre>
	 * @param message
	 *            JMS消息
	 */
	public void onMessage(Serializable message);

}
