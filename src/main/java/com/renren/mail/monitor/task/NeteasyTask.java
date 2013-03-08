package com.renren.mail.monitor.task;

import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;

import com.renren.mail.monitor.email.IEmail;

public class NeteasyTask extends TimerTask {

    @Autowired
    private IEmail neteasyEmail;

    @Override
    public void run() {
        System.out.println("starting NeteasyTask");
        //neteasyEmail.startAnalyse();
        System.out.println(neteasyEmail.toString());
        System.out.println("finished NeteasyTask");
    }

}
