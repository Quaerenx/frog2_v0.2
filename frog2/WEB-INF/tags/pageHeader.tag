<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="title" fragment="true" required="true" %>
<%@ attribute name="subtitle" fragment="true" required="false" %>
<%@ attribute name="actions" fragment="true" required="false" %>
<%@ attribute name="extra" fragment="true" required="false" %>

<div class="page-header">
  <div class="d-flex justify-content-between align-items-center">
    <div>
      <h1><jsp:invoke fragment="title"/></h1>
      <c:if test="${not empty subtitle}">
        <p class="lead" style="text-align:left;">
          <jsp:invoke fragment="subtitle"/>
        </p>
      </c:if>
    </div>
    <div>
      <c:if test="${not empty actions}">
        <jsp:invoke fragment="actions"/>
      </c:if>
    </div>
  </div>
  <c:if test="${not empty extra}">
    <div style="margin-top:8px;">
      <jsp:invoke fragment="extra"/>
    </div>
  </c:if>
  </div>

