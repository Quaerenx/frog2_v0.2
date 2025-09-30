package com.company.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.company.util.DBConnection;

public class MaintenanceRecordDAO {

    // 담당자별로 정기점검 이력을 그룹화하여 조회
    public Map<String, List<MaintenanceRecordDTO>> getMaintenanceRecordsByInspector() {
        Map<String, List<MaintenanceRecordDTO>> inspectorRecords = new LinkedHashMap<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM maintenance_records ORDER BY inspector_name, inspection_date DESC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MaintenanceRecordDTO record = new MaintenanceRecordDTO();
                record.setMaintenanceId(rs.getLong("maintenance_id"));
                record.setCustomerName(rs.getString("customer_name"));
                record.setInspectorName(rs.getString("inspector_name"));
                record.setInspectionDate(rs.getDate("inspection_date"));
                record.setVerticaVersion(rs.getString("vertica_version"));
                record.setNote(rs.getString("note"));
                record.setCreatedAt(rs.getTimestamp("created_at"));
                record.setUpdatedAt(rs.getTimestamp("updated_at"));

                String inspectorName = record.getInspectorName();
                inspectorRecords.computeIfAbsent(inspectorName, k -> new ArrayList<>()).add(record);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return inspectorRecords;
    }

    // 모든 정기점검 이력 조회
    public List<MaintenanceRecordDTO> getAllMaintenanceRecords() {
        List<MaintenanceRecordDTO> records = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM maintenance_records ORDER BY inspection_date DESC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MaintenanceRecordDTO record = new MaintenanceRecordDTO();
                record.setMaintenanceId(rs.getLong("maintenance_id"));
                record.setCustomerName(rs.getString("customer_name"));
                record.setInspectorName(rs.getString("inspector_name"));
                record.setInspectionDate(rs.getDate("inspection_date"));
                record.setVerticaVersion(rs.getString("vertica_version"));
                record.setNote(rs.getString("note"));
                record.setCreatedAt(rs.getTimestamp("created_at"));
                record.setUpdatedAt(rs.getTimestamp("updated_at"));

                records.add(record);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return records;
    }

    // 특정 담당자의 고객사 목록 조회 (활성 상태 고객사만)
    public List<String> getCustomersByInspector(String inspectorName) {
        List<String> customers = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT DISTINCT customer_name FROM vertica_customer_detail " +
                        "WHERE (main_manager = ? OR sub_manager = ?) " +
                        "ORDER BY customer_name";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, inspectorName);
            pstmt.setString(2, inspectorName);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                customers.add(rs.getString("customer_name"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return customers;
    }

    // 새로운 정기점검 이력 추가
    public boolean addMaintenanceRecord(MaintenanceRecordDTO record) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO maintenance_records (customer_name, inspector_name, " +
                        "inspection_date, vertica_version, note) VALUES (?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, record.getCustomerName());
            pstmt.setString(2, record.getInspectorName());
            pstmt.setDate(3, record.getInspectionDate());
            setStringOrNull(pstmt, 4, record.getVerticaVersion());
            setStringOrNull(pstmt, 5, record.getNote());

            int rowsAffected = pstmt.executeUpdate();
            success = (rowsAffected > 0);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(pstmt, conn);
        }

        return success;
    }

    // 정기점검 이력 수정
    public boolean updateMaintenanceRecord(MaintenanceRecordDTO record) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE maintenance_records SET customer_name = ?, inspector_name = ?, " +
                        "inspection_date = ?, vertica_version = ?, note = ?, updated_at = statement_timestamp() " +
                        "WHERE maintenance_id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, record.getCustomerName());
            pstmt.setString(2, record.getInspectorName());
            pstmt.setDate(3, record.getInspectionDate());
            setStringOrNull(pstmt, 4, record.getVerticaVersion());
            setStringOrNull(pstmt, 5, record.getNote());
            pstmt.setLong(6, record.getMaintenanceId());

            int rowsAffected = pstmt.executeUpdate();
            success = (rowsAffected > 0);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(pstmt, conn);
        }

        return success;
    }

    // 정기점검 이력 삭제
    public boolean deleteMaintenanceRecord(Long maintenanceId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM maintenance_records WHERE maintenance_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, maintenanceId);

            int rowsAffected = pstmt.executeUpdate();
            success = (rowsAffected > 0);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(pstmt, conn);
        }

        return success;
    }

    // ID로 특정 정기점검 이력 조회
    public MaintenanceRecordDTO getMaintenanceRecordById(Long maintenanceId) {
        MaintenanceRecordDTO record = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM maintenance_records WHERE maintenance_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, maintenanceId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                record = new MaintenanceRecordDTO();
                record.setMaintenanceId(rs.getLong("maintenance_id"));
                record.setCustomerName(rs.getString("customer_name"));
                record.setInspectorName(rs.getString("inspector_name"));
                record.setInspectionDate(rs.getDate("inspection_date"));
                record.setVerticaVersion(rs.getString("vertica_version"));
                record.setNote(rs.getString("note"));
                record.setCreatedAt(rs.getTimestamp("created_at"));
                record.setUpdatedAt(rs.getTimestamp("updated_at"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return record;
    }

    // 특정 고객사의 정기점검 이력 조회
    public List<MaintenanceRecordDTO> getMaintenanceRecordsByCustomer(String customerName) {
        List<MaintenanceRecordDTO> records = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM maintenance_records WHERE customer_name = ? ORDER BY inspection_date DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, customerName);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MaintenanceRecordDTO record = new MaintenanceRecordDTO();
                record.setMaintenanceId(rs.getLong("maintenance_id"));
                record.setCustomerName(rs.getString("customer_name"));
                record.setInspectorName(rs.getString("inspector_name"));
                record.setInspectionDate(rs.getDate("inspection_date"));
                record.setVerticaVersion(rs.getString("vertica_version"));
                record.setNote(rs.getString("note"));
                record.setCreatedAt(rs.getTimestamp("created_at"));
                record.setUpdatedAt(rs.getTimestamp("updated_at"));

                records.add(record);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return records;
    }

    // 빈 문자열을 NULL로 처리하는 도우미 메서드
    private void setStringOrNull(PreparedStatement pstmt, int parameterIndex, String value) throws SQLException {
        if (value == null || value.trim().isEmpty()) {
            pstmt.setNull(parameterIndex, Types.VARCHAR);
        } else {
            pstmt.setString(parameterIndex, value.trim());
        }
    }
}