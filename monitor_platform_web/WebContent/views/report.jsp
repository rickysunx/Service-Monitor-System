<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>



<table class="report_table" cellpadding="0" cellspacing="0">
	<tbody>
		<tr>
			<td class="col_0 report_header"></td>
			<td class="col_1 report_header">当前状态</td>
			<td class="col_2 report_header">
			<c:forEach var="date" items="${dateList}">
				<span class="date_header">${date}</span>
			</c:forEach>
			</td>
		</tr>
		
		<c:forEach var="module" items="${moduleList}">
		<tr>
			<td class="col_0">
				<c:if test="${module.moduleStatus==2}">
					<span class="module_yellow_icon"></span>
				</c:if>
				<c:if test="${module.moduleStatus!=2}">
					<span class="module_green_icon"></span>
				</c:if>
			</td>
			<td class="col_1">${module.moduleName}</td>
			<td class="content_col_2">
				<div class="content_row">
					<div class="content_split" style="left:100px;"></div>
					<div class="content_split" style="left:200px;"></div>
					<div class="content_split" style="left:300px;"></div>
					<div class="content_split" style="left:400px;"></div>
					<div class="content_split" style="left:500px;"></div>
					<div class="content_split" style="left:600px;"></div>
					<div class="content_item_div">
					<c:forEach var="data" items="${processData}">
						<c:if test="${data.moduleId==module.moduleId}">
							<div id="issue_${data.reportGroupId}" onmouseover="showReportLog(${data.reportGroupId});" 
								onmouseout="hideReportLog(${data.reportGroupId});" class="issue_icon" style="left:${data.pxStart}px;width:${data.pxWidth}px;" >
								<div class="issue_icon_left"></div>
								<c:if test="${data.pxMid>0}">
									<div class="issue_icon_mid" style="width:${data.pxMid}px;"></div>
								</c:if>
								<div class="issue_icon_right"></div>
							</div>
						</c:if>
					</c:forEach>
					</div>
				</div>
			</td>
		</tr>
		</c:forEach>
		
		<tr>
			<td class="footer_0"></td>
			<td class="footer_1"></td>
			<td class="footer_2">
				<a href="javascript:loadReport(${prevWeek});">&lt;&lt;上周</a>
				<a href="javascript:loadReport(${nextWeek});">下周&gt;&gt;</a>
			</td>
		</tr>
		
	</tbody>
</table>

<div id="reportLogDiv">
	<c:forEach var="data" items="${processData}">
		<div id="reportLogDiv_${data.reportGroupId}" _mtype="reportLogDiv" style="display:none;position:absolute;">${data.logText}</div>
	</c:forEach>
</div>




