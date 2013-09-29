package com.bupt.app.multivrWAP.dto;

public class WordWAPDTO {

    private Integer id;

    private String keyword;

    private Byte pagetype;

    private String vrid;
    
    private String type;

	private Byte linkid;

    private Float vrposav;

    private Integer pvnum;

    private Integer clicknum;

    private Integer endclicknum;

    private Integer hour;

    private String jhid;

    private String date;
    
    private String consumption;

	private String ctr;

    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
    
    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public String getKeyword() {
        return keyword;
    }


    public void setKeyword(String keyword) {
        this.keyword = keyword == null ? null : keyword.trim();
    }


    public Byte getPagetype() {
        return pagetype;
    }


    public void setPagetype(Byte pagetype) {
        this.pagetype = pagetype;
    }

 
    public String getVrid() {
        return vrid;
    }


    public void setVrid(String vrid) {
        this.vrid = vrid == null ? null : vrid.trim();
    }


    public Byte getLinkid() {
        return linkid;
    }


    public void setLinkid(Byte linkid) {
        this.linkid = linkid;
    }


    public Float getVrposav() {
        return vrposav;
    }


    public void setVrposav(Float vrposav) {
        this.vrposav = vrposav;
    }


    public Integer getPvnum() {
        return pvnum;
    }

 
    public void setPvnum(Integer pvnum) {
        this.pvnum = pvnum;
    }


    public Integer getClicknum() {
        return clicknum;
    }


    public void setClicknum(Integer clicknum) {
        this.clicknum = clicknum;
    }


    public Integer getEndclicknum() {
        return endclicknum;
    }

    public void setEndclicknum(Integer endclicknum) {
        this.endclicknum = endclicknum;
    }


    public Integer getHour() {
        return hour;
    }


    public void setHour(Integer hour) {
        this.hour = hour;
    }


    public String getJhid() {
        return jhid;
    }


    public void setJhid(String jhid) {
        this.jhid = jhid;
    }


    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date = date == null ? null : date.trim();
    }


	public void setConsumption(String consumption) {
		this.consumption = consumption;
	}
	
	public String getConsumption(){
		return this.consumption;
	}
	

	public String getCtr() {
		return ctr;
	}

	public void setCtr(String ctr) {
		this.ctr = ctr;
	}

}
