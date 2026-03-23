package com.company.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.company.model.UserDTO;
import com.company.model.UserVmHostDAO;
import com.company.model.UserVmHostDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class UserVmHostServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserVmHostDAO userVmHostDAO = new UserVmHostDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserDTO user = getSessionUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserVmHostDTO editHost = null;
        String editIp = trim(request.getParameter("editIp"));
        if (editIp != null) {
            editHost = userVmHostDAO.getHostByIpAndOwner(editIp, user.getUserId());
        }

        renderPage(request, response, user, editHost);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserDTO user = getSessionUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String returnTo = trim(request.getParameter("returnTo"));
        boolean dashboardRequest = "dashboard".equals(returnTo);
        String action = trim(request.getParameter("action"));
        if ("delete".equals(action)) {
            userVmHostDAO.deleteByIpAndOwner(trim(request.getParameter("ip")), user.getUserId());
            if (dashboardRequest) {
                response.sendRedirect(buildDashboardRedirect(request, "deleted"));
            } else {
                response.sendRedirect(request.getContextPath() + "/vm-hosts?result=deleted");
            }
            return;
        }

        UserVmHostDTO dto = new UserVmHostDTO();
        dto.setIp(trim(request.getParameter("ip")));
        dto.setOwnerUserId(user.getUserId());
        dto.setOwnerUserName(user.getUserName());
        dto.setPurpose(trim(request.getParameter("purpose")));
        dto.setOsInfo(trim(request.getParameter("osInfo")));
        dto.setVerticaVersion(trim(request.getParameter("verticaVersion")));
        dto.setRemoteHost(trim(request.getParameter("remoteHost")));
        dto.setNote(trim(request.getParameter("note")));

        String originalIp = trim(request.getParameter("originalIp"));
        String errorMessage = userVmHostDAO.save(dto, originalIp);
        if (errorMessage != null) {
            if (dashboardRequest) {
                renderDashboard(request, response, user, dto, errorMessage);
                return;
            }
            request.setAttribute("errorMessage", errorMessage);
            renderPage(request, response, user, dto);
            return;
        }

        if (dashboardRequest) {
            response.sendRedirect(buildDashboardRedirect(request, "saved"));
        } else {
            response.sendRedirect(request.getContextPath() + "/vm-hosts?result=saved");
        }
    }

    private void renderPage(HttpServletRequest request, HttpServletResponse response, UserDTO user, UserVmHostDTO editHost)
            throws ServletException, IOException {
        List<UserVmHostDTO> vmHosts = userVmHostDAO.getActiveHostsByOwner(user.getUserId());
        int vmHostLimit = userVmHostDAO.getMaxHostsPerUser();
        int vmHostCount = vmHosts.size();

        request.setAttribute("user", user);
        request.setAttribute("vmHosts", vmHosts);
        request.setAttribute("vmHostCount", vmHostCount);
        request.setAttribute("vmHostLimit", vmHostLimit);
        request.setAttribute("vmHostRemaining", Math.max(0, vmHostLimit - vmHostCount));
        request.setAttribute("editHost", editHost);
        request.getRequestDispatcher("/vm_hosts/list.jsp").forward(request, response);
    }

    private void renderDashboard(HttpServletRequest request, HttpServletResponse response, UserDTO user,
            UserVmHostDTO formData, String errorMessage) throws ServletException, IOException {
        List<UserVmHostDTO> vmHosts = userVmHostDAO.getActiveHostsByOwner(user.getUserId());
        int vmHostLimit = userVmHostDAO.getMaxHostsPerUser();
        int vmHostCount = vmHosts.size();

        request.setAttribute("user", user);
        request.setAttribute("vmHosts", vmHosts);
        request.setAttribute("vmHostCount", vmHostCount);
        request.setAttribute("vmHostLimit", vmHostLimit);
        request.setAttribute("vmHostRemaining", Math.max(0, vmHostLimit - vmHostCount));
        request.setAttribute("vmHostForm", formData);
        request.setAttribute("vmHostErrorMessage", errorMessage);
        request.setAttribute("dashboardMenus", buildDashboardMenus());
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }

    private Map<String, List<DashboardServlet.MenuItem>> buildDashboardMenus() {
        Map<String, List<DashboardServlet.MenuItem>> dashboardMenus = new LinkedHashMap<>();

        List<DashboardServlet.MenuItem> customerMenus = new ArrayList<>();
        customerMenus.add(new DashboardServlet.MenuItem("고객 정보", "customers?view=list", "fas fa-address-card"));
        customerMenus.add(new DashboardServlet.MenuItem("정기점검 이력", "maintenance", "fas fa-clipboard-check"));
        dashboardMenus.put("고객 관리", customerMenus);

        List<DashboardServlet.MenuItem> archiveMenus = new ArrayList<>();
        archiveMenus.add(new DashboardServlet.MenuItem("회의록", "meeting?view=list", "fas fa-users"));
        archiveMenus.add(new DashboardServlet.MenuItem("자료실", "filerepo/filerepo_downlist.jsp", "fas fa-file-alt"));
        archiveMenus.add(new DashboardServlet.MenuItem("트러블슈팅", "troubleshooting?view=list", "fas fa-tools"));
        dashboardMenus.put("자료 관리", archiveMenus);

        return dashboardMenus;
    }

    private String buildDashboardRedirect(HttpServletRequest request, String result) {
        return request.getContextPath() + "/dashboard?vmHostResult="
                + URLEncoder.encode(result, StandardCharsets.UTF_8);
    }

    private UserDTO getSessionUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object user = session.getAttribute("user");
        if (user instanceof UserDTO) {
            return (UserDTO) user;
        }
        return null;
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
