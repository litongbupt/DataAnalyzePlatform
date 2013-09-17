<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../common/init.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<base href="<%=basePath%>" />
	<jsp:include page="../common/meta.jsp" flush="true"/>
	<jsp:include page="../common/style.jsp" flush="true"/>
	
<title>搜狗PC多VR分析系统</title>
<style type="text/css">
	.select_opt{float:left; width:35px; height:100%; margin-left:20px;vertical-align:center;}
	.select_opt p{width:20px; height:13px; margin-top:20px; background:url(<%=templatePath%>/images/arr.gif) no-repeat; cursor:pointer; text-indent:-999em}
	.select_opt p#toright{background-position:2px 0}
	.select_opt p#toleft{background-position:2px -16px}
</style>
</head>

<body id="activity_pane">
<div class="rightSide">
	<div class="top">
    	<div class="left"></div>
        <div class="current">
        	当前位置：&gt; PC多VR统计查询
        </div>
    </div>
    <div class="content" id="main-content">
    	<div class="sear" id="query_form"> 
    		<form id="exportForm" method="post" >
        	<label>从：<input type="text" size="15" id="query_startTime" name="startTime"/></label>
            <label>到：<input type="text" size="15" id="query_endTime" name="endTime"/></label>
            <label>类型 :
                <select id="query_type" name="type" size="1" multiple="multiple" style="width: 160px">
            		<option></option>
         	    </select>
         	</label>
            <label>位置 :
                <select id="query_position" name="position" size="1">
             	  <option value=''>--请选择--</option>
         	    </select></label>
            <label>abtest:	
            	<select id="query_abtest" name="abtest" size="1">
         	    </select>
            </label>
            <label>时间级别:	
            	<select id="query_timelevel" name="timelevel" size="1">
         	    	<option value="hour" >小时</option>
         	    	<option value="day" selected="selected">天</option>
         	    </select>
            </label>
            <label>clickid:<input type="text" size="8" id="query_clickid" name="clickid"/></label>
                <input id="export_selectCols" name="selectCols" type="hidden" />
		       	<input id="export_maxRecords" name="maxRecords" type="hidden" />
		       	<input id="export_sortname" name="sortName" type="hidden" />
		       	<input id="export_sortorder" name="sortOrder" type="hidden" />
		       	<input id="export_search" name="search" type="hidden" />
            <label><input type="button" value="查询" class="butt" onclick="query()"/></label>
            </form>
        </div>
        <div class="toolBar">&nbsp;
        	<input type="button" value="导出" class="leadingInBeen" onclick="exportListIn('pc_word')"/>
        </div>
		<table id="modelList"></table>
		<div id="listPager"></div>
    </div>
    <div class="bottom">
    	<div class="left"></div>
        <div class="right"><div class="center"></div></div>
    </div>

<!-- 弹窗定义 -->
	<div id="detailDialog">
	</div>
	<!-- 导入弹窗-->
	<div id="exportDialog" title="导出数据" style="width:620px;"></div>
	<!--引入进度条、提示告警、确认和失败对话框 -->
	<%@ include file="../common/progress_dialog.jsp"%>
	<%@ include file="../common/alert_dialog.jsp" %>
	<%@ include file="../common/confirm_dialog.jsp" %>
	<%@ include file="../common/failure_dialog.jsp" %>
<jsp:include page="../common/script.jsp" flush="true"/>
<script type="text/javascript" src="<%=templatePath%>js/pc_statistics/multivr_pc_statistics_main.js"></script>
<script type="text/javascript" src="<%=templatePath%>js/common/export.js"></script>
</body>

</html>
