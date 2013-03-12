package com.renren.mail.monitor.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OneDayReport {

    private Log logger = LogFactory.getLog(getClass());

    private List<OneHourReport> oneHourReportList = new ArrayList<OneHourReport>();

    public List<OneHourReport> getOneHourReportList() {
        return oneHourReportList;
    }

    private BufferedReader reader = null;

    public OneDayReport() {
//        String yesterdayStr = getYesterdayString();
//        getYesterdatResultFileReader(yesterdayStr);
//        readResultFile();
//        closeReader();
    }

    @SuppressWarnings("unused")
    private void readResultFile() {
        if (reader != null) {
            String tempString = null;
            String record = null;
            int discid = 0;
            OneHourReport hourReport = null;
            try {
                while ((tempString = reader.readLine()) != null) {
                    if (tempString.indexOf("Record Time") >= 0) {
                        String time = tempString.substring(tempString.indexOf("Record Time:")
                                + "Record Time:".length());
                        hourReport = new OneHourReport(time);
                        while ((record = reader.readLine()) != null) {
                            if (record.indexOf("disc_id") >= 0) {
                                discid = Integer.valueOf(record.substring(record
                                        .indexOf("disc_id: ") + "disc_id: ".length()));
                                String discRecordStr = reader.readLine();
                                DiscResult discResult = new DiscResult(discid);
                                discResult.parse(discRecordStr);
                                hourReport.addDiscResult(discResult);
                            }
                            if (record.indexOf("End of") >= 0) {
                                oneHourReportList.add(hourReport);
                                break;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                logger.error("读写文件错误", e);
            }
        }
    }

    /**
     * 获取昨天的日期String表示形式
     * 
     * @return
     */
    @SuppressWarnings("unused")
    private String getYesterdayString() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date yesterday = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(yesterday);
    }

    @SuppressWarnings("unused")
    private void getYesterdatResultFileReader(String yesterdayStr) {
        String fileName = "/data/web/liushuang/email-monitor/logs/result_" + yesterdayStr + ".txt";
        File file = new File(fileName);
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            logger.error("没有找到相关文件", e);
        }
    }

    @SuppressWarnings("unused")
    private void closeReader() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                logger.error("关闭reader错误", e);
            }
        }
    }

    public static void main(String[] args) {
        new OneDayReport();
        System.out.println("done");
    }
}
