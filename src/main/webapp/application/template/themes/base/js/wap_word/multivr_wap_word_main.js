$(function(){
	//权限验证
	//if(validateRole('activity_pane','0')== false){
	//	$(".content").empty();
	//	return;
	//}
	//主页面表格
	$("#modelList").jqGrid({
	    url:golbal_context+"/wap_word/list.do",
	    datatype: "local",
	    mtype: "GET",
	    width:"100%",
	    height:"auto",
	    autowidth:true,
	    //multiselect:false, //是否在首列加入复选框
	    //multiselectWidth:30, //复选框的宽度
	    colModel : [   
			//{name:"title",index:"title",label : "id",width:30,align:"center", key:true,hidden:true},
	      	{name:"keyword",index:"keyword",label : "查询词",width:60,sortable:true,align:"center"},	 
	      	{name:"vrid",index:"vrid",label : "VRID",width:30,sortable:true,align:"center"},
	        {name:"type",index:"vrid",label : "类&nbsp;型",width:50,sortable:false,align:"center"},
	        {name:"vrposav",index:"vrposav",label : "位&nbsp;置",width:20,sortable:true,align:"center"},
	        {name:"pagetype",index:"pagetype",label : "pagetype",width:20,sortable:true,align:"center"},
	        {name:"linkid",index:"linkid",label : "linkid",width:30,sortable:true,align:"center"},
	        {name:"pvnum",index:"pvnum",label : "展现量",width:30,sortable:true,align:"center",firstsortorder:"desc" },
	        {name:"clicknum",index:"clicknum",label : "点击量",width:30,sortable:true,align:"center",firstsortorder:"desc" },
	        {name:"endclicknum",index:"endclicknum",label : "最终点击",width:30,sortable:true,align:"center",firstsortorder:"desc" },
	        {name:"consumption",index:"consumption",label : "消费率",width:30,sortable:false,align:"center",firstsortorder:"desc" },
	        {name:"date",index:"date",label : "日期",width:30,sortable:false,align:"center",firstsortorder:"desc" },
	        {name:"hour",index:"hour",label : "时间",width:30,sortable:true,align:"center",firstsortorder:"asc"}
	        //{name:"jhid",index:"jhid",label : "聚合",width:30,sortable:true,align:"center",firstsortorder:"asc"},
	        //{name:"setup",label : "配&nbsp;置",width:30,sortable:false,align:"center" }
		 ],
	    pager: "#listPager",	    
	    sortname: "vrid",
	    sortorder: "desc",
	    //rowNum:-1,
	    rowNum:20,
	    rowList:[20,50,100,200,1000,10000],
	    viewrecords: true,
	    gridview: true,
	    pgbuttons:true,
	    pginput:true,
	    prmNames : {  
		    page:"pageNo",     //表示请求页码的参数名称  
		    rows:"rows",     //表示请求行数的参数名称  
		    totalrows:"totalrows", // 表示需从Server得到总共多少行数据的参数名称，参见jqGrid选项中的rowTotal  
			search:"search",// 表示是否是搜索请求的参数名称
			sort:"sortName",// 表示用于排序的列名的参数名称  
		    order:"sortOrder"// 表示采用的排序方式的参数名称
		},
		jsonReader: {  
	        root:"contents", 
	        page: "pageNo",
	        total: "totalPages",
	        records: "totalRecords",
	        id:"pageNo",
	        repeatitems : false 
	    },
	    gridComplete:function(){  //在此事件中循环为每一行添加详细信息	     	
//	    	var ids=$(this).jqGrid('getDataIDs'); 
//			for(var i=0; i<ids.length; i++){
//				var id=ids[i];     
//				var setupstr = "<img id='" + id + "' src='"+golbal_context+"/application/template/themes/base/images/user_check.gif' title='查看' style='cursor:pointer;' onclick='checkUser(this.id);'/>&nbsp;&nbsp;";
//				//setupstr += "<img id='" + id + "' src='"+golbal_context+"/application/template/themes/base/images/user_modify.gif' title='修改' style='cursor:pointer;' onclick='modifyUserIn(this.id);'/>&nbsp;&nbsp;";
//				//setupstr += "<img id='" + id + "' src='"+golbal_context+"/application/template/themes/base/images/user_del.gif' title='删除' style='cursor:pointer;' onclick='delUserCF(this.id);'/>&nbsp;&nbsp;";
//				//setupstr += "<img id='" + id + "' src='"+golbal_context+"/application/template/themes/base/images/reset.gif' title='恢复密码' style='cursor:pointer;' onclick='recoverUserPassIn(this.id);'/>";
//				$(this).jqGrid("setCell",id,"setup",setupstr,{'padding':'2px 0 0 0'});
//			}
			var bodyObj=document.getElementById('main-content');
			if(bodyObj.scrollHeight>bodyObj.clientHeight||bodyObj.offsetHeight>bodyObj.clientHeight){
				$("#main-content .toolBar").css('width',$(this).width()-10);
			}
        }              
  	});
	
	//时间赋初值
	initTime();
	//日期控件初始化
	initCustomerDatepicker(new Array("query_startTime","query_endTime"),"yy-mm-dd","HH:mm:ss",true,true,false,false);
	
  	//查询下拉列表初始化
  	$.post(golbal_context+"/wap_word/selectInfo.do", function(data){
		var dobject = $.parseJSON(data);
		//fullFillForm(dobject,"query_form","normal");
		formLoadData("query_form", data);
		//带查询功能的下拉框
		$("#query_vrid").select2({
			placeholder: "-请选择-"
		});
		$("#query_jhid").select2({
			placeholder: "-请选择-"
		});
		$("#query_pagetype").select2({
			placeholder: "-请选择-"
		});
		$("#query_vrposav").select2({
			placeholder: "-请选择-"
		});
  	});
  	
  	
  	//信息对话框
  	$( "#detailDialog" ).dialog({
  		title:'趋势',
		autoOpen: false,
		width: '350',
		height:'300',
		modal: true,
		resizable:true,
		show: 'fade',
		hide: 'fade',
 		close:function(){
 			$( this ).empty();
 		}
	});	
  	
	//上传查询词
	$( "#importDialog" ).dialog({
		autoOpen: false,
		width: '500',
		height:'300',
		modal: true,
		resizable:true,
		show: 'fade',
		hide: 'fade',
		buttons: {
			"校验": validateExcel,
			"确定": importExcel,
			"取消": function() {
				$( this ).dialog( "close" );
			}
		},
		close:function(){
			$( "#importDialog" ).empty();
		}
	});
	
	initAlertDialog("alertDialog");
	initConfirmDialog("confirmDialog");
});	

