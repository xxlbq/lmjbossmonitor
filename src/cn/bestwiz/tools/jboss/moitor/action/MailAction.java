package cn.bestwiz.tools.jboss.moitor.action;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;

import cn.bestwiz.tools.jboss.moitor.main.ConfigFileParser;


public class MailAction {
	
    private static JavaMailSender javaMailSender =  new JavaMailSenderImpl();
    
    private String name;
    private String host;
    private String    subject;
    private String    contentTemplete;
    private String    fromAddress;
    private String[]  toAddresss;
    private String[]  bccAddresss;
    private String encoding = "SHIFT-JIS";
    
	/**<p>·¢ËÍÓÊ¼þ</p>
	 * @param subject - mail subject
	 * @param contentTemplete - mail content
	 * @param fromAddress - mail from address
	 * @param toAddresss - mail toAddresss
	 * @param bccAddresss - mail bccAddress
	 * @return boolean
	 * @throws 
	 */
	public void sendMail(Map<String, String> param) throws Exception {
		Properties prop = new Properties();
		prop.setProperty("mail.smtp.host", host);
		((JavaMailSenderImpl) javaMailSender).setJavaMailProperties(prop);
//		log.debug("======== CommonMailService sendMail() Start: ");
		String temp = ConfigFileParser.parseMailContent(this.getContentTemplete());
		
		Set<String> setKey = param.keySet();
		
		for (String key : setKey) {
			temp = temp.replace(key, param.get(key));
		}
		
		final String content = temp;
		
        try {
//    		m_MailServicelog.write("\n======== [Start Send Mail] ========\n" + 
//    				"\nsendMail() Start : " + 
//    				"\nsubject     : " + sSubject +
//    				"\ncontent     : " + sContent + 
//    				"\nfromAddress : " + sFromAddress + 
//    				"\ntoAddresss  : " + buildString(sToAddresss) + 
//    				"\nbccAddresss : " + buildString(sBccAddresss));
            MimeMessagePreparator mmp = new MimeMessagePreparator(){
                public void prepare(MimeMessage message) throws MessagingException{
                    if (subject != null)
                        message.setSubject(subject,encoding);

                    if (content != null)
                        message.setText(content,encoding);

                    if (fromAddress != null && fromAddress.trim().length() != 0)
                        message.setFrom(new InternetAddress(fromAddress));
                    
                    if (toAddresss != null && toAddresss.length != 0){
                        InternetAddress[] toAddressInter = new InternetAddress[toAddresss.length];
                        for(int i = 0 ; i < toAddressInter.length ; i++){
                            toAddressInter[i] = new InternetAddress(toAddresss[i]);
                        }
                        message.addRecipients(Message.RecipientType.TO, toAddressInter);
                    }
                    
                    if (bccAddresss != null && bccAddresss.length != 0){
                        InternetAddress[] bccAddressInter = new InternetAddress[bccAddresss.length];
                        for(int i = 0 ; i < bccAddressInter.length ; i++){
                            bccAddressInter[i] = new InternetAddress(bccAddresss[i]);
                        }
                        message.addRecipients(Message.RecipientType.BCC, bccAddressInter);
                    }
                }
            };
            javaMailSender.send(mmp);
//            m_log.debug("======== CommonMailService sendMail() End: ");
        } catch (Exception e) {
//            m_log.error("======== CommonMailService sendMail() Error: ", e);
        	e.printStackTrace();
        	throw e;
        }
	}

	// ============================================  Getter & Setter ================================================================

	public String[] getBccAddresss() {
		return bccAddresss;
	}

	public void setBccAddresss(String[] bccAddresss) {
		for (String string : bccAddresss) {
			if(string.equals("")){
				string = null;
			}
		}
		this.bccAddresss = bccAddresss;
	}

	public String getContentTemplete() {
		return contentTemplete;
	}

	public void setContentTemplete(String content) {
		this.contentTemplete = content;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String[] getToAddresss() {
		return toAddresss;
	}

	public void setToAddresss(String[] toAddresss) {
		this.toAddresss = toAddresss;
	}
}
