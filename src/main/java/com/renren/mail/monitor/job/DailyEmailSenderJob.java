package com.renren.mail.monitor.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.renren.mail.monitor.sender.IEmailSender;

/**
 * 每天定时发送邮件的job
 *
 * @author liushuang
 * @creation_date 2013-3-12
 */
public class DailyEmailSenderJob extends QuartzJobBean{
    private Log logger = LogFactory.getLog(getClass());
    
    @Autowired
    private IEmailSender emailSenderImpl;

    @Override
    protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
        logger.error("start sending daily email");
        emailSenderImpl.sendDailyEmail();
        logger.error("end sending daily email");
    }

}