function initTime(){
	//获取系统时间 
	var uom = new Date(); 
	uom.setDate(uom.getDate()-1);//取得系统时间的前一天,重点在这里,负数是前几天,正数是后几天 
	var LINT_MM=uom.getMonth()+1; 
	var LSTR_MM=LINT_MM >= 10?LINT_MM:("0"+LINT_MM) 
	var LINT_DD=uom.getDate(); 
	var LSTR_DD=LINT_DD >= 10?LINT_DD:("0"+LINT_DD) 
	//得到最终结果  
	uom = uom.getFullYear() + "-" + LSTR_MM + "-" + LSTR_DD; 
	$("#query_startTime").val(uom+ " 00:00:00");
	$("#query_endTime").val(uom+ " 23:00:00");
}

//保存当前选择的行Id
var selectId = "";



//词表查询
function importInfo(){
	//加载页面
 	$( "#importDialog" ).load(golbal_jsp_position+'/wap_word/multivr_wap_word_import_diaog.jsp', function(data){
		//表单验证注册
		$("#importUserForm").validate({
			rules:{
				userExcel:{required:true}
			},
			onkeyup:false,
			errorPlacement: function(error, element) { //指定错误信息位置
			element.parent().children().filter("span").append("<br/>");
      		error.appendTo(element.parent().children().filter("span"));
			},
	    	submitHandler:function(){ //验证通过后调用此函数
	    		disableButton();
	    		$('#activity_pane').showLoading();
	    		//采用ajax提交表单
	    		$('#importUserForm').ajaxSubmit({
	    			url : golbal_context+"/wap_word/validateExcel.do",
					dataType: 'json',//返回值类型 一般设置为json
					success:validateExcelCB //成功增加的回调函数
				});
	    	}
		});
		
		$("#importUserForm").ajaxForm();
 	});
	
	$("#importDialog").dialog("open");
}

//校验
function validateExcel(){
	$("#import_valiinfo").html("&nbsp;");
	$("#importUserForm").submit();
}

//校验回调
function validateExcelCB(data){
	//启用按钮
	enableButton();
	$('#activity_pane').hideLoading();
	if(data.result == "success") {
		$("#import_validateResult").val("success");
		$("#import_valiinfo").html("校验成功，可以开始导入文件了");
	}else if(data.result == "failure") {
		$("#import_validateResult").val("failure");
		$("#import_valiinfo").html(data.resultInfo);
	}
}

//导入
function importExcel(){
	var validateResult = $("#import_validateResult").val();
	if(validateResult == "init" || validateResult == "failure"){
		alertJQ("请先校验文件，校验通过后才能导入!","","350");
	}else if(validateResult == "success"){
		$("#importDialog").dialog("close");
		//query();
	}
}

//查询
function query() {
	var postData = {
		startTime : $("#query_startTime").val().trim(),
		endTime : $("#query_endTime").val().trim(),
		vrid: $("#query_vrid").val(),
		vrposav : $("#query_vrposav").val(),
		pagetype : $("#query_pagetype").val(),
		keyword : $("#query_keyword").val().trim(),
		linkid : $("#query_linkid").val().trim(),
		timelevel:$("#query_timelevel").val().trim(),
		jhid: $("#query_jhid").val()
	};
	//合并数据 	
	postData = $.extend($("#modelList").getGridParam("postData"), postData);
	//将合并后的数据设置到表格属性中，记得加search:true 
	$("#modelList").setGridParam({
		search : true,
		datatype: "json",
		postData : postData
	});
	$("#modelList").trigger("reloadGrid", [ {
		page : 1
	} ]);

}




