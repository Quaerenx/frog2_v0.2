package com.company.test;

import com.company.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * HikariCP Connection Pool 테스트 프로그램
 * 
 * 실행 방법:
 * 1. Eclipse에서 이 파일을 우클릭
 * 2. Run As > Java Application
 */
public class ConnectionPoolTest {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("HikariCP Connection Pool 테스트 시작");
        System.out.println("========================================\n");
        
        // 테스트 1: 단일 연결 테스트
        test1_SingleConnection();
        
        // 테스트 2: 멀티 연결 테스트 (성능 확인)
        test2_MultipleConnections();
        
        // 테스트 3: Pool 통계 확인
        test3_PoolStats();
        
        System.out.println("\n========================================");
        System.out.println("모든 테스트 완료!");
        System.out.println("========================================");
        
        // Connection Pool 종료
        DBConnection.shutdown();
    }
    
    /**
     * 테스트 1: 기본 연결 및 쿼리 실행
     */
    private static void test1_SingleConnection() {
        System.out.println("📌 테스트 1: 단일 연결 테스트");
        System.out.println("-----------------------------------------");
        
        long startTime = System.currentTimeMillis();
        
        try (Connection conn = DBConnection.getConnection()) {
            long elapsed = System.currentTimeMillis() - startTime;
            
            System.out.println("✅ Connection 획득 성공!");
            System.out.println("   - 소요 시간: " + elapsed + "ms");
            System.out.println("   - Auto Commit: " + conn.getAutoCommit());
            System.out.println("   - Read Only: " + conn.isReadOnly());
            
            // 간단한 쿼리 실행
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT 1 as test");
                 ResultSet rs = pstmt.executeQuery()) {
                
                if (rs.next()) {
                    System.out.println("   - 쿼리 실행 결과: " + rs.getInt("test"));
                    System.out.println("✅ 쿼리 실행 성공!");
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ 테스트 실패: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
    }
    
    /**
     * 테스트 2: 멀티 연결 성능 테스트
     */
    private static void test2_MultipleConnections() {
        System.out.println("📌 테스트 2: 멀티 연결 성능 테스트 (10회)");
        System.out.println("-----------------------------------------");
        
        int testCount = 10;
        long totalTime = 0;
        long minTime = Long.MAX_VALUE;
        long maxTime = 0;
        
        for (int i = 1; i <= testCount; i++) {
            long startTime = System.currentTimeMillis();
            
            try (Connection conn = DBConnection.getConnection()) {
                long elapsed = System.currentTimeMillis() - startTime;
                totalTime += elapsed;
                minTime = Math.min(minTime, elapsed);
                maxTime = Math.max(maxTime, elapsed);
                
                System.out.printf("   %2d회: %3dms%n", i, elapsed);
                
                // 실제 쿼리 실행
                try (PreparedStatement pstmt = conn.prepareStatement("SELECT 1");
                     ResultSet rs = pstmt.executeQuery()) {
                    rs.next();
                }
                
            } catch (Exception e) {
                System.err.println("   ❌ " + i + "회 실패: " + e.getMessage());
            }
        }
        
        System.out.println();
        System.out.println("📊 통계:");
        System.out.println("   - 평균 시간: " + (totalTime / testCount) + "ms");
        System.out.println("   - 최소 시간: " + minTime + "ms");
        System.out.println("   - 최대 시간: " + maxTime + "ms");
        System.out.println("   - 총 소요 시간: " + totalTime + "ms");
        
        if (totalTime / testCount < 20) {
            System.out.println("✅ 성능 우수! (평균 20ms 미만)");
        } else if (totalTime / testCount < 100) {
            System.out.println("✅ 성능 양호 (평균 100ms 미만)");
        } else {
            System.out.println("⚠️  성능 개선 필요 (평균 100ms 이상)");
        }
        
        System.out.println();
    }
    
    /**
     * 테스트 3: Pool 통계 확인
     */
    private static void test3_PoolStats() {
        System.out.println("📌 테스트 3: Connection Pool 통계");
        System.out.println("-----------------------------------------");
        
        String stats = DBConnection.getPoolStats();
        System.out.println("   " + stats);
        
        System.out.println();
    }
}
