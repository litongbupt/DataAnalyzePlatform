package com.bupt.app.multivrPC.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.bupt.app.multivrPC.dto.WordPCDTO;
import com.bupt.app.multivrPC.model.WordPC;
import com.bupt.core.base.service.BaseService;
/**
 * PC词表查询的业务逻辑接口
 * @author litong
 *
 */
public interface WordPCService extends BaseService<WordPC, WordPCDTO>{
	
	/**
	 * 根据唯一标示获取DTO
	 * @param title
	 * @return
	 * @author 李彤 2013-8-27 下午11:42:34
	 */
	public WordPCDTO selectByPrimaryKey(String title);
	
	/**
	 * 获取类型Map
	 * @return
	 * @author 李彤 2013-8-27 下午11:42:55
	 */
	public Map<String,String> getTypeMap();

	/**
	 * 获取类型列表
	 * @return
	 * @author 李彤 2013-8-27 下午11:43:05
	 */
	public List<Integer> getPositionList();

	/**
	 * 获取Abtest列表
	 * @return
	 * @author 李彤 2013-8-27 下午11:43:45
	 */
	public List<Integer> getAbtestList();

	/**
	 * 验证上传文件的正确性
	 * @param is 输入流
	 * @param sessionId session的ID
	 * @return
	 * @author 李彤 2013-8-27 下午11:44:11
	 */
	public String validateExcel(InputStream is, String sessionId);
	
	
}
