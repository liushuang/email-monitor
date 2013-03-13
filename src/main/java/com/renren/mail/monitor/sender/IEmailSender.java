package com.renren.mail.monitor.sender;


public interface IEmailSender {
    /**
     * 每天发送邮件
     */
    public void sendDailyEmail();
    
    /**
     * 没小时发送的邮件
     */
    public void sendHourlyEmail();
}
