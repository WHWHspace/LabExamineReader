package constants;

import model.ExamineItem;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by 31344 on 2016/3/6.
 * 这里用来对应lis系统的检验项目和血透系统的检验项目
 * 做法是在examine_code_map文件中把两边的检验项目对应起来
 * 例：RBC/G001/4002
 * RBC是lis系统红细胞的检验代号，G001和4002是血透系统检验的分组和代号，中间用“/”分割开
 */
public class CodeMap {

    private static Logger logger = Logger.getLogger(CodeMap.class);
    private static HashMap<String, ExamineItem> examineCodeMap = new HashMap<String,ExamineItem>();

    /**
     * 读取检验代码映射文件
     */
    public static void readCodeMap(){
        try {
            String path = System.getProperty("user.dir");
            BufferedReader r = new BufferedReader(new FileReader(new File(path + "/config/examine_code_map.txt")));
            String s = "";
            while((s = r.readLine()) != null){
                String[] map = s.split("/");
                if(map.length == 3){
                    examineCodeMap.put(map[0],new ExamineItem(map[1],map[2]));
                }
            }
            logger.info(new Date() + " 读取检验项目映射完成");
            r.close();
        } catch (FileNotFoundException e) {
            logger.error(new Date() + " 未找到配置文件，请在config目录下添加examine_code_map.txt,添加检验项目映射\n" + e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据lis的检验代码获取血透系统的检验代码
     * @param lisCode
     * @return
     */
    public static ExamineItem getMappedCode(String lisCode){
        ExamineItem item = null;
        if(lisCode == null){
            return item;
        }
        item = examineCodeMap.get(lisCode);
        return item;
    }
}
