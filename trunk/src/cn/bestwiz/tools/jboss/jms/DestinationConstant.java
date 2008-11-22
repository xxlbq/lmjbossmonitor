package cn.bestwiz.tools.jboss.jms;

/**
*
* define all jms destination 
* 				
* @author  zhouhc <zhouhc@bestwiz.cn> 			
* 				
* @copyright 2006, BestWiz(Dalian) Co.,Ltd		
* 	
* @version $Id: DestinationConstant.java,v 1.2 2007/11/07 06:03:47 panxy Exp $  	
*/
public interface DestinationConstant {
	//topic
	public final static  String AdminTopic = "topic/AdminTopic";
	public final static  String ClearDaoCacheTopic ="topic/ClearDaoCacheTopic";
	public final static  String CustomerRateTopic ="topic/CustomerRateTopic";
	public final static  String TimeQuoteResponseTopic="topic/TimeQuoteResponseTopic";
	public final static  String OrderResponseTopic="topic/OrderResponseTopic";
	public final static  String gwCounterpartyRateTopic="topic/gwCounterPartyRateTopic";
	public final static  String CustomerRate4RateCacheTopic="topic/CustomerRate4RateCacheTopic";
	// Modify by zuolin 2006.10.09
	public final static  String CpRate4RateCacheTopic = "topic/CpRate4RateCacheTopic"; 
	public final static  String CpRate4TradeTopic = "topic/CpRate4TradeTopic";	
	public final static String CPStatusMessageTopic = "topic/CPStatusMessageTopic";
    //added by mengfj 2006/11/13
    public final static String TradeResultTopic = "topic/TradeResultTopic";
    //add by mengfj 2006/11/18
    public final static String DealQuoteResponseTopic = "topic/DealQuoteResponseTopic";
    //add by wenyi 2006/12/28
    public final static String DealerLogMessageTopic = "topic/DealerLogMessageTopic";
    // add by zuolin 2006.12.28
    public final static String OrderResponseListTopic = "topic/OrderResponseListTopic";
	
	//queue
	public final static String TimeQuoteRequestQueue="queue/TimeQuoteRequestQueue";
	public final static String OrderRequestQueue="queue/OrderRequestQueue";	
	// for Gateway's trade begin==>
	public final static String gwTimeQuoteResponseQueue="queue/gwTimeQuoteResponseQueue";	
	public final static String gwOrderResponseQueue="queue/gwOrderResponseQueue";
	
	public final static String gsDbTQRequestQueue = "queue/gwDbTQRequestQueue";
	public final static String gwDbOrderRequestQueue="queue/gwDbOrderRequestQueue";	
	
	public final static String gsGsTQRequestQueue = "queue/gwGsTQRequestQueue";
	public final static String gwGsOrderRequestQueue = "queue/gwGsOrderRequestQueue";
	
	public final static String gwMpOrderRequestQueue = "queue/gwMpOrderRequestQueue";
	
	public final static String gsMockTQRequestQueue = "queue/gwMockTQRequestQueue";	
	public final static String gwMockOrderRequestQueue = "queue/gwMockOrderRequestQueue";
	// <==for Gateway's trade end
	public final static String coverRequestQueue="queue/coverRequestQueue";
	public final static String opmQueue="queue/opmQueue";
	public final static String rpmMailQueue="queue/rpmMailQueue";
	public final static String tsoQueue="queue/tsoQueue";
	// Modify by zuolin 2006.10.09
	public final static String CpOpmQueue ="queue/CpOpmQueue";
	public final static String DealRequestQueue = "queue/DealRequestQueue";
	public final static String TradeResultQueue = "queue/TradeResultQueue";
	public final static String ResendSpotRateQueue = "queue/ResendSpotRateQueue";
	// Modify by zuolin 2006.11.02
	public final static String ResendCpSpotRateQueue = "queue/ResendCpSpotRateQueue";
    
	//added by mengfj 2006/11/13
    public final static String DlManualRateQueue = "queue/DlManualRateQueue";
    
    //added by mengfj 2006/11/18
    public final static String DealQuoteRequestQueue = "queue/DealQuoteRequestQueue";
    
    //added by mengfj 2006/11/24
    public final static String gwDbTQRequestQueue = "queue/gwDbTQRequestQueue";
    public final static String gwGsTQRequestQueue = "queue/gwGsTQRequestQueue";
    public final static String gwMockTQRequestQueue = "queue/gwMockTQRequestQueue";
    
    //add by wangyan 2006/12/02
    public final static String CustSwapPointTopic = "topic/CustSwapPointTopic";
	/**
	 * Losscut用于接收汇率的queue
	 * Added by roger 2006.11.02
	 */ 
	public final static String LosscutRateQueue = "queue/losscutRateQueue";

	
}
