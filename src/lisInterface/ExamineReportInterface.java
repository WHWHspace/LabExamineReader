package lisInterface;

import model.ExamineReport;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by 31344 on 2016/3/2.
 */
public interface ExamineReportInterface {
    //获取所有病人某一时间之后新增的检验报告
    public ArrayList<ExamineReport> getUpdatedExamineReport(Date date);
}
