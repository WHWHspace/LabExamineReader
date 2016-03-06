package constants;

import launcher.Main;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 31344 on 2016/3/6.
 */
public class CodeMap {

    static Logger logger = Main.logger;
    static HashMap<String,String> examineCodeMap = new HashMap<String,String>();

    /**
     * 读取检验代码映射文件
     */
    public static void readCodeMap(){
        try {
            String path = System.getProperty("user.dir");
            BufferedReader r = new BufferedReader(new FileReader(new File(path + "/config/examine_code_map.txt")));
            String s = "";
            while((s = r.readLine()) != null){
                String[] map = s.split(" ");
                if(map.length == 2){
                    examineCodeMap.put(map[0],map[1]);
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
    public static String getMappedCode(String lisCode){
        String code = null;
        if(lisCode == null){
            return code;
        }
        code = examineCodeMap.get(lisCode);
        return code;
    }
}
