<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>로그인 | 사내 시스템</title>
    <!-- Favicon -->
    <link rel="icon" href="${pageContext.request.contextPath}/favicon.png" type="image/png" sizes="32x32">
    <link rel="apple-touch-icon" href="${pageContext.request.contextPath}/favicon.png">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.png" type="image/png">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pages/dashboard_box.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/login_style.css">
</head>
<body class="dash-centered">
    <div class="page-wrapper">
      <div class="page-body">
        <div class="form-area">
        <div class="logo d-flex align-items-center justify-content-center">
    <div class="logo-text">WorkSpace</div>
</div>

            <h1 class="text-center">로그인</h1>
            <!-- <p class="subtitle">사내 시스템에 액세스하려면 로그인하세요.</p> -->
            
            <% if(request.getAttribute("errorMessage") != null) { %>
                <div class="error-message">
                    <%= request.getAttribute("errorMessage") %>
                </div>
            <% } %>
            
            <form action="login" method="post">
                <div class="form-group">
                    <label for="userId">ID</label>
                    <input type="text" id="userId" name="userId" class="code-input" placeholder="" required>
                </div>
                
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" placeholder="" required>
                </div>
                
                <button type="submit" class="button">로그인</button>
                
                <div class="separator">
                    <div class="separator-line"></div>
                    <div class="separator-line"></div>
                </div>
                
                <p class="text-center mt-16">
                    <!-- <a href="#" style="color: var(--primary); text-decoration: none; font-size: 14px;">비밀번호를 잊으셨나요?</a> -->
                </p>
            </form>

            <div class="footer">
                <p class="mt-16">&copy; 2025 Company Inc. All rights reserved.</p>
            </div>
        </div>
      </div>
    </div>
    
    <div class="features">
        <div class="feature-item"></div>
        <div class="feature-item"></div>
        <div class="feature-item"></div>
    </div>
</body>
</html>