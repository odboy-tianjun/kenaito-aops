package cn.odboy.core.util;

import cn.odboy.core.framework.mybatisplus.constant.DataTypeEnum;
import cn.odboy.exception.BadRequestException;
import cn.odboy.util.CloseUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.common.collect.Lists;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Slf4j
public final class SqlUtil {
    /**
     * 获取数据源
     *
     * @param jdbcUrl  /
     * @param userName /
     * @param password /
     * @return DataSource
     */
    private static DataSource getDataSource(String jdbcUrl, String userName, String password) {
        HikariDataSource druidDataSource = new HikariDataSource();
        String className;
        try {
            className = DriverManager.getDriver(jdbcUrl.trim()).getClass().getName();
        } catch (SQLException e) {
            throw new BadRequestException("Get class name error: =" + jdbcUrl);
        }
        if (StringUtils.isEmpty(className)) {
            DataTypeEnum dataTypeEnum = DataTypeEnum.urlOf(jdbcUrl);
            if (null == dataTypeEnum) {
                throw new BadRequestException("Not supported data type: jdbcUrl=" + jdbcUrl);
            }
            druidDataSource.setDriverClassName(dataTypeEnum.getDriver());
        } else {
            druidDataSource.setDriverClassName(className);
        }
        druidDataSource.setJdbcUrl(jdbcUrl);
        druidDataSource.setUsername(userName);
        druidDataSource.setPassword(password);
        druidDataSource.setConnectionTimeout(3000);
        return druidDataSource;
    }

    private static Connection getConnection(String jdbcUrl, String userName, String password) {
        DataSource dataSource = getDataSource(jdbcUrl, userName, password);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (Exception ignored) {
        }
        try {
            int timeOut = 5;
            if (null == connection || connection.isClosed() || !connection.isValid(timeOut)) {
                log.info("connection is closed or invalid, retry get connection!");
                connection = dataSource.getConnection();
            }
        } catch (Exception e) {
            log.error("create connection error, jdbcUrl: {}", jdbcUrl);
            throw new BadRequestException("create connection error, jdbcUrl: " + jdbcUrl);
        } finally {
            CloseUtil.close(connection);
        }
        return connection;
    }

    private static void releaseConnection(Connection connection) {
        if (null != connection) {
            try {
                connection.close();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                log.error("connection close error：" + e.getMessage());
            }
        }
    }

    public static boolean testConnection(String jdbcUrl, String userName, String password) {
        Connection connection = null;
        try {
            connection = getConnection(jdbcUrl, userName, password);
            if (null != connection) {
                return true;
            }
        } catch (Exception e) {
            log.info("Get connection failed:" + e.getMessage());
        } finally {
            releaseConnection(connection);
        }
        return false;
    }

    public static String executeFile(String jdbcUrl, String username, String password, File sqlFile) {
        Connection connection = getConnection(jdbcUrl, username, password);
        try {
            batchExecute(connection, readSqlList(sqlFile));
        } catch (Exception e) {
            log.error("sql脚本执行发生异常:{}", e.getMessage());
            return e.getMessage();
        } finally {
            releaseConnection(connection);
        }
        return "success";
    }

    /**
     * 批量执行sql
     *
     * @param connection /
     * @param sqlList    /
     */
    public static void batchExecute(Connection connection, List<String> sqlList) {
        Statement st = null;
        try {
            st = connection.createStatement();
            for (String sql : sqlList) {
                if (sql.endsWith(";")) {
                    sql = sql.substring(0, sql.length() - 1);
                }
                st.addBatch(sql);
            }
            st.executeBatch();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            CloseUtil.close(st);
        }
    }

    /**
     * 将文件中的sql语句以；为单位读取到列表中
     *
     * @param sqlFile /
     * @return /
     * @throws Exception e
     */
    private static List<String> readSqlList(File sqlFile) throws Exception {
        List<String> sqlList = Lists.newArrayList();
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Files.newInputStream(sqlFile.toPath()), StandardCharsets.UTF_8))) {
            String tmp;
            while ((tmp = reader.readLine()) != null) {
                log.info("line:{}", tmp);
                if (tmp.endsWith(";")) {
                    sb.append(tmp);
                    sqlList.add(sb.toString());
                    sb.delete(0, sb.length());
                } else {
                    sb.append(tmp);
                }
            }
            if (!"".endsWith(sb.toString().trim())) {
                sqlList.add(sb.toString());
            }
        }
        return sqlList;
    }
}
