package lisInterface;

import model.ExamineReport;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by 31344 on 2016/3/2.
 */
public interface ExamineReportInterface {
    //��ȡ���в���ĳһʱ��֮�������ļ��鱨��
    public ArrayList<ExamineReport> getUpdatedExamineReport(Date date);
}
