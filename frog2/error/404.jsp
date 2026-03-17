<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <title>페이지를 찾을 수 없습니다 (404)</title>
  <!-- Favicon -->
  <link rel="icon" href="${pageContext.request.contextPath}/favicon.png" type="image/png" sizes="32x32">
  <link rel="apple-touch-icon" href="${pageContext.request.contextPath}/favicon.png">
  <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.png" type="image/png">
  <style>body{font-family:system-ui,-apple-system,Segoe UI,Roboto,Helvetica,Arial,sans-serif;margin:40px;color:#333} .card{max-width:640px;margin:auto;padding:24px;border:1px solid #eee;border-radius:8px;box-shadow:0 2px 8px rgba(0,0,0,.04)} h1{font-size:22px;margin:0 0 12px} p{margin:8px 0}</style>
</head>
<body>
  <div class="card">
    <h1>요청하신 페이지를 찾을 수 없습니다.</h1>
    <p>입력하신 주소가 정확한지 확인해 주세요.</p>
    <p><a href="<%= request.getContextPath() %>/dashboard">대시보드로 이동</a></p>
  </div>
</body>
</html>