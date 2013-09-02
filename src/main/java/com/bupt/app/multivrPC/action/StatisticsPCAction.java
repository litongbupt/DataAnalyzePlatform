package com.bupt.app.multivrPC.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bupt.app.multivrPC.dto.StatisticsPCDTO;
import com.bupt.app.multivrPC.model.StatisticsPC;
import com.bupt.app.multivrPC.service.StatisticsPCService;
import com.bupt.core.base.action.DataRequest;
import com.bupt.core.base.action.JqGridBaseAction;
import com.bupt.core.base.dto.ParameterDTO;
import com.bupt.core.base.dto.SelectOptionDTO;
import com.bupt.core.base.util.ExcelExporter;
import com.bupt.core.base.util.ExportParameter;
/**
 * PC多VR中统计查询的controller
 * @author litong
 *
 */
@Controller
@RequestMapping("/pc_statistics")
public class StatisticsPCAction extends JqGridBaseAction<StatisticsPCDTO>{
	
	private final Log log = LogFactory.getLog(getClass());
	private boolean debug = log.isDebugEnabled();
	
	@Resource(name="statisticsPCService")
	private StatisticsPCService statisticsPCService;

	@Override
	public List<StatisticsPCDTO> listResults(int start, int limit, String sortName,
			String sortOrder, HttpServletRequest request, Boolean search) {
		return this.statisticsPCService.listResults(start, limit, sortName, sortOrder, request,search);
	}

	@Override
	public Integer getTotalRecords(HttpServletRequest request, Boolean search) {
		return statisticsPCService.getTotalRecords(request, search);
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
		Map<String, ParameterDTO> paramMap = ExportParameter.getExportInfos().get("MULTIVR_PC_STATISTICSPC").getParams();
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
		param.setFileName("PC多VR统计查询");
		param.setSelectCols(selectCols);
		//获取导出的默认设置
		Map<String, ParameterDTO> paramMap = ExportParameter.getExportInfos().get(
				"MULTIVR_PC_STATISTICSPC").getParams();
		// 导出服务
		ExcelExporter<StatisticsPC, StatisticsPCDTO> exporter = new ExcelExporter<StatisticsPC, StatisticsPCDTO>();
		exporter.setService(this.statisticsPCService);
		exporter.setParam(param);
		exporter.setParamMap(paramMap);
		exporter.export(response);
	}
	

}
