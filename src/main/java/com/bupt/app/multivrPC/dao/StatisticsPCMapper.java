package com.bupt.app.multivrPC.dao;

import java.util.List;

import com.bupt.app.multivrPC.model.StatisticsPC;
import com.bupt.app.multivrPC.model.StatisticsPCExample;

public interface StatisticsPCMapper {
    int countByExample(StatisticsPCExample example);

    List<StatisticsPC> selectByExample(StatisticsPCExample example);
}