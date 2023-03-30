package com.github.gelald.batch.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author WuYingBin
 * date: 2022/12/26
 */
public class HikariCPUtils {
    public static HikariDataSource dataSource;

    /*
     * 初始化数据源
     */
    static {
        Properties properties = new Properties();
        // 读取配置文件
        try (InputStream inputStream = HikariCPUtils.class.getClassLoader().getResourceAsStream("hikaricp.properties")) {
            properties.load(inputStream);
            // 按照Hikaricp的配置加载Hikaricp配置类
            HikariConfig config = new HikariConfig(properties);
            // 根据配置生成连接池
            dataSource = new HikariDataSource(config);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * 通过数据源获取连接
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * 关闭连接
     *
     * @param connection 数据库连接
     */
    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 关闭语句集和连接
     * 由于用了连接池，这里的关闭连接其实不是销毁，而是交还给连接池
     *
     * @param statement  语句集
     * @param connection 数据库连接
     */
    public static void close(Statement statement, Connection connection) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        close(connection);
    }

    /**
     * 关闭结果集、语句集和连接
     *
     * @param resultSet  结果集
     * @param statement  语句集
     * @param connection 数据库连接
     */
    public static void close(ResultSet resultSet, Statement statement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        close(statement, connection);
    }
}
