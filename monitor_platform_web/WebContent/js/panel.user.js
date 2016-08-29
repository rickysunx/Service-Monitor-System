/**
 * 创建用户信息编辑窗口
 */
function createUserEditorWindow(store,data) {
	var form = new Ext.form.FormPanel({
			labelWidth:80,
    		labelAlign:'right',
    		defaultType: 'textfield',
    		bodyStyle:'padding:5px 30px 0',
    		formId:'userForm',
    		autoHeight:true,width:350,border:false,
			items:[
			    {fieldLabel:'id',name:'userId',width:180,value:(data?data.userId:undefined),hidden:true},
			    {fieldLabel:'登录名',name:'logName',width:180,value:(data?data.logName:undefined),
			    	allowBlank:false,msgTarget:'side'},
			    {fieldLabel:'用户名',name:'userName',width:180,value:(data?data.userName:undefined),
			    	allowBlank:false,msgTarget:'side'},
			    {fieldLabel:'手机',name:'mobile',width:180,value:(data?data.mobile:undefined),
			    	allowBlank:true,msgTarget:'side',regex:/^1[3|4|5|8]\d{9,9}$/,regexText:'手机格式不正确'},
			    {fieldLabel:'邮箱',name:'email',width:180,value:(data?data.email:undefined),
			    	allowBlank:true,msgTarget:'side',
			    	regex:/^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9\-]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/,
			    	regexText:'邮箱格式不正确'}
			],
			buttons:[{text:'保存',listeners:{'click':function(){
				form.getForm().submit({
					url:data?"user/update":"user/insert",
					success:function(form,action){
						Ext.Msg.alert('用户增加成功','初始密码为：'+action.result.data.initPass,function(){
							store.load();
							editorWindow.close();
						});
					},failure:function(f,a) {
						alertError(a.result.info);
					}
				});
			}}},{text:'取消',listeners:{'click':function(){
				editorWindow.close();
			}}}]
		});
	
	var editorWindow = new Ext.Window({
		title:'编辑用户',
		modal:true,layout:'fit',
		items:[form]
	});
	return editorWindow;
}

/**
 * 创建用户管理面板
 */
function createUserPanel() {
	var store = new Ext.data.JsonStore({
		url:'user/list',root:'data',
		fields:['userId','logName','userName','mobile','email','userType'/*,'userTypeName'*/]
	});
	var sm = new Ext.grid.CheckboxSelectionModel();
	var userGrid = new Ext.grid.GridPanel({
		'store':store,
		'columns':[
		    sm,
		    {header:'登录名',dataIndex:'logName'},
		    {header:'姓名',dataIndex:'userName'},
		    {header:'手机',dataIndex:'mobile'},
		    {header:'邮箱',dataIndex:'email'}
		],
		sm: sm,
		'border':false
	});
	store.load();
	return new Ext.Panel({
		layout:'fit',
		title:'用户设置',
		tbar:[
		    {'text':'新增',listeners:{'click':function(){
		    	var editorWindow = createUserEditorWindow(store);
		    	editorWindow.show();
		    }}},
		    {'text':'删除',listeners:{'click':function(){
		    	var selCount = userGrid.getSelectionModel().getCount();
		    	if(selCount==0) {
		    		Ext.MessageBox.alert('请选择','请选择要删除的用户');
		    		return;
		    	}
		    	Ext.MessageBox.confirm('确认删除','真的要删除这些用户吗？',function(result){
		    		if(result=='yes') {
		    			var selData = userGrid.getSelectionModel().getSelections();
		    			var idArray = new Array();
		    			for(var i=0;i<selData.length;i++) {
		    				idArray.push(selData[i].data.userId);
		    			}
		    			$.post("user/del",{id:idArray},function(data){
	    					if(data.success==1) {
	    						store.load();
	    					} else {
	    						alertError(data.info);
	    					}
	    				},"json");
		    		}
		    	});
		    }}},
		    {'text':'编辑',listeners:{'click':function(){
		    	var selCount = userGrid.getSelectionModel().getCount();
		    	if(selCount==0) {
		    		Ext.MessageBox.alert('请选择','请选择要编辑的用户');
		    		return;
		    	}
		    	var selData = userGrid.getSelectionModel().getSelected();
		    	var editorWindow = createUserEditorWindow(store,selData.data);
		    	editorWindow.show();
		    }}},
		    {'text':'刷新',listeners:{'click':function(){
		    	store.load();
		    }}}
		],
		items:[userGrid]
	});
}