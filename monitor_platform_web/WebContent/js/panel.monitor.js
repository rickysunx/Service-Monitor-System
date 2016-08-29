/**
 * 监控界面
 */
 
var showIndicatorDetail = null;
var processException = null;
var showLastErrorLog = null;

function getXX(n) {
	return (n<10)?("0"+n):(""+n);
}

function createMonitorPanel() {
	
	var currMainPanel = undefined;
	
	var returnButton = new Ext.Button({text:'返回',hidden:true,listeners:{'click':function(){
		setMonitorMainPanel(indicatorGridPanel);
		returnButton.setVisible(false);
	}}});
	
	var toolbar = [{text:'刷新',listeners:{'click':function(){
		reloadModuleList();
	}}},returnButton];
	
	//左侧模块列表
	var moduleListPanel = new Ext.tree.TreePanel({
		region:'west',width:150,split:true,border:false,bodyStyle:'border-width:0 1px 0 0',
		rootVisible:false,
		root:new Ext.tree.AsyncTreeNode({}),
		listeners:{'click':function(node,event){
			var nodeObject = node.attributes.userNodeObject;
			var moduleId = nodeObject.moduleId;
			returnButton.setVisible(false);
			setMonitorMainPanel(indicatorGridPanel);
			//这里装载全部的列表
			loadIndicatorList(moduleId);
		}}
	});
	
	//右侧主面板
	var monitorMainPanel = new Ext.Panel({
		region:'center',layout:'fit',border:false,bodyStyle:'border-width:0 0 0 1px'
	});
	
	//指标列表的Store
	var indicatorStore = new Ext.data.JsonStore({
		url:'indicator/list',root:'data',
		fields:['indicatorId','indicatorName','currentValue','currentStatus',
			'warnValue','alertValue','currentValueName','warnValueName','alertValueName',
			'lastStatusTimeName','lastStatus',
			'currentStatusHtml','lastStatusHtml','operationHtml']
	});
	
	//指标表格界面
	var indicatorGridPanel = new Ext.grid.GridPanel({
		'store':indicatorStore,border:false,
		'columns':[
			{header:'指标',dataIndex:'indicatorName'},
			{header:'当前值',dataIndex:'currentValueName',align:'right'},
			{header:'预警值',dataIndex:'warnValueName',align:'right'},
			{header:'报警值',dataIndex:'alertValueName',align:'right'},
			{header:'报警时间',dataIndex:'lastStatusTimeName',width:150},
			{header:'当前状态',dataIndex:'currentStatusHtml'},
			{header:'待处理状态',dataIndex:'lastStatusHtml'},
			{header:'操作',dataIndex:'operationHtml',width:150}
		]
	});
	
	showLastErrorLog = function(indicatorId) {
		Ext.Ajax.timeout=900000;
		$.post("indicator/getindicatorInfo",{'indicatorId':indicatorId},function(data){
			//所用到的store
			var indicatorType = data.data.indicatorType;
			var store;
			if(indicatorType==0){
				store = new Ext.data.JsonStore({
					url:'indicator/getErrorLog?indicatorId='+indicatorId,
					root:'data.logs',
					fields:['id','tableId','tableName','userId','userName','gameDomain','createTime','reason','logType','extension','properties']
				});
			}else{
				store = new Ext.data.JsonStore({
					url:'indicator/getErrorLog?indicatorId='+indicatorId,
					root:'data.logs',
					fields:['id','tableId','tableName','success','startTime','endTime','statusCode','spendTime','header','content','createTime']
				});
			};
			//添加查询条件
			
			var formPanel = new Ext.form.FormPanel({
				labelWidth : 60,
				labelAlign : 'right',
				//height : 250,
				autoHeight : true,
				frame: true,
				region : 'center',
				//定位
				items : [ {
					xtype : 'fieldset',
					title : '过滤条件',
					autoHeight : true,
					width:965,
					items: [{//第一行
			            layout:"column",
						width:940,
			            items:[  
			            { 
				            layout:"column",
				            items:[{   
				                columnWidth:.25,//第一列 
				                layout:"form",
				                items:[{   
				                    xtype:"datefield",
									id:'startDate',
				                    fieldLabel: '起始日期',
									emptyText : '请选择',
									format : 'Y-m-d',//日期格式  
				                    width:134   
				                    }]   
				            },{   
				                columnWidth:.25,//第二列   
				                layout:"form",
				                items:[{   
				                    xtype:"timefield", 
									id:'startTime',
									format:'H:i:s',
				                    fieldLabel: '起始时间',
									emptyText : '请选择',
				                    width:134   
				                    }]   
				            },{   
				                columnWidth:.25,//第三列   
				                layout:"form",   
				                items:[{   
				                    xtype:"datefield", 
									id:'endDate',
				                    fieldLabel: '结束日期',
									emptyText : '请选择',
									format : 'Y-m-d',//日期格式
				                    width:134
				                    }]   
				            },{   
				                columnWidth:.25,//第四列   
				                layout:"form",   
				                items:[{   
				                    xtype:"timefield",
									id:'endTime',
				                    fieldLabel: '结束时间',
				                    format:'H:i:s',
									emptyText : '请选择',
				                    width:134 
				                    }]   
				            }]},//第一行结束
							{   
			                layout:"form",   
			                items:[{   
			                    xtype:"textfield",
			                    id:"cond",
			                    fieldLabel: '查询语句',
			                    emptyText:"请输入",
								width:840
			                    }]
							}]},//第二行结束 
							{
								xtype:"textfield",
			                    id:"jexlCond",
			                    emptyText:"请正确输入jexl表达式",
			                    fieldLabel: 'jexl表达式',   
								width:840
							}
			           ]
				}],
				buttons:[{
					text:'查询',
					width:80,
					handler : function() {
						Ext.getCmp('page').setValue(1);
						var lastRecord = "";
//						temp = new Object();
//						temp.lastRecord = lastRecord;
//						var params = store.baseParams; 
//						Ext.apply(params,temp);
						//store.load({params: { start: 0, limit: 10 },callback : function(o,response,success){if (success == false){Ext.Msg.alert('失败！'+info);}}});
						query("");
						var meiyong=1;
						meiyong++;
					}
				},{
					xtype : 'button',
					text : '重置',
					width:80,
					handler : function() {
						formPanel.getForm().reset();
					}
				}]
			});
			function setTimeCond(){
				var starttime = data.data.startTime;
				var endtime = data.data.endTime;
				
				var startYear = "20"+(starttime.year-100).toString();
				var startMonth = starttime.month<10?("0"+(starttime.month+1).toString()):(starttime.month+1).toString();
				var startDate = starttime.date<10?("0"+starttime.date.toString()):starttime.date.toString();
				var starthours = starttime.hours<10?("0"+starttime.hours.toString()):starttime.hours.toString();
				var startminutes = starttime.minutes<10?("0"+starttime.minutes.toString()):starttime.minutes.toString();
				var startseconds = starttime.seconds<10?("0"+starttime.seconds.toString()):starttime.seconds.toString();
				
				
				var endYear = "20"+(endtime.year-100).toString();
				var endMonth = endtime.month<10?("0"+(endtime.month+1).toString()):(endtime.month+1).toString();
				var endDate = endtime.date<10?("0"+endtime.date.toString()):endtime.date.toString();
				var endhours = endtime.hours<10?("0"+endtime.hours.toString()):endtime.hours.toString();
				var endminutes = endtime.minutes<10?("0"+endtime.minutes.toString()):endtime.minutes.toString();
				var endseconds = endtime.seconds<10?("0"+endtime.seconds.toString()):endtime.seconds.toString();
				
				var sDate = startYear+"-"+startMonth+"-"+startDate;
				var sTime = starthours+":"+startminutes+":"+startseconds;
				var eDate = endYear+"-"+endMonth+"-"+endDate;
				var eTime = endhours+":"+endminutes+":"+endseconds;
				
				Ext.getCmp('startDate').setValue(sDate);
				Ext.getCmp('startTime').setValue(sTime);
				Ext.getCmp('endDate').setValue(eDate);
				Ext.getCmp('endTime').setValue(eTime);
			}
			setTimeCond();
			
			function createQueryCond(){
				var queryCond="";
				var temp="";
				var startDate = formPanel.findById('startDate').getValue();	
				var startTime = formPanel.findById('startTime').getValue();	
				var endDate = formPanel.findById('endDate').getValue();			
				var endTime = formPanel.findById('endTime').getValue();
				if(startDate!=""||endDate!=""){
					if (startDate!="") {
						if (startTime != "") {
							start = Date.parse(startDate.format('Y/m/d') + " "
									+ startTime);
							startStr = startDate.format('Y-m-d') + " " + startTime;
						} else {
							start = Date.parse(startDate.format('Y/m/d H:i:s'));
							startStr = startDate.format('Y-m-d H:i:s');
						}
					}
					if (endDate!="") {
						if (endTime != "") {
							end = Date.parse(endDate.format('Y/m/d') + " " + endTime);
							endStr = endDate.format('Y-m-d') + " " + endTime;
						} else {
							end = Date.parse(endDate.format('Y/m/d H:i:s'));
							endStr = endDate.format('Y-m-d H:i:s');
						}
					}
					var columnName = indicatorType==0?"create_time":"createTime";
					if(startDate!=""&&endDate!=""){
						if(start>end){
							Ext.Msg.alert("开始日期晚于结束日期,使用默认日期查询!");
						}else{
							temp = columnName+" >= '" + startStr +"' and "+columnName+" <= '"+endStr+"'";
						}
					}else{
						if(startDate!=""){
							temp = columnName+" >= '" + startStr +"'";
						}
						if(endDate!=""){
							temp = columnName+" <= '" + endStr +"'";
						}
					}
				}
				var cond = formPanel.findById('cond').getValue();
				if(temp==""){
					return cond;
				}else{
					if(cond!=""){
						queryCond = cond+" and "+temp;
					}else{
						queryCond = temp;
					}
				}
				return queryCond;
			}
			
			var grid = null;
			//如果是log类型的
			if(indicatorType==0){				
				//表格部分
				 grid = new Ext.grid.GridPanel({
					region: 'south',
			        store: store,
			        columns: [
			            {header: "user_id", width: 75,  dataIndex: 'userId'},
			            {header: "user_name", width: 115,  dataIndex: 'userName'},
			            {header: "game_domain", width: 101,   dataIndex: 'gameDomain'},
			            {header: "create_time", width: 139,  dataIndex: 'createTime'},
			            {header: "reason", width:50,  dataIndex: 'reason'},
						{header: "log_type", width: 60,  dataIndex: 'logType'},
						{header: "extension", width: 90,   dataIndex: 'extension'},
						{header: "properties", width: 75,   dataIndex: 'properties'}
			        ],
			        stripeRows: true,
			        autoExpandColumn: 7,
			        height:310,
			        width:1000,
			        loadMask:true,
			        title:'查询数据',
			        bbar:[{   
		            	xtype : 'button',
						text : '上一页',
						width:80,
						handler : function() {
							var page = Ext.getCmp('page').getValue();
	    					var pageNum = parseInt(page, 10);
	    					pageNum -=1;
	    					if(pageNum<1){
	    						pageNum=1;
	    					}
	    					Ext.getCmp('page').setValue(pageNum);
	    					pageUpDown(0);
						},
						scope : this    
		                },{
				        	xtype:'textfield',
				        	id:'page',
				        	readOnly:true,
		                	width:30,
		                	value:1
		                },{   
		                	xtype : 'button',
		    				text : '下一页',
		    				width:80,
		    				handler : function() {
		    					var page = Ext.getCmp('page').getValue();
		    					var pageNum = parseInt(page, 10);
		    					pageNum+=1;
		    					Ext.getCmp('page').setValue(pageNum);
		    					pageUpDown(1);
		    				},
		    				scope : this    
		                    }] 

			    });
				 grid.addListener('cellclick', function(grid, rowIndex, columnIndex, e){
						var record = grid.getStore().getAt(rowIndex);
						
						var showDetailForm =  new Ext.form.FormPanel({
					        labelWidth: 80,
					        frame: true,
					        layout:'form',
					        items: [{
					        	xtype:'textfield',
					        	fieldLabel:'userId',
			                	width:380,
								readOnly:true,
			                	value:record.get('userId')
			                	},{
						        	xtype:'textfield',
						        	fieldLabel:'userName',
				                	width:380,
									readOnly:true,
				                	value:record.get('userName')
				                },{
						        	xtype:'textfield',
						        	fieldLabel:'gameDomain',
				                	width:380,
									readOnly:true,
				                	value:record.get('gameDomain')
				                },{
						        	xtype:'textfield',
						        	fieldLabel:'createTime',
				                	width:380,
									readOnly:true,
				                	value:record.get('createTime')
				                },{
						        	xtype:'textfield',
						        	fieldLabel:'reason',
				                	width:380,
									readOnly:true,
				                	value:record.get('reason')
				                },{
						        	xtype:'textfield',
						        	fieldLabel:'logType',
				                	width:380,
									readOnly:true,
				                	value:record.get('logType')
				                },{
						        	xtype:'textfield',
						        	fieldLabel:'extension',
				                	width:380,
									readOnly:true,
				                	value:record.get('extension')
				                },{
						        	xtype:'textarea',
						        	fieldLabel:'properties',
				                	width:380,
				                	height:230,
									readOnly:true,
				                	value:record.get('properties')
				                }]
					    });
						var detailWin = new Ext.Window({
					        layout: 'fit',
					        width: 500,
					        title: 'form',
					        height: 500,
					        closeAction: 'close',
					        items: [showDetailForm],
					        buttons: [{
					            text:'Close',
					            handler: function(){
					            	detailWin.close();
					         		}
					        }]
					    });
						detailWin.show();
						});
			}else{//如果是数据类型的
				 grid = new Ext.grid.GridPanel({
					region: 'south',
			        store: store,
			        columns: [		     
			            {header: "statusCode", width: 75,  dataIndex: 'statusCode'},
			            {header: "spendTime", width: 75,  dataIndex: 'spendTime'},
			            {header: "header", width: 600,   dataIndex: 'header'},
			            {header: "success", width: 75,  dataIndex: 'success'},
			            {header: "createTime", width:75,  dataIndex: 'createTime'}
			        ],
			        stripeRows: true,
			        autoExpandColumn: 4,
			        height:310,
			        width:1000,
			        loadMask:true,
			        title:'查询数据',
			        bbar:[{   
		            	xtype : 'button',
						text : '上一页',
						width:80,
						handler : function() {
							var page = Ext.getCmp('page').getValue();
	    					var pageNum = parseInt(page, 10);
	    					pageNum -=1;
	    					if(pageNum<1){
	    						pageNum=1;
	    					}
	    					Ext.getCmp('page').setValue(pageNum);
	    					pageUpDown(0);
						},
						scope : this    
		                },{
				        	xtype:'textfield',
				        	id:'page',
				        	readOnly:true,
		                	width:30,
		                	value:1
		                },{   
		                	xtype : 'button',
		    				text : '下一页',
		    				width:80,
		    				handler : function() {
		    					var page = Ext.getCmp('page').getValue();
		    					var pageNum = parseInt(page, 10);
		    					pageNum+=1;
		    					Ext.getCmp('page').setValue(pageNum);
		    					pageUpDown(1);
		    				},
		    				scope : this    
		                    }]
			    });
				 grid.addListener('cellclick', function(grid, rowIndex, columnIndex, e){
						var record = grid.getStore().getAt(rowIndex);
						var showDetailForm =  new Ext.form.FormPanel({
					        labelWidth: 80,
					        frame: true,
					        layout:'form',
					        items: [{
					        	xtype:'textfield',
					        	fieldLabel:'statusCode',
			                	width:380,
								readOnly:true,
			                	value:record.get('statusCode')
			                	},{
						        	xtype:'textfield',
						        	fieldLabel:'spendTime',
				                	width:380,
									readOnly:true,
				                	value:record.get('spendTime')
				                },{
						        	xtype:'textfield',
						        	fieldLabel:'content',
				                	width:380,
									readOnly:true,
				                	value:record.get('content')
				                },{
						        	xtype:'textfield',
						        	fieldLabel:'createTime',
				                	width:380,
									readOnly:true,
				                	value:record.get('createTime')
				                },{
						        	xtype:'textarea',
						        	fieldLabel:'header',
				                	width:380,
				                	height:230,
									readOnly:true,
				                	value:record.get('header')
				                }]
					    });
						var detailWin = new Ext.Window({
					        layout: 'fit',
					        width: 500,
					        title: 'form',
					        height: 420,
					        closeAction: 'close',
					        items: [showDetailForm],
					        buttons: [{
					            text:'Close',
					            handler: function(){
					            	detailWin.close();
					         		}
					        }]
					    });
						detailWin.show();
						});
				 
			}
			var button = new Ext.Button({
				id : "AbortButton",
				text : "停止加载",
				tooltip : "停止加载数据",
				hidden : true, // 是否隐藏

				handler : function() {
					Ext.Ajax.abort();
					grid.loadMask.hide();
					Ext.getCmp("AbortButton").hide();//隐藏按钮
				}
			});
							
			// 窗口部分
			win = new Ext.Window({
				title : '异常日志查询窗口',
				layout : 'border',
				width : 1000,
				height : 540,
				closeAction : 'close',
				items : [ formPanel,grid ],
				buttons: [button,{text: 'Close',
					handler: function(){
					win.close();}
				}]

			});
			win.show();

			//上下翻页的处理
			var record;
			function pageUpDown(updown){
				if(store.getCount ()>0){
					var maxRecord = store.getCount ()-1;
					if(updown==0){
						record = store.getAt(0);
					}else{
						record = store.getAt(maxRecord);
					}
					record.set("updown",updown);
				}else{
					var lastId = record.get('id');
					var lasdIdNum = parseInt(lastId, 10);
					lasdIdNum++;
					record.set("id",lasdIdNum);
					record.set("updown",updown);
				}
				var page = Ext.getCmp('page').getValue();
				var pageNum = parseInt(page, 10);
				if(pageNum==1){
					var lastRecord = ""; 
				}else{
					var lastRecord = Ext.encode(record.data);
				}
				query(lastRecord);
				var meiyong = 0;
				meiyong++;
			}
			//这里用来将附加参数添加到store的url中
			store.on('beforeload', function (store, options) {
				var queryCond=createQueryCond();
				var jexlCond = Ext.getCmp('jexlCond').getValue();
				var temp = new Object();
				temp.queryCond=queryCond;
				temp.jexlCond = jexlCond;
				var params = store.baseParams; 
				Ext.apply(params,temp); 
				
				//显示停止加载按钮
				Ext.getCmp("AbortButton").show();//显示按钮
		    });
			store.on('load', function (store, options) {
				//隐藏停止加载按钮
				Ext.getCmp("AbortButton").hide();//隐藏按钮
		    });
			//加载数据
			store.load();
			//查询数据
			function query(lrecord){
				var queryCond=createQueryCond();
				var jexlCond = Ext.getCmp('jexlCond').getValue();
				grid.loadMask.show();
				Ext.getCmp("AbortButton").show();//显示按钮
				Ext.Ajax.request({
					url : 'indicator/getErrorLog?indicatorId='+indicatorId,
					params : { queryCond:queryCond,lastRecord:lrecord,jexlCond:jexlCond},
					method : 'POST',
					success : function(response) {
						var meiyong=1;
						meiyong++;
						var result = Ext.util.JSON.decode(response.responseText);
						if(result.success==1){
							store.loadData(result);
						}else{
							alertError(result.info);
						}
						grid.loadMask.hide();
						Ext.getCmp("AbortButton").hide();//隐藏按钮
					},
					failure : function(response) {
						var meiyong=1;
						meiyong++;
						var result = Ext.util.JSON.decode(response.responseText);
						Ext.Msg.alert("失败"+result.info);
						grid.loadMask.hide();
						Ext.getCmp("AbortButton").hide();//隐藏按钮
					}
				});			
			}
		},"json");
	};
	//异常处理窗口
	processException = function(moduleId,indicatorId,indicatorName) {
		var processExceptionForm = new Ext.form.FormPanel({
			labelWidth:80,
    		labelAlign:'right',
    		defaultType: 'textfield',
    		bodyStyle:'padding:5px 30px 0',
    		formId:'indicatorForm',
    		autoHeight:true,width:430,border:false,
    		defaults:{width:180},
    		items:[
    			{fieldLabel:'异常处理日志',xtype:'textarea',name:'logText',width:260,height:160,allowBlank:false,msgTarget:'side'}
    		]
		});
		var processExceptionWindow = new Ext.Window({
			
			title:'处理异常：'+indicatorName,modal:true,layout:'fit',width:450,
			items:[processExceptionForm],
			buttons:[{text:'确定',listeners:{'click':function(){
				
				processExceptionForm.getForm().submit({
					url:"indicator/process?indicatorId="+indicatorId,
					success:function(){
						reloadModuleList();
						loadIndicatorList(moduleId);
						processExceptionWindow.close();
					},failure:function(f,a) {
						if(a.result) {
							alertError(a.result.info);
						}
					}
				});
				
				
			}}},{text:'取消',listeners:{'click':function(){
				processExceptionWindow.close();
			}}}]
		});
		
		processExceptionWindow.show();
		
	};
	
	function setMonitorMainPanel(panel) {
		if(panel==currMainPanel) return;
		monitorMainPanel.removeAll(panel==indicatorGridPanel);
		monitorMainPanel.add(panel);
		monitorMainPanel.doLayout();
		currMainPanel = panel;
	}
	
	setMonitorMainPanel(indicatorGridPanel);
	function loadIndicatorList(moduleId) {
		indicatorStore.load({'params':{'moduleId':moduleId}});
	}
	
	showIndicatorDetail = function(indicatorId,indicatorName) {
		
		var charturl = "/indicator/chart?id="+indicatorId;
		
		var detailChart = new Ext.Panel({
			border:false,
			html:"<div style='margin:10px;'><img id='chartLoadingImg' src='/images/loading.gif'/><img style='display:none;' id='indicatorChart' src='/images/loading.gif'/></div>"
		});
		
		var queryData = undefined;
		
		var currurl = charturl;
		
		var detailPanel = new Ext.Panel({
			layout:'fit',title:'指标：'+indicatorName,border:false,
			tbar:[
				{text:'查询',listeners:{'click':function(){
					
					var myDate = new Date();
					var strDate = ""+myDate.getFullYear()+"-"+getXX(myDate.getMonth()+1)+"-"+getXX(myDate.getDate());
					var endTime = getXX(myDate.getHours())+getXX(myDate.getMinutes());
					var detailQueryForm = new Ext.form.FormPanel({
						labelWidth:80,
			    		labelAlign:'right',
			    		defaultType: 'textfield',
			    		bodyStyle:'padding:5px 30px 0',
			    		formId:'indicatorForm',
			    		autoHeight:true,width:380,border:false,
			    		defaults:{width:180},
			    		items:[
			    			{fieldLabel:'日期',xtype:'datefield',id:'chartDate',name:'d',allowBlank:false,msgTarget:'side',format:'Y-m-d',value:queryData?queryData.charDate:strDate},
			    			{xtype:'panel',fieldLabel:'开始时间',width:250,border:false,layout:'column',items:[
			    				new Ext.form.TextField({id:'chartStartTime',name:'start',width:80,regex:/^[0-2][0-9][0-5][0-9]$/,regexText:'时间格式不正确',allowBlank:false,value:queryData?queryData.startTime:"0000"}),
			    				new Ext.form.Label({text:'输入四位数字(例如:0805)',style:'padding-top:3px;margin-left:5px;'})
			    			]},
			    			{xtype:'panel',fieldLabel:'结束时间',width:250,border:false,layout:'column',items:[
			    				new Ext.form.TextField({id:'chartEndTime',name:'end',width:80,regex:/^[0-2][0-9][0-5][0-9]$/,regexText:'时间格式不正确',allowBlank:false,value:"2400"}),
			    				new Ext.form.Label({text:'输入四位数字(例如:1409)',style:'padding-top:3px;margin-left:5px;'})
			    			]}
			    		]
					});
					
					var detailQueryWindow = new Ext.Window({
						title:'指标查询',modal:true,layout:'fit',width:420,
						items:[detailQueryForm],
						buttons:[{text:'确定',listeners:{'click':function(){
							if(detailQueryForm.getForm().isValid()) {
			    				var items = detailQueryForm.getForm().items;
			    				var myDate = items.get('chartDate').getValue();
			    				var strDate = ""+myDate.getFullYear()+getXX(myDate.getMonth()+1)+getXX(myDate.getDate());
			    				var start = items.get('chartStartTime').getValue();
			    				var end = items.get('chartEndTime').getValue();
			    				queryData = new Object();
			    				queryData.charDate = myDate;
			    				queryData.startTime = start;
			    				queryData.endTime = end;
			    				currurl = charturl+"&d="+strDate+"&start="+start+"&end="+end;
			    				$("#chartLoadingImg").show();
								$("#indicatorChart").hide();
			    				$("#indicatorChart").attr('src',currurl+"&tt="+Math.random());
			    				detailQueryWindow.close();
							}
						}}},{text:'取消',listeners:{'click':function(){
			    			detailQueryWindow.close();
						}}}]
					});
					detailQueryWindow.show();
				}}},
				{text:'刷新',listeners:{'click':function(){
					$("#chartLoadingImg").show();
					$("#indicatorChart").hide();
					$("#indicatorChart").attr('src',currurl+"&tt="+Math.random());
				}}},
				{text:'返回',listeners:{'click':function(){
					setMonitorMainPanel(indicatorGridPanel);
					returnButton.setVisible(false);
				}}}
			],
			items:[detailChart]
		});
		
		setMonitorMainPanel(detailPanel);
		$("#indicatorChart").load(function(){
			$("#indicatorChart").show();
			$("#chartLoadingImg").hide();
		});
		$("#indicatorChart").attr('src',currurl+"&tt="+Math.random());
		returnButton.setVisible(true);
	};
	function reloadModuleList() {
		$.post("module/list0",null,function(result){
			if(result.success) {
				var moduleRoot = new Ext.tree.TreeNode({});
				var data = result.data;
				for(var i=0;i<data.length;i++) {
					var statusHtml = "<span style='color:#";
					if(data[i].moduleStatus==1) {
						statusHtml += "FFE47F";
					} else if(data[i].moduleStatus==2) {
						statusHtml += "e00";
					} else {
						statusHtml += "008000";
					}
					statusHtml += ";'>●</span>";
					
					var moduleNode = new Ext.tree.TreeNode({
						text:statusHtml+data[i].moduleName,
						userNodeObject:data[i]
					});
					moduleRoot.appendChild(moduleNode);
				};
				moduleListPanel.setRootNode(moduleRoot);
				moduleRoot.expand(true);			
				
			} else {
				alertError(result.info);
			}
		},"json");
	}
		
	reloadModuleList();
	
	return new Ext.Panel({
		title:'实时监控',layout:'border',
		tbar:toolbar,
		items:[moduleListPanel,monitorMainPanel]
	});
}



