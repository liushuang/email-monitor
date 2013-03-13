package com.renren.mail.monitor.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.renren.mail.monitor.sender.IEmailSender;

/**
 * 每小时发送邮件的job
 * 
 * @author liushuang
 * @creation_date 2013-3-12
 */
public class HourlyEmailSenderJob extends QuartzJobBean {

    private Log logger = LogFactory.getLog(getClass());

    private IEmailSender emailSenderImpl;

    public void setEmailSenderImpl(IEmailSender emailSenderImpl) {
        this.emailSenderImpl = emailSenderImpl;
    }

    @Override
    protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
        logger.error("start sending hourly email");
        emailSenderImpl.sendHourlyEmail();
        logger.error("end sending hourly email");
    }

}
