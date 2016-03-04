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

    private static final String ExamineReportViewName = "LAB_REPORT";
    private static final String url = "jdbc:oracle:thin:@132.147.160.7:1521:orcl";
    private static final String user = "lab";
    private static final String password = "lab117";

    private OracleHelper helper;

    public ExamineReportImpl(){
        helper = new OracleHelper(url,user,password);
    }

    @Override
    public ArrayList<ExamineReport> getUpdatedExamineReport(Date date) {
        helper.getConnection();
        ArrayList<ExamineReport> reports = new ArrayList<ExamineReport>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "SELECT TEST_NO,TO_NCHAR(REPORT_ITEM_NAME) AS REPORT_ITEM_NAME,REPORT_ITEM_CODE,ITEM_NO,RESULT,UNITS,RESULT_DATE_TIME,PATIENT_ID,EXECUTE_DATE,RESULTS_RPT_DATE_TIME from \""+ ExamineReportViewName +"\" WHERE \"RESULT_DATE_TIME\" > to_date('"+ dateFormat.format(date) +"', 'yyyy-mm-dd hh24:mi:ss')";

        ResultSet rs = helper.executeQuery(sql);
        reports = readExamineReportData(rs);

        helper.closeConnection();
        return reports;
    }

    private ArrayList<ExamineReport> readExamineReportData(ResultSet rs) {
        ArrayList<ExamineReport> reports = new ArrayList<ExamineReport>();
        try {
            while(rs.next()){
                ExamineReport report = new ExamineReport();
                System.out.println(rs.getString("patient_id"));
            }
        } catch (SQLException e) {
            logger.error(new Date() + " 读取查询数据错误\n" + e.getStackTrace());
        }
        return reports;
    }
}
