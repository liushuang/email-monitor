package com.renren.mail.monitor.sender;


public interface IEmailSender {
    public void sendDailyEmail();
    
    public void sendHourlyEmail();
}
