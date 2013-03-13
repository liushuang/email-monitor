package com.renren.mail.monitor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 一天24小时的报表
 * 
 * @author liushuang
 * @creationDate 2013-03-12
 */
public class Report {

    /**
     * 邮件标题
     */
    private String emailSubject;

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    private List<ReportTable> reportTableList = new ArrayList<ReportTable>();

    public List<ReportTable> getReportTableList() {
        return reportTableList;
    }

    /**
     * 添加一个 reportTable
     * 
     * @param reportTable 报表
     */
    public void addReportTable(ReportTable reportTable) {
        reportTableList.add(reportTable);
    }
}
