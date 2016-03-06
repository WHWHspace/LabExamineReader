package db;

import launcher.Main;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Date;

/**
 * Created by 31344 on 2016/2/24.
 */
public class OracleHelper {
    private Logger logger = Main.logger;
    Connection connection;
    String url;
    String user;
    String password;

    public OracleHelper(String url,String user,String password){
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public void getConnection(){
        try {
            //动态加载mysql驱动
            Class.forName("oracle.jdbc.driver.OracleDriver");

            //建立连接
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            logger.error(new Date() + " 加载oracle驱动失败\n" + e);
        } catch (SQLException e) {
            logger.error(new Date() + " 建立oracle连接失败\n" + e);
        }
    }

    /**
     * 关闭连接
     */
    public void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e) {
            logger.error(new Date() + " 关闭oracle数据库连接失败\n" + e);
        }
    }

    public ResultSet executeQuery(String sql) {
        Statement s = null;
        ResultSet rs = null;
        try {
            s = connection.createStatement();
            rs = s.executeQuery(sql);
            return rs;
        } catch (SQLException e) {
            logger.error(new Date() + " 查询oracle数据库失败\n" + e);
        }
        return  null;
    }
}
