package com.renren.mail.monitor.sender;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renren.mail.monitor.biz.DailyReportBiz;
import com.renren.mail.monitor.biz.HourlyReportBiz;
import com.renren.mail.monitor.model.DetailReport;
import com.renren.mail.monitor.model.Report;
import com.renren.mail.monitor.model.ReportTable;

@Service
public class EmailSenderImpl implements IEmailSender {

    @Autowired
    private HourlyReportBiz hourlyReportBiz;

    @Autowired
    private DailyReportBiz dailyReportBiz;
    
    @Override
    public void sendDailyEmail() {
        // 获取收件人列表
        List<String> reportPersonList = getReportPersonList();
        // 获取报告需要的数据
        Report report = dailyReportBiz.getDailyReport();
        // 发送邮件
        sendEmail(reportPersonList, report);
    }

    @Override
    public void sendHourlyEmail() {
        // 获取收件人列表
        List<String> reportPersonList = getReportPersonList();
        // 获取报告需要的数据
        Report report = hourlyReportBiz.getHourlyReport();
        // 发送邮件
        sendEmail(reportPersonList, report);
    }
    /**
     * 获取配置的收件人的列表
     * 
     * @return 收件人列表
     */
    public List<String> getReportPersonList() {
        List<String> addressList = new ArrayList<String>();
        Properties prop = new Properties();
        InputStream in = getClass().getResourceAsStream("/reportPersonList.properties");
        try {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Set<Object> keyValue = prop.keySet();
        for (Iterator<Object> it = keyValue.iterator(); it.hasNext();) {
            addressList.add((String) it.next());
        }
        return addressList;
    }

    /**
     * 发送邮件
     * 
     * @param receverList 收件人列表
     * @param report 邮件data
     */
    private void sendEmail(List<String> receverList, Report report) {
        try {
            if (receverList != null) {

                //设置会话的属性
                Properties pro = new Properties();
                //发送邮件的服务器主机
                pro.put("mail.smtp.host", "smtp.126.com");
                //设置已经通过验证
                pro.put("mail.smtp.auth", "true");
                //根据属性创建一个会话
                Session session = Session.getInstance(pro);
                //由邮件会话新建一个消息对象
                Message message = new MimeMessage(session);
                //设置发信人的地址
                InternetAddress from = new InternetAddress("renrenmailmonitor@126.com");
                message.setFrom(from);
                //设置收件人的地址
                Address[] tos = null;
                tos = new InternetAddress[receverList.size()];
                for (int i = 0; i < receverList.size(); i++) {
                    tos[i] = new InternetAddress(receverList.get(i));
                }
                message.setRecipients(Message.RecipientType.TO, tos);
                //设置主题
                message.setSubject(report.getEmailSubject());

                // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象    
                Multipart mainPart = new MimeMultipart();
                // 创建一个包含HTML内容的MimeBodyPart    
                BodyPart html = new MimeBodyPart();
                // 设置HTML内容    
                html.setContent(getEmailContent(report), "text/html; charset=utf-8");
                mainPart.addBodyPart(html);
                message.setContent(mainPart);
                //设置发送的日期
                message.setSentDate(Calendar.getInstance().getTime());

                //取得实施发送的对象
                Transport transport = session.getTransport("smtp");
                //取得与邮件服务器的连接
                transport.connect("smtp.126.com", "renrenmailmonitor@126.com", "renren.com");
                //发送信息
                transport.sendMessage(message, message.getAllRecipients());
                transport.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取邮件的正文HTML内容
     * 
     * @param oneDayReport 数据
     * @return 邮件正文HTML内容
     */
    private String getEmailContent(Report oneDayReport) {
        StringBuilder sb = new StringBuilder();

        for (ReportTable reportTable : oneDayReport.getReportTableList()) {
            sb.append("<table style=\"border-collapse: separate; border-radius: 4px 4px 4px 4px; border:1px solid #DDDDDD; border-left: none; border-top: none;\" >");
            sb.append("<thead>");
            sb.append("<tr>");
            sb.append(
                    "<th style=\"border-left: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD;\">")
                    .append("时间").append("</th>");
            sb.append(
                    "<th style=\"border-left: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD;\">")
                    .append("DiscId").append("</th>");
            sb.append(
                    "<th style=\"border-left: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD;\">")
                    .append("发件人域名").append("</th>");
            sb.append(
                    "<th style=\"border-left: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD;\">")
                    .append("收件人域名").append("</th>");
            sb.append(
                    "<th style=\"border-left: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD;\" >")
                    .append("总邮件数").append("</th>");
            sb.append(
                    "<th style=\"border-left: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD;\" >")
                    .append("收件箱数").append("</th>");
            sb.append(
                    "<th style=\"border-left: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD;\" >")
                    .append("订阅邮件数").append("</th>");
            sb.append(
                    "<th style=\"border-left: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD;\" >")
                    .append("垃圾邮件数").append("</th>");
            sb.append(
                    "<th style=\"border-left: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD;\" >")
                    .append("收件箱率").append("</th>");
            sb.append(
                    "<th style=\"border-left: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD;\" >")
                    .append("订阅邮件率").append("</th>");
            sb.append(
                    "<th style=\"border-left: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD;\" >")
                    .append("垃圾邮件率").append("</th>");
            sb.append("</tr>");
            sb.append("</thead>");
            boolean isodd = true;
            for (DetailReport oneHourReport : reportTable.getOneHourReportList()) {
                if (isodd) {
                    sb.append("<tr style=\"background-color: #F9F9F9;\">");
                } else {
                    sb.append("<tr>");
                }
                sb.append(
                        "<td style=\"border-left: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD;\" >")
                        .append(oneHourReport.getTime()).append("</td>");
                sb.append(
                        "<td style=\"border-left: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD;\" >")
                        .append(oneHourReport.getDiscId()).append("</td>");
                sb.append(
                        "<td style=\"border-left: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD;\" >")
                        .append(oneHourReport.getSenderDomain()).append("</td>");
                sb.append(
                        "<td style=\"border-left: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD;\" >")
                        .append(oneHourReport.getReceverDomain()).append("</td>");
                sb.append(
                        "<td style=\"border-left: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD;\" >")
                        .append(oneHourReport.getTotalCount()).append("</td>");
                sb.append(
                        "<td style=\"border-left: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD;\" >")
                        .append(oneHourReport.getInboxCount()).append("</td>");
                sb.append(
                        "<td style=\"border-left: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD;\" >")
                        .append(oneHourReport.getDingyueCount()).append("</td>");
                sb.append(
                        "<td style=\"border-left: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD;\" >")
                        .append(oneHourReport.getGarbageCount()).append("</td>");
                sb.append(
                        "<td style=\"border-left: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD;\" >")
                        .append(String.format("%.3f", oneHourReport.getInboxRate()))
                        .append("</td>");
                sb.append(
                        "<td style=\"border-left: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD;\" >")
                        .append(String.format("%.3f", oneHourReport.getDingyueRate()))
                        .append("</td>");
                sb.append(
                        "<td style=\"border-left: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD;\" >")
                        .append(String.format("%.3f", oneHourReport.getGarbageRate()))
                        .append("</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        
    }

    

}
