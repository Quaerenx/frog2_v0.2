<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.nio.file.*" %>
<%@ page import="jakarta.servlet.http.Part" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%
// 세션 확인
HttpSession userSession = request.getSession(false);
if (userSession == null || userSession.getAttribute("user") == null) {
    response.sendRedirect("login");
    return;
}

String uploadPath = request.getParameter("path");
if (uploadPath == null) uploadPath = "";

// 보안 검증
if (uploadPath.contains("..")) {
    out.println("❌ 잘못된 경로입니다.");
    return;
}

String realUploadPath = application.getRealPath("/files/" + uploadPath);
File uploadDir = new File(realUploadPath);
if (!uploadDir.exists()) {
    uploadDir.mkdirs();
}

try {
    // Servlet 3.0+ 내장 멀티파트 지원 사용
    Collection<Part> parts = request.getParts();
    
    if (parts == null || parts.isEmpty()) {
        out.println("❌ 업로드할 파일이 없습니다.");
        return;
    }
    
    int uploadCount = 0;
    StringBuilder results = new StringBuilder();
    
    for (Part part : parts) {
        // 폼 필드가 아닌 파일만 처리
        if (part.getName().equals("uploadFiles") && part.getSize() > 0) {
            String fileName = part.getSubmittedFileName();
            
            // 기본 보안 검증
            if (fileName == null || fileName.trim().isEmpty()) {
                results.append("⚠️ 빈 파일명이 있어서 건너뜀<br>");
                continue;
            }
            
            // 파일명에서 경로 제거 (보안)
            fileName = Paths.get(fileName).getFileName().toString();
            
            // 위험한 확장자 차단
            String[] dangerousExts = {".exe", ".jsp", ".php", ".bat", ".cmd", ".scr", ".js", ".vbs"};
            String lowerFileName = fileName.toLowerCase();
            boolean isDangerous = false;
            
            for (String ext : dangerousExts) {
                if (lowerFileName.endsWith(ext)) {
                    results.append("❌ 보안상 업로드할 수 없는 파일: ").append(fileName).append("<br>");
                    isDangerous = true;
                    break;
                }
            }
            
            if (isDangerous) continue;
            
            // 파일 크기 검증
            if (part.getSize() > 10 * 1024 * 1024) {
                results.append("❌ 파일이 너무 큼 (10MB 초과): ").append(fileName).append("<br>");
                continue;
            }
            
            if (part.getSize() == 0) {
                results.append("⚠️ 빈 파일은 건너뜀: ").append(fileName).append("<br>");
                continue;
            }
            
            // 파일 저장
            File uploadFile = new File(uploadDir, fileName);
            
            // 동일한 이름의 파일이 있으면 숫자 추가
            int counter = 1;
            String originalName = fileName;
            while (uploadFile.exists()) {
                String name = originalName;
                String ext = "";
                int lastDot = originalName.lastIndexOf('.');
                if (lastDot > 0) {
                    name = originalName.substring(0, lastDot);
                    ext = originalName.substring(lastDot);
                }
                fileName = name + "_" + counter + ext;
                uploadFile = new File(uploadDir, fileName);
                counter++;
            }
            
            // 파일 저장 (Servlet Part API 사용)
            part.write(uploadFile.getAbsolutePath());
            
            uploadCount++;
            results.append("✅ 업로드 성공: ").append(fileName);
            
            // 파일 크기 표시
            long fileSize = uploadFile.length();
            if (fileSize > 1024 * 1024) {
                results.append(" (").append(String.format("%.1f", fileSize / 1024.0 / 1024.0)).append("MB)");
            } else if (fileSize > 1024) {
                results.append(" (").append(String.format("%.1f", fileSize / 1024.0)).append("KB)");
            } else {
                results.append(" (").append(fileSize).append("B)");
            }
            results.append("<br>");
        }
    }
    
    // 결과 페이지 표시
    if (uploadCount > 0) {
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>업로드 완료</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/css/bootstrap.min.css">
    <style>
        .upload-result {
            max-width: 600px;
            margin: 50px auto;
            padding: 30px;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .success-icon {
            font-size: 48px;
            color: #28a745;
            text-align: center;
            margin-bottom: 20px;
        }
    </style>
</head>
<body class="bg-light">
    <div class="upload-result">
        <div class="success-icon">🎉</div>
        <h3 class="text-center text-success">업로드 완료!</h3>
        <hr>
        <div class="mb-3">
            <strong>📊 업로드 결과:</strong><br>
            <%= results.toString() %>
        </div>
        <div class="text-center">
            <a href="filerepo_downlist.jsp?path=<%= uploadPath %>" class="btn btn-primary">
                📁 파일 목록으로 돌아가기
            </a>
            <a href="filerepo_upload.jsp?path=<%= uploadPath %>" class="btn btn-secondary ml-2">
                📤 추가 업로드
            </a>
        </div>
    </div>
    
    <script>
        // 5초 후 자동으로 파일 목록으로 이동
        setTimeout(function() {
            window.location.href = 'filerepo_downlist.jsp?path=<%= uploadPath %>';
        }, 5000);
    </script>
</body>
</html>
<%
    } else {
        response.sendRedirect("filerepo_upload.jsp?path=" + uploadPath + "&error=no_files");
    }
    
} catch (Exception e) {
    out.println("❌ 업로드 오류: " + e.getMessage());
    out.println("<br><a href='filerepo_upload.jsp?path=" + uploadPath + "'>다시 시도</a>");
    e.printStackTrace();
}
%>


