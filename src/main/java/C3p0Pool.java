import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

public class C3p0Pool {
    private static Logger logger = Logger.getLogger(C3p0Pool.class);
    public static Connection getRawMysqlConnection() {
        Connection connection = null;
        try {
            Class.forName(PropertiesUtils.get("driverName"));
            connection = DriverManager.getConnection(PropertiesUtils.get("dbURL"),
                    PropertiesUtils.get("user"),
                    PropertiesUtils.get("password"));
            logger.info("连接数据库成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("数据库连接失败");
        }
        return connection;
    }
    public static Connection getSQLServerConnectionFromPool() {
        ComboPooledDataSource cpds = new ComboPooledDataSource("sqlserver");
        Connection conn = null;
        try {
            conn = cpds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  conn;
    }

    public static Connection getMysqlConnectionFromPool() {
        ComboPooledDataSource cpds = new ComboPooledDataSource("mysql");
        Connection conn = null;
        try {
            conn = cpds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  conn;
    }
    public static void main(String[] args) throws SQLException {
        HashMap<Connection, Integer> map = new HashMap<Connection, Integer>();
        for(int i=0;i<67;i++) {
            Connection connection = getMysqlConnectionFromPool();
            connection.close();// 标记connection是空闲的，真正释放由pool决定
            map.put(connection, 1);
        }
        logger.info("3333333333333333333333333333333333333----"+map.size());
    }




}
