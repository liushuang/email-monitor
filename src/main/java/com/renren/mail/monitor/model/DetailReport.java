package com.renren.mail.monitor.model;

/**
 * 每个table中的一行数据
 *
 * @author liushuang
 * @creation_date 2013-3-12
 */
public class DetailReport {

    // disc号
    private int discId;

    // 收件人域名
    private String senderDomain;

    // 发件人域名
    private String receverDomain;

    // 时间段
    private String time;

    // 进入收件箱的数量
    private int inboxCount;

    // 进入订阅的数量
    private int dingyueCount;

    // 进入垃圾邮件的数量
    private int garbageCount;

    public DetailReport() {

    }

    public DetailReport(String time) {
        this.time = time;
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

    public void setTime(String time) {
        this.time = time;
    }

    public int getTotalCount() {
        return inboxCount + dingyueCount + garbageCount;
    }

    public int getInboxCount() {
        return inboxCount;
    }

    public void setInboxCount(int inboxCount) {
        this.inboxCount = inboxCount;
    }

    public int getDingyueCount() {
        return dingyueCount;
    }

    public void setDingyueCount(int dingyueCount) {
        this.dingyueCount = dingyueCount;
    }

    public int getGarbageCount() {
        return garbageCount;
    }

    public void setGarbageCount(int garbageCount) {
        this.garbageCount = garbageCount;
    }

    /**
     * @return 进入收件箱的比率
     */
    public double getInboxRate() {
        if (getTotalCount() != 0) {
            return (double) inboxCount / getTotalCount();
        } else {
            return 0;
        }
    }

    /**
     * @return 进入订阅邮件的比率
     */
    public double getDingyueRate() {
        if (getTotalCount() != 0) {
            return (double) dingyueCount / getTotalCount();
        } else {
            return 0;
        }
    }

    /**
     * @return 进入垃圾邮件的比率
     */
    public double getGarbageRate() {
        if (getTotalCount() != 0) {
            return (double) garbageCount / getTotalCount();
        } else {
            return 0;
        }
    }

    public String getTime() {
        return time;
    }

}
