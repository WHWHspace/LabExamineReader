package lisImpl;

import constants.CodeMap;
import db.MysqlHelper;
import db.OracleHelper;
import hemodialysis.LabExamineReader;
import launcher.Main;
import lisInterface.ExamineReportInterface;
import model.ExamineItem;
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
    MysqlHelper mysqlHelper;

    public ExamineReportImpl(){
        helper = new OracleHelper(url,user,password);
        mysqlHelper = new MysqlHelper(LabExamineReader.url,LabExamineReader.user,LabExamineReader.password);
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
//        String sql = "SELECT * from \""+ ExamineReportViewName +"\" WHERE \"RESULT_DATE_TIME\" > to_date('"+ dateFormat.format(fromDate) +"', 'yyyy-mm-dd hh24:mi:ss') and \"RESULT_DATE_TIME\" <= to_date('"+ dateFormat.format(toDate) +"', 'yyyy-mm-dd hh24:mi:ss')";
        String sql = "SELECT * from \""+ ExamineReportViewName +"\" WHERE \"result_date_time\" > to_date('"+ dateFormat.format(fromDate) +"', 'yyyy-mm-dd hh24:mi:ss') and \"result_date_time\" <= to_date('"+ dateFormat.format(toDate) +"', 'yyyy-mm-dd hh24:mi:ss')";
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
        helper.getConnection();
        ArrayList<ExamineReport> reports = new ArrayList<ExamineReport>();
        if((ids == null)||(ids.size() == 0)){
            return reports;
        }
        String idsSql = "";
        idsSql = buildSqlString(ids);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String sql = "SELECT * from \""+ ExamineReportViewName +"\" WHERE \"RESULT_DATE_TIME\" > to_date('"+ dateFormat.format(fromDate) +"', 'yyyy-mm-dd hh24:mi:ss') and \"RESULT_DATE_TIME\" <= to_date('"+ dateFormat.format(toDate) +"', 'yyyy-mm-dd hh24:mi:ss') " +
//                "and \"PATIENT_ID\" in " + idsSql;
        String sql = "SELECT * from \""+ ExamineReportViewName +"\" WHERE \"result_date_time\" > to_date('"+ dateFormat.format(fromDate) +"', 'yyyy-mm-dd hh24:mi:ss') and \"result_date_time\" <= to_date('"+ dateFormat.format(toDate) +"', 'yyyy-mm-dd hh24:mi:ss') " +
                "and \"patient_id\" in " + idsSql;
        ResultSet rs = helper.executeQuery(sql);
        reports = readExamineReportData(rs);

        helper.closeConnection();
        return reports;
    }

    private String buildSqlString(ArrayList<String> ids) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < ids.size(); i++){
            sb.append("'");
            sb.append(ids.get(i));
            sb.append("'");
            if(i != ids.size() - 1){
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
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
                ExamineItem item = getMappedCode(rs.getString("report_item_code"));
                if(item == null){
                    continue;
                }
                report.setResult_code(item.group);
                report.setResult_class(item.code);
                report.setResult_date(rs.getString("result_date_time"));
                report.setResult_ver(1);
                report.setResult_value_t(rs.getString("result"));
                report.setResult_value_n(Integer.parseInt(rs.getString("result")));
                report.setKin_date(rs.getString("result_rpt_date_time"));
                report.setKin_user("");
                String patientId = rs.getString("patient_id");
                int patientNo = getPatientNo(patientId);
                report.setPat_no(patientNo);

                reports.add(report);
            }
        } catch (SQLException e) {
            logger.error(new Date() + " 读取查询数据错误\n" + e);
        }
        return reports;
    }

    private int getPatientNo(String patientId) {
        mysqlHelper.getConnection();
        int patientNo = 0;
        String sql = "SELECT pif_id FROM pat_info where pif_insid = '" + patientId +"';";
        ResultSet rs = mysqlHelper.executeQuery(sql);
        try {
            if(rs.next()){
                patientNo = Integer.parseInt(rs.getString("pif_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mysqlHelper.closeConnection();
        return patientNo;
    }

    private ExamineItem getMappedCode(String report_item_code) {
        return CodeMap.getMappedCode(report_item_code);
    }
}
