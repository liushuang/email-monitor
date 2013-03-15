package com.renren.mail.monitor.biz.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renren.mail.monitor.biz.HourlyReportBiz;
import com.renren.mail.monitor.dao.EmailMonitorRecordDAO;
import com.renren.mail.monitor.model.Report;
import com.renren.mail.monitor.model.DetailReport;
import com.renren.mail.monitor.model.ReportTable;

/**
 * 用于组装每小时发送的报告
 * 
 * @author liushuang
 * 
 */
@Service
public class HourlyReportBizImpl implements HourlyReportBiz {

    private Log logger = LogFactory.getLog(getClass());

    @Autowired
    private EmailMonitorRecordDAO emailMonitorRecordDAO;

    @Override
    public Report getHourlyReport() {
        Report report = new Report();
        Date startDate = get3hBeforeDate();
        Date endDate = getCurrentDate();
        // 获取最近3小时的discid
        List<Integer> discIdList = emailMonitorRecordDAO
                .selectSubjectListByDate(startDate, endDate);
        // 获取最近3小时的senderDomain
        List<String> senderDomainList = emailMonitorRecordDAO.selectSenderDomainListByDate(
                startDate, endDate);
        // 获取最近3小时的receverDomain
        List<String> receverDomainList = emailMonitorRecordDAO.selectReceverDomainListByDate(
                startDate, endDate);

        for (int discId : discIdList) {
            for (String senderDomain : senderDomainList) {
                for (String receverDomain : receverDomainList) {
                    // 对每一个discid，每一个发送域名，每一个接受域名生成一个报表,添加到一天的报表中
                    ReportTable reportTable = makeReportTable(discId, senderDomain, receverDomain);
                    report.addReportTable(reportTable);
                }
            }
        }
        // 设置邮件标题
        makeReportEmailSubject(report);
        return report;
    }

    /**
     * 创建一个reportTable
     * 
     * @param discid 模板号
     * @param senderDomain senderDomain
     * @param receverDomain receverDomain
     * @return 创建好的ReportTable
     */
    private ReportTable makeReportTable(int discId, String senderDomain, String receverDomain) {
        ReportTable reportTable = new ReportTable(discId, receverDomain, receverDomain);
        Date startDate = get3hBeforeDate();
        Date endDate = getOneHourAfterDate(startDate);
        // 对3个小时分别进行统计
        for (int i = 0; i < 3; i++) {
            DetailReport oneHourReport = makeOneHourReport(startDate, endDate, discId, senderDomain, receverDomain);
            reportTable.addOneHourReport(oneHourReport);
            startDate = getOneHourAfterDate(startDate);
            endDate = getOneHourAfterDate(endDate);
        }
        return reportTable;
    }

    private DetailReport makeOneHourReport(Date startDate, Date endDate, int discId,
            String senderDomain, String receverDomain) {
        DetailReport oneHourReport = new DetailReport();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd:HH");
        String time = sdf.format(startDate);
        oneHourReport.setTime(time);
        try {
            int inboxCount = emailMonitorRecordDAO
                    .selectCountByDateAndFolderAndDiscidAndSenderDomainAndReceverDomain(startDate,
                            endDate, 1, discId, senderDomain, receverDomain);
            int dingyueCount = emailMonitorRecordDAO
                    .selectCountByDateAndFolderAndDiscidAndSenderDomainAndReceverDomain(startDate,
                            endDate, 2, discId, senderDomain, receverDomain);
            int garbageCount = emailMonitorRecordDAO
                    .selectCountByDateAndFolderAndDiscidAndSenderDomainAndReceverDomain(startDate,
                            endDate, 3, discId, senderDomain, receverDomain);
            oneHourReport.setInboxCount(inboxCount);
            oneHourReport.setDingyueCount(dingyueCount);
            oneHourReport.setGarbageCount(garbageCount);
            oneHourReport.setDiscId(discId);
            oneHourReport.setSenderDomain(senderDomain);
            oneHourReport.setReceverDomain(receverDomain);
        } catch (Exception e) {
            logger.error("读取数据库失败", e);
            return null;
        }

        return oneHourReport;
    }

    /**
     * 获取当前时间
     * 
     * @return 当前时间
     */
    private Date getCurrentDate() {
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.set(Calendar.MINUTE, 0);
        tempCalendar.set(Calendar.SECOND, 0);
        tempCalendar.set(Calendar.MILLISECOND, 0);
        return tempCalendar.getTime();
    }

    /**
     * 获取3小时之前的时间
     * 
     * @return 3小时之前的之间
     */
    private Date get3hBeforeDate() {
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.set(Calendar.MINUTE, 0);
        tempCalendar.set(Calendar.SECOND, 0);
        tempCalendar.set(Calendar.MILLISECOND, 0);
        tempCalendar.add(Calendar.HOUR, -3);
        return tempCalendar.getTime();
    }

    /**
     * 获取一小时后的时间
     * 
     * @param d 当前时间
     * @return 一小时后的时间
     */
    private Date getOneHourAfterDate(Date d) {
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.setTime(d);
        tempCalendar.add(Calendar.HOUR, 1);
        return tempCalendar.getTime();
    }

    /**
     * 设置邮件标题
     * @param report
     */
    private void makeReportEmailSubject(Report report){
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy年MM月dd日:HH时");
        String subject = "每小时EDM邮件投递收件箱率统计:"
                + dateFormater.format(Calendar.getInstance().getTime());
        report.setEmailSubject(subject);
    }
    
}
