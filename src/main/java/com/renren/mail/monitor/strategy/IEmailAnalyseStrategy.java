package com.renren.mail.monitor.strategy;

import java.util.List;

import javax.mail.Message;

public interface IEmailAnalyseStrategy {

    void analyse(List<Message> inboxMessages, List<Message> dingyueMessages,
            List<Message> garbageMessages);
}
