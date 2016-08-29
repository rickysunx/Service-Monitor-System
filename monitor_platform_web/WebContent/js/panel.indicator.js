/**
 * 创建指标管理面板
 */
function createIndicatorPanel() {
	
	/**
	 * 创建模块编辑窗口
	 */
	function createModuleWindow(data) {
		
		var alertEnabledStore = new Ext.data.SimpleStore({
			fields:['id','text'],
			data:[[1,'启用'],[0,'不启用']]
		});
		
		var alertEnabledComboBox = new Ext.form.ComboBox({
			fieldLabel:'启用报警',name:'alertEnabledName',width:140,mode:'local',editable:false,triggerAction:'all',
			store:alertEnabledStore,displayField:'text',valueField:'id',hiddenName:'alertEnabled',allowBlank:false,msgTarget:'side'
		});
		
		if(data) {
			alertEnabledComboBox.setValue(data.alertEnabled);
		}
		
		var moduleForm = new Ext.form.FormPanel({
			labelWidth:90,
    		labelAlign:'right',
    		defaultType: 'textfield',
    		bodyStyle:'padding:5px 30px 0',
    		formId:'moduleForm',
    		autoHeight:true,width:350,border:false,
    		items:[
    			{name:'moduleId',value:data?data.moduleId:undefined,hidden:true},
    			{fieldLabel:'模块名称',name:'moduleName',width:140,allowBlank:false,msgTarget:'side',value:data?data.moduleName:undefined},
    			{fieldLabel:'短信组别',name:'smsGroup',width:140,allowBlank:false,msgTarget:'side',value:data?data.smsGroup:undefined},
    			{fieldLabel:'短信类别',name:'smsType',width:140,allowBlank:false,msgTarget:'side',value:data?data.smsType:undefined},
    			{fieldLabel:'报警周期(分钟)',name:'alertTime',width:140,allowBlank:false,msgTarget:'side',value:data?data.alertTime:undefined},
    			alertEnabledComboBox
    		],
    		buttons:[{text:'确定',listeners:{'click':function(){
    			moduleForm.getForm().submit({
					url:data?"module/update":"module/insert",
					success:function(){
						reloadIndiTreePanel();
						editorWindow.close();
					},failure:function(f,a) {
						if(a.result) {
							alertError(a.result.info);
						}
					}
				});
    		}}},{text:'取消',listeners:{'click':function(){
    			editorWindow.close();
    		}}}]
		});
		
		var editorWindow = new Ext.Window({
			title:'模块编辑',modal:true,layout:'fit',width:350,
			items:[moduleForm]
		});
		
		return editorWindow;
	}
	
	
	
	/**
	 * 创建指标编辑
	 */
	function createIndicatorWindow(data,moduleId) {
		
		var tableStore = new Ext.data.JsonStore({
			url:'indicator/tables',root:'data',
			fields:['tableName'],
			listeners:{'load':function(){
				if(data) {
					tableNameComboBox.setValue(data.tableName);
				}
			}}
		});
		
		var tableNameComboBox = new Ext.form.ComboBox({
			fieldLabel:'监控表名',name:'tableName',width:180,mode:'local',editable:false,triggerAction:'all',
			store:tableStore,displayField:'tableName',allowBlank:false,msgTarget:'side'
		});
		
		tableStore.load();
		
		var errorTypeStore = new Ext.data.SimpleStore({
			fields:['id','value'],
			data:[
				[1,'出错数'],
				[2,'出错率']
			]
		});
		
		var operTypeStore = new Ext.data.SimpleStore({
			fields:['value'],
			data:[['>'],['>='],['<'],['<=']]
		});
		
		var errorTypeCombo = new Ext.form.ComboBox({
			name:'errorTypeName',width:65,mode:'local',editable:false,triggerAction:'all',store:errorTypeStore,
			displayField:'value',valueField:'id',hiddenName:'errorType',allowBlank:false
		});
		
		var warnOperTypeCombo = new Ext.form.ComboBox({
			name:'warnOperType',width:50,mode:'local',editable:false,triggerAction:'all',store:operTypeStore,
			displayField:'value',allowBlank:false
		});
		
		var alertOperTypeCombo = new Ext.form.ComboBox({
			name:'alertOperType',width:50,mode:'local',editable:false,triggerAction:'all',store:operTypeStore,
			displayField:'value',allowBlank:false
		});
		
		var enabledStore = new Ext.data.SimpleStore({
			fields:['id','text'],
			data:[
				[1,'启用'],
				[0,'未启用']
			]
		});
		
		var alertEnabledComboBox = new Ext.form.ComboBox({
			fieldLabel:'启用报警',name:'alertEnabledName',width:80,mode:'local',editable:false,triggerAction:'all',
			store:enabledStore,displayField:'text',valueField:'id',hiddenName:'alertEnabled',allowBlank:false,msgTarget:'side'
		});
		
		var enabledComboBox = new Ext.form.ComboBox({
			fieldLabel:'启用监控',name:'enabledName',width:80,mode:'local',editable:false,triggerAction:'all',
			store:enabledStore,displayField:'text',valueField:'id',hiddenName:'enabled',allowBlank:false,msgTarget:'side'
		});
		
		
		if(data) {
			errorTypeCombo.setValue(data.errorType);
			warnOperTypeCombo.setValue(data.warnOperType);
			alertOperTypeCombo.setValue(data.alertOperType);
			alertEnabledComboBox.setValue(data.alertEnabled);
			enabledComboBox.setValue(data.enabled);
		}
		
		var indicatorForm = new Ext.form.FormPanel({
			labelWidth:90,
    		labelAlign:'right',
    		defaultType: 'textfield',
    		bodyStyle:'padding:5px 30px 0',
    		formId:'indicatorForm',
    		autoHeight:true,width:430,border:false,
    		defaults:{},
    		items:[
    			{name:'indicatorId',value:data?data.indicatorId:undefined,hidden:true},
    			{name:'moduleId',value:data?data.moduleId:moduleId,hidden:true},
    			{name:'indicatorType',value:0,hidden:true},
    			{fieldLabel:'指标名称',width:180,name:'indicatorName',allowBlank:false,value:data?data.indicatorName:undefined,msgTarget:'side'},
    			tableNameComboBox,
    			{fieldLabel:'采样间隔(秒)',width:50,name:'sampleRate',allowBlank:false,regex:/^[0-9]*$/,regexText:'请输入数字',msgTarget:'side',value:data?data.sampleRate:undefined},
    			{fieldLabel:'过滤条件',width:200,id:'filterCond',xtype:'textarea',name:'filterCond',allowBlank:false,msgTarget:'side',value:data?data.filterCond:undefined},
    			{fieldLabel:'出错条件',width:200,id:'errorCond',xtype:'textarea',name:'errorCond',allowBlank:false,msgTarget:'side',value:data?data.errorCond:undefined},
    			{xtype:'panel',fieldLabel:'指标值',width:250,border:false,layout:'column',items:[
    				new Ext.form.Label({text:'统计',style:'padding-top:3px;margin-right:5px;'}),
    				new Ext.form.TextField({name:'statTime',allowBlank:false,regex:/^[0-9]*$/,regexText:'请输入数字',width:40,style:'margin-right:5px;',value:data?data.statTime:undefined}),
    				new Ext.form.Label({text:'秒钟内的',style:'padding-top:3px;margin-right:5px;'}),
    				errorTypeCombo
    			]},
    			{xtype:'panel',fieldLabel:'预警条件',width:250,border:false,layout:'column',items:[
    				new Ext.form.Label({text:'指标值',style:'padding-top:3px;margin-right:5px;'}),
    				warnOperTypeCombo,
    				new Ext.form.TextField({name:'warnValue',width:40,style:'margin-left:5px;',regex:/^[0-9]*$/,regexText:'请输入数字',allowBlank:false,value:data?data.warnValue:undefined})
    			]},
    			{xtype:'panel',fieldLabel:'报警条件',width:250,border:false,layout:'column',items:[
    				new Ext.form.Label({text:'指标值',style:'padding-top:3px;margin-right:5px;'}),
    				alertOperTypeCombo,
    				new Ext.form.TextField({name:'alertValue',width:40,style:'margin-left:5px;',regex:/^[0-9]*$/,regexText:'请输入数字',allowBlank:false,value:data?data.alertValue:undefined}),
    				new Ext.form.Label({text:'下限值',style:'padding-top:3px;margin:0 0 0 5px;'}),
    				new Ext.form.TextField({name:'minAlertValue',width:40,style:'margin-left:5px;',regex:/^[0-9]*$/,regexText:'请输入数字',allowBlank:false,value:data?data.minAlertValue:0})
    			]},alertEnabledComboBox,enabledComboBox
    		],
    		buttons:[{text:'测试',listeners:{'click':function(){
    			var myitems = indicatorForm.getForm().items;
    			var filterCond = myitems.get('filterCond').getValue();
    			var errorCond = myitems.get('errorCond').getValue();
    			
    			var testForm = new Ext.form.FormPanel({
    				labelWidth:80,
		    		labelAlign:'right',
		    		defaultType: 'textfield',
		    		bodyStyle:'padding:5px 30px 0',
		    		formId:'testForm',
		    		autoHeight:true,width:430,border:false,
		    		defaults:{width:180},
		    		items:[
		    			{name:'filterCond',value:filterCond,hidden:true},
		    			{name:'errorCond',value:errorCond,hidden:true},
		    			{fieldLabel:'粘贴测试日志',xtype:'textarea',name:'msg',allowBlank:false,msgTarget:'side'}
		    		],
		    		buttons:[{text:'验证',listeners:{'click':function(){
		    			testForm.getForm().submit({
		    				url:"indicator/valid",
		    				success:function(form,action){
		    					Ext.MessageBox.alert("验证结果","过滤条件结果："+action.result.data.filterResult+
		    						"<br>出错条件结果："+action.result.data.errorResult);
		    				},failure:function(form,action){
		    					if(action.result) {
									alertError(action.result.info);
								}
		    				}
		    			});
		    		}}},{text:'取消',listeners:{'click':function(){
		    			testWindow.close();
		    		}}}]
    			});
    			
    			var testWindow = new Ext.Window({
    				title:'测试条件',modal:true,layout:'fit',width:460,
    				items:[testForm]
    			});
    			
    			testWindow.show();
    			
    		}}},{text:'确定',listeners:{'click':function(){
    			indicatorForm.getForm().submit({
					url:data?"indicator/update":"indicator/insert",
					success:function(){
						reloadIndiTreePanel();
						indicatorWindow.close();
					},failure:function(f,a) {
						if(a.result) {
							alertError(a.result.info);
						}
					}
				});
    		}}},{text:'取消',listeners:{'click':function(){
    			indicatorWindow.close();
    		}}}]
		});
		
		var indicatorWindow = new Ext.Window({
			title:'指标编辑',modal:true,layout:'fit',width:460,
			items:[indicatorForm]
		});
		
		return indicatorWindow;
	}
	
	
	function createUrltestWindow(data,moduleId) {
		
		var urlMethodStore = new Ext.data.SimpleStore({
			fields:['value'],
			data:[
				['GET'],
				['POST']
			]
		});
		
		var urlCharsetStore = new Ext.data.SimpleStore({
			fields:['value'],
			data:[
				['AUTO'],
				['UTF-8'],
				['GB2312'],
				['GBK']
			]
		});
		
		var errorTypeStore = new Ext.data.SimpleStore({
			fields:['id','value'],
			data:[
				[1,'出错数'],
				[2,'出错率']
			]
		});
		
		var operTypeStore = new Ext.data.SimpleStore({
			fields:['value'],
			data:[['>'],['>='],['<'],['<=']]
		});
		
		var postDataField = new Ext.form.TextField({fieldLabel:'POST参数',width:300,name:'urlPostData',hidden:true,allowBlank:true,value:data?data.urlPostData:undefined,msgTarget:'side'});
		
		var urlCharsetCombo = new Ext.form.ComboBox({
			fieldLabel:'返回内容编码',name:'urlCharset',width:65,mode:'local',editable:true,triggerAction:'all',store:urlCharsetStore,
			displayField:'value',allowBlank:false
		});
		
		var urlMethodCombo = new Ext.form.ComboBox({
			fieldLabel:'请求方法',name:'urlMethod',width:65,mode:'local',editable:false,triggerAction:'all',store:urlMethodStore,
			displayField:'value',allowBlank:false,listeners:{'select':function(combo,record,index){
				if(index==1) {
					postDataField.setVisible(true);
				} else {
					postDataField.setVisible(false);
				}
			}}
		});
		
		var errorTypeCombo = new Ext.form.ComboBox({
			name:'errorTypeName',width:65,mode:'local',editable:false,triggerAction:'all',store:errorTypeStore,
			displayField:'value',valueField:'id',hiddenName:'errorType',allowBlank:false
		});
		
		var warnOperTypeCombo = new Ext.form.ComboBox({
			name:'warnOperType',width:50,mode:'local',editable:false,triggerAction:'all',store:operTypeStore,
			displayField:'value',allowBlank:false
		});
		
		var alertOperTypeCombo = new Ext.form.ComboBox({
			name:'alertOperType',width:50,mode:'local',editable:false,triggerAction:'all',store:operTypeStore,
			displayField:'value',allowBlank:false
		});
		
		var enabledStore = new Ext.data.SimpleStore({
			fields:['id','text'],
			data:[
				[1,'启用'],
				[0,'未启用']
			]
		});
		
		var alertEnabledComboBox = new Ext.form.ComboBox({
			fieldLabel:'启用报警',name:'alertEnabledName',width:80,mode:'local',editable:false,triggerAction:'all',
			store:enabledStore,displayField:'text',valueField:'id',hiddenName:'alertEnabled',allowBlank:false,msgTarget:'side'
		});
		
		var enabledComboBox = new Ext.form.ComboBox({
			fieldLabel:'启用监控',name:'enabledName',width:80,mode:'local',editable:false,triggerAction:'all',
			store:enabledStore,displayField:'text',valueField:'id',hiddenName:'enabled',allowBlank:false,msgTarget:'side'
		});
		
		
		if(data) {
			urlMethodCombo.setValue(data.urlMethod);
			urlCharsetCombo.setValue(data.urlCharset);
			errorTypeCombo.setValue(data.errorType);
			warnOperTypeCombo.setValue(data.warnOperType);
			alertOperTypeCombo.setValue(data.alertOperType);
			alertEnabledComboBox.setValue(data.alertEnabled);
			enabledComboBox.setValue(data.enabled);
			if(data.urlMethod=="POST") {
				postDataField.setVisible(true);
			}
		}
		
		
		
		var urltestForm = new Ext.form.FormPanel({
			labelWidth:90,
    		labelAlign:'right',
    		defaultType: 'textfield',
    		bodyStyle:'padding:5px 30px 0',
    		formId:'indicatorForm',
    		autoHeight:true,width:460,border:false,
    		defaults:{},
    		items:[
    			{name:'indicatorId',value:data?data.indicatorId:undefined,hidden:true},
    			{name:'moduleId',value:data?data.moduleId:moduleId,hidden:true},
    			{name:'indicatorType',value:1,hidden:true},
    			{fieldLabel:'指标名称',width:180,name:'indicatorName',allowBlank:false,value:data?data.indicatorName:undefined,msgTarget:'side'},
    			{fieldLabel:'采样间隔(秒)',width:50,name:'sampleRate',allowBlank:false,regex:/^[0-9]*$/,regexText:'请输入数字',msgTarget:'side',value:data?data.sampleRate:undefined},
    			urlMethodCombo,
    			{fieldLabel:'URL地址',width:300,name:'urlPage',allowBlank:false,value:data?data.urlPage:undefined,msgTarget:'side'},
    			postDataField,
    			{fieldLabel:'超时时间(毫秒)',width:80,name:'urlTimeout',allowBlank:false,regex:/^[0-9]*$/,regexText:'请输入数字',value:data?data.urlTimeout:undefined,msgTarget:'side'},
    			urlCharsetCombo,
    			{fieldLabel:'出错条件',width:300,id:'errorCond',xtype:'textarea',name:'errorCond',allowBlank:false,msgTarget:'side',value:data?data.errorCond:undefined},
    			{xtype:'panel',fieldLabel:'指标值',width:250,border:false,layout:'column',items:[
    				new Ext.form.Label({text:'统计',style:'padding-top:3px;margin-right:5px;'}),
    				new Ext.form.TextField({name:'statTime',allowBlank:false,regex:/^[0-9]*$/,regexText:'请输入数字',width:40,style:'margin-right:5px;',value:data?data.statTime:undefined}),
    				new Ext.form.Label({text:'秒钟内的',style:'padding-top:3px;margin-right:5px;'}),
    				errorTypeCombo
    			]},
    			{xtype:'panel',fieldLabel:'预警条件',width:250,border:false,layout:'column',items:[
    				new Ext.form.Label({text:'指标值',style:'padding-top:3px;margin-right:5px;'}),
    				warnOperTypeCombo,
    				new Ext.form.TextField({name:'warnValue',width:40,style:'margin-left:5px;',regex:/^[0-9]*$/,regexText:'请输入数字',allowBlank:false,value:data?data.warnValue:undefined})
    			]},
    			{xtype:'panel',fieldLabel:'报警条件',width:250,border:false,layout:'column',items:[
    				new Ext.form.Label({text:'指标值',style:'padding-top:3px;margin-right:5px;'}),
    				alertOperTypeCombo,
    				new Ext.form.TextField({name:'alertValue',width:40,style:'margin-left:5px;',regex:/^[0-9]*$/,regexText:'请输入数字',allowBlank:false,value:data?data.alertValue:undefined}),
    				new Ext.form.Label({text:'下限值',style:'padding-top:3px;margin:0 0 0 5px;'}),
    				new Ext.form.TextField({name:'minAlertValue',width:40,style:'margin-left:5px;',regex:/^[0-9]*$/,regexText:'请输入数字',allowBlank:false,value:data?data.minAlertValue:0})
    			]},alertEnabledComboBox,enabledComboBox
    		],
    		buttons:[
    			{text:'测试',listeners:{'click':function(){
    				var testResultField = new Ext.form.TextArea(
    					{fieldLabel:'处理结果',height:500,width:600,name:'msg',
    					allowBlank:false,msgTarget:'side',value:'正在获取数据，请稍候……'});
    				var testForm = new Ext.form.FormPanel({
	    				labelWidth:80,
			    		labelAlign:'right',
			    		defaultType: 'textfield',
			    		bodyStyle:'padding:5px 30px 0',
			    		formId:'testForm',
			    		autoHeight:true,width:650,border:false,
			    		defaults:{},
			    		items:[testResultField],
			    		buttons:[{text:'关闭',listeners:{'click':function(){
			    			testWindow.close();
			    		}}}]
	    			});
	    			
	    			var testWindow = new Ext.Window({
	    				title:'处理结果',modal:true,layout:'fit',width:800,
	    				items:[testForm]
	    			});
	    			
	    			testWindow.show();
	    			urltestForm.getForm().submit({
						url:"indicator/urltest",
						success:function(f,a){
							testResultField.setValue(a.result.data);
						},failure:function(f,a) {
							if(a.result) {
								testResultField.setValue("出错：\r\n"+a.result.info);
							}
						}
					});
	    		}}},
    			{text:'确定',listeners:{'click':function(){
	    			urltestForm.getForm().submit({
						url:data?"indicator/update":"indicator/insert",
						success:function(){
							reloadIndiTreePanel();
							urltestWindow.close();
						},failure:function(f,a) {
							if(a.result) {
								alertError(a.result.info);
							}
						}
					});
	    		}}},
	    		{text:'取消',listeners:{'click':function(){
	    			urltestWindow.close();
	    		}}}]
		});
		
		var urltestWindow = new Ext.Window({
			title:'指标编辑',modal:true,layout:'fit',width:500,
			items:[urltestForm]
		});
		
		return urltestWindow;
	}
	
	var indiTreePanel = new Ext.tree.TreePanel({
		region:'west',width:150,split:true,border:false,bodyStyle:'border-width:0 1px 0 0',
		rootVisible:false,autoScroll:true,
		root:new Ext.tree.AsyncTreeNode({}),
		listeners:{'click':function(node,event){
			var nodeType = node.attributes.userNodeType;
			var nodeObject = node.attributes.userNodeObject;
			if(nodeType==0) {
				var html = "<div class='module_item'>";
				html += "<div>模块名称："+nodeObject.moduleName+"</div>";
				html += "<div>短信组别："+nodeObject.smsGroup+"</div>";
				html += "<div>短信类别："+nodeObject.smsType+"</div>";
				html += "<div>报警周期："+nodeObject.alertTime+"分钟</div>";
				html += "<div>启用报警："+(nodeObject.alertEnabled==1?'启用':'未启用')+"</div>";
				html += "</div>";
				indiMainPanel.update(html);
			} else if(nodeType==1) {
				if(nodeObject.indicatorType==0) {
					var html = "<div class='module_item'>"
						+"<div>指标名称:"+nodeObject.indicatorName+"</div>"
						+"<div>监控表名:"+nodeObject.tableName+"</div>"
						+"<div>采样间隔:"+nodeObject.sampleRate+"秒</div>"
						+"<div>过滤条件:"+nodeObject.filterCond+"</div>"
						+"<div>出错条件:"+nodeObject.errorCond+"</div>"
						+"<div>指标值：统计"+nodeObject.statTime+"秒内的"+(nodeObject.errorType==1?"出错数":"出错率")+"</div>"
						+"<div>预警条件:指标值"+nodeObject.warnOperType+nodeObject.warnValue+"</div>"
						+"<div>报警条件:指标值"+nodeObject.alertOperType+nodeObject.alertValue+"</div>"
						+"<div>启用报警："+(nodeObject.alertEnabled==1?'启用':'未启用')+"</div>"
						+"<div>启用监控："+(nodeObject.enabled==1?'启用':'未启用')+"</div>"
						+"</div>";
					indiMainPanel.update(html);
				} else {
					var html = "<div class='module_item'>"
						+"<div>指标名称:"+nodeObject.indicatorName+"</div>"
						+"<div>采样间隔:"+nodeObject.sampleRate+"秒</div>"
						+"<div>URL地址:"+nodeObject.urlPage+"</div>"
						+"<div>请求方法:"+nodeObject.urlMethod+"</div>"
						+((nodeObject.urlMethod=="POST")?("<div>POST参数:"+nodeObject.urlPostData):"")
						+"<div>超时时间:"+nodeObject.urlTimeout+"</div>"
						+"<div>返回内容编码:"+nodeObject.urlCharset+"</div>"
						+"<div>出错条件:"+nodeObject.errorCond+"</div>"
						+"<div>指标值：统计"+nodeObject.statTime+"秒内的"+(nodeObject.errorType==1?"出错数":"出错率")+"</div>"
						+"<div>预警条件:指标值"+nodeObject.warnOperType+nodeObject.warnValue+"</div>"
						+"<div>报警条件:指标值"+nodeObject.alertOperType+nodeObject.alertValue+"</div>"
						+"<div>启用报警："+(nodeObject.alertEnabled==1?'启用':'未启用')+"</div>"
						+"<div>启用监控："+(nodeObject.enabled==1?'启用':'未启用')+"</div>"
						+"</div>";
					indiMainPanel.update(html);
				}
			}
		}}
	});
	
	//加载数据
	//userNodeType:0-'module',1-'indicator'
	function reloadIndiTreePanel() {
		$.post("module/list",null,function(result){
			if(result.success) {
				var indiRoot = new Ext.tree.TreeNode({});
				var data = result.data;
				for(var i=0;i<data.length;i++) {
					var moduleNode = new Ext.tree.TreeNode({
						text:data[i].moduleName,
						userNodeType:0,
						userNodeObject:data[i]
					});
					indiRoot.appendChild(moduleNode);
					
					var indicatorList = data[i].indicatorList;
					
					for(var j=0;j<indicatorList.length;j++) {
						moduleNode.appendChild(new Ext.tree.TreeNode({
							text:indicatorList[j].indicatorName,
							userNodeType:1,
							userNodeObject:indicatorList[j]
						}));
					}
				};
				indiTreePanel.setRootNode(indiRoot);
				indiMainPanel.update("");
				indiRoot.expand(true);
			} else {
				alertError(result.info);
			}
		},"json");
	}
	
	var indiMainPanel = new Ext.Panel({
		region:'center',layout:'fit',border:false,bodyStyle:'border-width:0 0 0 1px'
	});
	
	reloadIndiTreePanel();
	
	return new Ext.Panel({
		title:'指标设置',layout:'border',
		tbar:[
			{text:'新增模块',listeners:{'click':function(){
				var moduleWindow = createModuleWindow();
				moduleWindow.show();
			}}},
			{text:'新增日志分析指标',listeners:{'click':function(){
				var selNode = indiTreePanel.getSelectionModel().getSelectedNode();
				if(selNode) {
					var nodeType = selNode.attributes.userNodeType;
					var nodeObject = selNode.attributes.userNodeObject;
					var indicatorWindow = createIndicatorWindow(undefined,nodeObject.moduleId);
					indicatorWindow.show();
				} else {
					alertError('请选择指标所属模块');
				}
			}}},
			{text:'新增主动监控指标',listeners:{'click':function(){
				var selNode = indiTreePanel.getSelectionModel().getSelectedNode();
				if(selNode) {
					var nodeType = selNode.attributes.userNodeType;
					var nodeObject = selNode.attributes.userNodeObject;
					var urltestWindow = createUrltestWindow(undefined,nodeObject.moduleId);
					urltestWindow.show();
				} else {
					alertError('请选择指标所属模块');
				}
			}}},
			{text:'删除',listeners:{'click':function(){
				var selNode = indiTreePanel.getSelectionModel().getSelectedNode();
				if(selNode) {
					var nodeType = selNode.attributes.userNodeType;
					var nodeObject = selNode.attributes.userNodeObject;
					if(nodeType==0) {
						Ext.MessageBox.confirm('确认删除','真的要删除本模块吗？',function(result){
							if(result=='yes') {
								$.post("module/del",{moduleId:nodeObject.moduleId},function(result){
									if(result.success) {
										reloadIndiTreePanel();
									} else {
										alertError(result.info);
									}
								},"json");
							}
						});
					} else if(nodeType==1) {
						Ext.MessageBox.confirm('确认删除','真的要删除本指标吗？',function(result){
							if(result=='yes') {
								$.post("indicator/del",{indicatorId:nodeObject.indicatorId},function(result){
									if(result.success) {
										reloadIndiTreePanel();
									} else {
										alertError(result.info);
									}
								},"json");
							}
						});
					}
				} else {
					alertError('请选择要删除的节点');
				}
			}}},
			{text:'编辑',listeners:{'click':function(){
				var selNode = indiTreePanel.getSelectionModel().getSelectedNode();
				if(selNode) {
					var nodeType = selNode.attributes.userNodeType;
					var nodeObject = selNode.attributes.userNodeObject;
					if(nodeType==0) {
						var moduleWindow = createModuleWindow(nodeObject);
						moduleWindow.show();
					} else if(nodeType==1) {
						if(nodeObject.indicatorType==0) {
							var indicatorWindow = createIndicatorWindow(nodeObject);
							indicatorWindow.show();
						} else if(nodeObject.indicatorType==1) {
							var urltestWindow = createUrltestWindow(nodeObject);
							urltestWindow.show();
						}
						
					}
				} else {
					alertError('请选择要编辑的节点');
				}
			}}},
			{text:'刷新',listeners:{'click':function(){
				reloadIndiTreePanel();
			}}}
		],items:[indiTreePanel,indiMainPanel]
	});
}
