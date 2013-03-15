package com.renren.mail.monitor.email;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.mail.Flags.Flag;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.renren.mail.monitor.dao.EmailMonitorRecordDAO;
import com.renren.mail.monitor.logger.EmailLogger;
import com.renren.mail.monitor.model.EmailAccount;
import com.renren.mail.monitor.model.EmailMonitorRecord;
import com.renren.mail.monitor.model.NeteasyFolderEnum;
import com.renren.mail.monitor.reciver.NeteasyEmailReciver;

public class NeteasyEmail implements IEmail {

    private Log logger = LogFactory.getLog(getClass());

    /** 网易测试邮件的列表 */
    private List<EmailAccount> mailAccountList = new ArrayList<EmailAccount>();

    @Autowired
    private NeteasyEmailReciver neteasyEmailReciver;

    /** 人人的发件人域名列表 */
    private List<String> senderDomainList;

    @Autowired
    private EmailMonitorRecordDAO emailMonitorRecordDao;


//    public void setNeteasyEmailRecever(NeteasyEmailReciver neteasyEmailReciver) {
//        this.neteasyEmailReciver = neteasyEmailReciver;
//    }
    
    public void setMailAccountList(List<EmailAccount> mailAccountList) {
        this.mailAccountList = mailAccountList;
    }

    public void setSenderDomainList(List<String> senderDomainList) {
        this.senderDomainList = senderDomainList;
    }

    /**
     * 开始读取邮件并分析
     */
    @Override
    public void startRecive() {
        // 接收邮件并写入DB，写log
        reciveMail();
        // 关闭网易邮件接收者
        neteasyEmailReciver.close();
    }

    /**
     * 接受邮件，包括获取不同文件夹中的邮件，写log，写DB等。
     */
    private void reciveMail() {
        List<String> folderNameList = NeteasyFolderEnum.getFolderList();
        // 遍历测试帐号获取邮件
        for (EmailAccount account : mailAccountList) {
            // 分别获取INBOX/订阅邮件/垃圾邮件
            for (String folderName : folderNameList) {
                try {
                    // 获取邮件
                    Message[] messages = neteasyEmailReciver.getMessages(account, folderName);
                    // 处理邮件，包括入DB，写log.
                    dealMessages(messages);
                } catch (Exception e) {
                    logger.error("获取帐号邮件失败,accountUserName" + account.getUserName(), e);
                }
            }
        }
    }

    /**
     * 处理邮件，包括入DB，写log。并将邮件标记为已读
     * 
     * @param messages 邮件
     * @throws MessagingException
     */
    private void dealMessages(Message[] messageList) throws MessagingException {
        for (Message message : messageList) {
            // 如果是未读邮件，则进行处理
            if (!message.getFlags().contains(Flag.SEEN)) {
                try {
                    if (isSendByRenren(message.getFrom()[0].toString())) {
                        // 根据邮件创建EmailMonitorRecord对象
                        EmailMonitorRecord emailMonitorRecord = makeEmailMonitorRecordFormMessage(message);
                        // 将结果插入数据库
                        insertIntoDataBase(emailMonitorRecord);
                        // 打印处理的log
                        recordActionLog(emailMonitorRecord);
                    }
                } catch (Exception e) {
                    logger.error("记录邮件内容错误", e);
                }
                // 标记为已读
                message.setFlag(Flag.SEEN, true);
            }
        }
    }

    /**
     * 由Message获取EmailMonitorRecord对象
     * 
     * @param message 邮件
     * @return EmailMonitorRecord对象
     */
    private EmailMonitorRecord makeEmailMonitorRecordFormMessage(Message message) {
        EmailMonitorRecord emailMonitorRecord = new EmailMonitorRecord();
        emailMonitorRecord.setSenderDomain(neteasyEmailReciver.getSenderDomain(message));
        emailMonitorRecord.setSenderEmail(neteasyEmailReciver.getSenderMail(message));
        emailMonitorRecord.setSenderName(neteasyEmailReciver.getSenderName(message));
        emailMonitorRecord.setSubject(neteasyEmailReciver.getEmailSubject(message));
        emailMonitorRecord.setFolder(neteasyEmailReciver.getFolderNumber(message));
        emailMonitorRecord.setReceverDomain(neteasyEmailReciver.getReceverDomain(message));
        emailMonitorRecord.setReceverEmail(neteasyEmailReciver.getReceverEmail(message));
        emailMonitorRecord.setSendDate(neteasyEmailReciver.getSendDate(message));
        emailMonitorRecord.setCheckDate(Calendar.getInstance().getTime());
        emailMonitorRecord.setSenderIp(neteasyEmailReciver.getSenderIp(message));

        return emailMonitorRecord;
    }

    /**
     * 判断是否是人人发出的邮件
     * 
     * @param messageFrom
     * @return
     */
    private boolean isSendByRenren(String messageFrom) {
        for (String domain : senderDomainList) {
            if (messageFrom.contains(domain)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 记录查询邮箱动作
     * 
     * @param message 消息
     * @throws MessagingException
     */
    private void recordActionLog(EmailMonitorRecord emailMonitorRecord) throws MessagingException {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sb.append("ReceverEmail:").append(emailMonitorRecord.getReceverEmail()).append("\t");
        sb.append("Subject:").append(emailMonitorRecord.getSubject()).append("\t");
        sb.append("Author:").append(emailMonitorRecord.getSenderName()).append("\t");
        sb.append("FromEmail:").append(emailMonitorRecord.getSenderEmail()).append("\t");
        sb.append("SendDate:").append(sdf.format(emailMonitorRecord.getSendDate())).append("\t");
        sb.append("FolderName:").append(emailMonitorRecord.getFolder()).append("\n");
        EmailLogger.actionLog(sb.toString());
    }

    /**
     * 将查询得到的记录保存至数据库中
     * 
     * @param emailMonitorRecord 查询得到的记录
     */
    private void insertIntoDataBase(EmailMonitorRecord emailMonitorRecord) {
        try {
            emailMonitorRecordDao.insertEmailMonitorRecord(emailMonitorRecord);
        } catch (Exception e) {
            logger.error("向表中插入数据错误", e);
        }
    }

    public static void main(String[] args) {
        NeteasyEmail neteasyEmail = new NeteasyEmail();
        neteasyEmail.startRecive();
    }

}
