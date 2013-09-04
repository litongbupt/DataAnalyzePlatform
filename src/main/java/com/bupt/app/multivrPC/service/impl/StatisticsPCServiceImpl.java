package com.bupt.app.multivrPC.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.bupt.app.multivrPC.dao.StatisticsPCMapper;
import com.bupt.app.multivrPC.dto.StatisticsPCDTO;
import com.bupt.app.multivrPC.model.StatisticsPC;
import com.bupt.app.multivrPC.model.StatisticsPCExample;
import com.bupt.app.multivrPC.model.StatisticsPCExample.Criteria;
import com.bupt.app.multivrPC.service.StatisticsPCService;
import com.bupt.app.multivrPC.utils.MultivrPCVRTypeUtils;
import com.bupt.core.base.util.Utils;
/**
 * PC多VR词表查询的业务逻辑实现
 * @author litong
 *
 */
@Service("statisticsPCService")
public class StatisticsPCServiceImpl implements StatisticsPCService {
	
	private final Log log = LogFactory.getLog(getClass());
	private boolean debug = log.isDebugEnabled();
	
	@Resource(name="statisticsPCMapper")
	private StatisticsPCMapper statisticsPCMapper;
	


	@Override
	public int getTotalRecords(HttpServletRequest request, Boolean search) {
		StatisticsPCExample statisticsPCExample = new StatisticsPCExample();
		//设置默认选择哪张表
		statisticsPCExample.setDate(Utils.lastDate(new Date()));
		if(debug){
			log.debug("date:"+Utils.lastDate(new Date()));
		}
		//添加查询条件
		if(search) addCriteria(request, statisticsPCExample);
		return statisticsPCMapper.countByExample(statisticsPCExample);
	}

	@Override
	public List<StatisticsPCDTO> listResults(int start, int limit, String sortName,
			String sortOrder, HttpServletRequest request,Boolean search) {
		StatisticsPCExample statisticsPCExample = new StatisticsPCExample();
		statisticsPCExample.setOrderByClause( sortName +" "+ sortOrder);
		statisticsPCExample.setStart(start);
		statisticsPCExample.setLimit(limit);
		//设置默认选择哪张表
		statisticsPCExample.setDate(Utils.lastDate(new Date()));
		
		//添加查询条件
		if(search) {
			addCriteria(request,  statisticsPCExample);
		}

		//获取结果列表
		List<StatisticsPC> statisticsPCs = statisticsPCMapper.selectByExample(statisticsPCExample);
		//获取VR类型 为了展示方便这里先不转换VR类型了
		Map<String, String> vrMap = MultivrPCVRTypeUtils.getVRType();
		
		List<StatisticsPCDTO> wordDTOList = new ArrayList<StatisticsPCDTO>();
		StatisticsPCDTO statisticsPCDTO = null;
		for (StatisticsPC statisticsPC : statisticsPCs) {
			statisticsPCDTO = new StatisticsPCDTO();
			Utils.copyProperties(statisticsPCDTO, statisticsPC);
			//VR类型转换
			statisticsPCDTO.setVrId(statisticsPC.getType());
			if(vrMap.containsKey(statisticsPC.getType())){
				statisticsPCDTO.setType(vrMap.get(statisticsPC.getType()));
			}
			wordDTOList.add(statisticsPCDTO);
		}
		return wordDTOList;
	}

	/**
	 * 添加查询条件
	 * @param request
	 * @param search
	 * @param statisticsPCExample
	 * @author 李彤 2013-8-26 下午8:18:21
	 */
	private void addCriteria(HttpServletRequest request,StatisticsPCExample statisticsPCExample) {
			String[] type = request.getParameterValues("type[]");
			if(type==null||type.length==0) type = request.getParameterValues("type");
			String position = request.getParameter("position");
			String abtest = request.getParameter("abtest");
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			if(debug){
				log.debug("type:"+type+"position:"+position+"abtest:"+abtest+"startTime:"+startTime+"endTime:"+endTime+"date:"+Utils.getDate(startTime));
			}
			statisticsPCExample.setDate(Utils.getDate(startTime));
			Criteria criteria = statisticsPCExample.createCriteria();
			if(type!=null&&type.length>0) criteria.andTypeIn(Arrays.asList(type));
			if(!StringUtils.isEmpty(position)) criteria.andPositionEqualTo(Integer.parseInt(position));
			if(!StringUtils.isEmpty(abtest)) criteria.andAbtestEqualTo(Integer.parseInt(abtest));
			if(!StringUtils.isEmpty(startTime)&&!StringUtils.isEmpty(endTime)) criteria.andHourBetween(Utils.getHour(startTime), Utils.getHour(endTime));
	}
	
	

}
