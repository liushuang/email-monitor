package com.renren.mail.monitor.dao;

import java.util.Date;
import java.util.List;

import com.renren.mail.monitor.model.EmailMonitorRecord;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;

/**
 * 对测试邮件进行记录和统计的DAO
 * @author liushuang
 * @creationDate 2013-03-07
 */
@DAO(catalog = "rrop_letter")
public interface EmailMonitorRecordDAO {

    /**
     * 根据id获取邮件记录
     * 
     * @param id id
     * @return 邮件记录
     */
    @SQL("select id , sender_domain ,sender_email, sender_name, subject, folder, "
            + " recever_domain, recever_email, recever_group, send_date, check_date "
            + " from email_monitor_record where id = :id")
    public EmailMonitorRecord selectEmailMonitorRecordById(@SQLParam("id") int id);

    /**
     * 向表中插入一条邮件记录
     * 
     * @param record 邮件记录
     */
    @SQL("insert into email_monitor_record ( sender_domain, sender_email, sender_name, subject, "
            + " folder, recever_domain, recever_email, recever_group, send_date, check_date )"
            + " values(:record.senderDomain, :record.senderEmail, :record.serderName, :record.subject, "
            + " :record.folder, :record.receverDomain, :record.receverEmail, "
            + " :record.receverGroup, :record.sendDate, :record.checkDate )")
    public void insertEmailMonitorRecord(@SQLParam("record") EmailMonitorRecord record);

    /**
     * 根据发送邮件的开始和结束时间获取邮件记录
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 邮件记录的List
     */
    @SQL("select id , sender_domain ,sender_email, sender_name, subject, folder, "
            + " recever_domain, recever_email, recever_group, send_date, check_date "
            + " from email_monitor_record where send_date between :startDate and :endDate ")
    public List<EmailMonitorRecord> selectEmailMonitorRecordBySendDate(
            @SQLParam("startDate") Date startDate, @SQLParam("endDate") Date endDate);

    /**
     * 根据发送邮件的开始和结束时间以及收件箱获取邮件数目
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param folder 投递到的邮件箱
     * @return 对应的邮件的数目
     */
    @SQL("select count(1) from email_monitor_record where send_date between :startDate and :endDate and folder = :folder")
    public int selectCountBySendDateAndFolder(@SQLParam("startDate") Date startDate,
            @SQLParam("endDate") Date endDate, @SQLParam("folder") int folder);

    /**
     * 根据发送邮件的开始和结束时间以及收件箱以及收件人域名获取邮件数目
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param folder 投递到的邮件箱
     * @param receverDomain 收件人的域名
     * @return 对应的邮件的数目
     */
    
    @SQL("select count(1) from email_monitor_record where send_date between :startDate and :endDate "
            + " and folder = :folder and recever_domain = :receverDomain")
    public int selectCountBySendDateAndFolderAndReceverDomain(
            @SQLParam("startDate") Date startDate, @SQLParam("endDate") Date endDate,
            @SQLParam("folder") int folder, @SQLParam("receverDomain") String receverDomain);
}
