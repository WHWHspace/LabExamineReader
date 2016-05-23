package launcher;

import constants.CodeMap;
import hemodialysis.LabExamineReader;
import org.apache.log4j.Logger;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by 31344 on 2016/3/1.
 */
public class Main {
    public static Logger logger = Logger.getLogger(Main.class);
    private int INTERVAL = 1440;
    private Date lastReadTime;

    public static void main(String args[]) {
        new Main().run();
    }

    private void run() {
        readInterval();
        readLastReadTime();
        CodeMap.readCodeMap();

        Runnable runnable = new LabExamineReader(lastReadTime);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(runnable, 1, INTERVAL, TimeUnit.MINUTES);
    }

    /**
     * 读取时间间隔
     */
    private void readInterval() {
        try {
            String path = System.getProperty("user.dir");
            BufferedReader r = new BufferedReader(new FileReader(new File(path + "/config/interval.txt")));
            String s = r.readLine();
            if (s != null){
                INTERVAL = Integer.parseInt(s);
            }
            logger.info(new Date() + " 读取时间间隔完成");
            r.close();
        } catch (FileNotFoundException e) {
            logger.error(new Date() + " 未找到配置文件，请在config目录下添加interval.txt,设置读取时间间隔（以毫秒为单位）\n" + e);

        } catch (IOException e) {
            logger.error(new Date() + " 读取时间间隔配置文件失败！\n" + e);
        }
    }



    /**
     * 读取上一次读取数据的时间
     * 如果设置上一次时间为很早以前，就是读取所有的数据，建议第一次运行的时候设置
     */
    private void readLastReadTime() {
        try {
            String path = System.getProperty("user.dir");
            BufferedReader r = new BufferedReader(new FileReader(new File(path + "/config/lastReadTime.txt")));
            String s = r.readLine();
            if (s != null){
                lastReadTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);
            }
            else{
                lastReadTime = new Date();
            }
            logger.info(new Date() + " 读取上一次时间完成");
            r.close();
        } catch (FileNotFoundException e) {
            logger.error(new Date() + " 未找到配置文件，请在config目录下添加lastReadTime.txt,设置上一次读取的时间。时间格式 2000-02-23 12:12:12\n"  + e);
        } catch (IOException e) {
            logger.error(new Date() + " 读取上一次时间配置文件失败！\n" + e);
        } catch (ParseException e) {
            logger.error(new Date() + " 日期格式错误，解析错误\n" + e);
        }

    }
}
