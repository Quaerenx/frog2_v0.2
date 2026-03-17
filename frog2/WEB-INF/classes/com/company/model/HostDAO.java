package com.company.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.company.util.DBConnection;

public class HostDAO {

    public HostDAO() {
        ensureTable();
    }

    // ?åÏù¥Î∏??ùÏÑ±/?§ÌÇ§Îß?Î≥¥Ï†ï
    private void ensureTable() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "CREATE TABLE IF NOT EXISTS hosts (" +
                         "ip VARCHAR(15) PRIMARY KEY, " +
                         "user_name VARCHAR(200), " +
                         "purpose VARCHAR(500), " +
                         "host_location VARCHAR(500), " +
                         "note VARCHAR(1000), " +
                         "row_color VARCHAR(16)" +
                         ")";
            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
            DBConnection.close(pstmt);

            // Ïª¨Îüº Ï∂îÍ? ?úÎèÑ (?¥Î? ?àÏúºÎ©??àÏô∏ Î¨¥Ïãú)
            try {
                String alter = "ALTER TABLE hosts ADD COLUMN row_color VARCHAR(16)";
                pstmt = conn.prepareStatement(alter);
                pstmt.executeUpdate();
            } catch (SQLException ignore) {
                // Ïª¨Îüº???¥Î? ?àÎäî Í≤ΩÏö∞ ?±Ï? Î¨¥Ïãú
            } finally {
                DBConnection.close(pstmt);
            }
        } catch (SQLException  e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn);
        }
    }

    // Î™®Îì† ?∏Ïä§?∏Î? IP Í∏∞Ï??ºÎ°ú Map Î∞òÌôò
    public Map<String, HostDTO> getAllHostsMap() {
        Map<String, HostDTO> map = new HashMap<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT ip, user_name, purpose, host_location, note, row_color FROM hosts";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                HostDTO dto = new HostDTO();
                dto.setIp(rs.getString("ip"));
                dto.setUserName(rs.getString("user_name"));
                dto.setPurpose(rs.getString("purpose"));
                dto.setHostLocation(rs.getString("host_location"));
                dto.setNote(rs.getString("note"));
                dto.setRowColor(rs.getString("row_color"));
                map.put(dto.getIp(), dto);
            }
        } catch (SQLException  e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }
        return map;
    }

    // ?Ä???òÏ†ï (?ÜÏúºÎ©?INSERT, ?àÏúºÎ©?UPDATE)
    public boolean upsert(HostDTO dto) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            // Î®ºÏ? UPDATE ?úÎèÑ
            String update = "UPDATE hosts SET user_name = ?, purpose = ?, host_location = ?, note = ?, row_color = ? WHERE ip = ?";
            pstmt = conn.prepareStatement(update);
            pstmt.setString(1, safe(dto.getUserName()));
            pstmt.setString(2, safe(dto.getPurpose()));
            pstmt.setString(3, safe(dto.getHostLocation()));
            pstmt.setString(4, safe(dto.getNote()));
            pstmt.setString(5, safe(dto.getRowColor()));
            pstmt.setString(6, dto.getIp());
            int updated = pstmt.executeUpdate();
            DBConnection.close(pstmt);

            if (updated == 0) {
                String insert = "INSERT INTO hosts (ip, user_name, purpose, host_location, note, row_color) VALUES (?, ?, ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(insert);
                pstmt.setString(1, dto.getIp());
                pstmt.setString(2, safe(dto.getUserName()));
                pstmt.setString(3, safe(dto.getPurpose()));
                pstmt.setString(4, safe(dto.getHostLocation()));
                pstmt.setString(5, safe(dto.getNote()));
                pstmt.setString(6, safe(dto.getRowColor()));
                return pstmt.executeUpdate() > 0;
            }
            return true;
        } catch (SQLException  e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.close(pstmt);
            DBConnection.close(conn);
        }
    }

    // ??†ú
    public boolean deleteByIp(String ip) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM hosts WHERE ip = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, ip);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException  e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.close(pstmt);
            DBConnection.close(conn);
        }
    }

    private String safe(String v) {
        return v == null ? null : v.trim();
    }
}
