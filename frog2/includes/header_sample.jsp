<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle} - 샘플 헤더</title>
    <!-- Favicon -->
    <link rel="icon" href="${pageContext.request.contextPath}/favicon.png" type="image/png" sizes="32x32">
    <link rel="apple-touch-icon" href="${pageContext.request.contextPath}/favicon.png">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.png" type="image/png">
    <!-- 기본 스타일 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/main_style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/components.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/utilities.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pages/header.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pages/header_sample.css">
</head>
<body>
    <script>
      (function(){
        try {
          var head = document.head || document.getElementsByTagName('head')[0];
          if (!head) return;
          var hasIcon = head.querySelector('link[rel~="icon"], link[rel="shortcut icon"]');
          if (!hasIcon) {
            var href = (window.__ctxPath || '${pageContext.request.contextPath}') + '/favicon.png?v=1';
            var link = document.createElement('link');
            link.rel = 'icon';
            link.type = 'image/png';
            link.sizes = '32x32';
            link.href = href;
            head.appendChild(link);
            var s = document.createElement('link');
            s.rel = 'shortcut icon';
            s.type = 'image/png';
            s.href = href;
            head.appendChild(s);
          }
        } catch (e) {}
      })();
    </script>
  <!-- 별개 박스 형태의 샘플 헤더 -->
  <div class="sample-header-box">
    <div class="sample-header-inner">
      <div class="sample-header-title">
        <i class="fas fa-tachometer-alt"></i>
        <span>${pageTitle}</span>
      </div>
      <div class="sample-header-actions">
        <a class="cta" href="${pageContext.request.contextPath}/customers?view=list">
          <i class="fas fa-plus"></i> 새 고객사 추가
        </a>
      </div>
    </div>
    <nav class="sample-header-nav">
      <ul>
        <li><a class="${pageTitle eq '대시보드 샘플' ? 'active' : ''}" href="${pageContext.request.contextPath}/dashboard_sample.jsp">대시보드</a></li>
        <li><a href="${pageContext.request.contextPath}/customers?view=list">고객관리</a></li>
        <li><a href="${pageContext.request.contextPath}/maintenance">정기점검</a></li>
        <li><a href="${pageContext.request.contextPath}/meeting?view=list">회의록</a></li>
        <li><a href="${pageContext.request.contextPath}/troubleshooting?view=list">트러블슈팅</a></li>
      </ul>
    </nav>
  </div>