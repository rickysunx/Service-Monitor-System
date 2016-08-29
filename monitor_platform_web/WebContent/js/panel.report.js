/**
 * 创建报表界面
 */

var reportPanel = null;

function loadReport(t) {
	$.post("report/list",t?{'t':t}:{},function(data){
		reportPanel.update(data);
	},"text");
}

function createReportPanel() {
	reportPanel = new Ext.Panel({
		title:'报表',autoScroll:true,
		html:""
	});
	
	loadReport();
	
	return reportPanel;
}

function showReportLog(id) {
	var pos = $("#issue_"+id).offset();
	$('#reportLogDiv_'+id).appendTo("body");
	if(pos.left+500>$("body").width()) {
		$('#reportLogDiv_'+id).css('left',(pos.left-400)<0?0:(pos.left-400));
	} else {
		$('#reportLogDiv_'+id).css('left',(pos.left-100)<0?0:(pos.left-100));
	}
	
	$('#reportLogDiv_'+id).css('top',pos.top+20);
	$('#reportLogDiv_'+id).show();
}

function hideReportLog(id) {
	$('#reportLogDiv_'+id).hide();
	$('#reportLogDiv_'+id).appendTo("#reportLogDiv");
}
