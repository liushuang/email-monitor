package com.renren.mail.monitor.logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EmailLogger {

    private static Log logger = LogFactory.getLog(EmailLogger.class);
    
    public static void resultLog(String message) {
        SimpleDateFormat lognameFormater = new SimpleDateFormat("yyyy-MM-dd");
        String today = lognameFormater.format(Calendar.getInstance().getTime());

        try {
            FileWriter fw = new FileWriter("/data/web/liushuang/email-monitor/logs/result_" + today + ".txt", true);
            BufferedWriter writer = new BufferedWriter(fw);
            Date time = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            writer.append("Record Time:").append(sdf.format(time) + "\n");
            writer.append(message);
            writer.append("End of:" + sdf.format(time)).append("\n");
            writer.close();
        } catch (IOException e) {
            logger.error("写log文件失败",e);
        }
    }

    public static void actionLog(String message) {
        SimpleDateFormat lognameFormater = new SimpleDateFormat("yyyy-MM-dd");
        String today = lognameFormater.format(Calendar.getInstance().getTime());
        
        try {
            FileWriter fw = new FileWriter("/data/web/liushuang/email-monitor/logs/action_" + today + ".txt", true);
            BufferedWriter writer = new BufferedWriter(fw);
            Date time = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            writer.append("Record Time:").append(sdf.format(time) + "\t");
            writer.append(message + "\n");
            writer.close();
        } catch (IOException e) {
            logger.error("写log文件失败",e);
        }
    }
}
