<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="대시보드" scope="request" />
<c:set var="pageBodyClass" value="page-1050 page-customers" scope="request" />
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${pageTitle}</title>
  <!-- Favicon -->
  <link rel="icon" href="${pageContext.request.contextPath}/favicon.png?v=20251017" type="image/png" sizes="32x32">
  <link rel="apple-touch-icon" href="${pageContext.request.contextPath}/favicon.png?v=20251017">
  <!-- 기본 스타일 로드 (헤더/푸터 없이 카드만 사용) -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/main_style.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/components.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/utilities.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pages/dashboard.css">
  <!-- 고객사 페이지 공통 톤(헤더/버튼)을 재사용 -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pages/customers.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pages/dashboard_box.css">
  <style>
    /* 카드 헤더 아이콘-타이틀 간격 최소화 (대시보드 카드 한정) */
    .dashboard-card .card-header { display: flex; align-items: center; gap: 4px; }
    .dashboard-card .card-header i { margin-right: 0; }
    /* 서브메뉴: 아이콘과 텍스트 수평/수직 정렬 */
    .dashboard-submenu a { display: inline-flex; align-items: center; }
    /* 대시보드의 pageHeader 서브타이틀은 flex를 사용하지 않고 줄바꿈 허용 */
    .customer-management .page-header .ph-subtitle { display: block !important; }
  </style>
</head>
<body class="page-1050 page-customers">
  <%@ include file="/includes/header.jsp" %>

    <div class="customer-management">
        <t:pageHeader>
          <jsp:attribute name="title"><i class="fas fa-th-large"></i> 대시보드</jsp:attribute>
          <jsp:attribute name="subtitle">
            업무 바로가기와 핵심 메뉴를 한눈에 확인하세요
            <br/>
            <span style="display:block; margin-top:6px; font-weight:600;">안녕하세요 <c:out value="${sessionScope.user != null ? sessionScope.user.userName : ''}"/> 님</span>
          </jsp:attribute>
        </t:pageHeader>
        <div class="card-grid">
        <c:forEach var="entry" items="${dashboardMenus}">
          <div class="card-item">
            <div class="card dashboard-card">
              <div class="card-header">
                <c:set var="cat" value="${entry.key}" />
                <c:choose>
                  <c:when test="${cat == '고객관리'}"><i class="fas fa-address-book"></i></c:when>
                  <c:when test="${cat == '자료관리'}"><i class="fas fa-folder-open"></i></c:when>
                  <c:otherwise><i class="fas fa-th-large"></i></c:otherwise>
                </c:choose>
                ${entry.key}
              </div>
              <div class="card-body">
                <ul class="dashboard-submenu">
                  <c:forEach var="menuItem" items="${entry.value}">
                    <c:set var="resolvedUrl" value="${menuItem.url}" />
                    <c:if test="${resolvedUrl == 'downlist.jsp'}">
                      <c:set var="resolvedUrl" value="filerepo/filerepo_downlist.jsp" />
                    </c:if>
                    <li>
                      <a href="${pageContext.request.contextPath}/${resolvedUrl}">
                        <i class="${menuItem.icon} fa-fw mr-2"></i>${menuItem.title}
                      </a>
                    </li>
                  </c:forEach>
                </ul>
              </div>
            </div>
          </div>
        </c:forEach>
          <!-- 추가 카드: 외부 링크 바로가기 리스트 -->
          <div class="card-item">
            <div class="card dashboard-card">
              <div class="card-header">
                <i class="fas fa-external-link-alt"></i> 바로가기
              </div>
              <div class="card-body">
                <ul class="dashboard-submenu">
                  <li>
                    <a href="https://docs.vertica.com/" target="_blank" rel="noopener noreferrer">
                      <i class="fas fa-link fa-fw mr-2"></i> Vertica 공식 홈페이지
                    </a>
                  </li>
                  <li>
                    <a href="https://x2wizard.github.io/" target="_blank" rel="noopener noreferrer">
                      <i class="fas fa-link fa-fw mr-2"></i> Vertica 블로그
                    </a>
                  </li>
                  <li>
                    <a href="https://www.microfocus.com/lifecycle/" target="_blank" rel="noopener noreferrer">
                      <i class="fas fa-link fa-fw mr-2"></i> Vertica EOS 정보
                    </a>
                  </li>
                  <li>
                    <a href="https://portal.microfocus.com/s/customdetailpage" target="_blank" rel="noopener noreferrer">
                      <i class="fas fa-link fa-fw mr-2"></i> Vertica Caseopen
                    </a>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

  <%@ include file="/includes/footer.jsp" %>

  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</body>
</html>