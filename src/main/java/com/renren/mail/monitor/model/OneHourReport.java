package com.renren.mail.monitor.model;

import java.util.ArrayList;
import java.util.List;

public class OneHourReport {

    private List<DiscResult> discResultList = new ArrayList<DiscResult>();

    private String time;

    public List<DiscResult> getDiscResultList() {
        return discResultList;
    }

    public String getTime() {
        return time;
    }

    public OneHourReport() {

    }

    public OneHourReport(String time) {
        this.time = time;
    }

    public void addDiscResult(DiscResult discResult) {
        if (discResult != null) {
            this.discResultList.add(discResult);
        }
    }
}
