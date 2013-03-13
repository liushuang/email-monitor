package com.renren.mail.monitor.strategy;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.mail.Flags.Flag;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.renren.mail.monitor.dao.EmailMonitorRecordDAO;
import com.renren.mail.monitor.disc.ChangeDisc;
import com.renren.mail.monitor.logger.EmailLogger;
import com.renren.mail.monitor.model.DiscResult;
import com.renren.mail.monitor.model.EmailMonitorRecord;
import com.renren.mail.monitor.recever.NeteasyEmailRecever;

public class RateAnalyseStrategy implements IEmailAnalyseStrategy {

    private Log logger = LogFactory.getLog(getClass());

    @Autowired
    private NeteasyEmailRecever neteasyEmailRecever;

    @Autowired
    private EmailMonitorRecordDAO emailMonitorRecordDao;

    private List<String> senderDomainList;

    public void setSenderDomainList(List<String> senderDomainList) {
        this.senderDomainList = senderDomainList;
    }

    @SuppressWarnings("unused")
    @Autowired
    private ChangeDisc changeDisc;

    @Override
    public void analyse(List<Message> inboxMessages, List<Message> dingyueMessages,
            List<Message> garbageMessages) {

        Map<Integer, DiscResult> resultMap = new HashMap<Integer, DiscResult>();
        try {
            makeReceiveResultMap(inboxMessages, resultMap, "INBOX");
            makeReceiveResultMap(dingyueMessages, resultMap, "订阅邮件");
            makeReceiveResultMap(garbageMessages, resultMap, "垃圾邮件");
            autoChangeDiscId(resultMap);
            // 不再按照小时进行记录了
            // recordResultLog(resultMap);
        } catch (MessagingException e) {
            logger.error("分析邮件错误", e);
        }

    }

    /**
     * 将查询得到的记录保存至数据库中
     * 
     * @param resultMap 查询得到的记录
     */
    private void insertIntoDataBase(EmailMonitorRecord emailMonitorRecord) {
        try {
            emailMonitorRecordDao.insertEmailMonitorRecord(emailMonitorRecord);
        } catch (Exception e) {
            logger.error("向表中插入数据错误", e);
        }
    }

    private void autoChangeDiscId(Map<Integer, DiscResult> resultMap) {
        //        System.out.println("*********************before change disc");
        //        changeDisc.changeDiscId(31210221, 31210222);
        //        System.out.println("*********************after chage disc");

    }

    /**
     * 将messageList分析后加入到resultMap中
     * 
     * @param messageList
     * @param resultMap
     * @param folderName
     * @throws MessagingException
     */
    private void makeReceiveResultMap(List<Message> messageList,
            Map<Integer, DiscResult> resultMap, String folderName) throws MessagingException {
        for (Message message : messageList) {
            if (!message.getFlags().contains(Flag.SEEN)) {
                try {
                    recordActionLog(message);
                    if (isSendByRenren(message.getFrom()[0].toString())) {
                        EmailMonitorRecord emailMonitorRecord = makeEmailMonitorRecordFormMessage(message);
                        addOneMessageToResult(message, resultMap, folderName);
                        insertIntoDataBase(emailMonitorRecord);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                message.setFlag(Flag.SEEN, true);
            }
        }
    }

    /**
     * 将一个Message放入DiscResultMap中
     * 
     * @param message
     * @param resultMap
     */
    private void addOneMessageToResult(Message message, Map<Integer, DiscResult> resultMap,
            String folderName) {
        int discId = 0;
        try {
            discId = Integer.valueOf(neteasyEmailRecever.getEmailSubject(message));
        } catch (Exception e) {
            logger.error("获取模板号失败,subject:" + neteasyEmailRecever.getEmailSubject(message));
            return;
        }
        DiscResult discResult = null;
        if (resultMap.containsKey(discId)) {
            discResult = resultMap.get(discId);

        } else {// 没有保存过DiscResult
            discResult = new DiscResult(discId);
            resultMap.put(discId, discResult);
        }
        if (folderName.equals("INBOX")) {
            discResult.setInboxCount(discResult.getInboxCount() + 1);
        } else if (folderName.equals("订阅邮件")) {
            discResult.setDingyueCount(discResult.getDingyueCount() + 1);
        } else if (folderName.equals("垃圾邮件")) {
            discResult.setGarbageCount(discResult.getGarbageCount() + 1);
        }
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
     * 记录最终结果
     * 
     * @param logText
     */
    @SuppressWarnings("unused")
    private void recordResultLog(Map<Integer, DiscResult> resultMap) {

        if (resultMap.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (Entry<Integer, DiscResult> li : resultMap.entrySet()) {
                sb.append("disc_id: ").append(li.getKey()).append("\n");
                DiscResult result = li.getValue();
                sb.append("Total:\t").append(result.getTotalCount()).append("\tInboxCount\t")
                        .append(result.getInboxCount()).append("\tDingyueCount\t")
                        .append(result.getDingyueCount()).append("\tGarbageCount\t")
                        .append(result.getGarbageCount()).append("\t");
                sb.append("InboxRate\t").append(String.format("%.4f", result.getInboxRate()))
                        .append("\tDingyueRate\t")
                        .append(String.format("%.4f", result.getDingyueRate()))
                        .append("\tGarbageRate\t")
                        .append(String.format("%.4f", result.getGarbageRate())).append("\n\n");
            }
            EmailLogger.resultLog(sb.toString());
        } else {
            EmailLogger.resultLog("No new email receved");
        }
    }

    /**
     * 记录查询邮箱动作
     * 
     * @param message
     * @throws MessagingException
     */
    private void recordActionLog(Message message) throws MessagingException {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sb.append("ReceverEmail:").append(neteasyEmailRecever.getReceverEmail(message))
                .append("\t");
        sb.append("Subject:").append(neteasyEmailRecever.getEmailSubject(message)).append("\t");
        sb.append("Author:").append(neteasyEmailRecever.getSenderName(message)).append("\t");
        sb.append("FromEmail:").append(neteasyEmailRecever.getSenderMail(message)).append("\t");
        sb.append("SendDate:").append(sdf.format(message.getSentDate())).append("\t");
        sb.append("FolderName:").append(neteasyEmailRecever.getFolderName(message)).append("\n");
        EmailLogger.actionLog(sb.toString());
    }

    private EmailMonitorRecord makeEmailMonitorRecordFormMessage(Message message) {
        EmailMonitorRecord emailMonitorRecord = new EmailMonitorRecord();
        emailMonitorRecord.setSenderDomain(neteasyEmailRecever.getSenderDomain(message));
        emailMonitorRecord.setSenderEmail(neteasyEmailRecever.getSenderMail(message));
        emailMonitorRecord.setSenderName(neteasyEmailRecever.getSenderName(message));
        emailMonitorRecord.setSubject(neteasyEmailRecever.getEmailSubject(message));
        emailMonitorRecord.setFolder(neteasyEmailRecever.getFolderNumber(message));
        emailMonitorRecord.setReceverDomain(neteasyEmailRecever.getReceverDomain(message));
        emailMonitorRecord.setReceverEmail(neteasyEmailRecever.getReceverEmail(message));
        emailMonitorRecord.setSendDate(neteasyEmailRecever.getSendDate(message));
        emailMonitorRecord.setCheckDate(Calendar.getInstance().getTime());
        emailMonitorRecord.setSenderIp(neteasyEmailRecever.getSenderIp(message));

        return emailMonitorRecord;
    }
}
