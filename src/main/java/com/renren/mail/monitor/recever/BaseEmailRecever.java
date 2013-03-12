package com.renren.mail.monitor.recever;

import java.util.ArrayList;
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

public class BaseEmailRecever {

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
                    e.printStackTrace();
                }
            }
        }
        for (Store s : storeList) {
            if (s != null) {
                try {
                    s.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
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
}
