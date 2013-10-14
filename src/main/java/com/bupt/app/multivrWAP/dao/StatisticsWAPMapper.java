package com.bupt.app.multivrWAP.dao;

import java.util.List;

import com.bupt.app.multivrWAP.model.StatisticsWAP;
import com.bupt.app.multivrWAP.model.StatisticsWAPExample;

public interface StatisticsWAPMapper {


    int countByExample(StatisticsWAPExample example);

    List<StatisticsWAP> selectByExample(StatisticsWAPExample example);
    
    int countDayByExample(StatisticsWAPExample example);

    List<StatisticsWAP> selectDayByExample(StatisticsWAPExample example);
}