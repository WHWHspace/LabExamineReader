package hemodialysis;

import launcher.Main;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 31344 on 2016/3/2.
 */
public class LabExamineReader implements Runnable{

    Logger logger = Main.logger;
    Date lastReadTime;

    public LabExamineReader(Date lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    @Override
    public void run() {
        logger.info(lastReadTime);
        Date currentDate = new Date();          //现在的时间
        writeLastReadTime(currentDate);
        ReadNewAddedExamineReport(lastReadTime); //读取上一次到现在之间的数据
        lastReadTime = currentDate;                     //跟新上一次读取的时间
    }

    private void ReadNewAddedExamineReport(Date lastReadTime) {

    }


    private void writeLastReadTime(Date currentDate) {
        try {
            String path = System.getProperty("user.dir");
            BufferedWriter w = new BufferedWriter(new FileWriter(new File(path + "/config/lastReadTime.txt"),false));
            w.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentDate));
            w.close();
        } catch (IOException e) {
            logger.error(new Date() + " 写入上一次读取时间错误\n" + e.getStackTrace());
        }
    }
}
