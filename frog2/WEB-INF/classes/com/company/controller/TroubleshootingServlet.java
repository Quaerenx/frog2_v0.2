package com.company.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.company.model.CustomerDAO;
import com.company.model.CustomerDTO;
import com.company.model.TroubleshootingDAO;
import com.company.model.TroubleshootingDTO;
import com.company.model.UserDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// @WebServlet("/troubleshooting") - web.xml에서 매핑하므로 주석 처리
public class TroubleshootingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 세션 확인
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        UserDTO user = (UserDTO) session.getAttribute("user");
        request.setAttribute("user", user);

        String viewType = request.getParameter("view");
        if (viewType == null || viewType.isEmpty()) {
            viewType = "list";
        }

        TroubleshootingDAO tsDAO = new TroubleshootingDAO();

        if ("list".equals(viewType)) {
            // 목록/검색 조회
            String q = request.getParameter("q");
            List<TroubleshootingDTO> troubleshootingList;
            if (q != null && !q.trim().isEmpty()) {
                troubleshootingList = tsDAO.searchTroubleshooting(q.trim());
                request.setAttribute("q", q.trim());
            } else {
                troubleshootingList = tsDAO.getAllTroubleshooting();
            }
            if (troubleshootingList == null) {
                troubleshootingList = new java.util.ArrayList<>();
            }

            request.setAttribute("troubleshootingList", troubleshootingList);
            request.setAttribute("viewType", "list");
            request.getRequestDispatcher("/troubleshooting/troubleshooting_list.jsp").forward(request, response);

        } else if ("add".equals(viewType)) {
            // 등록 폼
            CustomerDAO customerDAO = new CustomerDAO();
            List<CustomerDTO> customerList = customerDAO.getAllCustomers("", "ASC");
            request.setAttribute("customerList", customerList);
            request.setAttribute("viewType", "add");
            request.getRequestDispatcher("/troubleshooting/troubleshooting_add.jsp").forward(request, response);

        } else if ("view".equals(viewType)) {
            // 상세 조회
            String idParam = request.getParameter("id");
            if (idParam != null && !idParam.isEmpty()) {
                try {
                    int id = Integer.parseInt(idParam);
                    TroubleshootingDTO troubleshooting = tsDAO.getTroubleshootingById(id);

                    if (troubleshooting != null) {
                        request.setAttribute("troubleshooting", troubleshooting);
                        request.setAttribute("viewType", "view");
                        request.getRequestDispatcher("/troubleshooting/troubleshooting_view.jsp").forward(request, response);
                    } else {
                        session.setAttribute("error", "해당 트러블 슈팅 정보를 찾을 수 없습니다.");
                        response.sendRedirect("troubleshooting?view=list");
                    }
                } catch (NumberFormatException e) {
                    session.setAttribute("error", "잘못된 ID 형식입니다.");
                    response.sendRedirect("troubleshooting?view=list");
                }
            } else {
                response.sendRedirect("troubleshooting?view=list");
            }

        } else if ("edit".equals(viewType)) {
            // 수정 폼
            String idParam = request.getParameter("id");
            if (idParam != null && !idParam.isEmpty()) {
                try {
                    int id = Integer.parseInt(idParam);
                    TroubleshootingDTO troubleshooting = tsDAO.getTroubleshootingById(id);

                    if (troubleshooting != null) {
                        CustomerDAO customerDAO = new CustomerDAO();
                        List<CustomerDTO> customerList = customerDAO.getAllCustomers("", "ASC");

                        request.setAttribute("troubleshooting", troubleshooting);
                        request.setAttribute("customerList", customerList);
                        request.setAttribute("viewType", "edit");
                        request.getRequestDispatcher("/troubleshooting/troubleshooting_edit.jsp").forward(request, response);
                    } else {
                        session.setAttribute("error", "해당 트러블 슈팅 정보를 찾을 수 없습니다.");
                        response.sendRedirect("troubleshooting?view=list");
                    }
                } catch (NumberFormatException e) {
                    session.setAttribute("error", "잘못된 ID 형식입니다.");
                    response.sendRedirect("troubleshooting?view=list");
                }
            } else {
                response.sendRedirect("troubleshooting?view=list");
            }

        } else {
            response.sendRedirect("troubleshooting?view=list");
        }
    }

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        UserDTO user = (UserDTO) session.getAttribute("user");
        String actionType = request.getParameter("action");

        TroubleshootingDAO tsDAO = new TroubleshootingDAO();

        if ("add".equals(actionType)) {
            // 새 트러블 슈팅 등록
            TroubleshootingDTO ts = new TroubleshootingDTO();
            ts.setTitle(request.getParameter("title"));
            ts.setCustomerName(request.getParameter("customer_name"));
            ts.setCustomerManager(request.getParameter("customer_manager"));
            ts.setOccurrenceDate(parseDate(request.getParameter("occurrence_date")));
            ts.setWorkPersonnel(request.getParameter("work_personnel"));
            ts.setWorkPeriod(request.getParameter("work_period"));
            ts.setCreator(user.getUserName());
            ts.setSupportType(request.getParameter("support_type"));
            ts.setCaseOpenYn(request.getParameter("case_open_yn"));
            ts.setOverview(request.getParameter("overview"));
            ts.setCauseAnalysis(request.getParameter("cause_analysis"));
            ts.setErrorContent(request.getParameter("error_content"));
            ts.setActionTaken(request.getParameter("action_taken"));
            ts.setScriptContent(request.getParameter("script_content"));
            ts.setNote(request.getParameter("note"));

            boolean success = tsDAO.addTroubleshooting(ts);
            if (success) {
                session.setAttribute("message", "트러블 슈팅이 성공적으로 등록되었습니다.");
            } else {
                session.setAttribute("error", "트러블 슈팅 등록 중 오류가 발생했습니다.");
            }
            response.sendRedirect("troubleshooting?view=list");

        } else if ("update".equals(actionType)) {
            // 트러블 슈팅 수정
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                TroubleshootingDTO ts = new TroubleshootingDTO();
                ts.setId(id);
                ts.setTitle(request.getParameter("title"));
                ts.setCustomerName(request.getParameter("customer_name"));
                ts.setCustomerManager(request.getParameter("customer_manager"));
                ts.setOccurrenceDate(parseDate(request.getParameter("occurrence_date")));
                ts.setWorkPersonnel(request.getParameter("work_personnel"));
                ts.setWorkPeriod(request.getParameter("work_period"));
                ts.setSupportType(request.getParameter("support_type"));
                ts.setCaseOpenYn(request.getParameter("case_open_yn"));
                ts.setOverview(request.getParameter("overview"));
                ts.setCauseAnalysis(request.getParameter("cause_analysis"));
                ts.setErrorContent(request.getParameter("error_content"));
                ts.setActionTaken(request.getParameter("action_taken"));
                ts.setScriptContent(request.getParameter("script_content"));
                ts.setNote(request.getParameter("note"));

                boolean success = tsDAO.updateTroubleshooting(ts);
                if (success) {
                    session.setAttribute("message", "트러블 슈팅이 성공적으로 수정되었습니다.");
                    response.sendRedirect("troubleshooting?view=view&id=" + id);
                } else {
                    session.setAttribute("error", "트러블 슈팅 수정 중 오류가 발생했습니다.");
                    response.sendRedirect("troubleshooting?view=edit&id=" + id);
                }
            } catch (NumberFormatException e) {
                session.setAttribute("error", "잘못된 ID 형식입니다.");
                response.sendRedirect("troubleshooting?view=list");
            }

        } else if ("delete".equals(actionType)) {
            // 트러블 슈팅 삭제
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                boolean success = tsDAO.deleteTroubleshooting(id);
                if (success) {
                    session.setAttribute("message", "트러블 슈팅이 성공적으로 삭제되었습니다.");
                } else {
                    session.setAttribute("error", "트러블 슈팅 삭제 중 오류가 발생했습니다.");
                }
            } catch (NumberFormatException e) {
                session.setAttribute("error", "잘못된 ID 형식입니다.");
            }
            response.sendRedirect("troubleshooting?view=list");

        } else {
            response.sendRedirect("troubleshooting?view=list");
        }
    }

    // 날짜 문자열을 Date 객체로 변환
    private Date parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(dateString.trim());
        } catch (ParseException e) {
            return null;
        }
    }
}