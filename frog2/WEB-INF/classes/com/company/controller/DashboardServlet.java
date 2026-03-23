package com.company.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.company.model.UserDTO;
import com.company.model.UserVmHostDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserVmHostDAO userVmHostDAO = new UserVmHostDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        UserDTO user = (UserDTO) session.getAttribute("user");
        request.setAttribute("user", user);

        String servletPath = request.getServletPath();
        if ("/dashboard2".equals(servletPath)) {
            request.getRequestDispatcher("/dashboard2.jsp").forward(request, response);
            return;
        }

        int vmHostCount = userVmHostDAO.countActiveHostsByOwner(user.getUserId());
        int vmHostLimit = userVmHostDAO.getMaxHostsPerUser();

        request.setAttribute("vmHosts", userVmHostDAO.getActiveHostsByOwner(user.getUserId()));
        request.setAttribute("vmHostCount", vmHostCount);
        request.setAttribute("vmHostLimit", vmHostLimit);
        request.setAttribute("vmHostRemaining", Math.max(0, vmHostLimit - vmHostCount));

        Map<String, List<MenuItem>> dashboardMenus = new LinkedHashMap<>();

        List<MenuItem> customerMenus = new ArrayList<>();
        customerMenus.add(new MenuItem("고객 정보", "customers?view=list", "fas fa-address-card"));
        customerMenus.add(new MenuItem("정기점검 이력", "maintenance", "fas fa-clipboard-check"));
        dashboardMenus.put("고객 관리", customerMenus);

        List<MenuItem> archiveMenus = new ArrayList<>();
        archiveMenus.add(new MenuItem("회의록", "meeting?view=list", "fas fa-users"));
        archiveMenus.add(new MenuItem("자료실", "filerepo/filerepo_downlist.jsp", "fas fa-file-alt"));
        archiveMenus.add(new MenuItem("트러블슈팅", "troubleshooting?view=list", "fas fa-tools"));
        dashboardMenus.put("자료 관리", archiveMenus);

        request.setAttribute("dashboardMenus", dashboardMenus);
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }

    public static class MenuItem {
        private final String title;
        private final String url;
        private final String icon;

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
