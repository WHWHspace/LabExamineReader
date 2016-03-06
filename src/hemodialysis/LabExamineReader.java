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
//        readNewAddedExamineReport(lastReadTime); //读取上一次到现在之间的数据

        lastReadTime = currentDate;                     //跟新上一次读取的时间
    }

    private void readNewAddedExamineReport(Date fromDate,Date toDate) {
        ArrayList<ExamineReport> reports = lisImpl.getUpdatedExamineReport(fromDate,toDate);
        if(reports.size() > 0){
            logger.info(new Date() + " 添加" + reports.size() +"条检验报告数据");
            mysqlHelper.getConnection();

            insertExamineReport(reports);

            mysqlHelper.closeConnection();
        }
    }

//    /**
//     *根据病人id读取某一段时间内新增的检验报告
//     * @param fromDate
//     * @param toDate
//     */
//    public void readNewAddedExamineReportByIDs(Date fromDate,Date toDate){
//        ArrayList<String> ids = getPatientIds();
//
//        ArrayList<LongTermOrder> orders = hisImpl.getUpdatedLongTermOrder(fromDate, toDate, ids);
//        insertLongTermOrder(orders);
//    }

    /**
     * 插入检验报告数据
     * @param reports
     */
    private void insertExamineReport(ArrayList<ExamineReport> reports) {
        if(reports == null){
            return;
        }
        if(reports.size() > 0){
            logger.info(new Date() + " 添加" + reports.size() + "条检验报告数据");
            mysqlHelper.getConnection();

            for (int i = 0; i < reports.size(); i++){
//                ...........
            }

            mysqlHelper.closeConnection();
        }
    }


    /**
     * 写入上一次读取检验报告的最后时间
     * @param currentDate
     */
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
