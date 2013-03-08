package com.renren.mail.monitor.email;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.renren.mail.monitor.model.EmailAccount;
import com.renren.mail.monitor.recever.NeteasyEmailRecever;
import com.renren.mail.monitor.strategy.IEmailAnalyseStrategy;

public class NeteasyEmail implements IEmail{

    private Log logger = LogFactory.getLog(getClass());

    /** 网易测试邮件的列表 */
    private List<EmailAccount> mailAccountList = new ArrayList<EmailAccount>();

    public void setMailAccountList(List<EmailAccount> mailAccountList) {
        this.mailAccountList = mailAccountList;
    }

    private IEmailAnalyseStrategy rateAnalyseStrategy;

    public void setRateAnalyseStrategy(IEmailAnalyseStrategy rateAnalyseStrategy) {
        this.rateAnalyseStrategy = rateAnalyseStrategy;
    }

    /**
     * 开始读取邮件并分析
     */
    @Override
    public void startAnalyse() {
        NeteasyEmailRecever receiver = new NeteasyEmailRecever();
        List<Message> inboxMessageList = new ArrayList<Message>();
        List<Message> dingyueMessageList = new ArrayList<Message>();
        List<Message> garbageMessageList = new ArrayList<Message>();
        for (EmailAccount account : mailAccountList) {
            try {
                Message[] inboxMessages = receiver.getMessages(account, "INBOX");
                addMessageToList(inboxMessages, inboxMessageList);
                Message[] dingyueMessages = receiver.getMessages(account, "订阅邮件");
                addMessageToList(dingyueMessages, dingyueMessageList);
                Message[] garbageMessages = receiver.getMessages(account, "垃圾邮件");
                addMessageToList(garbageMessages, garbageMessageList);
            } catch (Exception e) {
                logger.error("获取信息失败", e);
            }
        }
        rateAnalyseStrategy.analyse(inboxMessageList, dingyueMessageList, garbageMessageList);
        receiver.close();
    }

    private static void addMessageToList(Message[] messages, List<Message> messageList) {
        if (messages != null && messageList != null) {
            for (Message msg : messages) {
                messageList.add(msg);
            }
        }
    }

    public static void main(String[] args) {
        NeteasyEmail neteasyEmail = new NeteasyEmail();
        neteasyEmail.startAnalyse();
    }

}
