package com.company.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.company.util.DBConnection;
import com.company.util.PasswordUtils;

public class UserDAO {

    // 사용자 인증 메소드
    public UserDTO authenticateUser(String userId, String password) {
        UserDTO user = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT userId, password, userName FROM company_users WHERE userId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");

                if (PasswordUtils.checkPassword(password, hashedPassword)) {
                    user = new UserDTO();
                    user.setUserId(rs.getString("userId"));
                    user.setPassword("");
                    user.setUserName(rs.getString("userName"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }
        return user;
    }

    // 새 사용자 등록 메소드
    public boolean registerUser(UserDTO user) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO company_users (userId, password, userName) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, PasswordUtils.hashPassword(user.getPassword()));
            pstmt.setString(3, user.getUserName());

            int rowsAffected = pstmt.executeUpdate();
            success = (rowsAffected > 0);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(pstmt, conn);
        }
        return success;
    }

    // 사용자 비밀번호 변경 메소드
    public boolean updatePassword(String userId, String newPassword) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE company_users SET password = ? WHERE userId = ?";
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, PasswordUtils.hashPassword(newPassword));
            pstmt.setString(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            success = (rowsAffected > 0);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(pstmt, conn);
        }
        return success;
    }

    // ID로 사용자 정보 조회 메소드
    public UserDTO getUserById(String userId) {
        UserDTO user = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            
            // department 컬럼 존재 여부 확인
            boolean hasDepartment = columnExists(conn, "company_users", "department");
            
            String sql;
            if (hasDepartment) {
                sql = "SELECT userId, userName, department FROM company_users WHERE userId = ?";
            } else {
                sql = "SELECT userId, userName FROM company_users WHERE userId = ?";
            }
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new UserDTO();
                user.setUserId(rs.getString("userId"));
                user.setUserName(rs.getString("userName"));
                
                if (hasDepartment) {
                    user.setDepartment(rs.getString("department"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }
        return user;
    }

    // 사용자 프로필 업데이트 메소드
    public boolean updateUserProfile(String userId, String userName, String department) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            
            // department 컬럼 존재 여부 확인
            boolean hasDepartment = columnExists(conn, "company_users", "department");
            
            String sql;
            if (hasDepartment) {
                sql = "UPDATE company_users SET userName = ?, department = ? WHERE userId = ?";
            } else {
                sql = "UPDATE company_users SET userName = ? WHERE userId = ?";
            }
            
            pstmt = conn.prepareStatement(sql);

            if (hasDepartment) {
                pstmt.setString(1, userName);
                pstmt.setString(2, department);
                pstmt.setString(3, userId);
            } else {
                pstmt.setString(1, userName);
                pstmt.setString(2, userId);
            }

            int rowsAffected = pstmt.executeUpdate();
            success = (rowsAffected > 0);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(pstmt, conn);
        }
        return success;
    }
    
    // 컬럼 존재 여부 확인 헬퍼 메소드
    private boolean columnExists(Connection conn, String tableName, String columnName) {
        ResultSet rs = null;
        try {
            java.sql.DatabaseMetaData meta = conn.getMetaData();
            rs = meta.getColumns(null, null, tableName, columnName);
            if (rs.next()) return true;
            DBConnection.close(rs);
            rs = meta.getColumns(null, null, tableName.toUpperCase(), columnName.toUpperCase());
            return rs.next();
        } catch (SQLException e) {
            return false;
        } finally {
            DBConnection.close(rs);
        }
    }
}