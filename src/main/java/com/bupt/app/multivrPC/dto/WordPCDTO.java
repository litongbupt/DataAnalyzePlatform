package com.bupt.app.multivrPC.dto;
/**
 * PC多VR词表查询的DTO
 * @author litong
 *
 */
public class WordPCDTO {

	// 标题，即唯一标示
	private String title;

	// abtest
	private Integer abtest;

	// 最终点击量
	private Integer eclpv;

	// 点击量
	private Integer click;

	// 位置
	private Integer position;

	// clickid
	private String clickid;

	// 关键字
	private String keyword;

	// 时间
	private Integer hour;

	// VR类型
	private String type;

	// 展现量
	private Integer pv;
	
	//ctr
	private String ctr;

	// VRID
	private String vrId;
	
	//消费率
	private String consumption;
	
	//日期
	private String day;
	
	//聚合id
	private String jhid;

	public String getTitle() {
		return title;
	}

	public Integer getAbtest() {
		return abtest;
	}

	public Integer getEclpv() {
		return eclpv;
	}

	public Integer getClick() {
		return click;
	}

	public Integer getPosition() {
		return position;
	}

	public String getClickid() {
		return clickid;
	}

	public String getKeyword() {
		return keyword;
	}

	public Integer getHour() {
		return hour;
	}

	public String getType() {
		return type;
	}

	public Integer getPv() {
		return pv;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setAbtest(Integer abtest) {
		this.abtest = abtest;
	}

	public void setEclpv(Integer eclpv) {
		this.eclpv = eclpv;
	}

	public void setClick(Integer click) {
		this.click = click;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public void setClickid(String clickid) {
		this.clickid = clickid;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public void setHour(Integer hour) {
		this.hour = hour;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setPv(Integer pv) {
		this.pv = pv;
	}

	public String getVrId() {
		return vrId;
	}

	public void setVrId(String vrId) {
		this.vrId = vrId;
	}

	public String getConsumption() {
		return consumption;
	}

	public void setConsumption(String consumption) {
		this.consumption = consumption;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getJhid() {
		return jhid;
	}

	public void setJhid(String jhid) {
		this.jhid = jhid;
	}

	public String getCtr() {
		return ctr;
	}

	public void setCtr(String ctr) {
		this.ctr = ctr;
	}

	

}
