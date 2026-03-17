package com.company.model;

import com.company.util.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 월별 고객 응대 기록 DAO
 */
public class MonthlyCustomerResponseDAO {
    private static final Logger logger = LoggerFactory.getLogger(MonthlyCustomerResponseDAO.class);
    
    /**
     * 특정 사용자의 월별 고객 응대 기록 조회
     */
    public List<MonthlyCustomerResponseDTO> getMonthlyResponses(String userName, int year, int month) {
        List<MonthlyCustomerResponseDTO> list = new ArrayList<>();
        String sql = "SELECT id, created_by, response_date, customer_name, reason, " +
                    "action_content, note, created_at, updated_at " +
                    "FROM monthly_customer_response " +
                    "WHERE created_by = ? AND YEAR(response_date) = ? AND MONTH(response_date) = ? " +
                    "ORDER BY response_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userName);
            pstmt.setInt(2, year);
            pstmt.setInt(3, month);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MonthlyCustomerResponseDTO dto = new MonthlyCustomerResponseDTO();
                    dto.setId(rs.getInt("id"));
                    dto.setUserName(rs.getString("created_by"));
                    dto.setResponseDate(rs.getDate("response_date"));
                    dto.setCustomerName(rs.getString("customer_name"));
                    dto.setReason(rs.getString("reason"));
                    dto.setActionContent(rs.getString("action_content"));
                    dto.setNote(rs.getString("note"));
                    dto.setCreatedAt(rs.getTimestamp("created_at"));
                    dto.setUpdatedAt(rs.getTimestamp("updated_at"));
                    list.add(dto);
                }
            }
            
            logger.info("월별 고객 응대 조회 완료 - 사용자: {}, 년월: {}-{}, 건수: {}", 
                       userName, year, month, list.size());
            
        } catch (SQLException e) {
            logger.error("월별 고객 응대 조회 실패 - 사용자: {}, 년월: {}-{}", userName, year, month, e);
        }
        
        return list;
    }
    
    /**
     * 고객 응대 기록 추가
     */
    public boolean addResponse(MonthlyCustomerResponseDTO dto) {
        String sql = "INSERT INTO monthly_customer_response " +
                    "(created_by, response_date, customer_name, reason, action_content, note, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dto.getUserName());
            pstmt.setDate(2, new java.sql.Date(dto.getResponseDate().getTime()));
            pstmt.setString(3, dto.getCustomerName());
            pstmt.setString(4, dto.getReason());
            pstmt.setString(5, dto.getActionContent());
            pstmt.setString(6, dto.getNote());
            
            int result = pstmt.executeUpdate();
            logger.info("고객 응대 추가 완료 - 사용자: {}, 고객명: {}", dto.getUserName(), dto.getCustomerName());
            return result > 0;
            
        } catch (SQLException e) {
            logger.error("고객 응대 추가 실패 - 사용자: {}", dto.getUserName(), e);
            return false;
        }
    }
    
    /**
     * 고객 응대 기록 수정
     */
    public boolean updateResponse(MonthlyCustomerResponseDTO dto) {
        String sql = "UPDATE monthly_customer_response SET " +
                    "response_date = ?, customer_name = ?, reason = ?, " +
                    "action_content = ?, note = ?, updated_at = GETDATE() " +
                    "WHERE id = ? AND created_by = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, new java.sql.Date(dto.getResponseDate().getTime()));
            pstmt.setString(2, dto.getCustomerName());
            pstmt.setString(3, dto.getReason());
            pstmt.setString(4, dto.getActionContent());
            pstmt.setString(5, dto.getNote());
            pstmt.setInt(6, dto.getId());
            pstmt.setString(7, dto.getUserName());
            
            int result = pstmt.executeUpdate();
            logger.info("고객 응대 수정 완료 - ID: {}, 사용자: {}", dto.getId(), dto.getUserName());
            return result > 0;
            
        } catch (SQLException e) {
            logger.error("고객 응대 수정 실패 - ID: {}", dto.getId(), e);
            return false;
        }
    }
    
    /**
     * 고객 응대 기록 삭제
     */
    public boolean deleteResponse(int id, String userName) {
        String sql = "DELETE FROM monthly_customer_response WHERE id = ? AND created_by = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.setString(2, userName);
            
            int result = pstmt.executeUpdate();
            logger.info("고객 응대 삭제 완료 - ID: {}, 사용자: {}", id, userName);
            return result > 0;
            
        } catch (SQLException e) {
            logger.error("고객 응대 삭제 실패 - ID: {}", id, e);
            return false;
        }
    }
    
    /**
     * 특정 고객 응대 기록 조회
     */
    public MonthlyCustomerResponseDTO getResponseById(int id) {
        String sql = "SELECT id, created_by, response_date, customer_name, reason, " +
                    "action_content, note, created_at, updated_at " +
                    "FROM monthly_customer_response WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    MonthlyCustomerResponseDTO dto = new MonthlyCustomerResponseDTO();
                    dto.setId(rs.getInt("id"));
                    dto.setUserName(rs.getString("created_by"));
                    dto.setResponseDate(rs.getDate("response_date"));
                    dto.setCustomerName(rs.getString("customer_name"));
                    dto.setReason(rs.getString("reason"));
                    dto.setActionContent(rs.getString("action_content"));
                    dto.setNote(rs.getString("note"));
                    dto.setCreatedAt(rs.getTimestamp("created_at"));
                    dto.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return dto;
                }
            }
            
        } catch (SQLException e) {
            logger.error("고객 응대 조회 실패 - ID: {}", id, e);
        }
        
        return null;
    }
}