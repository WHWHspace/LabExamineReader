package launcher;

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
    public static int INTERVAL = 6;
    static Date lastReadTime;

    public static void main(String args[]) {

        Runnable runnable = new LabExamineReader(lastReadTime);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // �ڶ�������Ϊ�״�ִ�е���ʱʱ�䣬����������Ϊ��ʱִ�еļ��ʱ��
        service.scheduleAtFixedRate(runnable, 0, INTERVAL, TimeUnit.HOURS);
    }

    /**
     * ��ȡʱ����
     */
    private static void readInterval() {
        try {
            String path = System.getProperty("user.dir");
            BufferedReader r = new BufferedReader(new FileReader(new File(path + "/config/interval.txt")));
            String s = r.readLine();
            if (s != null){
                INTERVAL = Integer.parseInt(s);
            }
            logger.info(new Date() + " ��ȡʱ�������");
            r.close();
        } catch (FileNotFoundException e) {
            logger.error(new Date() + " δ�ҵ������ļ�������configĿ¼�����interval.txt,���ö�ȡʱ�������Ժ���Ϊ��λ��\n" + e.getStackTrace());

        } catch (IOException e) {
            logger.error(new Date() + " ��ȡʱ���������ļ�ʧ�ܣ�\n" + e.getStackTrace());
        }
    }



    /**
     * ��ȡ��һ�ζ�ȡ���ݵ�ʱ��
     * ���������һ��ʱ��Ϊ������ǰ�����Ƕ�ȡ���е����ݣ������һ�����е�ʱ������
     */
    private static void readLastReadTime() {
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
            logger.info(new Date() + " ��ȡ��һ��ʱ�����");
            r.close();
        } catch (FileNotFoundException e) {
            logger.error(new Date() + " δ�ҵ������ļ�������configĿ¼�����lastReadTime.txt,������һ�ζ�ȡ��ʱ�䡣ʱ���ʽ 2000-02-23 12:12:12\n"  + e.getStackTrace());
        } catch (IOException e) {
            logger.error(new Date() + " ��ȡ��һ��ʱ�������ļ�ʧ�ܣ�\n" + e.getStackTrace());
        } catch (ParseException e) {
            logger.error(new Date() + " ���ڸ�ʽ���󣬽�������\n" + e.getStackTrace());
        }

    }
}
