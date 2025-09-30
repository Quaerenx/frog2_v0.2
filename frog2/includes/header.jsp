<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle} - 게시판 시스템</title>
    <!-- Favicon -->
    <link rel="icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/x-icon">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/x-icon">
    <!-- 기본 스타일시트 임포트 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/main_style.css">
    <!-- 컴포넌트 스타일시트 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/components.css">
    <!-- 유틸리티 스타일시트 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/utilities.css">
    <!-- 아카이브 스타일시트 -->
	<%--     <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/archive_style.css"> --%>
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
</head>
<body class="${pageBodyClass}">
    <header class="main-header">
        <div class="header-box">
        <div class="container">
            <div class="logo">
                <a href="${pageContext.request.contextPath}/dashboard" class="logo-icon d-flex align-items-center">
			        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="28" height="28">
			            <path d="M5 3h14a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5c0-1.1.9-2 2-2zm0 2v14h14V5H5zm2 2h10v2H7V7zm0 4h10v2H7v-2zm0 4h7v2H7v-2z" fill="#333333" opacity="0.9"/>
			        </svg>
                </a>	
                <span class="logo-text">ARCHIVE</span>
            </div>
            
            <nav class="main-nav">
                <ul>
                    <!-- 대시보드 -->
                    <li>
                        <a href="${pageContext.request.contextPath}/dashboard" class="${pageTitle eq '대시보드' ? 'active' : ''}">
                            <i class="fas fa-tachometer-alt mr-1"></i>대시보드
                        </a>
                    </li>
                    
                    
                    <!-- 고객관리 드롭다운 -->
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle ${pageTitle eq '고객사 정보' || pageTitle eq '정기점검 이력' ? 'active' : ''}">
                            <i class="fas fa-building mr-1"></i>고객관리
                            <!-- <i class="fas fa-caret-down ml-1"></i> -->
                        </a>
                        <ul class="dropdown-menu">
                            <li>
                                <a href="${pageContext.request.contextPath}/customers?view=list">
                                    <i class="fas fa-address-card mr-2"></i>고객사 정보
                                </a>
                            </li>
                            <li>
                                <a href="${pageContext.request.contextPath}/maintenance">
                                    <i class="fas fa-clipboard-check mr-2"></i>정기점검 이력
                                </a>
                            </li>
                        </ul>
                    </li>
                                        <!-- 자료관리 드롭다운 -->
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle ${pageTitle eq '업무자료' || pageTitle eq '회의자료' || pageTitle eq '기타자료' ? 'active' : ''}">
                            <i class="fas fa-folder mr-1"></i>자료관리
                            <!-- <i class="fas fa-caret-down ml-1"></i> -->
                        </a>	
                        <ul class="dropdown-menu">
                            <li>
							    <a href="${pageContext.request.contextPath}/meeting?view=list">
							        <i class="fas fa-clipboard-list mr-2"></i>회의록	
							    </a>
							</li>
                            <li>
                                <a href="${pageContext.request.contextPath}/filerepo/filerepo_downlist.jsp">
                                    <i class="fas fa-file-alt mr-2"></i>자료실
                                </a>
                            </li>
                            <li>
                                <a href="${pageContext.request.contextPath}/troubleshooting?view=list">
                                    <i class="fas fa-file-alt mr-2"></i>트러블슈팅
                                </a>
                            </li>
                        </ul>
                    </li>
                    
                    
                    
                    <!-- 로그아웃 -->
                    <li>
                        <a href="${pageContext.request.contextPath}/logout">
                            <i class="fas fa-sign-out-alt mr-1"></i>로그아웃
                        </a>
                    </li>
                </ul>	
            </nav>
        </div>
        </div>
    </header>
    
    <!-- 드롭다운 메뉴를 위한 스타일 및 스크립트 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pages/header.css">
    
    <script>
        $(document).ready(function() {
            // 모바일 환경에서 드롭다운 토글
            $('.main-nav ul li.dropdown > a').on('click', function(e) {
                if ($(window).width() <= 768) {
                    e.preventDefault();
                    $(this).parent().toggleClass('open');
                }
            });
            
            // 현재 메뉴 활성화
            const path = window.location.pathname;
            const query = window.location.search;
            
            // 자료관리 메뉴 활성화
            if (path.includes('/filerepo') || path.includes('/archive') || path.includes('/meeting') || path.includes('/downlist') || path.includes('/troubleshooting')) {
        	$('.main-nav ul li.dropdown').eq(1).addClass('active');
            }
            
            // 고객관리 메뉴 활성화
            if (path.includes('/customers') || path.includes('/maintenance')) {
            $('.main-nav ul li.dropdown').eq(0).addClass('active');
                
            }

            // 모바일에서 바깥 영역 클릭 시 드롭다운 닫기
            $(document).on('click', function(e) {
                if ($(window).width() <= 768) {
                    if (!$(e.target).closest('.main-nav ul li.dropdown').length) {
                        $('.main-nav ul li.dropdown').removeClass('open');
                    }
                }
            });

            // 모바일에서 드롭다운 항목 클릭 시 닫기
            $('.main-nav ul li.dropdown .dropdown-menu a').on('click', function() {
                if ($(window).width() <= 768) {
                    $('.main-nav ul li.dropdown').removeClass('open');
                }
            });
        });
    </script>
