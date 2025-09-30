<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="대시보드2" scope="request" />
<%@ include file="/includes/header.jsp" %>

<style>
.dashboard2-container {
    max-width: 1100px;
    margin: 0 auto;
    padding: 24px 16px;
}
.chart-card {
    background: #fff;
    border: 1px solid #e5e7eb;
    border-radius: 12px;
    box-shadow: 0 1px 3px rgba(0,0,0,0.06);
    padding: 16px;
}
.chart-title {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 8px;
}
#donutChart {
    width: 100%;
    height: 420px;
}
</style>

<div class="dashboard2-container">
    <div class="chart-card">
        <div class="chart-title"><i class="fas fa-chart-pie"></i> 카테고리(단일 링)</div>
        <div id="donutChart"></div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/echarts@5/dist/echarts.min.js"></script>
<script>
document.addEventListener('DOMContentLoaded', function() {
    var el = document.getElementById('donutChart');
    var chart = echarts.init(el);

    // 상위 카테고리만 표현 (정적)
    var categories = [
        { name: '고객관리', value: 1 },
        { name: '자료관리', value: 1 }
    ];

    var option = {
        tooltip: {
            trigger: 'item',
            formatter: '{b}'
        },
        legend: {
            orient: 'horizontal',
            bottom: 0
        },
        series: [
            {
                name: '카테고리',
                type: 'pie',
                radius: ['55%', '80%'],
                avoidLabelOverlap: true,
                padAngle: 2,
                itemStyle: {
                    borderRadius: 8,
                    borderColor: '#fff',
                    borderWidth: 2
                },
                label: {
                    show: true,
                    position: 'outside',
                    formatter: '{b}'
                },
                labelLine: {
                    show: true
                },
                data: categories
            }
        ]
    };

    chart.setOption(option);
    window.addEventListener('resize', function(){ chart.resize(); });
});
</script>

<%@ include file="/includes/footer.jsp" %>

