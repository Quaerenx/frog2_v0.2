package com.company.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.company.model.UserDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// @WebServlet("/dashboard") - web.xml에서 매핑하므로 주석 처리
public class DashboardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 세션 확인
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("user") == null) {
			// 로그인되지 않은 사용자는 로그인 페이지로 리다이렉트
			response.sendRedirect("login");
			return;
		}

		// 사용자 정보 가져오기
		UserDTO user = (UserDTO) session.getAttribute("user");
		request.setAttribute("user", user);

		// /dashboard2 요청은 새로운 디자인 페이지로 바로 포워드
		String servletPath = request.getServletPath();
		if ("/dashboard2".equals(servletPath)) {
			request.getRequestDispatcher("/dashboard2.jsp").forward(request, response);
			return;
		}

		// 대시보드 메뉴 구성 정보 설정 (카테고리별) - 순서 보장을 위해 LinkedHashMap 사용
		Map<String, List<MenuItem>> dashboardMenus = new LinkedHashMap<>();
		
		// 고객관리 메뉴
		List<MenuItem> customerMenus = new ArrayList<>();
		customerMenus.add(new MenuItem(" 고객사 정보", "customers?view=list", "fas fa-address-card"));
		customerMenus.add(new MenuItem(" 정기점검 이력", "maintenance", "fas fa-clipboard-check"));
		dashboardMenus.put("고객관리", customerMenus);

		// 자료관리 메뉴
		List<MenuItem> archiveMenus = new ArrayList<>();
		archiveMenus.add(new MenuItem(" 회의록", "meeting?view=list", "fas fa-users"));
        archiveMenus.add(new MenuItem(" 자료실", "filerepo/filerepo_downlist.jsp", "fas fa-file-alt"));
		archiveMenus.add(new MenuItem(" 트러블슈팅", "troubleshooting?view=list", "fas fa-tools"));
		dashboardMenus.put("자료관리", archiveMenus);


		// 대시보드 메뉴 구성 정보를 request에 설정
		request.setAttribute("dashboardMenus", dashboardMenus);

		// 대시보드 페이지로 포워드
		request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
	}

	// 메뉴 아이템 클래스 (내부 클래스)
	public static class MenuItem {
		private String title;
		private String url;
		private String icon;

		public MenuItem(String title, String url, String icon) {
			this.title = title;
			this.url = url;
			this.icon = icon;
		}

		public String getTitle() {
			return title;
		}

		public String getUrl() {
			return url;
		}

		public String getIcon() {
			return icon;
		}
	}
}