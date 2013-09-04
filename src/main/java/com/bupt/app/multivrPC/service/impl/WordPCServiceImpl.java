package com.bupt.app.multivrPC.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.bupt.app.multivrPC.dao.WordPCMapper;
import com.bupt.app.multivrPC.dto.WordPCDTO;
import com.bupt.app.multivrPC.model.WordPC;
import com.bupt.app.multivrPC.model.WordPCExample;
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


	@Override
	public int getTotalRecords(HttpServletRequest request, Boolean search) {
		WordPCExample wordPCExample = new WordPCExample();
		//设置默认选择哪张表
		wordPCExample.setDate(Utils.lastDate(new Date()));
		//添加查询条件
		if(search) addCriteria(request, wordPCExample);
		return wordPCMapper.countDayByExample(wordPCExample);
	}

	@Override
	public List<WordPCDTO> listResults(int start, int limit, String sortName,
			String sortOrder, HttpServletRequest request,Boolean search) {
		WordPCExample wordPCExample = new WordPCExample();
		wordPCExample.setOrderByClause( sortName +" "+ sortOrder);
		wordPCExample.setStart(start);
		wordPCExample.setLimit(limit);
		//设置默认选择哪张表
		wordPCExample.setDate(Utils.lastDate(new Date()));
		
		//添加查询条件
		if(search) {
			addCriteria(request,  wordPCExample);
			//当上传的查询词表生效一次后，清空词表信息
			String wordsKey = request.getSession().getId()+"_PCWORD";
			importWords.remove(wordsKey);
		}

		//获取结果列表
		List<WordPC> wordPCs = wordPCMapper.selectByExample(wordPCExample);
		//获取VR类型 为了展示方便这里先不转换VR类型了
		Map<String, String> vrMap = MultivrPCVRTypeUtils.getVRType();
		
		List<WordPCDTO> wordDTOList = new ArrayList<WordPCDTO>();
		WordPCDTO wordPCDTO = null;
		for (WordPC wordPC : wordPCs) {
			wordPCDTO = new WordPCDTO();
			Utils.copyProperties(wordPCDTO, wordPC);
			//VR类型转换
			wordPCDTO.setVrId(wordPC.getType());
			if(vrMap.containsKey(wordPC.getType())){
				wordPCDTO.setType(vrMap.get(wordPC.getType()));
			}
			wordDTOList.add(wordPCDTO);
		}
		return wordDTOList;
	}

	/**
	 * 添加查询条件
	 * @param request
	 * @param search
	 * @param wordPCExample
	 * @author 李彤 2013-8-26 下午8:18:21
	 */
	private void addCriteria(HttpServletRequest request,WordPCExample wordPCExample) {
			String keyword =  request.getParameter("keyword");
			String[] type = request.getParameterValues("type[]");
			if(type==null||type.length==0) type = request.getParameterValues("type");
			String position = request.getParameter("position");
			String abtest = request.getParameter("abtest");
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			if(debug){
				log.debug("keyword:"+keyword+"type:"+type+"position:"+position+"abtest:"+abtest+"startTime:"+startTime+"endTime:"+endTime);
			}
			wordPCExample.setDate(Utils.getDate(startTime));
			Criteria criteria = wordPCExample.createCriteria();
			if(type!=null&&type.length>0&&!type[0].equalsIgnoreCase("null")) criteria.andTypeIn(Arrays.asList(type));
			if(!StringUtils.isEmpty(position)) criteria.andPositionEqualTo(Integer.parseInt(position));
			if(!StringUtils.isEmpty(abtest)) criteria.andAbtestEqualTo(Integer.parseInt(abtest));
			if(!StringUtils.isEmpty(startTime)&&!StringUtils.isEmpty(endTime)) criteria.andHourBetween(Utils.getHour(startTime), Utils.getHour(endTime));
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
		return Arrays.asList(new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20});
	}

	@Override
	public List<Integer> getAbtestList() {
		return Arrays.asList(new Integer[]{0,1,2,3,4,5});
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

}
