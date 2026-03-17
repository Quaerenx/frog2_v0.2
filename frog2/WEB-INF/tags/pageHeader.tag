<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="title" fragment="true" required="true" %>
<%@ attribute name="subtitle" fragment="true" required="false" %>
<%@ attribute name="actions" fragment="true" required="false" %>
<%@ attribute name="extra" fragment="true" required="false" %>

<div class="page-header">
  <style>
    /* pageHeader 전용 레이아웃 개선 */
    .page-header .ph-header { display: flex; align-items: center; justify-content: space-between; gap: 1rem; flex-wrap: wrap; }
    .page-header .ph-left { display: flex; flex-direction: column; gap: 0.8rem; min-width: 240px; }
    .page-header .ph-title h1 { margin: 0 0 0.4rem 0; font-size: 1.5rem; line-height: 1.3; color: #1f2937; }
    .page-header .ph-subtitle { color: #6b7280; font-size: 0.95rem; display: flex; gap: 0.75rem; flex-wrap: wrap; }
    .page-header .ph-actions { display: inline-flex; align-items: center; justify-content: flex-end; gap: 0.5rem; margin-left: auto; align-self: center; }
    @media (max-width: 768px) {
      .page-header .ph-actions { width: 100%; justify-content: flex-start; order: 2; }
      .page-header .ph-left { order: 1; }
    }
  </style>

  <div class="ph-header">
    <div class="ph-left">
      <div class="ph-title"><h1><jsp:invoke fragment="title"/></h1></div>
      <c:if test="${not empty subtitle}">
        <div class="ph-subtitle"><jsp:invoke fragment="subtitle"/></div>
      </c:if>
    </div>

    <c:if test="${not empty actions}">
      <div class="ph-actions"><jsp:invoke fragment="actions"/></div>
    </c:if>
  </div>

  <c:if test="${not empty extra}">
    <div style="margin-top:8px;">
      <jsp:invoke fragment="extra"/>
    </div>
  </c:if>
</div>