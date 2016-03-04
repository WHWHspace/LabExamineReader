package hemodialysis;

import db.MysqlHelper;
import launcher.Main;
import lisImpl.ExamineReportImpl;
import lisInterface.ExamineReportInterface;
import model.ExamineReport;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by 31344 on 2016/3/2.
 */
public class LabExamineReader implements Runnable{

    Logger logger = Main.logger;
    Date lastReadTime;
    ExamineReportInterface lisImpl;
    MysqlHelper mysqlHelper;
    static String url="jdbc:mysql://127.0.0.1:3306/hemodialysis?useUnicode=true&characterEncoding=UTF-8";
    static String user = "root";
    static String password = "123456";
//    static String url="jdbc:mysql://127.0.0.1:3306/myhaisv4?useUnicode=true&characterEncoding=UTF-8";
//    static String user = "root";
//    static String password = "";

    public LabExamineReader(Date lastReadTime) {
        this.lastReadTime = lastReadTime;
        lisImpl = new ExamineReportImpl();

        mysqlHelper = new MysqlHelper(url,user,password);
    }

    @Override
    public void run() {
        Date currentDate = new Date();          //现在的时间
        logger.info(currentDate);
        writeLastReadTime(currentDate);
        ReadNewAddedExamineReport(lastReadTime); //读取上一次到现在之间的数据
        lastReadTime = currentDate;                     //跟新上一次读取的时间
    }

    private void ReadNewAddedExamineReport(Date lastReadTime) {
        ArrayList<ExamineReport> orders = lisImpl.getUpdatedExamineReport(lastReadTime);
        if(orders.size() > 0){
            logger.info(new Date() + " 添加" + orders.size() +"条检验报告数据");
            mysqlHelper.getConnection();

            insertLongTermOrder(orders);

            mysqlHelper.closeConnection();
        }
    }

    private void insertLongTermOrder(ArrayList<ExamineReport> orders) {
        for (int i = 0; i < orders.size(); i++){
            String sql = "";
        }
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
