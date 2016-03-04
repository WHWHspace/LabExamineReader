package model;

import java.util.Date;

/**
 * Created by 31344 on 2016/3/2.
 */
public class ExamineReport {
    private Date result_date;
    private String result_code;
    private String result_class;
    private int result_ver;
    private String result_value_t;
    private String result_value_n;
    private String kin_date;
    private String kin_user;
    private long pat_no;

    public Date getResult_date() {
        return result_date;
    }

    public void setResult_date(Date result_date) {
        this.result_date = result_date;
    }

    public String getResult_code() {
        return result_code;
    }

    public void setResult_code(String result_code) {
        this.result_code = result_code;
    }

    public String getResult_class() {
        return result_class;
    }

    public void setResult_class(String result_class) {
        this.result_class = result_class;
    }

    public int getResult_ver() {
        return result_ver;
    }

    public void setResult_ver(int result_ver) {
        this.result_ver = result_ver;
    }

    public String getResult_value_t() {
        return result_value_t;
    }

    public void setResult_value_t(String result_value_t) {
        this.result_value_t = result_value_t;
    }

    public String getResult_value_n() {
        return result_value_n;
    }

    public void setResult_value_n(String result_value_n) {
        this.result_value_n = result_value_n;
    }

    public String getKin_date() {
        return kin_date;
    }

    public void setKin_date(String kin_date) {
        this.kin_date = kin_date;
    }

    public String getKin_user() {
        return kin_user;
    }

    public void setKin_user(String kin_user) {
        this.kin_user = kin_user;
    }

    public long getPat_no() {
        return pat_no;
    }

    public void setPat_no(long pat_no) {
        this.pat_no = pat_no;
    }
}
