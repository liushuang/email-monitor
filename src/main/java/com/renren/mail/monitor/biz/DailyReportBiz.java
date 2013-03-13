/**
 * 
 */
package com.renren.mail.monitor.biz;

import com.renren.mail.monitor.model.Report;

/**
 * 获取每天的报表数据
 * 
 * @author liushuang
 * @creation_date 2013-3-12
 */
public interface DailyReportBiz {

    /**
     * 获取每天的报表数据
     * 
     * @return
     */
    public Report getDailyReport();
}
