package com.bupt.app.multivrWAP.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.bupt.app.multivrPC.utils.MultivrPCVRTypeUtils;
import com.bupt.app.multivrWAP.dao.StatisticsWAPMapper;
import com.bupt.app.multivrWAP.dto.StatisticsWAPDTO;
import com.bupt.app.multivrWAP.model.StatisticsWAP;
import com.bupt.app.multivrWAP.model.StatisticsWAPExample;
import com.bupt.app.multivrWAP.model.StatisticsWAPExample.Criteria;
import com.bupt.app.multivrWAP.service.StatisticsWAPService;
import com.bupt.app.multivrWAP.service.WordWAPService;
import com.bupt.app.multivrWAP.utils.MultivrWAPVRTypeUtils;
import com.bupt.core.base.util.Utils;
/**
 * WAP多VR词表查询的业务逻辑实现
 * @author litong
 *
 */
@Service("statisticsWAPService")
public class StaticticsWAPServiceImpl implements StatisticsWAPService {
	
	private final Log log = LogFactory.getLog(getClass());
	private boolean debug = log.isDebugEnabled();
	Map<String, String> vrMap;
	
	@Resource(name="statisticsWAPMapper")
	private StatisticsWAPMapper statisticsWAPMapper;
	
	@Resource(name="wordWAPService")
	private WordWAPService wordWAPService;
	
	private Map<Integer,Integer> totalRecordMap = new TreeMap<Integer,Integer>();
	
	

