package dao;

import tools.JDBCTools;
import java.sql.*;

public class SqlDAO {
    public static String executeSQL(String sql) throws SQLException {
        if (sql == null || sql.trim().isEmpty()) {
            throw new SQLException("SQL语句不能为空！");
        }
        
        sql = sql.trim();
        StringBuilder result = new StringBuilder();
        
        try (Connection conn = JDBCTools.getConnection();
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