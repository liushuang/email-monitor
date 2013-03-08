package com.renren.mail.monitor.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DiscResult {

    private Log logger = LogFactory.getLog(getClass());

    private int discid;

    private int inboxCount;

    private int dingyueCount;

    private int garbageCount;

    public DiscResult(int discid) {
        this.discid = discid;
    }

    public int getDiscid() {
        return discid;
    }

    public void setDiscid(int discid) {
        this.discid = discid;
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

    public double getInboxRate() {
        if (getTotalCount() != 0) {
            return (double) inboxCount / getTotalCount();
        } else {
            return 0;
        }
    }

    public double getDingyueRate() {
        if (getTotalCount() != 0) {
            return (double) dingyueCount / getTotalCount();
        } else {
            return 0;
        }
    }

    public double getGarbageRate() {
        if (getTotalCount() != 0) {
            return (double) garbageCount / getTotalCount();
        } else {
            return 0;
        }
    }

    public void parse(String discRecordStr) {
        try {
            String[] resultGroup = discRecordStr.split("\t");
            this.inboxCount = Integer.parseInt(resultGroup[3]);
            this.dingyueCount = Integer.parseInt(resultGroup[5]);
            this.garbageCount = Integer.parseInt(resultGroup[7]);
        } catch (Exception e) {
            logger.error("转换数字失败", e);
        }
    }
}
