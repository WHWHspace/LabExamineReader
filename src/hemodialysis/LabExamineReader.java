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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 31344 on 2016/3/2.
 */
public class LabExamineReader implements Runnable{

    Logger logger = Main.logger;
    Date lastReadTime;
    ExamineReportInterface lisImpl;
    MysqlHelper mysqlHelper;
//    public static String url="jdbc:mysql://127.0.0.1:3306/myhaisv4?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true";
//    public static String user = "root";
//    public static String password = "123456";

    public static String url="jdbc:mysql://127.0.0.1:3306/myhaisv4?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true";
    public static String user = "root";
    public static String password = "";

    public LabExamineReader(Date lastReadTime) {
        this.lastReadTime = lastReadTime;
        lisImpl = new ExamineReportImpl();

        mysqlHelper = new MysqlHelper(url,user,password);
    }

    @Override
    public void run() {
        //医院检验报告时间，只有日期，没有具体时间
        //一般上午的检查出结果的日期是今天
        //下午的检查出结果的日期是第二天
        //所以每天凌晨去读取检验数据的时候是读取上一次记录的时间到昨天为止的检验结果
        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String s = new SimpleDateFormat( "yyyy-MM-dd ").format(cal.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date yesterday = new Date();
        try {
            yesterday = dateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        logger.info(currentDate);
        readNewAddedExamineReportByIDs(lastReadTime, yesterday);//读取报告日期为上一次时间到昨天之间的检验
        writeLastReadTime(yesterday);
        lastReadTime = currentDate;                     //跟新上一次读取的时间
    }

    private void readNewAddedExamineReport(Date fromDate,Date toDate) {
        ArrayList<ExamineReport> reports = lisImpl.getUpdatedExamineReport(fromDate,toDate);
        insertExamineReport(reports);
    }

    /**
     *根据病人id读取某一段时间内新增的检验报告
     * @param fromDate
     * @param toDate
     */
    public void readNewAddedExamineReportByIDs(Date fromDate,Date toDate){
        ArrayList<String> ids = getPatientIds();

        ArrayList<ExamineReport> reports = lisImpl.getUpdatedExamineReport(fromDate, toDate, ids);
        insertExamineReport(reports);
    }

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
                ExamineReport report = reports.get(i);
                String sql = "insert into a_result_log (`result_date`,`result_code`,`result_class`," +
                        "`result_ver`,`result_value_t`,`result_value_n`,`kin_date`,`kin_user`,`pat_no`) \n" +
                        "values('"+ report.getResult_date() +"','"+ report.getResult_code() +"','"+ report.getResult_class() +"'," +
                        "'"+ report.getResult_ver() +"','"+ report.getResult_value_t() +"','"+ report.getResult_value_n() +"'," +
                        "'"+ report.getKin_date() +"','"+ report.getKin_user() +"','"+ report.getPat_no() +"')";
                mysqlHelper.executeUpdate(sql);
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
            logger.error(new Date() + " 写入上一次读取时间错误\n" + e);
        }
    }

    /**
     * 读取所有血透病人在his系统中的id
     * @return
     */
    private ArrayList<String> getPatientIds() {
        mysqlHelper.getConnection();

        ArrayList<String> ids = new ArrayList<String>();
        String sql = "SELECT pif_mrn FROM pat_info";
        ResultSet rs = mysqlHelper.executeQuery(sql);
        if(rs == null){
            mysqlHelper.closeConnection();
            return ids;
        }
        try {
            while(rs.next()){
                if(rs.getString("pif_mrn") != null){
                    ids.add(rs.getString("pif_mrn"));
                }
            }
        } catch (SQLException e) {
            logger.error(new Date() + "读取所有病人id失败" + e);
            e.printStackTrace();
        }
        mysqlHelper.closeConnection();
        return  ids;
    }
}
