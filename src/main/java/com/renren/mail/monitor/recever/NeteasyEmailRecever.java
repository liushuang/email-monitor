package com.renren.mail.monitor.recever;

import java.io.IOException;
import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NeteasyEmailRecever extends BaseEmailRecever {
	
    private Log logger = LogFactory.getLog(getClass());
    
	/**
	 * 获取发送者emai地址
	 * @param msg Message
	 * @return
	 */
	public String getSenderMail(Message msg) {
		try {
			String from = msg.getFrom()[0].toString();
			return from.substring(from.indexOf("?=") + 3);
		} catch (MessagingException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 获取发送者姓名
	 * @param msg Message
	 * @return
	 */
	public String getSenderName(Message msg) {
		try {
			String from = msg.getFrom()[0].toString();
			int start = from.indexOf("?B?") + 3;
			int end = from.indexOf("?=");
			if(start < 0 || end < 0 || start < end){
			    return "unknown";
			}
			String author = from.substring(start, end);
			return base64Decoder(author);
		} catch (MessagingException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 获取message的收件人的地址
	 * @param msg
	 * @return
	 */
	public String getReceverEmail(Message msg) {
		try {
		    return msg.getAllRecipients()[0].toString();
		} catch (MessagingException e) {
			
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * BASE64解码
	 * @param s 被解码的String
	 * @return 解码后的String
	 */
	private String base64Decoder(String s) {
		sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
		byte[] b;
		try {
			b = decoder.decodeBuffer(s);
			return (new String(b, "utf-8"));
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 获取消息的文件夹的名称
	 * @param message 消息
	 * @return 文件夹的名称
	 */
    public String getFolderName(Message message) {
        String folderName =  message.getFolder().toString();
        if(folderName.equals("INBOX")){
            return "INBOX";
        }else if (folderName.equals("订阅邮件")){
            return "Dingyue";
        }else if (folderName.equals("垃圾邮件")){
            return "Garbage";
        }
        return "unknown";
    }

    public int getFolderNumber(Message message){
        String folderName =  message.getFolder().toString();
        if(folderName.equals("INBOX")){
            return 1;
        }else if (folderName.equals("订阅邮件")){
            return 2;
        }else if (folderName.equals("垃圾邮件")){
            return 3;
        }
        return 0;
    }
    /**
     * 获取发件人的域名
     * @param message message
     * @return 发件人的域名
     */
    public String getSenderDomain(Message message) {
        String sendermail = getSenderMail(message);
        int start = sendermail.indexOf("@") + 1;
        int end = sendermail.indexOf(">");
        if(start >=0  && start < end){
            return sendermail.substring(start,end);
        }else{
            return sendermail;
        }
    }

    /**
     * 获取收件人的域名
     * @param message message
     * @return
     */
    public String getReceverDomain(Message message) {
        String receverEmail = getReceverEmail(message);
        int start = receverEmail.indexOf("@") + 1;
        if(start >=0){
            return receverEmail.substring(start);
        }else{
            return receverEmail;
        }
    }

    /**
     * 获取发送邮件的时间
     * @param message message
     * @return
     */
    public Date getSendDate(Message message) {
        try {
            return message.getSentDate();
        } catch (MessagingException e) {
            logger.error("获取发送日期失败",e);
            return null;
        }
    }
	
	
}
