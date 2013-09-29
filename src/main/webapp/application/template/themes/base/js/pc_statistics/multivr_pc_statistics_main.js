$(function(){
	//权限验证
	//if(validateRole('activity_pane','0')== false){
	//	$(".content").empty();
	//	return;
	//}
	//主页面表格
	$("#modelList").jqGrid({
	    url:golbal_context+"/pc_statistics/list.do",
	    datatype: "local",
	    mtype: "GET",
	    width:"100%",
	    height:"auto",
	    autowidth:true,
	    //multiselect:false, //是否在首列加入复选框
	    //multiselectWidth:30, //复选框的宽度
	    colModel : [   
	      	{name:"vrId",index:"type",label : "VRID",width:30,sortable:true,align:"center"},
	        {name:"type",index:"type",label : "类&nbsp;型",width:50,sortable:false,align:"center"},
	        {name:"position",index:"position",label : "位&nbsp;置",width:20,sortable:true,align:"center"},
	        //{name:"abtest",index:"abtest",label : "abtest",width:20,sortable:true,align:"center"},
	        {name:"clickid",index:"clickid",label : "clickid",width:30,sortable:true,align:"center"},
	        {name:"pv",index:"pv",label : "展现量",width:30,sortable:true,align:"center",firstsortorder:"desc" },
	        {name:"click",index:"click",label : "点击量",width:30,sortable:true,align:"center",firstsortorder:"desc" },
	        {name:"eclpv",index:"eclpv",label : "最终点击",width:30,sortable:true,align:"center",firstsortorder:"desc" },
	        {name:"consumption",index:"consumption",label : "消费率",width:30,sortable:false,align:"center",firstsortorder:"desc" },
	        {name:"day",index:"day",label : "日期",width:30,sortable:false,align:"center",firstsortorder:"desc" },
	        {name:"hour",index:"hour",label : "时间",width:30,sortable:true,align:"center",firstsortorder:"asc"},
	        {name:"jhid",index:"jhid",label : "聚合",width:30,sortable:true,align:"center",firstsortorder:"asc"},
	        //{name:"setup",label : "配&nbsp;置",width:30,sortable:false,align:"center" }
		 ],
	    pager: "#listPager",	    
	    sortname: "type",
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
  	$.post(golbal_context+"/pc_word/selectInfo.do", function(data){
		var dobject = $.parseJSON(data);
		//fullFillForm(dobject,"query_form","normal");
		formLoadData("query_form", data);
		//带查询功能的下拉框
		$("#query_type").select2({
			placeholder: "-请选择-"
		});
		$("#query_jhid").select2({
			placeholder: "-请选择-"
		});
		$("#query_abtest").select2({
			placeholder: "-请选择-"
		});
		$("#query_position").select2({
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
	
	initAlertDialog("alertDialog");
	initConfirmDialog("confirmDialog");
});	

function initTime(){
	//获取系统时间 
	var uom = new Date(); 
	uom.setDate(uom.getDate()-1);//取得系统时间的前一天,重点在这里,负数是前几天,正数是后几天 
	var LINT_MM=uom.getMonth()+1; 
	var LSTR_MM=LINT_MM > 10?LINT_MM:("0"+LINT_MM) 
	var LINT_DD=uom.getDate(); 
	var LSTR_DD=LINT_DD > 10?LINT_DD:("0"+LINT_DD) 
	//得到最终结果  
	uom = uom.getFullYear() + "-" + LSTR_MM + "-" + LSTR_DD; 
	$("#query_startTime").val(uom+ " 00:00:00");
	$("#query_endTime").val(uom+ " 23:00:00");
}

//保存当前选择的行Id
var selectId = "";

////查看用户函数
//function checkUser(userId) {
//	//$('#activity_pane').showLoading();
//	$("#detailDialog").dialog('option', 'title', '趋势');
//	$("#detailDialog").dialog('option', 'buttons', {
//		"返回" :  function() {
//			$(this).dialog("close");
//		}
//	});
//	selectId = userId;
//	//加载基本信息页面
//	$("#detailDialog").load(golbal_jsp_position+'/pc_word/multivr_pc_word_check_dialog.jsp',function() {
//		$.post(golbal_context+"/system/user/checkUser.do", {userId : userId}, function(data){
//			//为表单加载数据
//			//selectedRoles("checkUserForm", data);
//			//$('#activity_pane').hideLoading();
//			$("#detailDialog").dialog("open");
//		});
//	});
//}




//查询
function query() {
	var postData = {
		startTime : $("#query_startTime").val().trim(),
		endTime : $("#query_endTime").val().trim(),
		type: $("#query_type").val(),
		position : $("#query_position").val(),
		abtest : $("#query_abtest").val(),
		clickid : $("#query_clickid").val().trim(),
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




