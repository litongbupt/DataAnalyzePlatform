package com.bupt.app.multivrPC.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

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
	
	private Map<Integer,Integer> totalRecordMap = new TreeMap<Integer,Integer>();

	@Override
	public int getTotalRecords(HttpServletRequest request, Boolean search) {
		StatisticsPCExample statisticsPCExample = new StatisticsPCExample();

		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		
		int startDay;
		int endDay;
		try {
			startDay = Integer.parseInt(Utils.getDate(startTime));
			endDay = Integer.parseInt(Utils.getDate(endTime));
		} catch (Exception e) {
			// 设置默认选择哪张表
			startDay = endDay = Integer.parseInt(Utils.lastDate(new Date()));
		}

		Integer startHour = null;
		Integer endHour = null;
		if(startDay==endDay){
			statisticsPCExample.setDate(startDay + "");
			startHour = Utils.getHour(startTime);
			endHour = Utils.getHour(endTime);
			if(startHour==0&&endHour==23){
				startHour=null;
				endHour=null;
			}
			if (search) {
				addCriteria(request, statisticsPCExample, startHour, endHour);
			}
			totalRecordMap.put(startDay, statisticsPCMapper.countByExample(statisticsPCExample));
		}else if (startDay < endDay) {
			if (search) {
//				if (currentDay == startDay) {startHour = Utils.getHour(startTime);if(startHour!=0) endHour=23;} 
//				if (currentDay == endDay) {endHour = Utils.getHour(endTime);if(endHour!=23) startHour=0;}
				addCriteria(request, statisticsPCExample, startHour, endHour);
			}
			for (int currentDay = startDay; currentDay <= endDay; ++currentDay) {
				statisticsPCExample.setDate(currentDay + "");
				// 添加查询条件
				totalRecordMap.put(currentDay, statisticsPCMapper.countByExample(statisticsPCExample));
			}
		}
		int totalRecord = 0;
		for (Integer record : totalRecordMap.values()) {
			totalRecord+=record;
		}
		return totalRecord;
	}

	@Override
	public List<StatisticsPCDTO> listResults(int start, int limit, String sortName,
			String sortOrder, HttpServletRequest request,Boolean search) {

		//DTO
		List<StatisticsPCDTO> wordDTOList = new ArrayList<StatisticsPCDTO>();
		
		//获取起始日期
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		int startDay;
		int endDay;
		try {
			startDay = Integer.parseInt(Utils.getDate(startTime));
			endDay = Integer.parseInt(Utils.getDate(endTime));
		} catch (Exception e) {
			// 设置默认选择哪张表
			startDay = endDay = Integer.parseInt(Utils.lastDate(new Date()));
		}
		
		Integer startHour = null;
		Integer endHour = null;
		if(startDay==endDay){//如果是同一天，判断小时
			StatisticsPCExample statisticsPCExample = new StatisticsPCExample();
			statisticsPCExample.setOrderByClause( sortName +" "+ sortOrder);
			statisticsPCExample.setDate(startDay + "");
			startHour = Utils.getHour(startTime);
			endHour = Utils.getHour(endTime);
			if(startHour==0&&endHour==23){
				startHour=null;
				endHour=null;
			}
			if(search) addCriteria(request, statisticsPCExample, startHour, endHour);
			
			statisticsPCExample.setDate(startDay+"");
			statisticsPCExample.setStart(start);
			statisticsPCExample.setLimit(limit);
			wordDTOList.addAll(selectByPCExample(statisticsPCExample));
		}else if (startDay < endDay) {//不考虑小时
			StatisticsPCExample statisticsPCExample = new StatisticsPCExample();
			statisticsPCExample.setOrderByClause( sortName +" "+ sortOrder);
			if(search) addCriteria(request, statisticsPCExample, startHour, endHour);
			int currentTotalRecord = 0;
			for (int currentDay = startDay; currentDay <= endDay; currentDay++) {
				Integer currentRecordsCount = totalRecordMap.get(currentDay);
				if (start + limit <= currentTotalRecord + currentRecordsCount) {
					statisticsPCExample.setDate(currentDay + "");
					statisticsPCExample.setStart(start - currentTotalRecord);
					statisticsPCExample.setLimit(limit);
					wordDTOList.addAll(selectByPCExample(statisticsPCExample));
					break;
				} else if (start + limit > currentTotalRecord + currentRecordsCount&&currentTotalRecord + currentRecordsCount - start>0) {
					statisticsPCExample.setDate(currentDay + "");
					statisticsPCExample.setStart(start - currentTotalRecord);
					statisticsPCExample.setLimit(currentTotalRecord + currentRecordsCount - start);
					wordDTOList.addAll(selectByPCExample(statisticsPCExample));
					limit = limit - (currentTotalRecord + currentRecordsCount - start);
					start=currentTotalRecord+currentRecordsCount;
				}
				currentTotalRecord += currentRecordsCount;
			}
				
		}
		return wordDTOList;
	}

	/**
	 * @param statisticsPCExample
	 * @return
	 * @author 李彤 2013-9-12 下午2:15:21
	 */
	private List<StatisticsPCDTO> selectByPCExample(
			StatisticsPCExample statisticsPCExample) {
		Map<String, String> vrMap = MultivrPCVRTypeUtils.getVRType();
		List<StatisticsPCDTO> wordDTOList = new ArrayList<StatisticsPCDTO>();
		List<StatisticsPC> statisticsPCs = statisticsPCMapper.selectByExample(statisticsPCExample);
		StatisticsPCDTO statisticsPCDTO = null;
		for (StatisticsPC statisticsPC : statisticsPCs) {
			statisticsPCDTO = new StatisticsPCDTO();
			Utils.copyProperties(statisticsPCDTO, statisticsPC);
			//VR类型转换
			statisticsPCDTO.setVrId(statisticsPC.getType());
			if(vrMap.containsKey(statisticsPC.getType())){
				statisticsPCDTO.setType(vrMap.get(statisticsPC.getType()));
			}
			statisticsPCDTO.setConsumption(statisticsPCDTO.getEclpv()*100/statisticsPCDTO.getPv()+"%");
			statisticsPCDTO.setDate(statisticsPCExample.getDate());
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
	private void addCriteria(HttpServletRequest request,StatisticsPCExample statisticsPCExample,Integer startHour,Integer endHour) {
			String[] type = request.getParameterValues("type[]");
			if(type==null||type.length==0) type = request.getParameterValues("type");
			String position = request.getParameter("position");
			String abtest = request.getParameter("abtest");
			String clickid = request.getParameter("clickid");
			if(debug){
				log.debug("type:"+type+"position:"+position+"abtest:"+abtest+"startHour:"+startHour+"endHour:"+endHour+"clickid: "+clickid);
			}
			Criteria criteria = statisticsPCExample.createCriteria();
			if(type!=null&&type.length>0&&!type[0].equalsIgnoreCase("null")) criteria.andTypeIn(Arrays.asList(type));
			if(!StringUtils.isEmpty(position)) criteria.andPositionEqualTo(Integer.parseInt(position));
			if(!StringUtils.isEmpty(abtest)) criteria.andAbtestEqualTo(Integer.parseInt(abtest));
			if(!StringUtils.isEmpty(clickid)) criteria.andClickidEqualTo(clickid);
			if(startHour!=null&&endHour!=null) criteria.andHourBetween(startHour, endHour);
	}
	
	

}
