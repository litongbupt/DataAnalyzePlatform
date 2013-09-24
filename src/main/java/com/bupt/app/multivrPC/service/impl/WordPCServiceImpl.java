package com.bupt.app.multivrPC.service.impl;

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

import com.bupt.app.multivrPC.dao.WordPCMapper;
import com.bupt.app.multivrPC.dto.WordPCDTO;
import com.bupt.app.multivrPC.model.WordPCExample;
import com.bupt.app.multivrPC.model.WordPC;
import com.bupt.app.multivrPC.model.WordPCExample.Criteria;
import com.bupt.app.multivrPC.service.WordPCService;
import com.bupt.app.multivrPC.utils.MultivrPCVRTypeUtils;
import com.bupt.core.base.util.ServiceReturnResult;
import com.bupt.core.base.util.Utils;
/**
 * PC多VR词表查询的业务逻辑实现
 * @author litong
 *
 */
@Service("wordPCService")
public class WordPCServiceImpl implements WordPCService {
	
	private final Log log = LogFactory.getLog(getClass());
	private boolean debug = log.isDebugEnabled();
	private static Map<String, List<String>> importWords = new HashMap<String,List<String>>();
	
	@Resource(name="wordPCMapper")
	private WordPCMapper wordPCMapper;
	
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
	 * @param wordPCExample
	 * @author 李彤 2013-9-17 上午10:54:44
	 */
	private void getTotalRecordMap(HttpServletRequest request, Boolean search) {
		WordPCExample wordPCExample = new WordPCExample();
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
			wordPCExample.setDate(startDay + "");
			startHour = Utils.getHour(startTime);
			endHour = Utils.getHour(endTime);
			if(startHour==0&&endHour==23){
				startHour=null;
				endHour=null;
			}
			if (search) {
				addCriteria(request, wordPCExample, startHour, endHour);
			}
			if(timelevel.equals("hour")){
				totalRecordMap.put(startDay, wordPCMapper.countByExample(wordPCExample));
			}else{
				totalRecordMap.put(startDay, wordPCMapper.countDayByExample(wordPCExample));
			}
			
		}else if (startDay < endDay) {
			if (search) {// 添加查询条件
				addCriteria(request, wordPCExample, startHour, endHour);
			}
			for (int currentDay = startDay; currentDay <= endDay; ++currentDay) {
				wordPCExample.setDate(currentDay + "");
				if(timelevel.equals("hour")){
					totalRecordMap.put(startDay, wordPCMapper.countByExample(wordPCExample));
				}else{
					totalRecordMap.put(startDay, wordPCMapper.countDayByExample(wordPCExample));
				}
			}
		}
	}
	
	

	@Override
	public List<WordPCDTO> listResults(int start, int limit, String sortName,
			String sortOrder, HttpServletRequest request,Boolean search) {
		//DTO
		List<WordPCDTO> wordDTOList = new ArrayList<WordPCDTO>();
		
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
			WordPCExample wordPCExample = new WordPCExample();
			wordPCExample.setOrderByClause( sortName +" "+ sortOrder);
			wordPCExample.setDate(startDay + "");
			startHour = Utils.getHour(startTime);
			endHour = Utils.getHour(endTime);
			if(startHour==0&&endHour==23){
				startHour=null;
				endHour=null;
			}
			if(search) addCriteria(request, wordPCExample, startHour, endHour);
			
			wordPCExample.setDate(startDay+"");
			wordPCExample.setStart(start);
			wordPCExample.setLimit(limit);
			wordDTOList.addAll(selectByPCExample(wordPCExample,timelevel));
		}else if (startDay < endDay) {//不考虑小时
			if(totalRecordMap.isEmpty()){
				getTotalRecordMap(request, search);
			}
			WordPCExample wordPCExample = new WordPCExample();
			wordPCExample.setOrderByClause( sortName +" "+ sortOrder);
			if(search) addCriteria(request, wordPCExample, startHour, endHour);
			int currentTotalRecord = 0;
			for (int currentDay = startDay; currentDay <= endDay; currentDay++) {
				Integer currentRecordsCount = totalRecordMap.get(currentDay);
				if (start + limit <= currentTotalRecord + currentRecordsCount) {
					wordPCExample.setDate(currentDay + "");
					wordPCExample.setStart(start - currentTotalRecord);
					wordPCExample.setLimit(limit);
					wordDTOList.addAll(selectByPCExample(wordPCExample,timelevel));
					break;
				} else if (start + limit > currentTotalRecord + currentRecordsCount&&currentTotalRecord + currentRecordsCount - start>0) {
					wordPCExample.setDate(currentDay + "");
					wordPCExample.setStart(start - currentTotalRecord);
					wordPCExample.setLimit(currentTotalRecord + currentRecordsCount - start);
					wordDTOList.addAll(selectByPCExample(wordPCExample,timelevel));
					limit = limit - (currentTotalRecord + currentRecordsCount - start);
					start=currentTotalRecord+currentRecordsCount;
				}
				currentTotalRecord += currentRecordsCount;
			}
			totalRecordMap.clear();
		}
		return wordDTOList;
	}
	
	private Collection<? extends WordPCDTO> selectByPCExample(
			WordPCExample wordPCExample,String timelevel) {
		Map<String, String> vrMap = MultivrPCVRTypeUtils.getVRType();
		List<WordPCDTO> wordDTOList = new ArrayList<WordPCDTO>();
		List<WordPC> wordPCs = null;
		if(timelevel.equals("hour")){
			wordPCs = wordPCMapper.selectByExample(wordPCExample);
		}else{
			wordPCs = wordPCMapper.selectDayByExample(wordPCExample);
		}
		WordPCDTO wordPCDTO = null;
		for (WordPC wordPC : wordPCs) {
			wordPCDTO = new WordPCDTO();
			Utils.copyProperties(wordPCDTO, wordPC);
			//VR类型转换
			wordPCDTO.setVrId(wordPC.getType());
			if(vrMap.containsKey(wordPC.getType())){
				wordPCDTO.setType(vrMap.get(wordPC.getType()));
			}
			wordPCDTO.setConsumption(wordPCDTO.getEclpv()*100/wordPCDTO.getPv()+"%");
			wordDTOList.add(wordPCDTO);
		}
		return wordDTOList;
	}


	@Override
	public WordPCDTO selectByPrimaryKey(String title) {
		WordPCExample wordPCExample = new WordPCExample();
		wordPCExample.createCriteria().andTitleEqualTo(title);
		List<WordPC> resultList = wordPCMapper.selectDayByExample(wordPCExample);
		
		if(debug){
			log.debug("service.selectByPrimaryKey().resultList:"+resultList);
		}
		
		WordPCDTO wordPCDTO = new WordPCDTO();
		if(resultList!=null&&resultList.size()==1){
			WordPC wordPC = resultList.get(0);
			if(debug){
				log.debug("service.selectByPrimaryKey() wordPC: "+wordPC);
			}
			try {
				BeanUtils.copyProperties(wordPCDTO, wordPC);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			if(debug){
				log.debug("service.selectByPrimaryKey()  wordPCDTO: "+wordPCDTO);
			}
		}
		return wordPCDTO;
	}


	/**
	 * 添加查询条件
	 * @param request
	 * @param search
	 * @param wordPCExample
	 * @author 李彤 2013-8-26 下午8:18:21
	 */
	private void addCriteria(HttpServletRequest request,WordPCExample wordPCExample,Integer startHour,Integer endHour) {
			String keyword =  request.getParameter("keyword");
			String[] type = request.getParameterValues("type[]");
			if(type==null||type.length==0) type = request.getParameterValues("type");
			String[] jhid = request.getParameterValues("jhid[]");
			if(jhid==null||jhid.length==0) jhid = request.getParameterValues("jhid");
			String position = request.getParameter("position");
			String abtest = request.getParameter("abtest");
			String clickid = request.getParameter("clickid");
			if(debug){
				log.debug("jhid"+Arrays.toString(jhid)+"type:"+Arrays.toString(type)+"position:"+position+"abtest:"+abtest+"clickid: "+clickid);
			}
			Criteria criteria = wordPCExample.createCriteria();
			if(type!=null&&type.length>0&&!type[0].equalsIgnoreCase("null")) criteria.andTypeIn(Arrays.asList(type));
			if(jhid!=null&&jhid.length>0&&!jhid[0].equalsIgnoreCase("null")) criteria.andJhidIn(Arrays.asList(jhid));
			if(!StringUtils.isEmpty(position)) criteria.andPositionEqualTo(Integer.parseInt(position));
			if(!StringUtils.isEmpty(abtest)) criteria.andAbtestEqualTo(Integer.parseInt(abtest));
			if(!StringUtils.isEmpty(clickid)) criteria.andClickidEqualTo(clickid);
			if(startHour!=null&&endHour!=null) criteria.andHourBetween(startHour, endHour);
			String wordsKey = request.getSession().getId()+"_PCWORD";
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
		Map<String,String> map = MultivrPCVRTypeUtils.getVRType();
		return map;
	}

	@Override
	public List<Integer> getPositionList() {
		return Arrays.asList(new Integer[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20});
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
			log.debug("key:"+sessionId + "_PCWORD");
			log.debug("wordList:"+wordList);
		}
		importWords.put(sessionId + "_PCWORD" , wordList);
		return ServiceReturnResult.SERVICE_OP_SUCC;
	}
	
	public WordPCMapper getWordPCMapper() {
		return wordPCMapper;
	}

	public void setWordPCMapper(WordPCMapper wordPCMapper) {
		this.wordPCMapper = wordPCMapper;
	}


	@Override
	public Map<String, String> getJhidMap() {
		Map<String,String> map = new HashMap<String, String>();
		map.put("1001", "人物聚合");
		return map;
	}

}
