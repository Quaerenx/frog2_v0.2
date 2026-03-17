package com.company.controller;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.company.model.CustomerDAO;
import com.company.model.CustomerDTO;
import com.company.model.MaintenanceRecordDAO;
import com.company.model.MaintenanceRecordDTO;
import com.company.model.UserDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// @WebServlet("/maintenance") - web.xml에서 매핑하므로 주석 처리
public class MaintenanceServlet extends HttpServlet {
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

        // 뷰 타입 확인
        String viewType = request.getParameter("view");
        if (viewType == null || viewType.isEmpty()) {
            viewType = "cards";
        }

        MaintenanceRecordDAO maintenanceDAO = new MaintenanceRecordDAO();
        CustomerDAO customerDAO = new CustomerDAO();

        if ("cards".equals(viewType)) {
            // 담당자별 고객사 카드 목록 표시 (라이선스 요약 제거)
            Map<String, List<CustomerDTO>> inspectorCustomers = getInspectorCustomersMap();
            request.setAttribute("inspectorCustomers", inspectorCustomers);
            request.setAttribute("viewType", "cards");
            request.getRequestDispatcher("/maintenance/maintenance_cards.jsp").forward(request, response);

        } else if ("history".equals(viewType)) {
            // 특정 고객사의 정기점검 이력 목록
            String customerName = request.getParameter("customerName");
            if (customerName != null && !customerName.isEmpty()) {
                // 해당 고객사의 정기점검 이력 조회
                List<MaintenanceRecordDTO> records = maintenanceDAO.getMaintenanceRecordsByCustomer(customerName);
                // 라이선스 사용률 시리즈
                List<java.util.Map<String, Object>> usageSeries = maintenanceDAO.getLicenseUsageSeries(customerName);

                // 각 이력에 대한 라이선스 요약(TB) 문자열 생성
                Map<Long, String> licenseSummaries = new LinkedHashMap<>();
                for (MaintenanceRecordDTO rec : records) {
                    String summary = buildLicenseSummaryTB(rec);
                    if (summary != null) {
                        licenseSummaries.put(rec.getMaintenanceId(), summary);
                    }
                }

                // 고객사 기본 정보 조회
                CustomerDTO customer = customerDAO.getCustomerByName(customerName);

                request.setAttribute("records", records);
                request.setAttribute("usageSeries", usageSeries);
                request.setAttribute("licenseSummaries", licenseSummaries);
                request.setAttribute("customer", customer);
                request.setAttribute("customerName", customerName);
                request.setAttribute("viewType", "history");
                request.getRequestDispatcher("/maintenance/maintenance_history.jsp").forward(request, response);
            } else {
                response.sendRedirect("maintenance?view=cards");
            }

        } else if ("add".equals(viewType)) {
            // 새 정기점검 이력 추가 폼
            String customerName = request.getParameter("customerName");
            request.setAttribute("customerName", customerName);
            request.setAttribute("viewType", "add");
            request.getRequestDispatcher("/maintenance/maintenance_add.jsp").forward(request, response);

        } else if ("edit".equals(viewType)) {
            // 정기점검 이력 수정 폼
            String maintenanceIdStr = request.getParameter("id");
            if (maintenanceIdStr != null && !maintenanceIdStr.isEmpty()) {
                try {
                    Long maintenanceId = Long.parseLong(maintenanceIdStr);
                    MaintenanceRecordDTO record = maintenanceDAO.getMaintenanceRecordById(maintenanceId);
                    if (record != null) {
                        request.setAttribute("record", record);
                        request.setAttribute("viewType", "edit");
                        request.getRequestDispatcher("/maintenance/maintenance_edit.jsp").forward(request, response);
                    } else {
                        session.setAttribute("error", "해당 정기점검 이력을 찾을 수 없습니다.");
                        response.sendRedirect("maintenance?view=cards");
                    }
                } catch (NumberFormatException e) {
                    session.setAttribute("error", "잘못된 요청입니다.");
                    response.sendRedirect("maintenance?view=cards");
                }
            } else {
                response.sendRedirect("maintenance?view=cards");
            }

        } else {
            response.sendRedirect("maintenance?view=cards");
        }
    }

 // 담당자별 고객사 목록을 Map으로 구성 (정기점검 계약 고객사이면서 활성 상태인 것만)
    private Map<String, List<CustomerDTO>> getInspectorCustomersMap() {
        Map<String, List<CustomerDTO>> inspectorCustomers = new LinkedHashMap<>();
        CustomerDAO customerDAO = new CustomerDAO();

        try {
            // getAllCustomers 메소드가 이미 is_deleted = 1 조건을 포함하고 있음
            List<CustomerDTO> allCustomers = customerDAO.getAllCustomers("manager_name", "ASC");

            for (CustomerDTO customer : allCustomers) {
                String mainManager = customer.getManagerName();
                String customerType = customer.getCustomerType();

                // 정기점검 계약 고객사이고 담당자가 있는 경우만 포함
                // (is_deleted = 1 조건은 이미 getAllCustomers에서 필터링됨)
                if (mainManager != null && !mainManager.trim().isEmpty() &&
                    "정기점검 계약 고객사".equals(customerType)) {
                    inspectorCustomers.computeIfAbsent(mainManager.trim(), k -> new ArrayList<>()).add(customer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inspectorCustomers;
    }

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 세션 확인
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        String actionType = request.getParameter("action");
        MaintenanceRecordDAO maintenanceDAO = new MaintenanceRecordDAO();

        if ("add".equals(actionType)) {
            // 새 정기점검 이력 추가
            MaintenanceRecordDTO record = new MaintenanceRecordDTO();
            record.setCustomerName(request.getParameter("customer_name"));
            record.setInspectorName(request.getParameter("inspector_name"));
            record.setInspectionDate(parseDate(request.getParameter("inspection_date")));
            record.setVerticaVersion(request.getParameter("vertica_version"));
            record.setNote(request.getParameter("note"));
            // 문자열로 그대로 수집
            record.setLicenseSizeGb(trimToNull(request.getParameter("license_size_gb")));
            record.setLicenseUsageSize(trimToNull(request.getParameter("license_usage_size")));
            record.setLicenseUsagePct(trimToNull(request.getParameter("license_usage_pct")));

            boolean success = maintenanceDAO.addMaintenanceRecord(record);
            if (success) {
                session.setAttribute("message", "정기점검 이력이 성공적으로 추가되었습니다.");
            } else {
                session.setAttribute("error", "정기점검 이력 추가 중 오류가 발생했습니다.");
            }

            String customerName = record.getCustomerName();
            String encodedName = java.net.URLEncoder.encode(customerName, "UTF-8");
            response.sendRedirect("maintenance?view=history&customerName=" + encodedName);

        } else if ("update".equals(actionType)) {
            // 정기점검 이력 수정
            String maintenanceIdStr = request.getParameter("maintenance_id");
            if (maintenanceIdStr != null && !maintenanceIdStr.isEmpty()) {
                try {
                    MaintenanceRecordDTO record = new MaintenanceRecordDTO();
                    record.setMaintenanceId(Long.parseLong(maintenanceIdStr));
                    record.setCustomerName(request.getParameter("customer_name"));
                    record.setInspectorName(request.getParameter("inspector_name"));
                    record.setInspectionDate(parseDate(request.getParameter("inspection_date")));
                    record.setVerticaVersion(request.getParameter("vertica_version"));
                    record.setNote(request.getParameter("note"));
                    // 문자열로 그대로 수집
                    record.setLicenseSizeGb(trimToNull(request.getParameter("license_size_gb")));
                    record.setLicenseUsageSize(trimToNull(request.getParameter("license_usage_size")));
                    record.setLicenseUsagePct(trimToNull(request.getParameter("license_usage_pct")));

                    boolean success = maintenanceDAO.updateMaintenanceRecord(record);
                    if (success) {
                        session.setAttribute("message", "정기점검 이력이 성공적으로 수정되었습니다.");
                    } else {
                        session.setAttribute("error", "정기점검 이력 수정 중 오류가 발생했습니다.");
                    }

                    String customerName = record.getCustomerName();
                    String encodedName = java.net.URLEncoder.encode(customerName, "UTF-8");
                    response.sendRedirect("maintenance?view=history&customerName=" + encodedName);

                } catch (NumberFormatException e) {
                    session.setAttribute("error", "잘못된 요청입니다.");
                    response.sendRedirect("maintenance?view=cards");
                }
            }

        } else if ("delete".equals(actionType)) {
            // 정기점검 이력 삭제
            String maintenanceIdStr = request.getParameter("maintenance_id");
            String customerName = request.getParameter("customer_name");

            if (maintenanceIdStr != null && !maintenanceIdStr.isEmpty()) {
                try {
                    Long maintenanceId = Long.parseLong(maintenanceIdStr);
                    boolean success = maintenanceDAO.deleteMaintenanceRecord(maintenanceId);
                    if (success) {
                        session.setAttribute("message", "정기점검 이력이 성공적으로 삭제되었습니다.");
                    } else {
                        session.setAttribute("error", "정기점검 이력 삭제 중 오류가 발생했습니다.");
                    }
                } catch (NumberFormatException e) {
                    session.setAttribute("error", "잘못된 요청입니다.");
                }
            }

            if (customerName != null && !customerName.isEmpty()) {
                String encodedName = java.net.URLEncoder.encode(customerName, "UTF-8");
                response.sendRedirect("maintenance?view=history&customerName=" + encodedName);
            } else {
                response.sendRedirect("maintenance?view=cards");
            }

        } else {
            response.sendRedirect("maintenance?view=cards");
        }
    }

    // 날짜 문자열을 Date 객체로 변환
    private Date parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = sdf.parse(dateString.trim());
            return new Date(utilDate.getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    private String trimToNull(String v) {
        if (v == null) return null;
        String t = v.trim();
        return t.isEmpty() ? null : t;
    }

    // 라이선스 요약 문자열(TB) 생성: "{size}TB 중 {usage}TB 총 {pct}% 사용 중"
    private String buildLicenseSummaryTB(MaintenanceRecordDTO rec) {
        if (rec == null) return null;
        Double sizeGb = parseToGb(rec.getLicenseSizeGb());
        Double usageGb = parseToGb(rec.getLicenseUsageSize());
        Double pct = parseNumber(rec.getLicenseUsagePct());

        Double sizeTb = (sizeGb != null ? sizeGb / 1024.0 : null);
        Double usageTb = (usageGb != null ? usageGb / 1024.0 : null);

        // 퍼센트가 없고, 사이즈/사용량이 있으면 계산하여 채움
        if (pct == null && sizeGb != null && usageGb != null && sizeGb > 0) {
            pct = (usageGb / sizeGb) * 100.0;
        }

        // 아무 값도 없으면 표시하지 않음
        if (sizeTb == null && usageTb == null && pct == null) {
            return null;
        }

        String sizeStr = (sizeTb != null ? format2(sizeTb) + "TB" : null);
        String usageStr = (usageTb != null ? format2(usageTb) + "TB" : null);
        String pctStr = (pct != null ? String.valueOf(Math.round(pct)) + "%" : null);

        StringBuilder sb = new StringBuilder();
        if (sizeStr != null) sb.append(sizeStr); else sb.append("-");
        sb.append(" 중 ");
        if (usageStr != null) sb.append(usageStr); else sb.append("-");
        sb.append(" 총 ");
        if (pctStr != null) sb.append(pctStr); else sb.append("-");
        sb.append(" 사용 중");
        return sb.toString();
    }

    // 문자열 값에서 TB/GB 단위를 인지하여 GB로 환산해 반환
    private Double parseToGb(String s) {
        if (s == null) return null;
        String raw = s.trim();
        if (raw.isEmpty()) return null;
        String lower = raw.toLowerCase();
        boolean isTb = lower.contains("tb");
        boolean isGb = lower.contains("gb");
        Double n = parseNumber(raw);
        if (n == null) return null;
        if (isTb || (!isGb && !isTb)) {
            // TB 명시 또는 단위 미표시(기본 TB)인 경우 GB로 환산
            return n * 1024.0;
        }
        // GB 명시 시 그대로 GB로 처리
        return n;
    }

    private Double parseNumber(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;
        // 숫자, 소수점, 마이너스만 남김. 콤마 제거.
        t = t.replace(",", "");
        t = t.replaceAll("[^0-9.\\-]", "");
        if (t.isEmpty() || t.equals("-") || t.equals(".")) return null;
        try {
            return Double.parseDouble(t);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String format2(double v) {
        return String.format(java.util.Locale.US, "%.2f", v);
    }
}