	@Override
	public int getTotalRecords(HttpServletRequest request, Boolean search) {
		getTotalRecordMap(request, search);
		int totalRecord = 0;
		for (Integer record : totalRecordMap.values()) {
			totalRecord+=record;
		}
		return totalRecord;
	}
	
	
	/**
	 * @param request
	 * @param search
	 * @param statisticsWAPExample
	 * @author 李彤 2013-9-17 上午10:54:44
	 */
	private void getTotalRecordMap(HttpServletRequest request, Boolean search) {
		StatisticsWAPExample statisticsWAPExample = new StatisticsWAPExample();
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String timelevel = request.getParameter("timelevel");
		
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
			statisticsWAPExample.setDate(startDay + "");
			startHour = Utils.getHour(startTime);
			endHour = Utils.getHour(endTime);
			if(startHour==0&&endHour==23){
				startHour=null;
				endHour=null;
			}
			if (search) {
				addCriteria(request, statisticsWAPExample, startHour, endHour);
			}
			if(timelevel.equals("hour")){
				totalRecordMap.put(startDay, statisticsWAPMapper.countByExample(statisticsWAPExample));
			}else{
				totalRecordMap.put(startDay, statisticsWAPMapper.countDayByExample(statisticsWAPExample));
			}
			
		}else if (startDay < endDay) {
			if (search) {// 添加查询条件
				addCriteria(request, statisticsWAPExample, startHour, endHour);
			}
			for (int currentDay = startDay; currentDay <= endDay; ++currentDay) {
				statisticsWAPExample.setDate(currentDay + "");
				if(timelevel.equals("hour")){
					totalRecordMap.put(startDay, statisticsWAPMapper.countByExample(statisticsWAPExample));
				}else{
					totalRecordMap.put(startDay, statisticsWAPMapper.countDayByExample(statisticsWAPExample));
				}
			}
		}
	}
	
	

	@Override
	public List<StatisticsWAPDTO> listResults(int start, int limit, String sortName,
			String sortOrder, HttpServletRequest request,Boolean search) {
		//DTO
		List<StatisticsWAPDTO> statisticsDTOList = new ArrayList<StatisticsWAPDTO>();
		
		//获取起始日期
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String timelevel = request.getParameter("timelevel");
		
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
			StatisticsWAPExample statisticsWAPExample = new StatisticsWAPExample();
			statisticsWAPExample.setOrderByClause( sortName +" "+ sortOrder);
			statisticsWAPExample.setDate(startDay + "");
			startHour = Utils.getHour(startTime);
			endHour = Utils.getHour(endTime);
			if(startHour==0&&endHour==23){
				startHour=null;
				endHour=null;
			}
			if(search) addCriteria(request, statisticsWAPExample, startHour, endHour);
			
			statisticsWAPExample.setDate(startDay+"");
			statisticsWAPExample.setStart(start);
			statisticsWAPExample.setLimit(limit);
			statisticsDTOList.addAll(selectByWAPExample(statisticsWAPExample,timelevel));
		}else if (startDay < endDay) {//不考虑小时
			if(totalRecordMap.isEmpty()){
				getTotalRecordMap(request, search);
			}
			StatisticsWAPExample statisticsWAPExample = new StatisticsWAPExample();
			statisticsWAPExample.setOrderByClause( sortName +" "+ sortOrder);
			if(search) addCriteria(request, statisticsWAPExample, startHour, endHour);
			int currentTotalRecord = 0;
			for (int currentDay = startDay; currentDay <= endDay; currentDay++) {
				Integer currentRecordsCount = totalRecordMap.get(currentDay);
				if (start + limit <= currentTotalRecord + currentRecordsCount) {
					statisticsWAPExample.setDate(currentDay + "");
					statisticsWAPExample.setStart(start - currentTotalRecord);
					statisticsWAPExample.setLimit(limit);
					statisticsDTOList.addAll(selectByWAPExample(statisticsWAPExample,timelevel));
					break;
				} else if (start + limit > currentTotalRecord + currentRecordsCount&&currentTotalRecord + currentRecordsCount - start>0) {
					statisticsWAPExample.setDate(currentDay + "");
					statisticsWAPExample.setStart(start - currentTotalRecord);
					statisticsWAPExample.setLimit(currentTotalRecord + currentRecordsCount - start);
					statisticsDTOList.addAll(selectByWAPExample(statisticsWAPExample,timelevel));
					limit = limit - (currentTotalRecord + currentRecordsCount - start);
					start=currentTotalRecord+currentRecordsCount;
				}
				currentTotalRecord += currentRecordsCount;
			}
			totalRecordMap.clear();
		}
		return statisticsDTOList;
	}
	
	private Collection<? extends StatisticsWAPDTO> selectByWAPExample(
			StatisticsWAPExample statisticsWAPExample,String timelevel) {
		if(vrMap==null){
			vrMap = wordWAPService.getTypeMap();
		}
		Map<String, String> pageTypeMap = wordWAPService.getPageTypeMap();
		List<StatisticsWAPDTO> statisticsDTOList = new ArrayList<StatisticsWAPDTO>();
		List<StatisticsWAP> statisticsWAPs = null;
		if(timelevel.equals("hour")){
			statisticsWAPs = statisticsWAPMapper.selectByExample(statisticsWAPExample);
		}else{
			statisticsWAPs = statisticsWAPMapper.selectDayByExample(statisticsWAPExample);
		}
		StatisticsWAPDTO statisticsWAPDTO = null;
		for (StatisticsWAP statisticsWAP : statisticsWAPs) {
			statisticsWAPDTO = new StatisticsWAPDTO();
			Utils.copyProperties(statisticsWAPDTO, statisticsWAP);
			//VR类型转换
			statisticsWAPDTO.setVrid(statisticsWAP.getVrid());
			if(vrMap.containsKey(statisticsWAP.getVrid())){
				statisticsWAPDTO.setType(vrMap.get(statisticsWAP.getVrid()));
			}else{
				statisticsWAPDTO.setType("未找到该类型名");
			}
			if(pageTypeMap.containsKey(statisticsWAP.getPagetype()+"")){
				statisticsWAPDTO.setPagetype(pageTypeMap.get(statisticsWAP.getPagetype()+""));
			}
			if(statisticsWAPDTO.getPvnum()!=0){
				statisticsWAPDTO.setConsumption(statisticsWAPDTO.getEndclicknum()*100/statisticsWAPDTO.getPvnum()+"%");
				statisticsWAPDTO.setCtr(statisticsWAPDTO.getClicknum()*100/statisticsWAPDTO.getPvnum()+"%");
			}else{
				statisticsWAPDTO.setConsumption("-");
				statisticsWAPDTO.setCtr("-");
			}
			statisticsWAPDTO.setDate(statisticsWAPExample.getDate());
			statisticsDTOList.add(statisticsWAPDTO);
		}
		return statisticsDTOList;
	}



	/**
	 * 添加查询条件
	 * @param request
	 * @param search
	 * @param statisticsWAPExample
	 * @author 李彤 2013-8-26 下午8:18:21
	 */
	private void addCriteria(HttpServletRequest request,StatisticsWAPExample statisticsWAPExample,Integer startHour,Integer endHour) {
			String[] vrid = request.getParameterValues("vrid[]");
			if(vrid==null||vrid.length==0) vrid = request.getParameterValues("vrid");
			String[] jhid = request.getParameterValues("jhid[]");
			if(jhid==null||jhid.length==0) jhid = request.getParameterValues("jhid");
			String[] pagetype = request.getParameterValues("pagetype[]");
			if(pagetype==null||pagetype.length==0) pagetype = request.getParameterValues("pagetype");
			String[] vrposav = request.getParameterValues("vrposav[]");
			if(vrposav==null||vrposav.length==0) vrposav = request.getParameterValues("vrposav");
			
			String linkid = request.getParameter("linkid");
			if(debug){
				log.debug("jhid"+Arrays.toString(jhid)+"vrid:"+Arrays.toString(vrid)+"vrposav:"+vrposav+"pagetype:"+pagetype+"linkid: "+linkid);
			}
			Criteria criteria = statisticsWAPExample.createCriteria();
			if(vrid!=null&&vrid.length>0&&!vrid[0].equalsIgnoreCase("null")) criteria.andVridIn(Arrays.asList(vrid));
			if(jhid!=null&&jhid.length>0&&!jhid[0].equalsIgnoreCase("null")) criteria.andJhidIn(Arrays.asList(jhid));
			if(pagetype!=null&&pagetype.length>0&&!pagetype[0].equalsIgnoreCase("null")){
				Byte[] pagetypeByte = new Byte[pagetype.length];
				for (int i = 0; i < pagetype.length; i++) {
					pagetypeByte[i]=Byte.parseByte(pagetype[i]);
				}
				criteria.andPagetypeIn(Arrays.asList(pagetypeByte));
			}
			if(vrposav!=null&&vrposav.length>0&&!vrposav[0].equalsIgnoreCase("null")){
				Float[] vrposavByte = new Float[vrposav.length];
				for (int i = 0; i < vrposav.length; i++) {
					vrposavByte[i]=Float.parseFloat(vrposav[i]);
				}
				criteria.andVrposavIn(Arrays.asList(vrposavByte));
			}
			if(!StringUtils.isEmpty(linkid)) criteria.andLinkidEqualTo(Byte.parseByte(linkid));
			if(startHour!=null&&endHour!=null) criteria.andHourBetween(startHour, endHour);

	}

	
	
	public StatisticsWAPMapper getStatisticsWAPMapper() {
		return statisticsWAPMapper;
	}

	public void setStatisticsWAPMapper(StatisticsWAPMapper statisticsWAPMapper) {
		this.statisticsWAPMapper = statisticsWAPMapper;
	}

}
