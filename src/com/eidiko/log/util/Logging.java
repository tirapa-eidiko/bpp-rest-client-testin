package com.eidiko.log.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class Logging {
	public static void logConfiguration(String path){
		PatternLayout layout = new PatternLayout();
        String conversionPattern = "%-7p %d [%t] %c %x :: %m%n";
        layout.setConversionPattern(conversionPattern);
 
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy_HH-mm-ss");
        
        FileAppender fileAppender = new FileAppender();
        fileAppender.setFile(path+"/BPM-"+sdf.format(new Date())+".log");
        fileAppender.setLayout(layout);
        fileAppender.activateOptions();
        
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(fileAppender);

        
        
	}
}