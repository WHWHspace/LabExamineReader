package lisImpl;

import constants.CodeMap;
import db.MysqlHelper;
import db.OracleHelper;
import db.SqlServerHelper;
import hemodialysis.LabExamineReader;
import launcher.Main;
import lisInterface.ExamineReportInterface;
import model.ExamineItem;
import model.ExamineReport;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by 31344 on 2016/3/2.
 * 陕西武警医院lis实现
 */
public class ExamineReportImplOfSXWJ implements ExamineReportInterface{

    private Logger logger = Main.logger;

//    private static final String ExamineReportTableName = "LAB_REPORT";
//    private static final String url = "jdbc:oracle:thin:@132.147.160.7:1521:orcl";
//    private static final String user = "lab";
//    private static final String password = "lab117";

    private static final String ExamineReportTableName = "lis_reqresult";
    private static final String url = "jdbc:sqlserver://192.168.0.45:1433;DatabaseName=lis2002";
    private static final String user = "hzweijue";
    private static final String password = "123456";

    private SqlServerHelper helper;
    MysqlHelper mysqlHelper;

    public ExamineReportImplOfSXWJ(){
        helper = new SqlServerHelper(url,user,password);
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
        helper = new SqlServerHelper(url,user,password);
        helper.getConnection();
        ArrayList<ExamineReport> reports = new ArrayList<ExamineReport>();

//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String sql = "SELECT * from \""+ ExamineReportTableName +"\" WHERE \"RESULT_DATE_TIME\" > to_date('"+ dateFormat.format(fromDate) +"', 'yyyy-mm-dd hh24:mi:ss') and \"RESULT_DATE_TIME\" <= to_date('"+ dateFormat.format(toDate) +"', 'yyyy-mm-dd hh24:mi:ss')";
////        String sql = "SELECT * from \""+ ExamineReportTableName +"\" WHERE \"result_date_time\" > to_date('"+ dateFormat.format(fromDate) +"', 'yyyy-mm-dd hh24:mi:ss') and \"result_date_time\" <= to_date('"+ dateFormat.format(toDate) +"', 'yyyy-mm-dd hh24:mi:ss')";
//        ResultSet rs = helper.executeQuery(sql);
//        reports = readExamineReportData(rs);
//
//        helper.closeConnection();
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
        helper = new SqlServerHelper(url,user,password);
        helper.getConnection();
        ArrayList<ExamineReport> reports = new ArrayList<ExamineReport>();
        if((ids == null)||(ids.size() == 0)){
            return reports;
        }
        String idsSql = "";
        idsSql = buildSqlString(ids);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "SELECT * from " + ExamineReportTableName + " where resulttime > '" + dateFormat.format(fromDate) +"' and resulttime < '" + dateFormat.format(toDate) + "' and brdh in " + idsSql;
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
        if(rs == null){
            return reports;
        }
        mysqlHelper = new MysqlHelper(LabExamineReader.url,LabExamineReader.user,LabExamineReader.password);
        mysqlHelper.getConnection();
        try {
            while(rs.next()){
                ExamineReport report = new ExamineReport();
                ExamineItem item = getMappedCode(rs.getString("itemno"));
                if(item == null){
                    continue;
                }
                report.setResult_code(item.code);
                report.setResult_class(item.group);
//                report.setResult_code("4001");
//                report.setResult_class("G001");
                SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    report.setResult_date(dateFormat.format(readFormat.parse(rs.getString("resulttime"))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                report.setResult_ver(0);
                String result = rs.getString("testresult");
                report.setResult_value_t(result);
                report.setResult_value_n(parseDouble(result));
                try {
                    report.setKin_date(dateFormat.format(readFormat.parse(rs.getString("resulttime"))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                report.setKin_user("");
                String patientId = rs.getString("brdh");
                int patientNo = getPatientNo(patientId);
                report.setPat_no(patientNo);

                System.out.println(patientNo);
                reports.add(report);
            }
        } catch (SQLException e) {
            logger.error(new Date() + " 读取查询数据错误\n" + e);
        }
        mysqlHelper.closeConnection();
        return reports;
    }

    private double parseDouble(String result) {
        double d = 0;
        try {
            d = Double.parseDouble(result);
            return d;
        }
        catch (Exception e){
        }
        return d;
    }

    private int getPatientNo(String patientId) {
        int patientNo = 0;
        String sql = "SELECT pif_id FROM pat_info where pif_insid = '" + patientId +"';";
        ResultSet rs = mysqlHelper.executeQuery(sql);
        if(rs == null){
            return patientNo;
        }
        try {
            if(rs.next()){
                patientNo = Integer.parseInt(rs.getString("pif_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patientNo;
    }

    private ExamineItem getMappedCode(String report_item_code) {
        return CodeMap.getMappedCode(report_item_code);
    }
}
