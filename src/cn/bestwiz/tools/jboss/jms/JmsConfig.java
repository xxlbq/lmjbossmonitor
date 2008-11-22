package cn.bestwiz.tools.jboss.jms;


/**
 * 
 * @author panxy
 */
public class JmsConfig {
//    private static Log m_log = LogUtil.getLog(JmsConfig.class);
	
	private static String vendorName = "vendorName";
	private static String providerName = "providerName";
	public static String vendorUrl = "vendorUrl";
	
	
	public static Provider getProvider(String url,String dest){
		// vendor
		Vendor vendor = new Vendor();
		vendor.setName(vendorName);
		vendor.setUrl(url);
		vendor.setStatus(0);
		
		// provider
		Provider provider = new Provider();
		provider.setName(providerName);
		provider.setMasterVendor(vendor);
		provider.setSlaveVendor(vendor);
		provider.setUsingProvider(0);
		return provider;
	}
	
	public static void main(String[] args) {
		System.setProperty(vendorUrl, "10.15.1.28");
//		System.out.println(getProvider("").getMasterVendor().getUrl());
	}
}
