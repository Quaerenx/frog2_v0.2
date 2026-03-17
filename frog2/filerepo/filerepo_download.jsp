<%@ page import="java.io.*" %>
<%@ page contentType="application/octet-stream; charset=UTF-8" %>

<%
    String path = request.getParameter("path");
    if (path == null) path = "";
    path = path.replaceAll("\\.\\.", "");

    String filename = request.getParameter("filename");
    if (filename == null || filename.contains("..")) {
        out.println("❌ 잘못된 요청입니다.");
        return;
    }

    String realPath = application.getRealPath("/files/" + path + "/" + filename);
    File file = new File(realPath);

    if (!file.exists() || !file.isFile()) {
        out.println("❌ 파일이 존재하지 않거나 잘못된 경로입니다.");
        return;
    }

    response.setContentType("application/octet-stream");
    response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
    response.setHeader("Content-Length", String.valueOf(file.length()));

    FileInputStream in = new FileInputStream(file);
    OutputStream outStream = response.getOutputStream();

    byte[] buffer = new byte[4096];
    int bytesRead;

    while ((bytesRead = in.read(buffer)) != -1) {
        outStream.write(buffer, 0, bytesRead);
    }

    in.close();
    outStream.flush();
    outStream.close();
%>


