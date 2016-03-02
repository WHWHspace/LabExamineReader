package hemodialysis;

import java.util.Date;

/**
 * Created by 31344 on 2016/3/2.
 */
public class LabExamineReader implements Runnable{

    Date lastReadTime;

    public LabExamineReader(Date lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    @Override
    public void run() {
        System.out.println(1);
    }
}
