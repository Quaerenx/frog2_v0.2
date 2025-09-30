package com.company.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.company.model.CustomerDAO;
import com.company.model.CustomerDTO;
import com.company.model.CustomerDetailDAO;
import com.company.model.VerticaEosDAO;
import com.company.model.CustomerDetailDTO;
import com.company.model.UserDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// @WebServlet("/customers") - web.xml에서 매핑하므로 주석 처리
public class CustomersServlet extends HttpServlet {
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

        // 액션 파라미터 확인 (AJAX 요청 처리)
        String action = request.getParameter("action");
        if ("getDetail".equals(action)) {
            handleGetDetail(request, response);
            return;
        } else if ("getCustomersForMaintenance".equals(action)) {
            handleGetCustomersForMaintenance(request, response);
            return;
        }

        // 기존 GET 처리 로직
        String viewType = request.getParameter("view");
        if (viewType == null || viewType.isEmpty()) {
            viewType = "list";
        }

        String sortField = request.getParameter("sortField");
        String sortDirection = request.getParameter("sortDirection");
        String filter = request.getParameter("filter");

        if (sortField == null) {
            sortField = "";
        }
        if (sortDirection == null) {
            sortDirection = "ASC";
        }
        // 기본값은 정기점검 고객사만 보기
        if (filter == null || filter.isEmpty()) {
            filter = "maintenance";
        }

        CustomerDAO customerDAO = new CustomerDAO();

