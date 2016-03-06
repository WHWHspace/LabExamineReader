package lisImpl;

import db.OracleHelper;
import launcher.Main;
import lisInterface.ExamineReportInterface;
import model.ExamineReport;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by 31344 on 2016/3/2.
 */
public class ExamineReportImpl implements ExamineReportInterface{

    private Logger logger = Main.logger;

//    private static final String ExamineReportViewName = "LAB_REPORT";
//    private static final String url = "jdbc:oracle:thin:@132.147.160.7:1521:orcl";
//    private static final String user = "lab";
//    private static final String password = "lab117";

    private static final String ExamineReportViewName = "lab_report";
    private static final String url = "jdbc:oracle:thin:@127.0.0.1:1521:orcl";
    private static final String user = "test";
    private static final String password = "123456";

    private OracleHelper helper;

    public ExamineReportImpl(){
        helper = new OracleHelper(url,user,password);
    }

    /**
     * 获取所有病人某一时间段的检验报告
     * @param fromDate
     * @param toDate
     * @return
     */
    @Override
    public ArrayList<ExamineReport> getUpdatedExamineReport(Date fromDate,Date toDate) {
        helper.getConnection();
        ArrayList<ExamineReport> reports = new ArrayList<ExamineReport>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String sql = "SELECT * from \""+ ExamineReportViewName +"\" WHERE \"RESULT_DATE_TIME\" > to_date('"+ dateFormat.format(fromDate) +"', 'yyyy-mm-dd hh24:mi:ss') and \"RESULT_DATE_TIME\" <= to_date('"+ dateFormat.format(fromDate) +"', 'yyyy-mm-dd hh24:mi:ss')";
        String sql = "SELECT * from \""+ ExamineReportViewName +"\" WHERE \"result_date_time\" > to_date('"+ dateFormat.format(fromDate) +"', 'yyyy-mm-dd hh24:mi:ss') and \"result_date_time\" <= to_date('"+ dateFormat.format(fromDate) +"', 'yyyy-mm-dd hh24:mi:ss')";
        ResultSet rs = helper.executeQuery(sql);
        reports = readExamineReportData(rs);

        helper.closeConnection();
        return reports;
    }

    /**
     * 根据病人id获取某一时间段的检验报告
     * @param fromDate
     * @param toDate
     * @param ids
     * @return
     */
    @Override
    public ArrayList<ExamineReport> getUpdatedExamineReport(Date fromDate, Date toDate, ArrayList<String> ids) {
        return null;
    }

    /**
     * 读取检验报告的查询内容
     * @param rs
     * @return
     */
    private ArrayList<ExamineReport> readExamineReportData(ResultSet rs) {
        ArrayList<ExamineReport> reports = new ArrayList<ExamineReport>();
        try {
            while(rs.next()){
                ExamineReport report = new ExamineReport();
                String code = getMappedCode(rs.getString("report_item_code"));

            }
        } catch (SQLException e) {
            logger.error(new Date() + " 读取查询数据错误\n" + e);
        }
        return reports;
    }

    private String getMappedCode(String report_item_code) {
        return null;
    }
}
