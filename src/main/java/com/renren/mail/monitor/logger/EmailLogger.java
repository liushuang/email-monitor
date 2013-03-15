package com.renren.mail.monitor.logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EmailLogger {

    private static Log logger = LogFactory.getLog(EmailLogger.class);
    
    public static void actionLog(String message) {
        logger.info(message);
    }
}
