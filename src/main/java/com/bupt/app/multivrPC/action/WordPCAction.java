package com.bupt.app.multivrPC.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.bupt.app.multivrPC.dto.WordPCDTO;
import com.bupt.app.multivrPC.model.WordPC;
import com.bupt.app.multivrPC.service.WordPCService;
import com.bupt.core.base.action.DataRequest;
import com.bupt.core.base.action.JqGridBaseAction;
import com.bupt.core.base.dto.ParameterDTO;
import com.bupt.core.base.dto.SelectOptionDTO;
import com.bupt.core.base.util.ExcelExporter;
import com.bupt.core.base.util.ExportParameter;
import com.bupt.core.base.util.ServiceReturnResult;
/**
 * PC多VR中词表查询的controller
 * @author litong
 *
 */
@Controller
@RequestMapping("/pc_word")
public class WordPCAction extends JqGridBaseAction<WordPCDTO>{
	
	private final Log log = LogFactory.getLog(getClass());
	private boolean debug = log.isDebugEnabled();
	
	@Resource(name="wordPCService")
	private WordPCService wordPCService;

	@Override
	public List<WordPCDTO> listResults(int start, int limit, String sortName,
			String sortOrder, HttpServletRequest request, Boolean search) {
		return this.wordPCService.listResults(start, limit, sortName, sortOrder, request,search);
	}

	@Override
	public Integer getTotalRecords(HttpServletRequest request, Boolean search) {
		return wordPCService.getTotalRecords(request, search);
	}	
	
	/**
	 * 获取查询头中的下拉列表信息
	 * @return
	 * @author 李彤 2013-8-27 下午10:13:43
	 */
	@RequestMapping("/selectInfo.do")
	@ResponseBody
	public Map<String, List<SelectOptionDTO>> getSelectInfo(){
		//类型列表
		List<SelectOptionDTO> typeSelectOptionList = new ArrayList<SelectOptionDTO>();
		//位置列表
		List<SelectOptionDTO> positionSelectOptionList = new ArrayList<SelectOptionDTO>();
		//abtest列表
		List<SelectOptionDTO> abtestSelectOptionList = new ArrayList<SelectOptionDTO>();
		
		SelectOptionDTO  tempSelectOptionDto = null;
		
		//获取VR类型的下拉框信息
		Map<String,String> typeMap  = this.wordPCService.getTypeMap();
		for (Entry<String,String> entry : typeMap.entrySet()) {
			tempSelectOptionDto = new SelectOptionDTO();
			tempSelectOptionDto.setName(entry.getValue());
			tempSelectOptionDto.setValue(entry.getKey());
			typeSelectOptionList.add(tempSelectOptionDto);
		}
		
		//获取位置的下拉框信息
		List<Integer> positionList = this.wordPCService.getPositionList();
		for (Integer position : positionList) {
			tempSelectOptionDto = new SelectOptionDTO();
			tempSelectOptionDto.setName(position.toString());
			tempSelectOptionDto.setValue(position.toString());
			positionSelectOptionList.add(tempSelectOptionDto);
		}
		
		//获取abtest的下拉框信息
		List<Integer> abtestList = this.wordPCService.getAbtestList();
		for (Integer abtest : abtestList) {
			tempSelectOptionDto = new SelectOptionDTO();
			tempSelectOptionDto.setName(abtest.toString());
			tempSelectOptionDto.setValue(abtest.toString());
			abtestSelectOptionList.add(tempSelectOptionDto);
		}
		
		//放入Map返回结果
		Map<String, List<SelectOptionDTO>> map = new HashMap<String, List<SelectOptionDTO>>();
		map.put("type", typeSelectOptionList);
		map.put("position", positionSelectOptionList);
		map.put("abtest", abtestSelectOptionList);
		return map;
	}

