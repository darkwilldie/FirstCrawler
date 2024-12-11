package tools;

import java.sql.*;

public class JDBCTools {
    // 数据库连接信息
    private static final String URL = "jdbc:mysql://localhost:3306/58tongcheng?useUnicode=true&characterEncoding=utf8&useSSL=false";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";
    
    // 获取数据库连接
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL驱动加载失败", e);
        }
    }
    
    // 关闭数据库资源
    public static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 执行SQL语句
    public static String executeSQL(String sql) throws SQLException {
        if (sql == null || sql.trim().isEmpty()) {
            throw new SQLException("SQL语句不能为空！");
        }
        
        sql = sql.trim();
        StringBuilder result = new StringBuilder();
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            if (sql.toLowerCase().startsWith("select")) {
                // 执行查询
                try (ResultSet rs = stmt.executeQuery(sql)) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    
                    // 添加列名
                    for (int i = 1; i <= columnCount; i++) {
                        result.append(metaData.getColumnName(i)).append("\t");
                    }
                    result.append("\n");
                    
                    // 添加数据
                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            result.append(rs.getString(i)).append("\t");
                        }
                        result.append("\n");
                    }
                }
            } else {
                // 执行更新
                int count = stmt.executeUpdate(sql);
                result.append("执行成功，影响 ").append(count).append(" 行数据。");
            }
        }
        
        return result.toString();
    }
} 