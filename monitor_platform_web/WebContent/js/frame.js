var mainPanel = null;

function setMainPanel(panel) {
	mainPanel.removeAll(true);
	mainPanel.add(panel);
	mainPanel.doLayout();
}

Ext.onReady(function(){
	Ext.QuickTips.init();

	//功能树
	var funcTree = new Ext.tree.TreePanel({
		region:'west',
		title:'功能',
		split:true,
		collapsible: true,
		width:150,
		root:new Ext.tree.AsyncTreeNode({
			text:'监控平台',
			children:[{
				text:'欢迎页',leaf:true,listeners:{'click':function(){
					setMainPanel(createWelcomePanel());
				}}
			},{
				text:'基础设置',children:[{
					text:'用户设置',leaf:true,listeners:{'click':function(){
						setMainPanel(createUserPanel());
					}}
				},{
					text:'指标设置',leaf:true,listeners:{'click':function(){
						setMainPanel(createIndicatorPanel());
					}}
				}]
			},{
				text:'实时监控',leaf:true,listeners:{'click':function(){
					setMainPanel(createMonitorPanel());
				}}
			},{
				text:'报表查看',leaf:true,listeners:{'click':function(){
					setMainPanel(createReportPanel());
				}}
			},{
				text:'修改密码',leaf:true,listeners:{'click':function(){
					var changePassForm = new Ext.form.FormPanel({
						labelWidth:80,
			    		labelAlign:'right',
			    		defaultType: 'textfield',
			    		bodyStyle:'padding:5px 30px 0',
			    		formId:'indicatorForm',
			    		autoHeight:true,width:380,border:false,
			    		defaults:{width:180},
			    		items:[
			    			{fieldLabel:'旧密码',xtype:'textfield',name:'oldPassWord',inputType:'password',allowBlank:false,msgTarget:'side'},
			    			{fieldLabel:'新密码',xtype:'textfield',name:'newPassWord',inputType:'password',allowBlank:false,msgTarget:'side'},
			    			{fieldLabel:'再次输入',xtype:'textfield',name:'newPassWord0',inputType:'password',allowBlank:false,msgTarget:'side'}
			    		]
					});
					
					var changePassWindow = new Ext.Window({
						title:'修改密码',modal:true,layout:'fit',width:420,
						items:[changePassForm],
						buttons:[{text:'确定',listeners:{'click':function(){
							changePassForm.getForm().submit({
								url:"/user/changePass",
								success:function(){
									Ext.Msg.alert('成功', '密码修改成功');
									changePassWindow.close();
								},failure:function(f,a) {
									if(a.result) {
										alertError(a.result.info);
									}
								}
							});
						}}},{text:'取消',listeners:{'click':function(){
							changePassWindow.close();
						}}}]
					});
					changePassWindow.show();
				}}
			},{
				text:'退出系统',leaf:true,listeners:{'click':function(){
					window.location.href='exit';
				}}
			}]
		})
	});
	
	//主面板
	mainPanel = new Ext.Panel({
		region:'center',
		layout:'fit',
		border:false
	});
	
	//主视口
	var viewport = new Ext.Viewport({
		layout:'border',
		items:[funcTree,mainPanel]
	});
	setMainPanel(createWelcomePanel());
	viewport.render(document.body);
	funcTree.getRootNode().expand(true);
});



