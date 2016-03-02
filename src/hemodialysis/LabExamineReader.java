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
        Date currentDate = new Date();          //���ڵ�ʱ��
        writeLastReadTime(currentDate);
        ReadNewAddedExamineReport(lastReadTime); //��ȡ��һ�ε�����֮�������
        lastReadTime = currentDate;                     //������һ�ζ�ȡ��ʱ��
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
            logger.error(new Date() + " д����һ�ζ�ȡʱ�����\n" + e.getStackTrace());
        }
    }
}
