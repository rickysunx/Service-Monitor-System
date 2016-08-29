function submitForm() {
	$.post("login/check",$("#loginForm").serialize(),function(data){
		if(data.success) {
			window.location.href='/';
		} else {
			alertError(data.info);
		}
	},"json");
}

Ext.onReady(function(){
	Ext.QuickTips.init();
	new Ext.FormPanel({
		renderTo:'loginMainFrame',
		width:300,
		frame:true,
		title:'监控系统 - 登录',
		labelWidth:80,
		labelAlign:'right',
		defaultType: 'textfield',
		bodyStyle:'padding:5px 5px 0',
		formId:'loginForm',
		items:[
		    {fieldLabel:'用户名',name:'userName',allowBlank:false,width:150},
		    {fieldLabel:'密码',name:'passWord',allowBlank:false,inputType:'password',width:150,
		    	'enableKeyEvents':true,listeners:{'keydown':function(obj,event){
		    	if(event.keyCode==13) {
		    		submitForm();
		    	}
		    }}}
		],
		buttons:[{text:'登录',listeners:{'click':function(obj){
			submitForm();
		}}},{text:'忘记密码'}]
	});
});
