/**
 * 
 */
package com.renren.mail.monitor.biz.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renren.mail.monitor.biz.DailyReportBiz;
import com.renren.mail.monitor.dao.EmailMonitorRecordDAO;
import com.renren.mail.monitor.model.DetailReport;
import com.renren.mail.monitor.model.Report;
import com.renren.mail.monitor.model.ReportTable;

/**
 * 获取每天的报表数据
 * 
 * @author liushuang
 * @creation_date 2013-3-12
 */
@Service
public class DailyReportBizImpl implements DailyReportBiz {

    private Log logger = LogFactory.getLog(getClass());
    
    @Autowired
    private EmailMonitorRecordDAO emailMonitorRecordDAO;
    
    @Override
    public Report getDailyReport() {
        Report report = new Report();
        
        Date endDate = getCurrentDate();
        Date startDate = getTwoWeeksBeforeDate(endDate);
        // 获取最近24小时的discid
        List<Integer> discIdList = emailMonitorRecordDAO
                .selectSubjectListByDate(startDate, endDate);
        // 获取最近24小时的senderDomain
        List<String> senderDomainList = emailMonitorRecordDAO.selectSenderDomainListByDate(
                startDate, endDate);
        // 获取最近24小时的receverDomain
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
        // 开始时间是两周之前
        Date startDate = getTwoWeeksBeforeDate(getCurrentDate());
        Date endDate = getOneDayAfterDate(startDate);
        // 对两周的14天进行分别统计
        for (int i = 0; i < 14; i++) {
            DetailReport detailReport = makeDetailReport(startDate, endDate, discId, senderDomain, receverDomain);
            reportTable.addOneHourReport(detailReport);
            startDate = getOneDayAfterDate(startDate);
            endDate = getOneDayAfterDate(endDate);
        }
        return reportTable;
    }
    
    /**
     * 获取一个table中的一行的数据
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param discId 
     * @param senderDomain
     * @param receverDomain
     * @return
     */
    private DetailReport makeDetailReport(Date startDate, Date endDate, int discId,
            String senderDomain, String receverDomain) {
        DetailReport oneHourReport = new DetailReport();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
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
     * @param startDate
     * @return
     */
    private Date getOneDayAfterDate(Date d) {
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.setTime(d);
        tempCalendar.add(Calendar.DATE, 1);
        return tempCalendar.getTime();
    }

    /**
     * 获取两周之前的时间 
     * @param endDate
     * @return
     */
    private Date getTwoWeeksBeforeDate(Date date) {
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.setTime(date);
        tempCalendar.add(Calendar.DATE, -14);
        return tempCalendar.getTime();
    }

    /**
     * 获取当前时间
     * 
     * @return 当前时间
     */
    private Date getCurrentDate() {
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.set(Calendar.HOUR, 0);
        tempCalendar.set(Calendar.MINUTE, 0);
        tempCalendar.set(Calendar.SECOND, 0);
        tempCalendar.set(Calendar.MILLISECOND, 0);
        return tempCalendar.getTime();
    }
    
    private void makeReportEmailSubject(Report report){
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy年MM月dd日");
        String subject = "每日EDM邮件投递收件箱率统计:"
                + dateFormater.format(Calendar.getInstance().getTime());
        report.setEmailSubject(subject);
    }
}
