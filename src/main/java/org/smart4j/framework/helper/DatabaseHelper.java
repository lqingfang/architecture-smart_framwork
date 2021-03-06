package org.smart4j.framework.helper;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.framework.util.CollectionUtil;
import org.smart4j.framework.util.PropsUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * Created by sally on 2017/2/11.
 */
public final class DatabaseHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);
    private static final ThreadLocal<Connection> CONNECTION_HOLDER=new ThreadLocal<Connection>();
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();
    //查询实体，是一个list
    public static <T> List<T> queryEntityList(Class<T> entityClass, String sql, Object ... params) {
        List<T> entityList;
        try {
            Connection conn = getConnection();
            entityList = QUERY_RUNNER.query(conn, sql, new BeanListHandler<T>(entityClass), params);
        } catch (SQLException e) {
            LOGGER.error("query entity list failure", e);
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return entityList;
    }
    //查询单个实体
    public static <T> T queryEntity(Class<T> entityClass, String sql, Object ... params) {
        T entity;
        try {
            Connection conn = getConnection();
            entity = QUERY_RUNNER.query(conn, sql, new BeanHandler<T>(entityClass), params);
        } catch (SQLException e) {
            LOGGER.error("query entity list failure", e);
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return entity;
    }
    //执行查询语句（多张表查询，返回map）
    public static List<Map<String, Object>> executeQuery(String sql, Object ... params) {
        List<Map<String, Object>> result;
        try {
            Connection conn = getConnection();
            result = QUERY_RUNNER.query(conn, sql, new MapListHandler(), params);
        } catch (SQLException e) {
            LOGGER.error("query entity list failure", e);
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return result;
    }
    //执行更新语句
    public static int executeupdate(String sql, Object ... params) {
        int rows = 0;
        try {
            Connection conn = getConnection();
            rows = QUERY_RUNNER.update(conn, sql, params);
        } catch (SQLException e) {
            LOGGER.error("query entity list failure", e);
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return rows;
    }
    //根据id删除实体
    public static <T> boolean deleteEntity(Class<T> entityClass, long id) {
        String sql = "delete from " +getTableName(entityClass) + "where id = ?";
        return executeupdate(sql,id) == 1;
    }
    //更新实体
    public static <T> boolean updateEntity(Class<T> entityClass,long id,Map<String, Object> fieldMap) {
        if(CollectionUtil.isEmpty(fieldMap.keySet())) {
            LOGGER.error("can not update entity:fieldMap is empty");
            return  false;
        }
        String sql = " update "+getTableName(entityClass) + " set ";
        StringBuilder columns = new StringBuilder();
        for (String fieldName : fieldMap.keySet()) {
            columns.append(fieldName).append("=?, ");
        }
        sql += columns.substring(0, columns.lastIndexOf(", "))+ " where id=?";
        List<Object> paramList = new ArrayList<Object>();
        paramList.addAll(fieldMap.values());
        paramList.add(id);
        Object[] params = paramList.toArray();
        return executeupdate(sql,params) == 1;
    }
    //插入实体
    public static <T> boolean insertEntity(Class<T> entityClass,Map<String, Object> fieldMap) {
        if(CollectionUtil.isEmpty(fieldMap.keySet())) {
            LOGGER.error("can not update entity:fieldMap is empty");
            return  false;
        }
        String sql = " insert into "+getTableName(entityClass);
        StringBuilder columns = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");
        for (String fieldName : fieldMap.keySet()) {
            columns.append(fieldName).append(", ");
            values.append("?, ");
        }
        columns.replace(columns.lastIndexOf(", "), columns.length(), ")");
        values.replace(values.lastIndexOf(", "),values.length(),")");
        sql += columns+ " values" +values;
        Object[] params = fieldMap.values().toArray();
        return executeupdate(sql,params) == 1;

    }
    //拿到表名
    private static String getTableName(Class<?> entityClass) {
        return entityClass.getSimpleName();
    }
    //以下是jdbc的连接
    private static final BasicDataSource DATA_SOURCE;
    static {
        Properties conf = PropsUtil.loadProps("jdbc.properties");
        String driver=conf.getProperty("jdbc.driver");
        String url=conf.getProperty("jdbc.url");
        String username=conf.getProperty("jdbc.username");
        String password=conf.getProperty("jdbc.password");
        DATA_SOURCE = new BasicDataSource();
        DATA_SOURCE.setDriverClassName(driver);
        DATA_SOURCE.setUrl(url);
        DATA_SOURCE.setUsername(username);
        DATA_SOURCE.setPassword(password);
    }
    /**
     * 获取数据库连接
     */
   public static Connection getConnection() {
       Connection conn = CONNECTION_HOLDER.get();
       if(conn==null){
           try {
              conn = DATA_SOURCE.getConnection();
           } catch (SQLException e) {
               LOGGER.error("get connection failure",e);
               throw new RuntimeException(e);
           } finally {
               CONNECTION_HOLDER.set(conn);
           }
       }
       return conn;
   }
    /**
     * 关闭数据库连接
     */
    public static void closeConnection() {
        Connection conn = CONNECTION_HOLDER.get();
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("close connection failure",e);
                throw new RuntimeException(e);
            } finally {
                CONNECTION_HOLDER.remove();
            }
        }
    }
    public static void executeSqlFile(String filePath) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String sql;
            while ((sql=reader.readLine()) != null) {
                executeupdate(sql);
            }
        }catch (Exception e){
                LOGGER.error("execute sql file failure", e);
                throw new RuntimeException(e);
            }
        }
    /*
     开启事务
     */
    public static void beginTransaction() {
        Connection conn = getConnection();
        if (conn != null) {
            try {
                conn.setAutoCommit(false);
            } catch (SQLException e) {
                LOGGER.error("begin transaction failure", e);
                throw new RuntimeException(e);
            } finally {
                CONNECTION_HOLDER.set(conn);
            }
        }
    }
    /*
    提交事务
     */
    public static void commitTransaction() {
        Connection conn = getConnection();
        if (conn!=null) {
            try {
                conn.commit();
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("commit transaction failure", e);
                throw new RuntimeException(e);
            } finally {
                CONNECTION_HOLDER.remove();
            }
        }
    }
    /*
    回滚事务
     */
    public static void rollbackTransaction() {
        Connection conn = getConnection();
        if (conn!=null) {
            try {
                conn.rollback();
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("rollback transaction failure", e);
                throw new RuntimeException(e);
            } finally {
                CONNECTION_HOLDER.remove();
            }
        }
    }
}
