package com.bupt.app.multivrWAP.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.bupt.app.multivrWAP.dao.WordWAPMapper;
import com.bupt.app.multivrWAP.dto.WordWAPDTO;
import com.bupt.app.multivrWAP.model.WordWAP;
import com.bupt.app.multivrWAP.model.WordWAPExample;
import com.bupt.app.multivrWAP.model.WordWAPExample.Criteria;
import com.bupt.app.multivrWAP.service.WordWAPService;
import com.bupt.app.multivrWAP.utils.MultivrWAPVRTypeUtils;
import com.bupt.core.base.util.ServiceReturnResult;
import com.bupt.core.base.util.Utils;
/**
 * WAP多VR词表查询的业务逻辑实现
 * @author litong
 *
 */
@Service("wordWAPService")
public class WordWAPServiceImpl implements WordWAPService {
	
	private final Log log = LogFactory.getLog(getClass());
	private boolean debug = log.isDebugEnabled();
	private static Map<String, List<String>> importWords = new HashMap<String,List<String>>();
	
	//@Resource(name="wordWAPMapper")
	private WordWAPMapper wordWAPMapper;
	
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
	 * @param wordWAPExample
	 * @author 李彤 2013-9-17 上午10:54:44
	 */
	private void getTotalRecordMap(HttpServletRequest request, Boolean search) {
		WordWAPExample wordWAPExample = new WordWAPExample();
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
			wordWAPExample.setDate(startDay + "");
			startHour = Utils.getHour(startTime);
			endHour = Utils.getHour(endTime);
			if(startHour==0&&endHour==23){
				startHour=null;
				endHour=null;
			}
			if (search) {
				addCriteria(request, wordWAPExample, startHour, endHour);
			}
			if(timelevel.equals("hour")){
				totalRecordMap.put(startDay, wordWAPMapper.countByExample(wordWAPExample));
			}else{
				totalRecordMap.put(startDay, wordWAPMapper.countDayByExample(wordWAPExample));
			}
			
		}else if (startDay < endDay) {
			if (search) {// 添加查询条件
				addCriteria(request, wordWAPExample, startHour, endHour);
			}
			for (int currentDay = startDay; currentDay <= endDay; ++currentDay) {
				wordWAPExample.setDate(currentDay + "");
				if(timelevel.equals("hour")){
					totalRecordMap.put(startDay, wordWAPMapper.countByExample(wordWAPExample));
				}else{
					totalRecordMap.put(startDay, wordWAPMapper.countDayByExample(wordWAPExample));
				}
			}
		}
	}
	
	

	@Override
	public List<WordWAPDTO> listResults(int start, int limit, String sortName,
			String sortOrder, HttpServletRequest request,Boolean search) {
		//DTO
		List<WordWAPDTO> wordDTOList = new ArrayList<WordWAPDTO>();
		
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
			WordWAPExample wordWAPExample = new WordWAPExample();
			wordWAPExample.setOrderByClause( sortName +" "+ sortOrder);
			wordWAPExample.setDate(startDay + "");
			startHour = Utils.getHour(startTime);
			endHour = Utils.getHour(endTime);
			if(startHour==0&&endHour==23){
				startHour=null;
				endHour=null;
			}
			if(search) addCriteria(request, wordWAPExample, startHour, endHour);
			
			wordWAPExample.setDate(startDay+"");
			wordWAPExample.setStart(start);
			wordWAPExample.setLimit(limit);
			wordDTOList.addAll(selectByWAPExample(wordWAPExample,timelevel));
		}else if (startDay < endDay) {//不考虑小时
			if(totalRecordMap.isEmpty()){
				getTotalRecordMap(request, search);
			}
			WordWAPExample wordWAPExample = new WordWAPExample();
			wordWAPExample.setOrderByClause( sortName +" "+ sortOrder);
			if(search) addCriteria(request, wordWAPExample, startHour, endHour);
			int currentTotalRecord = 0;
			for (int currentDay = startDay; currentDay <= endDay; currentDay++) {
				Integer currentRecordsCount = totalRecordMap.get(currentDay);
				if (start + limit <= currentTotalRecord + currentRecordsCount) {
					wordWAPExample.setDate(currentDay + "");
					wordWAPExample.setStart(start - currentTotalRecord);
					wordWAPExample.setLimit(limit);
					wordDTOList.addAll(selectByWAPExample(wordWAPExample,timelevel));
					break;
				} else if (start + limit > currentTotalRecord + currentRecordsCount&&currentTotalRecord + currentRecordsCount - start>0) {
					wordWAPExample.setDate(currentDay + "");
					wordWAPExample.setStart(start - currentTotalRecord);
					wordWAPExample.setLimit(currentTotalRecord + currentRecordsCount - start);
					wordDTOList.addAll(selectByWAPExample(wordWAPExample,timelevel));
					limit = limit - (currentTotalRecord + currentRecordsCount - start);
					start=currentTotalRecord+currentRecordsCount;
				}
				currentTotalRecord += currentRecordsCount;
			}
			totalRecordMap.clear();
		}
		return wordDTOList;
	}
	
	private Collection<? extends WordWAPDTO> selectByWAPExample(
			WordWAPExample wordWAPExample,String timelevel) {
		Map<String, String> vrMap = MultivrWAPVRTypeUtils.getVRType();
		List<WordWAPDTO> wordDTOList = new ArrayList<WordWAPDTO>();
		List<WordWAP> wordWAPs = null;
		if(timelevel.equals("hour")){
			wordWAPs = wordWAPMapper.selectByExample(wordWAPExample);
		}else{
			wordWAPs = wordWAPMapper.selectDayByExample(wordWAPExample);
		}
		WordWAPDTO wordWAPDTO = null;
		for (WordWAP wordWAP : wordWAPs) {
			wordWAPDTO = new WordWAPDTO();
			Utils.copyProperties(wordWAPDTO, wordWAP);
			//VR类型转换
			wordWAPDTO.setVrid(wordWAP.getVrid());
			if(vrMap.containsKey(wordWAP.getVrid())){
				wordWAPDTO.setVrid(vrMap.get(wordWAP.getVrid()));
			}
			wordWAPDTO.setConsumption(wordWAPDTO.getEndclicknum()*100/wordWAPDTO.getPvnum()+"%");
			wordDTOList.add(wordWAPDTO);
		}
		return wordDTOList;
	}



	/**
	 * 添加查询条件
	 * @param request
	 * @param search
	 * @param wordWAPExample
	 * @author 李彤 2013-8-26 下午8:18:21
	 */
	private void addCriteria(HttpServletRequest request,WordWAPExample wordWAPExample,Integer startHour,Integer endHour) {
			String keyword =  request.getParameter("keyword");
			String[] vrid = request.getParameterValues("vrid[]");
			if(vrid==null||vrid.length==0) vrid = request.getParameterValues("vrid");
			String[] jhid = request.getParameterValues("jhid[]");
			if(jhid==null||jhid.length==0) jhid = request.getParameterValues("jhid");
			String vrposav = request.getParameter("vrposav");
			String pagetype = request.getParameter("pagetype");
			String linkid = request.getParameter("linkid");
			if(debug){
				log.debug("jhid"+Arrays.toString(jhid)+"vrid:"+Arrays.toString(vrid)+"vrposav:"+vrposav+"pagetype:"+pagetype+"linkid: "+linkid);
			}
			Criteria criteria = wordWAPExample.createCriteria();
			if(vrid!=null&&vrid.length>0&&!vrid[0].equalsIgnoreCase("null")) criteria.andVridIn(Arrays.asList(vrid));
			if(jhid!=null&&jhid.length>0&&!jhid[0].equalsIgnoreCase("null")) criteria.andJhidIn(Arrays.asList(jhid));
			if(!StringUtils.isEmpty(vrposav)) criteria.andVrposavEqualTo(Float.parseFloat(vrposav));
			if(!StringUtils.isEmpty(pagetype)) criteria.andPagetypeEqualTo(Byte.parseByte(pagetype));
			if(!StringUtils.isEmpty(linkid)) criteria.andLinkidEqualTo(Byte.parseByte(linkid));
			if(startHour!=null&&endHour!=null) criteria.andHourBetween(startHour, endHour);
			String wordsKey = request.getSession().getId()+"_WAPWORD";
			if(importWords.containsKey(wordsKey)){//如果有上传的词表
				List<String> wordsList = importWords.get(wordsKey);
				criteria.andKeywordIn(wordsList);
				if(debug){
					log.debug("key:"+wordsKey);
					log.debug("wordsList:"+wordsList);
				}
			}else{
				if(!StringUtils.isEmpty(keyword)) 
					criteria.andKeywordLike(keyword);
			}
	}
	
	
	@Override
	public Map<String,String> getTypeMap() {
		Map<String,String> map = MultivrWAPVRTypeUtils.getVRType();
		return map;
	}

	@Override
	public List<Integer> getPositionList() {
		return Arrays.asList(new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20});
	}

	@Override
	public List<Integer> getAbtestList() {
		return Arrays.asList(new Integer[]{0,1,2,3,4,5,6,7});
	}


	@Override
	public String validateExcel(InputStream is, String sessionId) {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String word = null;
		List<String> wordList = new ArrayList<String>();
		while(true){
			try {
				word = in.readLine();
				if(word==null){
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			wordList.add(word);
		}
		if(debug){
			log.debug("key:"+sessionId + "_WAPWORD");
			log.debug("wordList:"+wordList);
		}
		importWords.put(sessionId + "_WAPWORD" , wordList);
		return ServiceReturnResult.SERVICE_OP_SUCC;
	}
	
	public WordWAPMapper getWordWAPMapper() {
		return wordWAPMapper;
	}

	public void setWordWAPMapper(WordWAPMapper wordWAPMapper) {
		this.wordWAPMapper = wordWAPMapper;
	}


	@Override
	public Map<String, String> getJhidMap() {
		Map<String,String> map = new HashMap<String, String>();
		map.put("1001", "人物聚合");
		return map;
	}


}
