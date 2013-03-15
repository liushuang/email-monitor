package com.renren.mail.monitor.strategy;

import java.util.List;
import java.util.Map;

import javax.mail.Message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.renren.mail.monitor.disc.ChangeDisc;
import com.renren.mail.monitor.model.DiscResult;

public class RateAnalyseStrategy implements IEmailAnalyseStrategy {

    @SuppressWarnings("unused")
    private Log logger = LogFactory.getLog(getClass());

    @SuppressWarnings("unused")
    @Autowired
    private ChangeDisc changeDisc;

    @Override
    public void analyse(List<Message> inboxMessages, List<Message> dingyueMessages,
            List<Message> garbageMessages) {

    }

    @SuppressWarnings("unused")
    private void autoChangeDiscId(Map<Integer, DiscResult> resultMap) {
        //        System.out.println("*********************before change disc");
        //        changeDisc.changeDiscId(31210221, 31210222);
        //        System.out.println("*********************after chage disc");

    }

}
