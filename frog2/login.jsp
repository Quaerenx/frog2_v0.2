<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>로그인 | 사내 시스템</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pages/dashboard_box.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/login_style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/ryan_animation.css">
</head>
<body class="dash-centered">
    <div class="page-wrapper">
      <div class="page-body">
        <div class="form-area">
        <div class="logo d-flex align-items-center justify-content-center">
    <div class="logo-text">WorkSpace</div>
</div>

    <div class="ryan-wrap">
    <svg id="ryan" data-anim="character" viewBox="0 0 120 120" xmlns="http://www.w3.org/2000/svg" aria-label="Wheatley-inspired Animation" role="img">
        <defs>
            <radialGradient id="shellShading" cx="50%" cy="50%" r="50%" fx="30%" fy="30%">
                <stop offset="0%" stop-color="#ffffff"/>
                <stop offset="60%" stop-color="#dfe3e6"/>
                <stop offset="95%" stop-color="#b8c1c8"/>
                <stop offset="100%" stop-color="#9aa3ab"/>
            </radialGradient>

            <radialGradient id="eyeGlow" cx="50%" cy="50%" r="50%">
                <stop offset="0%" stop-color="#b9e6ff"/>
                <stop offset="25%" stop-color="#4fc3ff"/>
                <stop offset="55%" stop-color="#1e88e5"/>
                <stop offset="100%" stop-color="#0b5db3"/>
            </radialGradient>
            
            <pattern id="honeycomb" patternUnits="userSpaceOnUse" width="4" height="7" x="-1" y="-1">
                <path d="M-1,3.5 l2,-3.5 h4 l2,3.5 l-2,3.5 h-4 z" stroke="#aadeff" stroke-width="0.3" fill="none" />
            </pattern>

            <filter id="innerShadow" x="-20%" y="-20%" width="140%" height="140%">
                <feDropShadow dx="0.5" dy="0.5" stdDeviation="1" flood-color="#000000" flood-opacity="0.3" />
            </filter>
            
            <filter id="weathering" x="-10%" y="-10%" width="120%" height="120%">
                <feTurbulence type="fractalNoise" baseFrequency="0.03 0.9" numOctaves="1" result="scratchNoise"/>
                <feDisplacementMap in="SourceGraphic" in2="scratchNoise" scale="1.5" xChannelSelector="R" yChannelSelector="G" result="displaced"/>
                <feSpecularLighting in="scratchNoise" surfaceScale="2" specularConstant="1" specularExponent="30" lighting-color="#dddddd" result="specular">
                    <feDistantLight azimuth="235" elevation="60" />
                </feSpecularLighting>
                <feComposite in="displaced" in2="specular" operator="in" result="specularComposite"/>
                <feComposite in="displaced" in2="specularComposite" operator="arithmetic" k1="0" k2="1" k3="1" k4="0"/>
            </filter>
        </defs>

        <g filter="url(#weathering)">
            <animateTransform attributeName="transform" type="rotate" values="0 60 60; -1 60 60; 1 60 60; 0 60 60" dur="12s" repeatCount="indefinite" />

            <circle cx="60" cy="60" r="50" fill="url(#shellShading)" stroke="#b8c1c8" stroke-width="1" />

            <g stroke="#9aa3ab" stroke-width="0.75" fill="none" opacity="0.8">
                <path d="M 24.6,18.5 A 50 50 0 0 1 95.4,18.5" />
                <path d="M 12.2,42 A 50 50 0 0 1 12.2,78" />
                <path d="M 24.6,101.5 A 50 50 0 0 1 95.4,101.5" />
                <path d="M 107.8,42 A 50 50 0 0 0 107.8,78" />
            </g>

            <g class="ears" fill="#3c3f44" stroke="#212325" stroke-width="0.5">
                <path d="M25,15 C40,2, 80,2, 95,15 L97,19 C80,8, 40,8, 23,19 Z" />
                <path d="M25,105 C40,118, 80,118, 95,105 L97,101 C80,112, 40,112, 23,101 Z" />
                <path d="M21,28 l8,-12 -2,-3 -8,12z" />
                <path d="M99,28 l-8,-12 2,-3 8,12z" />
                <path d="M21,92 l8,12 -2,3 -8,-12z" />
                <path d="M99,92 l-8,12 2,3 8,12z" />
            </g>

            <g class="muzzle" filter="url(#innerShadow)">
                <circle cx="60" cy="60" r="34" fill="#f1f3f5" stroke="#adb5bd" stroke-width="1.5"/>
                <circle cx="60" cy="60" r="28" fill="#cccccc" stroke="#8b949e" stroke-width="1"/>
            </g>
        </g>

        <g class="eyes">
            <circle cx="60" cy="60" r="18" fill="url(#eyeGlow)"/>
            <circle cx="60" cy="60" r="18" fill="url(#honeycomb)" opacity="0.7"/>
            <circle class="pupil" cx="60" cy="60" r="7" fill="#083d77"/>
            <circle cx="56" cy="56" r="3" fill="#e6f7ff" opacity="0.9"/>
        </g>
        
        
    </svg>
    </div>
    <h1 class="text-center">로그인</h1>
            <!-- <p class="subtitle">사내 시스템에 액세스하려면 로그인하세요.</p> -->
            
            <% if(request.getAttribute("errorMessage") != null) { %>
                <div class="error-message">
                    <%= request.getAttribute("errorMessage") %>
                </div>
            <% } %>
            
            <form action="login" method="post" autocomplete="off">
                <!-- 브라우저 자동완성 방지용 더미 필드 -->
                <input type="text" style="display:none" autocomplete="username">
                <input type="password" style="display:none" autocomplete="new-password">
                <div class="form-group">
                    <label for="userId">ID</label>
                    <input type="text" id="userId" name="userId" class="code-input" placeholder="" required autocomplete="off" autocapitalize="off" autocorrect="off" spellcheck="false">
                </div>
                
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" placeholder="" required autocomplete="new-password" autocapitalize="off" autocorrect="off" spellcheck="false">
                </div>
                
                <div class="form-group" style="display:flex; align-items:center; gap:8px;">
                    <input type="checkbox" id="rememberId" name="rememberId" style="width:16px; height:16px;">
                    <label for="rememberId" style="margin:0;">ID 저장</label>
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
    <script src="${pageContext.request.contextPath}/resources/js/ryan_animation.js"></script>
    <script>
    // ID 저장 기능 (localStorage 사용)
    document.addEventListener('DOMContentLoaded', function() {
        var idInput = document.getElementById('userId');
        var remember = document.getElementById('rememberId');
        try {
            var saved = localStorage.getItem('savedUserId');
            var flag = localStorage.getItem('rememberId') === 'Y';
            if (flag && saved) {
                idInput.value = saved;
                remember.checked = true;
            }
        } catch (e) {}

        var form = document.querySelector('form[action="login"]');
        if (form) {
            form.addEventListener('submit', function() {
                try {
                    if (remember && remember.checked) {
                        localStorage.setItem('rememberId', 'Y');
                        localStorage.setItem('savedUserId', idInput ? (idInput.value || '') : '');
                    } else {
                        localStorage.removeItem('rememberId');
                        localStorage.removeItem('savedUserId');
                    }
                } catch (e) {}
            });
        }
    });
    </script>
</body>
</html>