        if ("list".equals(viewType)) {
            List<CustomerDTO> customerList = customerDAO.getAllCustomers(sortField, sortDirection, filter);
            if (customerList == null) {
                customerList = new java.util.ArrayList<>();
            }

            // 필터별 개수 조회
            int currentCount = customerDAO.getCustomerCount(filter);
            int totalCount = customerDAO.getCustomerCount("all");
            int maintenanceCount = customerDAO.getCustomerCount("maintenance");

            request.setAttribute("customerList", customerList);
            request.setAttribute("sortField", sortField);
            request.setAttribute("sortDirection", sortDirection);
            request.setAttribute("filter", filter);
            request.setAttribute("currentCount", currentCount);
            request.setAttribute("totalCount", totalCount);
            request.setAttribute("maintenanceCount", maintenanceCount);
            request.setAttribute("viewType", "list");
            request.getRequestDispatcher("/customers/customers_list.jsp").forward(request, response);
        } else if ("detail".equals(viewType)) {
            // 파라미터명을 customerName으로 통일
            String customerName = request.getParameter("customerName");
            if (customerName != null && !customerName.isEmpty()) {
                try {
                    // URL 디코딩 추가
                    customerName = java.net.URLDecoder.decode(customerName, "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 기본 고객사 정보 조회
                CustomerDTO customer = customerDAO.getCustomerByName(customerName);
                request.setAttribute("customer", customer);

                // 상세정보 조회
                CustomerDetailDAO detailDAO = new CustomerDetailDAO();
                CustomerDetailDTO customerDetail = detailDAO.getCustomerDetail(customerName);

                // Vertica EOS 조회: 상세정보의 버전 우선, 없으면 기본 고객 테이블의 버전으로 조회
                String versionText = null;
                if (customerDetail != null && customerDetail.getVerticaVersion() != null && !customerDetail.getVerticaVersion().trim().isEmpty()) {
                    versionText = customerDetail.getVerticaVersion();
                } else if (customer != null && customer.getVerticaVersion() != null && !customer.getVerticaVersion().trim().isEmpty()) {
                    versionText = customer.getVerticaVersion();
                }
                if (versionText != null) {
                    VerticaEosDAO eosDAO = new VerticaEosDAO();
                    java.util.Date eosDate = eosDAO.findEosDateByVersion(versionText);
                    if (eosDate != null) {
                        request.setAttribute("verticaEosDate", eosDate);
                    }
                }

                request.setAttribute("customerDetail", customerDetail);

                request.setAttribute("viewType", "detail");
                request.getRequestDispatcher("/customers/customers_detail.jsp").forward(request, response);
            } else {
                response.sendRedirect("customers?view=list");
            }
        } else if ("edit".equals(viewType)) {
            String customerName = request.getParameter("name");
            if (customerName != null && !customerName.isEmpty()) {
                try {
                    // URL 디코딩 추가
                    customerName = java.net.URLDecoder.decode(customerName, "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                CustomerDTO customer = customerDAO.getCustomerByName(customerName);
                request.setAttribute("customer", customer);
                request.setAttribute("viewType", "edit");
                request.getRequestDispatcher("/customers/customers_edit.jsp").forward(request, response);
            } else {
                response.sendRedirect("customers?view=list");
            }
        } else if ("support".equals(viewType)) {
            request.setAttribute("viewType", "support");
            request.getRequestDispatcher("/customers/support.jsp").forward(request, response);
        } else if ("editDetail".equals(viewType)) {
            String customerName = request.getParameter("customerName");
            if (customerName != null && !customerName.isEmpty()) {
                try {
                    // URL 디코딩 추가
                    customerName = java.net.URLDecoder.decode(customerName, "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 기본 고객사 정보 조회
                CustomerDTO customer = customerDAO.getCustomerByName(customerName);
                request.setAttribute("customer", customer);

                // 상세정보 조회
                CustomerDetailDAO detailDAO = new CustomerDetailDAO();
                CustomerDetailDTO customerDetail = detailDAO.getCustomerDetail(customerName);
                request.setAttribute("customerDetail", customerDetail);

                request.setAttribute("viewType", "editDetail");
                request.getRequestDispatcher("/customers/customers_detail_edit.jsp").forward(request, response);
            } else {
                response.sendRedirect("customers?view=list");
            }
        } else if ("add".equals(viewType)) {
            // 새 고객사 추가 폼 표시
            request.setAttribute("viewType", "add");
            request.getRequestDispatcher("/customers/customers_add.jsp").forward(request, response);
        } else {
            response.sendRedirect("customers?view=list");
        }
    }

    // 고객 상세정보 조회 (AJAX) - Gson 없이 JSON 생성
    private void handleGetDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String customerName = request.getParameter("customerName");

        // URL 디코딩 추가
        if (customerName != null && !customerName.isEmpty()) {
            try {
                customerName = java.net.URLDecoder.decode(customerName, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            CustomerDetailDAO detailDAO = new CustomerDetailDAO();
            CustomerDetailDTO detail = detailDAO.getCustomerDetail(customerName);

            if (detail != null) {
                // JSON 수동 생성
                StringBuilder json = new StringBuilder();
                json.append("{");
                json.append("\"customerName\":").append(jsonString(detail.getCustomerName())).append(",");
                json.append("\"systemName\":").append(jsonString(detail.getSystemName())).append(",");
                json.append("\"customerManager\":").append(jsonString(detail.getCustomerManager())).append(",");
                json.append("\"siCompany\":").append(jsonString(detail.getSiCompany())).append(",");
                json.append("\"siManager\":").append(jsonString(detail.getSiManager())).append(",");
                json.append("\"creator\":").append(jsonString(detail.getCreator())).append(",");
                json.append("\"createDate\":").append(jsonString(formatDate(detail.getCreateDate()))).append(",");
                json.append("\"mainManager\":").append(jsonString(detail.getMainManager())).append(",");
                json.append("\"subManager\":").append(jsonString(detail.getSubManager())).append(",");
                json.append("\"installDate\":").append(jsonString(formatDate(detail.getInstallDate()))).append(",");
                json.append("\"introductionYear\":").append(jsonString(detail.getIntroductionYear())).append(",");

                // Vertica 정보
                json.append("\"dbName\":").append(jsonString(detail.getDbName())).append(",");
                json.append("\"dbMode\":").append(jsonString(detail.getDbMode())).append(",");
                json.append("\"verticaVersion\":").append(jsonString(detail.getVerticaVersion())).append(",");
                json.append("\"licenseInfo\":").append(jsonString(detail.getLicenseInfo())).append(",");
                json.append("\"said\":").append(jsonString(detail.getSaid())).append(",");
                json.append("\"nodeCount\":").append(jsonString(detail.getNodeCount())).append(",");
                json.append("\"verticaAdmin\":").append(jsonString(detail.getVerticaAdmin())).append(",");
                json.append("\"subclusterYn\":").append(jsonString(detail.getSubclusterYn())).append(",");
                json.append("\"mcYn\":").append(jsonString(detail.getMcYn())).append(",");
                json.append("\"mcHost\":").append(jsonString(detail.getMcHost())).append(",");
                json.append("\"mcVersion\":").append(jsonString(detail.getMcVersion())).append(",");
                json.append("\"mcAdmin\":").append(jsonString(detail.getMcAdmin())).append(",");
                json.append("\"backupYn\":").append(jsonString(detail.getBackupYn())).append(",");
                json.append("\"customResourcePoolYn\":").append(jsonString(detail.getCustomResourcePoolYn())).append(",");
                json.append("\"backupNote\":").append(jsonString(detail.getBackupNote())).append(",");

                // 환경 정보
                json.append("\"osInfo\":").append(jsonString(detail.getOsInfo())).append(",");
                json.append("\"memoryInfo\":").append(jsonString(detail.getMemoryInfo())).append(",");
                json.append("\"infraType\":").append(jsonString(detail.getInfraType())).append(",");
                json.append("\"cpuSocket\":").append(jsonString(detail.getCpuSocket())).append(",");
                json.append("\"hyperThreading\":").append(jsonString(detail.getHyperThreading())).append(",");
                json.append("\"cpuCore\":").append(jsonString(detail.getCpuCore())).append(",");
                json.append("\"dataArea\":").append(jsonString(detail.getDataArea())).append(",");
                json.append("\"depotArea\":").append(jsonString(detail.getDepotArea())).append(",");
                json.append("\"catalogArea\":").append(jsonString(detail.getCatalogArea())).append(",");
                json.append("\"objectArea\":").append(jsonString(detail.getObjectArea())).append(",");
                json.append("\"publicYn\":").append(jsonString(detail.getPublicYn())).append(",");
                json.append("\"publicNetwork\":").append(jsonString(detail.getPublicNetwork())).append(",");
                json.append("\"privateYn\":").append(jsonString(detail.getPrivateYn())).append(",");
                json.append("\"privateNetwork\":").append(jsonString(detail.getPrivateNetwork())).append(",");
                json.append("\"storageYn\":").append(jsonString(detail.getStorageYn())).append(",");
                json.append("\"storageNetwork\":").append(jsonString(detail.getStorageNetwork())).append(",");

                // 외부 솔루션
                json.append("\"etlTool\":").append(jsonString(detail.getEtlTool())).append(",");
                json.append("\"biTool\":").append(jsonString(detail.getBiTool())).append(",");
                json.append("\"dbEncryption\":").append(jsonString(detail.getDbEncryption())).append(",");
                json.append("\"cdcTool\":").append(jsonString(detail.getCdcTool())).append(",");

                // 기타
                json.append("\"eosDate\":").append(jsonString(formatDate(detail.getEosDate()))).append(",");
                json.append("\"customerType\":").append(jsonString(detail.getCustomerType())).append(",");
                json.append("\"note\":").append(jsonString(detail.getNote()));

                json.append("}");

                out.print(json.toString());
            } else {
                // 상세정보가 없으면 기본 테이블 데이터로 응답
                CustomerDAO customerDAO = new CustomerDAO();
                CustomerDTO customer = customerDAO.getCustomerByName(customerName);

                StringBuilder basicJson = new StringBuilder();
                basicJson.append("{");
                if (customer != null) {
                    basicJson.append("\"customerName\":").append(jsonString(customer.getCustomerName())).append(",");
                    basicJson.append("\"introductionYear\":").append(jsonString(customer.getFirstIntroductionYear())).append(",");
                    basicJson.append("\"dbName\":").append(jsonString(customer.getDbName())).append(",");
                    basicJson.append("\"verticaVersion\":").append(jsonString(customer.getVerticaVersion())).append(",");
                    basicJson.append("\"eosDate\":").append(jsonString(customer.getVerticaEos())).append(",");
                    basicJson.append("\"dbMode\":").append(jsonString(customer.getMode())).append(",");
                    basicJson.append("\"osInfo\":").append(jsonString(customer.getOs())).append(",");
                    basicJson.append("\"nodeCount\":").append(jsonString(customer.getNodes())).append(",");
                    basicJson.append("\"licenseInfo\":").append(jsonString(customer.getLicenseSize())).append(",");
                    basicJson.append("\"mainManager\":").append(jsonString(customer.getManagerName())).append(",");
                    basicJson.append("\"subManager\":").append(jsonString(customer.getSubManagerName())).append(",");
                    basicJson.append("\"said\":").append(jsonString(customer.getSaid())).append(",");
                    basicJson.append("\"storageYn\":").append(jsonString(customer.getOsStorageConfig())).append(",");
                    basicJson.append("\"backupNote\":").append(jsonString(customer.getBackupConfig())).append(",");
                    basicJson.append("\"customerType\":").append(jsonString(customer.getCustomerType())).append(",");
                    basicJson.append("\"etlTool\":").append(jsonString(customer.getEtlTool())).append(",");
                    basicJson.append("\"biTool\":").append(jsonString(customer.getBiTool())).append(",");
                    basicJson.append("\"dbEncryption\":").append(jsonString(customer.getDbEncryption())).append(",");
                    basicJson.append("\"cdcTool\":").append(jsonString(customer.getCdcTool())).append(",");
                    basicJson.append("\"note\":").append(jsonString(customer.getNote()));

                }
                basicJson.append("}");

                out.print(basicJson.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\":\"상세정보를 불러오는 중 오류가 발생했습니다.\"}");
        }

        out.flush();
    }

 // 정기점검용 고객사 및 담당자 목록 조회 (AJAX) - 활성 상태 고객사만
    private void handleGetCustomersForMaintenance(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            CustomerDAO customerDAO = new CustomerDAO();
            // getAllCustomers 메소드가 이미 is_deleted = 1 조건을 포함하고 있음
            List<CustomerDTO> allCustomers = customerDAO.getAllCustomers("", "ASC");

            // 고객사 목록 추출
            Set<String> customerNames = new LinkedHashSet<>();
            Set<String> inspectorNames = new LinkedHashSet<>();

            for (CustomerDTO customer : allCustomers) {
                if (customer.getCustomerName() != null && !customer.getCustomerName().trim().isEmpty()) {
                    customerNames.add(customer.getCustomerName().trim());
                }
                if (customer.getManagerName() != null && !customer.getManagerName().trim().isEmpty()) {
                    inspectorNames.add(customer.getManagerName().trim());
                }
                if (customer.getSubManagerName() != null && !customer.getSubManagerName().trim().isEmpty()) {
                    inspectorNames.add(customer.getSubManagerName().trim());
                }
            }

            // JSON 수동 생성
            StringBuilder json = new StringBuilder();
            json.append("{");

            // 고객사 목록
            json.append("\"customers\":[");
            boolean first = true;
            for (String customerName : customerNames) {
                if (!first) {
					json.append(",");
				}
                json.append(jsonString(customerName));
                first = false;
            }
            json.append("],");

            // 담당자 목록
            json.append("\"inspectors\":[");
            first = true;
            for (String inspectorName : inspectorNames) {
                if (!first) {
					json.append(",");
				}
                json.append(jsonString(inspectorName));
                first = false;
            }
            json.append("]");

            json.append("}");

            out.print(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\":\"데이터를 불러오는 중 오류가 발생했습니다.\"}");
        }

        out.flush();
    }

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("=== doPost 시작 ===");

        HttpSession session = request.getSession(false);
        System.out.println("Session: " + session);
        System.out.println("User in session: " + (session != null ? session.getAttribute("user") : "null"));

        if (session == null || session.getAttribute("user") == null) {
            System.out.println("=== 세션 없음, 로그인으로 리다이렉션 ===");
            response.sendRedirect("login");
            return;
        }

        String actionType = request.getParameter("action");
        System.out.println("Action Type: " + actionType);

        // 상세정보 저장 처리
        if ("saveDetail".equals(actionType)) {
            System.out.println("=== saveDetail 처리 시작 ===");
            handleSaveDetail(request, response);
            return;
        }

        // 기존 POST 처리 로직
        CustomerDAO customerDAO = new CustomerDAO();

        if ("update".equals(actionType)) {
            CustomerDTO customer = new CustomerDTO();
            customer.setCustomerName(request.getParameter("customer_name"));
            customer.setFirstIntroductionYear(request.getParameter("first_introduction_year"));
            customer.setDbName(request.getParameter("db_name"));
            customer.setVerticaVersion(request.getParameter("vertica_version"));
            customer.setVerticaEos(request.getParameter("vertica_eos"));
            customer.setMode(request.getParameter("mode"));
            customer.setOs(request.getParameter("os"));
            customer.setNodes(request.getParameter("nodes"));
            customer.setLicenseSize(request.getParameter("license_size"));
            customer.setManagerName(request.getParameter("manager_name"));
            customer.setSubManagerName(request.getParameter("sub_manager_name"));
            customer.setSaid(request.getParameter("said"));
            customer.setNote(request.getParameter("note"));
            customer.setOsStorageConfig(request.getParameter("os_storage_config"));
            customer.setBackupConfig(request.getParameter("backup_config"));
            customer.setBiTool(request.getParameter("bi_tool"));
            customer.setEtlTool(request.getParameter("etl_tool"));
            customer.setDbEncryption(request.getParameter("db_encryption"));
            customer.setCdcTool(request.getParameter("cdc_tool"));
            customer.setCustomerType(request.getParameter("customer_type"));

            boolean success = customerDAO.updateCustomer(customer);
            if (success) {
                session.setAttribute("message", "고객사 정보가 성공적으로 업데이트되었습니다.");
            } else {
                session.setAttribute("error", "고객사 정보 업데이트 중 오류가 발생했습니다.");
            }
            response.sendRedirect("customers?view=list");
        } else if ("add".equals(actionType)) {
            CustomerDTO customer = new CustomerDTO();
            customer.setCustomerName(request.getParameter("customer_name"));
            customer.setFirstIntroductionYear(request.getParameter("first_introduction_year"));
            customer.setDbName(request.getParameter("db_name"));
            customer.setVerticaVersion(request.getParameter("vertica_version"));
            customer.setVerticaEos(request.getParameter("vertica_eos"));
            customer.setMode(request.getParameter("mode"));
            customer.setOs(request.getParameter("os"));
            customer.setNodes(request.getParameter("nodes"));
            customer.setLicenseSize(request.getParameter("license_size"));
            customer.setManagerName(request.getParameter("manager_name"));
            customer.setSubManagerName(request.getParameter("sub_manager_name"));
            customer.setSaid(request.getParameter("said"));
            customer.setNote(request.getParameter("note"));
            customer.setOsStorageConfig(request.getParameter("os_storage_config"));
            customer.setBackupConfig(request.getParameter("backup_config"));
            customer.setBiTool(request.getParameter("bi_tool"));
            customer.setEtlTool(request.getParameter("etl_tool"));
            customer.setDbEncryption(request.getParameter("db_encryption"));
            customer.setCdcTool(request.getParameter("cdc_tool"));
            customer.setCustomerType(request.getParameter("customer_type"));

            boolean success = customerDAO.addCustomer(customer);
            if (success) {
                session.setAttribute("message", "새 고객사가 성공적으로 추가되었습니다.");
            } else {
                session.setAttribute("error", "고객사 추가 중 오류가 발생했습니다.");
            }
            response.sendRedirect("customers?view=list");
        } else if ("delete".equals(actionType)) {
            String customerName = request.getParameter("customer_name");
            boolean success = customerDAO.deleteCustomer(customerName);
            if (success) {
                session.setAttribute("message", "고객사가 성공적으로 삭제되었습니다.");
            } else {
                session.setAttribute("error", "고객사 삭제 중 오류가 발생했습니다.");
            }
            response.sendRedirect("customers?view=list");
        } else {
            System.out.println("=== 알 수 없는 action, 목록으로 리다이렉션: " + actionType + " ===");
            response.sendRedirect("customers?view=list");
        }
    }

    // 상세정보 저장 처리 (AJAX)
    private void handleSaveDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("=== handleSaveDetail 시작 ===");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();


        try {
            CustomerDetailDTO detail = new CustomerDetailDTO();

            // 파라미터 로그 출력 (몇 개만 테스트)
            String customerName = getStringParam(request, "customerName");
            String subclusterYn = getStringParam(request, "subclusterYn");

            System.out.println("Customer Name: [" + customerName + "]");
            System.out.println("Subcluster YN: [" + subclusterYn + "]");

            // 기본적인 파라미터 받기 (빈 문자열을 null로 처리하는 도우미 메서드 사용)
            detail.setCustomerName(customerName);
            detail.setSystemName(request.getParameter("systemName"));
            detail.setCustomerManager(getStringParam(request, "customerManager"));
            detail.setSiCompany(getStringParam(request, "siCompany"));
            detail.setSiManager(getStringParam(request, "siManager"));
            detail.setCreator(getStringParam(request, "creator"));
            detail.setCreateDate(parseDate(request.getParameter("createDate")));
            detail.setMainManager(getStringParam(request, "mainManager"));
            detail.setSubManager(getStringParam(request, "subManager"));
            detail.setInstallDate(parseDate(request.getParameter("installDate")));
            detail.setIntroductionYear(getStringParam(request, "introductionYear"));

            // Vertica 정보
            detail.setDbName(getStringParam(request, "dbName"));
            detail.setDbMode(getStringParam(request, "dbMode"));
            detail.setVerticaVersion(getStringParam(request, "verticaVersion"));
            detail.setLicenseInfo(getStringParam(request, "licenseInfo"));
            detail.setSaid(getStringParam(request, "said"));
            detail.setNodeCount(getStringParam(request, "nodeCount"));
            detail.setVerticaAdmin(getStringParam(request, "verticaAdmin"));
            detail.setSubclusterYn(subclusterYn);
            detail.setMcYn(getStringParam(request, "mcYn"));
            detail.setMcHost(getStringParam(request, "mcHost"));
            detail.setMcVersion(getStringParam(request, "mcVersion"));
            detail.setMcAdmin(getStringParam(request, "mcAdmin"));
            detail.setBackupYn(getStringParam(request, "backupYn"));
            detail.setCustomResourcePoolYn(getStringParam(request, "customResourcePoolYn"));
            detail.setBackupNote(getStringParam(request, "backupNote"));

            // 환경 정보
            detail.setOsInfo(getStringParam(request, "osInfo"));
            detail.setMemoryInfo(getStringParam(request, "memoryInfo"));
            detail.setInfraType(getStringParam(request, "infraType"));
            detail.setCpuSocket(getStringParam(request, "cpuSocket"));
            detail.setHyperThreading(getStringParam(request, "hyperThreading"));
            detail.setCpuCore(getStringParam(request, "cpuCore"));
            detail.setDataArea(getStringParam(request, "dataArea"));
            detail.setDepotArea(getStringParam(request, "depotArea"));
            detail.setCatalogArea(getStringParam(request, "catalogArea"));
            detail.setObjectArea(getStringParam(request, "objectArea"));
            detail.setPublicYn(getStringParam(request, "publicYn"));
            detail.setPublicNetwork(getStringParam(request, "publicNetwork"));
            detail.setPrivateYn(getStringParam(request, "privateYn"));
            detail.setPrivateNetwork(getStringParam(request, "privateNetwork"));
            detail.setStorageYn(getStringParam(request, "storageYn"));
            detail.setStorageNetwork(getStringParam(request, "storageNetwork"));

            // 외부 솔루션
            detail.setEtlTool(getStringParam(request, "etlTool"));
            detail.setBiTool(getStringParam(request, "biTool"));
            detail.setDbEncryption(getStringParam(request, "dbEncryption"));
            detail.setCdcTool(getStringParam(request, "cdcTool"));

            // 기타
            detail.setEosDate(parseDate(request.getParameter("eosDate")));
            detail.setCustomerType(getStringParam(request, "customerType"));
            detail.setNote(getStringParam(request, "note"));

            System.out.println("=== DAO 호출 전 ===");
            CustomerDetailDAO detailDAO = new CustomerDetailDAO();
            boolean success = detailDAO.saveOrUpdateCustomerDetail(detail);
            System.out.println("=== DAO 호출 후, 결과: " + success + " ===");

            if (success) {
                session.setAttribute("message", "상세정보가 성공적으로 저장되었습니다.");
            } else {
                session.setAttribute("error", "상세정보 저장 중 오류가 발생했습니다.");
            }

            String encodedName = java.net.URLEncoder.encode(detail.getCustomerName(), "UTF-8");
            response.sendRedirect("customers?view=detail&customerName=" + encodedName);

        } catch (Exception e) {
            System.out.println("=== 예외 발생 ===");
            e.printStackTrace(); // 이게 중요!
            out.print("{\"success\":false,\"message\":\"저장 중 오류가 발생했습니다: " + e.getMessage() + "\"}");
        }

        out.flush();
        System.out.println("=== handleSaveDetail 완료 ===");
    }

    // 빈 문자열을 null로 처리하는 도우미 메서드
    private String getStringParam(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    // JSON 문자열 생성 도우미 메소드
    private String jsonString(String value) {
        if (value == null) {
            return "null";
        }
        // JSON 특수문자 이스케이프
        String escaped = value.replace("\\", "\\\\")
                              .replace("\"", "\\\"")
                              .replace("\n", "\\n")
                              .replace("\r", "\\r")
                              .replace("\t", "\\t");
        return "\"" + escaped + "\"";
    }

    // 날짜 포맷팅
    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
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