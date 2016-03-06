package db;

import launcher.Main;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Date;

/**
 * Created by 31344 on 2016/2/24.
 * 连接血透数据库，mysql
 */
public class MysqlHelper {
    private Logger logger = Main.logger;
    private Connection connection;
    String url;
    String user;
    String password;

    public MysqlHelper(String url,String user,String password){
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * 建立数据库连接
     */
    public void getConnection(){
        try {
            //动态加载mysql驱动
            Class.forName("com.mysql.jdbc.Driver");

            //建立连接
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            logger.error(new Date() + " 加载mysql驱动失败\n" + e);
        } catch (SQLException e) {
            logger.error(new Date() + " 建立mysql连接失败\n" + e);
        }
    }

    /**
     * 执行更新，插入语句
     * @param sql
     */
    public void executeUpdate(String sql){
        Statement s = null;
        try {
            s = connection.createStatement();
            int result = s.executeUpdate(sql);
        } catch (SQLException e) {
            logger.error(new Date() + " 执行mysql更新错误\n" + e);
        }
    }

    /**
     * 执行查询语句
     * @param sql
     */
    public ResultSet executeQuery(String sql){
        Statement s = null;
        ResultSet rs = null;
        try {
            s = connection.createStatement();
            rs = s.executeQuery(sql);
            return rs;
        } catch (SQLException e) {
            logger.error(new Date() + " 执行mysql查询错误\n" + e);
        }
        return null;
    }

    /**
     * 关闭连接
     */
    public void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e) {
            logger.error(new Date() + " 关闭mysql连接失败\n" + e);
        }
    }

}
