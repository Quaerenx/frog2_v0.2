package com.company.controller;

import com.company.util.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Connection Pool 상태 모니터링 Servlet
 * URL: /admin/pool-status
 */
@WebServlet("/admin/pool-status")
public class PoolMonitorServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(PoolMonitorServlet.class);
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html><head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Connection Pool Monitor</title>");
        out.println("<style>");
        out.println("body { font-family: Arial; margin: 20px; background: #f5f5f5; }");
        out.println(".container { max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
        out.println("h1 { color: #333; border-bottom: 2px solid #4CAF50; padding-bottom: 10px; }");
        out.println(".stat { margin: 15px 0; padding: 10px; background: #e8f5e9; border-left: 4px solid #4CAF50; }");
        out.println(".test-result { padding: 10px; margin-top: 20px; border-radius: 4px; }");
        out.println(".success { background: #d4edda; border: 1px solid #c3e6cb; color: #155724; }");
        out.println(".error { background: #f8d7da; border: 1px solid #f5c6cb; color: #721c24; }");
        out.println(".refresh-btn { background: #4CAF50; color: white; border: none; padding: 10px 20px; cursor: pointer; border-radius: 4px; }");
        out.println("</style>");
        out.println("</head><body>");
        out.println("<div class='container'>");
        out.println("<h1>🔧 HikariCP Connection Pool 모니터</h1>");
        
        // Connection Pool 통계
        out.println("<h2>📊 Pool 통계</h2>");
        String poolStats = DBConnection.getPoolStats();
        out.println("<div class='stat'>" + poolStats + "</div>");
        
        // Connection 테스트
        out.println("<h2>🔌 연결 테스트</h2>");
        long startTime = System.currentTimeMillis();
        boolean testPassed = false;
        String errorMessage = null;
        
        try (Connection conn = DBConnection.getConnection()) {
            long elapsed = System.currentTimeMillis() - startTime;
            testPassed = true;
            
            out.println("<div class='test-result success'>");
            out.println("✅ <strong>연결 성공!</strong><br>");
            out.println("연결 획득 시간: " + elapsed + "ms<br>");
            out.println("Auto Commit: " + conn.getAutoCommit() + "<br>");
            out.println("Read Only: " + conn.isReadOnly() + "<br>");
            out.println("Catalog: " + conn.getCatalog());
            out.println("</div>");
            
            logger.info("Pool 모니터 - 연결 테스트 성공 ({}ms)", elapsed);
            
        } catch (SQLException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            errorMessage = e.getMessage();
            
            out.println("<div class='test-result error'>");
            out.println("❌ <strong>연결 실패!</strong><br>");
            out.println("소요 시간: " + elapsed + "ms<br>");
            out.println("오류 메시지: " + errorMessage);
            out.println("</div>");
            
            logger.error("Pool 모니터 - 연결 테스트 실패", e);
        }
        
        // 새로고침 버튼
        out.println("<br><button class='refresh-btn' onclick='location.reload()'>🔄 새로고침</button>");
        
        out.println("<p style='margin-top: 30px; color: #666; font-size: 12px;'>");
        out.println("마지막 갱신: " + new java.util.Date());
        out.println("</p>");
        
        out.println("</div>");
        out.println("</body></html>");
    }
}
