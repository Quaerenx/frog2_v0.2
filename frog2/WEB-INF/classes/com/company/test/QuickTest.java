package com.company.test;

import com.company.util.DBConnection;
import java.sql.Connection;

/**
 * 간단한 HikariCP 작동 확인 테스트
 */
public class QuickTest {
    public static void main(String[] args) {
        System.out.println("=== HikariCP 빠른 테스트 ===\n");
        
        try {
            // 1. Connection 획득 테스트
            long start = System.currentTimeMillis();
            Connection conn = DBConnection.getConnection();
            long elapsed = System.currentTimeMillis() - start;
            
            System.out.println("✅ Connection Pool 정상 작동!");
            System.out.println("   연결 획득 시간: " + elapsed + "ms");
            System.out.println("   Connection 객체: " + conn.getClass().getName());
            
            // 2. Pool 통계
            System.out.println("\n📊 " + DBConnection.getPoolStats());
            
            conn.close();
            System.out.println("\n✅ 모든 테스트 통과!");
            
        } catch (Exception e) {
            System.err.println("❌ 오류 발생: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.shutdown();
        }
    }
}
