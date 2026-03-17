package com.company.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.company.util.DBConnection;

public class MeetingRecordDAO {
    private static final int PAGE_SIZE = 20;

    // ?˜ì´ì§?ì²˜ë¦¬???Œì˜ë¡?ëª©ë¡ ì¡°íšŒ (?“ê? ê°œìˆ˜ ?¬í•¨)
    public List<MeetingRecordDTO> getMeetingRecords(int page) {
        List<MeetingRecordDTO> records = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT m.*, " +
                        "COALESCE(c.comment_count, 0) as comment_count " +
                        "FROM meeting_records m " +
                        "LEFT JOIN (SELECT meeting_id, COUNT(*) as comment_count FROM meeting_comments GROUP BY meeting_id) c " +
                        "ON m.meeting_id = c.meeting_id " +
                        "ORDER BY m.meeting_datetime DESC " +
                        "LIMIT ? OFFSET ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, PAGE_SIZE);
            pstmt.setInt(2, (page - 1) * PAGE_SIZE);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MeetingRecordDTO record = new MeetingRecordDTO();
                record.setMeetingId(rs.getLong("meeting_id"));
                record.setTitle(rs.getString("title"));
                record.setMeetingDatetime(rs.getTimestamp("meeting_datetime"));
                record.setMeetingType(rs.getString("meeting_type"));
                record.setContent(rs.getString("content"));
                record.setAuthorId(rs.getString("author_id"));
                record.setAuthorName(rs.getString("author_name"));
                record.setViewCount(rs.getInt("view_count"));
                record.setCreatedAt(rs.getTimestamp("created_at"));
                record.setUpdatedAt(rs.getTimestamp("updated_at"));
                record.setCommentCount(rs.getInt("comment_count"));

                records.add(record);
            }
        } catch (SQLException  e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return records;
    }

    // ?„ì²´ ?Œì˜ë¡?ê°œìˆ˜ ì¡°íšŒ (?˜ì´ì§•ìš©)
    public int getTotalCount() {
        int count = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM meeting_records";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException  e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return count;
    }

    // ?¹ì • ?Œì˜ë¡?ì¡°íšŒ (ì¡°íšŒ??ì¦ê?)
    public MeetingRecordDTO getMeetingRecord(Long meetingId) {
        MeetingRecordDTO record = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();

            // ì¡°íšŒ??ì¦ê?
            String updateSql = "UPDATE meeting_records SET view_count = view_count + 1 WHERE meeting_id = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setLong(1, meetingId);
            pstmt.executeUpdate();
            DBConnection.close(pstmt);

            // ?Œì˜ë¡?ì¡°íšŒ
            String selectSql = "SELECT * FROM meeting_records WHERE meeting_id = ?";
            pstmt = conn.prepareStatement(selectSql);
            pstmt.setLong(1, meetingId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                record = new MeetingRecordDTO();
                record.setMeetingId(rs.getLong("meeting_id"));
                record.setTitle(rs.getString("title"));
                record.setMeetingDatetime(rs.getTimestamp("meeting_datetime"));
                record.setMeetingType(rs.getString("meeting_type"));
                record.setContent(rs.getString("content"));
                record.setAuthorId(rs.getString("author_id"));
                record.setAuthorName(rs.getString("author_name"));
                record.setViewCount(rs.getInt("view_count"));
                record.setCreatedAt(rs.getTimestamp("created_at"));
                record.setUpdatedAt(rs.getTimestamp("updated_at"));
            }
        } catch (SQLException  e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return record;
    }

    // ?Œì˜ë¡?ì¶”ê?
    public boolean addMeetingRecord(MeetingRecordDTO record) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO meeting_records (title, meeting_datetime, meeting_type, content, author_id, author_name) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, record.getTitle());
            pstmt.setTimestamp(2, record.getMeetingDatetime());
            pstmt.setString(3, record.getMeetingType());
            pstmt.setString(4, record.getContent());
            pstmt.setString(5, record.getAuthorId());
            pstmt.setString(6, record.getAuthorName());

            int rowsAffected = pstmt.executeUpdate();
            success = (rowsAffected > 0);

        } catch (SQLException  e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(pstmt, conn);
        }

        return success;
    }

    // ?Œì˜ë¡??˜ì •
    public boolean updateMeetingRecord(MeetingRecordDTO record) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE meeting_records SET title = ?, meeting_datetime = ?, meeting_type = ?, " +
                        "content = ?, updated_at = statement_timestamp() WHERE meeting_id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, record.getTitle());
            pstmt.setTimestamp(2, record.getMeetingDatetime());
            pstmt.setString(3, record.getMeetingType());
            pstmt.setString(4, record.getContent());
            pstmt.setLong(5, record.getMeetingId());

            int rowsAffected = pstmt.executeUpdate();
            success = (rowsAffected > 0);

        } catch (SQLException  e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(pstmt, conn);
        }

        return success;
    }

    // ?Œì˜ë¡??? œ
    public boolean deleteMeetingRecord(Long meetingId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM meeting_records WHERE meeting_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, meetingId);

            int rowsAffected = pstmt.executeUpdate();
            success = (rowsAffected > 0);

        } catch (SQLException  e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(pstmt, conn);
        }

        return success;
    }

    // ?‘ì„±??ê¶Œí•œ ?•ì¸
    public boolean isAuthor(Long meetingId, String userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isAuthor = false;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT author_id FROM meeting_records WHERE meeting_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, meetingId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String authorId = rs.getString("author_id");
                isAuthor = userId.equals(authorId);
            }
        } catch (SQLException  e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return isAuthor;
    }

    // ?˜ì´ì§€ ?¬ê¸° ë°˜í™˜
    public static int getPageSize() {
        return PAGE_SIZE;
    }
}
