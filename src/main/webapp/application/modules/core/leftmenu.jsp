<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../common/init.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>搜狗数据分析平台</title>
	<jsp:include page="../common/meta.jsp" flush="true"/>
	<jsp:include page="../common/style.jsp" flush="true"/>
</head>
<body>
<div class="leftSide">
    <div class="nav">
        <div class="top">管理平台</div>  
        <dl id="menu3">
            <dt><div class="icon9">PC多VR</div></dt>
            <dd>
                <a  href="javascript:void(0)"  onClick="Menu('<%=modulesPath %>pc_word/multivr_pc_word_main.jsp');">词表查询</a>
                <a  href="javascript:void(0)"  onClick="Menu('<%=modulesPath %>core/indexContent.jsp');">统计信息</a>
            </dd>
            <dt><div class="icon7">WAP多VR</div></dt>
            <dd>
                <a  href="javascript:void(0)"  onClick="Menu('<%=modulesPath %>core/indexContent.jsp');">词表查询</a>
                <a  href="javascript:void(0)"  onClick="Menu('<%=modulesPath %>core/indexContent.jsp');">统计信息</a>
            </dd>
            <dt><div class="icon6" onClick="Menu('<%=modulesPath %>core/indexContent.jsp');">CTR系统</div></dt>
        </dl>
    </div>
</div>
</body>
<jsp:include page="../common/script.jsp" flush="true"/>
<script type="text/javascript">
function Menu(url)
{//重写父窗口中右侧子窗口iframe的src，实现左侧子窗口控制父窗口并显示内容在右侧子窗口
 $('#rightFrame',window.parent.document).attr('src',url);
}
</script>
</html>
