package com.renren.mail.monitor.reciver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.renren.mail.monitor.model.EmailAccount;
import com.renren.mail.monitor.model.NeteasyFolderEnum;

public abstract class BaseEmailReciver {

    private Log logger = LogFactory.getLog(getClass());

    private List<Folder> folderList = new ArrayList<Folder>();

    private List<Store> storeList = new ArrayList<Store>();

    // 连接邮件服务器，获得所有邮件的列表
    public Message[] getMessages(EmailAccount account, String folderName) throws Exception {
        Folder folder = null;
        Store store = null;
        Properties prop = new Properties();
        prop.put("mail.imap.host", account.getHost());
        Session session = Session.getDefaultInstance(prop);

        store = session.getStore("imap");
        store.connect(account.getHost(), account.getUserName(), account.getPassword());

        folder = store.getFolder(folderName);
        folder.open(Folder.READ_WRITE);

        // 查找未读的邮件
        Message[] msg = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

        // 将folder和store存入list中，方便以后close
        folderList.add(folder);
        storeList.add(store);
        return msg;
    }

    /**
     * 获取邮件主题
     * 
     * @param msg
     * @return
     */
    public String getEmailSubject(Message msg) {
        try {
            return msg.getSubject();
        } catch (MessagingException e) {
            logger.error("获取邮件标题错误", e);
            return null;
        }
    }

    /**
     * 关闭各种连接
     */
    public void close() {
        for (Folder f : folderList) {
            if (f != null) {
                try {
                    f.close(false);
                } catch (MessagingException e) {
                    logger.error("关闭folder错误", e);
                }
            }
        }
        for (Store s : storeList) {
            if (s != null) {
                try {
                    s.close();
                } catch (MessagingException e) {
                    logger.error("关闭store错误", e);
                }
            }
        }
        folderList.clear();
        storeList.clear();
    }

    /**
     * 获取发件人的ip信息
     * 
     * @param msg
     * @return ip的字符串
     */
    public String getSenderIp(Message msg) {
        String header = null;
        try {
            header = msg.getHeader("Received")[0];
        } catch (MessagingException e) {
            logger.error("获取 header Received[0]信息出错", e);
        }
        Pattern ipPattern = Pattern
                .compile("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");
        Matcher mat = ipPattern.matcher(header);
        // 如果含有ip信息
        if (mat.find()) {
            return mat.group(0);
        } else {
            return null;
        }
    }
    

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
            logger.error("获取发件人失败",e);
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
            if(start < 0 || end < 0 || start > end){
                return "unknown";
            }
            String author = from.substring(start, end);
            return base64Decoder(author);
        } catch (MessagingException e) {
            logger.error("获取发件人姓名失败",e);
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
            logger.error("获取收件人地址失败",e);
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
            logger.error("base64Decoder失败"+e);
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

    /**
     * 获取邮件所在文件夹的数字代码
     * @param message 邮件
     * @return
     */
    public int getFolderNumber(Message message){
        String folderName =  message.getFolder().toString();
        return NeteasyFolderEnum.getFolderNumberByName(folderName);
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