	/**
	 * 
	 * 获取导出的字段的默认属性
	 * @Title: getDefaultCols
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	@RequestMapping("/getDefaultExportCols.do")
	@ResponseBody
	public Map<String,List<SelectOptionDTO>> getDefaultCols(
            HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
		//读取导出字段默认属性
		List<SelectOptionDTO> columns = new ArrayList<SelectOptionDTO>();
		Map<String, ParameterDTO> paramMap = ExportParameter.getExportInfos().get("MULTIVR_PC_WORD").getParams();
		for(String p : paramMap.keySet()){
			ParameterDTO pd = paramMap.get(p);
			if(pd.isShow()){
				SelectOptionDTO option = new SelectOptionDTO();
				option.setValue(pd.getName());
				option.setName(pd.getShowName());
				option.setSelected(pd.isSelected());
				columns.add(option);
			}
		}
		
		//放入Map返回结果
		Map<String,List<SelectOptionDTO>> dto = new HashMap<String,List<SelectOptionDTO>>();
		dto.put("columns", columns);
		return dto;
	}

	/**
	 * 导出数据
	 * @param maxRecords 导出条数
	 * @param sortName 以哪个字段排序
	 * @param sortOrder asc,desc
	 * @param selectCols 导出的字段列表
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 * @throws ServletException
	 * @author 李彤 2013-8-27 下午11:22:22
	 */
	@RequestMapping("/export.do")
	public void export(@RequestParam(value = "maxRecords") Integer maxRecords,
			@RequestParam(value = "sortName") String sortName,
			@RequestParam(value = "sortOrder") String sortOrder,
			@RequestParam(value = "search") Boolean search,
			@RequestParam(value = "selectCols") String selectCols,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// 获取导出参数，及查询参数
		DataRequest param = new DataRequest();
		// 设置查询条件
		param.setRequest(request);
		param.setSearch(search);
		param.setMaxRecords(maxRecords);
		param.setSortName(sortName);
		param.setSortOrder(sortOrder);
		param.setFileName("PC多VR词表查询");
		param.setSelectCols(selectCols);
		//获取导出的默认设置
		Map<String, ParameterDTO> paramMap = ExportParameter.getExportInfos().get(
				"MULTIVR_PC_WORD").getParams();
		// 导出服务
		ExcelExporter<WordPC, WordPCDTO> exporter = new ExcelExporter<WordPC, WordPCDTO>();
		exporter.setService(this.wordPCService);
		exporter.setParam(param);
		exporter.setParamMap(paramMap);
		exporter.export(response);
	}
	
	/**
	 * 验证上传的词表
	 * @param request
	 * @param response
	 * @param userExcel
	 * @return
	 * @author 李彤 2013-8-27 下午11:27:26
	 */
	@RequestMapping(value = "/validateExcel.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> validateExcel(HttpServletRequest request,
			HttpServletResponse response,@RequestParam MultipartFile[] userExcel) {
		String result = "";
		String resultInfo = "";
		//如果只是上传一个文件，则只需要MultipartFile类型接收文件即可，而且无需显式指定@RequestParam注解 
        //如果想上传多个文件，那么这里就要用MultipartFile[]类型来接收文件，并且还要指定@RequestParam注解 
        //并且上传多个文件时，前台表单中的所有<input type="file"/>的name都应该是myfiles，否则参数里的myfiles无法获取到所有上传的文件 
		for(MultipartFile myfile : userExcel){ 
            if(myfile.isEmpty()){ 
            	if(debug){
            		log.debug("文件未上传");
            	}
            }else{ 
            	if(debug){
            		log.debug("文件长度: " + myfile.getSize());
            		log.debug("文件类型: " + myfile.getContentType());
            		log.debug("文件名称: " + myfile.getName());
            		log.debug("文件原名: " + myfile.getOriginalFilename());
            	}
            	try {
					result = this.wordPCService.validateExcel(myfile.getInputStream(), request.getSession().getId());
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (!result.equals(ServiceReturnResult.SERVICE_OP_SUCC)) {
					// if browser is IE
					if (request.getHeader("User-Agent")
							.toLowerCase().indexOf("msie") > 0)
						resultInfo = result.replaceAll("<br />",
								"&lt;br /&gt;");
					else
						resultInfo = result;
					result = ServiceReturnResult.SERVICE_OP_FAIL;
				}
                //如果用的是Tomcat服务器，则文件会上传到\\%TOMCAT_HOME%\\webapps\\YourWebProject\\WEB-INF\\upload\\文件夹中 
                //String realPath = request.getSession().getServletContext().getRealPath("/WEB-INF/upload"); 
                //这里不必处理IO流关闭的问题，因为FileUtils.copyInputStreamToFile()方法内部会自动把用到的IO流关掉，我是看它的源码才知道的 
                //FileUtils.copyInputStreamToFile(myfile.getInputStream(), new File(realPath, myfile.getOriginalFilename())); 
            } 
		
		}
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("result", result);
		dataMap.put("resultInfo", resultInfo);
		return dataMap;
	}
}
