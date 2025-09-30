package com.company.controller;

import java.io.IOException;

import com.company.model.UserDAO;
import com.company.model.UserDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// @WebServlet("/login") - web.xml에서 매핑하므로 주석 처리
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 이미 로그인된 사용자라면 메인 페이지로 리다이렉트
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect("dashboard");
        } else {
            // 로그인 페이지로 포워드
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 요청 파라미터 수신
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");

        // 사용자 인증
        UserDAO userDAO = new UserDAO();

        try {
            UserDTO user = userDAO.authenticateUser(userId, password);
	        if (user != null) {
	            // 인증 성공: 세션 생성 및 사용자 정보 저장
	            HttpSession session = request.getSession();
	            session.setAttribute("user", user);
	            session.setMaxInactiveInterval(360 * 60); // 세션 유효 시간 설정 (6시간)

	            // 대시보드로 리다이렉트
	            response.sendRedirect("dashboard");
	        } else {
	            // 인증 실패: 에러 메시지와 함께 로그인 페이지로 이동
	            request.setAttribute("errorMessage", "아이디 또는 비밀번호가 올바르지 않습니다.");
	            request.getRequestDispatcher("login.jsp").forward(request, response);
	        }
        } catch (RuntimeException e) {
            // DAO에서 발생한 예외 처리
            request.setAttribute("errorMessage", "시스템 오류가 발생했습니다. 관리자에게 문의하세요.");
            // 로그 기록
            System.err.println("로그인 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}