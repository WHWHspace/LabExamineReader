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
    	logger.info("更新读取时间");
        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        String s = new SimpleDateFormat( "yyyy-MM-dd ").format(cal.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        try {
            today = dateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        logger.info(currentDate);

        readNewAddedExamineReportByIDs(lastReadTime, today);
        writeLastReadTime(today);
        lastReadTime = currentDate;                     //跟新上一次读取的时间
    }


    /**
     *根据病人id读取某一段时间内新增的检验报告
     * @param fromDate
     * @param toDate
     */
    public void readNewAddedExamineReportByIDs(Date fromDate,Date toDate){
    	logger.info("开始读取数据");
        ArrayList<String> ids = getPatientIds();
        ids = formatIds(ids);

        for(String id : ids){
            ArrayList<ExamineReport> reports = lisImpl.getUpdatedExamineReport(fromDate, toDate, id);
            insertExamineReport(reports);
        }
    }

    private ArrayList<String> formatIds(ArrayList<String> ids) {
        ArrayList<String> formatedIds = new ArrayList<String>();
        for(String id : ids){
            if(id.startsWith("SA")){
                id = id.substring(2);
            }
            else if(id.startsWith("A")){
                id = id.substring(1);
            }
            formatedIds.add(id);
        }
        return formatedIds;
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
            mysqlHelper = new MysqlHelper(url,user,password);
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
        mysqlHelper = new MysqlHelper(url,user,password);
        mysqlHelper.getConnection();

        ArrayList<String> ids = new ArrayList<String>();
        String sql = "SELECT pif_insid FROM pat_info";
        ResultSet rs = mysqlHelper.executeQuery(sql);
        if(rs == null){
            mysqlHelper.closeConnection();
            return ids;
        }
        try {
            while(rs.next()){
                String id = rs.getString("pif_insid");
                if((id != null)&&!("".equals(id))){
                    ids.add(id);
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
