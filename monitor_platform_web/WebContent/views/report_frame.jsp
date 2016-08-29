<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>监控系统</title>
	<link rel="stylesheet" type="text/css" href="ext/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="css/main.css" />
	<script type="text/javascript" src="ext/ext-base.js"></script>
	<script type="text/javascript" src="ext/ext-all-min.js"></script>
	<script type="text/javascript" src="ext/ext-lang-zh_CN.js" charset="utf-8"></script>
	<script type="text/javascript" src="js/jquery.js" charset="utf-8"></script>
	<script type="text/javascript" src="js/common.js" charset="utf-8"></script>
	<script type="text/javascript" src="js/panel.monitor.js" charset="utf-8"></script>
	<script type="text/javascript" src="js/panel.welcome.js" charset="utf-8"></script>
	<script type="text/javascript" src="js/panel.user.js" charset="utf-8"></script>
	<script type="text/javascript" src="js/panel.indicator.js" charset="utf-8"></script>
	<script type="text/javascript" src="js/panel.report.js" charset="utf-8"></script>
	<script type="text/javascript">
	loadReport = function(t) {
		$.post("report/list",t?{'t':t}:{},function(data){
			$("#reportDiv").html(data);
		},"text");
	};
    $(document).ready(function(){
    	loadReport();
    });
	
	</script>
</head>

<body>
	<div id="reportDiv"></div>
</body>

</html>