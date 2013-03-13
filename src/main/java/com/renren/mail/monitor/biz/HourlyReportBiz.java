package com.renren.mail.monitor.biz;

import com.renren.mail.monitor.model.Report;


public interface HourlyReportBiz {
    /**
     * 获取当前的24小时的报告
     * @return
     */
    public Report getHourlyReport();
}
