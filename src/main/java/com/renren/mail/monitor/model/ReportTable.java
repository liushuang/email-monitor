package com.renren.mail.monitor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 报告邮件的一个表格
 *
 * @author liushuang
 * @creation_date 2013-3-12
 */
public class ReportTable {
    // disc号
    private int discId;
    // 收件人域名
    private String senderDomain;
    // 发件人域名
    private String receverDomain;
    // 按小时统计的报告
    private List<DetailReport> oneHourReportList = new ArrayList<DetailReport>();
    
    public ReportTable(int discId , String senderDomain ,String receverDomain){
        this.discId = discId;
        this.senderDomain = senderDomain;
        this.receverDomain = receverDomain;
    }
    public int getDiscId() {
        return discId;
    }

    public void setDiscId(int discId) {
        this.discId = discId;
    }

    public String getSenderDomain() {
        return senderDomain;
    }

    public void setSenderDomain(String senderDomain) {
        this.senderDomain = senderDomain;
    }

    public String getReceverDomain() {
        return receverDomain;
    }

    public void setReceverDomain(String receverDomain) {
        this.receverDomain = receverDomain;
    }
    
    /**
     * 添加一个小时的记录
     * @param oneHourReport
     */
    public void addOneHourReport(DetailReport oneHourReport){
        oneHourReportList.add(oneHourReport);
    }
    
    public List<DetailReport> getOneHourReportList() {
        return oneHourReportList;
    }
}
